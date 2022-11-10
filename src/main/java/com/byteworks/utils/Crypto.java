package com.byteworks.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
//import android.util.Base64;

public class Crypto {

    /**
     * Read a TripleDES secret key from a byte array
     */
    public static SecretKey read3DESKey(byte[] rawkey) {
        // Read the raw bytes from the keyfile
        try {
            DESedeKeySpec keyspec = new DESedeKeySpec(rawkey);

            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DESede");
            SecretKey key;

            key = keyfactory.generateSecret(keyspec);
            key = keyfactory.translateKey(key);

            return key;
        } catch (InvalidKeySpecException ex) {
            return null;
        } catch (NoSuchAlgorithmException ex) {
            return null;
        } catch (InvalidKeyException ex) {
            return null;
        }
    }

    public static SecretKey readDESKey(byte[] rawkey) {
        // Read the raw bytes from the keyfile
        try {
            DESKeySpec keyspec = new DESKeySpec(rawkey);
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyfactory.generateSecret(keyspec);
            return key;
        } catch (InvalidKeySpecException ex) {
            return null;
        } catch (NoSuchAlgorithmException ex) {
            return null;
        } catch (InvalidKeyException ex) {
            return null;
        }

    }

    public static String EncryptDES(Key key, byte[] clearText) {
        try {

            Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            //System.out.println(cipher.getOutputSize(3));
            //byte[] clearText=hexToByte(clearComp);

            CipherOutputStream out = new CipherOutputStream(bytes, cipher);
            out.write(clearText);
            out.flush();
            out.close();
            byte[] ciphertext = bytes.toByteArray();
            bytes.flush();
            bytes.close();

            String encrypted = ToHexString(ciphertext);

            java.util.Arrays.fill(clearText, (byte) 0);
            java.util.Arrays.fill(ciphertext, (byte) 0);

            return encrypted;
        } catch (IOException ex) {
            return null;
        } catch (NoSuchPaddingException ex) {
            return null;
        } catch (NoSuchAlgorithmException ex) {
            return null;
        } catch (InvalidKeyException ex) {
            return null;
        }
    }

    public static String Encrypt3DES(Key key, String clearComp) {
        try {

            Cipher cipher;

            cipher = Cipher.getInstance("DESede/ECB/NoPadding");

            cipher.init(Cipher.ENCRYPT_MODE, key);

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

            byte[] clearText = hexToByte(clearComp);

            CipherOutputStream out = new CipherOutputStream(bytes, cipher);
            out.write(clearText);
            out.flush();
            out.close();
            byte[] ciphertext = bytes.toByteArray();
            bytes.flush();
            bytes.close();

            String encrypted = ToHexString(ciphertext);

            java.util.Arrays.fill(clearText, (byte) 0);
            java.util.Arrays.fill(ciphertext, (byte) 0);

            return encrypted;
        } catch (IOException ex) {
            return null;
        } catch (NoSuchPaddingException ex) {
            return null;
        } catch (NoSuchAlgorithmException ex) {
            return null;
        } catch (InvalidKeyException ex) {
            return null;
        }

    }

    public static String Decrypt3DES(Key key, String cipherComp) {
        try {

            Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");

            cipher.init(Cipher.DECRYPT_MODE, key);

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

            byte[] ciphertext = hexToByte(cipherComp);
            CipherOutputStream out;
            out = new CipherOutputStream(bytes, cipher);
            out.write(ciphertext);
            out.flush();
            out.close();
            byte[] deciphertext = bytes.toByteArray();
            bytes.flush();
            bytes.close();

            String decrypted = ToHexString(deciphertext);

            java.util.Arrays.fill(ciphertext, (byte) 0);
            java.util.Arrays.fill(deciphertext, (byte) 0);

            return decrypted;
        } catch (IOException ex) {
            return null;
        } catch (NoSuchPaddingException ex) {
            return null;
        } catch (NoSuchAlgorithmException ex) {
            return null;
        } catch (InvalidKeyException ex) {
            return null;
        }

    }

    public static String DecryptDES(Key key, String cipherComp) {
        try {
            Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key);

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

            byte[] cipherText = hexToByte(cipherComp);
            CipherOutputStream out;
            out = new CipherOutputStream(bytes, cipher);
            out.write(cipherText);
            out.flush();
            out.close();
            byte[] deciphertext = bytes.toByteArray();
            bytes.flush();
            bytes.close();

            String decrypted = ToHexString(deciphertext);

            java.util.Arrays.fill(cipherText, (byte) 0);
            java.util.Arrays.fill(deciphertext, (byte) 0);

            return decrypted;
        } catch (IOException ex) {
            return null;
        } catch (NoSuchPaddingException ex) {
            return null;
        } catch (NoSuchAlgorithmException ex) {
            return null;
        } catch (InvalidKeyException ex) {
            return null;
        }
    }

    public static String ToHexString(byte[] toAsciiData) {
        String hexString = "";

        for (byte b : toAsciiData) {
            hexString += String.format("%02X", b);
        }
        return hexString;
    }

    public static String toAscii(String toAsciiData) {
        String hexString = "";
        char hexChars[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        int i = 0;
        while (toAsciiData.length() > i) {

            int hbits = (toAsciiData.charAt(i) & 0x00f0) >>> 4;
            int lbits = (toAsciiData.charAt(i) & 0x000f);

            hexString += hexChars[hbits];

            hexString += hexChars[lbits];
            i++;
        }
        return hexString;
    }

    public static String MAC(String mwk, String macData) {
        String macValue = "0000000000000000";
        String mwk1 = "";
        String mwk2 = "";

        if(mwk == null){
            return macValue;
        }

        try{
            mwk1 = mwk.substring(0, 16);
            mwk2 = mwk.substring(16, 32);
        }
        catch(Exception ex){
            return macValue;
        }

        byte[] key1byte = hexToByte(mwk1);
        byte[] key2byte = hexToByte(mwk2);
        //System.out.println("From MAC method meesge to mac:"+macData);
        SecretKey key1 = Crypto.readDESKey(key1byte);
        SecretKey key2 = Crypto.readDESKey(key2byte);
        if (key1 == null || key2 == null) {
            return macValue;
        }

        macData = toAscii(macData);
        while ((macData.length() % 16) != 0) {
            macData += "0";
        }
        int len = macData.length() / 16;
        for (int i = 0; i < len; i++) {
            byte[] mac1 = Crypto.hexToByte(macValue);
            byte[] mac2 = Crypto.hexToByte(macData.substring(i * 16, i * 16 + 16));
            for (int j = 0; j < 8; j++) {
                mac1[j] ^= mac2[j];
            }
            macValue = Crypto.EncryptDES(key1, mac1);
        }
        macValue = Crypto.DecryptDES(key2, macValue);
        macValue = Crypto.EncryptDES(key1, Crypto.hexToByte(macValue));

        return macValue;
    }

    /**
     * Encrypt a string with AES algorithm.
     *
     * @param data is a string
     * @return the encrypted string
     */
    /*public static String AESEncrypt(String data, byte[] sessionKey) throws Exception {
        Key key = generateKey(sessionKey);
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(data.getBytes());
        return Base64.encodeToString(encVal, Base64.DEFAULT);
    }*/

    /**
     * Decrypt a string with AES algorithm.
     *
     * @param encryptedData is a string
     * @return the decrypted string
     */
    /*public static String AESDecrypt(String encryptedData, byte[] sessionKey) throws Exception {
        Key key = generateKey(sessionKey);
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = Base64.decode(encryptedData, Base64.DEFAULT);
        byte[] decValue = c.doFinal(decordedValue);
        return new String(decValue);
    }*/

    /**
     * Generate a new encryption key.
     */
    private static Key generateKey(byte[] sessionKey) throws Exception {
        return new SecretKeySpec(sessionKey, "AES");
    }

    public static byte[] hexToByte(String hexString) {
        String str = new String("0123456789ABCDEF");
        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0, j = 0; i < hexString.length(); i++) {
            byte firstQuad = (byte) ((str.indexOf(hexString.charAt(i))) << 4);
            byte secondQuad = (byte) str.indexOf(hexString.charAt(++i));
            bytes[j++] = (byte) (firstQuad | secondQuad);

        }
        return bytes;
    }

    public static void main(String[] ad) {

        //encryptedMasterKey=7D72B6D5FAF13BF8D2FC258E3D2329EA
        //posParamaters.key2=5D25072F04832A2329D93E4F91BA23A2
        //posParamaters.key3=86CBCDE3B0A22354853E04521686863D

        //decryptedMasterKey=254F298CF268B607FB31899D76832F79


        //encryptedSessionKey=F1497756E89BB3220762094293D9137C
        //decryptedSessionKey=895B403D0402FB2F2A25861A4A3849AB


        //encryptedPINKey=9F2CE4BB234336AF45FE98354553571D
        //decryptedPINKey=B037D6081F1F1A5EB6D002012FB6D33D
        
        //URL url = Crypto.class.getResource("/libepms.so");
        //System.out.println("path="+url.getPath());

        String key1 = "5D25072F04832A2329D93E4F91BA23A2";//"044F6F2936D028B935F9C35CB9FE8A00";
        String key2 = "86CBCDE3B0A22354853E04521686863D";//"BF468079DC20576D0DB58C64589B5220";


        byte[] keyB1 = Crypto.hexToByte(key1 + key1.substring(0, 16));
        byte[] keyB2 = Crypto.hexToByte(key2 + key2.substring(0, 16));
        byte[] keyB3 = new byte[keyB1.length];//TripleDES.hexToByte(key3 + key3.substring(0, 16));

        for (int i = 0; i < keyB1.length; i++) {
            keyB3[i]=(byte)(((byte)(keyB1[i] ^ keyB2[i])));
        }

        SecretKey key = Crypto.read3DESKey(keyB3);
        String dmk = Crypto.Decrypt3DES(key, "7D72B6D5FAF13BF8D2FC258E3D2329EA");
        System.out.println("dmk: " + dmk);

        keyB1 = Crypto.hexToByte( dmk +  dmk.substring(0, 16));
        key = Crypto.read3DESKey(keyB1);
        String dsk = Crypto.Decrypt3DES(key, "F1497756E89BB3220762094293D9137C");
        System.out.println("dsk: " + dsk);

        keyB1 = Crypto.hexToByte( dmk +  dmk.substring(0, 16));
        key = Crypto.read3DESKey(keyB1);
        String dpk = Crypto.Decrypt3DES(key, "9F2CE4BB234336AF45FE98354553571D");
        System.out.println("dpk: " + dpk);
        
        SecureKeyProvider sp = new SecureKeyProvider();
        System.out.println("RRN="+sp.get(15));

    }
}
