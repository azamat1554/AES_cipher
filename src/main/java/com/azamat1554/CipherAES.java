package com.azamat1554;

/**
 * Этот класс принимает входной поток, делит его на блоки по 16 байт, и отправляет на шифрование/расшифрование
 * классу CipherBlockAES, который обрабатывает блок и возвращает результат. Затем все блоки снова
 * склеиваются и преобразованный входной поток возвращается в вызивающий код.
 *
 * @author Azamat Abidokov
 */
public class CipherAES implements AESConst {
    //переменная класса для шифрования и расшифровки блоков
    private CipherBlockAES cbAES;

    //индекс на текущее положение во входном потоке байтов
    private int indexOfArray;

    //индекс конца полезных данных в массиве
    private int endOfArray;

    //индекс конца данный в массиве мовле преобразований
    private int sizeOfArray;

    //последний блок?
    private boolean lastChunk;

    //выходной поток байтов
    private byte[] arrayOfBytes; //outputBytes

    public CipherAES() {
        cbAES = new CipherBlockAES();
    }

    public void setKey(byte[] secretKey) {
        cbAES.init(secretKey);
    }

    private void init(int endOfArray, boolean lastChunk) {
        this.endOfArray = endOfArray;
        this.lastChunk = lastChunk;
    }

    /**
     * Шифрует входную последовательнойть байт и возвращает рузультат.
     *
     * @param plainText Исходный (открытый) массив данных
     * @return Зашифрованный блок данных
     */
    public byte[] encrypt(byte[] plainText, int endOfArray, boolean lastChunk) {
        init(endOfArray, lastChunk);

        //выходной поток
        sizeOfArray = setSizeOfArray();

        indexOfArray = 0;
        while (hasNextBlock()) {
            append(plainText, cbAES.encryptBlock(nextBlock(plainText)));
        }

        return plainText; //arrayOfBytes;
    }

    /**
     * Расшифровывает входную последовательнойть байт и возвращает рузультат.
     *
     * @param cipherText Массив хранящий зашифрованные байты, в методе <code>encrypt()</code>
     * @return Расшифрованный массив данных
     */
    public byte[] decrypt(byte[] cipherText, int endOfArray, boolean lastChunk) {
        init(endOfArray, lastChunk);

        //входной поток
        //arrayOfBytes = new byte[cipherText.length];

        indexOfArray = 0;
        sizeOfArray = this.endOfArray;
        int end = 0;
        while (hasNextBlock()) {
            end = append(cipherText, cbAES.decryptBlock(nextBlock(cipherText)));
        }
        sizeOfArray = end;
        return cipherText; //arrayOfBytes;
    }

    //возвращает значение индекса, на конец полезных данных в массиве байтов
    public int getSizeOfArray() {
        return sizeOfArray;
    }

    private int setSizeOfArray() {
        int tempSize;
        if (!lastChunk) {
            tempSize = endOfArray;
        } else {
            //отношение длины входного потока байтов к длине одного блока
            double ratio = endOfArray / (4.0 * NB);

            //кол-во необходимых блоков
            int numberOfBlock = (int) (ratio % 1 == 0 ? ratio + 1 : Math.ceil(ratio));

            //выходной поток
            tempSize = 4 * NB * numberOfBlock;
        }
        return tempSize;
    }

    //проверяет есть ли еще байты в потоке
    private boolean hasNextBlock() {
        if (indexOfArray < sizeOfArray) //arrayOfBytes.length)
            return true;
        else
            return false;
    }

    //возвращает блок байтов
    private byte[] nextBlock(byte[] streamOfBytes) {
        byte[] block = new byte[4 * NB];

        for (int i = 0; i < block.length; i++) {
            if (indexOfArray < endOfArray)  //streamOfBytes.length)
                block[i] = streamOfBytes[indexOfArray];
                //Дополнение блока, если это последний блок (кусок) файла
            else if (lastChunk && indexOfArray == endOfArray)  //streamOfBytes.length) {
                block[i] = (byte) 0x80;
            else
                block[i] = 0x00;

            indexOfArray++;
        }
        return block;
    }

    //присоединить обработанные байты в выходной массив
    private int append(byte[] streamOfBytes, byte[] block) {
        //int end = indexOfArray - 4 * NB;
        int end = indexOfArray - 4 * NB;

        if (!lastChunk || hasNextBlock()) { //если это не последний кусок файла
            //или есть еще блоки данных, тогда просто добавляем
            for (int i = 0; i < block.length; i++) {
                streamOfBytes[end++] = block[i];
            }
        } else { //иначе, если это последний кусок файла и последний блок данных
            for (int i = 0; i < block.length & end < sizeOfArray; i++) {
                if (isPadding(block, i)) break;
                streamOfBytes[end++] = block[i];
            }
        }
        return end;
    }

    private boolean isPadding(byte[] block, int i) {
        if (block[i] == (byte) 0x80) {
            for (int j = i + 1; j < block.length; j++) {
                if (block[j] != 0x00) return false;
            }
            return true;
        }
        return false;
    }
}
