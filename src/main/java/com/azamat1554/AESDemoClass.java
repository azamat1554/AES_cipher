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
        byte[] bytes = input.nextLine().getBytes();

        System.out.println("\nEnter your secret key below (without spaces):");
        byte[] key = input.next().getBytes();

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            key = md.digest(key);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

//        int[] bytesOfMsg = {
//                0x32, 0x43, 0xf6, 0xa8,
//                0x88, 0x5a, 0x30, 0x8d,
//                0x31, 0x31, 0x98, 0xa2,
//                0xe0, 0x37, 0x07, 0x80
//        };
//
//        int[] secretKey = {
//                0x2b, 0x7e, 0x15, 0x16,
//                0x28, 0xae, 0xd2, 0xa6,
//                0xab, 0xf7, 0x15, 0x88,
//                0x09, 0xcf, 0x4f, 0x3c
//        };

        CipherAES aes = new CipherAES();

        int[] secretKey = castToInt(key);
        int[] bytesOfMsg = castToInt(bytes);

        int[] cipherText = aes.encrypt(bytesOfMsg, secretKey);
        String encodingText = Base64.getEncoder().encodeToString(castToByte(cipherText));
        System.out.println("\nEncrypted text:\n" + encodingText);

        int[] decryptText = aes.decrypt(castToInt(Base64.getDecoder().decode(encodingText)), secretKey);
        System.out.println("\nDecrypted text:\n" + new String(castToByte(decryptText)) + "\n");

        //output(cipherText);
        //output(decryptText);

        System.out.println(Arrays.equals(bytesOfMsg, decryptText));
    }

    private static void output(int[] array) {
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
