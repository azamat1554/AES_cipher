package com.azamat1554;

import com.azamat1554.mode.Mode;

import java.util.Arrays;

/**
 * Класс для шифрования и расшифровки файлов
 */
public class AESDemoFile {
    private final FileHandler cipher = new FileHandler();

    public static void main(String[] args) {
        long start;
        AESDemoFile aesDemoFile = new AESDemoFile();

        //ключи шифрования/расшифрования
        final char[][] key = {{'l', 'o', 'l'}, {'l', 'a', 'u', 'g', 'h'}};

        //проверка работы с разными ключами во время одной сессии
        for (int i = 0; i < key.length - 1; i++) {
            start = System.currentTimeMillis();
            aesDemoFile.encrypt(key[i]);
            System.out.println((System.currentTimeMillis() - start) / 1000);

            start = System.currentTimeMillis();
            aesDemoFile.decrypt(key[i]);
            System.out.println((System.currentTimeMillis() - start) / 1000);
        }
    }

    public void encrypt(char[] key) {
        //String fileName = "/home/azamat/Desktop/file.format";
        String fileName = "/home/azamat/Desktop/test/";

        //Проверка на то, что файл уже был зашифрован.
        if (fileName.endsWith(".encrypted")) { //todo тогда показать окно для выбора дальнейших действий
            System.out.println("File have already been encrypted.");
            return;
        }


        cipher.encrypt(fileName, Arrays.copyOf(key, key.length), Mode.ECB);
    }

    public void decrypt(char[] key) {
        //String fileName = "/home/azamat/Desktop/file.format.encrypted";
        String fileName = "/home/azamat/Desktop/test/";

        //Проверка на то, что файл ещё не был зашифрован.
//        if (!fileName.endsWith(".encrypted")) { //todo тогда показать окно для выбора дальнейших действий
//            System.out.println("File haven't been encrypted yet");
//            return;
//        }

        cipher.decrypt(fileName, Arrays.copyOf(key, key.length), Mode.ECB);
    }
}
