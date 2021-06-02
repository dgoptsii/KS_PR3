package ua.goptsii.process;

import lombok.Data;
import ua.goptsii.packet.Message;
import ua.goptsii.packet.Packet;

import java.util.concurrent.BlockingQueue;

@Data
public class Decryptor implements Runnable {

    protected BlockingQueue<Message> output;
    protected BlockingQueue<byte[]> input;
    private boolean run = true;

    private static int id=0;
    private  int myId;

    public Decryptor(BlockingQueue<byte[]> input, BlockingQueue<Message> output) {
        this.input = input;
        this.output = output;
        id++;
        myId=id;
    }

    @Override
    public void run() {
        try {
            while (run){
                decryptMessage();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void decryptMessage() throws Exception {
        byte[] inputPacket = input.take();

        if (inputPacket.length == 0) { //ate poisonous pill
            Message stopMessage = new Message(0, 0, "");
            System.err.println("RIP - swallowed poisonous pill, finished work correctly: " + this.getClass().getName());
            run = false;

            output.put(stopMessage); //put it back for processor
        }else {
            Packet packet = new Packet(inputPacket);
            Message message = packet.getBMsq();

            message.decode();
            System.out.println("Decryptor #"+ this.myId +" decrypted a message! "+message);
            //add decoded messages to output
            output.put(message);
        }
    }

}
