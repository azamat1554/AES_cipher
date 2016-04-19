package com.azamat1554;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;

/**
 * Created by FamilyAccount on 15.04.2016.
 */
public class AESDemoClass {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.println("Enter your message below:");
        byte[] bytesOfMsg = input.nextLine().getBytes();

        System.out.println("\nEnter your secret key below (without spaces):");
        byte[] secretKey = input.next().getBytes();

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            secretKey = md.digest(secretKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

//        byte[] bytesOfMsg = {
//                0x32, 0x43, (byte) 0xf6, (byte) 0xa8,
//                (byte) 0x88, 0x5a, 0x30, (byte) 0x8d,
//                0x31, 0x31, (byte) 0x98, (byte) 0xa2,
//                (byte) 0xe0, 0x37, 0x07, 0x34
//        };
//
//        output(bytesOfMsg);
//
//        byte[] secretKey = {
//                0x2b, 0x7e, 0x15, 0x16,
//                0x28, (byte) 0xae, (byte) 0xd2, (byte) 0xa6,
//                (byte) 0xab, (byte) 0xf7, 0x15, (byte) 0x88,
//                0x09, (byte) 0xcf, 0x4f, 0x3c
//        };
//        output(secretKey);

        CipherAES aes = new CipherAES();

        //int[] secretKey = castToInt(key);
        //int[] bytesOfMsg = castToInt(bytes);

        byte[] cipherText = aes.encrypt(bytesOfMsg, secretKey);
        String encodingText = Base64.getEncoder().encodeToString(cipherText);
        System.out.println("\nEncrypted text:\n" + encodingText);

        byte[] decryptText = aes.decrypt(Base64.getDecoder().decode(encodingText), secretKey);
        System.out.println("\nDecrypted text:\n" + new String(decryptText) + "\n");

        //output(cipherText);
        //output(decryptText);

        System.out.println(Arrays.equals(bytesOfMsg, decryptText));
    }

    private static void output(byte[] array) {
        for (int r = 0; r < array.length; r++) {
            if (r > 0 & r % 16 == 0)
                System.out.println();
            System.out.print(Integer.toHexString(array[r]) + ", ");
        }
        System.out.println("\n");
    }

    private static int[] castToInt(byte[] bytes) {
        int[] array = new int[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            array[i] = bytes[i] & 0xff;
        }
        return array;
    }

    private static byte[] castToByte(int[] integers) {
        byte[] array = new byte[integers.length];
        for (int i = 0; i < integers.length; i++) {
            array[i] = (byte) integers[i];
        }
        return array;
    }

}
