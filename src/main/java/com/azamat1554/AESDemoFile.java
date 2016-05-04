package com.azamat1554;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;

/**
 * Created by FamilyAccount on 20.04.2016.
 */
public class AESDemoFile {
    public static void main(String[] args) {
        //long start = System.currentTimeMillis();
        encrypt();
        //decrypt();
        //System.out.println((System.currentTimeMillis() - start) / 1000);
    }

    public static void encrypt() {
        try (FileInputStream fin = new FileInputStream("E:\\Users\\FamilyAcc\\Рабочий стол\\file");
             FileOutputStream fout = new FileOutputStream("E:\\Users\\FamilyAcc\\Рабочий стол\\file2");
             Scanner input = new Scanner(System.in)) {

            System.out.println("\nEncrypt file.");

            byte[] bytesOfMsg = new byte[fin.available()];
            fin.read(bytesOfMsg);

            //System.out.println("\nEnter your secret key below (without spaces):");
            byte[] secretKey = "key".getBytes(); //input.next().getBytes();

            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                secretKey = md.digest(secretKey);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            CipherAES aes = new CipherAES();

            long start = System.currentTimeMillis();
            byte[] cipherText = aes.encrypt(bytesOfMsg, secretKey);
            System.out.println((System.currentTimeMillis() - start) / 1000);

            fout.write(cipherText); //Base64.getEncoder().encode(cipherText));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void decrypt() {
        try (FileInputStream fin = new FileInputStream("E:\\Users\\FamilyAcc\\Рабочий стол\\file2.jpg");
             FileOutputStream fout = new FileOutputStream("E:\\Users\\FamilyAcc\\Рабочий стол\\file3.jpg");
             Scanner input = new Scanner(System.in)) {

            System.out.println("\nDecrypt file");
            //input.next(); // для остановки

            byte[] cipherText = new byte[fin.available()];
            fin.read(cipherText);

            //cipherText = Base64.getDecoder().decode(cipherText);

            //System.out.println("\nEnter your secret key below (without spaces):");
            byte[] secretKey = "key".getBytes(); //input.next().getBytes();

            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                secretKey = md.digest(secretKey);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            CipherAES aes = new CipherAES();

            byte[] decryptText = aes.decrypt(cipherText, secretKey);
            fout.write(decryptText);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
