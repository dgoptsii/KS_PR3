package ua.goptsii.process;

import ua.goptsii.packet.Packet;

import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;

public class Sender implements Runnable  {
    private InetAddress ip;
    private BlockingQueue<byte[]> input;
    private boolean run = true;

    private static int id=0;
    private  int myId;

    public Sender(BlockingQueue<byte[]> input, InetAddress ip) {
        this.ip = ip;
        this.input = input;

        id++;
        myId =id;
    }

    @Override
    public void run() {
        try {
            while (run) {
                sendMessage();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() throws Exception {
        byte[] inputPacket = input.take();

        if(inputPacket.length==0){ //ate poisonous pill
            System.err.println("RIP - swallowed poisonous pill, finished work correctly: " + this.getClass().getName());
            run = false;
        }else {
            Packet packet = new Packet(inputPacket);

            System.out.println("Sender #"+ this.myId +" sent a packet! Packet id:"+packet.getBPktId()+" "+packet.getBMsq() );
        }
    }

}