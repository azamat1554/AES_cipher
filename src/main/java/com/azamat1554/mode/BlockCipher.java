package com.azamat1554.mode;

import com.azamat1554.AESConst;
import com.azamat1554.ModeOfOperating;

import java.security.SecureRandom;

/**
 * Интерфейс, который должен быть реализован
 * всеми классами выполняющими различные режими
 * шифрования.
 */

//// TODO: 8/19/16 сделать анонимный класс и перенести методы из MainWindow 
public interface BlockCipher {

    int update(byte[] streamOfBytes, int endOfArray, boolean last, ModeOfOperating mode);
}