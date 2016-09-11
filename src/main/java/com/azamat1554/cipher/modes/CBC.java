package com.azamat1554.cipher.modes;

import com.azamat1554.cipher.AESConst;
import com.azamat1554.cipher.CipherChunk;
import com.azamat1554.cipher.ModeOf;

/**
 * Реализует режим блочного шифра (Cipher block chaining).
 *
 * В этом режиме каждый блок данных зависит от предыдущего,
 * поэтому параллельная обработка невозможна. Для шифрования
 * первого блока используется случайная последовательность байт (IV).
 * При шифровании IV записывается в первый блок данных, что позволяет
 * при расшифровании считать его из первого блока.
 *
 * @author Azamat Abidokov
 */
public class CBC extends BlockCipher {
    /* Регистр обратной связи, хранит предыдущий блок данных. */
    private byte[] feedback = null;


    private final CipherChunk cipher = new CipherChunk() {
        @Override
        public int makeTransform() throws InterruptedException {
            int end = 0;
            if (getMode() == ModeOf.ENCRYPTION)
                while (hasNextBlock()) {
                    feedback = cbAES.encryptBlock(doXOR(nextBlock(), feedback));
                    end = writeTransformedBlock(feedback);

                }
            else
                while (hasNextBlock()) {
                    byte[] tempArray = nextBlock();
                    end = writeTransformedBlock(doXOR(cbAES.decryptBlock(tempArray), feedback));
                    feedback = tempArray;
                }

            return end;
        }
    };

    /**
     * Запускает процесс преобразования данных.
     *
     * @param streamOfBytes Массив хранящий данные, которые нужно преобразовать
     * @param endOfData     Индекс конца данных
     * @param last          Указывает, последний это кусок файла или нет
     * @return Индекс на конец полезных данных после преобразований.
     * @throws InterruptedException Генерируется если поток шифра был прерван.
     */
    @Override
    public int update(byte[] streamOfBytes, int endOfData, boolean last) throws InterruptedException {
        cipher.init(streamOfBytes, 0, endOfData, last);

        if (feedback == null) {
            //генерировать вектор только один раз и только при шифровании
            if (getMode() == ModeOf.ENCRYPTION) {
                writeIV(streamOfBytes);
            }
            //получить вектор инициализации из первого блока данных
            feedback = cipher.getFirstBlock();
        }

        return cipher.makeTransform();
    }

    private byte[] doXOR(byte[] a, byte[] b) {
        for (int i = 0; i < AESConst.BLOCK_SIZE; i++) {
            a[i] = (byte) (a[i] ^ b[i]);
        }
        return a;
    }
}
