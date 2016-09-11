package com.azamat1554.cipher.modes;

import com.azamat1554.cipher.AESConst;
import com.azamat1554.cipher.CipherChunk;
import com.azamat1554.cipher.ModeOf;

import java.security.SecureRandom;

/**
 * Этот класс расширяется всеми классами,
 * реализующими различные режими шифрования.
 *
 * @author Azamat Abidokov
 */
public abstract class BlockCipher {
    /** Хранит текущий режим работы. */
    private static ModeOf mode;

    /**
     * Флаг указывающий, поток выполняющий шифрование/расшифрование остановлен или нет.
     */
    public static volatile boolean cipherStop = false;

    /**
     * Запускает процесс преобразования данных.
     *
     * @param streamOfBytes Массив хранящий данные, которые нужно преобразовать
     * @param endOfData     Индекс конца данных
     * @param last          Указывает, последний это кусок файла или нет
     * @return Индекс на конец полезных данных после преобразований.
     * @throws InterruptedException Генерируется если поток шифра был прерван.
     */
    public abstract int update(byte[] streamOfBytes, int endOfData, boolean last)
            throws InterruptedException;

    /**
     * Инициализирует класс в зависимости от установленного режима.
     *
     * @param cipherMode Режим работы блочного шифра.
     */
    public static BlockCipher getCipher(CipherMode cipherMode) {
        BlockCipher cipher = null;
        switch (cipherMode) {
            case ECB:
                cipher = new ECB();
                break;
            case CBC:
                cipher = new CBC();
        }
        return cipher;
    }

    /** Устанавливает режим работы программы. */
    public static void setMode(ModeOf mode) {
        BlockCipher.mode = mode;
    }

    /** Возвращает режим работы программы. */
    public static ModeOf getMode() {
        return mode;
    }

    /**
     * Генерирует случайную последовательность байт (вектор инициализации - IV).
     * А затем записывает IV в первый блок массива {@code data}.
     *
     * @param data Массив с данными.
     */
    protected void writeIV(byte[] data) {
        //генерация IV
        byte[] iv = new SecureRandom().generateSeed(AESConst.BLOCK_SIZE);

        for (int i = 0; i < AESConst.BLOCK_SIZE; i++)
            data[i] = iv[i];
    }

    /** Возвращет количество обработанных байтов. */
    public static long getProgress() {
        return CipherChunk.completedBlocks.get() * AESConst.BLOCK_SIZE;
    }

    /** Сбрасывает статические переменные. */
    public static void reset() {
        cipherStop = false;
        CipherChunk.completedBlocks.set(0);
    }
}