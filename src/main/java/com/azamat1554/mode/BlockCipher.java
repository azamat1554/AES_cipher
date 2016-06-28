package com.azamat1554.mode;

import com.azamat1554.CipherAES;

/**
 * Общий класс для всех методов шифрования,
 * предположительно, будет использован для
 * того, чтобы в операторе swich использовать
 * одну ссылку на все подклассы. говнокод-костыль.
 */
public abstract class BlockCipher { //todo возможно, имеет смысл переделать в интерфейс
    protected CipherAES aes = new CipherAES();

    public void setKey(byte[] secretKey) {
        aes.setKey(secretKey);
    }

    //возвращает значение индекса, на конец полезных данных в массиве байтов
    public int getSizeOfArray() {
        return aes.getSizeOfArray();
    }

    //шифрует данные
    public abstract byte[] encrypt(byte[] bytesOfMsg, int countOfByte, boolean lastChunk);

    //расшифровывает данные
    public abstract byte[] decrypt(byte[] bytesOfMsg, int countOfByte, boolean lastChunk);
}