package com.azamat1554;

/**
 * Этот класс принимает входной поток, делит его на блоки по 16 байт (если последний блок меньше 16 байт выполняется дополнение),
 * и отправляет на шифрование/расшифрование классу CipherBlockAES, который обрабатывает блок и возвращает результат.
 * Затем все блоки снова склеиваются, и преобразованный входной поток возвращается в вызывающий код.
 *
 * @author Azamat Abidokov
 */
public class CipherAES {
    //ссылка на массив с данными, которые нужно преобразовать
    protected static byte[] data;

    //хранит текущий режим работы
    protected static ModeOfOperating mode;

    //переменная класса для шифрования и расшифровки блоков
    protected CipherBlockAES cbAES;

    //индекс на текущее положение в потоке байтов
    protected int indexOfArray;

    //индекс конца полезных данных в массиве
    private int endOfArray;

    //индекс конца полезных данных в массиве после выполнения дополнения
    private int endWithPadding;

    //указывает, последний это кусок файла или нет
    private boolean lastChunk;

    public CipherAES() {
        cbAES = new CipherBlockAES();
    }

    /**
     * Выполняет инициализацию объекта.
     *
     * @param data         Массив хранящий данные, которые нужно преобразовать
     * @param indexOfArray Индекс на текущее положение в потоке байтов
     * @param endOfArray   Индекс конца полезных данных в массиве до преобразований
     * @param lastChunk    Указывает, последний это кусок файла или нет
     * @param mode         Хранит текущий режим работы
     */
    public void init(byte[] data, int indexOfArray, int endOfArray, boolean lastChunk, ModeOfOperating mode) {
        CipherAES.data = data;
        CipherAES.mode = mode;

        this.indexOfArray = indexOfArray;
        this.endOfArray = endOfArray;
        this.lastChunk = lastChunk;
    }

    //Возвращает индекс конца полезных данных в массиве после выполнения дополнения
    private int getSizeWithPadding() {
        int newSize;

        if (!lastChunk)
            newSize = endOfArray;
        else { //если это последний кусок файла
            //кол-во дополнительных байт
            int padding = AESConst.BLOCK_SIZE - endOfArray % AESConst.BLOCK_SIZE;

            newSize = endOfArray + padding;
        }
        return newSize;
    }

    /**
     * Выполняет преобразование данных, в зависимости от режима работы: шифрует или расшивровывает.
     *
     * @return Индекс на конец полезных данных после преобразований
     */
    //// TODO: 7/25/16 Для того чтобы удовлетворить перфекциониста внутри себя
    //объявить этот метод как абстастный, а реализацию перенести в ECB.
    //Что приведет к единообразию для разных режимов обработки.
    public int makeTransform() {
        int end = 0;
        if (mode == ModeOfOperating.ENCRYPT) {

            endWithPadding = getSizeWithPadding();

            //todo понять замем я добавил второе условие
            while (hasNextBlock() || (indexOfArray < endWithPadding)) {
                end = appendBlock(cbAES.encryptBlock(nextBlock()), indexOfArray - AESConst.BLOCK_SIZE);
            }
        } else {
            while (hasNextBlock()) {
                end = appendBlock(cbAES.decryptBlock(nextBlock()), indexOfArray - AESConst.BLOCK_SIZE);
            }
        }
        return end;
    }

    //проверяет есть ли еще байты в потоке
    protected boolean hasNextBlock() {
        return indexOfArray < endOfArray;
    }

    //возвращает блок байтов
    protected byte[] nextBlock() {
        byte[] block = new byte[AESConst.BLOCK_SIZE];

        for (int i = 0; i < AESConst.BLOCK_SIZE; i++) {
            if (indexOfArray < endOfArray)
                block[i] = data[indexOfArray];
                //Дополнение блока, если это последний кусок файла
            else if (lastChunk && indexOfArray == endOfArray)
                block[i] = (byte) 0x80;
            else
                block[i] = 0x00;

            indexOfArray++;
        }
        return block;
    }

    protected int appendBlock(byte[] block, int position) {//byte[] data, byte[] block) {
        //при дешифровке, если это последний кусок файла и последний блок данных этого куска
        //тогда проверить, на дополнение
        if ((mode == ModeOfOperating.DECRYPT) && lastChunk && !hasNextBlock()) {
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
}
