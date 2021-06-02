package ua.goptsii.process;

import lombok.Data;
import ua.goptsii.packet.Message;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Data
public class ServerSimulator {

    private final int MESSAGES_NUMBER;
    private final int THREADS_NUMBER;
    private final int QUEUE_BOUND;

    private BlockingQueue<byte[]> messagesInput;
    private BlockingQueue<byte[]> recieverOutput;
    private BlockingQueue<Message> decryptorOutput;
    private BlockingQueue<Message> processorOutput;
    private BlockingQueue<byte[]> encryptorOutput;

    public ServerSimulator(int messageNumber,int threadNumber, int queueBound) {
        this.MESSAGES_NUMBER = messageNumber;
        this.THREADS_NUMBER = threadNumber;
        this.QUEUE_BOUND = queueBound;
        //generate message
        messagesInput =  Generator.generateInput(QUEUE_BOUND,THREADS_NUMBER,MESSAGES_NUMBER);

        recieverOutput = new LinkedBlockingQueue<>(QUEUE_BOUND);
        decryptorOutput = new LinkedBlockingQueue<>(QUEUE_BOUND);
        processorOutput = new LinkedBlockingQueue<>(QUEUE_BOUND);
        encryptorOutput = new LinkedBlockingQueue<>(QUEUE_BOUND);
    }

    public void simulate() {
        //receive messages
        receive();
        //decrypt messages
        decrypt();
        //process messages and form answer
        process();
        //encrypt messages
        encrypt();
        //send messages
        send();
    }

    public void receive() {
        for (int i = 0; i < THREADS_NUMBER; i++) {
            new Thread(new Receiver(messagesInput, recieverOutput)).start();
        }
    }

    public void decrypt() {
        for (int i = 0; i < THREADS_NUMBER; i++) {
            new Thread(new Decryptor(recieverOutput, decryptorOutput)).start();
        }
    }

    public void process() {
        for (int i = 0; i < THREADS_NUMBER; i++) {
            new Thread(new Processor(decryptorOutput, processorOutput)).start();
        }
    }

    public void encrypt() {
        for (int i = 0; i < THREADS_NUMBER; i++) {
            new Thread(new Encryptor(processorOutput, encryptorOutput)).start();
        }
    }

    public void send() {        //send messages
        for (int i = 0; i < THREADS_NUMBER; i++) {
            new Thread(new Sender(encryptorOutput, null)).start();
        }
    }

}
