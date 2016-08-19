package com.azamat1554.mode;

import com.azamat1554.CipherAES;
import com.azamat1554.ModeOfOperating;

import java.util.concurrent.RecursiveAction;

/**
 * Class implements concurrent encryption of ECB mode.
 */
public class ECB extends RecursiveAction implements BlockCipher {
    private static final int THRESHOLD = 2048;

    //массив с данными
    private static byte[] data;

    private static int lastByte;

    private static ModeOfOperating mode;

    /*класс осуществляющий разбиение на блоки для шифрования/расшифрования,
      а затем объединение блоков обратно*/
    private CipherAES cipher;

    //указывает, последний это кусок файла или нет
    private boolean lastChunk;

    //from - индекс начала диапазона, to - индекс конца диапазона
    private int from, to;

    //конструктор по умолчанию
    public ECB() {}

    private ECB(byte[] d, int s, int e, boolean last) {
        data = d;
        from = s;
        to = e;
        lastChunk = last;
    }

    /**
     * Выполняет инициализацию объекта, замем запускает задачу на рекурсивное выполнение.
     *
     * @param streamOfBytes Массив хранящий данные, которые нужно преобразовать
     * @param endOfArray    Индекс конца полезных данных в массиве до преобразований
     * @param last          Указывает, последний это кусок файла или нет
     * @param mode          Хранит текущий режим работы
     * @return Индекс на конец полезных данных после преобразований
     */
    public int update(byte[] streamOfBytes, int endOfArray, boolean last, ModeOfOperating mode) {
        ECB.mode = mode;
        lastByte = endOfArray;

        data = streamOfBytes;
        from = 0;
        to = endOfArray;
        lastChunk = last;
        this.invoke();

        return lastByte;
    }

    /**
     * В этом методе выполняется разделение больших данных до
     * указанного порога, после чего выполняется параллельная обработка
     * этих данных по частям.
     */
    @Override
    protected void compute() {
        if ((to - from) <= THRESHOLD) {
            cipher = new CipherAES();
            cipher.init(data, from, to, lastChunk, mode);
            int index = cipher.makeTransform();

            //если полседний блок будет обработан не в последнюю очередь
            if (lastChunk) lastByte = index;
        } else {
            int bound;
            if (!lastChunk) {  //если это не последний кусок файла, тогда делим на два
                bound = (from + to) / 2;

                invokeAll(new ECB(data, from, bound, false),
                        new ECB(data, bound, to, false));
            } else { //иначе, находим максимальную степень двойки, которая будет разделителем
                bound = getUpperBound(from, to);

                invokeAll(new ECB(data, from, bound, false),
                        new ECB(data, bound, to, true));
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
                amount = from + powerOfTwo[i];
                break;
            }
        }
        return amount;
    }
}
