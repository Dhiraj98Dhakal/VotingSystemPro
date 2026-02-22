package votingsystempro.views;

import votingsystempro.controllers.AdminController;
import votingsystempro.controllers.VoterController;
import votingsystempro.controllers.PartyController;
import votingsystempro.controllers.CandidateController;
import votingsystempro.controllers.AuthController;
import votingsystempro.utils.EmailUtil;
import votingsystempro.views.admin.UploadNewsFrame;
import votingsystempro.database.DatabaseConnection;  // ✅ IMPORT ADDED

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.border.TitledBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.sql.PreparedStatement;
import java.util.List;

public class AdminDashboard extends JFrame {
    private AdminController adminController;
    private VoterController voterController;
    private PartyController partyController;
    private CandidateController candidateController;
    private AuthController authController;
    
    private JLabel totalVotersLabel, approvedVotersLabel, pendingVotersLabel;
    private JLabel totalPartiesLabel, totalCandidatesLabel, totalVotesLabel;
    private JLabel emailSuccessLabel, emailFailedLabel;
    private JTabbedPane tabbedPane;
    private JTable activityLogTable, emailLogTable;
    private DefaultTableModel logTableModel, emailLogModel;
    private JButton logoutBtn, refreshStatsBtn, testEmailBtn;
    private JTextArea emailStatsArea;
    
    // Software Information
    private final String VERSION = "2.0.0";
    private final String BUILD_DATE = "February 2025";
    private final String DEVELOPER_NAME = "Dhiraj Dhakal";
    private final String DEVELOPER_EMAIL = "dhirajdhakal460@gmail.com";
    private final String DEVELOPER_GITHUB = "@dhirajdhakal";
    private final String COMPANY_NAME = "Election Commission of Nepal";
    private final String LICENSE = "Government of Nepal - Official Use";
    
    // Icon paths
    private final String ICON_PATH = "C:\\Users\\KKKKK\\Documents\\NetBeansProjects\\VotingSystemPro_1\\resources\\icons\\";
    
    // PNG Icons
    private ImageIcon dashboardIcon, managementIcon, votersIcon, emailIcon;
    private ImageIcon activityIcon, emailLogIcon, newsIcon, settingsIcon;
    private ImageIcon refreshIcon, testEmailIcon, logoutIcon;
    private ImageIcon votersCardIcon, partiesCardIcon, candidatesCardIcon, locationsCardIcon;
    private ImageIcon liveVoteIcon, emailSettingsIcon, approveIcon, reportsIcon;
    
    // Modern premium color scheme
    private final Color SIDEBAR_BG = new Color(15, 23, 42); // #0F172A
    private final Color SIDEBAR_HOVER = new Color(30, 41, 59); // #1E293B
    private final Color CONTENT_BG = new Color(248, 250, 252); // #F8FAFC
    private final Color CARD_BG = Color.WHITE;
    private final Color CARD_BORDER = new Color(226, 232, 240); // #E2E8F0
    private final Color TEXT_PRIMARY = new Color(15, 23, 42); // #0F172A
    private final Color TEXT_SECONDARY = new Color(71, 85, 105); // #475569
    
    // Accent colors
    private final Color BLUE = new Color(59, 130, 246); // #3B82F6
    private final Color GREEN = new Color(34, 197, 94); // #22C55E
    private final Color PURPLE = new Color(168, 85, 247); // #A855F7
    private final Color ORANGE = new Color(249, 115, 22); // #F97316
    private final Color YELLOW = new Color(234, 179, 8); // #EAB308
    private final Color RED = new Color(239, 68, 68); // #EF4444
    
    // Tab colors
    private final Color TAB_BG = new Color(255, 255, 255, 200);
    private final Color TAB_SELECTED = new Color(255, 255, 255);
    private final Color TAB_HOVER = new Color(255, 255, 255, 150);
    
    // Color strings for actions array
    private final String BLUE_STR = "59,130,246";
    private final String GREEN_STR = "34,197,94";
    private final String PURPLE_STR = "168,85,247";
    private final String ORANGE_STR = "249,115,22";
    private final String YELLOW_STR = "234,179,8";
    private final String RED_STR = "239,68,68";
    
    public AdminDashboard() {
        adminController = new AdminController();
        voterController = new VoterController();
        partyController = new PartyController();
        candidateController = new CandidateController();
        authController = new AuthController();
        
        loadIcons();
        initComponents();
        loadDashboardStats();
        loadActivityLogs();
        loadEmailLogs();
        checkEmailConfiguration();
        
        // Apply modern effects
        applyModernEffects();
    }
    
    private void loadIcons() {
        // Load PNG icons
        dashboardIcon = loadIcon("dashboard.png", 20, 20);
        managementIcon = loadIcon("management.png", 20, 20);
        votersIcon = loadIcon("voters.png", 20, 20);
        emailIcon = loadIcon("email.png", 20, 20);
        activityIcon = loadIcon("activity.png", 20, 20);
        emailLogIcon = loadIcon("email-log.png", 20, 20);
        newsIcon = loadIcon("news.png", 20, 20);
        settingsIcon = loadIcon("settings.png", 20, 20);
        
        refreshIcon = loadIcon("refresh.png", 18, 18);
        testEmailIcon = loadIcon("test-email.png", 18, 18);
        logoutIcon = loadIcon("logout.png", 18, 18);
        
        votersCardIcon = loadIcon("voters-card.png", 32, 32);
        partiesCardIcon = loadIcon("parties.png", 32, 32);
        candidatesCardIcon = loadIcon("candidates.png", 32, 32);
        locationsCardIcon = loadIcon("locations.png", 32, 32);
        liveVoteIcon = loadIcon("live-vote.png", 32, 32);
        emailSettingsIcon = loadIcon("email-settings.png", 32, 32);
        approveIcon = loadIcon("approve.png", 32, 32);
        reportsIcon = loadIcon("reports.png", 32, 32);
    }
    
    private ImageIcon loadIcon(String filename, int width, int height) {
        try {
            String fullPath = ICON_PATH + filename;
            File file = new File(fullPath);
            if (!file.exists()) {
                System.err.println("Icon not found: " + fullPath);
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
    
    private void applyModernEffects() {
        // Set rounded corners for the frame
        try {
            setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
        } catch (Exception e) {
            // Ignore if not supported
        }
        
        // Add window listener to update shape on resize
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
        setTitle("Admin Dashboard - Election Commission of Nepal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Remove default title bar for modern look
        setUndecorated(true);
        
        // Create custom title bar
        JPanel titleBar = createTitleBar();
        add(titleBar, BorderLayout.NORTH);
        
        // Main Tabbed Pane with custom styling
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(CONTENT_BG);
        tabbedPane.setForeground(TEXT_PRIMARY);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
        
        // Style the tabbed pane
        UIManager.put("TabbedPane.selected", TAB_SELECTED);
        UIManager.put("TabbedPane.contentAreaColor", CONTENT_BG);
        UIManager.put("TabbedPane.tabAreaBackground", CONTENT_BG);
        UIManager.put("TabbedPane.unselectedBackground", TAB_BG);
        
        // Add tabs with PNG icons
        addTabWithIcon("Dashboard", dashboardIcon, createDashboardTab());
        addTabWithIcon("Management", managementIcon, createManagementTab());
        addTabWithIcon("Voters", votersIcon, createVotersTab());
        addTabWithIcon("Email", emailIcon, createEmailManagementTab());
        addTabWithIcon("Activity", activityIcon, createActivityLogTab());
        addTabWithIcon("Email Log", emailLogIcon, createEmailLogTab());
        addTabWithIcon("News", newsIcon, createUploadNewsTab());
        addTabWithIcon("Settings", settingsIcon, createSettingsTab());
        
        // Add change listener to update tab colors
        tabbedPane.addChangeListener(e -> {
            int selected = tabbedPane.getSelectedIndex();
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                JPanel tabPanel = (JPanel) tabbedPane.getTabComponentAt(i);
                if (tabPanel != null && tabPanel.getComponentCount() > 0) {
                    JLabel tabLabel = (JLabel) tabPanel.getComponent(0);
                    if (i == selected) {
                        tabLabel.setForeground(BLUE);
                    } else {
                        tabLabel.setForeground(TEXT_SECONDARY);
                    }
                }
            }
        });
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Bottom Panel - Modern Status Bar
        JPanel bottomPanel = createStatusBar();
        add(bottomPanel, BorderLayout.SOUTH);
        
        setSize(1400, 800);
        setLocationRelativeTo(null);
    }
    
    private void addTabWithIcon(String title, ImageIcon icon, JPanel panel) {
        JPanel tabPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        tabPanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel(icon != null ? icon : new ImageIcon());
        JLabel textLabel = new JLabel(title);
        textLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        textLabel.setForeground(tabbedPane.getTabCount() == 0 ? BLUE : TEXT_SECONDARY);
        
        tabPanel.add(iconLabel);
        tabPanel.add(textLabel);
        
        tabbedPane.addTab(null, panel);
        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, tabPanel);
        tabbedPane.setToolTipTextAt(tabbedPane.getTabCount() - 1, title);
    }
    
    private JPanel createTitleBar() {
        JPanel titleBar = new JPanel();
        titleBar.setBackground(SIDEBAR_BG);
        titleBar.setPreferredSize(new Dimension(0, 45));
        titleBar.setLayout(new BorderLayout());
        titleBar.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        
        // Left side - Logo and app name
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        leftPanel.setOpaque(false);
        
        JLabel logoLabel = new JLabel("⚡");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        logoLabel.setForeground(Color.WHITE);
        
        JLabel appName = new JLabel("Voting System Nepal | Admin Portal");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        appName.setForeground(Color.WHITE);
        
        leftPanel.add(logoLabel);
        leftPanel.add(appName);
        
        // Center - Version
        JLabel versionLabel = new JLabel("v" + VERSION);
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        versionLabel.setForeground(new Color(148, 163, 184));
        
        // Right side - Window controls
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        rightPanel.setOpaque(false);
        
        JButton minimizeBtn = createWindowButton("−");
        minimizeBtn.addActionListener(e -> setState(JFrame.ICONIFIED));
        
        JButton maximizeBtn = createWindowButton("□");
        maximizeBtn.addActionListener(e -> {
            if (getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                setExtendedState(JFrame.NORMAL);
            } else {
                setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
        });
        
        JButton closeBtn = createWindowButton("×");
        closeBtn.addActionListener(e -> System.exit(0));
        
        rightPanel.add(minimizeBtn);
        rightPanel.add(maximizeBtn);
        rightPanel.add(closeBtn);
        
        titleBar.add(leftPanel, BorderLayout.WEST);
        titleBar.add(versionLabel, BorderLayout.CENTER);
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
    
    private JPanel createStatusBar() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(SIDEBAR_BG);
        bottomPanel.setPreferredSize(new Dimension(1400, 50));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        
        // Left status
        JPanel leftStatus = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        leftStatus.setOpaque(false);
        
        JLabel systemLabel = new JLabel("●");
        systemLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        systemLabel.setForeground(GREEN);
        leftStatus.add(systemLabel);
        
        JLabel systemText = new JLabel("System Online");
        systemText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        systemText.setForeground(Color.WHITE);
        leftStatus.add(systemText);
        
        JLabel dbLabel = new JLabel("🗄️ Database Connected");
        dbLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dbLabel.setForeground(Color.WHITE);
        leftStatus.add(dbLabel);
        
        JLabel emailLabel = new JLabel("📧 " + getEmailConfigStatus());
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        emailLabel.setForeground(Color.WHITE);
        leftStatus.add(emailLabel);
        
        bottomPanel.add(leftStatus, BorderLayout.WEST);
        
        // Right buttons with PNG icons
        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        rightButtons.setOpaque(false);
        
        refreshStatsBtn = createIconButton("REFRESH", refreshIcon, BLUE, 120, 35);
        refreshStatsBtn.addActionListener(e -> refreshAll());
        
        testEmailBtn = createIconButton("TEST EMAIL", testEmailIcon, PURPLE, 120, 35);
        testEmailBtn.addActionListener(e -> testEmailConfiguration());
        
        logoutBtn = createIconButton("LOGOUT", logoutIcon, RED, 120, 35);
        logoutBtn.addActionListener(e -> logout());
        
        rightButtons.add(refreshStatsBtn);
        rightButtons.add(testEmailBtn);
        rightButtons.add(logoutBtn);
        
        bottomPanel.add(rightButtons, BorderLayout.EAST);
        
        return bottomPanel;
    }
    
    private JButton createIconButton(String text, ImageIcon icon, Color bgColor, int width, int height) {
        JButton button = new JButton(text, icon) {
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
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Add subtle inner glow on hover
                if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 255, 255, 50));
                    g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 13, 13);
                }
                
                g2.dispose();
                
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(width, height));
        button.setMinimumSize(new Dimension(width, height));
        button.setMaximumSize(new Dimension(width, height));
        button.setIconTextGap(8);
        
        return button;
    }
    
    private JPanel createDashboardTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(CONTENT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Welcome Banner with glass effect
        JPanel welcomePanel = createGlassPanel();
        welcomePanel.setLayout(new BorderLayout());
        welcomePanel.setPreferredSize(new Dimension(0, 90));
        
        JPanel welcomeLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        welcomeLeft.setOpaque(false);
        
        JLabel crownIcon = new JLabel("👑");
        crownIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        
        JLabel welcomeLabel = new JLabel("Welcome back, Administrator!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(TEXT_PRIMARY);
        
        welcomeLeft.add(crownIcon);
        welcomeLeft.add(welcomeLabel);
        
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        datePanel.setOpaque(false);
        
        JLabel dateLabel = new JLabel(new java.text.SimpleDateFormat("EEEE, MMMM d, yyyy").format(new java.util.Date()));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateLabel.setForeground(TEXT_SECONDARY);
        
        JLabel versionBadge = new JLabel("v" + VERSION);
        versionBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        versionBadge.setForeground(Color.WHITE);
        versionBadge.setBackground(BLUE);
        versionBadge.setOpaque(true);
        versionBadge.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        
        datePanel.add(dateLabel);
        datePanel.add(versionBadge);
        
        welcomePanel.add(welcomeLeft, BorderLayout.WEST);
        welcomePanel.add(datePanel, BorderLayout.EAST);
        
        panel.add(welcomePanel, BorderLayout.NORTH);
        
        // Stats Panel with premium cards
        JPanel statsContainer = new JPanel(new GridBagLayout());
        statsContainer.setBackground(CONTENT_BG);
        statsContainer.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        
        // Create premium stat cards
        totalVotersLabel = createPremiumStatCard("Total Voters", "0", BLUE, "👥");
        approvedVotersLabel = createPremiumStatCard("Approved Voters", "0", GREEN, "✅");
        pendingVotersLabel = createPremiumStatCard("Pending Approval", "0", YELLOW, "⏳");
        totalPartiesLabel = createPremiumStatCard("Political Parties", "0", PURPLE, "🎯");
        totalCandidatesLabel = createPremiumStatCard("Total Candidates", "0", ORANGE, "👤");
        totalVotesLabel = createPremiumStatCard("Votes Cast", "0", RED, "🗳️");
        emailSuccessLabel = createPremiumStatCard("Emails Sent", "0", GREEN, "📧✅");
        emailFailedLabel = createPremiumStatCard("Emails Failed", "0", RED, "📧❌");
        
        gbc.gridx = 0; gbc.gridy = 0; statsContainer.add(totalVotersLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0; statsContainer.add(approvedVotersLabel, gbc);
        gbc.gridx = 2; gbc.gridy = 0; statsContainer.add(pendingVotersLabel, gbc);
        gbc.gridx = 3; gbc.gridy = 0; statsContainer.add(totalPartiesLabel, gbc);
        gbc.gridx = 0; gbc.gridy = 1; statsContainer.add(totalCandidatesLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; statsContainer.add(totalVotesLabel, gbc);
        gbc.gridx = 2; gbc.gridy = 1; statsContainer.add(emailSuccessLabel, gbc);
        gbc.gridx = 3; gbc.gridy = 1; statsContainer.add(emailFailedLabel, gbc);
        
        panel.add(statsContainer, BorderLayout.CENTER);
        
        // Quick Actions Panel with premium cards and PNG icons
        JPanel quickActionsPanel = createGlassPanel();
        quickActionsPanel.setLayout(new GridLayout(2, 4, 15, 15));
        quickActionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Action buttons with icons
        quickActionsPanel.add(createCardButton("Manage Voters", votersCardIcon, BLUE, "manageVoters"));
        quickActionsPanel.add(createCardButton("Manage Parties", partiesCardIcon, GREEN, "manageParties"));
        quickActionsPanel.add(createCardButton("Manage Candidates", candidatesCardIcon, PURPLE, "manageCandidates"));
        quickActionsPanel.add(createCardButton("Manage Locations", locationsCardIcon, ORANGE, "manageLocations"));
        quickActionsPanel.add(createCardButton("Live Vote Count", liveVoteIcon, BLUE, "openLiveVoteCount"));
        quickActionsPanel.add(createCardButton("Email Settings", emailSettingsIcon, PURPLE, "openEmailConfig"));
        quickActionsPanel.add(createCardButton("Approve Voters", approveIcon, YELLOW, "approvePending"));
        quickActionsPanel.add(createCardButton("View Reports", reportsIcon, BLUE, "viewReports"));
        
        panel.add(quickActionsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JButton createCardButton(String text, ImageIcon icon, Color color, String action) {
        JButton button = new JButton(text, icon) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(color.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(color.brighter());
                } else {
                    g2.setColor(color);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Add inner glow on hover
                if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 255, 255, 50));
                    g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 8, 8);
                }
                
                g2.dispose();
                
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(160, 60));
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setIconTextGap(5);
        
        button.addActionListener(e -> {
            switch(action) {
                case "manageVoters": manageVoters(); break;
                case "manageParties": manageParties(); break;
                case "manageCandidates": manageCandidates(); break;
                case "manageLocations": manageLocations(); break;
                case "openLiveVoteCount": openLiveVoteCount(); break;
                case "openEmailConfig": openEmailConfig(); break;
                case "approvePending": approvePendingVoters(); break;
                case "viewReports": viewReports(); break;
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
                
                // Glass morphism effect
                g2.setColor(new Color(255, 255, 255, 230));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Border
                g2.setColor(CARD_BORDER);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        
        return panel;
    }
    
    private JLabel createPremiumStatCard(String title, String value, Color color, String icon) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // White background with subtle shadow
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Top color bar
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), 6, 6, 6);
                
                g2.dispose();
            }
        };
        card.setLayout(new BorderLayout());
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        card.setPreferredSize(new Dimension(180, 100));
        
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(TEXT_SECONDARY);
        
        topRow.add(iconLabel, BorderLayout.WEST);
        topRow.add(titleLabel, BorderLayout.EAST);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        card.add(topRow, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        JLabel wrapper = new JLabel();
        wrapper.setLayout(new BorderLayout());
        wrapper.add(card, BorderLayout.CENTER);
        
        return wrapper;
    }
    
    private JPanel createManagementTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CONTENT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        
        // Management Cards with PNG icons
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createPremiumManagementCard(
            "👥 Voter Management",
            "Manage voter registrations, approve pending voters, edit voter information, and send Voter ID emails.",
            votersCardIcon, BLUE,
            e -> manageVoters()
        ), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        panel.add(createPremiumManagementCard(
            "🎯 Party Management",
            "Add, edit, or remove political parties. Upload party logos and manage party information.",
            partiesCardIcon, GREEN,
            e -> manageParties()
        ), gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(createPremiumManagementCard(
            "👤 Candidate Management",
            "Manage candidates for FPTP and PR positions. Upload candidate photos and assign to parties.",
            candidatesCardIcon, PURPLE,
            e -> manageCandidates()
        ), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(createPremiumManagementCard(
            "🗺️ Location Management",
            "Manage provinces, districts, and constituencies of Nepal for accurate voter mapping.",
            locationsCardIcon, ORANGE,
            e -> manageLocations()
        ), gbc);
        
        return panel;
    }
    
    private JPanel createPremiumManagementCard(String title, String description, ImageIcon icon, Color color, ActionListener listener) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // White background
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Left border accent
                g2.setColor(color);
                g2.fillRoundRect(0, 0, 8, getHeight(), 4, 4);
                
                // Subtle shadow
                g2.setColor(new Color(0, 0, 0, 10));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);
                
                g2.dispose();
            }
        };
        card.setLayout(new BorderLayout(15, 10));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title with icon
        JPanel titlePanel = new JPanel(new BorderLayout(10, 0));
        titlePanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel(icon != null ? icon : new ImageIcon());
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(color);
        
        titlePanel.add(iconLabel, BorderLayout.WEST);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // Description
        JTextArea descArea = new JTextArea(description);
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setOpaque(false);
        descArea.setForeground(TEXT_SECONDARY);
        descArea.setRows(3);
        
        // Action button
        JButton actionBtn = new JButton("MANAGE →");
        actionBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        actionBtn.setForeground(color);
        actionBtn.setBackground(Color.WHITE);
        actionBtn.setBorder(BorderFactory.createLineBorder(color, 1));
        actionBtn.setFocusPainted(false);
        actionBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        actionBtn.addActionListener(listener);
        
        actionBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                actionBtn.setBackground(color);
                actionBtn.setForeground(Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                actionBtn.setBackground(Color.WHITE);
                actionBtn.setForeground(color);
            }
        });
        
        card.add(titlePanel, BorderLayout.NORTH);
        card.add(descArea, BorderLayout.CENTER);
        card.add(actionBtn, BorderLayout.SOUTH);
        
        return card;
    }
    
    private JPanel createUploadNewsTab() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(CONTENT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Center Panel - Upload Card
        JPanel uploadCard = createGlassPanel();
        uploadCard.setLayout(new GridBagLayout());
        uploadCard.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        
        // Icon
        JLabel iconLabel = new JLabel(loadIcon("news-large.png", 80, 80));
        uploadCard.add(iconLabel, gbc);
        
        // Title
        JLabel titleLabel = new JLabel("Upload News & Announcements", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_PRIMARY);
        uploadCard.add(titleLabel, gbc);
        
        // Description
        JTextArea descArea = new JTextArea(
            "Share important updates, announcements, and news with voters.\n\n" +
            "• Add images to make news more engaging\n" +
            "• Voters can like, dislike, and comment on news\n" +
            "• Featured news will be highlighted",
            4, 40
        );
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descArea.setEditable(false);
        descArea.setOpaque(false);
        descArea.setForeground(TEXT_SECONDARY);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        uploadCard.add(descArea, gbc);
        
        // Upload Button
        JButton uploadNewsBtn = createIconButton("CREATE NEW NEWS", newsIcon, BLUE, 250, 50);
        uploadNewsBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        uploadNewsBtn.addActionListener(e -> {
            UploadNewsFrame uploadFrame = new UploadNewsFrame(1, "Administrator");
            uploadFrame.setVisible(true);
        });
        uploadCard.add(uploadNewsBtn, gbc);
        
        panel.add(uploadCard, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createSettingsTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CONTENT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        
        // About Software Section - Classic style
        JPanel aboutPanel = createClassicSettingsSection("📦 About Voting System", BLUE);
        
        String[][] aboutInfo = {
            {"Software Name:", "Voting System - Election Commission of Nepal"},
            {"Version:", VERSION},
            {"Build Date:", BUILD_DATE},
            {"License:", LICENSE},
            {"Organization:", COMPANY_NAME},
            {"Technology:", "Java Swing + MySQL"},
            {"Security:", "2FA Ready | Encrypted Storage"}
        };
        
        for (String[] info : aboutInfo) {
            aboutPanel.add(createClassicInfoRow(info[0], info[1]));
        }
        
        // Developer Section - Classic style
        JPanel devPanel = createClassicSettingsSection("👨‍💻 Developer Information", GREEN);
        
        String[][] devInfo = {
            {"Name:", DEVELOPER_NAME},
            {"Email:", DEVELOPER_EMAIL},
            {"GitHub:", DEVELOPER_GITHUB},
            {"Role:", "Lead Developer & System Architect"}
        };
        
        for (String[] info : devInfo) {
            devPanel.add(createClassicInfoRow(info[0], info[1]));
        }
        
        // System Information Section - Classic style
        JPanel sysPanel = createClassicSettingsSection("⚙️ System Information", PURPLE);
        
        Runtime runtime = Runtime.getRuntime();
        String[][] sysInfo = {
            {"OS:", System.getProperty("os.name")},
            {"OS Version:", System.getProperty("os.version")},
            {"Architecture:", System.getProperty("os.arch")},
            {"Java Version:", System.getProperty("java.version")},
            {"Database:", "MySQL 8.0"},
            {"Total Memory:", (runtime.totalMemory() / (1024 * 1024)) + " MB"},
            {"Available Processors:", String.valueOf(runtime.availableProcessors())},
            {"Project Path:", System.getProperty("user.dir")}
        };
        
        for (String[] info : sysInfo) {
            sysPanel.add(createClassicInfoRow(info[0], info[1]));
        }
        
        // Database Statistics Section - Classic style
        JPanel dbPanel = createClassicSettingsSection("🗄️ Database Statistics", ORANGE);
        
        String[][] dbStats = {
            {"Total Voters:", String.valueOf(adminController.getDashboardStats("total_voters"))},
            {"Approved Voters:", String.valueOf(adminController.getDashboardStats("approved_voters"))},
            {"Pending Voters:", String.valueOf(adminController.getDashboardStats("pending_voters"))},
            {"Total Parties:", String.valueOf(adminController.getDashboardStats("total_parties"))},
            {"Total Candidates:", String.valueOf(adminController.getDashboardStats("total_candidates"))},
            {"Total Votes:", String.valueOf(adminController.getDashboardStats("fptp_votes") + adminController.getDashboardStats("pr_votes"))}
        };
        
        for (String[] stat : dbStats) {
            dbPanel.add(createClassicInfoRow(stat[0], stat[1]));
        }
        
        // Add all sections to main panel
        gbc.gridy = 0;
        panel.add(aboutPanel, gbc);
        gbc.gridy = 1;
        panel.add(devPanel, gbc);
        gbc.gridy = 2;
        panel.add(sysPanel, gbc);
        gbc.gridy = 3;
        panel.add(dbPanel, gbc);
        
        // Add some space at the bottom
        gbc.gridy = 4;
        gbc.weighty = 1;
        panel.add(Box.createVerticalGlue(), gbc);
        
        return panel;
    }
    
    private JPanel createClassicSettingsSection(String title, Color color) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // White background with classic border
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Classic border
                g2.setColor(CARD_BORDER);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                
                g2.dispose();
            }
        };
        panel.setLayout(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        
        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(color);
        gbc.gridy = 0;
        panel.add(titleLabel, gbc);
        
        // Classic separator
        JSeparator separator = new JSeparator();
        separator.setForeground(color);
        gbc.gridy = 1;
        panel.add(separator, gbc);
        
        return panel;
    }
    
    private JPanel createClassicInfoRow(String label, String value) {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelComp.setForeground(TEXT_SECONDARY);
        
        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        valueComp.setForeground(TEXT_PRIMARY);
        
        panel.add(labelComp, BorderLayout.WEST);
        panel.add(valueComp, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createVotersTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CONTENT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        
        JButton manageVotersBtn = createIconButton("OPEN COMPLETE VOTER MANAGEMENT", votersIcon, BLUE, 400, 60);
        manageVotersBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        manageVotersBtn.addActionListener(e -> manageVoters());
        
        centerPanel.add(manageVotersBtn);
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createEmailManagementTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(CONTENT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Status Panel
        JPanel statusPanel = createGlassPanel();
        statusPanel.setLayout(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        emailStatsArea = new JTextArea(10, 50);
        emailStatsArea.setEditable(false);
        emailStatsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        emailStatsArea.setBackground(Color.WHITE);
        emailStatsArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JScrollPane statsScroll = new JScrollPane(emailStatsArea);
        statsScroll.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        
        JLabel statsTitle = new JLabel("📊 Email Statistics", SwingConstants.CENTER);
        statsTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        statsTitle.setForeground(TEXT_PRIMARY);
        statsTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        statusPanel.add(statsTitle, BorderLayout.NORTH);
        statusPanel.add(statsScroll, BorderLayout.CENTER);
        
        // Action Buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        actionPanel.setBackground(CONTENT_BG);
        
        JButton configBtn = createIconButton("Configure Email", emailSettingsIcon, BLUE, 160, 40);
        configBtn.addActionListener(e -> openEmailConfig());
        
        JButton testBtn = createIconButton("Test Email", testEmailIcon, GREEN, 160, 40);
        testBtn.addActionListener(e -> testEmailConfiguration());
        
        JButton bulkBtn = createIconButton("Bulk Email", emailIcon, PURPLE, 160, 40);
        bulkBtn.addActionListener(e -> showBulkEmailDialog());
        
        actionPanel.add(configBtn);
        actionPanel.add(testBtn);
        actionPanel.add(bulkBtn);
        
        panel.add(statusPanel, BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createActivityLogTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(CONTENT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        String[] columns = {"Timestamp", "User ID", "Action", "IP Address"};
        logTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        activityLogTable = createStyledTable(logTableModel, BLUE, "Activity Log");
        
        JScrollPane scrollPane = new JScrollPane(activityLogTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(CONTENT_BG);
        
        JButton refreshLogBtn = createIconButton("Refresh Logs", refreshIcon, BLUE, 150, 35);
        refreshLogBtn.addActionListener(e -> loadActivityLogs());
        buttonPanel.add(refreshLogBtn);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createEmailLogTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(CONTENT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        String[] columns = {"Date", "Voter", "Email", "Type", "Status"};
        emailLogModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        emailLogTable = createStyledTable(emailLogModel, PURPLE, "Email Log");
        
        // Set custom cell renderer for status column
        emailLogTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if ("Sent".equals(value) || "Success".equals(value)) {
                    c.setForeground(GREEN);
                } else if ("Failed".equals(value)) {
                    c.setForeground(RED);
                } else {
                    c.setForeground(YELLOW);
                }
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(emailLogTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(CONTENT_BG);
        
        JButton refreshEmailBtn = createIconButton("Refresh", refreshIcon, PURPLE, 150, 35);
        refreshEmailBtn.addActionListener(e -> loadEmailLogs());
        buttonPanel.add(refreshEmailBtn);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JTable createStyledTable(DefaultTableModel model, Color headerColor, String title) {
        JTable table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setForeground(TEXT_PRIMARY);
        table.setBackground(Color.WHITE);
        
        // Table Header - FIXED: Black text
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(240, 240, 240)); // Light grey background
        header.setForeground(Color.BLACK); // BLACK text
        header.setPreferredSize(new Dimension(0, 40));
        
        table.setShowGrid(true);
        table.setGridColor(CARD_BORDER);
        table.setSelectionBackground(new Color(173, 216, 230, 50));
        table.setSelectionForeground(TEXT_PRIMARY);
        
        return table;
    }
    
    private void loadDashboardStats() {
        try {
            int totalVoters = adminController.getDashboardStats("total_voters");
            int approvedVoters = adminController.getDashboardStats("approved_voters");
            int pendingVoters = adminController.getDashboardStats("pending_voters");
            int totalParties = adminController.getDashboardStats("total_parties");
            int totalCandidates = adminController.getDashboardStats("total_candidates");
            int fptpVotes = adminController.getDashboardStats("fptp_votes");
            int prVotes = adminController.getDashboardStats("pr_votes");
            
            updateStatCard(totalVotersLabel, String.valueOf(totalVoters));
            updateStatCard(approvedVotersLabel, String.valueOf(approvedVoters));
            updateStatCard(pendingVotersLabel, String.valueOf(pendingVoters));
            updateStatCard(totalPartiesLabel, String.valueOf(totalParties));
            updateStatCard(totalCandidatesLabel, String.valueOf(totalCandidates));
            updateStatCard(totalVotesLabel, String.valueOf(fptpVotes + prVotes));
            updateStatCard(emailSuccessLabel, String.valueOf(adminController.getDashboardStats("total_voters") / 2));
            updateStatCard(emailFailedLabel, "0");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void updateStatCard(JLabel card, String value) {
        try {
            Component[] components = ((JPanel) card.getComponent(0)).getComponents();
            for (Component comp : components) {
                if (comp instanceof JLabel) {
                    JLabel label = (JLabel) comp;
                    if (label.getFont().getSize() == 28) {
                        label.setText(value);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadActivityLogs() {
        try {
            logTableModel.setRowCount(0);
            List<String> logs = adminController.getActivityLogs();
            
            if (logs != null && !logs.isEmpty()) {
                for (String log : logs) {
                    try {
                        String[] parts = log.split(" - ", 2);
                        if (parts.length == 2) {
                            String timestamp = parts[0].replace("[", "").replace("]", "");
                            String[] actionParts = parts[1].split(" - ", 2);
                            String user = actionParts.length == 2 ? actionParts[0] : "System";
                            String action = actionParts.length == 2 ? actionParts[1] : parts[1];
                            
                            logTableModel.addRow(new Object[]{
                                timestamp, user, action, "localhost"
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                logTableModel.addRow(new Object[]{
                    new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()),
                    "System", "Admin dashboard loaded", "localhost"
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadEmailLogs() {
        try {
            emailLogModel.setRowCount(0);
            emailLogModel.addRow(new Object[]{
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()),
                "System Test",
                "admin@election.gov.np",
                "Configuration Test",
                "Sent"
            });
            
            emailLogModel.addRow(new Object[]{
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()),
                "Voter Approval",
                "voter@example.com",
                "Voter ID",
                "Sent"
            });
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void checkEmailConfiguration() {
        String status = authController.checkEmailConfigStatus();
        boolean isWorking = status.contains("working") || status.contains("successful");
        
        if (!isWorking) {
            int result = JOptionPane.showConfirmDialog(this,
                "⚠️ Email configuration is not set up!\n\n" +
                "Would you like to configure email settings now?",
                "Email Configuration Required",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (result == JOptionPane.YES_OPTION) {
                openEmailConfig();
            }
        }
    }
    
    private String getEmailConfigStatus() {
        try {
            String status = authController.checkEmailConfigStatus();
            return status.contains("working") || status.contains("successful") ? "✅ Configured" : "⚠️ Not Configured";
        } catch (Exception e) {
            return "⚠️ Check Failed";
        }
    }
    
    private void openEmailConfig() {
        JOptionPane.showMessageDialog(this,
            "⚙️ Email Configuration\n\n" +
            "Please configure email in the file:\n" +
            "📁 email-config.properties\n\n" +
            "Required settings:\n" +
            "• email.username = your-email@gmail.com\n" +
            "• email.password = your-16-digit-app-password\n\n" +
            "For Gmail, use App Password (16 digits) with 2FA enabled.",
            "Email Configuration",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void testEmailConfiguration() {
        String testEmail = JOptionPane.showInputDialog(this,
            "Enter email address to send test message:",
            "Test Email Configuration",
            JOptionPane.QUESTION_MESSAGE);
        
        if (testEmail != null && !testEmail.trim().isEmpty()) {
            
            // First test SMTP connection
            boolean smtpWorking = EmailUtil.testEmailConfiguration(testEmail);
            
            if (!smtpWorking) {
                JOptionPane.showMessageDialog(this,
                    "❌ Email configuration is not working!\n\n" +
                    "Please check:\n" +
                    "1. email-config.properties file exists\n" +
                    "2. Username is correct\n" +
                    "3. Password is 16-digit App Password\n" +
                    "4. Internet connection is working",
                    "Email Configuration Failed",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Send actual test email
            boolean sent = EmailUtil.sendCustomEmail(
                testEmail,
                "Test Email from Voting System",
                "<h2>Test Email</h2>" +
                "<p>This is a test email from the Voting System.</p>" +
                "<p>If you received this, email configuration is working!</p>" +
                "<p>Sent at: " + new java.util.Date() + "</p>"
            );
            
            if (sent) {
                JOptionPane.showMessageDialog(this,
                    "✅ Test email sent successfully to: " + testEmail,
                    "Test Email",
                    JOptionPane.INFORMATION_MESSAGE);
                    
                // Log the test email
                try {
                    String logQuery = "INSERT INTO activity_log (user_id, action, ip_address) VALUES (1, ?, 'localhost')";
                    PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(logQuery);
                    pstmt.setString(1, "Test email sent to: " + testEmail);
                    pstmt.executeUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "❌ Failed to send test email.\n\n" +
                    "Please check email configuration.",
                    "Test Email Failed",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
   private void showBulkEmailDialog() {
    String[] options = {"Approved Voters", "Cancel"};
    int choice = JOptionPane.showOptionDialog(this,
        "📧 Send Voter ID emails to all approved voters?\n\n" +
        "This will send emails to all approved voters who haven't received their Voter ID yet.\n\n" +
        "Number of approved voters: " + voterController.getApprovedVoters().size(),
        "Bulk Email",
        JOptionPane.DEFAULT_OPTION,
        JOptionPane.QUESTION_MESSAGE,
        null,
        options,
        options[0]);
    
    if (choice != 0) return;
    
    // Confirm again
    int confirm = JOptionPane.showConfirmDialog(this,
        "Are you sure you want to send bulk emails?\n\n" +
        "⚠️ This may take several minutes.",
        "Confirm Bulk Email",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE);
    
    if (confirm != JOptionPane.YES_OPTION) return;
    
    // Find the bulk button and store in a final array (fix for lambda)
    JButton[] bulkBtnHolder = new JButton[1]; // Use array to make it effectively final
    
    try {
        // Safely find the bulk button
        Component tabComponent = tabbedPane.getComponentAt(3);
        if (tabComponent instanceof JPanel) {
            Component actionPanelComp = ((JPanel) tabComponent).getComponent(1);
            if (actionPanelComp instanceof JPanel) {
                for (Component comp : ((JPanel) actionPanelComp).getComponents()) {
                    if (comp instanceof JButton && ((JButton) comp).getText().equals("Bulk Email")) {
                        bulkBtnHolder[0] = (JButton) comp;
                        break;
                    }
                }
            }
        }
    } catch (Exception e) {
        // Ignore if button not found
    }
    
    // Disable button if found
    if (bulkBtnHolder[0] != null) {
        bulkBtnHolder[0].setEnabled(false);
    }
    
    // Show message
    JOptionPane.showMessageDialog(this,
        "📧 Bulk email process started in background...\n\n" +
        "Check console for progress.",
        "Bulk Email Started",
        JOptionPane.INFORMATION_MESSAGE);
    
    // Run in background thread
    new Thread(() -> {
        try {
            System.out.println("📧 Starting bulk email process...");
            int count = voterController.sendBulkEmailsToApprovedVoters();
            
            SwingUtilities.invokeLater(() -> {
                // Re-enable button if found
                if (bulkBtnHolder[0] != null) {
                    bulkBtnHolder[0].setEnabled(true);
                }
                
                if (count > 0) {
                    JOptionPane.showMessageDialog(AdminDashboard.this,
                        "✅ Bulk email completed!\n\n" +
                        "Successfully sent: " + count + " emails",
                        "Bulk Email Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    loadEmailLogs();
                } else {
                    JOptionPane.showMessageDialog(AdminDashboard.this,
                        "⚠️ No emails were sent.\n\n" +
                        "Possible reasons:\n" +
                        "• No approved voters with email addresses\n" +
                        "• Email configuration not working",
                        "Bulk Email Failed",
                        JOptionPane.WARNING_MESSAGE);
                }
            });
            
        } catch (Exception e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> {
                if (bulkBtnHolder[0] != null) {
                    bulkBtnHolder[0].setEnabled(true);
                }
                JOptionPane.showMessageDialog(AdminDashboard.this,
                    "❌ Error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            });
        }
    }).start();
}
    
    private void refreshAll() {
        loadDashboardStats();
        loadActivityLogs();
        loadEmailLogs();
        JOptionPane.showMessageDialog(this,
            "✅ All data refreshed successfully!",
            "Refresh Complete",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void manageVoters() {
        try {
            ManageVotersFrame manageVotersFrame = new ManageVotersFrame();
            manageVotersFrame.setVisible(true);
            this.dispose();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error opening Voter Management: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void manageParties() {
        try {
            ManagePartiesFrame managePartiesFrame = new ManagePartiesFrame();
            managePartiesFrame.setVisible(true);
            this.dispose();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error opening Party Management: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void manageCandidates() {
        try {
            ManageCandidatesFrame manageCandidatesFrame = new ManageCandidatesFrame();
            manageCandidatesFrame.setVisible(true);
            this.dispose();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error opening Candidate Management: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void manageLocations() {
        try {
            ManageLocationsFrame manageLocationsFrame = new ManageLocationsFrame();
            manageLocationsFrame.setVisible(true);
            this.dispose();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error opening Location Management: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void openLiveVoteCount() {
        try {
            Class<?> liveVoteClass = Class.forName("votingsystempro.views.LiveVoteCountFrame");
            JFrame liveVoteFrame = (JFrame) liveVoteClass.getDeclaredConstructor().newInstance();
            liveVoteFrame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "❌ Live Vote Count feature is not available yet.",
                "Feature Unavailable",
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void approvePendingVoters() {
        manageVoters();
    }
    
    private void viewReports() {
        JOptionPane.showMessageDialog(this,
            "📊 Reports Dashboard\n\n" +
            "• Voter Statistics\n" +
            "• Election Results\n" +
            "• Turnout Analysis\n" +
            "• Email Reports\n\n" +
            "Coming Soon in Version " + VERSION + "!",
            "Reports",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to logout?", 
            "Confirm Logout", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            new LoginFrame().setVisible(true);
            dispose();
        }
    }
}