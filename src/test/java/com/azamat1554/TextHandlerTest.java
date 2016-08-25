package com.azamat1554;

import com.azamat1554.mode.CBC;
import com.azamat1554.mode.Mode;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Testing of TextHandler class
 */
public class TextHandlerTest {
    private TextHandler th = new TextHandler();

    private byte[] iv = {0x2f, (byte) 0xf2, (byte) 0xfe, 0x3c,
            0x39, (byte) 0xb8, 0x15, (byte) 0xe0,
            0x67, (byte) 0xc5, (byte) 0xa5, 0x30,
            0x2d, 0x5d, (byte) 0xc5, 0x6e
    };

    private byte[] secretKey = {
            0x2b, 0x7e, 0x15, 0x16,
            0x28, (byte) 0xae, (byte) 0xd2, (byte) 0xa6,
            (byte) 0xab, (byte) 0xf7, 0x15, (byte) 0x88,
            0x09, (byte) 0xcf, 0x4f, 0x3c
    };

    @Before
    public void init() {
        CipherBlockAES.Key.setKey(secretKey);
    }

    @Test
    public void encryptAndDecryptTest() throws Exception {
        assertEquals("encrypt", th.decrypt(th.encrypt("encrypt", Mode.CBC), Mode.CBC));

    }
}