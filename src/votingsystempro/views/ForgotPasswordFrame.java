package votingsystempro.views;

import votingsystempro.controllers.AuthController;
import votingsystempro.controllers.VoterController;
import votingsystempro.models.Voter;
import votingsystempro.utils.EmailUtil;
import votingsystempro.utils.OtpUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ForgotPasswordFrame extends JFrame {
    private JTextField voterIdField;
    private JButton sendButton;
    private JButton backButton;
    private JLabel messageLabel;
    private JPanel rightPanel;
    private JProgressBar progressBar;
    
    private AuthController authController;
    private VoterController voterController;
    
    // Modern premium color scheme
    private final Color GRADIENT_START = new Color(15, 23, 42);
    private final Color GRADIENT_END = new Color(30, 41, 59);
    private final Color PURE_WHITE = new Color(255, 255, 255);
    private final Color BLUE = new Color(59, 130, 246);
    private final Color BLUE_HOVER = new Color(37, 99, 235);
    private final Color TEXT_PRIMARY = new Color(17, 24, 39);
    private final Color TEXT_SECONDARY = new Color(107, 114, 128);
    private final Color CARD_BORDER = new Color(229, 231, 235);
    private final Color INPUT_BORDER = new Color(209, 213, 219);
    private final Color SUCCESS_GREEN = new Color(34, 197, 94);
    private final Color RED = new Color(239, 68, 68);
    
    // Flag to prevent multiple OTP sends
    private boolean isSendingOtp = false;
    
    public ForgotPasswordFrame() {
        authController = new AuthController();
        voterController = new VoterController();
        initComponents();
        applyModernEffects();
    }
    
    private void applyModernEffects() {
        setUndecorated(true);
        try {
            setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
        } catch (Exception e) {
            // Ignore
        }
        
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) {
                try {
                    setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
                } catch (Exception ex) {
                    // Ignore
                }
            }
        });
    }
    
    private void initComponents() {
        setTitle("Forgot Password - Election Commission of Nepal");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout()); // Simple BorderLayout
        
        // Create main split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(350); // Left panel width
        splitPane.setDividerSize(0); // No visible divider
        splitPane.setBorder(null);
        
        // Left Panel - Branding (35%)
        JPanel leftPanel = createLeftPanel();
        leftPanel.setPreferredSize(new Dimension(350, 600));
        
        // Right Panel - Action (65%)
        rightPanel = createRightPanel();
        rightPanel.setPreferredSize(new Dimension(550, 600));
        
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        
        add(splitPane, BorderLayout.CENTER);
        
        setSize(900, 600);
        setLocationRelativeTo(null);
    }
    
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Deep gradient background
                GradientPaint gp = new GradientPaint(0, 0, GRADIENT_START, 
                    getWidth(), getHeight(), GRADIENT_END);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                g2.dispose();
            }
        };
        panel.setLayout(new GridBagLayout());
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 0, 10, 0);
        
        // Large icon
        JLabel iconLabel = new JLabel("🔐");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 100));
        iconLabel.setForeground(Color.WHITE);
        panel.add(iconLabel, gbc);
        
        // Title
        JLabel titleLabel = new JLabel("Secure Account Recovery");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, gbc);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Reset your password securely");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));
        panel.add(subtitleLabel, gbc);
        
        return panel;
    }
    
    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PURE_WHITE);
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        
        // Header
        JLabel headerLabel = new JLabel("Reset Password");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        headerLabel.setForeground(TEXT_PRIMARY);
        panel.add(headerLabel, gbc);
        
        JLabel subHeaderLabel = new JLabel("Enter your Voter ID to receive OTP");
        subHeaderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subHeaderLabel.setForeground(TEXT_SECONDARY);
        gbc.insets = new Insets(5, 0, 25, 0);
        panel.add(subHeaderLabel, gbc);
        
        gbc.insets = new Insets(8, 0, 8, 0);
        
        // Voter ID Field with icon
        JPanel inputWrapper = new JPanel(new BorderLayout(10, 0));
        inputWrapper.setOpaque(false);
        
        JLabel iconLabel = new JLabel("🆔");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        iconLabel.setForeground(TEXT_SECONDARY);
        
        voterIdField = new JTextField(15);
        voterIdField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        voterIdField.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, INPUT_BORDER));
        voterIdField.setBackground(PURE_WHITE);
        voterIdField.setForeground(TEXT_PRIMARY);
        
        voterIdField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                voterIdField.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BLUE));
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                voterIdField.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, INPUT_BORDER));
            }
        });
        
        inputWrapper.add(iconLabel, BorderLayout.WEST);
        inputWrapper.add(voterIdField, BorderLayout.CENTER);
        panel.add(inputWrapper, gbc);
        
        // Info Card
        JPanel infoCard = createInfoCard();
        gbc.insets = new Insets(15, 0, 15, 0);
        panel.add(infoCard, gbc);
        
        // Progress Bar
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
        progressBar.setPreferredSize(new Dimension(350, 3));
        progressBar.setForeground(BLUE);
        progressBar.setBackground(new Color(229, 231, 235));
        panel.add(progressBar, gbc);
        
        // Message Label
        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setForeground(RED);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(messageLabel, gbc);
        
        // Send OTP Button
        sendButton = new JButton("Send OTP");
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        sendButton.setForeground(Color.WHITE);
        sendButton.setBackground(BLUE);
        sendButton.setFocusPainted(false);
        sendButton.setBorderPainted(false);
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendButton.setPreferredSize(new Dimension(350, 45));
        sendButton.addActionListener(e -> processForgotPassword());
        panel.add(sendButton, gbc);
        
        // Back to Login Button
        backButton = new JButton("← Back to Login");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        backButton.setForeground(TEXT_SECONDARY);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        
        backButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                backButton.setForeground(BLUE);
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                backButton.setForeground(TEXT_SECONDARY);
            }
        });
        
        gbc.insets = new Insets(15, 0, 5, 0);
        panel.add(backButton, gbc);
        
        return panel;
    }
    
    private JPanel createInfoCard() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(249, 250, 251));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235, 100), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 0, 3, 0);
        
        JLabel iconLabel = new JLabel("📧", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        iconLabel.setForeground(BLUE);
        panel.add(iconLabel, gbc);
        
        JLabel infoLabel = new JLabel(
            "<html><div style='text-align: center; width: 250px;'>" +
            "A 6-digit OTP will be sent to your registered email. " +
            "The OTP expires in 5 minutes.</div></html>"
        );
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoLabel.setForeground(TEXT_SECONDARY);
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(infoLabel, gbc);
        
        return panel;
    }
    
    private void showMessage(String message, Color color) {
        SwingUtilities.invokeLater(() -> {
            messageLabel.setText(message);
            messageLabel.setForeground(color);
        });
    }
    
    private void showProgress(boolean show) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setVisible(show);
        });
    }
    
    private void processForgotPassword() {
        if (isSendingOtp) {
            return;
        }
        
        String voterId = voterIdField.getText().trim().toUpperCase();
        
        if (voterId.isEmpty()) {
            showMessage("Please enter Voter ID", RED);
            voterIdField.requestFocus();
            return;
        }
        
        isSendingOtp = true;
        sendButton.setEnabled(false);
        showProgress(true);
        showMessage("Searching for voter...", BLUE);
        
        SwingWorker<Voter, Void> worker = new SwingWorker<Voter, Void>() {
            @Override
            protected Voter doInBackground() throws Exception {
                Thread.sleep(500);
                return findVoterByLoginId(voterId);
            }
            
            @Override
            protected void done() {
                try {
                    Voter voter = get();
                    
                    if (voter == null) {
                        showMessage("Voter ID not found", RED);
                        resetSendButton();
                        return;
                    }
                    
                    if (voter.getEmail() == null || voter.getEmail().isEmpty()) {
                        showMessage("No email registered", RED);
                        resetSendButton();
                        return;
                    }
                    
                    sendSingleOtp(voter);
                    
                } catch (InterruptedException | ExecutionException e) {
                    showMessage("Error: " + e.getMessage(), RED);
                    resetSendButton();
                }
            }
        };
        worker.execute();
    }
    
    private void resetSendButton() {
        isSendingOtp = false;
        sendButton.setEnabled(true);
        showProgress(false);
    }
    
private void sendSingleOtp(Voter voter) {
    // Generate ONE OTP
    String otp = OtpUtil.generateOtp();
    OtpUtil.storeOtp(voter.getEmail(), otp);
    
    System.out.println("=================================");
    System.out.println("🔐 OTP GENERATED: " + otp);
    System.out.println("📧 Sending to: " + voter.getEmail());
    System.out.println("=================================");
    
    // HTML FORMAT - राम्रो design सहित
    String emailContent = 
        "<!DOCTYPE html>" +
        "<html>" +
        "<head>" +
        "<meta charset='UTF-8'>" +
        "<style>" +
        "body { font-family: 'Segoe UI', Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px; }" +
        ".container { max-width: 500px; margin: 0 auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 5px 15px rgba(0,0,0,0.1); }" +
        ".header { background: #2c3e50; color: white; padding: 25px; text-align: center; }" +
        ".header h1 { margin: 0; font-size: 24px; }" +
        ".header p { margin: 5px 0 0; font-size: 14px; opacity: 0.9; }" +
        ".content { padding: 30px; }" +
        ".title { font-size: 20px; font-weight: bold; color: #2c3e50; margin-bottom: 20px; }" +
        ".otp-box { background: #f8f9fa; border: 2px dashed #3498db; padding: 20px; text-align: center; margin: 20px 0; }" +
        ".otp-code { font-size: 48px; font-weight: bold; color: #3498db; letter-spacing: 5px; }" +
        ".divider { border-top: 2px solid #ecf0f1; margin: 25px 0; }" +
        ".expiry { color: #e74c3c; font-weight: bold; margin: 15px 0; }" +
        ".footer { background: #ecf0f1; padding: 20px; text-align: center; font-size: 13px; color: #7f8c8d; }" +
        "</style>" +
        "</head>" +
        "<body>" +
        "<div class='container'>" +
        
        // Header
        "<div class='header'>" +
        "<h1>🇳🇵 Election Commission of Nepal</h1>" +
        "<p>निर्वाचन आयोग, नेपाल</p>" +
        "</div>" +
        
        // Content
        "<div class='content'>" +
        "<div class='title'>🔐 Password Reset OTP</div>" +
        
        "<p>Dear <strong>" + voter.getFullName() + "</strong>,</p>" +
        "<p>We received a request to reset your password.</p>" +
        
        "<p>Your OTP is:</p>" +
        
        // OTP Box
        "<div class='otp-box'>" +
        "<div class='otp-code'>" + otp + "</div>" +
        "</div>" +
        
        "<div class='divider'></div>" +
        
        // Expiry
        "<div class='expiry'>🔒 This OTP will expire in 5 minutes</div>" +
        
        "<p style='color: #7f8c8d; font-size: 13px;'>If you didn't request this, please ignore this email.</p>" +
        "</div>" +
        
        // Footer
        "<div class='footer'>" +
        "Election Commission of Nepal<br>" +
        "संघीय निर्वाचन आयोग, नेपाल" +
        "</div>" +
        
        "</div>" +
        "</body>" +
        "</html>";
    
    // Send ONE email only
    boolean sent = EmailUtil.sendCustomEmail(
        voter.getEmail(),
        "🔐 Password Reset OTP - Election Commission of Nepal",
        emailContent
    );
    
    if (sent) {
        JOptionPane.showMessageDialog(this,
            "✅ OTP sent to:\n" + voter.getEmail(),
            "OTP Sent",
            JOptionPane.INFORMATION_MESSAGE);
        
        new OtpVerificationFrame(voter).setVisible(true);
        dispose();
    } else {
        showMessage("❌ Failed to send OTP. Check email configuration.", RED);
        resetSendButton();
        
        // Fallback - plain text
        JOptionPane.showMessageDialog(this,
            "📧 EMAIL FAILED - USE THIS OTP\n\n" + otp,
            "OTP",
            JOptionPane.WARNING_MESSAGE);
        
        new OtpVerificationFrame(voter).setVisible(true);
        dispose();
    }
}
    
    private Voter findVoterByLoginId(String loginId) {
        List<Voter> allVoters = voterController.getAllVoters();
        if (allVoters != null) {
            for (Voter voter : allVoters) {
                String voterLoginId = "VOT" + voter.getUserId();
                if (voterLoginId.equals(loginId)) {
                    return voter;
                }
            }
        }
        return null;
    }
    
    @Override
    public void dispose() {
        isSendingOtp = false;
        super.dispose();
    }
}