package com.azamat1554;

import com.azamat1554.mode.BlockCipher;
import com.azamat1554.mode.Mode;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * Сразу создать массив нужного размера
 */
class TextHandler {
    private final int SIZE = 16_384;

    //хранит байты файла
    private byte[] bytesOfText = new byte[SIZE];

    private int offset;

    public String encrypt(String text, BlockCipher cipher, Mode mode, byte[] iv) {
        if ("".equals(text)) return null; //если текстовое поле пусто, ничего не делать

        byte[] src = null;
        try {
            src = text.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        offset = mode == Mode.ECB ? 0 : AESConst.BLOCK_SIZE;
        bytesOfText = new byte[src.length + offset + getSizeOfPadding(src.length)];

        copy(src, bytesOfText, iv);
        cipher.update(bytesOfText, src.length + offset, true, ModeOfOperating.ENCRYPT);

        //байты кодируются символами системы счисления Base64
        return Base64.getEncoder().encodeToString(bytesOfText);
    }

    public String decrypt(String text, BlockCipher cipher) {
        try {
            bytesOfText = Base64.getDecoder().decode(text.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (bytesOfText.length % AESConst.BLOCK_SIZE != 0) throw new IllegalArgumentException();

        int end = cipher.update(bytesOfText, bytesOfText.length, true, ModeOfOperating.DECRYPT);

        return new String(bytesOfText, 0, end);
    }

    //возвращает индекс конца полезных данных, и копирует данные из src в д
    private void copy(byte[] src, byte[] dest, byte[] iv) {
        for (int i = 0; i < offset; i++)
            dest[i] = iv[i];

        System.arraycopy(src, 0, dest, offset, src.length);
    }

    //Возвращает количество байтов, необходимых для дополнения
    private int getSizeOfPadding(int length) {
        return AESConst.BLOCK_SIZE - length % AESConst.BLOCK_SIZE;
    }
}
