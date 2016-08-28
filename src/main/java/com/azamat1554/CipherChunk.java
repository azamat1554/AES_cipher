package com.azamat1554;

/**
 * Этот класс принимает входной поток, делит его на блоки по 16 байт (если последний блок меньше 16 байт выполняется дополнение),
 * и отправляет на шифрование/расшифрование классу CipherBlockAES, который обрабатывает блок и возвращает результат.
 * Затем все блоки снова соединяются, и преобразованный входной поток возвращается в вызывающий код.
 *
 * @author Azamat Abidokov
 */
public abstract class CipherChunk {
    //ссылка на массив с данными, которые нужно преобразовать
    protected static byte[] data;

    //хранит текущий режим работы
    protected static ModeOf mode;

    //переменная класса для шифрования и расшифровки блоков
    protected CipherBlockAES cbAES;

    //индекс на текущее положение в потоке байтов
    protected int currentPosition;

    //индекс конца полезных данных в массиве
    private int endOfBytes;

    //индекс конца полезных данных в массиве после выполнения дополнения
    private int endOfChunk;

    //указывает, последний это кусок файла или нет
    private boolean lastChunk;

    public CipherChunk() {
        cbAES = new CipherBlockAES();
    }

    /**
     * Выполняет инициализацию объекта.
     *
     * @param data      Массив хранящий данные, которые нужно преобразовать
     * @param from      Индекс с которого начинается чтение байтов
     * @param to        Индекс до которого будет выполняться чтение байтов
     * @param lastChunk Указывает, последний это кусок файла или нет
     * @param mode      Хранит текущий режим работы
     */
    public void init(byte[] data, int from, int to, boolean lastChunk, ModeOf mode) {
        CipherChunk.data = data;
        CipherChunk.mode = mode;

        currentPosition = from;
        endOfBytes = to;
        this.lastChunk = lastChunk;

        //если режим шифрование и это последний кусок файла, тогда посчитать размер с дополнением, иначе не менять размер
        endOfChunk = (mode == ModeOf.ENCRYPTION) & lastChunk ? getSizeWithPadding() : to;
    }

    //Возвращает индекс конца полезных данных в массиве после выполнения дополнения
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
    public abstract int makeTransform();

    //проверяет есть ли еще байты в потоке
    protected boolean hasNextBlock() {
        return currentPosition < endOfChunk;
    }

    //возвращает блок байтов
    protected byte[] nextBlock() {
        byte[] block = new byte[AESConst.BLOCK_SIZE];

        for (int i = 0; i < AESConst.BLOCK_SIZE; i++) {
            if (currentPosition < endOfBytes)
                block[i] = data[currentPosition];
                //Дополнение блока, если это последний кусок файла
            else if (lastChunk && currentPosition == endOfBytes)
                block[i] = (byte) 0x80;
            else //if (lastChunk)
                block[i] = 0x00;

            currentPosition++;
        }
        return block;
    }

    //Возвращает индекс последнего обработанного байта
    protected int writeTransformedBlock(byte[] block) {
        int position = currentPosition - AESConst.BLOCK_SIZE;

        //при дешифровке, если это последний кусок файла и последний блок данных этого куска
        //тогда проверить, на дополнение
        if ((mode == ModeOf.DECRYPTION) && lastChunk && !hasNextBlock()) {
            for (int i = 0; i < AESConst.BLOCK_SIZE; i++) {
                if (isPadding(block, i)) break;
                data[position++] = block[i];
            }
        } else {
            for (int i = 0; i < AESConst.BLOCK_SIZE; i++) {
                data[position++] = block[i];
            }
        }

        return position;
    }

    private boolean isPadding(byte[] block, int i) {
        if (block[i] == (byte) 0x80) {
            for (int j = i + 1; j < AESConst.BLOCK_SIZE; j++) {
                if (block[j] != 0x00) return false;
            }
            return true;
        }
        return false;
    }

    public byte[] getFirstBlock() {
        if (currentPosition != 0) return null;
        return nextBlock();
    }
}
