package com.azamat1554.mode;

import com.azamat1554.CipherAES;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Class implements concurrent encryption of ECB mode.
 */
public class ECB extends BlockCipher {
//    CipherAES aes;
//    public ECB() {
//        aes = new CipherAES();
//    }

    public byte[] encrypt(byte[] plainText, int countOfByte, boolean lastChunk) {
        //// TODO: 04.05.2016
        return aes.encrypt(plainText, countOfByte, lastChunk);
    }

    public byte[] decrypt(byte[] plainText, int countOfByte, boolean lastChunk) {
        //// TODO: 04.05.2016
        return aes.decrypt(plainText, countOfByte, lastChunk);
    }
}
