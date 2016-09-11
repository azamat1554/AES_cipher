package com.azamat1554.handlers;

import com.azamat1554.cipher.AESConst;
import com.azamat1554.cipher.ModeOf;
import com.azamat1554.cipher.modes.BlockCipher;
import com.azamat1554.cipher.modes.CipherMode;

import java.nio.charset.Charset;
import java.util.Base64;

/**
 * Выполняет преобразование текста, введённого пользователем.
 * После шифрования, байты переводятся в систему счисления Base64.
 */
public class TextHandler extends Thread {
    /* Хранит байтовое представление строки. */
    private byte[] bytesOfText;

    /* Задает смещение для вектора инициализации */
    private int offset;

    /**
     * Шифрует полученную строку.
     *
     * @param text       Текст который требуется зашифровать.
     * @param cipherMode Режим работы блочного шифра.
     * @return Зашифрованный текст.
     */
    public String encrypt(String text, CipherMode cipherMode) {
        //если текстовое поле пусто, ничего не делать
        if ("".equals(text)) return null;

        //хранит байтовое представление исходной строки
        byte[] src = text.getBytes(Charset.forName("utf-8"));

        offset = cipherMode == CipherMode.ECB ? 0 : AESConst.BLOCK_SIZE;
        bytesOfText = new byte[src.length + offset + getSizeOfPadding(src.length)];

        System.arraycopy(src, 0, bytesOfText, offset, src.length);

        BlockCipher.setMode(ModeOf.ENCRYPTION);
        try {
            BlockCipher.getCipher(cipherMode).update(bytesOfText, src.length + offset, true);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }

        //байты кодируются символами системы счисления Base64
        return Base64.getEncoder().encodeToString(bytesOfText);
    }

    /**
     * Расшифрует полученную строку.
     *
     * @param text       Текст который требуется расшифровать.
     * @param cipherMode Режим работы блочного шифра.
     * @return Расшифрованный текст.
     */
    public String decrypt(String text, CipherMode cipherMode) {
        //если текстовое поле пусто, ничего не делать
        if ("".equals(text)) return null;

        //обратное преобразование из Base64 в байты
        bytesOfText = Base64.getDecoder().decode(text.getBytes(Charset.forName("utf-8")));

        //если длина массива не кратна размеру блока, то это недействительная строка
        if (bytesOfText.length % AESConst.BLOCK_SIZE != 0) throw new IllegalArgumentException();

        BlockCipher.setMode(ModeOf.DECRYPTION);

        int end = 0;
        try {
            end = BlockCipher.getCipher(cipherMode).update(bytesOfText, bytesOfText.length, true);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }

        offset = cipherMode == CipherMode.ECB ? 0 : AESConst.BLOCK_SIZE;
        return new String(bytesOfText, offset, end - offset, Charset.forName("utf-8"));
    }

    /**
     * Возвращает количество байтов, необходимых для дополнения.
     *
     * @param length Длина массива, который нужно дополнить.
     */
    private int getSizeOfPadding(int length) {
        return AESConst.BLOCK_SIZE - length % AESConst.BLOCK_SIZE;
    }
}
