package votingsystempro.views.voter;

import votingsystempro.controllers.NewsController;
import votingsystempro.models.News;
import votingsystempro.models.NewsComment;
import votingsystempro.models.Voter;
import votingsystempro.utils.ImageUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.util.List;

/**
 * ULTRA PREMIUM News Viewer - Windows 11 Inspired Design
 * Fixed: Text overflow, stats icons, and layout issues
 */
public class NewsViewerFrame extends JFrame {
    private NewsController newsController;
    private Voter currentVoter;
    
    // UI Components
    private JPanel newsListPanel;
    private JPanel newsDetailPanel;
    private JScrollPane newsListScroll;
    private JScrollPane newsDetailScroll;
    private JScrollPane commentsScroll;
    private JTextArea commentArea;
    private JButton likeBtn, dislikeBtn, commentBtn, backToListBtn, refreshBtn;
    private JLabel likeCountLabel, dislikeCountLabel, commentCountLabel;
    private JLabel titleLabel, authorLabel, dateLabel, imageLabel, summaryLabel;
    private JPanel statsPanel;
    
    private News currentNews;
    private List<News> newsList;
    private String userReaction;
    
    // Flags to prevent double-clicking
    private boolean isProcessingLike = false;
    private boolean isProcessingDislike = false;
    
    // ============== WINDOWS 11 PREMIUM COLOR SCHEME ==============
    private final Color SIDEBAR_BG = new Color(32, 33, 36);        // #202124 - Dark gray
    private final Color SIDEBAR_HOVER = new Color(48, 49, 52);     // #303134
    private final Color CONTENT_BG = new Color(248, 249, 250);     // #F8F9FA - Light background
    private final Color CARD_BG = new Color(255, 255, 255);        // Pure white
    private final Color CARD_BORDER = new Color(233, 236, 239);    // #E9ECEF
    private final Color TEXT_PRIMARY = new Color(33, 37, 41);      // #212529
    private final Color TEXT_SECONDARY = new Color(108, 117, 125); // #6C757D
    private final Color TEXT_MUTED = new Color(173, 181, 189);     // #ADB5BD
    
    // Windows 11 Accent Colors
    private final Color ACCENT_BLUE = new Color(0, 120, 212);      // Windows 11 blue
    private final Color ACCENT_GREEN = new Color(16, 124, 16);     // Forest green
    private final Color ACCENT_RED = new Color(196, 43, 28);       // Proper red
    
    // Fixed sizes
    private final int LIKE_BTN_WIDTH = 140;
    private final int LIKE_BTN_HEIGHT = 45;
    private final int CONTENT_WIDTH = 800; // Fixed width to match image
    
    // Windows 11 spacing system
    private final int SPACING_XS = 4;
    private final int SPACING_SM = 8;
    private final int SPACING_MD = 16;
    private final int SPACING_LG = 24;
    private final int SPACING_XL = 32;
    
    // Icons
    private ImageIcon likeIcon, likedIcon, dislikeIcon, dislikedIcon;
    
    public NewsViewerFrame(Voter voter) {
        this.newsController = new NewsController();
        this.currentVoter = voter;
        
        loadIcons();
        initComponents();
        loadNewsList();
        applyModernEffects();
    }
    
    private void loadIcons() {
        String basePath = "C:\\Users\\KKKKK\\Documents\\NetBeansProjects\\VotingSystemPro\\resources\\icons\\";
        
        likeIcon = loadIcon(basePath + "like.png", 18, 18);
        likedIcon = loadIcon(basePath + "liked.png", 18, 18);
        dislikeIcon = loadIcon(basePath + "dislike.png", 18, 18);
        dislikedIcon = loadIcon(basePath + "disliked.png", 18, 18);
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
    
    private void applyModernEffects() {
        try {
            setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
        } catch (Exception e) {
            // Ignore
        }
        
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                try {
                    setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
                } catch (Exception ex) {
                    // Ignore
                }
            }
        });
    }
    
    private void initComponents() {
        setTitle("News & Updates - Election Commission of Nepal");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setUndecorated(true);
        
        // Premium custom title bar
        JPanel titleBar = createPremiumTitleBar();
        add(titleBar, BorderLayout.NORTH);
        
        // Main Content - Split Pane with Windows 11 styling
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setDividerSize(2);
        splitPane.setBorder(null);
        splitPane.setBackground(CONTENT_BG);
        
        // Left Panel - Premium News List
        JPanel leftPanel = createPremiumLeftPanel();
        splitPane.setLeftComponent(leftPanel);
        
        // Right Panel - Premium News Detail
        JPanel rightPanel = createPremiumRightPanel();
        splitPane.setRightComponent(rightPanel);
        
        add(splitPane, BorderLayout.CENTER);
        
        // Premium Status Bar
        JPanel statusBar = createPremiumStatusBar();
        add(statusBar, BorderLayout.SOUTH);
        
        setSize(1400, 850);
        setLocationRelativeTo(null);
    }
    
    private JPanel createPremiumTitleBar() {
        JPanel titleBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gp = new GradientPaint(0, 0, SIDEBAR_BG, getWidth(), 0, SIDEBAR_HOVER);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                g2.dispose();
            }
        };
        titleBar.setPreferredSize(new Dimension(0, 55));
        titleBar.setLayout(new BorderLayout());
        titleBar.setBorder(BorderFactory.createEmptyBorder(0, SPACING_LG, 0, SPACING_MD));
        
        // Left side - Logo and title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, SPACING_MD, 12));
        leftPanel.setOpaque(false);
        
        JLabel logoLabel = new JLabel("📰");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
        logoLabel.setForeground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("News & Updates");
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        
        leftPanel.add(logoLabel);
        leftPanel.add(titleLabel);
        
        // Right side - Window controls
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 10));
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
        closeBtn.addActionListener(e -> dispose());
        
        rightPanel.add(minimizeBtn);
        rightPanel.add(maximizeBtn);
        rightPanel.add(closeBtn);
        
        titleBar.add(leftPanel, BorderLayout.WEST);
        titleBar.add(rightPanel, BorderLayout.EAST);
        
        return titleBar;
    }
    
    private JButton createWindowButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI Variable", Font.PLAIN, 18));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 0, 0, 0));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(40, 35));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(255, 255, 255, 30));
                button.setOpaque(true);
                button.repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(0, 0, 0, 0));
                button.setOpaque(false);
                button.repaint();
            }
        });
        
        return button;
    }
    
    private JPanel createPremiumStatusBar() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(SIDEBAR_BG);
        bottomPanel.setPreferredSize(new Dimension(1400, 40));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, SPACING_LG, 0, SPACING_LG));
        
        JPanel leftStatus = new JPanel(new FlowLayout(FlowLayout.LEFT, SPACING_MD, 8));
        leftStatus.setOpaque(false);
        
        // Windows 11 status indicator
        JPanel statusDot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(new Color(16, 124, 16));
                g2.fillOval(0, 0, 10, 10);
                
                g2.setColor(new Color(16, 124, 16, 50));
                g2.fillOval(-2, -2, 14, 14);
                
                g2.dispose();
            }
        };
        statusDot.setPreferredSize(new Dimension(10, 10));
        statusDot.setOpaque(false);
        
        JLabel versionLabel = new JLabel("Live Updates");
        versionLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 11));
        versionLabel.setForeground(new Color(148, 163, 184));
        
        leftStatus.add(statusDot);
        leftStatus.add(versionLabel);
        
        bottomPanel.add(leftStatus, BorderLayout.WEST);
        
        return bottomPanel;
    }
    
    private JPanel createPremiumLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout(SPACING_SM, SPACING_SM));
        panel.setBackground(CONTENT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(SPACING_LG, SPACING_LG, SPACING_LG, SPACING_MD));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CONTENT_BG);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, SPACING_MD, 0));
        
        JLabel newsListLabel = new JLabel("Latest News");
        newsListLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 22));
        newsListLabel.setForeground(TEXT_PRIMARY);
        headerPanel.add(newsListLabel, BorderLayout.WEST);
        
        refreshBtn = createPremiumButton("🔄", ACCENT_BLUE, 45, 35);
        refreshBtn.addActionListener(e -> loadNewsList());
        headerPanel.add(refreshBtn, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // News List Panel
        newsListPanel = new JPanel();
        newsListPanel.setLayout(new BoxLayout(newsListPanel, BoxLayout.Y_AXIS));
        newsListPanel.setBackground(CONTENT_BG);
        
        newsListScroll = new JScrollPane(newsListPanel);
        newsListScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        newsListScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        newsListScroll.getVerticalScrollBar().setUnitIncrement(16);
        newsListScroll.setBorder(null);
        newsListScroll.getViewport().setBackground(CONTENT_BG);
        
        // Custom scrollbar
        newsListScroll.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        newsListScroll.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(180, 180, 180);
                this.trackColor = CONTENT_BG;
            }
            
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }
        });
        
        panel.add(newsListScroll, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JButton createPremiumButton(String text, Color bgColor, int width, int height) {
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
                
                g2.setColor(new Color(0, 0, 0, 20));
                g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 8, 8);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI Variable", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(width, height));
        
        return button;
    }
    
    private JPanel createPremiumRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(SPACING_SM, SPACING_SM));
        panel.setBackground(CONTENT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(SPACING_LG, SPACING_MD, SPACING_LG, SPACING_LG));
        
        // Back Button Panel
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        backPanel.setBackground(CONTENT_BG);
        
        backToListBtn = createPremiumOutlineButton("← Back to News", SIDEBAR_BG, 150, 38);
        backToListBtn.addActionListener(e -> showNewsList());
        backToListBtn.setVisible(false);
        backPanel.add(backToListBtn);
        
        panel.add(backPanel, BorderLayout.NORTH);
        
        // News Detail Panel
        newsDetailPanel = createPremiumNewsDetailPanel();
        
        newsDetailScroll = new JScrollPane(newsDetailPanel);
        newsDetailScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        newsDetailScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        newsDetailScroll.getVerticalScrollBar().setUnitIncrement(16);
        newsDetailScroll.setBorder(null);
        newsDetailScroll.getViewport().setBackground(CONTENT_BG);
        
        // Custom scrollbar
        newsDetailScroll.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        newsDetailScroll.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(180, 180, 180);
                this.trackColor = CONTENT_BG;
            }
            
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }
        });
        
        panel.add(newsDetailScroll, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JButton createPremiumOutlineButton(String text, Color color, int width, int height) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(color.darker());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.setColor(color);
                    g2.setStroke(new BasicStroke(2));
                    g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 8, 8);
                } else {
                    g2.setColor(color);
                    g2.setStroke(new BasicStroke(2));
                    g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 8, 8);
                }
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI Variable", Font.BOLD, 12));
        button.setForeground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(width, height));
        
        return button;
    }
    
    private JPanel createPremiumNewsDetailPanel() {
        // Main panel with Windows 11 card styling
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                for (int i = 1; i <= 4; i++) {
                    g2.setColor(new Color(0, 0, 0, 3));
                    g2.drawRoundRect(i, i, getWidth() - i * 2 - 1, getHeight() - i * 2 - 1, 20, 20);
                }
                
                g2.dispose();
            }
        };
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(SPACING_XL, SPACING_XL, SPACING_XL, SPACING_XL));
        
        // ===== TITLE SECTION - Fixed width constraint =====
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setMaximumSize(new Dimension(CONTENT_WIDTH, 100));
        titlePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        titleLabel = new JLabel("Select a news article");
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_PRIMARY);
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        mainPanel.add(titlePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, SPACING_MD)));
        
        // ===== AUTHOR & DATE SECTION =====
        JPanel metaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, SPACING_LG, 0));
        metaPanel.setOpaque(false);
        metaPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        metaPanel.setMaximumSize(new Dimension(CONTENT_WIDTH, 30));
        
        authorLabel = new JLabel("By: ");
        authorLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 13));
        authorLabel.setForeground(ACCENT_BLUE);
        
        dateLabel = new JLabel("Published: ");
        dateLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 13));
        dateLabel.setForeground(TEXT_SECONDARY);
        
        metaPanel.add(authorLabel);
        metaPanel.add(dateLabel);
        
        mainPanel.add(metaPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, SPACING_LG)));
        
        // ===== IMAGE SECTION - Exact 800x300 =====
        JPanel imageContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Shadow
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fillRoundRect(5, 5, getWidth() - 5, getHeight() - 5, 15, 15);
                
                // White background
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 15, 15);
                
                g2.dispose();
            }
        };
        imageContainer.setOpaque(false);
        imageContainer.setPreferredSize(new Dimension(CONTENT_WIDTH, 300));
        imageContainer.setMaximumSize(new Dimension(CONTENT_WIDTH, 300));
        imageContainer.setMinimumSize(new Dimension(CONTENT_WIDTH, 300));
        imageContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        imageContainer.setLayout(new BorderLayout());
        
        imageLabel = new JLabel("📰", SwingConstants.CENTER);
        imageLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 72));
        imageLabel.setForeground(TEXT_MUTED);
        imageContainer.add(imageLabel, BorderLayout.CENTER);
        
        mainPanel.add(imageContainer);
        mainPanel.add(Box.createRigidArea(new Dimension(0, SPACING_LG)));
        
        // ===== SUMMARY SECTION - Constrained width =====
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setOpaque(false);
        summaryPanel.setMaximumSize(new Dimension(CONTENT_WIDTH, 80));
        summaryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        summaryLabel = new JLabel(" ");
        summaryLabel.setFont(new Font("Segoe UI Variable", Font.ITALIC, 15));
        summaryLabel.setForeground(TEXT_SECONDARY);
        summaryPanel.add(summaryLabel, BorderLayout.WEST);
        
        mainPanel.add(summaryPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, SPACING_SM)));
        
        // ===== CONTENT SECTION - Using JTextPane for HTML content =====
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setMaximumSize(new Dimension(CONTENT_WIDTH, 400));
        contentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, SPACING_LG, 0));
        
        // Use JTextPane with HTML for better text wrapping
        JTextPane contentTextPane = new JTextPane();
        contentTextPane.setContentType("text/html");
        contentTextPane.setEditable(false);
        contentTextPane.setBackground(CARD_BG);
        
        // Set initial HTML content with proper width constraint
        contentTextPane.setText("<html><body style='font-family: Segoe UI Variable; font-size: 14pt; color: #212529; margin: 0; padding: 0; width: 750px;'>Select a news article to read full content</body></html>");
        
        JScrollPane contentScroll = new JScrollPane(contentTextPane);
        contentScroll.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 1));
        contentScroll.setPreferredSize(new Dimension(CONTENT_WIDTH, 250));
        contentScroll.setMaximumSize(new Dimension(CONTENT_WIDTH, 400));
        contentScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        contentScroll.getVerticalScrollBar().setUnitIncrement(16);
        
        contentPanel.add(contentScroll, BorderLayout.CENTER);
        
        // Store reference to contentTextPane for later updates
        mainPanel.putClientProperty("contentTextPane", contentTextPane);
        mainPanel.add(contentPanel);
        
        // ===== STATS PANEL - Fixed alignment and emoji rendering =====
        statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, SPACING_LG, 0));
        statsPanel.setOpaque(false);
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsPanel.setMaximumSize(new Dimension(CONTENT_WIDTH, 50));
        
        likeCountLabel = createStatsLabel("👍", "0", ACCENT_GREEN);
        dislikeCountLabel = createStatsLabel("👎", "0", ACCENT_RED);
        commentCountLabel = createStatsLabel("💬", "0 comments", ACCENT_BLUE);
        
        statsPanel.add(likeCountLabel);
        statsPanel.add(dislikeCountLabel);
        statsPanel.add(commentCountLabel);
        
        mainPanel.add(statsPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, SPACING_LG)));
        
        // ===== ACTION BUTTONS PANEL =====
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, SPACING_LG, 0));
        actionPanel.setOpaque(false);
        actionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        actionPanel.setMaximumSize(new Dimension(CONTENT_WIDTH, LIKE_BTN_HEIGHT + SPACING_SM));
        
        likeBtn = createReactionButton("Like", likeIcon, ACCENT_GREEN, LIKE_BTN_WIDTH, LIKE_BTN_HEIGHT);
        likeBtn.setEnabled(false);
        likeBtn.addActionListener(e -> handleLike());
        
        dislikeBtn = createReactionButton("Dislike", dislikeIcon, ACCENT_RED, LIKE_BTN_WIDTH, LIKE_BTN_HEIGHT);
        dislikeBtn.setEnabled(false);
        dislikeBtn.addActionListener(e -> handleDislike());
        
        actionPanel.add(likeBtn);
        actionPanel.add(dislikeBtn);
        
        mainPanel.add(actionPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, SPACING_XL)));
        
        // ===== COMMENTS SECTION =====
        JPanel commentSection = createPremiumCommentsSection();
        mainPanel.add(commentSection);
        mainPanel.add(Box.createRigidArea(new Dimension(0, SPACING_LG)));
        
        return mainPanel;
    }
    
    private JLabel createStatsLabel(String icon, String text, Color color) {
        JPanel badge = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));
        badge.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
        badge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(color.getRed(), color.getGreen(), color.getBlue(), 80), 1),
            BorderFactory.createEmptyBorder(5, SPACING_MD, 5, SPACING_MD)
        ));
        
        // Use Segoe UI Emoji font for icons
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 14));
        textLabel.setForeground(color);
        
        badge.add(iconLabel);
        badge.add(textLabel);
        
        JLabel wrapper = new JLabel();
        wrapper.setLayout(new BorderLayout());
        wrapper.add(badge, BorderLayout.CENTER);
        wrapper.putClientProperty("textLabel", textLabel);
        wrapper.putClientProperty("iconLabel", iconLabel);
        
        return wrapper;
    }
    
    private void updateStatLabel(JLabel wrapper, String newText) {
        if (wrapper != null) {
            JLabel textLabel = (JLabel) wrapper.getClientProperty("textLabel");
            if (textLabel != null) {
                textLabel.setText(newText);
            }
        }
    }
    
    private JButton createReactionButton(String text, ImageIcon icon, Color bgColor, int width, int height) {
        JButton button = new JButton(text, icon != null ? icon : new ImageIcon()) {
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
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                g2.setColor(new Color(0, 0, 0, 30));
                g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 12, 12);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI Variable", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalTextPosition(SwingConstants.RIGHT);
        button.setIconTextGap(10);
        button.setPreferredSize(new Dimension(width, height));
        
        return button;
    }
    
    private JPanel createPremiumCommentsSection() {
        JPanel section = new JPanel(new BorderLayout(0, SPACING_MD));
        section.setBackground(CARD_BG);
        section.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(SPACING_LG, SPACING_LG, SPACING_LG, SPACING_LG)
        ));
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.setMaximumSize(new Dimension(CONTENT_WIDTH, 350));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_BG);
        
        JLabel headerLabel = new JLabel("💬 Comments");
        headerLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 18));
        headerLabel.setForeground(TEXT_PRIMARY);
        headerPanel.add(headerLabel, BorderLayout.WEST);
        
        section.add(headerPanel, BorderLayout.NORTH);
        
        // Comments List
        JPanel commentsPanel = new JPanel();
        commentsPanel.setLayout(new BoxLayout(commentsPanel, BoxLayout.Y_AXIS));
        commentsPanel.setBackground(CARD_BG);
        
        commentsScroll = new JScrollPane(commentsPanel);
        commentsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        commentsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        commentsScroll.getVerticalScrollBar().setUnitIncrement(16);
        commentsScroll.setPreferredSize(new Dimension(CONTENT_WIDTH - 50, 150));
        commentsScroll.setBorder(null);
        commentsScroll.getViewport().setBackground(CARD_BG);
        
        // Custom scrollbar
        commentsScroll.getVerticalScrollBar().setPreferredSize(new Dimension(6, 0));
        commentsScroll.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(200, 200, 200);
            }
            
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }
        });
        
        section.add(commentsScroll, BorderLayout.CENTER);
        
        // Add Comment Panel
        JPanel addCommentPanel = new JPanel(new BorderLayout(SPACING_MD, 0));
        addCommentPanel.setBackground(CARD_BG);
        addCommentPanel.setBorder(BorderFactory.createEmptyBorder(SPACING_MD, 0, 0, 0));
        
        commentArea = new JTextArea(2, 40);
        commentArea.setFont(new Font("Segoe UI Variable", Font.PLAIN, 13));
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        commentArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(SPACING_MD, SPACING_MD, SPACING_MD, SPACING_MD)
        ));
        commentArea.setEnabled(false);
        
        String placeholderText = "Write your comment here...";
        commentArea.setText(placeholderText);
        commentArea.setForeground(TEXT_MUTED);
        
        commentArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (commentArea.getText().equals(placeholderText)) {
                    commentArea.setText("");
                    commentArea.setForeground(TEXT_PRIMARY);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (commentArea.getText().isEmpty()) {
                    commentArea.setText(placeholderText);
                    commentArea.setForeground(TEXT_MUTED);
                }
            }
        });
        
        commentBtn = createPremiumButton("Post", ACCENT_BLUE, 100, 40);
        commentBtn.setEnabled(false);
        commentBtn.addActionListener(e -> postComment());
        
        addCommentPanel.add(new JScrollPane(commentArea), BorderLayout.CENTER);
        addCommentPanel.add(commentBtn, BorderLayout.EAST);
        
        section.add(addCommentPanel, BorderLayout.SOUTH);
        
        return section;
    }
    
    private void updateReactionButtons() {
        SwingUtilities.invokeLater(() -> {
            if ("like".equals(userReaction)) {
                likeBtn.setText("Liked");
                likeBtn.setIcon(likedIcon != null ? likedIcon : likeIcon);
                likeBtn.setBackground(ACCENT_GREEN.darker());
                
                dislikeBtn.setText("Dislike");
                dislikeBtn.setIcon(dislikeIcon);
                dislikeBtn.setBackground(ACCENT_RED);
            } else if ("dislike".equals(userReaction)) {
                likeBtn.setText("Like");
                likeBtn.setIcon(likeIcon);
                likeBtn.setBackground(ACCENT_GREEN);
                
                dislikeBtn.setText("Disliked");
                dislikeBtn.setIcon(dislikedIcon != null ? dislikedIcon : dislikeIcon);
                dislikeBtn.setBackground(ACCENT_RED.darker());
            } else {
                likeBtn.setText("Like");
                likeBtn.setIcon(likeIcon);
                likeBtn.setBackground(ACCENT_GREEN);
                
                dislikeBtn.setText("Dislike");
                dislikeBtn.setIcon(dislikeIcon);
                dislikeBtn.setBackground(ACCENT_RED);
            }
            
            likeBtn.repaint();
            dislikeBtn.repaint();
        });
    }
    
    private void loadNewsList() {
        newsListPanel.removeAll();
        JLabel loadingLabel = new JLabel("⏳ Loading news...", SwingConstants.CENTER);
        loadingLabel.setFont(new Font("Segoe UI Variable", Font.ITALIC, 14));
        loadingLabel.setForeground(TEXT_SECONDARY);
        loadingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        newsListPanel.add(loadingLabel);
        newsListPanel.revalidate();
        newsListPanel.repaint();
        
        SwingWorker<List<News>, Void> worker = new SwingWorker<List<News>, Void>() {
            @Override
            protected List<News> doInBackground() throws Exception {
                return newsController.getAllNews();
            }
            
            @Override
            protected void done() {
                try {
                    newsList = get();
                    newsListPanel.removeAll();
                    
                    if (newsList.isEmpty()) {
                        JPanel emptyPanel = createEmptyStatePanel("📭 No News Available", 
                            "There are no news articles at the moment.", TEXT_MUTED);
                        newsListPanel.add(emptyPanel);
                    } else {
                        for (News news : newsList) {
                            JPanel newsItem = createNewsItem(news);
                            newsListPanel.add(newsItem);
                            newsListPanel.add(Box.createRigidArea(new Dimension(0, SPACING_SM)));
                        }
                    }
                    
                    newsListPanel.revalidate();
                    newsListPanel.repaint();
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private JPanel createEmptyStatePanel(String title, String message, Color color) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 1));
        panel.setPreferredSize(new Dimension(350, 200));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(SPACING_SM, SPACING_SM, SPACING_SM, SPACING_SM);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        
        JLabel iconLabel = new JLabel(title.substring(0, 2));
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        gbc.gridy = 0;
        panel.add(iconLabel, gbc);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
        titleLabel.setForeground(color);
        gbc.gridy = 1;
        panel.add(titleLabel, gbc);
        
        JLabel msgLabel = new JLabel(message);
        msgLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 12));
        msgLabel.setForeground(TEXT_SECONDARY);
        gbc.gridy = 2;
        panel.add(msgLabel, gbc);
        
        return panel;
    }
    
    private JPanel createNewsItem(News news) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                g2.setColor(new Color(0, 0, 0, 5));
                g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 15, 15);
                
                g2.setColor(CARD_BORDER);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                
                g2.dispose();
            }
        };
        panel.setLayout(new BorderLayout(SPACING_SM, SPACING_SM));
        panel.setOpaque(false);
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.setMaximumSize(new Dimension(350, 140));
        panel.setPreferredSize(new Dimension(350, 140));
        
        // Top Row - Title
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.setBorder(BorderFactory.createEmptyBorder(SPACING_MD, SPACING_MD, SPACING_SM, SPACING_MD));
        
        JLabel titleLabel = new JLabel(truncateText(news.getTitle(), 40));
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_PRIMARY);
        topRow.add(titleLabel, BorderLayout.WEST);
        
        if (news.isFeatured()) {
            JLabel featuredLabel = new JLabel("⭐");
            featuredLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            featuredLabel.setForeground(new Color(255, 193, 7));
            topRow.add(featuredLabel, BorderLayout.EAST);
        }
        
        panel.add(topRow, BorderLayout.NORTH);
        
        // Summary
        String summary = news.getSummary() != null ? news.getSummary() : "";
        if (summary.length() > 60) summary = summary.substring(0, 60) + "...";
        
        JLabel summaryLabel = new JLabel("<html><body style='width:280px; padding:0 " + SPACING_MD + "px'>" + summary + "</body></html>");
        summaryLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 11));
        summaryLabel.setForeground(TEXT_SECONDARY);
        panel.add(summaryLabel, BorderLayout.CENTER);
        
        // Bottom - Date and Stats
        JPanel bottomRow = new JPanel(new BorderLayout());
        bottomRow.setOpaque(false);
        bottomRow.setBorder(BorderFactory.createEmptyBorder(SPACING_SM, SPACING_MD, SPACING_MD, SPACING_MD));
        
        JLabel dateLabel = new JLabel("📅 " + news.getFormattedDate());
        dateLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 9));
        dateLabel.setForeground(TEXT_MUTED);
        bottomRow.add(dateLabel, BorderLayout.WEST);
        
        JPanel statsRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, SPACING_SM, 0));
        statsRow.setOpaque(false);
        
        JLabel likeIcon = new JLabel("👍 " + news.getLikesCount());
        likeIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 10));
        likeIcon.setForeground(ACCENT_GREEN);
        
        JLabel commentIcon = new JLabel("💬 " + news.getCommentsCount());
        commentIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 10));
        commentIcon.setForeground(ACCENT_BLUE);
        
        statsRow.add(likeIcon);
        statsRow.add(commentIcon);
        bottomRow.add(statsRow, BorderLayout.EAST);
        
        panel.add(bottomRow, BorderLayout.SOUTH);
        
        // Hover effect
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                panel.repaint();
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                showNewsDetail(news);
            }
        });
        
        return panel;
    }
    
    private String truncateText(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) return text;
        return text.substring(0, maxLength) + "...";
    }
    
    private void showNewsDetail(News news) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                currentNews = newsController.getNewsById(news.getNewsId());
                return null;
            }
            
            @Override
            protected void done() {
                if (currentNews != null) {
                    // Set title
                    titleLabel.setText(currentNews.getTitle());
                    
                    // Set summary
                    summaryLabel.setText(currentNews.getSummary() != null ? currentNews.getSummary() : "");
                    
                    // Set content using HTML for better wrapping
                    JTextPane contentTextPane = (JTextPane) newsDetailPanel.getClientProperty("contentTextPane");
                    if (contentTextPane != null) {
                        String htmlContent = String.format(
                            "<html><body style='font-family: Segoe UI Variable; font-size: 14pt; color: #212529; margin: 0; padding: 0; width: 750px;'>%s</body></html>",
                            currentNews.getContent().replace("\n", "<br>")
                        );
                        contentTextPane.setText(htmlContent);
                        contentTextPane.setCaretPosition(0);
                    }
                    
                    // Set author and date
                    authorLabel.setText("By: " + currentNews.getAuthorName());
                    dateLabel.setText("Published: " + currentNews.getFormattedDate());
                    
                    // Update stats with proper numbers
                    updateStatLabel(likeCountLabel, String.valueOf(currentNews.getLikesCount()));
                    updateStatLabel(dislikeCountLabel, String.valueOf(currentNews.getDislikesCount()));
                    updateStatLabel(commentCountLabel, currentNews.getCommentsCount() + " comments");
                    
                    // Load and display image with proper scaling - Fixed: Exact 800x300
                    if (currentNews.getImagePath() != null && !currentNews.getImagePath().isEmpty()) {
                        ImageIcon icon = ImageUtil.createImageIcon(currentNews.getImagePath(), CONTENT_WIDTH, 300);
                        if (icon != null) {
                            imageLabel.setIcon(icon);
                            imageLabel.setText("");
                        } else {
                            imageLabel.setIcon(null);
                            imageLabel.setText("📰");
                        }
                    } else {
                        imageLabel.setIcon(null);
                        imageLabel.setText("📰");
                    }
                    
                    // Get user reaction and update buttons
                    userReaction = newsController.getUserReaction(currentNews.getNewsId(), currentVoter.getUserId());
                    updateReactionButtons();
                    
                    // Load comments
                    loadComments();
                    
                    // Enable buttons
                    likeBtn.setEnabled(true);
                    dislikeBtn.setEnabled(true);
                    commentArea.setEnabled(true);
                    commentBtn.setEnabled(true);
                    
                    // Clear placeholder when enabled
                    String placeholderText = "Write your comment here...";
                    if (commentArea.getText().equals(placeholderText)) {
                        commentArea.setText(placeholderText);
                        commentArea.setForeground(TEXT_MUTED);
                    }
                    
                    backToListBtn.setVisible(true);
                }
            }
        };
        worker.execute();
    }
    
    private void handleLike() {
        if (currentNews == null || isProcessingLike) return;
        
        isProcessingLike = true;
        likeBtn.setEnabled(false);
        dislikeBtn.setEnabled(false);
        
        ActionListener[] likeListeners = likeBtn.getActionListeners();
        ActionListener[] dislikeListeners = dislikeBtn.getActionListeners();
        
        for (ActionListener al : likeListeners) likeBtn.removeActionListener(al);
        for (ActionListener al : dislikeListeners) dislikeBtn.removeActionListener(al);
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return newsController.addLike(currentNews.getNewsId(), currentVoter.getUserId(), 
                                               currentVoter.getFullName(), "like");
            }
            
            @Override
            protected void done() {
                try {
                    if (get()) {
                        currentNews = newsController.getNewsById(currentNews.getNewsId());
                        // Update stats using the helper method
                        updateStatLabel(likeCountLabel, String.valueOf(currentNews.getLikesCount()));
                        updateStatLabel(dislikeCountLabel, String.valueOf(currentNews.getDislikesCount()));
                        userReaction = newsController.getUserReaction(currentNews.getNewsId(), currentVoter.getUserId());
                        updateReactionButtons();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    for (ActionListener al : likeListeners) likeBtn.addActionListener(al);
                    for (ActionListener al : dislikeListeners) dislikeBtn.addActionListener(al);
                    
                    likeBtn.setEnabled(true);
                    dislikeBtn.setEnabled(true);
                    isProcessingLike = false;
                }
            }
        };
        worker.execute();
    }
    
    private void handleDislike() {
        if (currentNews == null || isProcessingDislike) return;
        
        isProcessingDislike = true;
        likeBtn.setEnabled(false);
        dislikeBtn.setEnabled(false);
        
        ActionListener[] likeListeners = likeBtn.getActionListeners();
        ActionListener[] dislikeListeners = dislikeBtn.getActionListeners();
        
        for (ActionListener al : likeListeners) likeBtn.removeActionListener(al);
        for (ActionListener al : dislikeListeners) dislikeBtn.removeActionListener(al);
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return newsController.addLike(currentNews.getNewsId(), currentVoter.getUserId(), 
                                               currentVoter.getFullName(), "dislike");
            }
            
            @Override
            protected void done() {
                try {
                    if (get()) {
                        currentNews = newsController.getNewsById(currentNews.getNewsId());
                        // Update stats using the helper method
                        updateStatLabel(likeCountLabel, String.valueOf(currentNews.getLikesCount()));
                        updateStatLabel(dislikeCountLabel, String.valueOf(currentNews.getDislikesCount()));
                        userReaction = newsController.getUserReaction(currentNews.getNewsId(), currentVoter.getUserId());
                        updateReactionButtons();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    for (ActionListener al : likeListeners) likeBtn.addActionListener(al);
                    for (ActionListener al : dislikeListeners) dislikeBtn.addActionListener(al);
                    
                    likeBtn.setEnabled(true);
                    dislikeBtn.setEnabled(true);
                    isProcessingDislike = false;
                }
            }
        };
        worker.execute();
    }
    
    private void loadComments() {
        if (currentNews == null) return;
        
        SwingWorker<List<NewsComment>, Void> worker = new SwingWorker<List<NewsComment>, Void>() {
            @Override
            protected List<NewsComment> doInBackground() throws Exception {
                return newsController.getComments(currentNews.getNewsId());
            }
            
            @Override
            protected void done() {
                try {
                    List<NewsComment> comments = get();
                    
                    JPanel commentsPanel = new JPanel();
                    commentsPanel.setLayout(new BoxLayout(commentsPanel, BoxLayout.Y_AXIS));
                    commentsPanel.setBackground(CARD_BG);
                    
                    if (comments.isEmpty()) {
                        JLabel noComments = new JLabel("💬 No comments yet. Be the first to comment!");
                        noComments.setFont(new Font("Segoe UI Variable", Font.ITALIC, 12));
                        noComments.setForeground(TEXT_MUTED);
                        noComments.setAlignmentX(Component.CENTER_ALIGNMENT);
                        commentsPanel.add(noComments);
                    } else {
                        for (NewsComment comment : comments) {
                            JPanel commentItem = createCommentItem(comment);
                            commentsPanel.add(commentItem);
                            commentsPanel.add(Box.createRigidArea(new Dimension(0, SPACING_SM)));
                        }
                    }
                    
                    commentsScroll.setViewportView(commentsPanel);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private JPanel createCommentItem(NewsComment comment) {
        JPanel panel = new JPanel(new BorderLayout(SPACING_SM, SPACING_SM));
        panel.setBackground(new Color(248, 249, 250));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(SPACING_MD, SPACING_MD, SPACING_MD, SPACING_MD)
        ));
        panel.setMaximumSize(new Dimension(CONTENT_WIDTH - 70, 70));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(248, 249, 250));
        
        JLabel userLabel = new JLabel("👤 " + comment.getUsername());
        userLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 11));
        userLabel.setForeground(ACCENT_BLUE);
        
        JLabel dateLabel = new JLabel(comment.getFormattedDate());
        dateLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 9));
        dateLabel.setForeground(TEXT_MUTED);
        
        headerPanel.add(userLabel, BorderLayout.WEST);
        headerPanel.add(dateLabel, BorderLayout.EAST);
        
        JTextArea commentText = new JTextArea(comment.getCommentText());
        commentText.setEditable(false);
        commentText.setLineWrap(true);
        commentText.setWrapStyleWord(true);
        commentText.setFont(new Font("Segoe UI Variable", Font.PLAIN, 11));
        commentText.setForeground(TEXT_PRIMARY);
        commentText.setBackground(new Color(248, 249, 250));
        commentText.setBorder(BorderFactory.createEmptyBorder(SPACING_SM, 0, 0, 0));
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(commentText, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void postComment() {
        String placeholderText = "Write your comment here...";
        String commentText = commentArea.getText().trim();
        
        if (commentText.isEmpty() || commentText.equals(placeholderText)) {
            JOptionPane.showMessageDialog(this, "⚠️ Please enter a comment", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (commentText.length() > 500) {
            JOptionPane.showMessageDialog(this, "⚠️ Comment too long (max 500 characters)", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        commentBtn.setEnabled(false);
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return newsController.addComment(currentNews.getNewsId(), currentVoter.getUserId(),
                                                  currentVoter.getFullName(), commentText);
            }
            
            @Override
            protected void done() {
                try {
                    if (get()) {
                        commentArea.setText(placeholderText);
                        commentArea.setForeground(TEXT_MUTED);
                        refreshCurrentNews();
                        JOptionPane.showMessageDialog(NewsViewerFrame.this, 
                            "✅ Comment posted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(NewsViewerFrame.this, 
                            "❌ Error posting comment", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    commentBtn.setEnabled(true);
                }
            }
        };
        worker.execute();
    }
    
    private void refreshCurrentNews() {
        if (currentNews != null) {
            showNewsDetail(currentNews);
        }
    }
    
    private void showNewsList() {
        currentNews = null;
        titleLabel.setText("Select a news article");
        summaryLabel.setText(" ");
        
        // Reset content
        JTextPane contentTextPane = (JTextPane) newsDetailPanel.getClientProperty("contentTextPane");
        if (contentTextPane != null) {
            contentTextPane.setText("<html><body style='font-family: Segoe UI Variable; font-size: 14pt; color: #212529; margin: 0; padding: 0; width: 750px;'>Select a news article to read full content</body></html>");
        }
        
        authorLabel.setText("By: ");
        dateLabel.setText("Published: ");
        
        // Reset stat labels
        updateStatLabel(likeCountLabel, "0");
        updateStatLabel(dislikeCountLabel, "0");
        updateStatLabel(commentCountLabel, "0 comments");
        
        imageLabel.setIcon(null);
        imageLabel.setText("📰");
        
        likeBtn.setEnabled(false);
        dislikeBtn.setEnabled(false);
        commentArea.setEnabled(false);
        commentBtn.setEnabled(false);
        backToListBtn.setVisible(false);
        
        String placeholderText = "Write your comment here...";
        commentArea.setText(placeholderText);
        commentArea.setForeground(TEXT_MUTED);
        
        JPanel emptyPanel = new JPanel();
        emptyPanel.setBackground(CARD_BG);
        JLabel emptyLabel = new JLabel("Select a news article to view comments", SwingConstants.CENTER);
        emptyLabel.setFont(new Font("Segoe UI Variable", Font.ITALIC, 12));
        emptyLabel.setForeground(TEXT_MUTED);
        emptyPanel.add(emptyLabel);
        commentsScroll.setViewportView(emptyPanel);
    }
}