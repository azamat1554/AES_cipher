package com.azamat1554.gui;

import com.azamat1554.cipher.CipherBlockAES;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Класс содержащий служебные методы.
 *
 * @author Azamat Abidokov
 */
class Utilities {
    /* Устанавливает указанные ограничения*/
    static GridBagConstraints setConstraints(GridBagConstraints gbc, int gridx, int gridy, int gridwidth,
                                             int gridheight, double weightx, double weighty) {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        gbc.weightx = weightx;
        gbc.weighty = weighty;
        return gbc;
    }

    /**
     * Устанавливает ключ шифрования.
     *
     * @param key Массив символов ключа.
     * @return {@code true} если ключ был успешно установлен, {@code false} иначе.
     */
    static boolean setKey(char[] key) {
        // Если ключ не задан, выйти.
        if (key.length == 0) return false;

        //установка ключа
        try {
            // Вычисляет хэш от ключа, который затем используется как ключ шифрования.
            CipherBlockAES.Key.setKey(MessageDigest.getInstance("MD5").digest(toByte(key)));

            //обнулить массив с ключем
            Arrays.fill(key, '\u0000');
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
            return false;
        }
        return true;
    }

    // Переводит символы в байты
    private static byte[] toByte(char[] input) {
        byte[] output = new byte[input.length * 2];
        for (int i = 0; i < input.length; i++) {
            output[i * 2] = (byte) (input[i] >> 8);
            output[i * 2 + 1] = (byte) input[i];
        }
        return output;
    }

    private static final Icon openEye = new ImageIcon(ClassLoader.getSystemResource("images/eye_open.png"));
    private static final Icon closeEye = new ImageIcon(ClassLoader.getSystemResource("images/eye_close.png"));

    /**
     * Показывает и скрывает ключ шифрования.
     *
     * @param showHideBtn Кнопка, которая была нажата.
     * @param passFld     Текстовое поле содержащее ключ
     * @return Обработчик нажатия на кнопку.
     */
    public static ActionListener showHideAction(JButton showHideBtn, JPasswordField passFld) {
        return e -> {
            if (passFld.getEchoChar() != 0) {
                showHideBtn.setIcon(closeEye);

                //делает символы видимыми
                passFld.setEchoChar((char) 0);
            } else {
                showHideBtn.setIcon(openEye);
                passFld.setEchoChar((char) 0x2022);
            }
        };
    }
}
