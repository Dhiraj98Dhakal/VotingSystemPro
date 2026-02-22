package votingsystempro.views;

import votingsystempro.controllers.AuthController;
import votingsystempro.controllers.VoterController;
import votingsystempro.database.DatabaseConnection;
import votingsystempro.models.Voter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.io.File;

public class LoginFrame extends JFrame {
    private JTextField voterIdField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JButton forgotPasswordButton;
    private JButton showPasswordButton;
    private JLabel statusLabel;
    
    private AuthController authController;
    private boolean isPasswordVisible = false;
    
    // Modern color scheme
    private final Color GRADIENT_START = new Color(30, 60, 114); // #1e3c72
    private final Color GRADIENT_END = new Color(42, 82, 152); // #2a5298
    private final Color PRIMARY_BLUE = new Color(41, 128, 185); // #2980B9
    private final Color SUCCESS_GREEN = new Color(39, 174, 96); // #27AE60
    private final Color LIGHT_GREY = new Color(213, 216, 220); // #D5D8DC
    private final Color ERROR_RED = new Color(231, 76, 60);
    private final Color WARNING_YELLOW = new Color(241, 196, 15);
    
    // Icon path - resources folder bata
    private final String ICON_PATH = "C:\\Users\\KKKKK\\Documents\\NetBeansProjects\\VotingSystemPro\\resources\\icons\\";
    
    // Eye icons
    private ImageIcon eyeOpenIcon;
    private ImageIcon eyeClosedIcon;
    
    // Fixed size for show password button to prevent shaking
    private final int SHOW_PASSWORD_BUTTON_WIDTH = 140;
    private final int SHOW_PASSWORD_BUTTON_HEIGHT = 30;
    
    public LoginFrame() {
        authController = new AuthController();
        loadEyeIcons();
        initComponents();
        checkDatabaseConnection();
    }
    
    private void loadEyeIcons() {
        // Eye open icon (when password is visible)
        eyeOpenIcon = loadIcon("eye-open.png", 18, 18);
        
        // Eye closed icon (when password is hidden)
        eyeClosedIcon = loadIcon("eye-closed.png", 18, 18);
    }
    
    private ImageIcon loadIcon(String filename, int width, int height) {
        try {
            // Absolute path use garne
            String fullPath = ICON_PATH + filename;
            
            System.out.println("Loading icon from: " + fullPath);
            
            File file = new File(fullPath);
            if (!file.exists()) {
                System.err.println("File does not exist: " + fullPath);
                return null;
            }
            
            ImageIcon icon = new ImageIcon(fullPath);
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
            
        } catch (Exception e) {
            System.err.println("Error loading icon: " + filename);
            e.printStackTrace();
            return null;
        }
    }
    
    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;
        
        if (isPasswordVisible) {
            // Show password
            passwordField.setEchoChar((char) 0);
            
            // Update button with fixed size (no size change)
            if (eyeOpenIcon != null) {
                showPasswordButton.setIcon(eyeOpenIcon);
                showPasswordButton.setText("Hide Password");
            } else {
                showPasswordButton.setText("👁️ Hide Password");
            }
        } else {
            // Hide password
            passwordField.setEchoChar('•');
            
            // Update button with fixed size (no size change)
            if (eyeClosedIcon != null) {
                showPasswordButton.setIcon(eyeClosedIcon);
                showPasswordButton.setText("Show Password");
            } else {
                showPasswordButton.setText("👁️ Show Password");
            }
        }
        
        // Fixed size - no revalidation needed as size doesn't change
        showPasswordButton.setPreferredSize(new Dimension(SHOW_PASSWORD_BUTTON_WIDTH, SHOW_PASSWORD_BUTTON_HEIGHT));
        showPasswordButton.setMaximumSize(new Dimension(SHOW_PASSWORD_BUTTON_WIDTH, SHOW_PASSWORD_BUTTON_HEIGHT));
        showPasswordButton.setMinimumSize(new Dimension(SHOW_PASSWORD_BUTTON_WIDTH, SHOW_PASSWORD_BUTTON_HEIGHT));
    }
    
    private void initComponents() {
        setTitle("Login - Election Commission of Nepal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Main content panel with horizontal split
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 0, 0));
        mainPanel.setBackground(Color.WHITE);
        
        // ==================== LEFT PANEL - BRANDING (40%) ====================
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int w = getWidth(), h = getHeight();
                // Vertical gradient
                GradientPaint gp = new GradientPaint(0, 0, GRADIENT_START, 0, h, GRADIENT_END);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
                
                // Subtle pattern overlay
                g2d.setColor(new Color(255, 255, 255, 20));
                for (int i = 0; i < h; i += 40) {
                    g2d.drawLine(0, i, w, i);
                }
            }
        };
        leftPanel.setLayout(new GridBagLayout());
        
        // Branding content
        JPanel brandingPanel = new JPanel(new GridBagLayout());
        brandingPanel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        
        // Vote Icon from PNG
        ImageIcon voteIcon = loadIcon("vote.png", 100, 100);
        JLabel iconLabel = new JLabel(voteIcon != null ? voteIcon : new ImageIcon());
        if (voteIcon == null) {
            iconLabel.setText("🗳️");
            iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        }
        iconLabel.setForeground(Color.WHITE);
        brandingPanel.add(iconLabel, gbc);
        
        // Title
        JLabel titleLabel = new JLabel("ELECTION COMMISSION", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        brandingPanel.add(titleLabel, gbc);
        
        JLabel subtitleLabel = new JLabel("OF NEPAL", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        subtitleLabel.setForeground(Color.WHITE);
        brandingPanel.add(subtitleLabel, gbc);
        
        // Tagline
        JLabel taglineLabel = new JLabel("Democratic Voting System", SwingConstants.CENTER);
        taglineLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        taglineLabel.setForeground(new Color(255, 255, 255, 200));
        brandingPanel.add(taglineLabel, gbc);
        
        leftPanel.add(brandingPanel);
        
        // ==================== RIGHT PANEL - LOGIN FORM (60%) ====================
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        GridBagConstraints gbcRight = new GridBagConstraints();
        gbcRight.gridwidth = GridBagConstraints.REMAINDER;
        gbcRight.insets = new Insets(5, 20, 5, 20);
        gbcRight.fill = GridBagConstraints.HORIZONTAL;
        
        // Welcome Text
        JLabel welcomeLabel = new JLabel("Welcome Back!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(GRADIENT_START);
        rightPanel.add(welcomeLabel, gbcRight);
        
        JLabel instructionLabel = new JLabel("Please login to access your account", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        instructionLabel.setForeground(Color.GRAY);
        gbcRight.insets = new Insets(5, 20, 25, 20);
        rightPanel.add(instructionLabel, gbcRight);
        
        // ===== Voter ID Section with Label (Properly Adjusted) =====
        gbcRight.insets = new Insets(0, 20, 2, 20);  // Small gap below label
        gbcRight.anchor = GridBagConstraints.WEST;   // Left align the label
        
        // ID Label - Properly aligned
        JLabel idLabel = new JLabel("Voter ID");
        idLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        idLabel.setForeground(GRADIENT_START);
        rightPanel.add(idLabel, gbcRight);
        
        // Voter ID Field with user.png icon
        JPanel voterIdPanel = new JPanel(new BorderLayout(10, 0));
        voterIdPanel.setBackground(Color.WHITE);
        voterIdPanel.setPreferredSize(new Dimension(350, 50));
        voterIdPanel.setMaximumSize(new Dimension(350, 50));
        
        ImageIcon userIcon = loadIcon("user.png", 25, 25);
        JLabel voterIdIcon = new JLabel(userIcon != null ? userIcon : new ImageIcon());
        if (userIcon == null) {
            voterIdIcon.setText("👤");
            voterIdIcon.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        }
        voterIdIcon.setPreferredSize(new Dimension(45, 45));
        voterIdIcon.setHorizontalAlignment(SwingConstants.CENTER);
        
        voterIdField = new JTextField();
        voterIdField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        voterIdField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GREY, 1),
            BorderFactory.createEmptyBorder(12, 10, 12, 10)
        ));
        voterIdField.setPreferredSize(new Dimension(250, 45));
        
        voterIdPanel.add(voterIdIcon, BorderLayout.WEST);
        voterIdPanel.add(voterIdField, BorderLayout.CENTER);
        
        gbcRight.insets = new Insets(0, 20, 15, 20);  // Larger gap below field
        rightPanel.add(voterIdPanel, gbcRight);
        
        // ===== Password Section with Label (Properly Adjusted) =====
        gbcRight.insets = new Insets(0, 20, 2, 20);  // Small gap below label
        gbcRight.anchor = GridBagConstraints.WEST;   // Left align the label
        
        // Password Label - Properly aligned
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        passLabel.setForeground(GRADIENT_START);
        rightPanel.add(passLabel, gbcRight);
        
        // Password Field with lock.png icon
        JPanel passwordPanel = new JPanel(new BorderLayout(10, 0));
        passwordPanel.setBackground(Color.WHITE);
        passwordPanel.setPreferredSize(new Dimension(350, 50));
        passwordPanel.setMaximumSize(new Dimension(350, 50));
        
        ImageIcon lockIcon = loadIcon("lock.png", 25, 25);
        JLabel passwordIcon = new JLabel(lockIcon != null ? lockIcon : new ImageIcon());
        if (lockIcon == null) {
            passwordIcon.setText("🔒");
            passwordIcon.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        }
        passwordIcon.setPreferredSize(new Dimension(45, 45));
        passwordIcon.setHorizontalAlignment(SwingConstants.CENTER);
        
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GREY, 1),
            BorderFactory.createEmptyBorder(12, 10, 12, 10)
        ));
        passwordField.setPreferredSize(new Dimension(250, 45));
        passwordField.setEchoChar('•'); // Initially hidden
        
        passwordPanel.add(passwordIcon, BorderLayout.WEST);
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        
        gbcRight.insets = new Insets(0, 20, 10, 20);  // Larger gap below field
        rightPanel.add(passwordPanel, gbcRight);
        
        // ===== NEW ROW: Show Password and Forgot Password (centered) =====
        JPanel optionsRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        optionsRow.setBackground(Color.WHITE);
        
        // Show Password Button - Using JButton instead of JCheckBox to prevent shaking
        showPasswordButton = new JButton();
        showPasswordButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        showPasswordButton.setForeground(GRADIENT_START);
        showPasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        showPasswordButton.setContentAreaFilled(false);
        showPasswordButton.setBorderPainted(false);
        showPasswordButton.setFocusPainted(false);
        
        // Fixed size to prevent shaking
        showPasswordButton.setPreferredSize(new Dimension(SHOW_PASSWORD_BUTTON_WIDTH, SHOW_PASSWORD_BUTTON_HEIGHT));
        showPasswordButton.setMaximumSize(new Dimension(SHOW_PASSWORD_BUTTON_WIDTH, SHOW_PASSWORD_BUTTON_HEIGHT));
        showPasswordButton.setMinimumSize(new Dimension(SHOW_PASSWORD_BUTTON_WIDTH, SHOW_PASSWORD_BUTTON_HEIGHT));
        
        // Set initial icon and text
        if (eyeClosedIcon != null) {
            showPasswordButton.setIcon(eyeClosedIcon);
            showPasswordButton.setText("Show Password");
            showPasswordButton.setIconTextGap(8);
        } else {
            showPasswordButton.setText("👁️ Show Password");
        }
        
        showPasswordButton.addActionListener(e -> togglePasswordVisibility());
        
        // Forgot Password Button with question.png icon
        ImageIcon questionIcon = loadIcon("question.png", 16, 16);
        forgotPasswordButton = new JButton("Forgot Password?", questionIcon != null ? questionIcon : new ImageIcon());
        if (questionIcon == null) {
            forgotPasswordButton.setText("❓ Forgot Password?");
        }
        forgotPasswordButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        forgotPasswordButton.setForeground(GRADIENT_START);
        forgotPasswordButton.setBorderPainted(false);
        forgotPasswordButton.setContentAreaFilled(false);
        forgotPasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPasswordButton.setIconTextGap(8);
        forgotPasswordButton.setPreferredSize(new Dimension(130, 30));
        forgotPasswordButton.setMaximumSize(new Dimension(130, 30));
        forgotPasswordButton.setMinimumSize(new Dimension(130, 30));
        forgotPasswordButton.addActionListener(e -> openForgotPasswordFrame());
        
        // Simple hover effect - color change only
        forgotPasswordButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                forgotPasswordButton.setForeground(PRIMARY_BLUE);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                forgotPasswordButton.setForeground(GRADIENT_START);
            }
        });
        
        optionsRow.add(showPasswordButton);
        optionsRow.add(forgotPasswordButton);
        
        gbcRight.insets = new Insets(15, 20, 15, 20);
        gbcRight.anchor = GridBagConstraints.CENTER;  // Center align for the options row
        rightPanel.add(optionsRow, gbcRight);
        
        // Status Label
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(ERROR_RED);
        gbcRight.insets = new Insets(5, 20, 10, 20);
        rightPanel.add(statusLabel, gbcRight);
        
        // Login Button with login.png icon
        ImageIcon loginIcon = loadIcon("login.png", 20, 20);
        loginButton = createModernButton("LOGIN", loginIcon, PRIMARY_BLUE);
        loginButton.setPreferredSize(new Dimension(350, 50));
        loginButton.addActionListener(e -> login());
        rightPanel.add(loginButton, gbcRight);
        
        // Register Button with register.png icon
        ImageIcon registerIcon = loadIcon("register.png", 20, 20);
        registerButton = createModernButton("REGISTER", registerIcon, SUCCESS_GREEN);
        registerButton.setPreferredSize(new Dimension(350, 50));
        registerButton.addActionListener(e -> openRegisterFrame());
        rightPanel.add(registerButton, gbcRight);
        
        // Add panels to main panel
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Enter key listener
        voterIdField.addActionListener(e -> passwordField.requestFocus());
        passwordField.addActionListener(e -> login());
    }
    
    private JButton createModernButton(String text, ImageIcon icon, Color bgColor) {
        JButton button = new JButton(text, icon != null ? icon : new ImageIcon()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker().darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.darker());
                } else {
                    g2.setColor(bgColor);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                
                super.paintComponent(g);
            }
        };
        
        if (icon == null) {
            // Icon load भएन भने text मा symbol देखाउने
            if (text.equals("LOGIN")) {
                button.setText("🔐 LOGIN");
            } else if (text.equals("REGISTER")) {
                button.setText("📝 REGISTER");
            }
        }
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(350, 50));
        button.setIconTextGap(10);
        button.setHorizontalTextPosition(SwingConstants.RIGHT);
        
        return button;
    }
    
    private void checkDatabaseConnection() {
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                Connection conn = DatabaseConnection.getConnection();
                return conn != null && !conn.isClosed();
            }
            
            @Override
            protected void done() {
                try {
                    boolean connected = get();
                    if (connected) {
                        statusLabel.setText("✅ Database connected");
                        statusLabel.setForeground(SUCCESS_GREEN);
                    } else {
                        statusLabel.setText("❌ Database connection failed");
                        statusLabel.setForeground(ERROR_RED);
                    }
                } catch (Exception e) {
                    statusLabel.setText("❌ Database error");
                    statusLabel.setForeground(ERROR_RED);
                }
            }
        };
        worker.execute();
    }
    
    private void login() {
        String voterId = voterIdField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (voterId.isEmpty()) {
            showError("Please enter Voter ID");
            voterIdField.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            showError("Please enter Password");
            passwordField.requestFocus();
            return;
        }
        
        setButtonsEnabled(false);
        statusLabel.setText("⏳ Logging in...");
        statusLabel.setForeground(WARNING_YELLOW);
        
        SwingWorker<Object, Void> worker = new SwingWorker<Object, Void>() {
            @Override
            protected Object doInBackground() throws Exception {
                Thread.sleep(500);
                return authController.login(voterId, password);
            }
            
            @Override
            protected void done() {
                try {
                    Object result = get();
                    
                    if (result != null) {
                        if (result instanceof String && result.equals("admin")) {
                            statusLabel.setText("✅ Admin login successful!");
                            statusLabel.setForeground(SUCCESS_GREEN);
                            
                            Timer timer = new Timer(500, e -> {
                                new AdminDashboard().setVisible(true);
                                dispose();
                            });
                            timer.setRepeats(false);
                            timer.start();
                            
                        } else if (result instanceof Integer) {
                            int voterId_int = (Integer) result;
                            VoterController voterController = new VoterController();
                            Voter voter = voterController.getVoterById(voterId_int);
                            
                            if (voter != null && voter.isApproved()) {
                                statusLabel.setText("✅ Login successful! Redirecting...");
                                statusLabel.setForeground(SUCCESS_GREEN);
                                
                                Timer timer = new Timer(500, e -> {
                                    new VoterDashboard(voterId_int).setVisible(true);
                                    dispose();
                                });
                                timer.setRepeats(false);
                                timer.start();
                            } else {
                                statusLabel.setText("⏳ Your registration is pending approval");
                                statusLabel.setForeground(WARNING_YELLOW);
                                setButtonsEnabled(true);
                                showPendingApprovalDialog(voter);
                            }
                            
                        } else {
                            showError(result.toString());
                            setButtonsEnabled(true);
                        }
                    } else {
                        showError("Invalid Voter ID or Password");
                        setButtonsEnabled(true);
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    showError("Login error: " + e.getMessage());
                    setButtonsEnabled(true);
                }
            }
        };
        
        worker.execute();
    }
    
    private void showPendingApprovalDialog(Voter voter) {
        String message;
        String title;
        
        if (voter != null) {
            message = String.format(
                "⏳ Your registration is pending approval from the Election Commission.\n\n" +
                "📋 Registration Details:\n" +
                "• Name: %s\n" +
                "• Email: %s\n" +
                "• Citizenship: %s\n\n" +
                "📧 You will receive an email at:\n" +
                "   %s\n\n" +
                "once your registration is approved.\n\n" +
                "This process usually takes 1-2 business days.\n\n" +
                "Please check your email (including spam folder) regularly.",
                voter.getFullName(),
                voter.getEmail(),
                voter.getCitizenshipNumber(),
                voter.getEmail()
            );
            title = "Registration Pending Approval";
        } else {
            message = "Your registration is pending approval from the Election Commission.\n\n" +
                     "You will receive an email with your Voter ID once approved.\n\n" +
                     "Please check your email regularly.";
            title = "Pending Approval";
        }
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        
        JLabel iconLabel = new JLabel("⏳", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        
        JTextArea textArea = new JTextArea(message);
        textArea.setEditable(false);
        textArea.setBackground(Color.WHITE);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setRows(12);
        
        panel.add(iconLabel, BorderLayout.NORTH);
        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        
        JOptionPane.showMessageDialog(this, panel, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setForeground(ERROR_RED);
        Toolkit.getDefaultToolkit().beep();
    }
    
    private void setButtonsEnabled(boolean enabled) {
        loginButton.setEnabled(enabled);
        registerButton.setEnabled(enabled);
        forgotPasswordButton.setEnabled(enabled);
        showPasswordButton.setEnabled(enabled);
    }
    
    private void openRegisterFrame() {
        new RegisterFrame().setVisible(true);
        dispose();
    }
    
    /**
     * Open Forgot Password Frame with Email/Phone options
     */
    private void openForgotPasswordFrame() {
        new ForgotPasswordFrame().setVisible(true);
        dispose();
    }
}