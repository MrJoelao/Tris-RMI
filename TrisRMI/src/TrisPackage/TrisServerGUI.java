package TrisPackage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TrisServerGUI extends JFrame {
    private JTextArea logArea;
    private JLabel statusLabel;
    private JButton startButton;
    private JButton stopButton;
    private Registry registry;
    private TrisServer server;
    private boolean isRunning;
    private JTextField portField;
    private int currentPort = 1099;
    private JButton restartButton;
    private static final Color BACKGROUND_COLOR = new Color(250, 250, 250);
    private static final Color HEADER_COLOR = new Color(70, 70, 70);
    private static final Color BUTTON_COLOR = new Color(100, 100, 100);
    private static final Color ACCENT_COLOR = new Color(120, 120, 120);
    private static final Color TEXT_COLOR = new Color(240, 240, 240);
    private static final Color BORDER_COLOR = new Color(200, 200, 200);
    
    public TrisServerGUI() {
        super("Tris Server RMI");
        setupGUI();
        isRunning = false;
    }
    
    private void setupGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setMinimumSize(new Dimension(600, 400));
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(HEADER_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Status Label
        statusLabel = new JLabel("Server Stopped", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        statusLabel.setForeground(TEXT_COLOR);
        headerPanel.add(statusLabel, BorderLayout.CENTER);
        
        // Port Panel
        JPanel portPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        portPanel.setOpaque(false);
        portField = new JTextField(String.valueOf(currentPort), 5);
        portField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        portField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        
        JLabel portLabel = new JLabel("Port: ");
        portLabel.setForeground(TEXT_COLOR);
        portLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JButton changePortButton = new JButton("Change Port");
        styleButton(changePortButton);
        
        portPanel.add(portLabel);
        portPanel.add(portField);
        portPanel.add(changePortButton);
        headerPanel.add(portPanel, BorderLayout.EAST);
        
        // Control Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        
        startButton = new JButton("Start Server");
        stopButton = new JButton("Stop Server");
        restartButton = new JButton("Restart Server");
        
        styleButton(startButton);
        styleButton(stopButton);
        styleButton(restartButton);
        
        stopButton.setEnabled(false);
        restartButton.setEnabled(false);
        
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(restartButton);
        
        // Log Area Panel
        JPanel logPanel = new JPanel(new BorderLayout(0, 5));
        logPanel.setBackground(BACKGROUND_COLOR);
        logPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel logLabel = new JLabel("Server Log");
        logLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logLabel.setForeground(HEADER_COLOR);
        logPanel.add(logLabel, BorderLayout.NORTH);
        
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        logArea.setBackground(Color.WHITE);
        logArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        logPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Main Content Panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.add(logPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        
        // Event Listeners
        startButton.addActionListener(e -> startServer());
        stopButton.addActionListener(e -> stopServer());
        restartButton.addActionListener(e -> restartServer());
        changePortButton.addActionListener(e -> changePort());
    }
    
    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setBackground(BUTTON_COLOR);
        button.setForeground(TEXT_COLOR);
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(ACCENT_COLOR);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(BUTTON_COLOR);
                }
            }
        });
    }
    
    private void updateServerStatus(boolean running) {
        isRunning = running;
        if (running) {
            statusLabel.setText("Server Running on port " + currentPort);
            statusLabel.setForeground(TEXT_COLOR);
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            restartButton.setEnabled(true);
        } else {
            statusLabel.setText("Server Stopped");
            statusLabel.setForeground(TEXT_COLOR);
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            restartButton.setEnabled(false);
        }
    }
    
    private void changePort() {
        try {
            int newPort = Integer.parseInt(portField.getText().trim());
            if (newPort < 1024 || newPort > 65535) {
                throw new NumberFormatException();
            }
            
            if (isRunning) {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Changing port requires server restart. Continue?",
                    "Confirm Port Change",
                    JOptionPane.YES_NO_OPTION);
                    
                if (confirm == JOptionPane.YES_OPTION) {
                    currentPort = newPort;
                    restartServer();
                } else {
                    portField.setText(String.valueOf(currentPort));
                }
            } else {
                currentPort = newPort;
                log("Port changed to: " + currentPort);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid port number (1024-65535)",
                "Invalid Port",
                JOptionPane.ERROR_MESSAGE);
            portField.setText(String.valueOf(currentPort));
        }
    }
    
    private void restartServer() {
        if (isRunning) {
            stopServer();
        }
        startServer();
        log("Server restarted on port: " + currentPort);
    }
    
    private void startServer() {
        try {
            registry = LocateRegistry.createRegistry(currentPort);
            server = new TrisServer();
            TrisServer.setGUI(this);
            registry.rebind("TrisServer", server);
            updateServerStatus(true);
            log("Server started successfully on port " + currentPort);
        } catch (RemoteException e) {
            log("Error starting server: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Error starting server: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void stopServer() {
        try {
            if (registry != null) {
                java.rmi.server.UnicastRemoteObject.unexportObject(registry, true);
                registry = null;
            }
            updateServerStatus(false);
            log("Server stopped");
        } catch (Exception e) {
            log("Error stopping server: " + e.getMessage());
        }
    }
    
    private void log(String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String timestamp = sdf.format(new Date());
        logArea.append(String.format("[%s] %s%n", timestamp, message));
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
    
    public void addToLog(String message) {
        log("Debug: " + message);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TrisServerGUI gui = new TrisServerGUI();
            gui.setVisible(true);
        });
    }
} 