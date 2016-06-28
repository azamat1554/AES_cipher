package com.azamat1554.mode;

import com.azamat1554.CipherAES;

/**
 * Created by FamilyAccount on 07.06.2016.
 */
public class CBC extends BlockCipher {

    public CBC() {

    }

    @Override
    public byte[] encrypt(byte[] bytesOfMsg, int countOfByte, boolean lastChunk) {
        return new byte[0];
    }

    @Override
    public byte[] decrypt(byte[] bytesOfMsg, int countOfByte, boolean lastChunk) {
        return new byte[0];
    }
}
