package mobiarmy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import mobiarmy.server.Server;

public class MobiArmy extends JFrame {

    private Server server;
    private JButton startButton;
    private JButton stopButton;
    private JLabel statusLabel;
    private JLabel connectionLabel;
    private Thread serverThread;

    public MobiArmy() {
        // Thiết lập frame
        setTitle("Server Manager");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Tạo giao diện
        createUI();

        // Khởi tạo server (cấu hình cổng mặc định 8122)
        server = new Server(8122);
    }

    private void createUI() {
        // Khởi tạo các thành phần
        startButton = new JButton("Start Server");
        stopButton = new JButton("Stop Server");
        stopButton.setEnabled(false); // Ban đầu nút dừng server không hoạt động

        statusLabel = new JLabel("Server status: Stopped");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        connectionLabel = new JLabel("Connected clients: 0");
        connectionLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Thiết lập sự kiện cho nút Start
        startButton.addActionListener((ActionEvent e) -> {
            startServer();
        });

        // Thiết lập sự kiện cho nút Stop
        stopButton.addActionListener((ActionEvent e) -> {
            stopServer();
        });

        // Layout các thành phần trong panel chính
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10));
        panel.add(statusLabel);
        panel.add(connectionLabel);
        panel.add(startButton);
        panel.add(stopButton);

        // Thêm panel vào frame
        add(panel);
    }

    // Hàm khởi động server trong luồng riêng
    private void startServer() {
        serverThread = new Thread(() -> {
            try {
                server.start();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        serverThread.start();
        statusLabel.setText("Server status: Running");

        // Cập nhật nút
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
    }

    // Hàm dừng server
    private void stopServer() {
        server.stop();
        try {
            serverThread.join(); // Chờ luồng server kết thúc
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        statusLabel.setText("Server status: Stopped");
        connectionLabel.setText("Connected clients: 0");
        stopButton.setEnabled(false);
        startButton.setEnabled(true);
    }

    public static void main(String[] args) {
        // Tạo và hiển thị giao diện
        SwingUtilities.invokeLater(() -> {
            MobiArmy manager = new MobiArmy();
            manager.setVisible(true);
        });
    }
}
