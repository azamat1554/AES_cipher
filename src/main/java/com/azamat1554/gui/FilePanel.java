package com.azamat1554.gui;

import com.azamat1554.FileHandler;
import com.azamat1554.ModeOf;
import com.azamat1554.mode.Mode;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;
import java.util.List;

import static com.azamat1554.gui.Utilities.getConstraints;

//подкласс панели для шифрования файлов
public class FilePanel extends JPanel implements ActionListener {
    private final FileHandler fileHandler = new FileHandler(this);
    private Thread cipherThread;
    private Timer progressTmr;

    private JTabbedPane container;
    private CardLayout cardLayout;
    private JLabel keyLbl;
    private JLabel fileNameLbl;
    private JPasswordField passFld;
    private JButton showHideBtn;
    private JButton encryptBtn;
    private JButton decryptBtn;
    private JButton addFileBtn;
    private JButton removeFileBtn;
    private JButton stopBtn;
    private JComboBox<Mode> modesCBox;
    private JTable filesTbl;
    private JProgressBar progressBar;

    public FilePanel(JComponent container) {
        this.container = (JTabbedPane) container;
        cardLayout = new CardLayout();

        filesTbl = new JTable(new DefaultTableModel(new Object[]{"Path", "Size"}, 0)) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        addFileBtn = new JButton("Add files");
        removeFileBtn = new JButton(new ImageIcon("src/main/resources/images/trash.png"));

        keyLbl = new JLabel(" Key: ", SwingConstants.CENTER);
        fileNameLbl = new JLabel();
        passFld = new JPasswordField();
        showHideBtn = new JButton(new ImageIcon("src/main/resources/images/eye_open.png"));
        encryptBtn = new JButton("Encrypt");
        decryptBtn = new JButton("Decrypt");
        modesCBox = new JComboBox<>(Mode.values());
        progressBar = new JProgressBar();
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
        filesTbl.setDragEnabled(true);
        filesTbl.setFillsViewportHeight(true);

        showHideBtn.setBackground(Color.WHITE);
        showHideBtn.setPreferredSize(new Dimension(25, 25));
        progressBar.setStringPainted(true);
    }

    private void addComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        JPanel keyAndModePnl = new JPanel(new GridBagLayout());
        gbc.insets = new Insets(0, 2, 0, 2);
        keyAndModePnl.add(keyLbl, getConstraints(gbc, 0, 0, 1, 1, 0, 0));
        keyAndModePnl.add(showHideBtn, getConstraints(gbc, 1, 0, 1, 1, 0, 0));
        keyAndModePnl.add(modesCBox, getConstraints(gbc, 3, 0, 1, 1, 0, 0));
        keyAndModePnl.add(passFld, getConstraints(gbc, 2, 0, 1, 1, 1, 0));


        JPanel controlPnl = new JPanel(new GridBagLayout());
        controlPnl.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        gbc.insets = new Insets(2, 2, 2, 2);
        controlPnl.add(keyAndModePnl, getConstraints(gbc, 0, 0, 3, 1, 1, 0));
        gbc.ipady = 10;
        controlPnl.add(encryptBtn, getConstraints(gbc, 0, 1, 1, 1, 1, 1));
        controlPnl.add(decryptBtn, getConstraints(gbc, 1, 1, 1, 1, 1, 1));
        gbc.ipady = 0;


        JPanel mainPnl = new JPanel(new GridBagLayout());
        mainPnl.add(new JScrollPane(filesTbl), getConstraints(gbc, 0, 0, 2, 1, 1, 1));
        mainPnl.add(addFileBtn, getConstraints(gbc, 0, 1, 1, 1, 1, 0));
        mainPnl.add(removeFileBtn, getConstraints(gbc, 1, 1, 1, 1, 0, 0));
        mainPnl.add(controlPnl, getConstraints(gbc, 0, 2, 2, 1, 1, 0.1));


        JPanel progressPnl = new JPanel(new GridBagLayout());
        progressPnl.add(fileNameLbl, getConstraints(gbc, 0, 0, 2, 1, 0, 0));
        progressPnl.add(progressBar, getConstraints(gbc, 0, 1, 1, 1, 1, 0));
        progressPnl.add(stopBtn, getConstraints(gbc, 1, 1, 1, 1, 0, 0));


        setLayout(cardLayout);
        add(mainPnl, "Main");
        add(progressPnl, "Progress");
    }

    private Mode mode = Mode.ECB;
    List<File> files = new ArrayList<>();

    private void eventHandlers() {
        addFileBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setMultiSelectionEnabled(true);

            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
                addFiles(Arrays.asList(fileChooser.getSelectedFiles()));
        });

        //drag and drop
        filesTbl.setTransferHandler(new TransferHandler() {
            //этот метод проверяет удовлетворяет ли DnD указанным условиям
            @Override
            public boolean canImport(TransferHandler.TransferSupport support) {
                return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
            }

            //этот метод вызывается, если DnD был выполненен успешно, т.е. canImport вурнул true.
            @Override
            public boolean importData(TransferHandler.TransferSupport support) {
                try {
                    addFiles((List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor));
                } catch (Exception e) {
                    return false;
                }
                return true;
            }
        });

        removeFileBtn.addActionListener(e -> {
            if (files.size() > 0) {
                //если в коллекции только один файл
                if (files.size() == 1) {
                    files.remove(0); //удаляет файл из коллекции
                    ((DefaultTableModel) filesTbl.getModel()).removeRow(0); //удаляет строку о файле из таблицы
                    return;
                }

                //если в коллекции больше одного файла, тогда удалить выделенные файлы
                int[] selectedRowsIndexes = filesTbl.getSelectedRows();
                if (selectedRowsIndexes.length > 0) {
                    for (int i = selectedRowsIndexes.length - 1; i >= 0; i--) {
                        files.remove(selectedRowsIndexes[i]); //удаляет файл из коллекции
                        ((DefaultTableModel) filesTbl.getModel()).removeRow(selectedRowsIndexes[i]); //удаляет строку о файле из таблицы
                    }
                }
            }
        });

        showHideBtn.addActionListener(Utilities.showHideAction(showHideBtn, passFld));
        encryptBtn.addActionListener(this);
        decryptBtn.addActionListener(this);
        modesCBox.addActionListener(e -> mode = (Mode) modesCBox.getSelectedItem());

        stopBtn.addActionListener(e -> {
            stopBtn.setEnabled(false);
            progressTmr.stop();

            System.out.println("filepanel::cancel");
//            cipherThread.interrupt();
            fileHandler.cancel();
        });

        progressTmr = new Timer(1000, e -> {
            int percent = fileHandler.getProgressInPercentage();
            progressBar.setValue(percent);
        });
    }

    private void addFiles(List<File> listOfFiles) {
        for (File f : listOfFiles)
            //Если файл еще не был добавлен в коллекцию, тогда добавить и вывести на таблицу
            if (!f.isDirectory() && !files.contains(f)) {
                files.add(f);
                ((DefaultTableModel) filesTbl.getModel()).addRow(new Object[]{f.getAbsolutePath(), getFileSize(f)});
            }
    }

    private String getFileSize(File f) {
        double megabytes = (double) f.length() / (1024 * 1024);
        return String.format("%.1f MB", megabytes);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isSelected()) return;

        try {
            Utilities.setKey(passFld.getPassword());
        } catch (IllegalArgumentException e1) {
            System.out.println("FilePanel::actionPerformed");
            e1.printStackTrace();

            return;
        }

        if (e.getSource() == encryptBtn) {
            fileHandler.init(files, getIndexesOfRows(), mode, ModeOf.ENCRYPTION);
        } else if (e.getSource() == decryptBtn) {
            fileHandler.init(files, getIndexesOfRows(), mode, ModeOf.DECRYPTION);
        }
        //нужно создавать новый поток, или блокировать его

        //запустить новый поток
        cipherThread = new Thread(fileHandler);
        cipherThread.start();

        //запустить таймер
        progressTmr.start();

        //поменять карту
        turn(true);
    }

    //возвращает true, если в коллекции только один файл или если
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

    public void setFileNameLbl(String text) {
        SwingUtilities.invokeLater(() -> {
            fileNameLbl.setText(text);
        });
    }

    //вызывается из потока шифрования файлов,
    //чтобы отобразить внесенные изменения
    public void apply(int[] indexes) {
        SwingUtilities.invokeLater(() -> {
            //обновить строки в таблице
            System.out.println("FilePanel::apply");

            for (int i : indexes) {
                System.out.println("updateTable: " + files.get(i).getAbsolutePath());
                filesTbl.getModel().setValueAt(files.get(i).getAbsolutePath(), i, 0);
            }

            turn(false);
        });
    }

    //устанавливает значения
    private void turn(boolean started) {
        cardLayout.next(this);
        stopBtn.setEnabled(started);
        container.setEnabled(!started);
        progressBar.setValue(0);
    }

    //если поток не завершен возвращает true, иначе false
    public boolean running() {
        return cipherThread.isAlive();
    }
}
