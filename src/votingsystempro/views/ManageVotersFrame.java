package votingsystempro.views;

import votingsystempro.controllers.VoterController;
import votingsystempro.controllers.AuthController;
import votingsystempro.controllers.LocationController;
import votingsystempro.models.Voter;
import votingsystempro.utils.ImageUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class ManageVotersFrame extends JFrame {
    private VoterController voterController;
    private AuthController authController;
    private LocationController locationController;
    
    private JTable votersTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> tableSorter;
    
    private JTextField searchField;
    private JComboBox<String> filterCombo;
    private JButton refreshBtn, approveBtn, rejectBtn, editBtn, deleteBtn, backBtn;
    private JButton approveSelectedBtn, resendEmailBtn;
    private JLabel photoLabel;
    private JPanel detailsPanel;
    private JScrollPane detailsScrollPane;
    
    private List<Integer> selectedVoterIds;
    private Map<Integer, String> provinces;
    
    // Modern premium color scheme (matching AdminDashboard)
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
    
    // Icons path (using emoji fallbacks)
    private final String ICON_PATH = "C:\\Users\\KKKKK\\Documents\\NetBeansProjects\\VotingSystemPro\\resources\\icons\\";
    
    public ManageVotersFrame() {
        voterController = new VoterController();
        authController = new AuthController();
        locationController = new LocationController();
        
        selectedVoterIds = new ArrayList<>();
        provinces = locationController.getAllProvinces();
        
        initComponents();
        loadVoters();
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
    
    private void initComponents() {
        setTitle("Manage Voters - Election Commission of Nepal");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Remove default title bar for modern look
        setUndecorated(true);
        
        // Custom title bar with all 3 buttons
        JPanel titleBar = createTitleBar();
        add(titleBar, BorderLayout.NORTH);
        
        // Main Content
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(700);
        splitPane.setBorder(null);
        splitPane.setBackground(CONTENT_BG);
        
        // Left Panel - Voters Table
        JPanel leftPanel = createLeftPanel();
        splitPane.setLeftComponent(leftPanel);
        
        // Right Panel - Voter Details
        JPanel rightPanel = createRightPanel();
        splitPane.setRightComponent(rightPanel);
        
        add(splitPane, BorderLayout.CENTER);
        
        // Status Bar
        JPanel statusBar = createStatusBar();
        add(statusBar, BorderLayout.SOUTH);
        
        // Add window listener to handle visibility
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                SwingUtilities.invokeLater(() -> {
                    validate();
                    repaint();
                });
            }
        });
        
        setSize(1400, 800);
        setLocationRelativeTo(null);
    }
    
    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (b) {
            // Ensure proper layout without shaking
            SwingUtilities.invokeLater(() -> {
                validate();
                repaint();
            });
        }
    }
    
    private JPanel createTitleBar() {
        JPanel titleBar = new JPanel();
        titleBar.setBackground(SIDEBAR_BG);
        titleBar.setPreferredSize(new Dimension(0, 45));
        titleBar.setLayout(new BorderLayout());
        titleBar.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        titleBar.setName("titleBar");
        
        // Left side - Logo and title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        leftPanel.setOpaque(false);
        leftPanel.setName("leftPanel");
        
        JLabel logoLabel = new JLabel("👥");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        logoLabel.setForeground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Voter Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        
        leftPanel.add(logoLabel);
        leftPanel.add(titleLabel);
        
        // Center - Static title (NO DYNAMIC UPDATES)
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        centerPanel.setOpaque(false);
        centerPanel.setName("centerPanel");
        
        JLabel countIcon = new JLabel("📊");
        countIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        countIcon.setForeground(Color.WHITE);
        
        // STATIC TEXT - No updates to prevent shaking
        JLabel countLabel = new JLabel("Voter Management Portal");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        countLabel.setForeground(Color.WHITE);
        countLabel.setName("countLabel");
        
        centerPanel.add(countIcon);
        centerPanel.add(countLabel);
        
        // Right side - ALL 3 WINDOW CONTROLS
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        rightPanel.setOpaque(false);
        rightPanel.setName("rightPanel");
        
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
        closeBtn.addActionListener(e -> {
            new AdminDashboard().setVisible(true);
            dispose();
        });
        
        rightPanel.add(minimizeBtn);
        rightPanel.add(maximizeBtn);
        rightPanel.add(closeBtn);
        
        titleBar.add(leftPanel, BorderLayout.WEST);
        titleBar.add(centerPanel, BorderLayout.CENTER);
        titleBar.add(rightPanel, BorderLayout.EAST);
        
        return titleBar;
    }
    
    private JButton createWindowButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(SIDEBAR_HOVER.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(SIDEBAR_HOVER);
                    g2.setColor(new Color(255, 255, 255, 50));
                    g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 8, 8);
                } else {
                    g2.setColor(SIDEBAR_BG);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(35, 30));
        
        return button;
    }
    
    private JPanel createLeftPanel() {
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
        
        JLabel headerLabel = new JLabel("🗳️ Registered Voters");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(BLUE);
        
        headerPanel.add(headerLabel, BorderLayout.WEST);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Search Panel
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBackground(new Color(248, 250, 252));
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.1;
        JLabel searchLabel = new JLabel("🔍 Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchPanel.add(searchLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.5;
        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        searchField.addActionListener(e -> filterTable());
        searchPanel.add(searchField, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0.1;
        JLabel filterLabel = new JLabel("📋 Filter:");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchPanel.add(filterLabel, gbc);
        
        gbc.gridx = 3; gbc.weightx = 0.3;
        filterCombo = new JComboBox<>(new String[]{
            "All Voters", "⏳ Pending", "✅ Approved", "🗳️ Has Voted", "⏹️ Not Voted"
        });
        filterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterCombo.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 1));
        filterCombo.addActionListener(e -> filterTable());
        searchPanel.add(filterCombo, gbc);
        
        gbc.gridx = 4; gbc.weightx = 0.1;
        refreshBtn = createStyledButton("🔄", BLUE, 40, 35);
        refreshBtn.setToolTipText("Refresh");
        refreshBtn.addActionListener(e -> loadVoters());
        searchPanel.add(refreshBtn, gbc);
        
        panel.add(searchPanel, BorderLayout.CENTER);
        
        // Table
        String[] columns = {"ID", "Voter ID", "Full Name", "Citizenship", "Email", "Status", "FPTP", "PR"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class;
                return String.class;
            }
        };
        
        votersTable = new JTable(tableModel);
        votersTable.setRowHeight(40);
        votersTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        votersTable.setShowGrid(true);
        votersTable.setGridColor(CARD_BORDER);
        votersTable.setSelectionBackground(new Color(173, 216, 230, 50));
        votersTable.setSelectionForeground(TEXT_PRIMARY);
        
        // Table Header
        JTableHeader header = votersTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(240, 240, 240));
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(0, 40));
        
        // Set column widths
        votersTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        votersTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        votersTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        votersTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        votersTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        votersTable.getColumnModel().getColumn(5).setPreferredWidth(70);
        votersTable.getColumnModel().getColumn(6).setPreferredWidth(60);
        votersTable.getColumnModel().getColumn(7).setPreferredWidth(60);
        
        // Custom renderer for status column
        votersTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if ("Approved".equals(value)) {
                    c.setForeground(GREEN);
                    setFont(getFont().deriveFont(Font.BOLD));
                } else if ("Pending".equals(value)) {
                    c.setForeground(YELLOW);
                }
                return c;
            }
        });
        
        // Custom renderer for vote columns
        for (int i = 6; i <= 7; i++) {
            votersTable.getColumnModel().getColumn(i).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if ("✓ Yes".equals(value)) {
                        c.setForeground(GREEN);
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else if ("✗ No".equals(value)) {
                        c.setForeground(RED);
                    }
                    return c;
                }
            });
        }
        
        votersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showVoterDetails();
                updateButtonStates();
            }
        });
        
        JScrollPane tableScroll = new JScrollPane(votersTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        tableScroll.getViewport().setBackground(Color.WHITE);
        
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        tablePanel.add(tableScroll, BorderLayout.CENTER);
        
        panel.add(tablePanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createRightPanel() {
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
        
        JLabel headerLabel = new JLabel("📋 Voter Details");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(PURPLE);
        
        headerPanel.add(headerLabel, BorderLayout.WEST);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Details Panel with ScrollPane to handle overflow
        detailsPanel = createDetailsPanel();
        detailsScrollPane = new JScrollPane(detailsPanel);
        detailsScrollPane.setBorder(null);
        detailsScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        detailsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        panel.add(detailsScrollPane, BorderLayout.CENTER);
        
        // Action Buttons - MODERN DESIGN
        JPanel actionPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        actionPanel.setOpaque(false);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        approveBtn = createModernStyledButton("✅ Approve", GREEN, -1, 45);
        approveBtn.addActionListener(e -> approveVoter());
        
        rejectBtn = createModernStyledButton("❌ Reject", RED, -1, 45);
        rejectBtn.addActionListener(e -> rejectVoter());
        
        editBtn = createModernStyledButton("✏️ Edit", BLUE, -1, 45);
        editBtn.addActionListener(e -> editVoter());
        
        deleteBtn = createModernStyledButton("🗑️ Delete", ORANGE, -1, 45);
        deleteBtn.addActionListener(e -> deleteVoter());
        
        actionPanel.add(approveBtn);
        actionPanel.add(rejectBtn);
        actionPanel.add(editBtn);
        actionPanel.add(deleteBtn);
        
        // Bulk Actions - MODERN DESIGN
        JPanel bulkPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        bulkPanel.setOpaque(false);
        bulkPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        approveSelectedBtn = createModernStyledButton("✅ Approve Selected", GREEN, -1, 45);
        approveSelectedBtn.addActionListener(e -> approveSelectedVoters());
        
        resendEmailBtn = createModernStyledButton("📧 Resend Email", PURPLE, -1, 45);
        resendEmailBtn.addActionListener(e -> resendEmail());
        
        bulkPanel.add(approveSelectedBtn);
        bulkPanel.add(resendEmailBtn);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(actionPanel, BorderLayout.NORTH);
        bottomPanel.add(bulkPanel, BorderLayout.SOUTH);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createStatusBar() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(SIDEBAR_BG);
        bottomPanel.setPreferredSize(new Dimension(1400, 35));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        
        JPanel leftStatus = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        leftStatus.setOpaque(false);
        
        JLabel versionLabel = new JLabel("⚡ Voter Management | Election Commission of Nepal");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        versionLabel.setForeground(new Color(148, 163, 184));
        leftStatus.add(versionLabel);
        
        bottomPanel.add(leftStatus, BorderLayout.WEST);
        
        return bottomPanel;
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
        if (width > 0) {
            button.setPreferredSize(new Dimension(width, height));
        }
        
        return button;
    }
    
    private JButton createModernStyledButton(String text, Color bgColor, int width, int height) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background
                GradientPaint gp;
                if (getModel().isPressed()) {
                    gp = new GradientPaint(0, 0, bgColor.darker().darker(), 
                        getWidth(), getHeight(), bgColor.darker());
                } else if (getModel().isRollover()) {
                    gp = new GradientPaint(0, 0, bgColor.brighter(), 
                        getWidth(), getHeight(), bgColor);
                } else {
                    gp = new GradientPaint(0, 0, bgColor, 
                        getWidth(), getHeight(), bgColor.darker());
                }
                
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Inner glow on hover
                if (getModel().isRollover() && !getModel().isPressed()) {
                    g2.setColor(new Color(255, 255, 255, 70));
                    g2.setStroke(new BasicStroke(2));
                    g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 8, 8);
                }
                
                // Subtle shadow effect
                if (!getModel().isPressed()) {
                    g2.setColor(new Color(0, 0, 0, 30));
                    g2.fillRoundRect(2, 2, getWidth(), getHeight(), 10, 10);
                }
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        if (width > 0) {
            button.setPreferredSize(new Dimension(width, height));
        }
        
        return button;
    }
    
    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Photo with FIXED SIZE to prevent layout shift
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        
        JPanel photoContainer = new JPanel(new BorderLayout());
        photoContainer.setBackground(Color.WHITE);
        photoContainer.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 2));
        photoContainer.setPreferredSize(new Dimension(140, 140)); // FIXED SIZE
        photoContainer.setMinimumSize(new Dimension(140, 140));
        photoContainer.setMaximumSize(new Dimension(140, 140));
        
        photoLabel = new JLabel("📷", SwingConstants.CENTER);
        photoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        photoLabel.setForeground(TEXT_SECONDARY);
        photoLabel.setBackground(Color.WHITE);
        photoLabel.setOpaque(true);
        photoContainer.add(photoLabel, BorderLayout.CENTER);
        
        panel.add(photoContainer, gbc);
        
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 1;
        addDetailField(panel, "Voter ID:", "", gbc, row++, BLUE);
        addDetailField(panel, "Full Name:", "", gbc, row++, TEXT_PRIMARY);
        addDetailField(panel, "Age:", "", gbc, row++, TEXT_PRIMARY);
        addDetailField(panel, "Citizenship No:", "", gbc, row++, TEXT_PRIMARY);
        addDetailField(panel, "Father's Name:", "", gbc, row++, TEXT_PRIMARY);
        addDetailField(panel, "Mother's Name:", "", gbc, row++, TEXT_PRIMARY);
        addDetailField(panel, "Address:", "", gbc, row++, TEXT_PRIMARY);
        addDetailField(panel, "Phone:", "", gbc, row++, TEXT_PRIMARY);
        addDetailField(panel, "Email:", "", gbc, row++, TEXT_PRIMARY);
        addDetailField(panel, "Location:", "", gbc, row++, TEXT_PRIMARY);
        addDetailField(panel, "Status:", "", gbc, row++, GREEN);
        addDetailField(panel, "FPTP Vote:", "", gbc, row++, TEXT_PRIMARY);
        addDetailField(panel, "PR Vote:", "", gbc, row++, TEXT_PRIMARY);
        
        return panel;
    }
    
    private void addDetailField(JPanel panel, String label, String value, GridBagConstraints gbc, int row, Color labelColor) {
        gbc.gridy = row;
        
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.BOLD, 11));
        labelComp.setForeground(labelColor);
        panel.add(labelComp, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        valueComp.setForeground(TEXT_PRIMARY);
        valueComp.setName("field_" + label.replace(":", "").replace(" ", "_"));
        panel.add(valueComp, gbc);
    }
    
    private void filterTable() {
        String searchText = searchField.getText().toLowerCase().trim();
        String filter = (String) filterCombo.getSelectedItem();
        
        tableModel.setRowCount(0);
        List<Voter> voters = voterController.getAllVoters();
        
        if (voters != null) {
            for (Voter voter : voters) {
                boolean include = true;
                
                if (!searchText.isEmpty()) {
                    include = voter.getFullName().toLowerCase().contains(searchText) ||
                             voter.getCitizenshipNumber().toLowerCase().contains(searchText) ||
                             voter.getEmail().toLowerCase().contains(searchText);
                }
                
                if (include && filter != null && !"All Voters".equals(filter)) {
                    switch (filter) {
                        case "⏳ Pending":
                            include = !voter.isApproved();
                            break;
                        case "✅ Approved":
                            include = voter.isApproved();
                            break;
                        case "🗳️ Has Voted":
                            include = voter.isHasVotedFptp() || voter.isHasVotedPr();
                            break;
                        case "⏹️ Not Voted":
                            include = !voter.isHasVotedFptp() && !voter.isHasVotedPr();
                            break;
                    }
                }
                
                if (include) {
                    String status = voter.isApproved() ? "Approved" : "Pending";
                    String fptp = voter.isHasVotedFptp() ? "✓ Yes" : "✗ No";
                    String pr = voter.isHasVotedPr() ? "✓ Yes" : "✗ No";
                    
                    tableModel.addRow(new Object[]{
                        voter.getVoterId(),
                        "VOT" + voter.getUserId(),
                        voter.getFullName(),
                        voter.getCitizenshipNumber(),
                        voter.getEmail(),
                        status,
                        fptp,
                        pr
                    });
                }
            }
            
            // REMOVED: updateTitleBarCount(voters.size()); - This was causing screen shake
        }
    }
    
    private void loadVoters() {
        filterTable();
    }
    
    private void showVoterDetails() {
        int selectedRow = votersTable.getSelectedRow();
        if (selectedRow < 0) return;
        
        int voterId = (int) tableModel.getValueAt(selectedRow, 0);
        Voter voter = voterController.getVoterById(voterId);
        
        if (voter != null) {
            // Load photo with FIXED SIZE
            if (voter.getPhotoPath() != null && !voter.getPhotoPath().isEmpty()) {
                ImageIcon icon = ImageUtil.createImageIcon(voter.getPhotoPath(), 130, 130); // Slightly smaller
                if (icon != null) {
                    photoLabel.setIcon(icon);
                    photoLabel.setText("");
                } else {
                    photoLabel.setIcon(null);
                    photoLabel.setText("📷");
                    photoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
                }
            } else {
                photoLabel.setIcon(null);
                photoLabel.setText("📷");
                photoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
            }
            
            // Update all fields
            updateDetailField("Voter ID:", "VOT" + voter.getUserId());
            updateDetailField("Full Name:", voter.getFullName());
            updateDetailField("Age:", voter.getAge() + " years");
            updateDetailField("Citizenship No:", voter.getCitizenshipNumber());
            updateDetailField("Father's Name:", voter.getFatherName());
            updateDetailField("Mother's Name:", voter.getMotherName());
            updateDetailField("Address:", voter.getAddress());
            updateDetailField("Phone:", voter.getPhoneNumber());
            updateDetailField("Email:", voter.getEmail());
            
            String location = voterController.getConstituencyNumber(voter.getConstituencyId()) + ", " +
                             voterController.getDistrictName(voter.getDistrictId()) + ", " +
                             voterController.getProvinceName(voter.getProvinceId());
            updateDetailField("Location:", location);
            
            updateDetailField("Status:", voter.isApproved() ? "✅ Approved" : "⏳ Pending");
            updateDetailField("FPTP Vote:", voter.isHasVotedFptp() ? "✓ Voted" : "✗ Not Voted");
            updateDetailField("PR Vote:", voter.isHasVotedPr() ? "✓ Voted" : "✗ Not Voted");
            
            // Force layout update
            detailsPanel.revalidate();
            detailsPanel.repaint();
        }
    }
    
    private void updateDetailField(String label, String value) {
        Component[] components = detailsPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JLabel) {
                JLabel labelComp = (JLabel) comp;
                if (labelComp.getName() != null && labelComp.getName().equals("field_" + label.replace(":", "").replace(" ", "_"))) {
                    labelComp.setText(value != null ? value : "N/A");
                    break;
                }
            }
        }
    }
    
    private void updateButtonStates() {
        int selectedRow = votersTable.getSelectedRow();
        boolean hasSelection = selectedRow >= 0;
        
        if (hasSelection) {
            String status = (String) tableModel.getValueAt(selectedRow, 5);
            boolean isApproved = "Approved".equals(status);
            
            approveBtn.setEnabled(!isApproved);
            rejectBtn.setEnabled(!isApproved);
            editBtn.setEnabled(true);
            deleteBtn.setEnabled(true);
            resendEmailBtn.setEnabled(isApproved);
        } else {
            approveBtn.setEnabled(false);
            rejectBtn.setEnabled(false);
            editBtn.setEnabled(false);
            deleteBtn.setEnabled(false);
            resendEmailBtn.setEnabled(false);
        }
    }
    
    private void approveVoter() {
        int selectedRow = votersTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a voter to approve");
            return;
        }
        
        int voterId = (int) tableModel.getValueAt(selectedRow, 0);
        String voterName = (String) tableModel.getValueAt(selectedRow, 2);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Approve " + voterName + "?\n\nVoter ID email will be sent to their email address.",
            "Confirm Approval",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (voterController.approveVoter(voterId)) {
                JOptionPane.showMessageDialog(this,
                    "✅ Voter approved successfully!\n\nEmail will be sent shortly.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                loadVoters();
                showVoterDetails();
            } else {
                JOptionPane.showMessageDialog(this,
                    "❌ Error approving voter.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void approveSelectedVoters() {
        int[] selectedRows = votersTable.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Please select voters to approve");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Approve " + selectedRows.length + " selected voters?",
            "Confirm Bulk Approval",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int successCount = 0;
            for (int row : selectedRows) {
                int voterId = (int) tableModel.getValueAt(row, 0);
                if (voterController.approveVoter(voterId)) {
                    successCount++;
                }
            }
            
            JOptionPane.showMessageDialog(this,
                "✅ Approved " + successCount + " out of " + selectedRows.length + " voters.",
                "Bulk Approval Complete",
                JOptionPane.INFORMATION_MESSAGE);
            
            loadVoters();
        }
    }
    
    private void resendEmail() {
        int selectedRow = votersTable.getSelectedRow();
        if (selectedRow < 0) return;
        
        int voterId = (int) tableModel.getValueAt(selectedRow, 0);
        String voterName = (String) tableModel.getValueAt(selectedRow, 2);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Resend Voter ID email to " + voterName + "?",
            "Confirm Resend",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (authController.sendVoterIdEmail(voterId)) {
                JOptionPane.showMessageDialog(this,
                    "✅ Email resent successfully!",
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
    
    private void rejectVoter() {
        int selectedRow = votersTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a voter to reject");
            return;
        }
        
        int voterId = (int) tableModel.getValueAt(selectedRow, 0);
        String voterName = (String) tableModel.getValueAt(selectedRow, 2);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Reject " + voterName + "?\n\n⚠️ This will permanently delete the voter's registration!",
            "Confirm Rejection",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (voterController.rejectVoter(voterId)) {
                JOptionPane.showMessageDialog(this,
                    "✅ Voter rejected and removed from system.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                loadVoters();
                clearDetails();
            } else {
                JOptionPane.showMessageDialog(this,
                    "❌ Error rejecting voter",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void editVoter() {
        JOptionPane.showMessageDialog(this,
            "✏️ Edit feature coming soon in Version 2.1!",
            "Under Development",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void deleteVoter() {
        int selectedRow = votersTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a voter to delete");
            return;
        }
        
        int voterId = (int) tableModel.getValueAt(selectedRow, 0);
        String voterName = (String) tableModel.getValueAt(selectedRow, 2);
        
        JTextField confirmField = new JTextField(10);
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.add(new JLabel("Type 'DELETE' to confirm:"));
        panel.add(confirmField);
        
        int confirm = JOptionPane.showConfirmDialog(this, panel,
            "⚠️ PERMANENT DELETE CONFIRMATION",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.OK_OPTION && "DELETE".equals(confirmField.getText().toUpperCase())) {
            if (voterController.deleteVoter(voterId)) {
                JOptionPane.showMessageDialog(this,
                    "✅ Voter permanently deleted.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                loadVoters();
                clearDetails();
            } else {
                JOptionPane.showMessageDialog(this,
                    "❌ Error deleting voter",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void clearDetails() {
        photoLabel.setIcon(null);
        photoLabel.setText("📷");
        photoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        
        updateDetailField("Voter ID:", "");
        updateDetailField("Full Name:", "");
        updateDetailField("Age:", "");
        updateDetailField("Citizenship No:", "");
        updateDetailField("Father's Name:", "");
        updateDetailField("Mother's Name:", "");
        updateDetailField("Address:", "");
        updateDetailField("Phone:", "");
        updateDetailField("Email:", "");
        updateDetailField("Location:", "");
        updateDetailField("Status:", "");
        updateDetailField("FPTP Vote:", "");
        updateDetailField("PR Vote:", "");
    }
}