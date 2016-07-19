package com.azamat1554.mode;

import com.azamat1554.AESConst;
import com.azamat1554.CipherAES;
import com.azamat1554.CipherBlockAES;
import com.azamat1554.ModeOfOperating;

/**
 * Общий класс для всех методов шифрования,
 * предположительно, будет использован для
 * того, чтобы в операторе swich использовать
 * одну ссылку на все подклассы. говнокод-костыль.
 */
public interface BlockCipher { //todo возможно, имеет смысл переделать в абстрактный класс
    //шифрует данные
    //byte[] encrypt(byte[] bytesOfMsg, int countOfByte, boolean lastChunk);

    //расшифровывает данные
    //byte[] decrypt(byte[] bytesOfMsg, int countOfByte, boolean lastChunk);


    int update(byte[] streamOfBytes, int endOfArray, boolean last, ModeOfOperating mode);

    //возвращает индекс последнего байта данных
    //int getIndexOfLastByte();
}