package com.azamat1554.gui;

import com.azamat1554.handlers.TextHandler;
import com.azamat1554.cipher.modes.CipherMode;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.azamat1554.gui.Utilities.setConstraints;

/**
 * Класс панели, которая формирует ГПИ для обработки текста.
 *
 * @author Azamat Abidokov
 */
public class TextPanel extends JPanel implements ActionListener {
    //ограничение на максимальное кол-во символов
    private final int MAX_LENGTH = 5000;

    private final TextHandler textHandler = new TextHandler();

    private JLabel keyLbl;
    private JLabel restLettersLbl;
    private JPasswordField passFld;
    private JButton showHideBtn;
    private JComboBox<CipherMode> modesCBox;
    private JButton encryptBtn;
    private JButton decryptBtn;
    private JTextArea plainText;
    private JTextArea cipherText;

    public TextPanel() {
        keyLbl = new JLabel(" Key: ", SwingConstants.CENTER);
        restLettersLbl = new JLabel(MAX_LENGTH + " characters is remaining.");
        passFld = new JPasswordField();
        showHideBtn = new JButton(new ImageIcon(ClassLoader.getSystemResource("images/eye_open.png")));

        modesCBox = new JComboBox<>(CipherMode.values());
        encryptBtn = new JButton("Encrypt >>>");
        decryptBtn = new JButton("<<< Decrypt");
        plainText = new JTextArea();
        cipherText = new JTextArea();

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
        restLettersLbl.setFont(font);

        showHideBtn.setBackground(Color.WHITE);
        showHideBtn.setPreferredSize(new Dimension(25, 25));

        //устанавливает перенос текста
        plainText.setLineWrap(true);
        plainText.setWrapStyleWord(true);
        cipherText.setLineWrap(true);
        cipherText.setWrapStyleWord(true);

        /*
         * Устанавливает модель для текстовых областей,
         * которая позволяет ввести ограниченное количество символов символов.
         */
        plainText.setDocument(getDocumentInstance(MAX_LENGTH));
        cipherText.setDocument(getDocumentInstance(MAX_LENGTH * 3));
    }

    private Document getDocumentInstance(int length) {
        return new DefaultStyledDocument() {
            // Вызывается при изменении представления, чтобы обновить модель.
            @Override
            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                if (getLength() + str.length() <= length)
                    super.insertString(offs, str, a);
                else
                    JOptionPane.showMessageDialog(null, "Length of text is exceeded. Use the file tab.");
            }
        };
    }

    private void addComponents() {
        GridBagLayout gbag = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        setLayout(gbag);

        gbc.fill = GridBagConstraints.BOTH;

        //add components on keyPnl
        JPanel keyPnl = new JPanel(new GridBagLayout());
        gbc.insets = new Insets(0, 2, 0, 2);
        keyPnl.add(keyLbl, setConstraints(gbc, 0, 0, 1, 1, 0, 0));
        keyPnl.add(showHideBtn, setConstraints(gbc, 1, 0, 1, 1, 0, 0));
        keyPnl.add(passFld, setConstraints(gbc, 2, 0, 1, 1, 1, 0));

        //add components on TextPanel
        gbc.insets = new Insets(2, 2, 2, 2);
        add(keyPnl, setConstraints(gbc, 0, 0, 3, 1, 1, 0));
        add(new JScrollPane(plainText), setConstraints(gbc, 0, 1, 1, 3, 1, 1));
        add(modesCBox, setConstraints(gbc, 1, 1, 1, 1, 0, 0));
        add(encryptBtn, setConstraints(gbc, 1, 2, 1, 1, 0, 1));
        add(decryptBtn, setConstraints(gbc, 1, 3, 1, 1, 0, 1));
        add(new JScrollPane(cipherText), setConstraints(gbc, 2, 1, 1, 3, 1, 1));
        add(restLettersLbl, setConstraints(gbc, 0, 4, 3, 1, 1, 0));
    }

    private CipherMode cipherMode = CipherMode.ECB;

    private void eventHandlers() {
        showHideBtn.addActionListener(Utilities.showHideAction(showHideBtn, passFld));
        encryptBtn.addActionListener(this);
        decryptBtn.addActionListener(this);
        modesCBox.addActionListener(e -> cipherMode = (CipherMode) modesCBox.getSelectedItem());

        // Обрабатывает события изменения содержимого текстовой области.
        plainText.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateRestLbl();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateRestLbl();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateRestLbl();
            }
        });
    }

    private void updateRestLbl() {
        int rest = MAX_LENGTH - (plainText.getDocument().getLength());
        if (rest >= 0)
            restLettersLbl.setText(rest + " characters is remaining.");
    }

    /* Обработчик нажатия на кнопки Encrypt/Decrypt. */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!Utilities.setKey(passFld.getPassword())) {
            JOptionPane.showMessageDialog(null, "You don't enter key.");
            return;
        }

        if (e.getSource() == encryptBtn)
            cipherText.setText(textHandler.encrypt(plainText.getText(), cipherMode));
        else
            try {
                plainText.setText(textHandler.decrypt(cipherText.getText(), cipherMode));
            } catch (IllegalArgumentException e1) {
                JOptionPane.showMessageDialog(this, "Encrypted string is invalid.");
            }
    }
}

