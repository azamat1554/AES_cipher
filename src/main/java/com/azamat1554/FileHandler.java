package com.azamat1554;

import com.azamat1554.mode.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Arrays;

/**
 * Обработчик файлов, если файл больше 128 МБ, то делит его на части и шифрует.
 * Результат записывается в новый файл, который имеет тоже имя с добавлением
 * <code>encrypted</code> в конце. При дешифровке операции выполняются в обратном порядке.
 */
public class FileHandler {
    //размер массива
    private final int SIZE = 134_217_744;

    //хранит байты файла
    private final byte[] bytesOfFile = new byte[SIZE];

    private BlockCipher cipher;

    //кол-во считываемых байт
    private int numberOfByte = 0;

    //указывает на то, последняя это часть файла или нет
    private boolean lastChunk;

    public void encrypt(String fileName, Mode mode, byte[] iv) {
        try (FileInputStream fin = new FileInputStream(fileName + "file1");
             FileOutputStream fout = new FileOutputStream(fileName + "file2")) {

            System.out.println("\nEncrypt file.");

            int offset = 0;

            //инициализирует класс в зависимости от режима
            cipher = getCipherMode(mode);
            if (mode != Mode.ECB) {
                setIV(bytesOfFile, iv);
                offset = AESConst.BLOCK_SIZE;
            }

            //цикл чтения из файла
            do {
                lastChunk = fin.available() <= (numberOfByte = getSizeOfChunk(fin, mode));

                //считать байты из файла в массив со смещением для первого куска и без для остальных до numberOfByte
                fin.read(bytesOfFile, offset, numberOfByte);

                int lastByte = cipher.update(bytesOfFile, numberOfByte + offset, lastChunk, ModeOfOperating.ENCRYPT);
                offset = 0;

                //записывает зашифрованные данные в новый файл
                fout.write(bytesOfFile, 0, lastByte);
            } while (fin.available() > 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void decrypt(String fileName, Mode mode) {
        try (FileInputStream fin = new FileInputStream(fileName + "file2");
             FileOutputStream fout = new FileOutputStream(fileName + "file3")) {

            System.out.println("\nDecrypt file.");

            //инициализирует класс в зависимости от режима
            cipher = getCipherMode(mode);

            //цикл чтения из файла
            do {
                lastChunk = fin.available() <= (numberOfByte = getSizeOfChunk(fin, mode));

                //считать байты из файла в массив с нулевого индекса до numberOfByte
                fin.read(bytesOfFile, 0, numberOfByte);

                int lastByte = cipher.update(bytesOfFile, numberOfByte, lastChunk, ModeOfOperating.DECRYPT);

                //записывает зашифрованные данные в новый файл
                fout.write(bytesOfFile, 0, lastByte);
            } while (fin.available() > 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private int getSizeOfChunk(FileInputStream fin, Mode mode) throws IOException {
        int numberOfByte = fin.available();
        int temp;

        switch (mode) {

            // TODO: 8/18/16 Возможно имеет смысл всегда оставлять два блока в запасе
            //это сократит код
            case ECB:
                if (numberOfByte > (temp = SIZE - AESConst.BLOCK_SIZE))
                    numberOfByte = temp; //один блок в запасе для дополнения
                break;
            case CBC:
                //два блока в запасе, один для вектора инициализации, второй для дополнения
                if (numberOfByte > (temp = SIZE - 2 * AESConst.BLOCK_SIZE))
                    numberOfByte = temp;
        }
        return numberOfByte;
    }

    //устанавливает вектор инициализации в первый блок данных
    protected void setIV(byte[] data, byte[] iv) {
        for (int i = 0; i < AESConst.BLOCK_SIZE; i++)
            data[i] = iv[i];
    }

    private byte[] toByte(char[] input) {
        byte[] output = new byte[input.length * 2];
        for (int i = 0; i < input.length; i++) {
            output[i * 2] = (byte) (input[i] >> 8);
            output[i * 2 + 1] = (byte) input[i];
        }
        return output;
    }
}
