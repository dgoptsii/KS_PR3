package ua.goptsii.connection;

import ua.goptsii.network.TCPNetwork;

import java.io.IOException;

public class Server {
    public static void main(String[] args) throws InterruptedException, IOException {
        try{
            TCPNetwork network= null;
            String networkType = "tcp";

            if(networkType.equals("tcp")){
                network = new TCPNetwork();
            }else{
//                ...
            }
            System.out.println("Server running via"+network);
            network.listen();
            network.receive();
            network.close();

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
