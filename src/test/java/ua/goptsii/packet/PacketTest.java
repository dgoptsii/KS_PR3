package ua.goptsii.packet;

import com.google.common.primitives.UnsignedLong;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertThrows;

public class PacketTest {

    public static final String TEST_MESSAGE = "test message for test";

    @Test
    public void selfTest(){

        Packet pack = new Packet((byte) 7, UnsignedLong.valueOf(1L),new Message(5, 4,TEST_MESSAGE));
        Packet pack2 = null;
        try {
            pack2 = new Packet(pack.toPacket());
        } catch (Exception e) {
            e.printStackTrace();
        }

        pack.getBMsq().decode();
        pack2.getBMsq().decode();

        Message packMessage = pack.getBMsq();
        Message pack2Message = pack2.getBMsq();

        System.out.println("pack message - " + packMessage.getMessageText());
        System.out.println("pack2 message - " + pack2Message.getMessageText());

        Assert.assertEquals(pack.getBSrc(), pack2.getBSrc());
        Assert.assertEquals(pack.getBPktId(), pack2.getBPktId());
        Assert.assertEquals(pack.getWLen(), pack2.getWLen());

        //if crc is equals then parts of packets automatically equals (because when we calculate crc we use a sequence of bytes of packet fields)
        Assert.assertEquals(pack.getWCrc16_1(), pack2.getWCrc16_1());
        Assert.assertEquals(pack.getWCrc16_2(), pack2.getWCrc16_2());

        Assert.assertEquals(packMessage.getCType(), pack2Message.getCType());
        Assert.assertEquals(packMessage.getBUserId(), pack2Message.getBUserId());

        Assert.assertEquals(TEST_MESSAGE, new String(packMessage.getMessage()));
        Assert.assertEquals(packMessage.getMessageText(),  pack2Message.getMessageText());
    }

    @Test
    public void HexArrayTest(){

        Packet orig = new Packet((byte) 123, UnsignedLong.valueOf(478697), new Message(10, 1, "test"));

        //hex representation of "orig" encoded packet
        byte[] packetHex = {
                0x13, 0x7B, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x07, 0x4D, -0x17,
                0x00, 0x00, 0x00, 0x10, 0x6E,
                -0x6E, 0x00, 0x00, 0x00, 0x0A,
                0x00, 0x00, 0x00, 0x01, 0x5E,
                -0x44, -0x06, -0x0C, 0x5F, 0x63,
                -0x2E, -0x2A, -0x60, 0x37, -0x24,
                0x1C, 0x75, -0x28, 0x6B, 0x09,
                0x2F, -0x7A
        };

//        System.out.println(Arrays.toString(orig.toPacket()));
//        System.out.println(Arrays.toString(packetHex));

        Assert.assertEquals(Arrays.toString(orig.toPacket()),Arrays.toString(packetHex));

        Packet hex = null;
        try {
            hex = new Packet(packetHex);
        } catch (Exception e) {
            e.printStackTrace();
        }

        orig.getBMsq().decode();
        hex.getBMsq().decode();

        Message originMessage = orig.getBMsq();
        Message hexMessage = hex.getBMsq();

        Assert.assertEquals(orig.getBSrc(), hex.getBSrc());
        Assert.assertEquals(orig.getBPktId(), hex.getBPktId());
        Assert.assertEquals(orig.getWLen(), hex.getWLen());

        System.out.println("orig message - " + originMessage.getMessageText());
        System.out.println("hex message - " + hexMessage.getMessageText());

        Assert.assertEquals(orig.getWCrc16_1(), hex.getWCrc16_1());
        Assert.assertEquals(orig.getWCrc16_2(), hex.getWCrc16_2());

        Assert.assertEquals(originMessage.getCType(), hexMessage.getCType());
        Assert.assertEquals(originMessage.getBUserId(), hexMessage.getBUserId());

        Assert.assertEquals( orig.getBMsq().getMessageText(), hex.getBMsq().getMessageText());
    }

    @Test
    public void NotEqualsCRC16_1(){

        Packet orig = new Packet((byte) 123, UnsignedLong.valueOf(478697), new Message(10, 1, "test"));

        //hex representation of "orig" encoded packet
        byte[] packetHex = {
                0x13, 0x7B, 0x00, 0x00, 0x00,
                0x03, 0x05, 0x07, 0x4D, -0x17,
                0x00, 0x00, 0x00, 0x10, 0x6E,
                -0x6E, 0x00, 0x00, 0x00, 0x0A,
                0x00, 0x00, 0x00, 0x01, 0x5E,
                -0x44, -0x06, -0x0C, 0x5F, 0x63,
                -0x2E, -0x2A, -0x60, 0x37, -0x24,
                0x1C, 0x75, -0x28, 0x6B, 0x09,
                0x2F, -0x7A
        };

        System.out.println(Arrays.toString(orig.toPacket()));
        System.out.println(Arrays.toString(packetHex));

        Exception exception = assertThrows(Exception.class, () -> { Packet hex  = new Packet(packetHex); } );
        Assert.assertEquals("Wrong wCrc16_1!", exception.getMessage());

    }

    @Test
    public void NotEqualsCRC16_2(){

        Packet orig = new Packet((byte) 123, UnsignedLong.valueOf(478697), new Message(10, 1, "test"));

        //hex representation of "orig" encoded packet
        byte[] packetHex = {
                0x13, 0x7B, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x07, 0x4D, -0x17,
                0x00, 0x00, 0x00, 0x10, 0x6E,
                -0x6E, 0x00, 0x00, 0x00, 0x0A,
                0x00, 0x00, 0x00, 0x01, 0x5E,
                -0x44, -0x07, -0x0D, 0x5F, 0x63,
                -0x2E, -0x2A, -0x60, 0x37, -0x24,
                0x1C, 0x75, -0x28, 0x6B, 0x09,
                0x2F, -0x7A
        };

        System.out.println(Arrays.toString(orig.toPacket()));
        System.out.println(Arrays.toString(packetHex));

        Exception exception = assertThrows(Exception.class, () -> { Packet hex  = new Packet(packetHex); } );
        Assert.assertEquals("Wrong wCrc16_2!", exception.getMessage());

    }

    @Test
    public void NotEqualsBMagic(){

        Packet orig = new Packet((byte) 123, UnsignedLong.valueOf(478697), new Message(10, 1, "test"));

        //hex representation of "orig" encoded packet
        byte[] packetHex = {
                0x2, 0x7B, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x07, 0x4D, -0x17,
                0x00, 0x00, 0x00, 0x10, 0x6E,
                -0x6E, 0x00, 0x00, 0x00, 0x0A,
                0x00, 0x00, 0x00, 0x01, 0x5E,
                -0x44, -0x06, -0x0C, 0x5F, 0x63,
                -0x2E, -0x2A, -0x60, 0x37, -0x24,
                0x1C, 0x75, -0x28, 0x6B, 0x09,
                0x2F, -0x7A
        };

        System.out.println(Arrays.toString(orig.toPacket()));
        System.out.println(Arrays.toString(packetHex));

        Exception exception = assertThrows(Exception.class, () -> { Packet hex  = new Packet(packetHex); } );
        Assert.assertEquals("Wrong bMagic!", exception.getMessage());

    }
}
