package com.azamat1554.mode;

import com.azamat1554.CipherAES;
import com.azamat1554.ModeOfOperating;

/**
 * Created by FamilyAccount on 07.06.2016.
 */
public class CBC implements BlockCipher {

    public CBC() {

    }

    //@Override
    public byte[] encrypt(byte[] bytesOfMsg, int countOfByte, boolean lastChunk) {
        return new byte[0];
    }

    //@Override
    public byte[] decrypt(byte[] bytesOfMsg, int countOfByte, boolean lastChunk) {
        return new byte[0];
    }

    @Override
    public int update(byte[] streamOfBytes, int endOfArray, boolean last, ModeOfOperating mode) {return 0;}

    //@Override
    public int getIndexOfLastByte() {
        return 0;
    }
}
