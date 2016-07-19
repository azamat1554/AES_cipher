package com.azamat1554;

import com.azamat1554.mode.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.util.Arrays;

import static com.azamat1554.AESConst.NB;

/**
 * Обработчик файлов, если файл больше 128 МБ, то делит его на части и шифрует.
 * Результат записывается в новый файл, который имеет тоже имя с добавлением
 * <code>encrypted</code> в конце. При дешифровке операции выполняются в обратном порядке.
 */
public class FileHandler {
    //размер массива
    private final int size = 134_217_744;

    //хранит байты файла
    private final byte[] bytesOfMsg = new byte[size];

    private BlockCipher cipher;

    //private CipherAES cipher = new CipherAES();

    //кол-во считываемых байт
    private int countOfByte = 0;

    //указывает на то, последняя это часть файла или нет
    private boolean lastChunk = true;

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
//                if ((countOfByte = fin.available()) > size - 4 * NB) {
//                    countOfByte = size - 4 * NB; //один блок в запасе для дополнения
//                    lastChunk = false;
//                }
//
//                //считать байты из файла в массив с нулевого индекса до countOfByte
//                fin.read(bytesOfMsg, 0, countOfByte);
//
//                //long start = System.currentTimeMillis();
//                byte[] cipherText = cipher.encrypt(bytesOfMsg, countOfByte, lastChunk);
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

            //инициализирует класс в зависимости от режима
            cipher = getCipherMode(mode);

            //хранит хэш переменной secretKey, чтобы не допустить утечки пароля
            CipherBlockAES.Key.setKey(MessageDigest.getInstance("MD5").digest(toByte(secretKey)));

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
                //byte[] cipherText = cipher.encrypt(bytesOfMsg, countOfByte, lastChunk);
                int lastByte = cipher.update(bytesOfMsg, countOfByte, lastChunk, ModeOfOperating.ENCRYPT);
                //System.out.println((System.currentTimeMillis() - start) / 1000);

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
//                if ((countOfByte = fin.available()) > size - 4 * NB) {
//                    countOfByte = size - 4 * NB; //один блок в запасе для дополнения
//                    lastChunk = false;
//                }
//
//                //считать байты из файла в массив с нулевого индекса до size
//                fin.read(bytesOfMsg, 0, countOfByte);
//
//                //long start = System.currentTimeMillis();
//                byte[] cipherText = cipher.decrypt(bytesOfMsg, countOfByte, lastChunk);
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

            //инициализирует класс в зависимости от режима
            cipher = getCipherMode(mode);

            //установка ключа
            CipherBlockAES.Key.setKey(MessageDigest.getInstance("MD5").digest(toByte(secretKey)));

            //обнулить массив с ключем
            Arrays.fill(secretKey, '\u0000');

            //цикл чтения из файла
            do {
                lastChunk = true;
                if ((countOfByte = fin.available()) > size - 4 * NB) {
                    countOfByte = size - 4 * NB; //один блок в запасе для дополнения
                    lastChunk = false;
                }

                //считать байты из файла в массив с нулевого индекса до size
                fin.read(bytesOfMsg, 0, countOfByte);

                //long start = System.currentTimeMillis();
                //byte[] cipherText = cipher.decrypt(bytesOfMsg, countOfByte, lastChunk);
                int lastByte = cipher.update(bytesOfMsg, countOfByte, lastChunk, ModeOfOperating.DECRYPT);
                //System.out.println((System.currentTimeMillis() - start) / 1000);

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

    private byte[] toByte(char[] input) {
        byte[] output = new byte[input.length * 2];
        for (int i = 0; i < input.length; i++) {
            output[i * 2] = (byte) (input[i] >> 8);
            output[i * 2 + 1] = (byte) input[i];
        }
        return output;
    }
}
