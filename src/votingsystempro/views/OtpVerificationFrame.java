package votingsystempro.views;

import votingsystempro.models.Voter;
import votingsystempro.utils.EmailUtil;
import votingsystempro.utils.OtpUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

public class OtpVerificationFrame extends JFrame {
    private Voter voter;
    private JTextField otpField;
    private JButton verifyButton;
    private JButton resendButton;
    private JButton cancelButton;
    private JLabel messageLabel;
    private JLabel timerLabel;
    private Timer timer;
    private int timeLeft = 300; // 5 minutes
    private JPanel cardPanel;
    
    // Modern colors
    private final Color SIDEBAR_BG = new Color(15, 23, 42);
    private final Color SIDEBAR_HOVER = new Color(30, 41, 59);
    private final Color CONTENT_BG = new Color(248, 250, 252);
    private final Color CARD_BORDER = new Color(226, 232, 240);
    private final Color BLUE = new Color(59, 130, 246);
    private final Color GREEN = new Color(34, 197, 94);
    private final Color RED = new Color(239, 68, 68);
    private final Color AMBER = new Color(245, 158, 11);
    private final Color TEXT_PRIMARY = new Color(17, 24, 39);
    private final Color TEXT_SECONDARY = new Color(107, 114, 128);
    
    public OtpVerificationFrame(Voter voter) {
        this.voter = voter;
        initComponents();
        startTimer();
        applyModernEffects();
    }
    
    private void applyModernEffects() {
        setUndecorated(true);
        try {
            setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
        } catch (Exception e) {
            // Ignore
        }
    }
    
    private void initComponents() {
        setTitle("OTP Verification");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Custom title bar
        JPanel titleBar = createTitleBar();
        add(titleBar, BorderLayout.NORTH);
        
        // Main content with clean background
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(CONTENT_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        // Floating card with drop shadow
        cardPanel = createFloatingCard();
        cardPanel.setLayout(new GridBagLayout());
        cardPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 15, 12, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        
        // ===== HEADER SECTION =====
        // Icon
        JLabel iconLabel = new JLabel("📧", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        iconLabel.setForeground(BLUE);
        cardPanel.add(iconLabel, gbc);
        
        // Header: "Verify Identity"
        JLabel headerLabel = new JLabel("Verify Identity", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setForeground(TEXT_PRIMARY);
        cardPanel.add(headerLabel, gbc);
        
        // ===== INSTRUCTION SECTION =====
        JLabel instructionLabel = new JLabel(
            "Enter the code sent to your email", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        instructionLabel.setForeground(TEXT_SECONDARY);
        cardPanel.add(instructionLabel, gbc);
        
        // Email info
        JLabel infoLabel = new JLabel(
            "<html><div style='text-align: center;'>" +
            "📧 " + maskEmail(voter.getEmail()) + "</div></html>",
            SwingConstants.CENTER
        );
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoLabel.setForeground(TEXT_SECONDARY);
        cardPanel.add(infoLabel, gbc);
        
        // ===== HERO SECTION - Massive OTP Field =====
        JPanel otpWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        otpWrapper.setOpaque(false);
        
        otpField = new JTextField(6);
        otpField.setFont(new Font("Monospaced", Font.BOLD, 42));
        otpField.setForeground(TEXT_PRIMARY);
        otpField.setHorizontalAlignment(JTextField.CENTER);
        otpField.setOpaque(false);
        otpField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 3, 0, BLUE),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        otpField.setPreferredSize(new Dimension(300, 70));
        
        // Add focus listener for bottom line
        otpField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                otpField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 3, 0, BLUE),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                otpField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 3, 0, new Color(200, 200, 200)),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            }
        });
        
        otpWrapper.add(otpField);
        cardPanel.add(otpWrapper, gbc);
        
        // Timer with dynamic color
        timerLabel = new JLabel("⏰ 05:00 remaining", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        timerLabel.setForeground(BLUE);
        cardPanel.add(timerLabel, gbc);
        
        // Message Label
        messageLabel = new JLabel(" ", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setForeground(RED);
        cardPanel.add(messageLabel, gbc);
        
        // ===== FOOTER - Buttons =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);
        
        verifyButton = createStyledButton("VERIFY", GREEN, 140, 45);
        verifyButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        verifyButton.addActionListener(e -> verifyOtp());
        
        resendButton = createStyledButton("RESEND", BLUE, 140, 45);
        resendButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        resendButton.addActionListener(e -> resendOtp());
        
        cancelButton = createStyledButton("BACK", SIDEBAR_BG, 140, 45);
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        cancelButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                new ForgotPasswordFrame().setVisible(true);
                dispose();
            });
        });
        
        buttonPanel.add(verifyButton);
        buttonPanel.add(resendButton);
        buttonPanel.add(cancelButton);
        cardPanel.add(buttonPanel, gbc);
        
        mainPanel.add(cardPanel);
        add(mainPanel, BorderLayout.CENTER);
        
        // Enter key listener
        otpField.addActionListener(e -> verifyOtp());
        
        setSize(600, 700);
        setLocationRelativeTo(null);
    }
    
    private JPanel createFloatingCard() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // White background
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Soft drop shadow (simulated)
                for (int i = 0; i < 5; i++) {
                    g2.setColor(new Color(0, 0, 0, 5));
                    g2.drawRoundRect(i, i, getWidth() - i * 2 - 1, 
                        getHeight() - i * 2 - 1, 20 - i, 20 - i);
                }
                
                g2.dispose();
            }
        };
    }
    
    private JPanel createTitleBar() {
        JPanel titleBar = new JPanel();
        titleBar.setBackground(SIDEBAR_BG);
        titleBar.setPreferredSize(new Dimension(0, 45));
        titleBar.setLayout(new BorderLayout());
        titleBar.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        leftPanel.setOpaque(false);
        
        JLabel logoLabel = new JLabel("🔐");
        logoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        logoLabel.setForeground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Secure Verification");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        
        leftPanel.add(logoLabel);
        leftPanel.add(titleLabel);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        rightPanel.setOpaque(false);
        
        JButton minimizeBtn = createWindowButton("−");
        minimizeBtn.addActionListener(e -> setState(JFrame.ICONIFIED));
        
        JButton closeBtn = createWindowButton("×");
        closeBtn.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                new LoginFrame().setVisible(true);
                dispose();
            });
        });
        
        rightPanel.add(minimizeBtn);
        rightPanel.add(closeBtn);
        
        titleBar.add(leftPanel, BorderLayout.WEST);
        titleBar.add(rightPanel, BorderLayout.EAST);
        
        return titleBar;
    }
    
    private JButton createWindowButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        button.setForeground(Color.WHITE);
        button.setBackground(SIDEBAR_BG);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(40, 30));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(SIDEBAR_HOVER);
                button.setOpaque(true);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(SIDEBAR_BG);
                button.setOpaque(false);
            }
        });
        
        return button;
    }
    
    private JButton createStyledButton(String text, Color bgColor, int width, int height) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                // Subtle shadow
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 8, 8);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(width, height));
        
        return button;
    }
    
    private String maskEmail(String email) {
        if (email == null || email.isEmpty()) return "N/A";
        String[] parts = email.split("@");
        if (parts.length != 2) return email;
        String name = parts[0];
        if (name.length() <= 2) return email;
        return name.substring(0, 2) + "***@" + parts[1];
    }
    
    private void startTimer() {
        timer = new Timer(1000, e -> {
            timeLeft--;
            int minutes = timeLeft / 60;
            int seconds = timeLeft % 60;
            timerLabel.setText(String.format("⏰ %02d:%02d remaining", minutes, seconds));
            
            // Dynamic color transition
            if (timeLeft < 60) {
                timerLabel.setForeground(RED);
            } else if (timeLeft < 180) {
                timerLabel.setForeground(AMBER);
            } else {
                timerLabel.setForeground(BLUE);
            }
            
            if (timeLeft <= 0) {
                timer.stop();
                timerLabel.setText("⏰ OTP Expired!");
                timerLabel.setForeground(RED);
                otpField.setEnabled(false);
                verifyButton.setEnabled(false);
                showMessage("⚠️ OTP Expired. Please resend.", RED);
            }
        });
        timer.start();
    }
    
    private void resendOtp() {
        int choice = JOptionPane.showConfirmDialog(this,
            "To resend OTP, please go back and click 'SEND OTP' again.\n\n" +
            "Do you want to go back now?",
            "Resend OTP",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) {
            SwingUtilities.invokeLater(() -> {
                new ForgotPasswordFrame().setVisible(true);
                dispose();
            });
        }
    }
    
    private void verifyOtp() {
        String enteredOtp = otpField.getText().trim();
        
        if (enteredOtp.isEmpty()) {
            showMessage("Please enter OTP", RED);
            shakePanel();
            return;
        }
        
        boolean isValid = OtpUtil.validateOtp(voter.getEmail(), enteredOtp);
        System.out.println("OTP validation: " + isValid);
        
        if (isValid) {
            // Success - smooth transition
            timer.stop();
            showMessage("✅ Verified! Redirecting...", GREEN);
            
            Timer successTimer = new Timer(1000, evt -> {
                SwingUtilities.invokeLater(() -> {
                    new ResetPasswordFrame(voter.getUserId()).setVisible(true);
                    dispose();
                });
            });
            successTimer.setRepeats(false);
            successTimer.start();
            
        } else {
            showMessage("❌ Invalid OTP. Please try again.", RED);
            shakePanel();
        }
    }
    
    private void shakePanel() {
        final int originalX = cardPanel.getX();
        final int originalY = cardPanel.getY();
        
        Timer shakeTimer = new Timer(50, new ActionListener() {
            int count = 0;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (count < 6) {
                    int delta = (count % 2 == 0) ? 5 : -5;
                    cardPanel.setLocation(cardPanel.getX() + delta, cardPanel.getY());
                    count++;
                } else {
                    cardPanel.setLocation(originalX, originalY);
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        shakeTimer.start();
    }
    
    private void showMessage(String message, Color color) {
        messageLabel.setText(message);
        messageLabel.setForeground(color);
    }
}