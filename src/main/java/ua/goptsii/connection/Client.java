package ua.goptsii.connection;

import com.google.common.primitives.UnsignedLong;
import ua.goptsii.network.TCPNetwork;
import ua.goptsii.packet.CommandTypeEncoder;
import ua.goptsii.packet.Message;
import ua.goptsii.packet.Packet;

import java.io.IOException;

public class Client {
    public static void main(String[] args) throws InterruptedException, IOException {
        Message m = new Message(CommandTypeEncoder.randomCommand(), 1, "fuck you");
        Packet packet = new Packet((byte) 1, UnsignedLong.valueOf(1), m);
        try {

            TCPNetwork network = new TCPNetwork();

            System.out.println("Server running via"+network);

            network.connect();

            network.send(packet);

            Packet answer =  network.receive();

            network.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
