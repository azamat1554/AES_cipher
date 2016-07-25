package com.azamat1554;

import com.azamat1554.mode.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.SecureRandom;
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
    private final byte[] bytesOfMsg = new byte[SIZE];

    private BlockCipher cipher;

    //кол-во считываемых байт
    private int numberOfByte = 0;

    //указывает на то, последняя это часть файла или нет
    private boolean lastChunk;

    //шифрует файл на который указывает параметр inputFlow
//    public void encrypt(String fileName, char[] secretKey, Mode mode) {
//        try (FileInputStream fin = new FileInputStream(fileName);
//             FileOutputStream fout = new FileOutputStream(fileName + ".encrypted")) {
//
//            System.out.println("\nEncrypt file.");
//
//            //инициализирует класс в зависимости от режима
//            cipher = getCipherMode(mode);
//
//            //хранит хэш переменной secretKey, чтобы не допустить утечки пароля
//            CipherBlockAES.Key.setKey(MessageDigest.getInstance("MD5").digest(toByte(secretKey)));
//
//            //обнулить массив с ключем
//            Arrays.fill(secretKey, '\u0000');
//
//            //цикл чтения из файла
//            do {
//                lastChunk = true;
//                if ((numberOfByte = fin.available()) > SIZE - 4 * NB) {
//                    numberOfByte = SIZE - 4 * NB; //один блок в запасе для дополнения
//                    lastChunk = false;
//                }
//
//                //считать байты из файла в массив с нулевого индекса до numberOfByte
//                fin.read(bytesOfMsg, 0, numberOfByte);
//
//                //long start = System.currentTimeMillis();
//                byte[] cipherText = cipher.encrypt(bytesOfMsg, numberOfByte, lastChunk);
//                //System.out.println((System.currentTimeMillis() - start) / 1000);
//
//                //записывает зашифрованные данные в новый файл
//                fout.write(cipherText, 0, cipher.getIndexOfLastByte());
//            } while (fin.available() > 0);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    public void encrypt(String fileName, char[] secretKey, Mode mode) {
        try (FileInputStream fin = new FileInputStream(fileName + "file1");
             FileOutputStream fout = new FileOutputStream(fileName + "file2")) {

            System.out.println("\nEncrypt file.");

            //хранит хэш переменной secretKey, чтобы не допустить утечки пароля
            CipherBlockAES.Key.setKey(MessageDigest.getInstance("MD5").digest(toByte(secretKey)));

            //обнулить массив с ключем
            Arrays.fill(secretKey, '\u0000');

            int offset = 0;

            //инициализирует класс в зависимости от режима
            cipher = getCipherMode(mode);
            if (mode != Mode.ECB) {
                setIV(bytesOfMsg);
                offset = AESConst.BLOCK_SIZE;
            }

            //цикл чтения из файла
            do {
                lastChunk = true;
                if (fin.available() > (numberOfByte = getSizeOfChunk(fin, mode)))
                    lastChunk = false;

                //считать байты из файла в массив с нулевого индекса до numberOfByte
                //// TODO: 7/24/16 все куски кроме первого нужно считывать без смещения.
                fin.read(bytesOfMsg, offset, numberOfByte);

                //numberOfByte = readFile(fin, mode);

                int lastByte = cipher.update(bytesOfMsg, numberOfByte + offset, lastChunk, ModeOfOperating.ENCRYPT);
                offset = 0;

                //записывает зашифрованные данные в новый файл
                fout.write(bytesOfMsg, 0, lastByte);
            } while (fin.available() > 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //расшифровывает файл на который указывает параметр inputFlow
//    public void decrypt(String fileName, char[] secretKey, Mode mode) {
//        try (FileInputStream fin = new FileInputStream(fileName);
//             FileOutputStream fout = new FileOutputStream(fileName.substring(0, fileName.lastIndexOf('.')))) {
//
//            System.out.println("\nDecrypt file.");
//
//            //инициализирует класс в зависимости от режима
//            cipher = getCipherMode(mode);
//
//            //установка ключа
//            CipherBlockAES.Key.setKey(MessageDigest.getInstance("MD5").digest(toByte(secretKey)));
//
//            //обнулить массив с ключем
//            Arrays.fill(secretKey, '\u0000');
//
//            //цикл чтения из файла
//            do {
//                lastChunk = true;
//                if ((numberOfByte = fin.available()) > SIZE - 4 * NB) {
//                    numberOfByte = SIZE - 4 * NB; //один блок в запасе для дополнения
//                    lastChunk = false;
//                }
//
//                //считать байты из файла в массив с нулевого индекса до SIZE
//                fin.read(bytesOfMsg, 0, numberOfByte);
//
//                //long start = System.currentTimeMillis();
//                byte[] cipherText = cipher.decrypt(bytesOfMsg, numberOfByte, lastChunk);
//                //System.out.println((System.currentTimeMillis() - start) / 1000);
//
//                //записывает зашифрованные данные в новый файл
//                fout.write(cipherText, 0, cipher.getIndexOfLastByte());
//            } while (fin.available() > 0);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public void decrypt(String fileName, char[] secretKey, Mode mode) {
        try (FileInputStream fin = new FileInputStream(fileName + "file2");
             FileOutputStream fout = new FileOutputStream(fileName + "file3")) {

            System.out.println("\nDecrypt file.");

            //установка ключа
            CipherBlockAES.Key.setKey(MessageDigest.getInstance("MD5").digest(toByte(secretKey)));

            //обнулить массив с ключем
            Arrays.fill(secretKey, '\u0000');

            //инициализирует класс в зависимости от режима
            cipher = getCipherMode(mode);

            //int offset = 0;
            //if (mode != Mode.ECB) offset = AESConst.BLOCK_SIZE;

            //цикл чтения из файла
            do {
                lastChunk = true;
                if (fin.available() > (numberOfByte = getSizeOfChunk(fin, mode)))
                    lastChunk = false;

                //считать байты из файла в массив с нулевого индекса до numberOfByte
                fin.read(bytesOfMsg, 0, numberOfByte);

                int lastByte = cipher.update(bytesOfMsg, numberOfByte, lastChunk, ModeOfOperating.DECRYPT);

                //записывает зашифрованные данные в новый файл
                fout.write(bytesOfMsg, 0, lastByte);
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
            case ECB:
                if (numberOfByte > (temp = SIZE - AESConst.BLOCK_SIZE))
                    numberOfByte = temp; //один блок в запасе для дополнения
                break;
            case CBC:
                if (numberOfByte > (temp = SIZE - 2 * AESConst.BLOCK_SIZE))
                    numberOfByte = temp;
        }
        return numberOfByte;
    }

    //считывает байты из файла
    private int readFile(FileInputStream fin, Mode mode) throws IOException {
        int length = numberOfByte;
        switch (mode) {
            case ECB:
                fin.read(bytesOfMsg, 0, getSizeOfChunk(fin, mode));
                break;
            case CBC:
                length = numberOfByte + AESConst.BLOCK_SIZE;
                fin.read(bytesOfMsg, AESConst.BLOCK_SIZE, getSizeOfChunk(fin, mode));
        }
        return length;
    }

    //устанавливает вектор инициализации в первый блок данных
    protected void setIV(byte[] data) {
        byte[] iv = new SecureRandom().generateSeed(AESConst.BLOCK_SIZE);

        for (int i = 0; i < AESConst.BLOCK_SIZE; i++) {
            data[i] = iv[i];
        }
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
