package com.azamat1554.gui;

import com.azamat1554.FileHandler;
import com.azamat1554.ModeOf;
import com.azamat1554.mode.Mode;

import javax.swing.*;
import java.io.File;
import java.util.List;

/**
 * Класс управляющий потоком, осуществляющим шифрование/дешифрование
 */
public class Executor extends Thread implements Runnable{
    private volatile boolean encrypted;
    private volatile boolean stopFlag;

    private FileHandler handler;

    private List<File> files;
    private int[] indexes;
    private Mode mode;
    private ModeOf modeOf;

    public Executor(FileHandler handler) {
        this.handler = handler;
    }

    public synchronized void init( List<File> files, int[] indexes, Mode mode, ModeOf modeOf) {
        this.files = files;
        this.indexes = indexes;
        this.mode = mode;
        this.modeOf = modeOf;
    }


    public void cancel() {
        //выполнить сброс объекта
        System.out.println("execution::cancel");
        interrupt();
    }

    public void suspendExecution() {
        System.out.println("execution::suspend");
        stopFlag = true;
    }

    public synchronized void resumeExecution() {
        System.out.println("execution::resume");
        stopFlag = false;
        notify();
    }

    public int getProgress() {
        return 0;
    }

    @Override
    public synchronized void run() {
        while (!currentThread().isInterrupted()) {
            while (stopFlag) {
                try {
                    System.out.println("executor::waiting");
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

//            if (modeOf == ModeOf.ENCRYPTION)
//                handler.encrypt(files, indexes, mode);
//            else
//                handler.decrypt(files, indexes, mode);
        }
        System.out.println("executor::end");
    }
}

