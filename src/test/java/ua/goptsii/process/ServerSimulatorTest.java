package ua.goptsii.process;

import com.google.common.primitives.UnsignedLong;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import ua.goptsii.packet.CommandTypeEncoder;
import ua.goptsii.packet.Message;
import ua.goptsii.packet.MyCipher;
import ua.goptsii.packet.Packet;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerSimulatorTest extends TestCase {

    private final int MESSAGES_NUMBER = 150;
    private final int THREADS_NUMBER = 4;
    private int QUEUE_BOUND = 200;
    private final String MESSAGE = "Hello";
    private final String ANSWER = "OK";
    private final byte[] ENCRYPTED_ANSWER = MyCipher.encode(ANSWER.getBytes());

    @Test
    public void testReceiver() {
        ServerSimulator simulator = new ServerSimulator(MESSAGES_NUMBER, THREADS_NUMBER, QUEUE_BOUND);
        BlockingQueue<byte[]> input = simulator.getMessagesInput();

        ///////////////////
        Thread[] threads = new Thread[simulator.getTHREADS_NUMBER()];
        int inputSize = input.size();

        for (int i = 0; i < simulator.getTHREADS_NUMBER(); i++) {
            threads[i] = new Thread(new Receiver(input, simulator.getRecieverOutput()));
            threads[i].start();
        }

        for (int i = 0; i < simulator.getTHREADS_NUMBER(); i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //check that messages were not lost
        Assert.assertEquals(inputSize, simulator.getRecieverOutput().size());
    }

    @Test
    public void testDecryptor() {

        ServerSimulator simulator = new ServerSimulator(MESSAGES_NUMBER, THREADS_NUMBER, QUEUE_BOUND);

        //Generate decryptor input
        BlockingQueue<byte[]> input = new LinkedBlockingQueue<>(QUEUE_BOUND);
        for (int i = 0; i < MESSAGES_NUMBER; i++) {
            Message m = new Message(CommandTypeEncoder.randomCommand(), 1, MESSAGE);
            Packet p = new Packet((byte) 1, UnsignedLong.valueOf(i), m);
            input.add(p.toPacket());

        }

        //put as many poisonous pills as threads working
        for (int j = 0; j < THREADS_NUMBER; j++) {
            input.add(new byte[0]);
        }
        int inputSize = input.size();
        /////////////////////////////////////////////////////////
        Thread[] threads = new Thread[simulator.getTHREADS_NUMBER()];
        for (int i = 0; i < simulator.getTHREADS_NUMBER(); i++) {
            threads[i] = new Thread(new Decryptor(input, simulator.getDecryptorOutput()));
            threads[i].start();
        }
        for (int i = 0; i < simulator.getTHREADS_NUMBER(); i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //check that messages were not lost
        Assert.assertEquals(inputSize, simulator.getDecryptorOutput().size());
        //check that decryption worked correctly
        try {
            for (int i = 0; i < simulator.getDecryptorOutput().size(); i++) {
                Message m = simulator.getDecryptorOutput().take();
                Assert.assertEquals(m.getMessageText(), MESSAGE);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testProcessor(){
        ServerSimulator simulator = new ServerSimulator(MESSAGES_NUMBER,THREADS_NUMBER,QUEUE_BOUND);
        //Generate decryptor input
        BlockingQueue<Message> input = new LinkedBlockingQueue<>(QUEUE_BOUND);
        for (int i = 0; i < MESSAGES_NUMBER; i++) {
            Message m = new Message(CommandTypeEncoder.randomCommand(),1, MESSAGE);
            input.add(m);
            //put as many poisonous pills as threads working
        }
        for (int j = 0; j < THREADS_NUMBER; j++) {
            Message m =new Message(0, 0, "");
            input.add(m);
        }
        int inputSize = input.size();
        /////////////////////////////////////////////////////////
        Thread[] threads = new Thread[simulator.getTHREADS_NUMBER()];
        for (int i = 0; i < simulator.getTHREADS_NUMBER(); i++) {
            threads[i]= new Thread(new Processor( input, simulator.getProcessorOutput()));
            threads[i].start();
        }
        for (int i = 0; i < simulator.getTHREADS_NUMBER(); i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //check that messages were not lost
        Assert.assertEquals(inputSize,simulator.getProcessorOutput().size());
        //check that processor answered correctly
        try {
            for (int i=0;i<simulator.getProcessorOutput().size();i++) {
                Message m = simulator.getProcessorOutput().take();
                String command = null;
                //check command type
                try {
                    CommandTypeEncoder typeEncoder = new CommandTypeEncoder(m.getCType());
                    command = typeEncoder.getCommandType();
                }catch (Exception e) {
                    e.printStackTrace();
                }
                Assert.assertEquals(m.getMessageText(), ANSWER+" - "+command);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }




    @Test
    public void testEncryptor(){
        ServerSimulator simulator = new ServerSimulator(MESSAGES_NUMBER,THREADS_NUMBER,QUEUE_BOUND);
        //Generate decryptor input
        BlockingQueue<Message> input = new LinkedBlockingQueue<>(QUEUE_BOUND);
        for (int i = 0; i < MESSAGES_NUMBER; i++) {
            Message m = new Message(CommandTypeEncoder.randomCommand(),1, ANSWER);
            input.add(m);
            //put as many poisonous pills as threads working
        }
        for (int j = 0; j < THREADS_NUMBER; j++) {
            Message m =new Message(0, 0, "");
            input.add(m);
        }
        int inputSize = input.size();
        /////////////////////////////////////////////////////////
        Thread[] threads = new Thread[simulator.getTHREADS_NUMBER()];
        for (int i = 0; i < simulator.getTHREADS_NUMBER(); i++) {
            threads[i]= new Thread(new Encryptor( input, simulator.getEncryptorOutput()));
            threads[i].start();
        }
        for (int i = 0; i < simulator.getTHREADS_NUMBER(); i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //check that messages were not lost
        Assert.assertEquals(inputSize,simulator.getEncryptorOutput().size());
        //check that encryption worked correctly

        try {
            for (int i=0;i<simulator.getEncryptorOutput().size();i++) {
                Packet packet = new Packet(simulator.getEncryptorOutput().take());
                Message m = packet.getBMsq();
                Assert.assertEquals(m.getMessageText(), new String(ENCRYPTED_ANSWER));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Test
    public void testSender(){
        ServerSimulator simulator = new ServerSimulator(MESSAGES_NUMBER,THREADS_NUMBER,QUEUE_BOUND);
        //Generate decryptor input
        BlockingQueue<byte[]> input = new LinkedBlockingQueue<>(QUEUE_BOUND);
        for (int i = 0; i < MESSAGES_NUMBER; i++) {
            Message m = new Message(CommandTypeEncoder.randomCommand(),1, ENCRYPTED_ANSWER);
            Packet p = new Packet((byte) 1, UnsignedLong.valueOf(i), m);
            input.add(p.toPacket());
            //put as many poisonous pills as threads working
        }

        for (int j = 0; j < THREADS_NUMBER; j++) {
            input.add(new byte[0]);
        }

        /////////////////////////////////////////////////////////
        Thread[] threads = new Thread[simulator.getTHREADS_NUMBER()];
        for (int i = 0; i < simulator.getTHREADS_NUMBER(); i++) {
            threads[i]= new Thread(new Sender( input,null));
            threads[i].start();
        }
        for (int i = 0; i < simulator.getTHREADS_NUMBER(); i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //check that messages were not lost and were sent
        Assert.assertEquals(0,input.size());
    }

}