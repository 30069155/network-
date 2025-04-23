import java.io.*;
import java.net.*;

public class Switch {
    String name;
    boolean isRoot = false;
    String BridgeID;

    ObjectOutputStream toUser = null;
    ObjectInputStream userIn = null;

    ObjectOutputStream toPeer = null;
    ObjectInputStream peerIn = null;

    public void startSwitch(String switchName, int userPort, String peerHost, int peerConnectPort, int peerListenPort, String bridgeID) throws Exception {
        name = switchName;
        BridgeID = bridgeID;

        //starting and connect to other switch
        Socket peerSocket = null;
        try {
            peerSocket = new Socket(peerHost, peerConnectPort);
            System.out.println("[" + name + "] Connected to peer via connect.");
        } catch (IOException e) {
            System.out.println("[" + name + "] Peer not ready, listening for peer...");
            ServerSocket peerListener = new ServerSocket(peerListenPort);
            peerSocket = peerListener.accept();
            System.out.println("[" + name + "] Accepted peer connection.");
        }

        //transfer the bridge id
        toPeer = new ObjectOutputStream(peerSocket.getOutputStream());
        peerIn = new ObjectInputStream(peerSocket.getInputStream());

        toPeer.writeObject(new Message("BPDU", bridgeID, name));
        toPeer.flush();
        System.out.println("[" + name + "] Sent BPDU with bridgeId: " + bridgeID);

        Message received = (Message) peerIn.readObject();
        String peerBridgeId = received.content;
        System.out.println("[" + name + "] Received BPDU from peer. Their bridgeId: " + peerBridgeId);

        isRoot = bridgeID.compareTo(peerBridgeId) < 0;

        System.out.println("[" + name + "] STP result: I'm " + (isRoot ? "ROOT" : "BLOCKING"));


        //read to receive
        new Thread(() -> {
            try {
                while (true) {
                    Message message = (Message) peerIn.readObject();
                    System.out.println("[" + name + "] Received from peer: " + message.content);

                    if (toUser != null) {
                        toUser.writeObject(message);
                        toUser.flush();
                        System.out.println("[" + name + "] Forwarded to local user.");
                    } else {
                        System.out.println("[" + name + "] No user connected → drop.");
                    }
                }
            } catch (Exception e) {
                System.out.println("[" + name + "] Peer disconnected or error.");
            }
        }).start();

        // send the messages
        new Thread(() -> {
            try (ServerSocket userServer = new ServerSocket(userPort)) {
                System.out.println("[" + name + "] Waiting for user connection...");
                Socket userSocket = userServer.accept();
                toUser = new ObjectOutputStream(userSocket.getOutputStream());
                userIn = new ObjectInputStream(userSocket.getInputStream());
                System.out.println("[" + name + "] User connected.");

                while (true) {
                    Message message = (Message) userIn.readObject();
                    System.out.println("[" + name + "] Received from local user: " + message.content);

                    if (!isRoot) {
                        System.out.println("[" + name + "] Not Root → STP blocking, drop message.");
                        continue;
                    }

                    if (toPeer == null) {
                        System.out.println("[" + name + "] Peer not ready → drop.");
                        continue;
                    }

                    toPeer.writeObject(message);
                    toPeer.flush();
                    System.out.println("[" + name + "] Forwarded to peer switch.");
                }
            } catch (Exception e) {
                System.out.println("[" + name + "] User disconnected or error.");
            }
        }).start();
    }
}