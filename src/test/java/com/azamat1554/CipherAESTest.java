package com.azamat1554;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by FamilyAccount on 20.04.2016.
 */
public class CipherAESTest {
    //plain (original) bytes
    private byte[] bytesOfMsg = {
            0x32, 0x43, (byte) 0xf6, (byte) 0xa8,
            (byte) 0x88, 0x5a, 0x30, (byte) 0x8d,
            0x31, 0x31, (byte) 0x98, (byte) 0xa2,
            (byte) 0xe0, 0x37, 0x07, 0x34,
            0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0,
            0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0
    };

    private byte[] secretKey = {
            0x2b, 0x7e, 0x15, 0x16,
            0x28, (byte) 0xae, (byte) 0xd2, (byte) 0xa6,
            (byte) 0xab, (byte) 0xf7, 0x15, (byte) 0x88,
            0x09, (byte) 0xcf, 0x4f, 0x3c
    };

    private byte[] cipherBytes = {
            0x39, 0x25, (byte) 0x84, 0x1d, 0x02, (byte) 0xdc, (byte) 0x09, (byte) 0xfb,
            (byte) 0xdc, 0x11, (byte) 0x85, (byte) 0x97, 0x19, 0x6a, 0x0b, 0x32,
            (byte) 0xf6, (byte) 0xc7, 0x1e, (byte) 0xed, (byte) 0xc3, (byte) 0xd9, (byte) 0x9b, (byte) 0xb1,
            (byte) 0x83, (byte) 0xcb, 0x5b, (byte) 0x8d, 0x15, 0x68, (byte) 0xe6, 0x06
    };

    //instance of class is implemented AES encryption
    CipherAES cAES = new CipherAES();

    @Before
    public void init() {
        //cAES.setKey(secretKey);
        CipherBlockAES.Key.setKey(secretKey);

    }

    @Test
    public void testEncrypt() throws Exception {
        cAES.init(bytesOfMsg, 0, bytesOfMsg.length - 16, true, ModeOfOperating.ENCRYPT);

        cAES.makeTransform();

        //сравнивает с ожидаемым результатом
        assertArrayEquals(cipherBytes, bytesOfMsg);
    }

    @Test
    public void testDecrypt() throws Exception {
        cAES.init(cipherBytes, 0, cipherBytes.length, true, ModeOfOperating.DECRYPT);

        int lastByte = cAES.makeTransform();

        //сравнивает с ожидаемым результатом
        assertArrayEquals(Arrays.copyOf(bytesOfMsg, bytesOfMsg.length - 16), Arrays.copyOf(cipherBytes, lastByte));
    }
}