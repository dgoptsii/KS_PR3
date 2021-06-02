package ua.goptsii.process;

import com.google.common.primitives.UnsignedLong;
import lombok.Data;
import ua.goptsii.packet.Message;
import ua.goptsii.packet.Packet;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;

@Data
public class Encryptor implements Runnable {
    protected BlockingQueue<byte[]> output;
    protected BlockingQueue<Message> input;

    static private long id= 0;
    private  final BigInteger TWO_64 = BigInteger.ONE.shiftLeft(64);
    private boolean run = true;

    private static int encryptorId =0;
    private  int myId;

    public Encryptor(BlockingQueue<Message> input, BlockingQueue<byte[]> output) {
        this.input = input;
        this.output = output;

        encryptorId++;
        myId = encryptorId;
    }
    @Override
    public void run() {
        try {
            while (run) {
                encryptMessage();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void encryptMessage() throws InterruptedException {
        Message message = input.take();
        if(message.getMessage().length == 0){ //ate poisonous pill
            System.err.println("RIP - swallowed poisonous pill, finished work correctly: " + this.getClass().getName());
            run = false;
            output.put(new byte[0]);
        }else {

            encryptorId++;
            //add answers to output
            Packet packet = new Packet((byte) 2, UnsignedLong.valueOf(asUnsignedDecimalString(encryptorId)), message);
            output.put(packet.toPacket()); //put it back for sender
            System.out.println("Encryptor #"+ this.myId +" encrypted a message! Message: "+packet.getBMsq().getMessageText());
        }
    }

    public  String asUnsignedDecimalString(long l) {
        BigInteger b = BigInteger.valueOf(l);
        if(b.signum() < 0) {
            b = b.add(TWO_64);
        }
        return b.toString();
    }
}