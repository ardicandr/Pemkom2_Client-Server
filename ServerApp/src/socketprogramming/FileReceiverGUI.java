/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package socketprogramming;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class FileReceiverGUI extends JFrame {
    private JTextField portField, folderField;
    private JButton pilihFolderBtn, mulaiBtn;
    private JTextArea logArea;
    private File saveDirectory;

    public FileReceiverGUI() {
        setTitle("File Receiver");
        setSize(500, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel input atas
        JPanel topPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        portField = new JTextField("1234");
        folderField = new JTextField();
        folderField.setEditable(false);
        pilihFolderBtn = new JButton("Pilih Folder");
        mulaiBtn = new JButton("Mulai Server");

        topPanel.add(new JLabel("Port:"));
        topPanel.add(portField);
        topPanel.add(new JLabel("Folder Simpan:"));
        topPanel.add(folderField);
        topPanel.add(pilihFolderBtn);
        topPanel.add(mulaiBtn);

        add(topPanel, BorderLayout.NORTH);

        // Area log
        logArea = new JTextArea();
        logArea.setEditable(false);
        add(new JScrollPane(logArea), BorderLayout.CENTER);

        // Action listeners
        pilihFolderBtn.addActionListener(e -> pilihFolder());
        mulaiBtn.addActionListener(e -> mulaiServer());

        setVisible(true);
    }

    private void pilihFolder() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            saveDirectory = chooser.getSelectedFile();
            folderField.setText(saveDirectory.getAbsolutePath());
        }
    }

    private void mulaiServer() {
        if (saveDirectory == null) {
            JOptionPane.showMessageDialog(this, "Pilih folder penyimpanan terlebih dahulu.");
            return;
        }

        int port = Integer.parseInt(portField.getText());

        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                log("Menunggu koneksi di port " + port + "...");

                while (true) {
                    Socket socket = serverSocket.accept();
                    log("Terhubung dengan: " + socket.getInetAddress());

                    try (DataInputStream dis = new DataInputStream(socket.getInputStream())) {
                        String fileName = dis.readUTF();
                        long fileSize = dis.readLong();

                        File outputFile = new File(saveDirectory, "received_" + fileName);
                        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                            byte[] buffer = new byte[4096];
                            int read;
                            long remaining = fileSize;

                            while ((read = dis.read(buffer, 0, (int) Math.min(buffer.length, remaining))) > 0) {
                                fos.write(buffer, 0, read);
                                remaining -= read;
                            }
                        }

                        log("File diterima: " + fileName);
                    } catch (IOException e) {
                        log("Gagal menerima file: " + e.getMessage());
                    }

                    socket.close();
                }

            } catch (IOException ex) {
                log("Server error: " + ex.getMessage());
            }
        }).start();
    }

    private void log(String message) {
        SwingUtilities.invokeLater(() -> logArea.append(message + "\n"));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FileReceiverGUI::new);
    }
}
