package com.azamat1554;

import com.azamat1554.cipher.CipherBlockAES;
import com.azamat1554.cipher.ModeOf;
import com.azamat1554.cipher.modes.BlockCipher;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Testing of class CipherBlockAES
 */
public class CipherBlockAESTest {
    //plain (original) bytes
    private byte[] bytesOfMsg = {
            0x32, 0x43, (byte) 0xf6, (byte) 0xa8,
            (byte) 0x88, 0x5a, 0x30, (byte) 0x8d,
            0x31, 0x31, (byte) 0x98, (byte) 0xa2,
            (byte) 0xe0, 0x37, 0x07, 0x34
    };

    private byte[] secretKey = {
            0x2b, 0x7e, 0x15, 0x16,
            0x28, (byte) 0xae, (byte) 0xd2, (byte) 0xa6,
            (byte) 0xab, (byte) 0xf7, 0x15, (byte) 0x88,
            0x09, (byte) 0xcf, 0x4f, 0x3c
    };

    private byte[] cipherBytes = {
            0x39, 0x25, (byte) 0x84, 0x1d,
            0x02, (byte) 0xdc, (byte) 0x09, (byte) 0xfb,
            (byte) 0xdc, 0x11, (byte) 0x85, (byte) 0x97,
            0x19, 0x6a, 0x0b, 0x32
    };

    //instance of class is implemented AES encryption
    private CipherBlockAES cbAES = new CipherBlockAES();

    @Before
    public void setKey() {
        CipherBlockAES.Key.setKey(secretKey);
    }

    @Test
    public void testEncryptBlock() throws Exception {
        BlockCipher.setMode(ModeOf.ENCRYPTION);
        assertArrayEquals(cipherBytes, cbAES.encryptBlock(bytesOfMsg));
    }

    @Test
    public void testDecryptBlock() throws Exception {
        BlockCipher.setMode(ModeOf.DECRYPTION);
        assertArrayEquals(bytesOfMsg, cbAES.decryptBlock(cipherBytes));
    }
}