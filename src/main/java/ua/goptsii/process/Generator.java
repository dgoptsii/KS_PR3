package ua.goptsii.process;

import com.google.common.primitives.UnsignedLong;
import lombok.Data;
import ua.goptsii.packet.CommandTypeEncoder;
import ua.goptsii.packet.Message;
import ua.goptsii.packet.Packet;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

//Fake realisation of interface, that generate
@Data
public class Generator {

    public static BlockingQueue<byte[]> generateInput(int bound,int threadsNumber,int messageNumber) {
        BlockingQueue<byte[]> messagesInput = new LinkedBlockingQueue<>(bound);

        //simulate messages sent to server
        for (int i = 0; i < messageNumber; i++) {
            Message m = new Message(CommandTypeEncoder.randomCommand(), 1, "Hello there! #" + i);
            m.encode();
            Packet p = new Packet((byte) 1, UnsignedLong.valueOf(i), m);
            messagesInput.add(p.toPacket());
        }
        //put as many poisonous pills as threads working
        for (int j = 0; j < threadsNumber; j++) {
            messagesInput.add( new byte[0]);
        }
        return messagesInput;
    }
}
