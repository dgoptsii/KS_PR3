package ua.goptsii.process;

import lombok.Data;
import ua.goptsii.packet.CommandTypeEncoder;
import ua.goptsii.packet.Message;

import java.util.concurrent.BlockingQueue;

@Data
public class Processor implements Runnable {

    private static final String ANSWER_OK = "OK";
    private static final String ANSWER_BAD = "BAD COMMAND";
    private static final Integer ID = 2;

    protected BlockingQueue<Message> output;
    protected BlockingQueue<Message> input;
    private boolean run = true;

    private static int id=0;
    private  int myId;

    public Processor(BlockingQueue<Message> input, BlockingQueue<Message> output) {
        this.input = input;
        this.output = output;
        id++;
        myId=id;
    }
    @Override
    public void run() {
        try {
            while (run) {
                processMessage();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void processMessage() throws InterruptedException {
        Message message = input.take();
        if(message.getMessage().length == 0){ //ate poisonous pill
            System.err.println("RIP - swallowed poisonous pill, finished work correctly: " + this.getClass().getName());
            run = false;
            output.put(message); //put it back for encryptor
        }else {
            String command;
            Message response;

            //check command type
            try {
                CommandTypeEncoder typeEncoder = new CommandTypeEncoder(message.getCType());
                command =typeEncoder.getCommandType();
            }catch (Exception e) {
                command = ANSWER_BAD;
            }

            //send a response
            if (command.equals(ANSWER_BAD)) {
                response =  new Message(message.getCType(), ID, ANSWER_BAD);
            }else{
                response = new Message(message.getCType(), ID, ANSWER_OK+" - "+command);
            }
            output.put(response);

            System.out.println("Processor #"+ this.myId +" processed a message! "+message);
        }
    }

}