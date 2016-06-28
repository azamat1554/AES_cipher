package com.azamat1554;

import com.azamat1554.mode.Mode;

/**
 * Класс для шифрования и расшифровки файлов
 */
public class AESDemoFile {
    private final FileHandler cipher = new FileHandler();

    public static void main(String[] args) {
        //long start = System.currentTimeMillis();
        AESDemoFile aesDemoFile = new AESDemoFile();
        //aesDemoFile.encrypt();
        aesDemoFile.decrypt();

        //System.out.println((System.currentTimeMillis() - start) / 1000);
    }

    public void encrypt() {
        //использовать один и тот же массив для ключа,
        //чтобы не оставлять данные в хипе.
        //После использования заполнить нулями
        final char[] key = {'l', 'o', 'l'};

        String fileName = "/home/azamat/Desktop/file.format";

        //Проверка на то, что файл уже был зашифрован.
        if (fileName.endsWith(".encrypted")) { //todo тогда показать окно для выбора дальнейших действий
            System.out.println("File have already been encrypted.");
            return;
        }

        cipher.encrypt(fileName, key, Mode.ECB);
    }

    public void decrypt() {
        char[] key = {'l', 'o', 'l'};
        String fileName = "/home/azamat/Desktop/file.format.encrypted";

        //Проверка на то, что файл ещё не был зашифрован.
        if (!fileName.endsWith(".encrypted")) { //todo тогда показать окно для выбора дальнейших действий
            System.out.println("File haven't been encrypted yet");
            return;
        }

        cipher.decrypt(fileName, key, Mode.ECB);
    }
}
