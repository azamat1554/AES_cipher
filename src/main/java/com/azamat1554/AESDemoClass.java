package com.azamat1554;

import java.util.Arrays;
import java.util.List;

/**
 * Created by FamilyAccount on 15.04.2016.
 */
public class AESDemoClass {
    public static void main(String[] args) {
        CipherAES aes = new CipherAES();
        int[] bytesmass = {
                0x32, 0x43, 0xf6, 0xa8,
                0x88, 0x5a, 0x30, 0x8d,
                0x31, 0x31, 0x98, 0xa2,
                0xe0, 0x37, 0x07, 0x80
        };

        int[] secretKey = {
                0x2b, 0x7e, 0x15, 0x16,
                0x28, 0xae, 0xd2, 0xa6,
                0xab, 0xf7, 0x15, 0x88,
                0x09, 0xcf, 0x4f, 0x3c
        };

        int[] cipherText = aes.encrypt(bytesmass, secretKey);
        output(cipherText);
        int[] decryptText = aes.decrypt(cipherText, secretKey);
        output(decryptText);

        System.out.println(Arrays.equals(bytesmass, decryptText));
    }

    private static void output(int[] array) {
        for (int r = 0; r < array.length; r++) {
            if (r > 0 & r % 16 == 0)
                System.out.println();
            System.out.print(Integer.toHexString(array[r]) + ", ");
        }
        System.out.println("\n");

    }
}
