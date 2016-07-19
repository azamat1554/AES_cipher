package com.azamat1554.mode;

import com.azamat1554.CipherAES;
import com.azamat1554.ModeOfOperating;

import java.util.concurrent.RecursiveAction;

/**
 * Class implements concurrent encryption of ECB mode.
 */
public class ECB extends RecursiveAction implements BlockCipher {
//    private ModeOfOperating mode;
//
//    private int lastByte;
//
//    /**
//     * Шифрует входную последовательнойть байт и возвращает рузультат.
//     *
//     * @param streamOfBytes Исходный (открытый) массив данных
//     * @return Зашифрованный блок данных
//     */
//    public byte[] encrypt(byte[] streamOfBytes, int endOfArray, boolean lastChunk) {
//        mode = ModeOfOperating.ENCRYPT;
//
//        //придумать нормальное решение
//        if (!lastChunk) lastByte = endOfArray;
//
//        ParallelExecution pe = new ParallelExecution(streamOfBytes, 0, endOfArray, lastChunk);
//        pe.invoke();
//
//        return streamOfBytes;
//    }
//
//    /**
//     * Расшифровывает входную последовательнойть байт и возвращает рузультат.
//     *
//     * @param streamOfBytes Массив хранящий зашифрованные байты, в методе <code>encrypt()</code>
//     * @return Расшифрованный массив данных
//     */
//    public byte[] decrypt(byte[] streamOfBytes, int endOfArray, boolean lastChunk) {
//        mode = ModeOfOperating.DECRYPT;
//
//        //придумать нормальное решение
//        if (!lastChunk) lastByte = endOfArray;
//
//        ParallelExecution pe = new ParallelExecution(streamOfBytes, 0, endOfArray, lastChunk);
//        pe.invoke();
//
//        return streamOfBytes;
//    }
//
//    public int getIndexOfLastByte() {
//        //System.out.println("\n lastByte: " + lastByte);
//        return lastByte;
//
//    }

    //private class ParallelExecution extends RecursiveAction {

    static final int THRESHOLD = 65536;

    private static int lastByte;

    private static ModeOfOperating mode;

    /*класс осуществляющий разбиение на блоки для шифрования/расшифрования,
      а затем обратно объединение */
    CipherAES cipher;

    //последний кусок файла?
    boolean lastChunk;

    byte[] data;

    int start, end;

    //конструктор по умолчанию
    public ECB() {
        cipher = new CipherAES();
    }

    private ECB(byte[] d, int s, int e, boolean last) {
        cipher = new CipherAES();

        data = d;
        start = s;
        end = e;
        lastChunk = last;
    }

    public int update(byte[] streamOfBytes, int endOfArray, boolean last, ModeOfOperating mode) {
        this.mode = mode;

        data = streamOfBytes;
        start = 0;
        end = endOfArray;
        lastByte = endOfArray;
        lastChunk = last;
        this.invoke();

        return lastByte;
    }

    @Override
    protected void compute() {
        if ((end - start) <= THRESHOLD) {
            cipher.init(start, end, lastChunk, mode);
            int index = cipher.makeTransform(data);

            //если полседний блок будет обработан не в последнюю очередь
            if (lastChunk)
                lastByte = index;

            //System.out.println("compute method, s: " + start + " e: " + end);
        } else {
            int bound;
            if (!lastChunk) {  //если это не последний кусок файла, тогда делим на два
                bound = (start + end) / 2;

                invokeAll(new ECB(data, start, bound, false),
                        new ECB(data, bound, end, false));
            } else { //иначе, находим максимальную степень двойки, которая будет разделителем
                bound = getUpperBound(start, end);

                invokeAll(new ECB(data, start, bound, false),
                        new ECB(data, bound, end, true));
            }
        }
    }

    //возвращает верхнюю границу, так чтобы размер получившегося интервала был кратен 16
    private int getUpperBound(int s, int e) {
        int amount = e - s;

        int[] powerOfTwo = {1024, 2048, 4096, 8192, 16384, 32768, 65536, 131072, 262144,
                524288, 1048576, 2097152, 4194304, 8388608, 16777216, 33554432, 67108864};

        for (int i = powerOfTwo.length - 1; i >= 0; i--) {
            if (powerOfTwo[i] < amount) {
                amount = start + powerOfTwo[i];
                break;
            }
        }
        return amount;
    }
    // }
}
