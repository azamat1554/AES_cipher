package com.azamat1554;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;

/**
 * Класс с которого начинается выполнение программы.
 * Текст для шифрования вводится с клавиатуры в консоль, после чего вводится ключ шифрования,
 * от которого вычисляется хэш (MD5).
 *
 * @author Azamat Abidokov
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
            secretKey = md.digest(secretKey); //вычисляет хэш
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        //объект класса, осуществляющего шифрование/расшифровку
        CipherAES aes = new CipherAES();

        //шифрование
        byte[] cipherText = aes.encrypt(bytesOfMsg, secretKey);

        //байты кодируются символами системы счисления Base64
        String encodingText = Base64.getEncoder().encodeToString(cipherText);
        System.out.println("\nEncrypted text:\n" + encodingText);

        //System.out.println("\nIn order to decrypt message, enter it below:");
        //расшифровка
        byte[] decryptText = aes.decrypt(Base64.getDecoder().decode(encodingText), secretKey);
        System.out.println("\nDecrypted text:\n" + new String(decryptText) + "\n");


        input.close();
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
