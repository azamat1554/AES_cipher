package com.azamat1554.cipher;

import java.util.concurrent.atomic.AtomicLong;

import com.azamat1554.cipher.modes.BlockCipher;

import static com.azamat1554.cipher.modes.BlockCipher.getMode;

/**
 * Этот класс принимает входной поток, делит его на блоки по 16 байт (если последний блок меньше 16 байт выполняется дополнение),
 * и отправляет на шифрование/расшифрование классу CipherBlockAES, который обрабатывает блок и возвращает результат.
 * Затем все блоки снова соединяются, и преобразованный входной поток возвращается в вызывающий код.
 *
 * @author Azamat Abidokov
 */
public abstract class CipherChunk {
    /* Ссылка на массив с данными, которые нужно преобразовать. */
    private static byte[] data;

    /* Переменная класса для шифрования и расшифровки блоков. */
    protected final CipherBlockAES cbAES;

    /* Индекс на текущее положение в потоке байтов. */
    private int currentPosition;

    /* Индекс конца полезных данных в массиве. */
    private int endOfBytes;

    /* Индекс конца полезных данных в массиве после выполнения дополнения. */
    private int endOfChunk;

    /* Флаг, который указывает, последний это кусок файла или нет. */
    private boolean lastChunk;

    /* Количество записанных блоков. */
    public static final AtomicLong completedBlocks = new AtomicLong();

    public CipherChunk() {
        cbAES = new CipherBlockAES();
    }

    /* Возвращает первый блок данных. */
    public byte[] getFirstBlock() {
        if (currentPosition != 0) return null;
        return nextBlock();
    }

    /**
     * Выполняет инициализацию объекта.
     *
     * @param data      Массив хранящий данные, которые нужно преобразовать
     * @param from      Индекс с которого начинается чтение байтов
     * @param to        Индекс до которого будет выполняться чтение байтов
     * @param lastChunk Указывает, последний это кусок файла или нет.
     */
    public void init(byte[] data, int from, int to, boolean lastChunk) {
        CipherChunk.data = data;

        currentPosition = from;
        endOfBytes = to;
        this.lastChunk = lastChunk;

        //если режим шифрование и это последний кусок файла, тогда посчитать размер с дополнением, иначе не менять размер
        endOfChunk = (getMode() == ModeOf.ENCRYPTION) & lastChunk ? getSizeWithPadding() : to;
    }

    /**
     * Возвращает индекс конца полезных данных в массиве после выполнения дополнения.
     */
    private int getSizeWithPadding() {
        //кол-во дополнительных байт
        int padding = AESConst.BLOCK_SIZE - endOfBytes % AESConst.BLOCK_SIZE;

        return endOfBytes + padding;
    }

    /**
     * Выполняет преобразование данных, в зависимости от режима работы: шифрует или расшифровывает.
     *
     * @return Индекс на конец полезных данных после преобразований
     */
    public abstract int makeTransform() throws InterruptedException;

    /**
     * Проверяет есть ли еще байты, которые нужно обработать.
     *
     * @return {@code true} если еще есть байты, {@code false} иначе.
     */
    protected boolean hasNextBlock() {
        return currentPosition < endOfChunk;
    }

    /**
     * Возвращает следующий блок байтов.
     * Если количества оставшихся байт недостаточно для того,
     * чтобы заполнить блок полностью - выполняется дополнение.
     */
    protected byte[] nextBlock() {
        byte[] block = new byte[AESConst.BLOCK_SIZE];

        for (int i = 0; i < AESConst.BLOCK_SIZE; i++) {
            if (currentPosition < endOfBytes)
                block[i] = data[currentPosition];
                //Дополнение блока, если это последний кусок файла
            else if (lastChunk && currentPosition == endOfBytes)
                block[i] = (byte) 0x80;
            else
                block[i] = 0x00;

            currentPosition++;
        }
        return block;
    }

    /**
     * Записывает преобразованный блок данных, на место последнего полученного блока.
     *
     * @param block Массив который требуется записать.
     * @return Возвращает индекс последнего обработанного байта.
     */
    protected int writeTransformedBlock(byte[] block) throws InterruptedException {
        int position = currentPosition - AESConst.BLOCK_SIZE;

        int to = AESConst.BLOCK_SIZE;
        if (containPadding()) to = startPadding(block);

        for (int i = 0; i < to; i++) {
            data[position++] = block[i];
        }

        // Если поток был прерван, тогда сгенерировать исключение.
        if (BlockCipher.cipherStop) throw new InterruptedException();

        // Увеличиваем количество обработанных блоков.
        completedBlocks.incrementAndGet();

        return position;
    }

    /* Возвращает индекс байта с которого начинается дополнение. */
    private int startPadding(byte[] block) {
        int i = AESConst.BLOCK_SIZE - 1;
        while (block[i] != (byte) 0x80) i--;

        return i;
    }

    /**
     * Проверяет содержит ли текущий блок дополнение.
     *
     * @return {@code true} если текущий блок содержит дополнение, {@code false} иначе.
     */
    private boolean containPadding() {
        //при дешифровке, если это последний кусок файла и последний блок данных этого куска,
        //тогда он содержит дополнение
        return (getMode() == ModeOf.DECRYPTION) && lastChunk && !hasNextBlock();
    }
}
