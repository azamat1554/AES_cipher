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

        CipherAES aes = new CipherAES();

        byte[] cipherText = aes.encrypt(bytesOfMsg, secretKey);
        String encodingText = Base64.getEncoder().encodeToString(cipherText);
        System.out.println("\nEncrypted text:\n" + encodingText);

        //System.out.println("\nIn order to decrypt message, enter it below:");
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
