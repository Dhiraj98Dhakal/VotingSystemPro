package votingsystempro.views;

import votingsystempro.controllers.VoteController;
import votingsystempro.models.LiveVoteData;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LiveVoteCountFrame extends JFrame {
    private VoteController voteController;
    
    // Modern premium color scheme
    private final Color SIDEBAR_BG = new Color(15, 23, 42);
    private final Color SIDEBAR_HOVER = new Color(30, 41, 59);
    private final Color CONTENT_BG = new Color(248, 250, 252);
    private final Color CARD_BG = Color.WHITE;
    private final Color CARD_BORDER = new Color(226, 232, 240);
    private final Color TEXT_PRIMARY = new Color(15, 23, 42);
    private final Color TEXT_SECONDARY = new Color(71, 85, 105);
    
    // Accent colors
    private final Color BLUE = new Color(59, 130, 246);
    private final Color GREEN = new Color(34, 197, 94);
    private final Color PURPLE = new Color(168, 85, 247);
    private final Color ORANGE = new Color(249, 115, 22);
    private final Color YELLOW = new Color(234, 179, 8);
    private final Color RED = new Color(239, 68, 68);
    private final Color FPTP_COLOR = new Color(46, 204, 113);
    private final Color PR_COLOR = new Color(155, 89, 182);
    
    // UI Components
    private JLabel totalVotesLabel, fptpVotesLabel, prVotesLabel, lastUpdatedLabel;
    private JLabel turnoutPercentageLabel, leadingPartyLabel, leadingCandidateLabel;
    private JTable fptpResultTable, prResultTable;
    private DefaultTableModel fptpTableModel, prTableModel;
    private JProgressBar voterTurnoutBar;
    private JButton refreshBtn, toggleBtn, exportBtn, closeBtn;
    private JLabel statusIcon;
    
    private Timer refreshTimer;
    private boolean isRefreshing = true;
    private final String VERSION = "2.0.0";
    
    public LiveVoteCountFrame() {
        voteController = new VoteController();
        initComponents();
        applyModernEffects();
        startLiveRefresh();
        loadLiveData();
    }
    
    private void applyModernEffects() {
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
        setTitle("🏛️ Live Vote Counting - Election Commission of Nepal");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setUndecorated(true);
        
        // Custom title bar
        JPanel titleBar = createTitleBar();
        add(titleBar, BorderLayout.NORTH);
        
        // Main Content Panel
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(CONTENT_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Stats Panel
        JPanel statsPanel = createStatsPanel();
        mainPanel.add(statsPanel, BorderLayout.NORTH);
        
        // Results Split Pane
        JSplitPane resultsSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        resultsSplitPane.setDividerLocation(580);
        resultsSplitPane.setBorder(null);
        resultsSplitPane.setBackground(CONTENT_BG);
        
        // FPTP Panel
        JPanel fptpPanel = createFPTPPanel();
        resultsSplitPane.setLeftComponent(fptpPanel);
        
        // PR Panel
        JPanel prPanel = createPRPanel();
        resultsSplitPane.setRightComponent(prPanel);
        
        mainPanel.add(resultsSplitPane, BorderLayout.CENTER);
        
        // Control Panel
        JPanel controlPanel = createControlPanel();
        mainPanel.add(controlPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Status Bar
        JPanel statusBar = createStatusBar();
        add(statusBar, BorderLayout.SOUTH);
        
        setSize(1400, 800);
        setLocationRelativeTo(null);
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                stopLiveRefresh();
                voteController.closeConnection();
            }
        });
    }
    
    private JPanel createTitleBar() {
        JPanel titleBar = new JPanel();
        titleBar.setBackground(SIDEBAR_BG);
        titleBar.setPreferredSize(new Dimension(0, 45));
        titleBar.setLayout(new BorderLayout());
        titleBar.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        leftPanel.setOpaque(false);
        
        JLabel logoLabel = new JLabel("🏛️");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        logoLabel.setForeground(Color.WHITE);
        
        JLabel appName = new JLabel("Live Vote Counting | Election Commission of Nepal");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        appName.setForeground(Color.WHITE);
        
        leftPanel.add(logoLabel);
        leftPanel.add(appName);
        
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 8));
        centerPanel.setOpaque(false);
        
        statusIcon = new JLabel("●");
        statusIcon.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusIcon.setForeground(GREEN);
        
        JLabel liveLabel = new JLabel("LIVE");
        liveLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        liveLabel.setForeground(Color.WHITE);
        
        centerPanel.add(statusIcon);
        centerPanel.add(liveLabel);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        rightPanel.setOpaque(false);
        
        JButton minimizeBtn = createWindowButton("−");
        minimizeBtn.addActionListener(e -> setState(JFrame.ICONIFIED));
        
        JButton closeBtn = new JButton("×");
        closeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setBackground(SIDEBAR_BG);
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.setPreferredSize(new Dimension(35, 30));
        closeBtn.addActionListener(e -> dispose());
        
        closeBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                closeBtn.setBackground(RED);
                closeBtn.setOpaque(true);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                closeBtn.setBackground(SIDEBAR_BG);
                closeBtn.setOpaque(false);
            }
        });
        
        rightPanel.add(minimizeBtn);
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
        
        JLabel versionLabel = new JLabel("v" + VERSION + " | Real-time Results");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        versionLabel.setForeground(new Color(148, 163, 184));
        leftStatus.add(versionLabel);
        
        bottomPanel.add(leftStatus, BorderLayout.WEST);
        
        JPanel rightStatus = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        rightStatus.setOpaque(false);
        
        JLabel electionLabel = new JLabel("🏛️ निर्वाचन आयोग, नेपाल | Election Commission, Nepal");
        electionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        electionLabel.setForeground(new Color(148, 163, 184));
        rightStatus.add(electionLabel);
        
        bottomPanel.add(rightStatus, BorderLayout.EAST);
        
        return bottomPanel;
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CONTENT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        
        totalVotesLabel = createStatCardValue("0");
        JPanel totalVotesCard = createPremiumStatCard("Total Votes Cast", totalVotesLabel, BLUE, "🗳️");
        
        fptpVotesLabel = createStatCardValue("0");
        JPanel fptpCard = createPremiumStatCard("FPTP Votes", fptpVotesLabel, FPTP_COLOR, "✅");
        
        prVotesLabel = createStatCardValue("0");
        JPanel prCard = createPremiumStatCard("PR Votes", prVotesLabel, PR_COLOR, "📊");
        
        // Voter Turnout Card
        JPanel turnoutCard = createPremiumProgressCard("Voter Turnout", YELLOW, "📈");
        
        leadingPartyLabel = createStatCardValue("N/A");
        JPanel leadingPartyCard = createPremiumStatCard("Leading Party", leadingPartyLabel, PURPLE, "🏆");
        
        leadingCandidateLabel = createStatCardValue("N/A");
        JPanel leadingCandidateCard = createPremiumStatCard("Leading Candidate", leadingCandidateLabel, ORANGE, "👑");
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1; gbc.weighty = 1;
        panel.add(totalVotesCard, gbc);
        
        gbc.gridx = 1;
        panel.add(fptpCard, gbc);
        
        gbc.gridx = 2;
        panel.add(prCard, gbc);
        
        gbc.gridx = 3;
        panel.add(turnoutCard, gbc);
        
        gbc.gridx = 4;
        panel.add(leadingPartyCard, gbc);
        
        gbc.gridx = 5;
        panel.add(leadingCandidateCard, gbc);
        
        return panel;
    }
    
    private JLabel createStatCardValue(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 20));
        label.setForeground(TEXT_PRIMARY);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }
    
    private JPanel createPremiumStatCard(String title, JLabel valueLabel, Color color, String icon) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), 6, 6, 6);
                g2.setColor(CARD_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                
                g2.dispose();
            }
        };
        card.setLayout(new BorderLayout(5, 5));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        card.setPreferredSize(new Dimension(180, 90));
        
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        titleLabel.setForeground(TEXT_SECONDARY);
        
        topRow.add(iconLabel, BorderLayout.WEST);
        topRow.add(titleLabel, BorderLayout.EAST);
        
        card.add(topRow, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(card, BorderLayout.CENTER);
        
        return wrapper;
    }
    
    private JPanel createPremiumProgressCard(String title, Color color, String icon) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), 6, 6, 6);
                g2.setColor(CARD_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                
                g2.dispose();
            }
        };
        card.setLayout(new BorderLayout(5, 5));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        card.setPreferredSize(new Dimension(180, 90));
        
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        titleLabel.setForeground(TEXT_SECONDARY);
        
        topRow.add(iconLabel, BorderLayout.WEST);
        topRow.add(titleLabel, BorderLayout.EAST);
        
        voterTurnoutBar = new JProgressBar(0, 100);
        voterTurnoutBar.setStringPainted(true);
        voterTurnoutBar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        voterTurnoutBar.setForeground(color);
        voterTurnoutBar.setBackground(new Color(226, 232, 240));
        voterTurnoutBar.setBorderPainted(false);
        voterTurnoutBar.setPreferredSize(new Dimension(150, 20));
        
        turnoutPercentageLabel = new JLabel("0%");
        turnoutPercentageLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        turnoutPercentageLabel.setForeground(color);
        turnoutPercentageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        card.add(topRow, BorderLayout.NORTH);
        card.add(voterTurnoutBar, BorderLayout.CENTER);
        card.add(turnoutPercentageLabel, BorderLayout.SOUTH);
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(card, BorderLayout.CENTER);
        
        return wrapper;
    }
    
    private JPanel createFPTPPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel titleLabel = new JLabel("🗳️ FPTP Results (First Past The Post)");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(FPTP_COLOR);
        
        JLabel countLabel = new JLabel("Constituency-wise Results");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        countLabel.setForeground(TEXT_SECONDARY);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(countLabel, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        String[] columns = {"Rank", "Candidate", "Party", "Constituency", "Votes", "Percentage"};
        fptpTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        fptpResultTable = new JTable(fptpTableModel);
        fptpResultTable.setRowHeight(35);
        fptpResultTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        fptpResultTable.setShowGrid(true);
        fptpResultTable.setGridColor(CARD_BORDER);
        
        JTableHeader header = fptpResultTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(240, 240, 240));
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(0, 35));
        
        fptpResultTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        fptpResultTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        fptpResultTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        fptpResultTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        fptpResultTable.getColumnModel().getColumn(4).setPreferredWidth(70);
        fptpResultTable.getColumnModel().getColumn(5).setPreferredWidth(70);
        
        JScrollPane scrollPane = new JScrollPane(fptpResultTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createPRPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel titleLabel = new JLabel("📊 PR Results (Proportional Representation)");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(PR_COLOR);
        
        JLabel countLabel = new JLabel("Party-wise Results");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        countLabel.setForeground(TEXT_SECONDARY);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(countLabel, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        String[] columns = {"Rank", "Party", "Votes", "Percentage", "Est. Seats"};
        prTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        prResultTable = new JTable(prTableModel);
        prResultTable.setRowHeight(35);
        prResultTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        prResultTable.setShowGrid(true);
        prResultTable.setGridColor(CARD_BORDER);
        
        JTableHeader header = prResultTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(240, 240, 240));
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(0, 35));
        
        prResultTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        prResultTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        prResultTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        prResultTable.getColumnModel().getColumn(3).setPreferredWidth(70);
        prResultTable.getColumnModel().getColumn(4).setPreferredWidth(70);
        
        JScrollPane scrollPane = new JScrollPane(prResultTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        leftPanel.setOpaque(false);
        
        JLabel clockIcon = new JLabel("🕒");
        clockIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        
        lastUpdatedLabel = new JLabel("Last Updated: Just now");
        lastUpdatedLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lastUpdatedLabel.setForeground(TEXT_SECONDARY);
        
        leftPanel.add(clockIcon);
        leftPanel.add(lastUpdatedLabel);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);
        
        refreshBtn = createStyledButton("🔄 Refresh Now", BLUE, 130, 35);
        refreshBtn.addActionListener(e -> loadLiveData());
        
        toggleBtn = createStyledButton("⏸️ Pause", YELLOW, 100, 35);
        toggleBtn.addActionListener(e -> toggleRefresh());
        
        exportBtn = createStyledButton("📥 Export CSV", PURPLE, 120, 35);
        exportBtn.addActionListener(e -> exportResults());
        
        closeBtn = createStyledButton("✖️ Close", RED, 100, 35);
        closeBtn.addActionListener(e -> dispose());
        
        buttonPanel.add(refreshBtn);
        buttonPanel.add(toggleBtn);
        buttonPanel.add(exportBtn);
        buttonPanel.add(closeBtn);
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(buttonPanel, BorderLayout.EAST);
        
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
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(width, height));
        
        return button;
    }
    
    private void toggleRefresh() {
        if (isRefreshing) {
            stopLiveRefresh();
            toggleBtn.setText("▶️ Resume");
            toggleBtn.setBackground(GREEN);
            statusIcon.setForeground(RED);
        } else {
            startLiveRefresh();
            toggleBtn.setText("⏸️ Pause");
            toggleBtn.setBackground(YELLOW);
            statusIcon.setForeground(GREEN);
        }
    }
    
    private void startLiveRefresh() {
        isRefreshing = true;
        refreshTimer = new Timer();
        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> loadLiveData());
            }
        }, 0, 5000);
    }
    
    private void stopLiveRefresh() {
        isRefreshing = false;
        if (refreshTimer != null) {
            refreshTimer.cancel();
            refreshTimer = null;
        }
    }
    
private void loadLiveData() {
    try {
        LiveVoteData data = voteController.getLiveVoteData();
        
        // Update stats with formatted numbers
        totalVotesLabel.setText(formatNumber(data.getTotalVotes()));
        fptpVotesLabel.setText(formatNumber(data.getFptpVotes()));
        prVotesLabel.setText(formatNumber(data.getPrVotes()));
        
        // Update turnout
        int turnoutPercent = data.getTurnoutPercentage();
        voterTurnoutBar.setValue(turnoutPercent);
        turnoutPercentageLabel.setText(turnoutPercent + "%");
        
        // Update leading indicators
        if (data.getLeadingParty() != null && !data.getLeadingParty().equals("No votes yet") && !data.getLeadingParty().equals("No data")) {
            leadingPartyLabel.setText(data.getLeadingParty());
        } else {
            leadingPartyLabel.setText("No votes yet");
        }
        
        if (data.getLeadingCandidate() != null && !data.getLeadingCandidate().equals("No votes yet") && !data.getLeadingCandidate().equals("No data")) {
            leadingCandidateLabel.setText(data.getLeadingCandidate());
        } else {
            leadingCandidateLabel.setText("No votes yet");
        }
        
        // Update FPTP results
        updateFPTPTable(data.getFptpResults());
        
        // Update PR results
        updatePRTable(data.getPrResults());
        
        // Update timestamp
        lastUpdatedLabel.setText("Last Updated: " + 
            new SimpleDateFormat("hh:mm:ss a").format(new Date()));
        
        // Update status icon based on data presence
        if (data.getTotalVotes() > 0) {
            // Green - Data available
            statusIcon.setForeground(GREEN);
            statusIcon.setToolTipText("Live - " + data.getTotalVotes() + " votes counted");
        } else if (data.getTotalVotes() == 0 && data.getFptpResults().isEmpty() && data.getPrResults().isEmpty()) {
            // Yellow - No data but tables exist
            statusIcon.setForeground(YELLOW);
            statusIcon.setToolTipText("Waiting for votes - No votes cast yet");
        } else {
            // Red - Error state
            statusIcon.setForeground(RED);
            statusIcon.setToolTipText("Error loading data");
        }
        
    } catch (Exception e) {
        e.printStackTrace();
        lastUpdatedLabel.setText("Last Updated: Error loading data");
        statusIcon.setForeground(RED);
        statusIcon.setToolTipText("Error: " + e.getMessage());
    }
}
    
    private String formatNumber(int number) {
        return String.format("%,d", number);
    }
    
    private void updateFPTPTable(List<Object[]> results) {
        fptpTableModel.setRowCount(0);
        if (results != null && !results.isEmpty()) {
            int rank = 1;
            for (Object[] row : results) {
                fptpTableModel.addRow(new Object[]{
                    rank++,
                    row[0], // candidate name
                    row[1], // party
                    row[2], // constituency
                    row[3], // votes
                    row[4]  // percentage
                });
            }
        } else {
            // Show empty state
            fptpTableModel.addRow(new Object[]{1, "No data", "No data", "No data", 0, 0});
        }
    }
    
    private void updatePRTable(List<Object[]> results) {
        prTableModel.setRowCount(0);
        if (results != null && !results.isEmpty()) {
            int rank = 1;
            for (Object[] row : results) {
                prTableModel.addRow(new Object[]{
                    rank++,
                    row[0], // party name
                    row[1], // votes
                    row[2], // percentage
                    row[3]  // seats
                });
            }
        } else {
            // Show empty state
            prTableModel.addRow(new Object[]{1, "No data", 0, 0, 0});
        }
    }
    
    private void exportResults() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Election Results");
        fileChooser.setSelectedFile(new File("Election_Results_" + 
            new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".csv"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println("FPTP RESULTS (First Past The Post)");
                writer.println("Rank,Candidate,Party,Constituency,Votes,Percentage");
                
                for (int i = 0; i < fptpTableModel.getRowCount(); i++) {
                    writer.println(
                        fptpTableModel.getValueAt(i, 0) + "," +
                        fptpTableModel.getValueAt(i, 1) + "," +
                        fptpTableModel.getValueAt(i, 2) + "," +
                        fptpTableModel.getValueAt(i, 3) + "," +
                        fptpTableModel.getValueAt(i, 4) + "," +
                        fptpTableModel.getValueAt(i, 5)
                    );
                }
                
                writer.println("\n\nPR RESULTS (Proportional Representation)");
                writer.println("Rank,Party,Votes,Percentage,Est. Seats");
                
                for (int i = 0; i < prTableModel.getRowCount(); i++) {
                    writer.println(
                        prTableModel.getValueAt(i, 0) + "," +
                        prTableModel.getValueAt(i, 1) + "," +
                        prTableModel.getValueAt(i, 2) + "," +
                        prTableModel.getValueAt(i, 3) + "," +
                        prTableModel.getValueAt(i, 4)
                    );
                }
                
                JOptionPane.showMessageDialog(this,
                    "✅ Results exported successfully to:\n" + file.getAbsolutePath(),
                    "Export Complete",
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "❌ Error exporting results: " + e.getMessage(),
                    "Export Failed",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}