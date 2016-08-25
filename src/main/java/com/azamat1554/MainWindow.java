package com.azamat1554;

import com.azamat1554.mode.*;

import javax.swing.*;
import java.awt.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Класс реализующий пользовательский интерфейс
 */

// TODO: 8/19/16 Смысл показывать пользователю IV, если он не может его изменить? Просто посмотреть?
//Удалить всю логику связанную с IV.
public class MainWindow extends JFrame {
    private final FileHandler fileHandler = new FileHandler();
    private final TextHandler textHandler = new TextHandler();

    private Mode mode = Mode.ECB;

    private MainWindow() {
        super("AES encryption");

        JTabbedPane jtp = new JTabbedPane();
        jtp.addTab("Text", new TextPanel());
        jtp.addTab("File", new FilePanel());
        add(jtp);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
        setLocationByPlatform(true);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainWindow::new);
    }

    private class TextPanel extends JPanel {
        JLabel keyLbl;
        JPasswordField passFld;
        JButton showHideBtn;
        JTextField initVector;
        JComboBox<Mode> modesCBox;
        JButton encryptBtn;
        JButton decryptBtn;
        JTextArea plainText;
        JTextArea cipherText;

        private TextPanel() {
            //create instance
            keyLbl = new JLabel(" Key: ", SwingConstants.CENTER);
            passFld = new JPasswordField();
            showHideBtn = new JButton(new ImageIcon("src/main/resources/images/eye_open.png"));

            initVector = new JTextField();
            modesCBox = new JComboBox<>(Mode.values());
            encryptBtn = new JButton("Encrypt >>>");
            decryptBtn = new JButton("<<< Decrypt");
            plainText = getNewTextArea();
            cipherText = getNewTextArea();

            setProperties();

            configuredLayoutManagerAndAddComponents();

            eventHandlers();
        }

        private JTextArea getNewTextArea() {
            JTextArea textArea = new JTextArea(10, 35);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            return textArea;
        }

        private void setProperties() {
            //set properties
            Font font = new Font("Arial", Font.PLAIN, 16);
            keyLbl.setFont(font);
            passFld.setFont(font);
            modesCBox.setFont(font);
            encryptBtn.setFont(font);
            decryptBtn.setFont(font);

            showHideBtn.setPreferredSize(new Dimension(25, 25));
            showHideBtn.setMaximumSize(new Dimension(25, 25));
        }

        private void configuredLayoutManagerAndAddComponents() {
            //set up layout manager and add components in panel
            GridBagLayout gbag = new GridBagLayout();
            GridBagConstraints gbc = new GridBagConstraints();
            setLayout(gbag);

            gbc.insets = new Insets(2, 2, 2, 2);
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 0;
            gbc.weighty = 0;
            gbc.gridx = 0;
            gbc.gridy = 0;
            add(keyLbl, gbc);

            gbc.gridx = 2;
            gbc.gridy = 0;
            gbc.gridwidth = 3;
            add(passFld, gbc);

            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.gridwidth = 1;
            add(showHideBtn, gbc);

            gbc.weightx = 1;
            gbc.weighty = 1;
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 3;
            gbc.gridheight = 3;
            add(new JScrollPane(plainText), gbc);

            gbc.weightx = 0;
            gbc.weighty = 0;
            gbc.gridx = 3;
            gbc.gridy = 2;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            add(modesCBox, gbc);

            gbc.weighty = 1;
            gbc.gridx = 3;
            gbc.gridy = 3;
            add(encryptBtn, gbc);

            gbc.gridx = 3;
            gbc.gridy = 4;
            add(decryptBtn, gbc);

            gbc.weightx = 1;
            gbc.gridx = 4;
            gbc.gridy = 2;
            gbc.gridwidth = 1;
            gbc.gridheight = 3;
            add(new JScrollPane(cipherText), gbc);
        }

        //пароль скрыт?
        private boolean passHide = true;

        private void eventHandlers() {
            Icon openEye = new ImageIcon("src/main/resources/images/eye_open.png");
            Icon closeEye = new ImageIcon("src/main/resources/images/eye_close.png");

            showHideBtn.addActionListener(e -> {
                if (passHide) {
                    showHideBtn.setIcon(closeEye);
                    passFld.setEchoChar((char) 0);
                    passHide = false;
                } else {
                    showHideBtn.setIcon(openEye);
                    passFld.setEchoChar((char) 0x2022);
                    passHide = true;
                }
            });

            encryptBtn.addActionListener(e -> {
                try {
                    setKey(passFld.getPassword());
                } catch (IllegalArgumentException e1) {
                    return;
                }

                cipherText.setText(textHandler.encrypt(plainText.getText(), mode));
            });

            decryptBtn.addActionListener(e -> {
                try {
                    setKey(passFld.getPassword());
                } catch (IllegalArgumentException e1) {
                    return;
                }

                try {
                    plainText.setText(textHandler.decrypt(cipherText.getText(), mode));
                } catch (IllegalArgumentException e1) {
                    JOptionPane.showMessageDialog(this, "Encrypted string is invalid.");
                }
            });

            modesCBox.addActionListener(e -> {
                mode = (Mode) modesCBox.getSelectedItem();
            });
        }
    }

    private class FilePanel extends JPanel {
        JLabel keyLbl;
        JPasswordField passFld;
        JButton encryptDecryptBtn;
        JComboBox<Mode> modesCBox;

        private FilePanel() {
            keyLbl = new JLabel(" Key: ", SwingConstants.CENTER);
            passFld = new JPasswordField(20);
            encryptDecryptBtn = new JButton("Pending");
            modesCBox = new JComboBox<>(Mode.values());

            add(keyLbl);
            add(passFld);
            add(encryptDecryptBtn);
            add(modesCBox);

            encryptDecryptBtn.addActionListener(e -> {
                String fileName = "/home/azamat/Desktop/test/";

//                //Проверка на то, что файл еще не был зашифрован.
//                if (!fileName.endsWith(".encrypted")) { //todo тогда показать окно для выбора дальнейших действий
//                    //fileHandler.encrypt(fileName, mode, iv);
//
//                    System.out.println("File have already been encrypted.");
//                    return;
//                } else {
//
//
                long start;

                //ключи шифрования/расшифрования
                setKey(passFld.getPassword());

                mode = (Mode) modesCBox.getSelectedItem();
                //iv = getIV();

                start = System.currentTimeMillis();
                fileHandler.encrypt(fileName, mode);
                System.out.println((System.currentTimeMillis() - start) / 1000);

                start = System.currentTimeMillis();
                fileHandler.decrypt(fileName, mode);
                System.out.println((System.currentTimeMillis() - start) / 1000);
            });
        }


    }

    private void setKey(char[] key) {
        if (key.length == 0) {
            JOptionPane.showMessageDialog(this, "You don't enter key.");
            // TODO: 8/18/16 сделать свой класс исключений, при отсутствии ключа
            return;
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

    private String hexSrting(byte[] array) {
        char[] hex = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        StringBuilder ivStr = new StringBuilder();
        for (byte anArray : array) {
            ivStr.append(hex[(anArray & 0xf0) >> 4]).append(hex[anArray & 0x0f]).append(' ');
        }
        return ivStr.toString();
    }

    private byte[] toByte(char[] input) {
        byte[] output = new byte[input.length * 2];
        for (int i = 0; i < input.length; i++) {
            output[i * 2] = (byte) (input[i] >> 8);
            output[i * 2 + 1] = (byte) input[i];
        }
        return output;
    }
}
