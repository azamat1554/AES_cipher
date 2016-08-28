package com.azamat1554;

import com.azamat1554.mode.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.List;

/**
 * Класс реализующий пользовательский интерфейс
 */

// TODO: 8/26/16 Если программа уже выполняет шифрование, тогда запретить
public class MainWindow extends JFrame {
    private final FileHandler fileHandler = new FileHandler();
    private final TextHandler textHandler = new TextHandler();

    private MainWindow() {
        super("AES encryption");

        JTabbedPane jtp = new JTabbedPane();
        jtp.addTab("Text", new TextPanel());
        jtp.addTab("File", new FilePanel());
        add(jtp);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(400, 300));
        setSize(new Dimension(800, 400));
        //pack();
        setLocationByPlatform(true);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainWindow::new);
    }

    //подкласс панели для шифрования текста
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
        JPanel keyPnl;

        private TextPanel() {
            //create instance
            keyLbl = new JLabel(" Key: ", SwingConstants.CENTER);
            passFld = new JPasswordField();
            showHideBtn = new JButton(new ImageIcon("src/main/resources/images/eye_open.png"));
            keyPnl = new JPanel(new GridBagLayout());

            initVector = new JTextField();
            modesCBox = new JComboBox<>(Mode.values());
            encryptBtn = new JButton("Encrypt >>>");
            decryptBtn = new JButton("<<< Decrypt");
            plainText = getNewTextArea();
            cipherText = getNewTextArea();

            setProperties();
            addComponents();
            eventHandlers();
        }

        private JTextArea getNewTextArea() {
            JTextArea textArea = new JTextArea(10, 30);
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

            showHideBtn.setBackground(Color.WHITE);

            showHideBtn.setPreferredSize(new Dimension(25, 25));
        }

        private void addComponents() {
            //set up layout manager and add components in panel
            GridBagLayout gbag = new GridBagLayout();
            GridBagConstraints gbc = new GridBagConstraints();
            setLayout(gbag);

            gbc.fill = GridBagConstraints.BOTH;

            //add components on keyPnl
            gbc.insets = new Insets(0, 2, 0, 2);
            keyPnl.add(keyLbl, getConstraints(gbc, 0, 0, 1, 1, 0, 0));
            keyPnl.add(showHideBtn, getConstraints(gbc, 1, 0, 1, 1, 0, 0));
            keyPnl.add(passFld, getConstraints(gbc, 2, 0, 1, 1, 1, 0));

            //add components on TextPanel
            gbc.insets = new Insets(2, 2, 2, 2);
            add(keyPnl, getConstraints(gbc, 0, 0, 3, 1, 1, 0));
            add(new JScrollPane(plainText), getConstraints(gbc, 0, 1, 1, 3, 1, 1));
            add(modesCBox, getConstraints(gbc, 1, 1, 1, 1, 0, 0));
            add(encryptBtn, getConstraints(gbc, 1, 2, 1, 1, 0, 1));
            add(decryptBtn, getConstraints(gbc, 1, 3, 1, 1, 0, 1));
            add(new JScrollPane(cipherText), getConstraints(gbc, 2, 1, 1, 3, 1, 1));
        }

        //пароль скрыт?
        private boolean passHide = true;
        private Mode mode = Mode.ECB;

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

            modesCBox.addActionListener(e -> mode = (Mode) modesCBox.getSelectedItem());
        }
    }


    // TODO: 8/28/16 Make drag and drop to table.
    //подкласс панели для шифрования файлов
    private class FilePanel extends JPanel {
        JLabel keyLbl;
        JPasswordField passFld;
        JButton showHideBtn;
        JButton encryptBtn;
        JButton decryptBtn;
        JButton addFileBtn;
        JButton removeFileBtn;
        JButton stopBtn;
        JComboBox<Mode> modesCBox;
        JTable filesTbl;
        JProgressBar progressBar;

        private FilePanel() {
            filesTbl = new JTable(new DefaultTableModel(new Object[]{"Path", "Size"}, 0)) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            addFileBtn = new JButton("Add files");
            removeFileBtn = new JButton(new ImageIcon("src/main/resources/images/trash.png"));

            keyLbl = new JLabel(" Key: ", SwingConstants.CENTER);
            passFld = new JPasswordField();
            showHideBtn = new JButton(new ImageIcon("src/main/resources/images/eye_open.png"));
            encryptBtn = new JButton("Encrypt");
            decryptBtn = new JButton("Decrypt");
            modesCBox = new JComboBox<>(Mode.values());
            progressBar = new JProgressBar(SwingConstants.HORIZONTAL);
            stopBtn = new JButton("Stop");

            setProperties();
            addComponents();
            eventHandlers();
        }

        private void setProperties() {
            Font font = new Font("Arial", Font.PLAIN, 16);
            keyLbl.setFont(font);
            passFld.setFont(font);
            modesCBox.setFont(font);
            encryptBtn.setFont(font);
            decryptBtn.setFont(font);
            addFileBtn.setFont(font);
            stopBtn.setFont(new Font("Arial", Font.PLAIN, 12));

            filesTbl.getColumnModel().getColumn(1).setMaxWidth(100);
            showHideBtn.setBackground(Color.WHITE);
            showHideBtn.setPreferredSize(new Dimension(25, 25));
            stopBtn.setEnabled(false);


        }

        private void addComponents() {
            //set up layout manager and add components in panel
            GridBagLayout gbag = new GridBagLayout();
            GridBagConstraints gbc = new GridBagConstraints();
            setLayout(gbag);

            gbc.fill = GridBagConstraints.BOTH;

            JPanel listFilesPnl = new JPanel(new GridBagLayout());
            gbc.insets = new Insets(2, 2, 2, 2);
            listFilesPnl.add(new JScrollPane(filesTbl), getConstraints(gbc, 0, 0, 2, 1, 1, 1));
            listFilesPnl.add(addFileBtn, getConstraints(gbc, 0, 1, 1, 1, 1, 0));
            listFilesPnl.add(removeFileBtn, getConstraints(gbc, 1, 1, 1, 1, 0, 0));


            JPanel controlPnl = new JPanel(new GridBagLayout());
            controlPnl.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)));

            JPanel keyAndModePnl = new JPanel(new GridBagLayout());
            gbc.insets = new Insets(0, 2, 0, 2);
            keyAndModePnl.add(keyLbl, getConstraints(gbc, 0, 0, 1, 1, 0, 0));
            keyAndModePnl.add(showHideBtn, getConstraints(gbc, 1, 0, 1, 1, 0, 0));
            keyAndModePnl.add(modesCBox, getConstraints(gbc, 3, 0, 1, 1, 0, 0));
            keyAndModePnl.add(passFld, getConstraints(gbc, 2, 0, 1, 1, 1, 0));

            //add components on controlPnl
            gbc.insets = new Insets(2, 2, 2, 2);
            controlPnl.add(keyAndModePnl, getConstraints(gbc, 0, 0, 3, 1, 1, 0));

            gbc.ipady = 10;
            controlPnl.add(encryptBtn, getConstraints(gbc, 0, 1, 1, 1, 1, 1));
            controlPnl.add(decryptBtn, getConstraints(gbc, 1, 1, 1, 1, 1, 1));
            gbc.ipady = 0;

            JPanel progressPnl = new JPanel(new GridBagLayout());
            progressPnl.add(stopBtn, getConstraints(gbc, 1, 0, 1, 1, 0, 0));
            progressPnl.add(progressBar, getConstraints(gbc, 0, 0, 1, 1, 1, 0));

            //add components on FilePanel
            add(listFilesPnl, getConstraints(gbc, 0, 0, 1, 1, 1, 1));
            add(controlPnl, getConstraints(gbc, 0, 1, 1, 1, 1, 0.1));
            add(progressPnl, getConstraints(gbc, 0, 2, 1, 1, 1, 0));
        }

        //пароль скрыт?
        private boolean passHide = true;
        private Mode mode = Mode.ECB;
        List<File> files = new ArrayList<>();
        //List<String> filePaths = new ArrayList<>();

        private void eventHandlers() {
            addFileBtn.addActionListener(e -> {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setMultiSelectionEnabled(true);

                int result = fileChooser.showOpenDialog(this);

                if (result == JFileChooser.APPROVE_OPTION) {
                    //коллекция файлов
                    for (File f : fileChooser.getSelectedFiles())
                        //Если файл еще не был добавлен в коллекцию, тогда добавить и вывести на таблицу
                        if (!files.contains(f)) {
                            files.add(f);
                            ((DefaultTableModel) filesTbl.getModel()).addRow(new Object[]{f.getAbsolutePath(), getFileSize(f)});
                        }
                }
            });

            removeFileBtn.addActionListener(e -> {
                int[] selectedRowsIndexes = filesTbl.getSelectedRows();
                if (selectedRowsIndexes.length > 0) {
                    for (int i = selectedRowsIndexes.length - 1; i >= 0; i--) {
                        files.remove(selectedRowsIndexes[i]); //удаляет файл из коллекции
                        ((DefaultTableModel) filesTbl.getModel()).removeRow(selectedRowsIndexes[i]); //удаляет строку о файле из таблицы
                    }
                }
            });

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
                if (!isSelected()) return;
                int[] selectedRowsIndexes = getIndexesOfRows();

                try {
                    setKey(passFld.getPassword());
                } catch (IllegalArgumentException e1) {
                    return;
                }

                for (int i : selectedRowsIndexes) {
                    long start = System.currentTimeMillis();
                    File newFile = fileHandler.encrypt(files.get(i), mode);
                    System.out.println((System.currentTimeMillis() - start) / 1000);

                    files.set(i, newFile);
                    filesTbl.getModel().setValueAt(newFile.getAbsolutePath(), i, 0);
                }
            });

            decryptBtn.addActionListener(e -> {
                if (!isSelected()) return;
                int[] selectedRowsIndexes = getIndexesOfRows();

                try {
                    setKey(passFld.getPassword());
                } catch (IllegalArgumentException e1) {
                    return;
                }


                for (int i : selectedRowsIndexes) {
                    if (!isEncrypted(files.get(i))) continue;

                    long start = System.currentTimeMillis();
                    File newFile = fileHandler.decrypt(files.get(i), mode);
                    System.out.println((System.currentTimeMillis() - start) / 1000);

                    files.set(i, newFile);
                    filesTbl.getModel().setValueAt(newFile.getAbsolutePath(), i, 0);
                }
            });

            modesCBox.addActionListener(e -> mode = (Mode) modesCBox.getSelectedItem());

            stopBtn.addActionListener(e -> {

            });
        }

        private String getFileSize(File f) {
            // TODO: 8/27/16 Установить макс размер файла в 5 ГБ

            double megabytes = (double) f.length() / (1024 * 1024);
            return String.format("%.1f MB", megabytes);
        }

        private boolean isEncrypted(File file) {
            if (!file.getName().endsWith(".encrypted")) {
                JOptionPane.showMessageDialog(this, "File " + file.getAbsolutePath() + " wasn't encryption. It won't be skipped.");
                return false;
            }
            return true;
        }

        private boolean isSelected() {
            if (!files.isEmpty()) {
                if (files.size() == 1 || filesTbl.getSelectedRowCount() != 0)
                    return true;

                JOptionPane.showMessageDialog(this, "You haven't selected any file.");
            }
            return false;
        }

        private int[] getIndexesOfRows() {
            if (files.size() == 1)
                return new int[]{0};

            return filesTbl.getSelectedRows();
        }
    }

    private GridBagConstraints getConstraints(GridBagConstraints gbc, int gridx, int gridy, int gridwidth,
                                              int gridheight, double weightx, double weighty) {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        gbc.weightx = weightx;
        gbc.weighty = weighty;
        return gbc;
    }

    private void setKey(char[] key) {
        if (key.length == 0) {
            JOptionPane.showMessageDialog(this, "You don't enter key.");
            // TODO: 8/18/16 сделать свой класс исключений, при отсутствии ключа
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

    private byte[] toByte(char[] input) {
        byte[] output = new byte[input.length * 2];
        for (int i = 0; i < input.length; i++) {
            output[i * 2] = (byte) (input[i] >> 8);
            output[i * 2 + 1] = (byte) input[i];
        }
        return output;
    }
}
