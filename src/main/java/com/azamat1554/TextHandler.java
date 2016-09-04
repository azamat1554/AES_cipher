package com.azamat1554;

import com.azamat1554.mode.BlockCipher;
import com.azamat1554.mode.Mode;

import javax.naming.LimitExceededException;
import java.nio.charset.Charset;
import java.util.Base64;

/**
 * Класс для обработки текста
 */
public class TextHandler extends Thread {
    //ограничение на кол-во байт данных
    private final int MAX_LENGTH = 16_384;

    //хранит байты файла
    private byte[] bytesOfText;

    private int offset;


    public synchronized void init(String text, Mode mode, ModeOf modeOf) {
//        this.files = files;
//        this.indexes = indexes;
//        this.mode = mode;
//        this.modeOf = modeOf;
//
//        total = getTotalSize();
//
//        //инициализирует класс в зависимости от режима
//        cipher = BlockCipher.getCipher(mode);
//
//        //Смещение нужно для вектора инициализации (IV)
//        if (mode != Mode.ECB)
//            offset = AESConst.BLOCK_SIZE;
    }

    public String encrypt(String text, Mode mode) throws LimitExceededException {
        if ("".equals(text)) return null; //если текстовое поле пусто, ничего не делать

        //узнать про ByteBuffer
        byte[] src = text.getBytes(Charset.forName("utf-8"));

        offset = mode == Mode.ECB ? 0 : AESConst.BLOCK_SIZE;
        bytesOfText = new byte[src.length + offset + getSizeOfPadding(src.length)];
        if (bytesOfText.length > MAX_LENGTH) throw new LimitExceededException();

        System.arraycopy(src, 0, bytesOfText, offset, src.length);

        try {
            BlockCipher.getCipher(mode).update(bytesOfText, src.length + offset, true, ModeOf.ENCRYPTION);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //байты кодируются символами системы счисления Base64
        return Base64.getEncoder().encodeToString(bytesOfText);
    }

    public String decrypt(String text, Mode mode) throws LimitExceededException {
        if ("".equals(text)) return null; //если текстовое поле пусто, ничего не делать

        bytesOfText = Base64.getDecoder().decode(text.getBytes(Charset.forName("utf-8")));
        if (bytesOfText.length > MAX_LENGTH) throw new LimitExceededException();

        //если длина массива не кратна размеру блока, то это недействительная строка
        if (bytesOfText.length % AESConst.BLOCK_SIZE != 0) throw new IllegalArgumentException();

        int end = 0;
        try {
            end = BlockCipher.getCipher(mode).update(bytesOfText, bytesOfText.length, true, ModeOf.DECRYPTION);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        offset = mode == Mode.ECB ? 0 : AESConst.BLOCK_SIZE;
        return new String(bytesOfText, offset, end - offset, Charset.forName("utf-8"));
    }

    //Возвращает количество байтов, необходимых для дополнения
    private int getSizeOfPadding(int length) {
        return AESConst.BLOCK_SIZE - length % AESConst.BLOCK_SIZE;
    }
}
