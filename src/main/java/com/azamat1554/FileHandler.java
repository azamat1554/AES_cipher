package com.azamat1554;

import com.azamat1554.mode.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Обработчик файлов, если файл больше 128 МБ, то делит его на части и шифрует.
 * Результат записывается в новый файл, который имеет тоже имя с добавлением
 * <code>encrypted</code> в конце. При дешифровке операции выполняются в обратном порядке.
 */
public class FileHandler {
    //размер массива без дополнительных блоков
    private final int SIZE = 134_217_728;

    // TODO: 8/24/16 Возможно имеет смысл перевести все на ByteArrayInputStream или ArrayList 
    //хранит байты файла. Два блока в запасе, один для вектора инициализации, второй для дополнения
    private final byte[] bytesOfFile = new byte[SIZE + 2 * AESConst.BLOCK_SIZE];

    private BlockCipher cipher;

    //кол-во считываемых байт
    private int numberOfBytes = 0;

    //указывает на то, последняя это часть файла или нет
    private boolean lastChunk;

    public void encrypt(String fileName, Mode mode) {
        try (FileInputStream fin = new FileInputStream(fileName + "file1");
             FileOutputStream fout = new FileOutputStream(fileName + "file2")) {

            System.out.println("\nEncrypt file.");

            //инициализирует класс в зависимости от режима
            cipher = BlockCipher.getCipher(mode);

            //Смещение нужно для того чтобы добавить IV в первый блок
            int offset = 0;
            if (mode != Mode.ECB)
                offset = AESConst.BLOCK_SIZE;

            int restBytes;
            //цикл чтения из файла
            while ((restBytes = fin.available()) > 0) {
                lastChunk = restBytes <= SIZE;
                //считать из файла указанное кол-во байт или сколько осталось, и вернуть прочитанное количество байт.
                numberOfBytes = fin.read(bytesOfFile, offset, SIZE);

                int lastByte = cipher.update(bytesOfFile, numberOfBytes + offset, lastChunk, ModeOfOperating.ENCRYPT);
                offset = 0;

                //записывает зашифрованные данные в новый файл
                fout.write(bytesOfFile, 0, lastByte);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void decrypt(String fileName, Mode mode) {
        try (FileInputStream fin = new FileInputStream(fileName + "file2");
             FileOutputStream fout = new FileOutputStream(fileName + "file3")) {

            System.out.println("\nDecrypt file.");

            //инициализирует класс в зависимости от режима
            cipher = BlockCipher.getCipher(mode);

            //Смещение нужно чтобы оставить место для IV
            int offset = 0;
            if (mode != Mode.ECB)
                offset = AESConst.BLOCK_SIZE;

            int restBytes = fin.available();
            //цикл чтения из файла
            while ((restBytes = fin.available()) > 0) {
                lastChunk = restBytes <= SIZE;
                //считать из файла указанное кол-во байт или сколько осталось, и вернуть прочитанное количество байт.
                numberOfBytes = fin.read(bytesOfFile, 0, SIZE);

                int lastByte = cipher.update(bytesOfFile, numberOfBytes, lastChunk, ModeOfOperating.DECRYPT);

                //записывает зашифрованные данные в новый файл
                fout.write(bytesOfFile, offset, lastByte - offset);
                offset = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
