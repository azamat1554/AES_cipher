package com.azamat1554;

/**
 * Этот класс принимает входной поток, делит его на блоки по 16 байт, и отправляет на шифрование/расшифрование
 * классу CipherBlockAES, который обрабатывает блок и возвращает результат. Затем все блоки снова
 * склеиваются и преобразованный входной поток возвращается в вызивающий код.
 *
 * @author Azamat Abidokov
 */
public class CipherAES {
    //переменная класса для шифрования и расшифровки блоков
    private CipherBlockAES cbAES;

    //индекс на текущее положение в потоке байтов
    private int indexOfArray;

    //индекс конца полезных данных в массиве до преобразований
    private int endOfArray;

    //индекс конца данный в массиве после преобразований
    private int lastByte;

    //последний кусок файла?
    private boolean lastChunk;

    //хранит текущий режим
    private ModeOfOperating mode;

    public CipherAES() {
        cbAES = new CipherBlockAES();
    }

    public void init(int indexOfArray, int endOfArray, boolean lastChunk, ModeOfOperating mode) {
        this.indexOfArray = indexOfArray;
        this.endOfArray = endOfArray;
        this.lastChunk = lastChunk;
        this.mode = mode;

        lastByte = setLastByteOfArray();
    }

    /**
     * Шифрует входную последовательнойть байт и возвращает рузультат.
     *
     * @param streamOfBytes Исходный (открытый) массив данных
     * @return Зашифрованный блок данных
     */
    public byte[] encrypt(byte[] streamOfBytes, int endOfArray, boolean lastChunk) {
        init(0, endOfArray, lastChunk, ModeOfOperating.ENCRYPT);

        makeTransform(streamOfBytes);

        return streamOfBytes;
    }

    /**
     * Расшифровывает входную последовательнойть байт и возвращает рузультат.
     *
     * @param streamOfBytes Массив хранящий зашифрованные байты, в методе <code>encrypt()</code>
     * @return Расшифрованный массив данных
     */
    public byte[] decrypt(byte[] streamOfBytes, int endOfArray, boolean lastChunk) {
        init(0, endOfArray, lastChunk, ModeOfOperating.DECRYPT);

        lastByte = makeTransform(streamOfBytes);
        return streamOfBytes;
    }

    public int makeTransform(byte[] data) {
        int end = 0;
        if (mode == ModeOfOperating.ENCRYPT) {
            while (hasNextBlock()) {
                end = append(data, cbAES.encryptBlock(nextBlock(data)));
            }
        } else {
            while (hasNextBlock()) {
                end = append(data, cbAES.decryptBlock(nextBlock(data)));
            }
        }
        return end;
    }

    //возвращает значение индекса, на конец полезных данных в массиве байтов
    public int getIndexOfLastByte() {
        return lastByte;
    }

    private int setLastByteOfArray() {
        //требуемое кол-во блоков для хранения данных после шифрования/дешифрования
        int tempSize;

        if ((mode == ModeOfOperating.DECRYPT) || !lastChunk) {
            tempSize = endOfArray;
        } else { //если это последний кусок файла
            //отношение длины входного потока байтов к длине одного блока
            double ratio = endOfArray / (4.0 * AESConst.NB);

            //кол-во необходимых блоков
            int numberOfBlock = (int) (ratio % 1 == 0 ? ratio + 1 : Math.ceil(ratio));

            //необходимое кол-во байт, с учетом дополнения до блока
            tempSize = 4 * AESConst.NB * numberOfBlock;
        }
        return tempSize;
    }

    //проверяет есть ли еще байты в потоке
    private boolean hasNextBlock() {
        if (indexOfArray < lastByte)
            return true;
        else
            return false;
    }

    //возвращает блок байтов
    private byte[] nextBlock(byte[] streamOfBytes) {
        byte[] block = new byte[4 * AESConst.NB];

        for (int i = 0; i < block.length; i++) {
            if (indexOfArray < endOfArray)
                block[i] = streamOfBytes[indexOfArray];
                //Дополнение блока, если это последний блок (кусок) файла
            else if (lastChunk && indexOfArray == endOfArray)
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
        int end = indexOfArray - 4 * AESConst.NB;

        //если выполняется дешифровка, это последний кусок файла и последний блок данных этого куска
        //тогда проверить, на дополнение
        if ((mode == ModeOfOperating.DECRYPT) && lastChunk && !hasNextBlock()) {
            for (int i = 0; i < block.length; i++) {
                if (isPadding(block, i)) break;
                streamOfBytes[end++] = block[i];
            }
        } else {
            for (int i = 0; i < block.length; i++) {
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
