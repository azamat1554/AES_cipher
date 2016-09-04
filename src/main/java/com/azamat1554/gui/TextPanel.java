package com.azamat1554.gui;

import com.azamat1554.TextHandler;
import com.azamat1554.mode.Mode;

import javax.naming.LimitExceededException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.azamat1554.gui.Utilities.getConstraints;
import static com.azamat1554.gui.Utilities.setKey;

//подкласс панели для шифрования текста
public class TextPanel extends JPanel implements ActionListener {
    private final TextHandler textHandler = new TextHandler();

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

    public TextPanel() {
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

    private Mode mode = Mode.ECB;

    private void eventHandlers() {
        showHideBtn.addActionListener(Utilities.showHideAction(showHideBtn, passFld));
        encryptBtn.addActionListener(this);
        decryptBtn.addActionListener(this);
        modesCBox.addActionListener(e -> mode = (Mode) modesCBox.getSelectedItem());
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            setKey(passFld.getPassword());
        } catch (IllegalArgumentException e1) {
            return;
        }

        if (e.getSource() == encryptBtn)
// TODO: 9/2/16 Выяснить, почему залипает кнопка при многократном нажатии 
            try {
                cipherText.setText(textHandler.encrypt(plainText.getText(), mode));
            } catch (LimitExceededException e1) {
                JOptionPane.showMessageDialog(this, "Length of text is exceeded. Use the file tab.");
                //e1.printStackTrace();
            }
        else
            try {
                plainText.setText(textHandler.decrypt(cipherText.getText(), mode));
            } catch (IllegalArgumentException e1) {
                JOptionPane.showMessageDialog(this, "Encrypted string is invalid.");
            } catch (LimitExceededException e1) {
                JOptionPane.showMessageDialog(this, "Length of text is exceeded. Use the file tab.");
                //e1.printStackTrace();
            }
    }
}

