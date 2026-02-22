package votingsystempro.views;

import votingsystempro.controllers.AuthController;
import votingsystempro.database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.sql.PreparedStatement;

public class ResetPasswordFrame extends JFrame {
    private int userId;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JButton resetButton;
    private JButton cancelButton;
    private JLabel messageLabel;
    private JCheckBox showPasswordCheckBox;
    
    private AuthController authController;
    
    // Modern colors
    private final Color SIDEBAR_BG = new Color(15, 23, 42);
    private final Color SIDEBAR_HOVER = new Color(30, 41, 59);
    private final Color CONTENT_BG = new Color(248, 250, 252);
    private final Color CARD_BORDER = new Color(226, 232, 240);
    private final Color TEXT_PRIMARY = new Color(15, 23, 42);
    private final Color TEXT_SECONDARY = new Color(71, 85, 105);
    private final Color BLUE = new Color(59, 130, 246);
    private final Color GREEN = new Color(34, 197, 94);
    private final Color RED = new Color(239, 68, 68);
    
    public ResetPasswordFrame(int userId) {
        this.userId = userId;
        this.authController = new AuthController();
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
    }
    
    private void initComponents() {
        setTitle("Reset Password - Election Commission of Nepal");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Custom title bar
        JPanel titleBar = createTitleBar();
        add(titleBar, BorderLayout.NORTH);
        
        // Main content
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(CONTENT_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JPanel card = createGlassPanel();
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 15, 12, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        
        // Icon
        JLabel iconLabel = new JLabel("🔑", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        iconLabel.setForeground(BLUE);
        card.add(iconLabel, gbc);
        
        // Title
        JLabel titleLabel = new JLabel("Reset Password", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_PRIMARY);
        card.add(titleLabel, gbc);
        
        // Instruction
        JLabel instructionLabel = new JLabel(
            "<html><div style='text-align: center;'>" +
            "Enter your new password below.<br>Minimum 6 characters with at least one number.</div></html>",
            SwingConstants.CENTER
        );
        instructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        instructionLabel.setForeground(TEXT_SECONDARY);
        card.add(instructionLabel, gbc);
        
        // New Password
        JPanel newPasswordPanel = new JPanel(new BorderLayout(10, 5));
        newPasswordPanel.setOpaque(false);
        
        JLabel newPasswordLabel = new JLabel("New Password:");
        newPasswordLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        newPasswordField = new JPasswordField(20);
        newPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        newPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        
        newPasswordPanel.add(newPasswordLabel, BorderLayout.WEST);
        newPasswordPanel.add(newPasswordField, BorderLayout.CENTER);
        card.add(newPasswordPanel, gbc);
        
        // Confirm Password
        JPanel confirmPasswordPanel = new JPanel(new BorderLayout(10, 5));
        confirmPasswordPanel.setOpaque(false);
        
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        
        confirmPasswordPanel.add(confirmPasswordLabel, BorderLayout.WEST);
        confirmPasswordPanel.add(confirmPasswordField, BorderLayout.CENTER);
        card.add(confirmPasswordPanel, gbc);
        
        // Show Password Checkbox
        showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.setOpaque(false);
        showPasswordCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        showPasswordCheckBox.addActionListener(e -> {
            if (showPasswordCheckBox.isSelected()) {
                newPasswordField.setEchoChar((char) 0);
                confirmPasswordField.setEchoChar((char) 0);
            } else {
                newPasswordField.setEchoChar('•');
                confirmPasswordField.setEchoChar('•');
            }
        });
        card.add(showPasswordCheckBox, gbc);
        
        // Message
        messageLabel = new JLabel(" ", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        messageLabel.setForeground(RED);
        card.add(messageLabel, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);
        
        resetButton = new JButton("✅ RESET PASSWORD") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(GREEN.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(GREEN.brighter());
                } else {
                    g2.setColor(GREEN);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 255, 255, 50));
                    g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 8, 8);
                }
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        resetButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        resetButton.setForeground(Color.WHITE);
        resetButton.setFocusPainted(false);
        resetButton.setBorderPainted(false);
        resetButton.setContentAreaFilled(false);
        resetButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        resetButton.setPreferredSize(new Dimension(200, 45));
        resetButton.addActionListener(e -> resetPassword());
        
        cancelButton = new JButton("✖️ CANCEL") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(SIDEBAR_BG.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(SIDEBAR_HOVER);
                } else {
                    g2.setColor(SIDEBAR_BG);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 255, 255, 50));
                    g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 8, 8);
                }
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setContentAreaFilled(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.setPreferredSize(new Dimension(150, 45));
        cancelButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        
        buttonPanel.add(resetButton);
        buttonPanel.add(cancelButton);
        card.add(buttonPanel, gbc);
        
        mainPanel.add(card);
        add(mainPanel, BorderLayout.CENTER);
        
        setSize(550, 600);
        setLocationRelativeTo(null);
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
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        logoLabel.setForeground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Reset Password");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        
        leftPanel.add(logoLabel);
        leftPanel.add(titleLabel);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        rightPanel.setOpaque(false);
        
        JButton minimizeBtn = createWindowButton("−");
        minimizeBtn.addActionListener(e -> setState(JFrame.ICONIFIED));
        
        JButton closeBtn = createWindowButton("×");
        closeBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        
        rightPanel.add(minimizeBtn);
        rightPanel.add(closeBtn);
        
        titleBar.add(leftPanel, BorderLayout.WEST);
        titleBar.add(rightPanel, BorderLayout.EAST);
        
        return titleBar;
    }
    
    private JButton createWindowButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(SIDEBAR_BG);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(35, 30));
        
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
    
    private JPanel createGlassPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(new Color(255, 255, 255, 230));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                g2.setColor(CARD_BORDER);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        return panel;
    }
    
    private void showMessage(String message, Color color) {
        messageLabel.setText(message);
        messageLabel.setForeground(color);
    }
    
    private void resetPassword() {
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        // Validate
        if (newPassword.isEmpty()) {
            showMessage("Please enter new password", RED);
            newPasswordField.requestFocus();
            return;
        }
        
        if (confirmPassword.isEmpty()) {
            showMessage("Please confirm password", RED);
            confirmPasswordField.requestFocus();
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            showMessage("Passwords do not match", RED);
            confirmPasswordField.requestFocus();
            return;
        }
        
        if (newPassword.length() < 6) {
            showMessage("Password must be at least 6 characters", RED);
            newPasswordField.requestFocus();
            return;
        }
        
        if (!newPassword.matches(".*[0-9].*")) {
            showMessage("Password must contain at least one number", RED);
            newPasswordField.requestFocus();
            return;
        }
        
        // Update password in database
        resetButton.setEnabled(false);
        showMessage("⏳ Updating password...", BLUE);
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                Thread.sleep(500);
                return updatePasswordInDatabase(newPassword);
            }
            
            @Override
            protected void done() {
                try {
                    boolean success = get();
                    
                    if (success) {
                        JOptionPane.showMessageDialog(ResetPasswordFrame.this,
                            "✅ Password reset successfully!\n\nYou can now login with your new password.",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        new LoginFrame().setVisible(true);
                        dispose();
                    } else {
                        showMessage("❌ Failed to reset password. Please try again.", RED);
                        resetButton.setEnabled(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage("❌ Error: " + e.getMessage(), RED);
                    resetButton.setEnabled(true);
                }
            }
        };
        worker.execute();
    }
    
    private boolean updatePasswordInDatabase(String newPassword) {
        String query = "UPDATE users SET password = ? WHERE user_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, userId);
            int affected = pstmt.executeUpdate();
            System.out.println("Password update affected rows: " + affected);
            return affected > 0;
        } catch (Exception e) {
            System.err.println("❌ Error updating password: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}