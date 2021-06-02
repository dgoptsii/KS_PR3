package ua.goptsii.network;

import com.google.common.primitives.UnsignedLong;
import ua.goptsii.packet.CommandTypeEncoder;
import ua.goptsii.packet.Message;
import ua.goptsii.packet.Packet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class TCPNetwork {
    ServerSocket serverSocket;
    Socket socket;

    private final String hostProperty="localhost";
    private final String postProperty="2305";

    //server
    public void listen() throws IOException {

        serverSocket = new ServerSocket(Integer.parseInt(postProperty));

        socket = serverSocket.accept();
    }

//client
    public void connect() throws IOException {
        socket = new Socket(hostProperty, Integer.parseInt(postProperty));
        System.out.println("Connected");

    }

    public Packet receive() throws Exception {
        InputStream serverInputStream = socket.getInputStream();
        try{
            byte[] maxPacketBuffer = new byte[Packet.PACKET_MAX_SIZE];

            serverInputStream.read(maxPacketBuffer);

//            serverInputStream.read(maxPacketBuffer);

            System.out.println("Received");
            System.out.println(Arrays.toString(maxPacketBuffer));

            Packet packet = new Packet(maxPacketBuffer);

            System.err.println(packet.getBMsq().getMessageText());

            if(serverSocket != null){
                Message m = new Message(CommandTypeEncoder.randomCommand(), 1, "fuck you too");
                packet = new Packet((byte) 1, UnsignedLong.valueOf(1), m);
                System.out.println("need to send FUCK YOU TOO");
                return packet;
            }
            return packet;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    //client
    public void send(Packet packet) throws IOException {
        OutputStream socketOutputStream = socket.getOutputStream();
        byte[] packetBytes = packet.toPacket();

        socketOutputStream.write(packetBytes);
//        socketOutputStream.flush();

        System.out.println("Send");
        System.out.println(Arrays.toString(packetBytes));
        ////////
        packet.getBMsq().decode();
        System.out.println(packet.getBMsq().getMessageText()+"\n");
    }


    public void close() throws IOException {
        socket.close();
        serverSocket.close();
    }



}
