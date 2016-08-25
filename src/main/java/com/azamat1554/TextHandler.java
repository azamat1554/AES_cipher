package com.azamat1554;

import com.azamat1554.mode.BlockCipher;
import com.azamat1554.mode.Mode;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Base64;

/**
 * Сразу создать массив нужного размера
 */
class TextHandler {
    private final int SIZE = 16_384;

    //хранит байты файла
    private byte[] bytesOfText; // = new byte[SIZE];

    private int offset;

    public String encrypt(String text, Mode mode) {
        if ("".equals(text)) return null; //если текстовое поле пусто, ничего не делать

        byte[] src;

        //узнать про ByteBuffer
        src = text.getBytes(Charset.forName("utf-8"));

        offset = mode == Mode.ECB ? 0 : AESConst.BLOCK_SIZE;
        bytesOfText = new byte[src.length + offset + getSizeOfPadding(src.length)];

        System.arraycopy(src, 0, bytesOfText, offset, src.length);

        BlockCipher.getCipher(mode).update(bytesOfText, offset, src.length + offset, true, ModeOfOperating.ENCRYPT);

        //байты кодируются символами системы счисления Base64
        return Base64.getEncoder().encodeToString(bytesOfText);
    }

    public String decrypt(String text, Mode mode) {
        if ("".equals(text)) return null; //если текстовое поле пусто, ничего не делать

        bytesOfText = Base64.getDecoder().decode(text.getBytes(Charset.forName("utf-8")));

        if (bytesOfText.length % AESConst.BLOCK_SIZE != 0) throw new IllegalArgumentException();

        offset = mode == Mode.ECB ? 0 : AESConst.BLOCK_SIZE;

        int end = BlockCipher.getCipher(mode).update(bytesOfText, offset, bytesOfText.length, true, ModeOfOperating.DECRYPT);

        return new String(bytesOfText, offset, end - offset, Charset.forName("utf-8"));
    }

    //возвращает индекс конца полезных данных, и копирует данные из src в д
    private void copy(byte[] src, byte[] dest) {
        System.arraycopy(src, 0, dest, offset, src.length);
    }

    //Возвращает количество байтов, необходимых для дополнения
    private int getSizeOfPadding(int length) {
        return AESConst.BLOCK_SIZE - length % AESConst.BLOCK_SIZE;
    }
}
