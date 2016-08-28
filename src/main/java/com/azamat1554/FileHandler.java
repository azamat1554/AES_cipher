package com.azamat1554;

import com.azamat1554.mode.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Обработчик файлов, если файл больше 128 МБ, то делит его на части и шифрует.
 * Результат записывается в новый файл, который имеет тоже имя с добавлением
 * <code>encrypted</code> в конце. При дешифровке операции выполняются в обратном порядке.
 */

// TODO: 8/28/16 Так и хочется избавить от смещения, чтобы оно выполнялось в классе CBC.
public class FileHandler {
    //размер массива без дополнительных блоков
    private final int SIZE = 134_217_728;

    //хранит байты файла. Два блока в запасе, один для вектора инициализации, второй для дополнения
    private final byte[] bytesOfFile = new byte[SIZE + 2 * AESConst.BLOCK_SIZE];

    private BlockCipher cipher;

    //кол-во считываемых байт
    private int numberOfBytes = 0;

    //указывает на то, последняя это часть файла или нет
    private boolean lastChunk;

    private int offset;

    private void init(Mode mode) {
        //инициализирует класс в зависимости от режима
        cipher = BlockCipher.getCipher(mode);

        //Смещение нужно для вектора инициализации (IV)
        if (mode != Mode.ECB)
            offset = AESConst.BLOCK_SIZE;
    }

//    public void make(List<File> files, Mode mode, ModeOf modeOf) {
//        //инициализирует класс в зависимости от режима
//        cipher = BlockCipher.getCipher(mode);
//
//        //Смещение нужно для вектора инициализации (IV)
//        if (mode != Mode.ECB)
//            offset = AESConst.BLOCK_SIZE;
//
//        if (modeOf == ModeOf.ENCRYPTION) encrypt(files);
//        else decrypt(files);
//    }

    public File encrypt(File fileSrc, Mode mode) {
        System.out.println("\nEncrypt file.");
        init(mode);

        File fileDest = new File(fileSrc.getAbsolutePath() + ".encrypted");
        //for (File file : files) {
        try (FileInputStream fin = new FileInputStream(fileSrc);
             FileOutputStream fout = new FileOutputStream(fileDest)) {

            int restBytes;
            //цикл чтения из файла
            while ((restBytes = fin.available()) > 0) {
                lastChunk = restBytes <= SIZE;
                //считать из файла указанное кол-во байт или сколько осталось, и вернуть прочитанное количество байт.
                numberOfBytes = fin.read(bytesOfFile, offset, SIZE);

                int lastByte = cipher.update(bytesOfFile, numberOfBytes + offset, lastChunk, ModeOf.ENCRYPTION);
                offset = 0;

                //записывает зашифрованные данные в новый файл
                fout.write(bytesOfFile, 0, lastByte);
            }

            fileSrc.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //}
        return fileDest;
    }

    public File decrypt(File fileSrc, Mode mode) {
        init(mode);
        String path = fileSrc.getAbsolutePath();
        File fileDest = new File(path.substring(0, path.lastIndexOf('.')));

        //for (File file : file) {
        try (FileInputStream fin = new FileInputStream(fileSrc);
             FileOutputStream fout = new FileOutputStream(fileDest)) {

            int restBytes;
            //цикл чтения из файла
            while ((restBytes = fin.available()) > 0) {
                lastChunk = restBytes <= SIZE;
                //считать из файла указанное кол-во байт или сколько осталось, и вернуть прочитанное количество байт.
                numberOfBytes = fin.read(bytesOfFile, 0, SIZE);

                int lastByte = cipher.update(bytesOfFile, numberOfBytes, lastChunk, ModeOf.DECRYPTION);

                //записывает зашифрованные данные в новый файл
                fout.write(bytesOfFile, offset, lastByte - offset);
                offset = 0;
            }

            fileSrc.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //}
        return fileDest;
    }
}
