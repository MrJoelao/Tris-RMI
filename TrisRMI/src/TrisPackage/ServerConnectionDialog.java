package TrisPackage;

import javax.swing.*;
import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerConnectionDialog extends JDialog {
    // Colori neutri coerenti con il server
    private static final Color BACKGROUND_COLOR = new Color(250, 250, 250);
    private static final Color HEADER_COLOR = new Color(70, 70, 70);
    private static final Color BUTTON_COLOR = new Color(100, 100, 100);
    private static final Color ACCENT_COLOR = new Color(120, 120, 120);
    private static final Color TEXT_COLOR = new Color(240, 240, 240);
    private static final Color BORDER_COLOR = new Color(200, 200, 200);
    
    private JLabel statusLabel;
    private JProgressBar progressBar;
    private JButton retryButton;
    private JButton exitButton;
    private final Timer connectionTimer;
    private TrisServerInterface server;
    private final AtomicBoolean isConnected = new AtomicBoolean(false);
    private JTextField ipField;
    private JTextField portField;
    private String currentIp = "localhost";
    private int currentPort = 1099;
    
    // Messaggi di stato predefiniti
    private static final String MSG_CONNECTING = "Connecting to server...";
    private static final String MSG_INVALID_PORT = "Invalid port number (1024-65535)";
    private static final String MSG_CONNECTION_FAILED = "Connection failed";
    private static final String MSG_SERVER_NOT_FOUND = "Server not found";
    private static final String MSG_NETWORK_ERROR = "Network error";
    private static final String MSG_INVALID_IP = "Invalid IP address";
    
    private JPanel connectionPanel;
    private JButton menuButton;
    private boolean isMenuExpanded = false;
    
    public ServerConnectionDialog(JFrame parent) {
        super(parent, "Server Connection", true);
        setupGUI();
        
        // Aggiungi listener per la X
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.exit(0);
            }
        });
        
        connectionTimer = new Timer(2000, e -> tryConnection());
        connectionTimer.setRepeats(false);
    }
    
    private void setupGUI() {
        setLayout(new BorderLayout());
        setSize(400, 200);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel("Server Connection", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_COLOR);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Menu Button (Hamburger)
        menuButton = new JButton("\u2630"); // Unicode per il simbolo hamburger
        menuButton.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        menuButton.setForeground(TEXT_COLOR);
        menuButton.setBackground(null);
        menuButton.setBorder(null);
        menuButton.setFocusPainted(false);
        menuButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        menuButton.addActionListener(e -> toggleConnectionPanel());
        headerPanel.add(menuButton, BorderLayout.EAST);
        
        // Connection Panel (inizialmente nascosto)
        connectionPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        connectionPanel.setBackground(HEADER_COLOR);
        connectionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        connectionPanel.setVisible(false);
        
        // IP Field
        JLabel ipLabel = new JLabel("IP:");
        ipLabel.setForeground(TEXT_COLOR);
        ipLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        ipField = new JTextField(currentIp, 12);
        styleTextField(ipField);
        
        // Port Field
        JLabel portLabel = new JLabel("Port:");
        portLabel.setForeground(TEXT_COLOR);
        portLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        portField = new JTextField(String.valueOf(currentPort), 5);
        styleTextField(portField);
        
        connectionPanel.add(ipLabel);
        connectionPanel.add(ipField);
        connectionPanel.add(portLabel);
        connectionPanel.add(portField);
        
        // Aggiungi il pannello di connessione sotto l'header
        add(connectionPanel, BorderLayout.CENTER);
        
        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        
        // Status Panel
        JPanel statusPanel = new JPanel(new BorderLayout(10, 10));
        statusPanel.setOpaque(false);
        
        statusLabel = new JLabel("Connecting to server...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusPanel.add(statusLabel, BorderLayout.NORTH);
        
        // Progress Bar con stile personalizzato
        setupProgressBar();
        
        JPanel progressPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        progressPanel.setOpaque(false);
        progressPanel.add(progressBar);
        statusPanel.add(progressPanel, BorderLayout.CENTER);
        
        mainPanel.add(statusPanel, BorderLayout.CENTER);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);
        
        retryButton = new JButton("Retry Connection");
        styleButton(retryButton);
        retryButton.setEnabled(false);
        retryButton.addActionListener(e -> retryConnection());
        
        exitButton = new JButton("Exit");
        styleButton(exitButton);
        exitButton.addActionListener(e -> System.exit(0));
        
        buttonPanel.add(retryButton);
        buttonPanel.add(exitButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.SOUTH);
    }
    
    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setBackground(BUTTON_COLOR);
        button.setForeground(TEXT_COLOR);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(ACCENT_COLOR);
                }
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(BUTTON_COLOR);
                }
            }
        });
    }
    
    private void setupProgressBar() {
        final int[] animationIndex = {0};
        Timer animationTimer = new Timer(50, e -> {
            animationIndex[0]++;
            progressBar.repaint();
        });
        
        progressBar = new JProgressBar() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Sfondo della barra
                g2d.setColor(BORDER_COLOR);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                if (isIndeterminate()) {
                    // Animazione indeterminata
                    g2d.setColor(HEADER_COLOR);
                    int width = getWidth() / 3;
                    int x = (int) ((getWidth() - width) * (Math.sin(animationIndex[0] * 0.1) + 1) / 2);
                    g2d.fillRoundRect(x, 0, width, getHeight(), 8, 8);
                } else {
                    // Barra determinata
                    int width = (int) ((getWidth() - 4) * getPercentComplete());
                    if (width > 0) {
                        g2d.setColor(HEADER_COLOR);
                        g2d.fillRoundRect(2, 2, width, getHeight() - 4, 6, 6);
                    }
                }
                
                g2d.dispose();
            }
        };
        
        progressBar.setPreferredSize(new Dimension(300, 4));
        progressBar.setBorderPainted(false);
        progressBar.setOpaque(false);
        progressBar.setIndeterminate(true);
        animationTimer.start();
    }
    
    private void updateStatus(String message) {
        Timer fadeTimer = new Timer(20, null);
        final float[] alpha = {1.0f};
        
        fadeTimer.addActionListener(e -> {
            alpha[0] -= 0.1f;
            if (alpha[0] <= 0.0f) {
                statusLabel.setText(message);
                fadeTimer.stop();
                
                Timer fadeInTimer = new Timer(20, e2 -> {
                    alpha[0] += 0.1f;
                    statusLabel.setForeground(new Color(
                        HEADER_COLOR.getRed(),
                        HEADER_COLOR.getGreen(),
                        HEADER_COLOR.getBlue(),
                        (int)(alpha[0] * 255)
                    ));
                    if (alpha[0] >= 1.0f) {
                        ((Timer)e2.getSource()).stop();
                    }
                });
                fadeInTimer.start();
            }
            statusLabel.setForeground(new Color(
                HEADER_COLOR.getRed(),
                HEADER_COLOR.getGreen(),
                HEADER_COLOR.getBlue(),
                (int)(alpha[0] * 255)
            ));
        });
        fadeTimer.start();
    }
    
    private void retryConnection() {
        updateStatus(MSG_CONNECTING);
        progressBar.setIndeterminate(true);
        retryButton.setEnabled(false);
        connectionTimer.restart();
    }
    
    private void tryConnection() {
        try {
            // Valida e leggi l'IP
            currentIp = ipField.getText().trim();
            if (currentIp.isEmpty()) {
                currentIp = "localhost";
                ipField.setText(currentIp);
            } else if (!isValidIpAddress(currentIp) && !currentIp.equals("localhost")) {
                updateStatus(MSG_INVALID_IP);
                progressBar.setIndeterminate(false);
                retryButton.setEnabled(true);
                return;
            }
            
            // Valida e leggi la porta
            try {
                currentPort = Integer.parseInt(portField.getText().trim());
                if (currentPort < 1024 || currentPort > 65535) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                updateStatus(MSG_INVALID_PORT);
                progressBar.setIndeterminate(false);
                retryButton.setEnabled(true);
                return;
            }
            
            updateStatus(MSG_CONNECTING);
            
            Registry registry = LocateRegistry.getRegistry(currentIp, currentPort);
            server = (TrisServerInterface) registry.lookup("TrisServer");
            
            // Test connection
            server.getAllPlayerStats();
            
            isConnected.set(true);
            dispose();
            
        } catch (java.rmi.NotBoundException e) {
            updateStatus(MSG_SERVER_NOT_FOUND);
            progressBar.setIndeterminate(false);
            retryButton.setEnabled(true);
        } catch (java.rmi.ConnectException e) {
            updateStatus(MSG_CONNECTION_FAILED);
            progressBar.setIndeterminate(false);
            retryButton.setEnabled(true);
        } catch (Exception e) {
            updateStatus(MSG_NETWORK_ERROR);
            progressBar.setIndeterminate(false);
            retryButton.setEnabled(true);
        }
    }
    
    private boolean isValidIpAddress(String ip) {
        if (ip == null || ip.isEmpty()) return false;
        String[] parts = ip.split("\\.");
        if (parts.length != 4) return false;
        
        try {
            for (String part : parts) {
                int value = Integer.parseInt(part);
                if (value < 0 || value > 255) return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        
        return true;
    }
    
    public TrisServerInterface getServer() {
        return server;
    }
    
    public static TrisServerInterface waitForServer(JFrame parent) {
        ServerConnectionDialog dialog = new ServerConnectionDialog(parent);
        dialog.connectionTimer.start();
        dialog.setVisible(true); // Blocca finché non viene chiamato dispose()
        
        return dialog.getServer();
    }
    
    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(new Color(60, 60, 60));
        field.setForeground(TEXT_COLOR);
        field.setCaretColor(TEXT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
    }
    
    private void toggleConnectionPanel() {
        isMenuExpanded = !isMenuExpanded;
        connectionPanel.setVisible(isMenuExpanded);
        
        // Anima il bottone
        menuButton.setText(isMenuExpanded ? "\u2715" : "\u2630"); // × o ≡
        
        // Ricalcola la dimensione della finestra
        if (isMenuExpanded) {
            setSize(getWidth(), getHeight() + connectionPanel.getPreferredSize().height);
        } else {
            setSize(getWidth(), getHeight() - connectionPanel.getPreferredSize().height);
        }
    }
} 