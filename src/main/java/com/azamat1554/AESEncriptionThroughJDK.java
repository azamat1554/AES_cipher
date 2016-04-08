package com.azamat1554;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Scanner;

/**
 * Реализует шифрованиее AES средствами стандартной библиотеки Java.
 * @author Azamat Abidokov
 */
public class AESEncriptionThroughJDK {
    private static KeyGenerator keygen;

    /**
     * Хранит генерируемый ключ
     */
    private static SecretKey aesKey;
    private static Cipher cipher;

    static {
        try {
            keygen = KeyGenerator.getInstance("AES");
            aesKey = keygen.generateKey();
            cipher = Cipher.getInstance("AES");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        Scanner in = new Scanner(System.in);

        String msg = "";
        while (in.hasNext()) {
            msg = in.nextLine();
            if (msg.equals("exit")) break;
            byte[] encMsg = encrypt(getBytes(msg));

            System.out.println(getString(encMsg));

            System.out.println(getString(decrypt(encMsg)));
        }
        in.close();
    }

    /**
     * Преобразует строку с массив байт.
     *
     * @param str строка, которую нужно представить в виде байтов
     * @return Массив байт
     */
    protected static byte[] getBytes(String str) {
        byte[] bytes = new byte[str.length() * 2];
        char c;
        for (int i = 0; i < str.length(); i++) {
            c = str.charAt(i);
            bytes[i*2] = (byte) ((0xff00 & c) >> 8);
            bytes[i*2 + 1] = (byte) (0x00ff & c);
        }
        return bytes;
    }

    /**
     * Переводит массив байтов в строковое представление.
     *
     * @param bytes массив содержащий байтовое представление символов
     * @return Возращает строку
     */
    protected static String getString(byte[] bytes) {
        char[] c = new char[bytes.length / 2];
        int count = 0;
        for (int i = 0; i < bytes.length; i+=2) {
            c[count++] = (char) ((bytes[i] << 8) | bytes[i + 1]);
        }
        return new String(c);
    }

    /**
     * Шифрует массив байтов, передаваемый в качестве параметра <code>byteofMsg</code>.
     * @param bytesOfMsg массив байтов
     * @return Зашифрованный массив байтов
     * @throws Exception
     */
    public static byte[] encrypt(byte[] bytesOfMsg) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        byte[] temp = cipher.doFinal(bytesOfMsg);
        return temp;
    }

    /**
     * Рассшифровывает массив байтов, передаваемый в качестве параметра <code>byteofMsg</code>.
     * @param bytesOfMsg зашифрованный массив байтов
     * @return Массив байтов
     * @throws Exception
     */
    public static byte[] decrypt(byte[] bytesOfMsg) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, aesKey);
        byte[] temp = cipher.doFinal(bytesOfMsg);
        return temp;
    }
}
