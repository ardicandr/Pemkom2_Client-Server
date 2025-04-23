/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package socketprogramming;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class FileSenderGUI extends JFrame {
    private JTextField ipField, portField, fileField;
    private JButton browseBtn, sendBtn;

    private File selectedFile;

    public FileSenderGUI() {
        setTitle("File Sender");
        setSize(400, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2, 5, 5));

        ipField = new JTextField("127.0.0.1");
        portField = new JTextField("1234");
        fileField = new JTextField();
        fileField.setEditable(false);

        browseBtn = new JButton("Pilih File");
        sendBtn = new JButton("Kirim");

        add(new JLabel("IP Address:"));
        add(ipField);
        add(new JLabel("Port:"));
        add(portField);
        add(new JLabel("File:"));
        add(fileField);
        add(browseBtn);
        add(sendBtn);

        browseBtn.addActionListener(e -> pilihFile());
        sendBtn.addActionListener(e -> kirimFile());

        setVisible(true);
    }

    private void pilihFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            fileField.setText(selectedFile.getAbsolutePath());
        }
    }

    private void kirimFile() {
        String ip = ipField.getText();
        int port = Integer.parseInt(portField.getText());

        if (selectedFile == null) {
            JOptionPane.showMessageDialog(this, "Pilih file terlebih dahulu.");
            return;
        }

        try (Socket socket = new Socket(ip, port)) {
            FileInputStream fis = new FileInputStream(selectedFile);
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            dos.writeUTF(selectedFile.getName());
            dos.writeLong(selectedFile.length());

            byte[] buffer = new byte[4096];
            int read;
            while ((read = fis.read(buffer)) > 0) {
                dos.write(buffer, 0, read);
            }

            fis.close();
            dos.close();
            socket.close();

            JOptionPane.showMessageDialog(this, "File berhasil dikirim.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Gagal mengirim file: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FileSenderGUI::new);
    }
}
