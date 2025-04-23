public class Simulation {
    public static void main(String[] args) {
        //start switch a
        new Thread(()->{
            try{
                Switch switchA = new Switch();
                switchA.startSwitch( "SwitchA", 5100, "localhost", 8100, 7100,"0001.SwitchA");
                System.out.println("Switch A stared.");
            }catch(Exception e){
                System.out.println("Failed to start Switch A:");
                e.printStackTrace();
            }
        }).start();

        //waiting for switch a starting finish
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        //start switch b
        new Thread(()->{
            try{
                Switch switchB = new Switch();
                switchB.startSwitch("SwitchB", 6100, "localhost", 7100, 8100,"0002.SwitchB");
                System.out.println("Switch B started.");
            }catch(Exception e){
                System.out.println("Failed to start Switch B:");
                e.printStackTrace();
            }
        }).start();
    }
}
