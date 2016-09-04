package com.azamat1554.gui;

import com.azamat1554.CipherBlockAES;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Класс содержащий служебные методы
 */
public class Utilities {
    static GridBagConstraints getConstraints(GridBagConstraints gbc, int gridx, int gridy, int gridwidth,
                                              int gridheight, double weightx, double weighty) {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        gbc.weightx = weightx;
        gbc.weighty = weighty;
        return gbc;
    }

    static void setKey(char[] key) {
        if (key.length == 0) {
            JOptionPane.showMessageDialog(null, "You don't enter key.");

            throw new IllegalArgumentException();
        }

        //установка ключа
        try {
            CipherBlockAES.Key.setKey(MessageDigest.getInstance("MD5").digest(toByte(key)));
            //обнулить массив с ключем
            Arrays.fill(key, '\u0000');
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
    }

    private static byte[] toByte(char[] input) {
        byte[] output = new byte[input.length * 2];
        for (int i = 0; i < input.length; i++) {
            output[i * 2] = (byte) (input[i] >> 8);
            output[i * 2 + 1] = (byte) input[i];
        }
        return output;
    }

    private static Icon openEye = new ImageIcon("src/main/resources/images/eye_open.png");
    private static Icon closeEye = new ImageIcon("src/main/resources/images/eye_close.png");

    public static ActionListener showHideAction(JButton showHideBtn, JPasswordField passFld) {
        return e -> {
            if (passFld.getEchoChar() != 0) {
                showHideBtn.setIcon(closeEye);
                passFld.setEchoChar((char) 0);
            } else {
                showHideBtn.setIcon(openEye);
                passFld.setEchoChar((char) 0x2022);
            }
        };
    }
}
