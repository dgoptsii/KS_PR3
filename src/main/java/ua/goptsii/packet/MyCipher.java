package ua.goptsii.packet;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class MyCipher {

    private static Cipher cipher;
    private static SecretKey secretKey;

    //static block of initialization
    static {
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        byte[] encryptionKeyBytes = "thisIsA128BitKey".getBytes();
        secretKey = new SecretKeySpec(encryptionKeyBytes, "AES");
    }

    public static byte[] encode(byte[] message){
        byte[] encodedMessage = new byte[0];
         try {
             synchronized (cipher) {
                 cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                 encodedMessage = cipher.doFinal(message);
             }
        } catch (IllegalBlockSizeException | InvalidKeyException | BadPaddingException e) {
            e.printStackTrace();
        }
        return encodedMessage;
    }


    public static byte[] decode(byte[] message){

        byte[] decodedMessage = new byte[0];
        try {
            synchronized (cipher) {
                cipher.init(Cipher.DECRYPT_MODE, secretKey);
                decodedMessage = cipher.doFinal(message);
            }
        } catch (IllegalBlockSizeException | InvalidKeyException | BadPaddingException e) {
            e.printStackTrace();
        }
        return decodedMessage;
    }

}
