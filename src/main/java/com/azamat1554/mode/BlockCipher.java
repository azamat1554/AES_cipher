package com.azamat1554.mode;

import com.azamat1554.AESConst;
import com.azamat1554.ModeOfOperating;

import java.security.SecureRandom;

/**
 * Интерфейс, который должен быть реализован
 * всеми классами выполняющими различные режими
 * шифрования.
 */

//// TODO: 8/19/16 сделать анонимный класс и перенести методы из MainWindow
public abstract class BlockCipher {
    private static byte[] iv;

    /**
     * Выполняет инициализацию объекта, замем запускает задачу на рекурсивное выполнение.
     *
     * @param streamOfBytes Массив хранящий данные, которые нужно преобразовать
     * @param offset        Индекс первого байта
     * @param endOfData     Индекс конца данных
     * @param last          Указывает, последний это кусок файла или нет
     * @param mode          Хранит текущий режим работы
     * @return Индекс на конец полезных данных после преобразований
     */
    public abstract int update(byte[] streamOfBytes, int offset, int endOfData, boolean last, ModeOfOperating mode);

    //инициализирует класс в зависимости от режима
    public static BlockCipher getCipher(Mode mode) {
        BlockCipher cipher = null;
        switch (mode) {
            case ECB:
                cipher = new ECB();
                break;
            case CBC:
                cipher = new CBC();
        }
        return cipher;
    }

    public static byte[] generateIV() {
        return iv = new SecureRandom().generateSeed(AESConst.BLOCK_SIZE);
    }

    /**
     * устанавливает вектор инициализации в первый блок данных
     * и возвращает его
     */
    protected byte[] writeIV(byte[] data) {
        //iv = new SecureRandom().generateSeed(AESConst.BLOCK_SIZE);

        for (int i = 0; i < AESConst.BLOCK_SIZE; i++)
            data[i] = iv[i];

        return iv;
    }

    protected byte[] getFirstBlock(byte[] data) {
        byte[] block = new byte[AESConst.BLOCK_SIZE];
        for (int i = 0; i < AESConst.BLOCK_SIZE; i++)
            block[i] = data[i];

        return block;
    }
}