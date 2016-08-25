package com.azamat1554.mode;

import com.azamat1554.AESConst;
import com.azamat1554.CipherChunk;
import com.azamat1554.ModeOfOperating;

/**
 * Class implements encryption of CBC mode.
 */
public class CBC extends BlockCipher {
    private static int lastByte;

    //регистр обратной связи
    private byte[] feedback = null;

    private int numberOfBlocksOffset;

    private CipherChunk cipher = new CipherChunk() {
        @Override
        public int makeTransform() {
            int end = 0;
//            if (mode == ModeOfOperating.ENCRYPT) {
//                byte[] temp = nextBlock();
//                //feedback = cbAES.encryptBlock(doXOR(nextBlock(), feedback));
//                while (hasNextBlock()) {
//                    feedback = cbAES.encryptBlock(doXOR(temp, feedback));
//                    temp = nextBlock();
//                    appendBlock(feedback, getBackwardPosition(numberOfBlocksOffset));
//                }
//                feedback = cbAES.encryptBlock(doXOR(temp, feedback));
//                end = appendBlock(feedback, currentPosition - numberOfBlocksOffset * AESConst.BLOCK_SIZE);
//            } else {
//                while (hasNextBlock()) {
//                    byte[] tempArray = nextBlock();
//                    end = appendBlock(doXOR(cbAES.decryptBlock(tempArray), feedback), getBackwardPosition(numberOfBlocksOffset));
//                    feedback = tempArray;
//                }
//            }

            if (mode == ModeOfOperating.ENCRYPT) {
                while (hasNextBlock()) {
                    feedback = cbAES.encryptBlock(doXOR(nextBlock(), feedback));
                    end = appendBlock(feedback, 1);
                }
            } else {
                while (hasNextBlock()) {
                    byte[] tempArray = nextBlock();
                    end = appendBlock(doXOR(cbAES.decryptBlock(tempArray), feedback), 1);
                    feedback = tempArray;
                }
            }

            return end;
        }
    };

    @Override
    public int update(byte[] streamOfBytes, int offset, int endOfData, boolean last, ModeOfOperating mode) {
        if (feedback == null) {
            //генерировать вектор только один раз и только при шифровании
            if (mode == ModeOfOperating.ENCRYPT) {
                generateIV();
                writeIV(streamOfBytes);
            }

            //получить вектор инициализации из первого блока данных
            feedback = getFirstBlock(streamOfBytes);
        }

        //cipher.init(streamOfBytes, offset, length + offset, last, mode);
        cipher.init(streamOfBytes, offset, endOfData, last, mode);
        lastByte = cipher.makeTransform();

        return lastByte;
    }

    private byte[] doXOR(byte[] a, byte[] b) {
        for (int i = 0; i < AESConst.BLOCK_SIZE; i++) {
            a[i] = (byte) (a[i] ^ b[i]); //возможны проблемы с приведением типов
        }
        return a;
    }
}
