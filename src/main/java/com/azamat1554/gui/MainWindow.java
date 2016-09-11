package com.azamat1554.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Класс формы, с него начинается выполнение приложения
 *
 * @author Azamat Abidokov
 */
public class MainWindow extends JFrame {

    private MainWindow() {
        super("AES encryption");

        JTabbedPane jtp = new JTabbedPane();

        FilePanel filePanel = new FilePanel(jtp);
        jtp.addTab("File", filePanel);
        jtp.addTab("Text", new TextPanel());
        add(jtp);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (filePanel.running()) return;

                System.exit(0);
            }
        });

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(400, 300));
        setSize(new Dimension(800, 400));
        setLocationByPlatform(true);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainWindow::new);
    }
}
