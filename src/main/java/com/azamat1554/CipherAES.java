package com.azamat1554;

/**
 * Этот класс принимает входной поток, делит его на блоки по 16 байт, и отправляет на шифрование/расшифрование
 * классу CipherBlockAES, который обрабатывает блок и возвращает результат. Затем все блоки снова
 * склеиваются и преобразованный входной поток возвращается в вызивающий код.
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
        this.data = data;
        this.indexOfArray = indexOfArray;
        this.endOfArray = endOfArray;
        this.lastChunk = lastChunk;
        this.mode = mode;
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
    public int makeTransform() { //byte[] data) {
        int end = 0;
        if (mode == ModeOfOperating.ENCRYPT) {

            endWithPadding = getSizeWithPadding();

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
        if (indexOfArray < endOfArray)
            return true;
        else
            return false;
    }

    //возвращает блок байтов
    protected byte[] nextBlock() { //byte[] data) {
        byte[] block = new byte[AESConst.BLOCK_SIZE];

        for (int i = 0; i < AESConst.BLOCK_SIZE; i++) {
            if (indexOfArray < endOfArray)
                block[i] = data[indexOfArray];
                //Дополнение блока, если это последний блок (кусок) файла
            else if (lastChunk && indexOfArray == endOfArray)
                block[i] = (byte) 0x80;
            else
                block[i] = 0x00;

            indexOfArray++;
        }
        return block;
    }

//    //возвращает блок байтов
//    private byte[][] nextBlock(byte[] streamOfBytes) {
//        byte[][] block = new byte[4][AESConst.NB];
//
//        for (int c = 0; c < AESConst.NB; c++) {
//            for (int r = 0; r < 4; r++) {
//                if (indexOfArray < endOfArray)
//                    block[r][c] = streamOfBytes[indexOfArray];
//                    //Дополнение блока, если это последний блок (кусок) файла
//                else if (lastChunk && indexOfArray == endOfArray)
//                    block[r][c] = (byte) 0x80;
//                else
//                    block[r][c] = 0x00;
//
//                indexOfArray++;
//            }
//        }
//
//        return block;
//    }

    //присоединить обработанные байты в выходной массив
//    protected int appendBlock(byte[] block) {//byte[] data, byte[] block) {
//        int end = indexOfArray - AESConst.BLOCK_SIZE;
//
//        //если выполняется дешифровка, это последний кусок файла и последний блок данных этого куска
//        //тогда проверить, на дополнение
//        if ((mode == ModeOfOperating.DECRYPT) && lastChunk && !hasNextBlock()) {
//            for (int i = 0; i < AESConst.BLOCK_SIZE; i++) {
//                if (isPadding(block, i)) break;
//                data[end++] = block[i];
//            }
//        } else {
//            for (int i = 0; i < AESConst.BLOCK_SIZE; i++) {
//                data[end++] = block[i];
//            }
//        }
//        return end;
//    }
    protected int appendBlock(byte[] block, int position) {//byte[] data, byte[] block) {
        //int end = indexOfArray - AESConst.BLOCK_SIZE;

        //если выполняется дешифровка, это последний кусок файла и последний блок данных этого куска
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
