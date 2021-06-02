package ua.goptsii.process;
import lombok.Data;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;

@Data
public class Receiver implements Runnable, ReceiveInterface {
    private BlockingQueue<byte[]> input;
    private BlockingQueue<byte[]> output;

    private boolean run = true;
    private static int id=0;
    private  int myId;

    public Receiver(BlockingQueue<byte[]> input, BlockingQueue<byte[]> output) {
        this.output = output;
        this.input = input;
        id++;
        myId =id;
    }

    @Override
    public void run() {
        try {
            while (run){
                receivePacket();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void receivePacket() throws Exception {
        byte[] inputPacket = input.take();

        if(inputPacket.length ==0){
            System.err.println("RIP - swallowed poisonous pill, finished work correctly: " + this.getClass().getName());
            run = false;
        }else {
            System.out.println("Receiver #"+ this.myId + " received a packet! Packet : "+ Arrays.toString(inputPacket));
        }

        //put packet or poisonous pill for decryptor
        output.put(inputPacket);

    }

}