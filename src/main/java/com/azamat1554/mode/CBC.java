package com.azamat1554.mode;

import com.azamat1554.AESConst;
import com.azamat1554.CipherChunk;
import com.azamat1554.ModeOfOperating;

/**
 * Class implements encryption of CBC mode.
 */
public class CBC extends BlockCipher {
    //регистр обратной связи
    private byte[] feedback = null;

    private CipherChunk cipher = new CipherChunk() {

        @Override
        public int makeTransform() {
            int end = 0;
            if (mode == ModeOfOperating.ENCRYPT) {
                while (hasNextBlock()) {
                    feedback = cbAES.encryptBlock(doXOR(nextBlock(), feedback));
                    end = appendBlock(feedback);
                }
            } else {
                while (hasNextBlock()) {
                    byte[] tempArray = nextBlock();
                    end = appendBlock(doXOR(cbAES.decryptBlock(tempArray), feedback));
                    feedback = tempArray;
                }
            }
            return end;
        }
    };

    @Override
    public int update(byte[] streamOfBytes, int endOfData, boolean last, ModeOfOperating mode) {
        cipher.init(streamOfBytes, 0, endOfData, last, mode);

        if (feedback == null) {
            //генерировать вектор только один раз и только при шифровании
            if (mode == ModeOfOperating.ENCRYPT) {
                generateIV();
                writeIV(streamOfBytes);
            }
            //получить вектор инициализации из первого блока данных
            feedback = cipher.getFirstBlock();
        }

        int IndexOfLastByte = cipher.makeTransform();
        return IndexOfLastByte;
    }

    private byte[] doXOR(byte[] a, byte[] b) {
        for (int i = 0; i < AESConst.BLOCK_SIZE; i++) {
            a[i] = (byte) (a[i] ^ b[i]); //возможны проблемы с приведением типов
        }
        return a;
    }
}
