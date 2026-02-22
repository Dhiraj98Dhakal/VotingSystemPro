package votingsystempro.views;

import votingsystempro.controllers.VoterController;
import votingsystempro.controllers.AuthController;
import votingsystempro.models.Voter;
import votingsystempro.utils.PDFGenerator;
import votingsystempro.utils.ImageUtil;
import votingsystempro.views.voter.NewsViewerFrame;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Ellipse2D;
import java.io.File;

public class VoterDashboard extends JFrame {
    private int voterId;
    private VoterController voterController;
    private AuthController authController;
    private Voter voter;
    
    // UI Components
    private JLabel photoLabel;
    private JLabel welcomeLabel;
    private JLabel nameValue, ageValue, citizenshipValue;
    private JLabel fatherValue, motherValue, addressValue, phoneValue, emailValue;
    private JLabel provinceValue, districtValue, constituencyValue;
    private JLabel fptpStatusValue, prStatusValue, emailStatusLabel;
    private JLabel voterIdLabel, profileNameLabel;
    
    private JButton voteFPTPButton, votePRButton, downloadPDFButton;
    private JButton resendEmailButton;
    private JButton newsButton, electionInfoButton, supportButton, logoutButton;
    
    private JPanel mainContentPanel, sidebar, contentWrapper;
    private JProgressBar votingProgressBar;
    private JLabel progressTextLabel;
    private JSplitPane splitPane;
    private JScrollPane mainScrollPane;
    
    // Modern premium color scheme
    private final Color SIDEBAR_BG = new Color(15, 23, 42); // #0F172A - Dark blue
    private final Color SIDEBAR_HOVER = new Color(30, 41, 59); // #1E293B
    private final Color SIDEBAR_ACTIVE = new Color(56, 189, 248); // #38BDF8
    private final Color CONTENT_BG = new Color(248, 250, 252); // #F8FAFC
    private final Color CARD_BG = new Color(255, 255, 255, 240); // White with transparency
    private final Color CARD_BORDER = new Color(226, 232, 240); // #E2E8F0
    private final Color TEXT_PRIMARY = new Color(15, 23, 42); // #0F172A
    private final Color TEXT_SECONDARY = new Color(71, 85, 105); // #475569
    private final Color TEXT_MUTED = new Color(148, 163, 184); // #94A3B4
    
    // Accent colors
    private final Color FPTP_GREEN = new Color(34, 197, 94); // #22C55E
    private final Color PR_PURPLE = new Color(168, 85, 247); // #A855F7
    private final Color PENDING_YELLOW = new Color(234, 179, 8); // #EAB308
    private final Color SUCCESS_GREEN = new Color(34, 197, 94); // #22C55E
    private final Color DANGER_RED = new Color(239, 68, 68); // #EF4444
    private final Color BLUE = new Color(59, 130, 246); // #3B82F6
    private final Color GRADIENT_START = new Color(59, 130, 246); // #3B82F6
    private final Color GRADIENT_END = new Color(168, 85, 247); // #A855F7
    
    public VoterDashboard(int voterId) {
        this.voterId = voterId;
        this.voterController = new VoterController();
        this.authController = new AuthController();
        
        loadVoterData();
        initComponents();
        applyModernEffects();
    }
    
    private void loadVoterData() {
        voter = voterController.getVoterById(voterId);
        if (voter == null) {
            JOptionPane.showMessageDialog(this, 
                "Error loading voter data. Please login again.",
                "Error", JOptionPane.ERROR_MESSAGE);
            new LoginFrame().setVisible(true);
            dispose();
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
        setTitle("Voter Portal - Election Commission of Nepal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setUndecorated(true); // Remove default title bar for modern look
        
        // Set minimum size
        setMinimumSize(new Dimension(1000, 600));
        
        // Create custom title bar
        JPanel titleBar = createTitleBar();
        add(titleBar, BorderLayout.NORTH);
        
        // ==================== SIDEBAR (Left) ====================
        sidebar = createSidebar();
        
        // ==================== MAIN CONTENT (Right) ====================
        contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(CONTENT_BG);
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        // Slim Header with gradient
        JPanel header = createHeader();
        contentWrapper.add(header, BorderLayout.NORTH);
        
        // Main Content Area with Scroll Pane
        mainContentPanel = createMainContent();
        mainScrollPane = new JScrollPane(mainContentPanel);
        mainScrollPane.setBorder(null);
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainScrollPane.getViewport().setBackground(CONTENT_BG);
        
        // Modern scrollbar styling
        mainScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        mainScrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(200, 200, 200);
            }
        });
        
        contentWrapper.add(mainScrollPane, BorderLayout.CENTER);
        
        // ==================== SPLIT PANE ====================
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar, contentWrapper);
        splitPane.setDividerLocation(280);
        splitPane.setDividerSize(2);
        splitPane.setResizeWeight(0.2);
        splitPane.setContinuousLayout(true);
        splitPane.setBorder(null);
        
        // Style the divider
        UIManager.put("SplitPane.dividerSize", 2);
        UIManager.put("SplitPane.background", new Color(226, 232, 240));
        
        add(splitPane, BorderLayout.CENTER);
        
        // Add resize listener
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) {
                adjustLayout();
            }
        });
        
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        
        checkEmailStatus();
    }
    
    private JPanel createTitleBar() {
        JPanel titleBar = new JPanel();
        titleBar.setBackground(SIDEBAR_BG);
        titleBar.setPreferredSize(new Dimension(0, 40));
        titleBar.setLayout(new BorderLayout());
        titleBar.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        
        // Left side - Logo and app name
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        leftPanel.setOpaque(false);
        
        JLabel logoLabel = new JLabel("🗳️");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        logoLabel.setForeground(Color.WHITE);
        
        JLabel appName = new JLabel("Voting System Nepal");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        appName.setForeground(Color.WHITE);
        
        leftPanel.add(logoLabel);
        leftPanel.add(appName);
        
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
    
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setLayout(new BorderLayout());
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        
        // Sidebar Header with Voter Photo - FIXED CIRCULAR PHOTO
        JPanel sidebarHeader = new JPanel();
        sidebarHeader.setBackground(SIDEBAR_BG);
        sidebarHeader.setLayout(new BoxLayout(sidebarHeader, BoxLayout.Y_AXIS));
        
        // Create circular photo using a specialized component
        JPanel photoContainer = new JPanel() {
            private ImageIcon photoIcon = null;
            
            {
                // Load photo in constructor
                if (voter != null && voter.getPhotoPath() != null && !voter.getPhotoPath().isEmpty()) {
                    photoIcon = ImageUtil.createImageIcon(voter.getPhotoPath(), 100, 100);
                }
                setPreferredSize(new Dimension(120, 120));
                setMaximumSize(new Dimension(120, 120));
                setMinimumSize(new Dimension(120, 120));
                setBackground(SIDEBAR_BG);
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Draw glow effect
                g2.setColor(new Color(56, 189, 248, 50));
                g2.fillOval(5, 5, 110, 110);
                
                // Create circular clip
                Shape circle = new Ellipse2D.Float(10, 10, 100, 100);
                g2.setClip(circle);
                
                // Draw white background
                g2.setColor(Color.WHITE);
                g2.fillOval(10, 10, 100, 100);
                
                // Draw photo if available
                if (photoIcon != null) {
                    g2.drawImage(photoIcon.getImage(), 10, 10, 100, 100, null);
                } else {
                    // Draw emoji fallback
                    g2.setClip(null);
                    g2.setColor(Color.WHITE);
                    g2.fillOval(10, 10, 100, 100);
                    
                    g2.setColor(TEXT_SECONDARY);
                    g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
                    FontMetrics fm = g2.getFontMetrics();
                    String emoji = "📷";
                    int x = 10 + (100 - fm.stringWidth(emoji)) / 2;
                    int y = 10 + ((100 - fm.getHeight()) / 2) + fm.getAscent();
                    g2.drawString(emoji, x, y);
                }
                
                // Remove clip for border
                g2.setClip(null);
                
                // Draw outer ring with gradient
                GradientPaint gp = new GradientPaint(0, 0, GRADIENT_START, 120, 120, GRADIENT_END);
                g2.setPaint(gp);
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(10, 10, 100, 100);
                
                g2.dispose();
            }
        };
        
        // Voter Name with gradient text
        JLabel nameLabel = new JLabel(truncateText(voter != null ? voter.getFullName() : "Voter", 20)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create gradient paint
                GradientPaint gp = new GradientPaint(0, 0, new Color(56, 189, 248), 
                    getWidth(), 0, new Color(168, 85, 247));
                g2.setPaint(gp);
                
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2;
                
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Voter ID badge
        JPanel idBadge = new JPanel();
        idBadge.setBackground(new Color(30, 41, 59));
        idBadge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(56, 189, 248, 100), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        idBadge.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel idLabel = new JLabel("VOT" + (voter != null ? voter.getUserId() : "000"));
        idLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        idLabel.setForeground(new Color(56, 189, 248));
        idBadge.add(idLabel);
        
        sidebarHeader.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarHeader.add(photoContainer);
        sidebarHeader.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebarHeader.add(nameLabel);
        sidebarHeader.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarHeader.add(idBadge);
        
        // Sidebar Menu Items
        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(SIDEBAR_BG);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        
        // Dashboard (active by default)
        JButton dashboardBtn = createModernSidebarButton("🏠 Dashboard", true);
        dashboardBtn.addActionListener(e -> {
            // Already on dashboard
        });
        
        // News Button
        newsButton = createModernSidebarButton("📰 News & Updates", false);
        newsButton.addActionListener(e -> openNewsViewer());
        
        // Election Info Button
        electionInfoButton = createModernSidebarButton("📋 Election Info", false);
        electionInfoButton.addActionListener(e -> viewElectionInfo());
        
        // Support Button
        supportButton = createModernSidebarButton("🆘 Contact Support", false);
        supportButton.addActionListener(e -> contactSupport());
        
        // Separator
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(51, 65, 85));
        separator.setMaximumSize(new Dimension(200, 1));
        separator.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Logout Button
        logoutButton = createModernSidebarButton("🚪 Logout", false);
        logoutButton.addActionListener(e -> logout());
        
        menuPanel.add(dashboardBtn);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        menuPanel.add(newsButton);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        menuPanel.add(electionInfoButton);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        menuPanel.add(supportButton);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        menuPanel.add(separator);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        menuPanel.add(logoutButton);
        
        sidebar.add(sidebarHeader, BorderLayout.NORTH);
        sidebar.add(menuPanel, BorderLayout.CENTER);
        
        return sidebar;
    }
    
    private JButton createModernSidebarButton(String text, boolean active) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isRollover() || active) {
                    g2.setColor(active ? SIDEBAR_ACTIVE : SIDEBAR_HOVER);
                    g2.fillRoundRect(5, 2, getWidth() - 10, getHeight() - 4, 10, 10);
                }
                
                super.paintComponent(g);
                g2.dispose();
            }
        };
        
        button.setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 13));
        button.setForeground(active ? Color.WHITE : TEXT_MUTED);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setPreferredSize(new Dimension(220, 45));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        return button;
    }
    
    private JPanel createHeader() {
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Subtle gradient background
                GradientPaint gp = new GradientPaint(0, 0, new Color(255, 255, 255, 200), 
                    getWidth(), 0, new Color(248, 250, 252, 200));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                g2.dispose();
            }
        };
        header.setBackground(CONTENT_BG);
        header.setBorder(BorderFactory.createEmptyBorder(20, 25, 15, 25));
        header.setLayout(new BorderLayout());
        
        // Welcome Text with gradient effect
        welcomeLabel = new JLabel("Welcome back, " + truncateText(voter != null ? voter.getFullName() : "Voter", 30)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient text
                GradientPaint gp = new GradientPaint(0, 0, GRADIENT_START, getWidth(), 0, GRADIENT_END);
                g2.setPaint(gp);
                
                FontMetrics fm = g2.getFontMetrics();
                int x = 0;
                int y = (getHeight() + fm.getAscent()) / 2;
                
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcomeLabel.setForeground(TEXT_PRIMARY);
        
        // Date and Status with modern badge
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);
        
        JLabel dateLabel = new JLabel(new java.text.SimpleDateFormat("EEEE, MMMM d, yyyy").format(new java.util.Date()));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateLabel.setForeground(TEXT_SECONDARY);
        
        JPanel statusBadge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw pill background
                g2.setColor(voter != null && voter.isApproved() ? 
                    new Color(34, 197, 94, 50) : new Color(234, 179, 8, 50));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                g2.dispose();
            }
        };
        statusBadge.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 2));
        statusBadge.setOpaque(false);
        
        JLabel statusDot = new JLabel("●");
        statusDot.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusDot.setForeground(voter != null && voter.isApproved() ? SUCCESS_GREEN : PENDING_YELLOW);
        
        JLabel statusText = new JLabel(voter != null && voter.isApproved() ? "Verified" : "Pending");
        statusText.setFont(new Font("Segoe UI", Font.BOLD, 11));
        statusText.setForeground(voter != null && voter.isApproved() ? SUCCESS_GREEN : PENDING_YELLOW);
        
        statusBadge.add(statusDot);
        statusBadge.add(statusText);
        
        rightPanel.add(dateLabel);
        rightPanel.add(statusBadge);
        
        header.add(welcomeLabel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);
        
        return header;
    }
    
    private JPanel createMainContent() {
        JPanel content = new JPanel();
        content.setBackground(CONTENT_BG);
        content.setBorder(BorderFactory.createEmptyBorder(0, 25, 25, 25));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        
        // ===== STATUS CARD (Glass morphism effect) =====
        JPanel statusCard = createGlassCard();
        statusCard.setLayout(new BorderLayout(20, 0));
        statusCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        
        JPanel statusLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        statusLeft.setOpaque(false);
        
        JLabel statusIcon = new JLabel("🎯");
        statusIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        
        JPanel statusTextPanel = new JPanel(new GridLayout(2, 1));
        statusTextPanel.setOpaque(false);
        
        JLabel statusTitle = new JLabel("Voting Progress");
        statusTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        statusTitle.setForeground(TEXT_PRIMARY);
        
        progressTextLabel = new JLabel(getVotingProgressText());
        progressTextLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        progressTextLabel.setForeground(TEXT_SECONDARY);
        
        statusTextPanel.add(statusTitle);
        statusTextPanel.add(progressTextLabel);
        
        statusLeft.add(statusIcon);
        statusLeft.add(statusTextPanel);
        
        // Modern progress bar
        votingProgressBar = new JProgressBar(0, 2) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background
                g2.setColor(new Color(226, 232, 240));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Progress
                int progressWidth = (int) (getWidth() * ((double) getValue() / getMaximum()));
                g2.setColor(getForeground());
                g2.fillRoundRect(0, 0, progressWidth, getHeight(), 15, 15);
                
                g2.dispose();
            }
        };
        votingProgressBar.setValue(getVotingProgress());
        votingProgressBar.setStringPainted(false);
        votingProgressBar.setForeground(getVotingProgress() == 2 ? SUCCESS_GREEN : PENDING_YELLOW);
        votingProgressBar.setBackground(new Color(226, 232, 240));
        votingProgressBar.setPreferredSize(new Dimension(300, 12));
        votingProgressBar.setBorder(null);
        
        // Percentage label
        JLabel percentLabel = new JLabel(getVotingProgressPercent());
        percentLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        percentLabel.setForeground(getVotingProgress() == 2 ? SUCCESS_GREEN : PENDING_YELLOW);
        
        JPanel progressPanel = new JPanel(new BorderLayout(10, 0));
        progressPanel.setOpaque(false);
        progressPanel.add(votingProgressBar, BorderLayout.CENTER);
        progressPanel.add(percentLabel, BorderLayout.EAST);
        
        statusCard.add(statusLeft, BorderLayout.WEST);
        statusCard.add(progressPanel, BorderLayout.CENTER);
        
        content.add(statusCard);
        content.add(Box.createRigidArea(new Dimension(0, 25)));
        
        // ===== VOTER INFORMATION CARDS (Premium Cards) =====
        JPanel infoGrid = new JPanel(new GridLayout(2, 2, 20, 20));
        infoGrid.setBackground(CONTENT_BG);
        
        // Personal Details Card
        infoGrid.add(createPremiumCard("👤 Personal Details", new String[]{
            "Full Name:", voter != null ? voter.getFullName() : "N/A",
            "Age:", voter != null ? voter.getAge() + " years" : "N/A",
            "DOB:", voter != null ? new java.text.SimpleDateFormat("dd MMM yyyy").format(voter.getDateOfBirth()) : "N/A",
            "Citizenship:", voter != null ? voter.getCitizenshipNumber() : "N/A",
            "Father:", voter != null ? voter.getFatherName() : "N/A",
            "Mother:", voter != null ? voter.getMotherName() : "N/A"
        }));
        
        // Contact Card
        infoGrid.add(createPremiumCard("📞 Contact Details", new String[]{
            "Address:", voter != null ? voter.getAddress() : "N/A",
            "Phone:", voter != null ? voter.getPhoneNumber() : "N/A",
            "Email:", voter != null ? voter.getEmail() : "N/A"
        }));
        
        // Location Card
        String provinceName = voter != null ? voterController.getProvinceName(voter.getProvinceId()) : "N/A";
        String districtName = voter != null ? voterController.getDistrictName(voter.getDistrictId()) : "N/A";
        String constituency = voter != null ? voterController.getConstituencyNumber(voter.getConstituencyId()) : "N/A";
        
        infoGrid.add(createPremiumCard("📍 Location Details", new String[]{
            "Province:", provinceName,
            "District:", districtName,
            "Constituency:", constituency
        }));
        
        // Voting Status Card
        String fptpStatus = voter != null && voter.isHasVotedFptp() ? "✓ Completed" : "⏳ Pending";
        String prStatus = voter != null && voter.isHasVotedPr() ? "✓ Completed" : "⏳ Pending";
        
        infoGrid.add(createPremiumCard("🗳️ Voting Status", new String[]{
            "FPTP Vote:", fptpStatus,
            "PR Vote:", prStatus,
            "Registration:", voter != null && voter.isApproved() ? "✓ Approved" : "⏳ Pending"
        }));
        
        content.add(infoGrid);
        content.add(Box.createRigidArea(new Dimension(0, 25)));
        
        // ===== ACTION BUTTONS (Premium) =====
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        actionPanel.setBackground(CONTENT_BG);
        
        voteFPTPButton = createPremiumButton("🗳️ FPTP", FPTP_GREEN);
        voteFPTPButton.setEnabled(voter != null && !voter.isHasVotedFptp() && voter.isApproved());
        voteFPTPButton.addActionListener(e -> openVotingFrame("fptp"));
        
        votePRButton = createPremiumButton("📊 PR", PR_PURPLE);
        votePRButton.setEnabled(voter != null && !voter.isHasVotedPr() && voter.isApproved());
        votePRButton.addActionListener(e -> openVotingFrame("pr"));
        
        downloadPDFButton = createPremiumButton("📥 PDF", BLUE);
        downloadPDFButton.addActionListener(e -> generatePDF());
        
        resendEmailButton = createPremiumButton("📧 Email", PR_PURPLE);
        resendEmailButton.setEnabled(voter != null && voter.isApproved());
        resendEmailButton.addActionListener(e -> resendVoterIdEmail());
        
        actionPanel.add(voteFPTPButton);
        actionPanel.add(votePRButton);
        actionPanel.add(downloadPDFButton);
        actionPanel.add(resendEmailButton);
        
        content.add(actionPanel);
        
        // Email Status with modern styling
        emailStatusLabel = new JLabel(" ", SwingConstants.CENTER);
        emailStatusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        emailStatusLabel.setForeground(TEXT_SECONDARY);
        emailStatusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailStatusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        content.add(emailStatusLabel);
        
        return content;
    }
    
    private JPanel createGlassCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Glass morphism effect
                g2.setColor(new Color(255, 255, 255, 200));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Border
                g2.setColor(new Color(255, 255, 255, 100));
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        return card;
    }
    
    private JPanel createPremiumCard(String title, String[] pairs) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // White background with subtle shadow
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Subtle border
                g2.setColor(new Color(226, 232, 240));
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        card.add(titleLabel, BorderLayout.NORTH);
        
        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        
        for (int i = 0; i < pairs.length; i += 2) {
            JPanel row = new JPanel(new BorderLayout(5, 0));
            row.setOpaque(false);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
            
            JLabel labelComp = new JLabel(pairs[i]);
            labelComp.setFont(new Font("Segoe UI", Font.BOLD, 11));
            labelComp.setForeground(TEXT_SECONDARY);
            
            JLabel valueComp = new JLabel(truncateText(pairs[i + 1], 25));
            valueComp.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            valueComp.setForeground(TEXT_PRIMARY);
            
            row.add(labelComp, BorderLayout.WEST);
            row.add(valueComp, BorderLayout.CENTER);
            
            contentPanel.add(row);
            contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        
        card.add(contentPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JButton createPremiumButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (!isEnabled()) {
                    g2.setColor(new Color(148, 163, 184));
                } else if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Add subtle inner glow on hover
                if (getModel().isRollover() && isEnabled()) {
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
        button.setPreferredSize(new Dimension(130, 38));
        
        return button;
    }
    
    private void adjustLayout() {
        int width = getWidth();
        int sidebarWidth = Math.min(280, (int)(width * 0.18));
        splitPane.setDividerLocation(sidebarWidth);
        splitPane.revalidate();
    }
    
    private String truncateText(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) return text;
        return text.substring(0, maxLength) + "...";
    }
    
    private int getVotingProgress() {
        if (voter == null) return 0;
        int progress = 0;
        if (voter.isHasVotedFptp()) progress++;
        if (voter.isHasVotedPr()) progress++;
        return progress;
    }
    
    private String getVotingProgressText() {
        if (voter == null) return "Not Started";
        if (voter.isHasVotedFptp() && voter.isHasVotedPr()) {
            return "✓ All votes cast";
        } else if (voter.isHasVotedFptp()) {
            return "FPTP: ✓ • PR: ⏳";
        } else if (voter.isHasVotedPr()) {
            return "PR: ✓ • FPTP: ⏳";
        } else {
            return "⏳ Not Started";
        }
    }
    
    private String getVotingProgressPercent() {
        if (voter == null) return "0%";
        int percent = getVotingProgress() * 50;
        return percent + "%";
    }
    
    private void checkEmailStatus() {
        if (voter != null && voter.isApproved()) {
            emailStatusLabel.setText("📧 Voter ID sent to: " + truncateText(voter.getEmail(), 30));
            emailStatusLabel.setForeground(SUCCESS_GREEN);
        } else if (voter != null) {
            emailStatusLabel.setText("📧 Email pending approval");
            emailStatusLabel.setForeground(PENDING_YELLOW);
        }
    }
    
    private void resendVoterIdEmail() {
        if (voter == null) return;
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Resend Voter ID to " + voter.getEmail() + "?",
            "Confirm Resend",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean sent = authController.sendVoterIdEmail(voter.getVoterId());
            
            if (sent) {
                JOptionPane.showMessageDialog(this,
                    "✅ Voter ID resent successfully to " + voter.getEmail(),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "❌ Failed to send email.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void openVotingFrame(String voteType) {
        if (voter == null) return;
        
        String type = voteType.equals("fptp") ? "FPTP" : "PR";
        
        if ((voteType.equals("fptp") && voter.isHasVotedFptp()) ||
            (voteType.equals("pr") && voter.isHasVotedPr())) {
            JOptionPane.showMessageDialog(this,
                "You have already cast your " + type + " vote.",
                "Already Voted",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!voter.isApproved()) {
            JOptionPane.showMessageDialog(this,
                "Your registration is pending approval.",
                "Not Approved",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Cast your " + type + " vote?\nThis action cannot be undone.",
            "Confirm Voting",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            new VotingFrame(voterId, voteType).setVisible(true);
            dispose();
        }
    }
    
    private void generatePDF() {
        if (voter == null) return;
        
        String fileName = "Voter_ID_Card_VOT" + voter.getUserId() + ".pdf";
        String filePath = System.getProperty("user.home") + "/Downloads/" + fileName;
        
        boolean success = PDFGenerator.generateVoterInfoPDF(voter, filePath);
        
        if (success) {
            JOptionPane.showMessageDialog(this,
                "✅ PDF saved to Downloads folder!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            
            int open = JOptionPane.showConfirmDialog(this,
                "Would you like to open the PDF now?",
                "Open PDF",
                JOptionPane.YES_NO_OPTION);
            
            if (open == JOptionPane.YES_OPTION) {
                try {
                    Desktop.getDesktop().open(new java.io.File(filePath));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "❌ Error generating PDF.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void viewElectionInfo() {
        String message = String.format(
            "📋 ELECTION INFORMATION\n\n" +
            "🗳️ Current Elections:\n" +
            "   • FPTP (First Past The Post)\n" +
            "   • PR (Proportional Representation)\n\n" +
            "📍 Your Constituency: %s\n" +
            "📅 Voting Period: Ongoing\n" +
            "⏰ Last Date: December 31, 2024",
            voterController.getConstituencyNumber(voter.getConstituencyId())
        );
        
        JOptionPane.showMessageDialog(this,
            message,
            "Election Information",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void contactSupport() {
        String message = String.format(
            "📞 SUPPORT INFORMATION\n\n" +
            "🏛️ Election Commission of Nepal\n" +
            "📍 Belbari, Morang\n\n" +
            "📧 Email: support@election.gov.np\n" +
            "📞 Phone: +977-9808704655\n" +
            "📱 Hotline: 1618\n\n" +
            "🆔 Your Voter ID: VOT%d\n" +
            "📧 Your Email: %s",
            voter != null ? voter.getUserId() : 0,
            voter != null ? voter.getEmail() : "N/A"
        );
        
        JOptionPane.showMessageDialog(this,
            message,
            "Contact Support",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void openNewsViewer() {
        NewsViewerFrame newsFrame = new NewsViewerFrame(voter);
        newsFrame.setVisible(true);
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            new LoginFrame().setVisible(true);
            dispose();
        }
    }
}