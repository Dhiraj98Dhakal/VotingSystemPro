package votingsystempro.views;

import votingsystempro.controllers.CandidateController;
import votingsystempro.controllers.VoterController;
import votingsystempro.controllers.PartyController;
import votingsystempro.models.Candidate;
import votingsystempro.models.Party;
import votingsystempro.models.Voter;
import votingsystempro.utils.ImageUtil;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.util.List;

public class VotingFrame extends JFrame {
    private int voterId;
    private String voteType; // "fptp" or "pr"
    private CandidateController candidateController;
    private VoterController voterController;
    private PartyController partyController;
    private Voter voter;
    
    private JLabel titleLabel, instructionLabel, voterInfoLabel;
    private JPanel candidatesPanel;
    private ButtonGroup voteGroup;
    private JButton submitButton, cancelButton;
    private JScrollPane scrollPane;
    private JProgressBar progressBar;
    
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
    
    private final Color FPTP_COLOR = new Color(46, 204, 113); // Green for FPTP
    private final Color PR_COLOR = new Color(155, 89, 182); // Purple for PR
    
    // Icon paths
    private final String ICON_PATH = "C:\\Users\\KKKKK\\Documents\\NetBeansProjects\\VotingSystemPro\\resources\\icons\\";
    
    public VotingFrame(int voterId, String voteType) {
        this.voterId = voterId;
        this.voteType = voteType;
        this.candidateController = new CandidateController();
        this.voterController = new VoterController();
        this.partyController = new PartyController();
        
        loadVoterData();
        initComponents();
        loadCandidates();
        applyModernEffects();
    }
    
    private void applyModernEffects() {
        // Set rounded corners for the frame
        try {
            setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
        } catch (Exception e) {
            // Ignore if not supported
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
    
    private void loadVoterData() {
        voter = voterController.getVoterById(voterId);
    }
    
    private void initComponents() {
        setTitle(voteType.equals("fptp") ? "FPTP Voting - Election Commission of Nepal" : "PR Voting - Election Commission of Nepal");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Remove default title bar for modern look
        setUndecorated(true);
        
        // Custom title bar
        JPanel titleBar = createTitleBar();
        add(titleBar, BorderLayout.NORTH);
        
        // Main Content Panel
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(CONTENT_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Voter Info Card
        JPanel voterInfoCard = createVoterInfoCard();
        mainPanel.add(voterInfoCard, BorderLayout.NORTH);
        
        // Center Panel - Candidates
        JPanel centerPanel = createCandidatesPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Bottom Panel - Buttons
        JPanel bottomPanel = createButtonPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Status Bar
        JPanel statusBar = createStatusBar();
        add(statusBar, BorderLayout.SOUTH);
        
        setSize(1400, 850);
        setLocationRelativeTo(null);
    }
    
    private JPanel createTitleBar() {
        JPanel titleBar = new JPanel();
        titleBar.setBackground(SIDEBAR_BG);
        titleBar.setPreferredSize(new Dimension(0, 45));
        titleBar.setLayout(new BorderLayout());
        titleBar.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        
        // Left side - Logo and title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        leftPanel.setOpaque(false);
        
        JLabel logoLabel = new JLabel(voteType.equals("fptp") ? "🗳️" : "📊");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        logoLabel.setForeground(Color.WHITE);
        
        String voteTypeDisplay = voteType.equals("fptp") ? "FPTP Voting" : "PR Voting";
        JLabel titleLabel = new JLabel(voteTypeDisplay);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        
        leftPanel.add(logoLabel);
        leftPanel.add(titleLabel);
        
        // Center - voter name
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        centerPanel.setOpaque(false);
        
        JLabel voterIcon = new JLabel("👤");
        voterIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        voterIcon.setForeground(Color.WHITE);
        
        JLabel voterNameLabel = new JLabel(voter != null ? voter.getFullName() : "Voter");
        voterNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        voterNameLabel.setForeground(new Color(148, 163, 184));
        
        centerPanel.add(voterIcon);
        centerPanel.add(voterNameLabel);
        
        // Right side - Window controls (ALL 3 BUTTONS)
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
        closeBtn.addActionListener(e -> cancelVoting());
        
        rightPanel.add(minimizeBtn);
        rightPanel.add(maximizeBtn);
        rightPanel.add(closeBtn);
        
        titleBar.add(leftPanel, BorderLayout.WEST);
        titleBar.add(centerPanel, BorderLayout.CENTER);
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
        bottomPanel.setPreferredSize(new Dimension(1400, 35));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        
        JPanel leftStatus = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        leftStatus.setOpaque(false);
        
        JLabel versionLabel = new JLabel(voteType.equals("fptp") ? 
            "🗳️ FPTP Voting | Election Commission of Nepal" : 
            "📊 PR Voting | Election Commission of Nepal");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        versionLabel.setForeground(new Color(148, 163, 184));
        leftStatus.add(versionLabel);
        
        bottomPanel.add(leftStatus, BorderLayout.WEST);
        
        return bottomPanel;
    }
    
    private JPanel createVoterInfoCard() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // White background with subtle shadow
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Top color bar based on vote type
                g2.setColor(voteType.equals("fptp") ? FPTP_COLOR : PR_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), 6, 6, 6);
                
                g2.dispose();
            }
        };
        panel.setLayout(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setPreferredSize(new Dimension(0, 80));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Voter photo (small)
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.1;
        JLabel photoLabel = new JLabel();
        photoLabel.setPreferredSize(new Dimension(50, 50));
        photoLabel.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 1));
        photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        if (voter != null && voter.getPhotoPath() != null) {
            ImageIcon icon = ImageUtil.createImageIcon(voter.getPhotoPath(), 48, 48);
            if (icon != null) {
                photoLabel.setIcon(icon);
                photoLabel.setText("");
            } else {
                photoLabel.setText("📷");
                photoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
            }
        } else {
            photoLabel.setText("📷");
            photoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        }
        panel.add(photoLabel, gbc);
        
        // Voter details
        gbc.gridx = 1; gbc.weightx = 0.7;
        JPanel detailsPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        detailsPanel.setOpaque(false);
        
        if (voter != null) {
            detailsPanel.add(createInfoLabel("👤 Name:", voter.getFullName()));
            detailsPanel.add(createInfoLabel("🆔 Voter ID:", "VOT" + voter.getUserId()));
            detailsPanel.add(createInfoLabel("📍 Location:", voterController.getConstituencyNumber(voter.getConstituencyId())));
            detailsPanel.add(createInfoLabel("📞 Phone:", voter.getPhoneNumber()));
        }
        
        panel.add(detailsPanel, gbc);
        
        // Vote type badge
        gbc.gridx = 2; gbc.weightx = 0.2;
        JPanel badgePanel = new JPanel();
        badgePanel.setBackground(voteType.equals("fptp") ? FPTP_COLOR : PR_COLOR);
        badgePanel.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        badgePanel.setLayout(new BorderLayout());
        
        JLabel badgeLabel = new JLabel(voteType.equals("fptp") ? "🗳️ FPTP VOTE" : "📊 PR VOTE");
        badgeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        badgeLabel.setForeground(Color.WHITE);
        badgePanel.add(badgeLabel, BorderLayout.CENTER);
        
        panel.add(badgePanel, gbc);
        
        return panel;
    }
    
    private JLabel createInfoLabel(String label, String value) {
        JLabel infoLabel = new JLabel("<html><b>" + label + "</b> " + (value != null ? value : "N/A") + "</html>");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoLabel.setForeground(TEXT_PRIMARY);
        return infoLabel;
    }
    
    private JPanel createCandidatesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel headerLabel = new JLabel(voteType.equals("fptp") ? 
            "🗳️ Select Your Candidate (FPTP)" : 
            "📊 Select Your Party (PR)");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(voteType.equals("fptp") ? FPTP_COLOR : PR_COLOR);
        
        headerPanel.add(headerLabel, BorderLayout.WEST);
        
        // Progress indicator
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
        progressBar.setPreferredSize(new Dimension(100, 20));
        headerPanel.add(progressBar, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Instructions
        instructionLabel = new JLabel("Please select your " + 
            (voteType.equals("fptp") ? "candidate" : "party") + 
            " and click SUBMIT VOTE");
        instructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        instructionLabel.setForeground(TEXT_SECONDARY);
        instructionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(instructionLabel, BorderLayout.CENTER);
        
        // Candidates Panel (will be populated dynamically)
        candidatesPanel = new JPanel();
        candidatesPanel.setLayout(new BoxLayout(candidatesPanel, BoxLayout.Y_AXIS));
        candidatesPanel.setBackground(Color.WHITE);
        
        scrollPane = new JScrollPane(candidatesPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        panel.add(scrollPane, BorderLayout.SOUTH);
        
        return panel;
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
                
                if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 255, 255, 50));
                    g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 6, 6);
                }
                
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
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        submitButton = createStyledButton("✅ SUBMIT VOTE", GREEN, 180, 45);
        submitButton.addActionListener(e -> submitVote());
        
        cancelButton = createStyledButton("❌ CANCEL", RED, 150, 45);
        cancelButton.addActionListener(e -> cancelVoting());
        
        panel.add(submitButton);
        panel.add(cancelButton);
        
        return panel;
    }
    
    private void loadCandidates() {
        candidatesPanel.removeAll();
        voteGroup = new ButtonGroup();
        
        progressBar.setVisible(true);
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                if (voteType.equals("fptp")) {
                    loadFPTPCandidates();
                } else {
                    loadPRParties();
                }
                return null;
            }
            
            @Override
            protected void done() {
                candidatesPanel.revalidate();
                candidatesPanel.repaint();
                progressBar.setVisible(false);
            }
        };
        worker.execute();
    }
    
    private void loadFPTPCandidates() {
        List<Candidate> candidates = candidateController.getCandidatesByConstituency(
            voter != null ? voter.getConstituencyId() : 0, "fptp");
        
        if (candidates == null || candidates.isEmpty()) {
            JPanel emptyPanel = createEmptyStatePanel(
                "❌ No Candidates Available",
                "There are no candidates for your constituency at this time.",
                FPTP_COLOR
            );
            candidatesPanel.add(emptyPanel);
            submitButton.setEnabled(false);
        } else {
            for (Candidate candidate : candidates) {
                JPanel candidatePanel = createModernCandidatePanel(candidate);
                candidatesPanel.add(candidatePanel);
                candidatesPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
            submitButton.setEnabled(true);
        }
    }
    
    private void loadPRParties() {
        List<Party> parties = partyController.getAllParties();
        
        if (parties == null || parties.isEmpty()) {
            JPanel emptyPanel = createEmptyStatePanel(
                "❌ No Parties Available",
                "There are no parties registered for PR voting.",
                PR_COLOR
            );
            candidatesPanel.add(emptyPanel);
            submitButton.setEnabled(false);
        } else {
            for (Party party : parties) {
                JPanel partyPanel = createModernPartyPanel(party);
                candidatesPanel.add(partyPanel);
                candidatesPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
            submitButton.setEnabled(true);
        }
    }
    
    private JPanel createEmptyStatePanel(String title, String message, Color color) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 1));
        panel.setPreferredSize(new Dimension(500, 200));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        
        JLabel iconLabel = new JLabel("😔");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        gbc.gridy = 0;
        panel.add(iconLabel, gbc);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(color);
        gbc.gridy = 1;
        panel.add(titleLabel, gbc);
        
        JLabel msgLabel = new JLabel(message);
        msgLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        msgLabel.setForeground(TEXT_SECONDARY);
        gbc.gridy = 2;
        panel.add(msgLabel, gbc);
        
        return panel;
    }
    
    private JPanel createModernCandidatePanel(Candidate candidate) {
        JPanel panel = new JPanel(new BorderLayout(15, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(800, 120));
        
        // Radio button with custom styling
        JRadioButton radioButton = new JRadioButton();
        radioButton.setActionCommand(String.valueOf(candidate.getCandidateId()));
        radioButton.setBackground(Color.WHITE);
        radioButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        radioButton.setFocusPainted(false);
        voteGroup.add(radioButton);
        
        // Photo with rounded corners
        JPanel photoContainer = new JPanel(new BorderLayout());
        photoContainer.setBackground(Color.WHITE);
        photoContainer.setBorder(BorderFactory.createLineBorder(FPTP_COLOR, 2));
        photoContainer.setPreferredSize(new Dimension(80, 80));
        
        JLabel photoLabel = new JLabel();
        photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        photoLabel.setPreferredSize(new Dimension(76, 76));
        
        if (candidate.getPhotoPath() != null) {
            ImageIcon icon = ImageUtil.createImageIcon(candidate.getPhotoPath(), 70, 70);
            if (icon != null) {
                photoLabel.setIcon(icon);
                photoLabel.setText("");
            } else {
                photoLabel.setText("📷");
                photoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
            }
        } else {
            photoLabel.setText("📷");
            photoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        }
        photoContainer.add(photoLabel, BorderLayout.CENTER);
        
        // Info Panel
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 10, 2, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        
        // Candidate name
        JLabel nameLabel = new JLabel(candidate.getCandidateName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(TEXT_PRIMARY);
        gbc.gridy = 0;
        infoPanel.add(nameLabel, gbc);
        
        // Party name with color
        JLabel partyLabel = new JLabel("🏛️ " + (candidate.getPartyName() != null ? candidate.getPartyName() : "Independent"));
        partyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        partyLabel.setForeground(FPTP_COLOR);
        gbc.gridy = 1;
        infoPanel.add(partyLabel, gbc);
        
        // Bio
        String bio = candidate.getBiography();
        if (bio != null && bio.length() > 80) {
            bio = bio.substring(0, 80) + "...";
        }
        JLabel bioLabel = new JLabel("📝 " + (bio != null ? bio : "No biography available"));
        bioLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        bioLabel.setForeground(TEXT_SECONDARY);
        gbc.gridy = 2;
        infoPanel.add(bioLabel, gbc);
        
        panel.add(radioButton, BorderLayout.WEST);
        panel.add(photoContainer, BorderLayout.CENTER);
        panel.add(infoPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createModernPartyPanel(Party party) {
        JPanel panel = new JPanel(new BorderLayout(15, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(800, 100));
        
        // Radio button with custom styling
        JRadioButton radioButton = new JRadioButton();
        radioButton.setActionCommand(String.valueOf(party.getPartyId()));
        radioButton.setBackground(Color.WHITE);
        radioButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        radioButton.setFocusPainted(false);
        voteGroup.add(radioButton);
        
        // Logo with rounded corners
        JPanel logoContainer = new JPanel(new BorderLayout());
        logoContainer.setBackground(Color.WHITE);
        logoContainer.setBorder(BorderFactory.createLineBorder(PR_COLOR, 2));
        logoContainer.setPreferredSize(new Dimension(80, 80));
        
        JLabel logoLabel = new JLabel();
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setPreferredSize(new Dimension(76, 76));
        
        if (party.getPartyLogoPath() != null) {
            ImageIcon icon = ImageUtil.createImageIcon(party.getPartyLogoPath(), 70, 70);
            if (icon != null) {
                logoLabel.setIcon(icon);
                logoLabel.setText("");
            } else {
                logoLabel.setText("🏛️");
                logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
            }
        } else {
            logoLabel.setText("🏛️");
            logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        }
        logoContainer.add(logoLabel, BorderLayout.CENTER);
        
        // Info Panel
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 10, 2, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        
        // Party name
        JLabel nameLabel = new JLabel(party.getPartyName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(TEXT_PRIMARY);
        gbc.gridy = 0;
        infoPanel.add(nameLabel, gbc);
        
        // Description
        String desc = party.getDescription();
        if (desc != null && desc.length() > 80) {
            desc = desc.substring(0, 80) + "...";
        }
        JLabel descLabel = new JLabel("📋 " + (desc != null ? desc : "No description available"));
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(TEXT_SECONDARY);
        gbc.gridy = 1;
        infoPanel.add(descLabel, gbc);
        
        panel.add(radioButton, BorderLayout.WEST);
        panel.add(logoContainer, BorderLayout.CENTER);
        panel.add(infoPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private void submitVote() {
        ButtonModel selected = voteGroup.getSelection();
        if (selected == null) {
            showWarning("Please select a " + (voteType.equals("fptp") ? "candidate" : "party") + " to vote");
            return;
        }
        
        int selectedId = Integer.parseInt(selected.getActionCommand());
        
        // Confirmation dialog with custom styling
        String message = voteType.equals("fptp") ? 
            "🗳️ Are you sure you want to cast your FPTP vote?\n\n⚠️ This action cannot be undone." :
            "📊 Are you sure you want to cast your PR vote?\n\n⚠️ This action cannot be undone.";
        
        String title = voteType.equals("fptp") ? "Confirm FPTP Vote" : "Confirm PR Vote";
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            message, 
            title, 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            submitButton.setEnabled(false);
            progressBar.setVisible(true);
            
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    if (voteType.equals("fptp")) {
                        return candidateController.castVoteFPTP(voterId, selectedId);
                    } else {
                        return candidateController.castVotePR(voterId, selectedId);
                    }
                }
                
                @Override
                protected void done() {
                    try {
                        if (get()) {
                            showSuccess("✅ Your vote has been recorded successfully!\n\nThank you for participating in the election.");
                            
                            // Return to dashboard
                            new VoterDashboard(voterId).setVisible(true);
                            dispose();
                        } else {
                            showError("❌ You have already voted or an error occurred.");
                            submitButton.setEnabled(true);
                        }
                    } catch (Exception e) {
                        showError("Error: " + e.getMessage());
                        submitButton.setEnabled(true);
                    } finally {
                        progressBar.setVisible(false);
                    }
                }
            };
            worker.execute();
        }
    }
    
    private void cancelVoting() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "❓ Are you sure you want to cancel voting?\n\nYour vote will not be recorded.",
            "Cancel Voting", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            new VoterDashboard(voterId).setVisible(true);
            dispose();
        }
    }
    
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }
}