package com.azamat1554.mode;

import com.azamat1554.CipherChunk;
import com.azamat1554.ModeOf;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * Class implements concurrent encryption of ECB mode.
 */
public class ECB extends BlockCipher {
    //массив с данными
    private static byte[] data;

    private static int lastByte;

    private static ModeOf mode;

    @Override
    public int update(byte[] streamOfBytes, int endOfData, boolean last, ModeOf mode) throws InterruptedException {
        data = streamOfBytes;
        ECB.mode = mode;
        lastByte = endOfData;

        new ForkJoinPool().invoke(new ForkJoinExecution(0, endOfData, last));

        if (Thread.currentThread().isInterrupted()) throw new InterruptedException();

        return lastByte;
    }

    private class ForkJoinExecution extends RecursiveAction {
        private static final int THRESHOLD = 2048;

        //указывает, последний это кусок файла или нет
        private boolean lastChunk;

        //from - индекс начала диапазона, to - индекс конца диапазона
        private int from, to;

        private ForkJoinExecution(int s, int e, boolean last) {
            from = s;
            to = e;
            lastChunk = last;
        }

        /**
         * В этом методе выполняется разделение больших данных до
         * указанного порога, после чего выполняется параллельная обработка
         * этих данных по частям.
         */
        @Override
        protected void compute() {
            if ((to - from) <= THRESHOLD) {
                /*класс осуществляющий разбиение на блоки для шифрования/расшифрования,
          а затем объединение блоков обратно*/
                CipherChunk cipher = getCipherChunk();
                cipher.init(data, from, to, lastChunk, mode);

                int index = 0;
                try {
                    index = cipher.makeTransform();
                } catch (InterruptedException e) {
                    System.out.println("ECB::compute");
                    e.printStackTrace();

                    //прекращает выполнение всех задач немедленно
                    getPool().shutdownNow();
                }
                //проверка нужна, потому что последний кусок,
                //может быть обработан не в последнюю очередь
                //запомнить индекс на последний байт в массиве
                if (lastChunk) lastByte = index;
            } else {
                int bound;
                if (!lastChunk) {  //если это не последний кусок файла, тогда делим на два
                    bound = (from + to) / 2;

                    invokeAll(new ForkJoinExecution(from, bound, false),
                            new ForkJoinExecution(bound, to, false));
                } else { //иначе, находим максимальную степень двойки, которая будет разделителем
                    bound = getUpperBound(from, to);

                    invokeAll(new ForkJoinExecution(from, bound, false),
                            new ForkJoinExecution(bound, to, true));
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

        private CipherChunk getCipherChunk() {
            return new CipherChunk() {
                @Override
                public int makeTransform() throws InterruptedException {
                    int end = 0;
                    if (mode == ModeOf.ENCRYPTION)
                        while (hasNextBlock())
                            end = writeTransformedBlock(cbAES.encryptBlock(nextBlock()));
                    else
                        while (hasNextBlock())
                            end = writeTransformedBlock(cbAES.decryptBlock(nextBlock()));

                    return end;
                }
            };
        }
    }
}
