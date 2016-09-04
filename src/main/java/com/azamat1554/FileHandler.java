package com.azamat1554;

import com.azamat1554.gui.FilePanel;
import com.azamat1554.mode.*;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Обработчик файлов, если файл больше 128 МБ, то делит его на части и шифрует.
 * Результат записывается в новый файл, который имеет тоже имя с добавлением
 * <code>encrypted</code> в конце. При дешифровке операции выполняются в обратном порядке.
 */

// TODO: 8/28/16 Так и хочется избавить от смещения, чтобы оно выполнялось в классе CBC.
public class FileHandler implements Runnable {
    //размер массива без дополнительных блоков
    private final int SIZE = 134_217_728;

    //хранит байты файла. Два блока в запасе, один для вектора инициализации, второй для дополнения
    private final byte[] bytesOfFile = new byte[SIZE + 2 * AESConst.BLOCK_SIZE];

    private BlockCipher cipher;

    //кол-во считываемых байт
    private int numberOfBytes;

    //указывает на то, последняя это часть файла или нет
    private boolean lastChunk;

    private int offset;

    private long total;

    private List<File> files;
    private int[] indexes;
    private ModeOf modeOf;

    FilePanel panel;

    public FileHandler(FilePanel panel) {
        this.panel = panel;
    }

    public synchronized void init(List<File> files, int[] indexes, Mode mode, ModeOf modeOf) {
        this.files = files;
        this.indexes = indexes;
        this.modeOf = modeOf;

        total = getTotalSize();

        //инициализирует класс в зависимости от режима
        cipher = BlockCipher.getCipher(mode);

        //Смещение нужно для вектора инициализации (IV)
        if (mode != Mode.ECB)
            offset = AESConst.BLOCK_SIZE;
    }

    @Override
    public synchronized void run() {
        int length = indexes.length;
//        for (int i : indexes) {
        for (int i = 0; i < length; i++) {
            File srcFile = files.get(indexes[i]);

            //если выполняется дешифрование, и при этом файл не был зашифрован, тогда следующий файл
            if (modeOf == ModeOf.DECRYPTION && !isEncrypted(srcFile)) continue;

            File destFile = getDestFile(srcFile, modeOf);

            try (FileInputStream fin = new FileInputStream(srcFile);
                 FileOutputStream fout = new FileOutputStream(destFile)) {
                if (modeOf == ModeOf.ENCRYPTION) {
                    panel.setFileNameLbl("[" + (i + 1) + "/" + length + "] File: " + srcFile.getAbsolutePath() + " is encrypting.");
//                    updateText(srcFile.getAbsolutePath() + " is encrypting.", i + 1, length);
                    encrypt(fin, fout);
                } else {
                    panel.setFileNameLbl("[" + (i + 1) + "/" + length + "] File: " + srcFile.getAbsolutePath() + " is decrypting.");
//                    updateText(srcFile.getAbsolutePath() + " is decrypting.", i + 1, length);
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

    private File getDestFile(File srcFile, ModeOf modeOf) {
        File destFile;
        if (modeOf == ModeOf.ENCRYPTION)
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
            boolean lastChunk = restBytes <= SIZE;
            //считать из файла указанное кол-во байт или сколько осталось, и вернуть прочитанное количество байт.
            int numberOfBytes = fin.read(bytesOfFile, offset, SIZE);

            int lastByte = cipher.update(bytesOfFile, numberOfBytes + offset, lastChunk, ModeOf.ENCRYPTION);
            offset = 0;

            //записывает зашифрованные данные в новый файл
            fout.write(bytesOfFile, 0, lastByte);
        }
    }

    private void decrypt(FileInputStream fin, FileOutputStream fout) throws Exception {
        //цикл чтения из файла
        int restBytes;
        while ((restBytes = fin.available()) > 0) {
            boolean lastChunk = restBytes <= SIZE;
            //считать из файла указанное кол-во байт или сколько осталось, и вернуть прочитанное количество байт.
            int numberOfBytes = fin.read(bytesOfFile, 0, SIZE);

            int lastByte = cipher.update(bytesOfFile, numberOfBytes, lastChunk, ModeOf.DECRYPTION);

            //записывает зашифрованные данные в новый файл
            fout.write(bytesOfFile, offset, lastByte - offset);
            offset = 0;
        }
    }

    private void done() {
        panel.apply(indexes);
        BlockCipher.reset();
    }

    private boolean isEncrypted(File file) {
        if (!file.getName().endsWith(".encrypted")) {
            //вызывать в потоке диспетчеризации событий
            JOptionPane.showMessageDialog(panel, "File " + file.getAbsolutePath() + "\nwasn't encryption. It won't be skipped.");
            return false;
        }
        return true;
    }

    public int getProgressInPercentage() {
        return (int) (BlockCipher.getProgress() * 100 / total);
    }

    private long getTotalSize() {
        long length = 0;
        for (int i : indexes) {
            length += files.get(i).length();
        }
        return length;
    }

//    private void updateText(String str, int fileNumber, int totalFiles) {
//        panel.setFileNameLbl("[" + fileNumber + "/" + totalFiles + "] File: " + str);
//    }

    public void cancel() {
        BlockCipher.cipherStop = true;
    }
}
