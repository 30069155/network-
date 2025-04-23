import java.io.*;
import java.net.Socket;
import java.util.Scanner;


public class UserSession {
    public static void start(String userName, String switchHost,int switchPort){
        //connect to the switch
        try{
            Socket socket = new Socket (switchHost,switchPort);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            //prepare to receive messages
            new Thread(()->{
                try{
                    while(true){
                        Message message = (Message) in.readObject();
                        System.out.println("\nReceived Message from "+ message.from+ ": "+ message.content);
                    }
                }catch(Exception e){
                    System.out.println("Received error.");
                }
            }).start();

            //ready to send messages
            Scanner scanner = new Scanner(System.in);
            while(true){
                System.out.print("Enter your content (exit to quit): ");
                String content = scanner.nextLine();
                if(content.equals("exit")) break;
                out.writeObject(new Message("DATA",content,userName));
                out.flush();
            }

            socket.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
