package com.azamat1554.mode;

import com.azamat1554.AESConst;
import com.azamat1554.CipherAES;
import com.azamat1554.ModeOfOperating;

/**
 * Class implements encryption of CBC mode.
 */
public class CBC extends CipherAES implements BlockCipher {
    private static int lastByte;

    //регистр обратной связи
    private byte[] feedBack = null;

    private int offset;

    @Override
    public int update(byte[] streamOfBytes, int endOfArray, boolean last, ModeOfOperating mode) {
        init(streamOfBytes, 0, endOfArray, last, mode);

        offset = 0;

        //получить вектор инициализации из первого блока данных
        if (feedBack == null) {
            feedBack = nextBlock();
            offset = AESConst.BLOCK_SIZE;
        }

        lastByte = makeTransform();

        return lastByte;
    }

    /**
     * Выполняет преобразование данных, в зависимости от режима работы: шифрует или расшивровывает.
     *
     * @return Индекс на конец полезных данных после преобразований
     */
    @Override
    public int makeTransform() {
        int end = 0;
        if (mode == ModeOfOperating.ENCRYPT) {
            while (hasNextBlock()) {
                feedBack = cbAES.encryptBlock(doXOR(nextBlock()));
                end = appendBlock(feedBack, indexOfArray - AESConst.BLOCK_SIZE);
            }
        } else {
            while (hasNextBlock()) {
                byte[] tempArray = nextBlock();
                end = appendBlock(doXOR(cbAES.decryptBlock(tempArray)), indexOfArray - AESConst.BLOCK_SIZE - offset);
                feedBack = tempArray;
            }
        }
        return end;
    }


    private byte[] doXOR(byte[] block) {
        for (int i = 0; i < AESConst.BLOCK_SIZE; i++) {
            block[i] = (byte) (block[i] ^ feedBack[i]); //возможны проблемы с приведением типов
        }
        return block;
    }
}
