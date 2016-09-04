package com.azamat1554.mode;

import com.azamat1554.AESConst;
import com.azamat1554.CipherChunk;
import com.azamat1554.ModeOf;

import java.security.SecureRandom;

/**
 * Интерфейс, который должен быть реализован
 * всеми классами выполняющими различные режими
 * шифрования.
 */
public abstract class BlockCipher {
    public static volatile boolean cipherStop = false;

    private static byte[] iv;

    /**
     * Выполняет инициализацию объекта, замем запускает задачу на рекурсивное выполнение.
     *
     * @param streamOfBytes Массив хранящий данные, которые нужно преобразовать
     * @param endOfData     Индекс конца данных
     * @param last          Указывает, последний это кусок файла или нет
     * @param mode          Хранит текущий режим работы
     * @return Индекс на конец полезных данных после преобразований
     */
    public abstract int update(byte[] streamOfBytes, int endOfData, boolean last, ModeOf mode)
            throws InterruptedException;

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

    public static long getProgress() {
        return CipherChunk.completedBlocks.get() * AESConst.BLOCK_SIZE;
    }

    //сбрасывает статические переменные
    public static void reset() {
        cipherStop = false;
        CipherChunk.completedBlocks.set(0);
    }

    public static String hexSrting(byte[] array) {
        char[] hex = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        StringBuilder ivStr = new StringBuilder();
        for (byte anArray : array) {
            ivStr.append(hex[(anArray & 0xf0) >> 4]).append(hex[anArray & 0x0f]).append(' ');
        }
        return ivStr.toString();
    }
}