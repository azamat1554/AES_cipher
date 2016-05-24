package com.azamat1554;

import com.azamat1554.mode.ECB;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;

/**
 * Этот класс принимает входной поток, делит его на блоки и отправляет на шифрование/расшифрование
 * классу CipherBlockAES, который обрабатывает блок и возвращает результат. Затем все блоки снова
 * склеиваются и преобразованный входной поток возвращается в вызивающий код.
 *
 * @author Azamat Abidokov
 */
public class CipherAES {
    //переменная класса для шифрования и расшифровки блоков
    private CipherBlockAES cbAES;

    //индекс на текущее положение во входном потоке байтов
    private int indexOfArray;

    //выходной поток байтов
    private byte[] arrayOfBytes; //outputBytes

    /**
     * Перечисление, которое содержит режимы шифрования
     */
    public enum Mode {
        ECB, CBC
    }

    public CipherAES(byte[] secretKey) {
        cbAES = new CipherBlockAES(secretKey);
    }

    //шифрует файл на который указывает параметр inputFlow
    public byte[] encrypt(FileInputStream inputFlow, byte[] secretKey, Mode mode) {
        //ECB ecb = new ECB(secretKey);
        //ecb.encrypt(inputFlow, secretKey);
        //// TODO: 04.05.2016 если файл большой, считать часть файла и отправить на шифрование
        //в подкласс соответствующий режиму. Затем зашифрованный массив записать в новый файл oldFile.format.encrypted

        return null;
    }

    //расшифровывает файл на который указывает параметр inputFlow
    public byte[] decrypt(FileOutputStream inputFlow, byte[] secretKey, Mode mode) {
        //ECB ecb = new ECB(secretKey);
        //ecb.decrypt(inputFlow, secretKey);
        //// TODO: 04.05.2016 если файл большой, считать часть файла и отправить на шифрование
        //в подкласс соответствующий режиму. Затем зашифрованный массив записать в новый файл oldFile.format.encrypted

        return null;
    }

    /**
     * Шифрует входную последовательнойть байт и возвращает рузультат.
     *
     * @param plainText Исходный (открытый) массив данных
     * @return Зашифрованный блок данных
     */
    public byte[] encrypt(byte[] plainText) {
        //проверка на пустой массив
        if (plainText.length == 0) return null;

        //отношение длины входного потока байтов к длине одного блока
        double ratio = plainText.length / (4.0 * cbAES.NB);

        //кол-во необходимых блоков
        int numberOfBlock = (int) (ratio % 1 == 0 ? ratio + 1 : Math.ceil(ratio));

        //выходной поток
        arrayOfBytes = new byte[4 * cbAES.NB * numberOfBlock]; //Arrays.copyOf(plainText, (int) (4 * cbAES.NB * Math.ceil(plainText.length / (4.0 * cbAES.NB))));

        indexOfArray = 0;
        while (hasNextBlock()) {
            append(cbAES.encryptBlock(nextBlock(plainText)));
        }

        return arrayOfBytes;
    }

    /**
     * Расшифровывает входную последовательнойть байт и возвращает рузультат.
     *
     * @param cipherText Массив хранящий зашифрованные байты, в методе <code>encrypt()</code>
     * @return Расшифрованный массив данных
     */
    public byte[] decrypt(byte[] cipherText) {
        //проверка на пустой массив
        if (cipherText.length == 0) return null;

        //входной поток
        arrayOfBytes = new byte[cipherText.length];

        indexOfArray = 0;
        int endOfArray = 0;
        while (hasNextBlock()) {
            endOfArray = append(cbAES.decryptBlock(nextBlock(cipherText)));
        }

        indexOfArray = endOfArray; //todo temp desision
        //arrayOfBytes = Arrays.copyOf(arrayOfBytes, endOfArray);
        return arrayOfBytes;
    }

    //возвращает значение индекса, на текущее положение в массиве байтов
    public int getIndexOfArray() {
        return indexOfArray;
    }

    //проверяет есть ли еще байты в потоке
    private boolean hasNextBlock() {
        if (indexOfArray < arrayOfBytes.length)
            return true;
        else
            return false;
    }

    //возвращает блок байтов
    private byte[] nextBlock(byte[] streamOfBytes) {
        byte[] block = new byte[4 * cbAES.NB];
        for (int i = 0; i < block.length; i++) {
            if (indexOfArray < streamOfBytes.length)
                block[i] = streamOfBytes[indexOfArray];
                //Дополнение блока
            else if (indexOfArray == streamOfBytes.length) {
                block[i] = (byte) 0x80;
                indexOfArray += block.length - i;
                break;
            }

            indexOfArray++;
        }
        return block;
    }

    //присоединить обработанные байты в выходной массив
    private int append(byte[] block) {
        int end = indexOfArray - 4 * cbAES.NB;

        if (hasNextBlock()) {
            for (int i = 0; i < block.length & i < arrayOfBytes.length; i++) {
                arrayOfBytes[end++] = block[i];
            }
        } else {
            for (int i = 0; i < block.length & end < arrayOfBytes.length; i++) {
                if (isPadding(block, i)) break;
                arrayOfBytes[end++] = block[i];
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
