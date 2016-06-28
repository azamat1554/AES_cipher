package com.azamat1554;

import com.azamat1554.mode.*;
import com.sun.istack.internal.NotNull;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.util.Arrays;

import static com.azamat1554.AESConst.NB;

/**
 * Обработчик файлов, если файл больше 200 МБ, то делит его на части и шифрует.
 * Результат записывается в новый файл, который имеет тоже имя с добавлением
 * <code>encrypted</code> в конце.
 */
public class FileHandler {
    //размер массива
    private final int size = 134_217_728;

    //хранит байты файла
    private final byte[] bytesOfMsg = new byte[size];

    private BlockCipher cipher;

    //кол-во считываемых байт
    private int countOfByte = 0;

    //указывает на то, последняя это часть файла или нет
    private boolean lastChunk = true;

    //шифрует файл на который указывает параметр inputFlow
    @NotNull
    public byte[] encrypt(String fileName, char[] secretKey, Mode mode) {
        try (FileInputStream fin = new FileInputStream(fileName);
             FileOutputStream fout = new FileOutputStream(fileName + ".encrypted")) {

            System.out.println("\nEncrypt file.");

            //инициализирует класс в зависимости от режима
            cipher = getCipherMode(mode);

            //хранит хэш переменной secretKey, чтобы не допустить утечки пароля
            cipher.setKey(MessageDigest.getInstance("MD5").digest(toByte(secretKey)));

            //обнулить массив с ключем
            Arrays.fill(secretKey, '\u0000');

            //цикл чтения из файла
            do {
                lastChunk = true;
                if ((countOfByte = fin.available()) > size - 4 * NB) {
                    countOfByte = size - 4 * NB; //один блок в запасе для дополнения
                    lastChunk = false;
                }

                //считать байты из файла в массив с нулевого индекса до countOfByte
                fin.read(bytesOfMsg, 0, countOfByte);

                //long start = System.currentTimeMillis();
                //todo придумать, как сделать так, чтобы не создавать новый массив,
                //но при этом обработка существующего выполнялась до тех пор пока есть полезные данные
                byte[] cipherText = cipher.encrypt(bytesOfMsg, countOfByte, lastChunk);
                //System.out.println((System.currentTimeMillis() - start) / 1000);

                //записывает зашифрованные данные в новый файл
                fout.write(cipherText, 0, cipher.getSizeOfArray());
            } while (fin.available() > 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //расшифровывает файл на который указывает параметр inputFlow
    public byte[] decrypt(String fileName, char[] secretKey, Mode mode) {
        try (FileInputStream fin = new FileInputStream(fileName);
             FileOutputStream fout = new FileOutputStream(fileName.substring(0, fileName.lastIndexOf('.')))) {

            System.out.println("\nDecrypt file.");

            //инициализирует класс в зависимости от режима
            cipher = getCipherMode(mode);

            //хранит хэш переменной secretKey, чтобы не допустить утечки пароля
            cipher.setKey(MessageDigest.getInstance("MD5").digest(toByte(secretKey)));

            //обнулить массив с ключем
            Arrays.fill(secretKey, '\u0000');

            //цикл чтения из файла
            do {
                lastChunk = true;
                if ((countOfByte = fin.available()) > size - 4 * NB) {
                    countOfByte = 134_217_728;
                    lastChunk = false;
                }

                //считать байты из файла в массив с нулевого индекса до size
                fin.read(bytesOfMsg, 0, countOfByte);

                //long start = System.currentTimeMillis();
                byte[] cipherText = cipher.decrypt(bytesOfMsg, countOfByte, lastChunk);
                //System.out.println((System.currentTimeMillis() - start) / 1000);

                //записывает зашифрованные данные в новый файл
                fout.write(cipherText, 0, cipher.getSizeOfArray());
            } while (fin.available() > 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //инициализирует класс в зависимости от режима
    private BlockCipher getCipherMode(Mode mode) {
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

    private byte[] toByte(char[] input) {
        byte[] output = new byte[input.length * 2];
        for (int i = 0; i < input.length; i++) {
            output[i * 2] = (byte) (input[i] >> 8);
            output[i * 2 + 1] = (byte) input[i];
        }
        return output;
    }

    private char[] toChar(byte[] input) {
        char[] output = new char[input.length / 2];
        for (int i = 0; i < output.length; i++) {
            output[i] = (char) ((input[i * 2] << 8) + (input[i * 2 + 1] & 0xff));
        }
        return output;
    }
}
