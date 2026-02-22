package votingsystempro.views;

import votingsystempro.controllers.AuthController;
import votingsystempro.controllers.LocationController;
import votingsystempro.models.Voter;
import votingsystempro.utils.ImageUtil;
import votingsystempro.utils.ValidationUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

public class RegisterFrame extends JFrame {
    private JTextField fullNameField, citizenshipField, fatherNameField, motherNameField;
    private JTextField addressField, phoneField, emailField;
    private JComboBox<String> provinceCombo, districtCombo, constituencyCombo;
    private JTextField dayField, monthField, yearField;
    private JButton uploadPhotoButton, registerButton, backButton, nextButton, prevButton;
    private JLabel photoLabel;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private int currentStep = 0;
    private final int TOTAL_STEPS = 3;
    
    private AuthController authController;
    private LocationController locationController;
    private String photoPath;
    private Map<Integer, String> provinces;
    private Map<Integer, String> districts;
    private Map<Integer, String> constituencies;
    
    // Password fields
    private JPasswordField passwordField, confirmPasswordField;
    
    // Icons
    private ImageIcon nextIcon, prevIcon, registerIcon, backIcon;
    
    // Modern color scheme
    private final Color GRADIENT_START = new Color(30, 60, 114);
    private final Color GRADIENT_END = new Color(42, 82, 152);
    private final Color PRIMARY_BLUE = new Color(41, 128, 185);
    private final Color SUCCESS_GREEN = new Color(39, 174, 96);
    private final Color LIGHT_GREY = new Color(213, 216, 220);
    
    public RegisterFrame() {
        authController = new AuthController();
        locationController = new LocationController();
        provinces = new HashMap<>();
        districts = new HashMap<>();
        constituencies = new HashMap<>();
        
        loadIcons();
        initComponents();
        loadProvinces();
    }
    
    private void loadIcons() {
        String basePath = "C:\\Users\\KKKKK\\Documents\\NetBeansProjects\\VotingSystemPro\\resources\\icons\\";
        nextIcon = loadIcon(basePath + "next.png", 20, 20);
        prevIcon = loadIcon(basePath + "previous.png", 20, 20);
        registerIcon = loadIcon(basePath + "register.png", 20, 20);
        backIcon = loadIcon(basePath + "back.png", 20, 20);
    }
    
    private ImageIcon loadIcon(String path, int width, int height) {
        try {
            File file = new File(path);
            if (!file.exists()) return null;
            ImageIcon icon = new ImageIcon(path);
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            return null;
        }
    }
    
    private void initComponents() {
        setTitle("Voter Registration - Election Commission of Nepal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(900, 700);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Top Panel with Gradient Header
        JPanel topPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int w = getWidth(), h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, GRADIENT_START, 0, h, GRADIENT_END);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        topPanel.setPreferredSize(new Dimension(900, 100));
        topPanel.setLayout(new BorderLayout());
        
        JLabel headerLabel = new JLabel("📝 VOTER REGISTRATION", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        headerLabel.setForeground(Color.WHITE);
        topPanel.add(headerLabel, BorderLayout.CENTER);
        
        JLabel subHeaderLabel = new JLabel("Election Commission of Nepal", SwingConstants.CENTER);
        subHeaderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subHeaderLabel.setForeground(new Color(255, 255, 255, 200));
        topPanel.add(subHeaderLabel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Step Indicator Panel
        JPanel stepPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        stepPanel.setBackground(Color.WHITE);
        stepPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 10, 40));
        
        String[] steps = {"PERSONAL INFO", "CONTACT & LOCATION", "PASSWORD"};
        String[] stepIcons = {"👤", "📍", "🔒"};
        
        for (int i = 0; i < steps.length; i++) {
            JPanel stepCard = new JPanel(new BorderLayout(5, 5));
            stepCard.setBackground(Color.WHITE);
            stepCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(i == 0 ? PRIMARY_BLUE : LIGHT_GREY, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            
            JLabel iconLabel = new JLabel(stepIcons[i], SwingConstants.CENTER);
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
            
            JLabel stepLabel = new JLabel(steps[i], SwingConstants.CENTER);
            stepLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            stepLabel.setForeground(i == 0 ? PRIMARY_BLUE : Color.GRAY);
            
            stepCard.add(iconLabel, BorderLayout.NORTH);
            stepCard.add(stepLabel, BorderLayout.CENTER);
            stepCard.setName("step" + i);
            
            stepPanel.add(stepCard);
        }
        
        add(stepPanel, BorderLayout.CENTER);
        
        // Card Panel
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        cardPanel.add(createPersonalInfoPanel(), "personal");
        cardPanel.add(createContactLocationPanel(), "contact");
        cardPanel.add(createPasswordPanel(), "password");
        
        add(cardPanel, BorderLayout.CENTER);
        
        // Navigation Panel
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        navPanel.setBackground(Color.WHITE);
        navPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        
        prevButton = createNavButton("PREVIOUS", prevIcon, new Color(100, 100, 100));
        prevButton.setEnabled(false);
        prevButton.addActionListener(e -> navigatePrevious());
        
        nextButton = createNavButton("NEXT", nextIcon, PRIMARY_BLUE);
        nextButton.addActionListener(e -> navigateNext());
        
        registerButton = createNavButton("REGISTER", registerIcon, SUCCESS_GREEN);
        registerButton.setVisible(false);
        registerButton.addActionListener(e -> registerVoter());
        
        backButton = createNavButton("BACK TO LOGIN", backIcon, new Color(231, 76, 60));
        backButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        
        navPanel.add(prevButton);
        navPanel.add(nextButton);
        navPanel.add(registerButton);
        navPanel.add(backButton);
        
        add(navPanel, BorderLayout.SOUTH);
    }
    
    private JButton createNavButton(String text, ImageIcon icon, Color bgColor) {
        JButton button = new JButton(text, icon) {
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
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
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
        button.setPreferredSize(new Dimension(160, 45));
        button.setIconTextGap(8);
        
        return button;
    }
    
    private JPanel createPersonalInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Section Header
        JLabel sectionHeader = new JLabel("👤 PERSONAL INFORMATION");
        sectionHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        sectionHeader.setForeground(PRIMARY_BLUE);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(sectionHeader, gbc);
        row++;
        
        // Separator
        JSeparator separator = new JSeparator();
        separator.setForeground(LIGHT_GREY);
        gbc.insets = new Insets(5, 10, 10, 10);
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(separator, gbc);
        row++;
        
        gbc.insets = new Insets(5, 10, 5, 10);
        
        // Photo Upload Section
        JLabel photoLabel_title = new JLabel("Photo:");
        photoLabel_title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(photoLabel_title, gbc);
        
        // Photo Panel - FIXED SIZE TO PREVENT SHAKING
        JPanel photoPanel = new JPanel(new BorderLayout());
        photoPanel.setBackground(Color.WHITE);
        photoPanel.setBorder(BorderFactory.createLineBorder(LIGHT_GREY, 2));
        photoPanel.setPreferredSize(new Dimension(100, 100));
        photoPanel.setMinimumSize(new Dimension(100, 100));
        photoPanel.setMaximumSize(new Dimension(100, 100));
        
        photoLabel = new JLabel("📷", SwingConstants.CENTER);
        photoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        photoLabel.setForeground(Color.GRAY);
        photoPanel.add(photoLabel, BorderLayout.CENTER);
        
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(photoPanel, gbc);
        
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        uploadPhotoButton = new JButton("Browse...");
        uploadPhotoButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        uploadPhotoButton.setBackground(PRIMARY_BLUE);
        uploadPhotoButton.setForeground(Color.WHITE);
        uploadPhotoButton.setFocusPainted(false);
        uploadPhotoButton.setBorderPainted(false);
        uploadPhotoButton.setPreferredSize(new Dimension(90, 30));
        uploadPhotoButton.addActionListener(e -> uploadPhoto());
        panel.add(uploadPhotoButton, gbc);
        row++;
        
        // Full Name
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel nameLabel = new JLabel("Full Name:*");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(nameLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        fullNameField = new JTextField(20);
        fullNameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fullNameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GREY, 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        panel.add(fullNameField, gbc);
        row++;
        
        // Date of Birth - FIXED with PROPER SIZES
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel dobLabel = new JLabel("Date of Birth:*");
        dobLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(dobLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        JPanel dobPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        dobPanel.setBackground(Color.WHITE);
        
        // Day Field - NORMAL SIZE
        dayField = new JTextField(3);
        dayField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dayField.setHorizontalAlignment(JTextField.CENTER);
        dayField.setBorder(BorderFactory.createLineBorder(LIGHT_GREY, 1));
        dayField.setPreferredSize(new Dimension(45, 30));
        
        // Month Field - NORMAL SIZE
        monthField = new JTextField(3);
        monthField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        monthField.setHorizontalAlignment(JTextField.CENTER);
        monthField.setBorder(BorderFactory.createLineBorder(LIGHT_GREY, 1));
        monthField.setPreferredSize(new Dimension(45, 30));
        
        // Year Field - NORMAL SIZE
        yearField = new JTextField(5);
        yearField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        yearField.setHorizontalAlignment(JTextField.CENTER);
        yearField.setBorder(BorderFactory.createLineBorder(LIGHT_GREY, 1));
        yearField.setPreferredSize(new Dimension(70, 30));
        
        dobPanel.add(new JLabel("Day:"));
        dobPanel.add(dayField);
        dobPanel.add(Box.createHorizontalStrut(5));
        dobPanel.add(new JLabel("Month:"));
        dobPanel.add(monthField);
        dobPanel.add(Box.createHorizontalStrut(5));
        dobPanel.add(new JLabel("Year:"));
        dobPanel.add(yearField);
        
        panel.add(dobPanel, gbc);
        row++;
        
        // Citizenship Number
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel citizenLabel = new JLabel("Citizenship No:*");
        citizenLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(citizenLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        citizenshipField = new JTextField(20);
        citizenshipField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        citizenshipField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GREY, 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        panel.add(citizenshipField, gbc);
        row++;
        
        // Father's Name
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel fatherLabel = new JLabel("Father's Name:*");
        fatherLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(fatherLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        fatherNameField = new JTextField(20);
        fatherNameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fatherNameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GREY, 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        panel.add(fatherNameField, gbc);
        row++;
        
        // Mother's Name
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel motherLabel = new JLabel("Mother's Name:*");
        motherLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(motherLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        motherNameField = new JTextField(20);
        motherNameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        motherNameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GREY, 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        panel.add(motherNameField, gbc);
        row++;
        
        return panel;
    }
    
    private JPanel createContactLocationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Contact Section Header
        JLabel contactHeader = new JLabel("📞 CONTACT INFORMATION");
        contactHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        contactHeader.setForeground(PRIMARY_BLUE);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(contactHeader, gbc);
        row++;
        
        JSeparator separator1 = new JSeparator();
        separator1.setForeground(LIGHT_GREY);
        gbc.insets = new Insets(5, 10, 10, 10);
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(separator1, gbc);
        row++;
        
        gbc.insets = new Insets(5, 10, 5, 10);
        
        // Phone Number
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel phoneLabel = new JLabel("Phone Number:*");
        phoneLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(phoneLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        phoneField = new JTextField(20);
        phoneField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        phoneField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GREY, 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        panel.add(phoneField, gbc);
        row++;
        
        // Email
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel emailLabel = new JLabel("Email:*");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(emailLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        emailField = new JTextField(20);
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GREY, 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        panel.add(emailField, gbc);
        row++;
        
        // Address
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel addressLabel = new JLabel("Address:*");
        addressLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(addressLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        addressField = new JTextField(20);
        addressField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addressField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GREY, 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        panel.add(addressField, gbc);
        row++;
        
        // Location Section Header
        JLabel locationHeader = new JLabel("📍 LOCATION INFORMATION");
        locationHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        locationHeader.setForeground(PRIMARY_BLUE);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(15, 10, 5, 10);
        panel.add(locationHeader, gbc);
        row++;
        
        JSeparator separator2 = new JSeparator();
        separator2.setForeground(LIGHT_GREY);
        gbc.insets = new Insets(5, 10, 10, 10);
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(separator2, gbc);
        row++;
        
        gbc.insets = new Insets(5, 10, 5, 10);
        
        // Province
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel provinceLabel = new JLabel("Province:*");
        provinceLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(provinceLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        provinceCombo = new JComboBox<>();
        provinceCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        provinceCombo.setPreferredSize(new Dimension(250, 35));
        provinceCombo.setBorder(BorderFactory.createLineBorder(LIGHT_GREY, 1));
        provinceCombo.addActionListener(e -> {
            if (provinceCombo.getSelectedIndex() > 0) {
                loadDistricts();
            } else {
                districtCombo.removeAllItems();
                districtCombo.addItem("-- Select District --");
                constituencyCombo.removeAllItems();
                constituencyCombo.addItem("-- Select Constituency --");
            }
        });
        panel.add(provinceCombo, gbc);
        row++;
        
        // District
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel districtLabel = new JLabel("District:*");
        districtLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(districtLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        districtCombo = new JComboBox<>();
        districtCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        districtCombo.setPreferredSize(new Dimension(250, 35));
        districtCombo.setBorder(BorderFactory.createLineBorder(LIGHT_GREY, 1));
        districtCombo.addActionListener(e -> {
            if (districtCombo.getSelectedIndex() > 0) {
                loadConstituencies();
            } else {
                constituencyCombo.removeAllItems();
                constituencyCombo.addItem("-- Select Constituency --");
            }
        });
        panel.add(districtCombo, gbc);
        row++;
        
        // Constituency
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel constituencyLabel = new JLabel("Constituency:*");
        constituencyLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(constituencyLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        constituencyCombo = new JComboBox<>();
        constituencyCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        constituencyCombo.setPreferredSize(new Dimension(250, 35));
        constituencyCombo.setBorder(BorderFactory.createLineBorder(LIGHT_GREY, 1));
        panel.add(constituencyCombo, gbc);
        row++;
        
        return panel;
    }
    
    private JPanel createPasswordPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        JLabel sectionHeader = new JLabel("🔒 PASSWORD SETUP");
        sectionHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        sectionHeader.setForeground(PRIMARY_BLUE);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(sectionHeader, gbc);
        row++;
        
        JSeparator separator = new JSeparator();
        separator.setForeground(LIGHT_GREY);
        gbc.insets = new Insets(5, 10, 15, 10);
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(separator, gbc);
        row++;
        
        gbc.insets = new Insets(5, 10, 5, 10);
        
        // Password
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel passwordLabel = new JLabel("Password:*");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(passwordLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GREY, 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        panel.add(passwordField, gbc);
        row++;
        
        // Confirm Password
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel confirmLabel = new JLabel("Confirm Password:*");
        confirmLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(confirmLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GREY, 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        panel.add(confirmPasswordField, gbc);
        row++;
        
        // Password requirements card
        JPanel reqPanel = new JPanel(new GridBagLayout());
        reqPanel.setBackground(new Color(240, 248, 255));
        reqPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_BLUE, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        GridBagConstraints reqGbc = new GridBagConstraints();
        reqGbc.gridwidth = GridBagConstraints.REMAINDER;
        reqGbc.anchor = GridBagConstraints.WEST;
        reqGbc.insets = new Insets(2, 5, 2, 5);
        
        JLabel reqTitle = new JLabel("📋 Password Requirements:");
        reqTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        reqTitle.setForeground(PRIMARY_BLUE);
        reqPanel.add(reqTitle, reqGbc);
        
        JLabel req1 = new JLabel("• Minimum 6 characters");
        req1.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        reqPanel.add(req1, reqGbc);
        
        JLabel req2 = new JLabel("• At least one uppercase letter");
        req2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        reqPanel.add(req2, reqGbc);
        
        JLabel req3 = new JLabel("• At least one number");
        req3.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        reqPanel.add(req3, reqGbc);
        
        JLabel req4 = new JLabel("• Both passwords must match");
        req4.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        reqPanel.add(req4, reqGbc);
        
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(15, 10, 5, 10);
        panel.add(reqPanel, gbc);
        
        return panel;
    }
    
    private void navigateNext() {
        if (currentStep == 0 && !validatePersonalInfo()) return;
        if (currentStep == 1 && !validateContactLocationInfo()) return;
        
        if (currentStep < TOTAL_STEPS - 1) {
            currentStep++;
            updateStepDisplay();
        }
    }
    
    private void navigatePrevious() {
        if (currentStep > 0) {
            currentStep--;
            updateStepDisplay();
        }
    }
    
    private void updateStepDisplay() {
        JPanel stepPanel = (JPanel) getContentPane().getComponent(1);
        for (int i = 0; i < TOTAL_STEPS; i++) {
            JPanel stepCard = (JPanel) stepPanel.getComponent(i);
            stepCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(i == currentStep ? PRIMARY_BLUE : LIGHT_GREY, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            
            JLabel stepLabel = (JLabel) stepCard.getComponent(1);
            stepLabel.setForeground(i == currentStep ? PRIMARY_BLUE : Color.GRAY);
        }
        
        switch (currentStep) {
            case 0: cardLayout.show(cardPanel, "personal"); break;
            case 1: cardLayout.show(cardPanel, "contact"); break;
            case 2: cardLayout.show(cardPanel, "password"); break;
        }
        
        prevButton.setEnabled(currentStep > 0);
        
        if (currentStep == TOTAL_STEPS - 1) {
            nextButton.setVisible(false);
            registerButton.setVisible(true);
        } else {
            nextButton.setVisible(true);
            registerButton.setVisible(false);
        }
    }
    
    private boolean validatePersonalInfo() {
        if (fullNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your full name");
            return false;
        }
        
        // Date of Birth Validation - FIXED
        String dayText = dayField.getText().trim();
        String monthText = monthField.getText().trim();
        String yearText = yearField.getText().trim();
        
        if (dayText.isEmpty() || monthText.isEmpty() || yearText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter complete date of birth");
            return false;
        }
        
        try {
            int day = Integer.parseInt(dayText);
            int month = Integer.parseInt(monthText);
            int year = Integer.parseInt(yearText);
            
            if (day < 1 || day > 31) {
                JOptionPane.showMessageDialog(this, "Day must be between 1 and 31");
                return false;
            }
            
            if (month < 1 || month > 12) {
                JOptionPane.showMessageDialog(this, "Month must be between 1 and 12");
                return false;
            }
            
            if (year < 1900 || year > 2024) {
                JOptionPane.showMessageDialog(this, "Year must be between 1900 and 2024");
                return false;
            }
            
            // Month-specific validation
            if (month == 2) {
                boolean isLeapYear = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
                if (isLeapYear && day > 29) {
                    JOptionPane.showMessageDialog(this, "February has only 29 days in leap year");
                    return false;
                } else if (!isLeapYear && day > 28) {
                    JOptionPane.showMessageDialog(this, "February has only 28 days");
                    return false;
                }
            } else if (month == 4 || month == 6 || month == 9 || month == 11) {
                if (day > 30) {
                    JOptionPane.showMessageDialog(this, "This month has only 30 days");
                    return false;
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for date");
            return false;
        }
        
        if (citizenshipField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter citizenship number");
            return false;
        }
        
        if (fatherNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter father's name");
            return false;
        }
        
        if (motherNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter mother's name");
            return false;
        }
        
        if (photoPath == null || photoPath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please upload your photo");
            return false;
        }
        
        return true;
    }
    
    private boolean validateContactLocationInfo() {
        if (phoneField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter phone number");
            return false;
        }
        
        if (!ValidationUtil.isValidPhone(phoneField.getText().trim())) {
            JOptionPane.showMessageDialog(this, "Invalid phone number!");
            return false;
        }
        
        if (emailField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter email");
            return false;
        }
        
        if (!ValidationUtil.isValidEmail(emailField.getText().trim())) {
            JOptionPane.showMessageDialog(this, "Invalid email format!");
            return false;
        }
        
        if (addressField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter address");
            return false;
        }
        
        if (provinceCombo.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Please select province");
            return false;
        }
        
        if (districtCombo.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Please select district");
            return false;
        }
        
        if (constituencyCombo.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Please select constituency");
            return false;
        }
        
        return true;
    }
    
    private void loadProvinces() {
        try {
            provinces = locationController.getAllProvinces();
            provinceCombo.removeAllItems();
            provinceCombo.addItem("-- Select Province --");
            
            if (provinces != null && !provinces.isEmpty()) {
                for (Map.Entry<Integer, String> entry : provinces.entrySet()) {
                    provinceCombo.addItem(entry.getValue());
                }
            } else {
                // Fallback provinces if database doesn't have them
                provinceCombo.addItem("Province No. 1");
                provinceCombo.addItem("Province No. 2");
                provinceCombo.addItem("Bagmati Province");
                provinceCombo.addItem("Gandaki Province");
                provinceCombo.addItem("Lumbini Province");
                provinceCombo.addItem("Karnali Province");
                provinceCombo.addItem("Sudurpashchim Province");
            }
        } catch (Exception e) {
            e.printStackTrace();
            provinceCombo.removeAllItems();
            provinceCombo.addItem("-- Select Province --");
            provinceCombo.addItem("Province No. 1");
            provinceCombo.addItem("Province No. 2");
            provinceCombo.addItem("Bagmati Province");
            provinceCombo.addItem("Gandaki Province");
            provinceCombo.addItem("Lumbini Province");
            provinceCombo.addItem("Karnali Province");
            provinceCombo.addItem("Sudurpashchim Province");
        }
    }
    
    private void loadDistricts() {
        int selectedIndex = provinceCombo.getSelectedIndex();
        if (selectedIndex > 0) {
            try {
                String selectedProvince = (String) provinceCombo.getSelectedItem();
                int provinceId = -1;
                
                // Try to get province ID from the provinces map
                for (Map.Entry<Integer, String> entry : provinces.entrySet()) {
                    if (entry.getValue().equals(selectedProvince)) {
                        provinceId = entry.getKey();
                        break;
                    }
                }
                
                if (provinceId != -1) {
                    districts = locationController.getDistrictsByProvince(provinceId);
                } else {
                    // If province ID not found, try to get by name
                    districts = getFallbackDistricts(selectedProvince);
                }
                
                districtCombo.removeAllItems();
                districtCombo.addItem("-- Select District --");
                
                if (districts != null && !districts.isEmpty()) {
                    for (Map.Entry<Integer, String> entry : districts.entrySet()) {
                        districtCombo.addItem(entry.getValue());
                    }
                } else {
                    districtCombo.addItem("No districts found");
                }
            } catch (Exception e) {
                e.printStackTrace();
                districtCombo.removeAllItems();
                districtCombo.addItem("-- Select District --");
            }
        }
    }
    
    private Map<Integer, String> getFallbackDistricts(String provinceName) {
        Map<Integer, String> fallback = new HashMap<>();
        
        // Fallback district data based on province
        if (provinceName.contains("1")) {
            fallback.put(1, "Jhapa");
            fallback.put(2, "Morang");
            fallback.put(3, "Sunsari");
            fallback.put(4, "Dhankuta");
            fallback.put(5, "Tehrathum");
        } else if (provinceName.contains("2")) {
            fallback.put(6, "Saptari");
            fallback.put(7, "Siraha");
            fallback.put(8, "Dhanusa");
            fallback.put(9, "Mahottari");
            fallback.put(10, "Sarlahi");
        } else if (provinceName.contains("Bagmati")) {
            fallback.put(11, "Kathmandu");
            fallback.put(12, "Lalitpur");
            fallback.put(13, "Bhaktapur");
            fallback.put(14, "Chitwan");
            fallback.put(15, "Makwanpur");
        } else if (provinceName.contains("Gandaki")) {
            fallback.put(16, "Kaski");
            fallback.put(17, "Lamjung");
            fallback.put(18, "Gorkha");
            fallback.put(19, "Tanahun");
            fallback.put(20, "Syangja");
        } else if (provinceName.contains("Lumbini")) {
            fallback.put(21, "Rupandehi");
            fallback.put(22, "Kapilvastu");
            fallback.put(23, "Dang");
            fallback.put(24, "Banke");
            fallback.put(25, "Bardiya");
        } else if (provinceName.contains("Karnali")) {
            fallback.put(26, "Surkhet");
            fallback.put(27, "Dailekh");
            fallback.put(28, "Jumla");
            fallback.put(29, "Mugu");
            fallback.put(30, "Humla");
        } else if (provinceName.contains("Sudurpashchim")) {
            fallback.put(31, "Kailali");
            fallback.put(32, "Kanchanpur");
            fallback.put(33, "Doti");
            fallback.put(34, "Achham");
            fallback.put(35, "Baitadi");
        }
        
        return fallback;
    }
    
    private void loadConstituencies() {
        int selectedIndex = districtCombo.getSelectedIndex();
        if (selectedIndex > 0) {
            try {
                String selectedDistrict = (String) districtCombo.getSelectedItem();
                int districtId = -1;
                
                // Try to get district ID from the districts map
                for (Map.Entry<Integer, String> entry : districts.entrySet()) {
                    if (entry.getValue().equals(selectedDistrict)) {
                        districtId = entry.getKey();
                        break;
                    }
                }
                
                if (districtId != -1) {
                    constituencies = locationController.getConstituenciesByDistrict(districtId);
                } else {
                    // If district ID not found, create fallback constituencies
                    constituencies = getFallbackConstituencies(selectedDistrict);
                }
                
                constituencyCombo.removeAllItems();
                constituencyCombo.addItem("-- Select Constituency --");
                
                if (constituencies != null && !constituencies.isEmpty()) {
                    for (Map.Entry<Integer, String> entry : constituencies.entrySet()) {
                        constituencyCombo.addItem(entry.getValue());
                    }
                } else {
                    // Fallback: Add 3 sample constituencies
                    constituencyCombo.addItem("Constituency No. 1");
                    constituencyCombo.addItem("Constituency No. 2");
                    constituencyCombo.addItem("Constituency No. 3");
                }
            } catch (Exception e) {
                e.printStackTrace();
                constituencyCombo.removeAllItems();
                constituencyCombo.addItem("-- Select Constituency --");
                // Add fallback constituencies
                constituencyCombo.addItem("Constituency No. 1");
                constituencyCombo.addItem("Constituency No. 2");
                constituencyCombo.addItem("Constituency No. 3");
            }
        }
    }
    
    private Map<Integer, String> getFallbackConstituencies(String districtName) {
        Map<Integer, String> fallback = new HashMap<>();
        
        // Add constituencies based on district
        if (districtName.equals("Kathmandu")) {
            fallback.put(1, "Constituency No. 1");
            fallback.put(2, "Constituency No. 2");
            fallback.put(3, "Constituency No. 3");
            fallback.put(4, "Constituency No. 4");
            fallback.put(5, "Constituency No. 5");
            fallback.put(6, "Constituency No. 6");
            fallback.put(7, "Constituency No. 7");
            fallback.put(8, "Constituency No. 8");
            fallback.put(9, "Constituency No. 9");
            fallback.put(10, "Constituency No. 10");
        } else if (districtName.equals("Lalitpur") || districtName.equals("Bhaktapur") || 
                   districtName.equals("Chitwan") || districtName.equals("Kaski") ||
                   districtName.equals("Rupandehi") || districtName.equals("Kailali")) {
            fallback.put(1, "Constituency No. 1");
            fallback.put(2, "Constituency No. 2");
            fallback.put(3, "Constituency No. 3");
        } else {
            fallback.put(1, "Constituency No. 1");
            fallback.put(2, "Constituency No. 2");
        }
        
        return fallback;
    }
    
    private void uploadPhoto() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Photo");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Image files (*.jpg, *.jpeg, *.png, *.gif)", "jpg", "jpeg", "png", "gif"));
        
        int result = fileChooser.showOpenDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            
            if (!selectedFile.exists()) {
                JOptionPane.showMessageDialog(this, "File does not exist!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (selectedFile.length() > 5 * 1024 * 1024) {
                JOptionPane.showMessageDialog(this, "File too large! (Max 5MB)", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            uploadPhotoButton.setEnabled(false);
            
            SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() throws Exception {
                    return ImageUtil.saveImage(selectedFile, "voter_photos");
                }
                
                @Override
                protected void done() {
                    try {
                        photoPath = get();
                        if (photoPath != null) {
                            ImageIcon icon = ImageUtil.createImageIcon(photoPath, 90, 90);
                            if (icon != null) {
                                photoLabel.setIcon(icon);
                                photoLabel.setText("");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        setCursor(Cursor.getDefaultCursor());
                        uploadPhotoButton.setEnabled(true);
                    }
                }
            };
            worker.execute();
        }
    }
    
    private void registerVoter() {
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmPasswordField.getPassword());
        
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters!");
            return;
        }
        
        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!");
            return;
        }
        
        Voter voter = new Voter();
        voter.setFullName(fullNameField.getText().trim().toUpperCase());
        
        try {
            int day = Integer.parseInt(dayField.getText().trim());
            int month = Integer.parseInt(monthField.getText().trim());
            int year = Integer.parseInt(yearField.getText().trim());
            
            Date dob = new SimpleDateFormat("yyyy-MM-dd").parse(year + "-" + month + "-" + day);
            voter.setDateOfBirth(dob);
            voter.setAge(calculateAge(dob));
            
            if (voter.getAge() < 18) {
                JOptionPane.showMessageDialog(this, "You must be at least 18 years old!");
                return;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid date format");
            return;
        }
        
        voter.setCitizenshipNumber(citizenshipField.getText().trim().toUpperCase());
        voter.setFatherName(fatherNameField.getText().trim().toUpperCase());
        voter.setMotherName(motherNameField.getText().trim().toUpperCase());
        voter.setAddress(addressField.getText().trim().toUpperCase());
        voter.setPhoneNumber(phoneField.getText().trim());
        voter.setEmail(emailField.getText().trim().toLowerCase());
        
        // Get province ID
        String selectedProvince = (String) provinceCombo.getSelectedItem();
        for (Map.Entry<Integer, String> entry : provinces.entrySet()) {
            if (entry.getValue().equals(selectedProvince)) {
                voter.setProvinceId(entry.getKey());
                break;
            }
        }
        
        // Get district ID
        String selectedDistrict = (String) districtCombo.getSelectedItem();
        for (Map.Entry<Integer, String> entry : districts.entrySet()) {
            if (entry.getValue().equals(selectedDistrict)) {
                voter.setDistrictId(entry.getKey());
                break;
            }
        }
        
        // Get constituency ID
        String selectedConstituency = (String) constituencyCombo.getSelectedItem();
        for (Map.Entry<Integer, String> entry : constituencies.entrySet()) {
            if (entry.getValue().equals(selectedConstituency)) {
                voter.setConstituencyId(entry.getKey());
                break;
            }
        }
        
        voter.setPhotoPath(photoPath);
        
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        registerButton.setEnabled(false);
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return authController.registerVoter(voter, password);
            }
            
            @Override
            protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(RegisterFrame.this, 
                            "✅ Registration successful! Awaiting admin approval.\nYou will receive an email with your Voter ID once approved.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                        new LoginFrame().setVisible(true);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(RegisterFrame.this, 
                            "❌ Registration failed. Please check if email or citizenship number already exists.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                    registerButton.setEnabled(true);
                }
            }
        };
        worker.execute();
    }
    
    private int calculateAge(Date dob) {
        Date currentDate = new Date();
        long diff = currentDate.getTime() - dob.getTime();
        return (int) (diff / (1000L * 60 * 60 * 24 * 365));
    }
}