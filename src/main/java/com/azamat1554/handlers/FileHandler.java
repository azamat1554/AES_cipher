package com.azamat1554.handlers;

import com.azamat1554.cipher.AESConst;
import com.azamat1554.cipher.ModeOf;
import com.azamat1554.cipher.modes.*;
import com.azamat1554.gui.FilePanel;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.azamat1554.cipher.modes.BlockCipher.getMode;

/**
 * Обработчик файлов, если файл больше 128 МБ, тогда считывает его частями и запускает преобразование данных,
 * в соответствии с указанными параметрами. Результат преобразования записывается в новый файл.
 * При шифровании к имени файла добавляется расширение <i>encrypted</i> в конце.
 * При расшифровке расширение отбрасывается.
 */
public class FileHandler implements Runnable {
    /* Максимально количество данных, которые могут быть считаны за раз. */
    private final int SIZE = 134_217_728;

    /**
     * Хранит байты файла. Два блока в запасе, один для вектора инициализации, второй для дополнения.
     */
    private final byte[] bytesOfFile = new byte[SIZE + 2 * AESConst.BLOCK_SIZE];

    /* Ссылка на класс, который осуществляет обработку считанных данных. */
    private BlockCipher cipher;

    /* Задает смещение для вектора инициализации. */
    private int offset;

    /* Общее количество байт, которые нужно обработать. */
    private long total;

    /* Список файлов, которые нужно обработать. */
    private List<File> files;

    /* Индексы файлов в списке, которые выделил пользователь. */
    private int[] indexes;

    private final FilePanel panel;

    public FileHandler(FilePanel panel) {
        this.panel = panel;
    }

    /**
     * Выполняет инициализацию класса.
     *
     * @param files      Список всех файлов.
     * @param indexes    Индексы файлов, которые нужно обработать.
     * @param cipherMode Режим блочного шифра.
     * @return {@code true} если инициализация прошла успешно, {@code false} иначе.
     */
    public synchronized boolean init(List<File> files, int[] indexes, CipherMode cipherMode) {
        this.files = files;
        this.indexes = getApproveIndexes(indexes);
        total = getTotalSize();

        // Если нет подходящих файлов или их размер равен нулю, тогда прервать
        if (this.indexes.length == 0 || total == 0) return false;

        //инициализирует класс в зависимости от режима
        cipher = BlockCipher.getCipher(cipherMode);

        //Смещение нужно для вектора инициализации (IV)
        if (cipherMode != CipherMode.ECB)
            offset = AESConst.BLOCK_SIZE;

        return true;
    }

    @Override
    public synchronized void run() {
        int length = indexes.length;
        for (int i = 0; i < length; i++) {
            File srcFile = files.get(indexes[i]);
            File destFile = getDestFile(srcFile, getMode());

            try (FileInputStream fin = new FileInputStream(srcFile);
                 FileOutputStream fout = new FileOutputStream(destFile)) {

                if (getMode() == ModeOf.ENCRYPTION) {
                    panel.setFileNameLbl("[" + (i + 1) + "/" + length + "] File: " + srcFile.getAbsolutePath() + " is encrypting.");
                    encrypt(fin, fout);
                } else {
                    panel.setFileNameLbl("[" + (i + 1) + "/" + length + "] File: " + srcFile.getAbsolutePath() + " is decrypting.");
                    decrypt(fin, fout);
                }
            } catch (Exception e) {
                System.out.println("FileHandler::run");
                e.printStackTrace();

                destFile.delete();
                done();
                return;
            }
            files.set(indexes[i], destFile);
            srcFile.delete();
        }
        done();
    }

    /* Возвращает объект файла, в который будут записываться обработанные данные. */
    private File getDestFile(File srcFile, ModeOf mode) {
        File destFile;
        if (mode == ModeOf.ENCRYPTION)
            destFile = new File(srcFile.getAbsolutePath() + ".encrypted");
        else {
            String path = srcFile.getAbsolutePath();
            destFile = new File(path.substring(0, path.lastIndexOf('.')));
        }

        return destFile;
    }

    private void encrypt(FileInputStream fin, FileOutputStream fout) throws Exception {
        //цикл чтения из файла
        int restBytes;
        while ((restBytes = fin.available()) > 0) {
            //считывает из файла указанное кол-во байт или сколько осталось, и вернуть прочитанное количество байт.
            int numberOfBytes = fin.read(bytesOfFile, offset, SIZE);

            int lastByte = cipher.update(bytesOfFile, numberOfBytes + offset, restBytes <= SIZE);
            offset = 0;

            //записывает зашифрованные данные в новый файл
            fout.write(bytesOfFile, 0, lastByte);
        }
    }

    private void decrypt(FileInputStream fin, FileOutputStream fout) throws Exception {
        //цикл чтения из файла
        int restBytes;
        while ((restBytes = fin.available()) > 0) {
            //считывает из файла указанное кол-во байт или сколько осталось, и возвращает прочитанное количество байт.
            int numberOfBytes = fin.read(bytesOfFile, 0, SIZE);

            int lastByte = cipher.update(bytesOfFile, numberOfBytes, restBytes <= SIZE);

            //записывает зашифрованные данные в новый файл
            fout.write(bytesOfFile, offset, lastByte - offset);
            offset = 0;
        }
    }

    /* Возвращает индексы файлов, которые удовлетворяют условию. */
    private int[] getApproveIndexes(int[] indexes) {
        if (getMode() == ModeOf.DECRYPTION) {
            // Исключить файлы, которые не были зашифрованы
            return Arrays.stream(indexes).filter(i -> isEncrypted(files.get(i))).toArray();
        }

        return indexes;
    }

    private boolean isEncrypted(File file) {
        if (!file.getName().endsWith(".encrypted")) {
            JOptionPane.showMessageDialog(panel, "File " + file.getAbsolutePath() + "\nwasn't encryption. It will be skipped.");
            return false;
        }
        return true;
    }

    private long getTotalSize() {
        long length = 0;
        for (int i : indexes) {
            length += files.get(i).length();
        }
        return length;
    }

    private void done() {
        panel.apply(indexes);
        BlockCipher.reset();
    }

    /**
     * Возвращает состояние выполнения в процентах.
     */
    public int getProgressInPercentage() {
        return (int) (BlockCipher.getProgress() * 100 / total);
    }

    /**
     * Прерывает поток выполняющий обработку данных.
     */
    public void cancel() {
        BlockCipher.cipherStop = true;
    }
}
