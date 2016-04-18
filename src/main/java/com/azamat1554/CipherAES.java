package com.azamat1554;

import java.util.Arrays;

/**
 * Этот класс принимает входной поток, делит его на блоки и отправляет на шифрование/расшифрование
 * классу CipherBlockAES, который обрабатывает блок и возвращает результат. Затем все блоки снова
 * склеиваются и преобразованный входной поток возвращается в вызивающий код.
 *
 * @author Azamat Abidokov
 */
public class CipherAES {
    private CipherBlockAES cbAES;
    private int indexOfArray;
    private int[] arrayOfBytes;

    public CipherAES() {
        cbAES = new CipherBlockAES();
    }

    /**
     * Шифрует входную последовательнойть байт и возвращает рузультат.
     *
     * @param plainText Исходный (открытый) массив данных
     * @param secretKey Секретный ключ для шифрования данных
     * @return Зашифрованный блок данных
     */
    public int[] encrypt(int[] plainText, int[] secretKey) {
        //проверка на пустой массив
        if (plainText.length == 0) return null;

        //входной поток
        arrayOfBytes = new int[(int) (4 * cbAES.NB * Math.ceil(plainText.length / (4.0 * cbAES.NB)))]; //Arrays.copyOf(plainText, (int) (4 * cbAES.NB * Math.ceil(plainText.length / (4.0 * cbAES.NB))));

        indexOfArray = 0;
        while (hasNextBlock()) {
            append(cbAES.encrypt(nextBlock(plainText), secretKey));
        }
        return arrayOfBytes;
    }

    /**
     * Расшифровывает входную последовательнойть байт и возвращает рузультат.
     *
     * @param cipherText Массив хранящий зашифрованные байты, в методе <code>encrypt()</code>
     * @param secretKey  Секретный ключ для шифрования данных
     * @return Расшифрованный массив данных
     */
    public int[] decrypt(int[] cipherText, int[] secretKey) {
        //проверка на пустой массив
        if (cipherText.length == 0) return null;

        //входной поток
        arrayOfBytes = new int[cipherText.length];

        indexOfArray = 0;
        int endOfArray = 0;
        while (hasNextBlock()) {
            endOfArray = append(cbAES.decrypt(nextBlock(cipherText), secretKey));
        }
        arrayOfBytes = Arrays.copyOf(arrayOfBytes, endOfArray);

        return arrayOfBytes;
    }


    //проверяет есть ли еще байты в потоке
    private boolean hasNextBlock() {
        if (indexOfArray < arrayOfBytes.length)
            return true;
        else
            return false;
    }

    //возвращает блок байтов
    private int[] nextBlock(int[] streamOfBytes) {
        int[] block = new int[4 * cbAES.NB];
        for (int i = 0; i < block.length; i++) {
            if (indexOfArray < streamOfBytes.length)
                block[i] = streamOfBytes[indexOfArray];
                //Дополнение блока
            else if (indexOfArray == streamOfBytes.length)
                block[i] = 0x80;
            else
                block[i] = 0x00;

            indexOfArray++;
        }
        return block;
    }

    //присоединить обработанные байты в выходной массив
    private int append(int[] block) {
        int end = indexOfArray - 4 * cbAES.NB;
        for (int i = 0; i < block.length & i < arrayOfBytes.length; i++) {
            if (isPadding(block, i)) break;
            arrayOfBytes[end++] = block[i];
        }
        return end;
    }

    private boolean isPadding(int[] block, int i) {
        if (block[i] == 0x80) {
            for (int j = i + 1; j < block.length; j++) {
                if (block[j] != 0x00) return false;
            }
            return true;
        }
        return false;
    }
}
