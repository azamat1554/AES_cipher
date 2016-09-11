package com.azamat1554.cipher;

/**
 * Этот класс хранит константы, которые используются для определения
 * параметров шифрования.
 */
public final class AESConst {
    /** Число 32-битных слов, составляющих один блок данных. */
    public static final int NB = 4;

    /** Число 32-битных слов в ключе, в данном случае 128-битный ключ. */
    public static final int NK = 4;

    /** Количество раундов. */
    public static final int NR = 10;

    /** Размер одного блока данных. */
    public static final int BLOCK_SIZE = 4 * NB;
}
