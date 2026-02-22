package votingsystempro.views;

import votingsystempro.controllers.LocationController;
import votingsystempro.models.Province;
import votingsystempro.models.District;
import votingsystempro.models.Constituency;

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
import java.util.List;
import java.util.Map;

public class ManageLocationsFrame extends JFrame {
    private LocationController locationController;
    
    private JTabbedPane tabbedPane;
    private JTable provincesTable, districtsTable, constituenciesTable;
    private DefaultTableModel provinceModel, districtModel, constituencyModel;
    private JTextField provinceNameField, provinceNumberField;
    private JTextField districtNameField;
    private JComboBox<String> provinceCombo, districtProvinceCombo, constituencyDistrictCombo;
    private JTextField constituencyNumberField;
    private JButton addProvinceBtn, updateProvinceBtn, deleteProvinceBtn;
    private JButton addDistrictBtn, updateDistrictBtn, deleteDistrictBtn;
    private JButton addConstituencyBtn, updateConstituencyBtn, deleteConstituencyBtn;
    private JButton refreshBtn, backBtn;
    
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
    
    public ManageLocationsFrame() {
        locationController = new LocationController();
        initComponents();
        loadProvinces();
        loadDistricts();
        loadConstituencies();
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
        setTitle("Location Management - Election Commission of Nepal");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Remove default title bar for modern look
        setUndecorated(true);
        
        // Custom title bar
        JPanel titleBar = createTitleBar();
        add(titleBar, BorderLayout.NORTH);
        
        // Tabbed Pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(CONTENT_BG);
        tabbedPane.setForeground(TEXT_PRIMARY);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Style the tabbed pane
        UIManager.put("TabbedPane.selected", Color.WHITE);
        UIManager.put("TabbedPane.contentAreaColor", CONTENT_BG);
        UIManager.put("TabbedPane.tabAreaBackground", CONTENT_BG);
        UIManager.put("TabbedPane.unselectedBackground", new Color(255, 255, 255, 200));
        
        // Add tabs
        tabbedPane.addTab("🏛️ Provinces", createProvincesPanel());
        tabbedPane.addTab("🗺️ Districts", createDistrictsPanel());
        tabbedPane.addTab("🗳️ Constituencies", createConstituenciesPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Status Bar
        JPanel statusBar = createStatusBar();
        add(statusBar, BorderLayout.SOUTH);
        
        setSize(1200, 800);
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
        
        JLabel logoLabel = new JLabel("🗺️");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        logoLabel.setForeground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Location Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        
        leftPanel.add(logoLabel);
        leftPanel.add(titleLabel);
        
        // Center - subtitle
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        centerPanel.setOpaque(false);
        
        JLabel subtitleLabel = new JLabel("Provinces, Districts & Constituencies of Nepal");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(148, 163, 184));
        
        centerPanel.add(subtitleLabel);
        
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
        
        JLabel versionLabel = new JLabel("🗺️ Location Management | Election Commission of Nepal");
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
        button.setPreferredSize(new Dimension(width, height));
        
        return button;
    }
    
    private JPanel createProvincesPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(CONTENT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Form Panel
        JPanel formPanel = createGlassPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title
        JLabel formTitle = new JLabel("🏛️ Province Management");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formTitle.setForeground(BLUE);
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(formTitle, gbc);
        
        // Separator
        JSeparator sep = new JSeparator();
        sep.setForeground(CARD_BORDER);
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(sep, gbc);
        
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        
        // Province Name
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel nameLabel = new JLabel("Province Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nameLabel.setForeground(TEXT_PRIMARY);
        formPanel.add(nameLabel, gbc);
        
        gbc.gridx = 1;
        provinceNameField = new JTextField(20);
        provinceNameField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        provinceNameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        formPanel.add(provinceNameField, gbc);
        
        // Province Number
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel numberLabel = new JLabel("Province Number:");
        numberLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        numberLabel.setForeground(TEXT_PRIMARY);
        formPanel.add(numberLabel, gbc);
        
        gbc.gridx = 1;
        provinceNumberField = new JTextField(20);
        provinceNumberField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        provinceNumberField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        formPanel.add(provinceNumberField, gbc);
        
        // Buttons
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setOpaque(false);
        
        addProvinceBtn = createStyledButton("➕ ADD", GREEN, 100, 35);
        addProvinceBtn.addActionListener(e -> addProvince());
        
        updateProvinceBtn = createStyledButton("✏️ UPDATE", BLUE, 100, 35);
        updateProvinceBtn.addActionListener(e -> updateProvince());
        
        deleteProvinceBtn = createStyledButton("🗑️ DELETE", RED, 100, 35);
        deleteProvinceBtn.addActionListener(e -> deleteProvince());
        
        buttonPanel.add(addProvinceBtn);
        buttonPanel.add(updateProvinceBtn);
        buttonPanel.add(deleteProvinceBtn);
        formPanel.add(buttonPanel, gbc);
        
        panel.add(formPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"ID", "Province Name", "Number", "Districts"};
        provinceModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        provincesTable = createStyledTable(provinceModel, BLUE);
        provincesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectProvince();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(provincesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createDistrictsPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(CONTENT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Form Panel
        JPanel formPanel = createGlassPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title
        JLabel formTitle = new JLabel("🗺️ District Management");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formTitle.setForeground(GREEN);
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(formTitle, gbc);
        
        // Separator
        JSeparator sep = new JSeparator();
        sep.setForeground(CARD_BORDER);
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(sep, gbc);
        
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        
        // District Name
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel nameLabel = new JLabel("District Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nameLabel.setForeground(TEXT_PRIMARY);
        formPanel.add(nameLabel, gbc);
        
        gbc.gridx = 1;
        districtNameField = new JTextField(20);
        districtNameField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        districtNameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        formPanel.add(districtNameField, gbc);
        
        // Province Combo
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel provinceLabel = new JLabel("Province:");
        provinceLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        provinceLabel.setForeground(TEXT_PRIMARY);
        formPanel.add(provinceLabel, gbc);
        
        gbc.gridx = 1;
        districtProvinceCombo = new JComboBox<>();
        districtProvinceCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        districtProvinceCombo.setBackground(Color.WHITE);
        districtProvinceCombo.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 1));
        districtProvinceCombo.addItem("Select Province");
        formPanel.add(districtProvinceCombo, gbc);
        
        // Buttons
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setOpaque(false);
        
        addDistrictBtn = createStyledButton("➕ ADD", GREEN, 100, 35);
        addDistrictBtn.addActionListener(e -> addDistrict());
        
        updateDistrictBtn = createStyledButton("✏️ UPDATE", BLUE, 100, 35);
        updateDistrictBtn.addActionListener(e -> updateDistrict());
        
        deleteDistrictBtn = createStyledButton("🗑️ DELETE", RED, 100, 35);
        deleteDistrictBtn.addActionListener(e -> deleteDistrict());
        
        buttonPanel.add(addDistrictBtn);
        buttonPanel.add(updateDistrictBtn);
        buttonPanel.add(deleteDistrictBtn);
        formPanel.add(buttonPanel, gbc);
        
        panel.add(formPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"ID", "District Name", "Province", "Constituencies"};
        districtModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        districtsTable = createStyledTable(districtModel, GREEN);
        districtsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectDistrict();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(districtsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createConstituenciesPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(CONTENT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Form Panel
        JPanel formPanel = createGlassPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title
        JLabel formTitle = new JLabel("🗳️ Constituency Management");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formTitle.setForeground(PURPLE);
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(formTitle, gbc);
        
        // Separator
        JSeparator sep = new JSeparator();
        sep.setForeground(CARD_BORDER);
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(sep, gbc);
        
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        
        // Constituency Number
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel numberLabel = new JLabel("Constituency Number:");
        numberLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        numberLabel.setForeground(TEXT_PRIMARY);
        formPanel.add(numberLabel, gbc);
        
        gbc.gridx = 1;
        constituencyNumberField = new JTextField(20);
        constituencyNumberField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        constituencyNumberField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        formPanel.add(constituencyNumberField, gbc);
        
        // District Combo
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel districtLabel = new JLabel("District:");
        districtLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        districtLabel.setForeground(TEXT_PRIMARY);
        formPanel.add(districtLabel, gbc);
        
        gbc.gridx = 1;
        constituencyDistrictCombo = new JComboBox<>();
        constituencyDistrictCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        constituencyDistrictCombo.setBackground(Color.WHITE);
        constituencyDistrictCombo.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 1));
        constituencyDistrictCombo.addItem("Select District");
        formPanel.add(constituencyDistrictCombo, gbc);
        
        // Buttons
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setOpaque(false);
        
        addConstituencyBtn = createStyledButton("➕ ADD", GREEN, 100, 35);
        addConstituencyBtn.addActionListener(e -> addConstituency());
        
        updateConstituencyBtn = createStyledButton("✏️ UPDATE", BLUE, 100, 35);
        updateConstituencyBtn.addActionListener(e -> updateConstituency());
        
        deleteConstituencyBtn = createStyledButton("🗑️ DELETE", RED, 100, 35);
        deleteConstituencyBtn.addActionListener(e -> deleteConstituency());
        
        buttonPanel.add(addConstituencyBtn);
        buttonPanel.add(updateConstituencyBtn);
        buttonPanel.add(deleteConstituencyBtn);
        formPanel.add(buttonPanel, gbc);
        
        panel.add(formPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"ID", "Constituency No", "District", "Province"};
        constituencyModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        constituenciesTable = createStyledTable(constituencyModel, PURPLE);
        constituenciesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectConstituency();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(constituenciesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Bottom buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        bottomPanel.setOpaque(false);
        
        refreshBtn = createStyledButton("🔄 REFRESH ALL", ORANGE, 150, 40);
        refreshBtn.addActionListener(e -> refreshAll());
        
        backBtn = createStyledButton("◀ BACK TO DASHBOARD", SIDEBAR_BG, 200, 40);
        backBtn.addActionListener(e -> {
            new AdminDashboard().setVisible(true);
            dispose();
        });
        
        bottomPanel.add(refreshBtn);
        bottomPanel.add(backBtn);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
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
    
    private JTable createStyledTable(DefaultTableModel model, Color headerBgColor) {
        JTable table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setForeground(TEXT_PRIMARY);
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(new Color(173, 216, 230, 50));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setShowGrid(true);
        table.setGridColor(CARD_BORDER);
        
        // Center align text
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setForeground(TEXT_PRIMARY);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Table Header - FIXED: Text BLACK now
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(headerBgColor);
        header.setForeground(Color.BLACK); // Changed to BLACK
        header.setPreferredSize(new Dimension(0, 40));
        
        return table;
    }
    
    private void loadProvinces() {
        provinceModel.setRowCount(0);
        Map<Integer, String> provinces = locationController.getAllProvinces();
        
        districtProvinceCombo.removeAllItems();
        districtProvinceCombo.addItem("Select Province");
        
        for (Map.Entry<Integer, String> entry : provinces.entrySet()) {
            int districtCount = locationController.getDistrictsByProvince(entry.getKey()).size();
            provinceModel.addRow(new Object[]{
                entry.getKey(),
                entry.getValue(),
                getProvinceNumber(entry.getValue()),
                districtCount
            });
            
            districtProvinceCombo.addItem(entry.getValue());
        }
    }
    
    private int getProvinceNumber(String provinceName) {
        if (provinceName.contains("Province No.")) {
            String num = provinceName.replace("Province No.", "").trim();
            try {
                return Integer.parseInt(num);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
    
    private void loadDistricts() {
        districtModel.setRowCount(0);
        List<String> districts = locationController.getAllDistrictsWithProvince();
        
        constituencyDistrictCombo.removeAllItems();
        constituencyDistrictCombo.addItem("Select District");
        
        for (String district : districts) {
            String[] parts = district.split(" \\(");
            String districtName = parts[0];
            String province = parts[1].replace(")", "");
            
            int districtId = districts.indexOf(district) + 1;
            int constituencyCount = 0;
            
            districtModel.addRow(new Object[]{
                districtId,
                districtName,
                province,
                constituencyCount
            });
            
            constituencyDistrictCombo.addItem(districtName + " (" + province + ")");
        }
    }
    
    private void loadConstituencies() {
        constituencyModel.setRowCount(0);
        // Load constituencies from database here
    }
    
    private void selectProvince() {
        int selectedRow = provincesTable.getSelectedRow();
        if (selectedRow >= 0) {
            provinceNameField.setText((String) provinceModel.getValueAt(selectedRow, 1));
            provinceNumberField.setText(String.valueOf(provinceModel.getValueAt(selectedRow, 2)));
        }
    }
    
    private void selectDistrict() {
        int selectedRow = districtsTable.getSelectedRow();
        if (selectedRow >= 0) {
            districtNameField.setText((String) districtModel.getValueAt(selectedRow, 1));
            String province = (String) districtModel.getValueAt(selectedRow, 2);
            districtProvinceCombo.setSelectedItem(province);
        }
    }
    
    private void selectConstituency() {
        int selectedRow = constituenciesTable.getSelectedRow();
        if (selectedRow >= 0) {
            constituencyNumberField.setText(String.valueOf(constituencyModel.getValueAt(selectedRow, 1)));
            String district = (String) constituencyModel.getValueAt(selectedRow, 2);
            constituencyDistrictCombo.setSelectedItem(district);
        }
    }
    
    private void addProvince() {
        String name = provinceNameField.getText().trim();
        String numberStr = provinceNumberField.getText().trim();
        
        if (name.isEmpty() || numberStr.isEmpty()) {
            showWarning("Please fill all fields");
            return;
        }
        
        try {
            int number = Integer.parseInt(numberStr);
            if (locationController.addProvince(name, number)) {
                showSuccess("Province added successfully");
                clearProvinceForm();
                loadProvinces();
            } else {
                showError("Error adding province");
            }
        } catch (NumberFormatException e) {
            showError("Invalid province number");
        }
    }
    
    private void updateProvince() {
        int selectedRow = provincesTable.getSelectedRow();
        if (selectedRow < 0) {
            showWarning("Please select a province");
            return;
        }
        
        int provinceId = (int) provinceModel.getValueAt(selectedRow, 0);
        String name = provinceNameField.getText().trim();
        String numberStr = provinceNumberField.getText().trim();
        
        try {
            int number = Integer.parseInt(numberStr);
            if (locationController.updateProvince(provinceId, name, number)) {
                showSuccess("Province updated successfully");
                loadProvinces();
            } else {
                showError("Error updating province");
            }
        } catch (NumberFormatException e) {
            showError("Invalid province number");
        }
    }
    
    private void deleteProvince() {
        int selectedRow = provincesTable.getSelectedRow();
        if (selectedRow < 0) {
            showWarning("Please select a province");
            return;
        }
        
        int provinceId = (int) provinceModel.getValueAt(selectedRow, 0);
        String provinceName = (String) provinceModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete " + provinceName + "?\nThis will delete all districts and constituencies in this province.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (locationController.deleteProvince(provinceId)) {
                showSuccess("Province deleted successfully");
                clearProvinceForm();
                loadProvinces();
                loadDistricts();
                loadConstituencies();
            } else {
                showError("Error deleting province");
            }
        }
    }
    
    private void addDistrict() {
        String name = districtNameField.getText().trim();
        int provinceIndex = districtProvinceCombo.getSelectedIndex();
        
        if (name.isEmpty() || provinceIndex == 0) {
            showWarning("Please fill all fields");
            return;
        }
        
        int provinceId = provinceIndex;
        
        if (locationController.addDistrict(name, provinceId)) {
            showSuccess("District added successfully");
            clearDistrictForm();
            loadDistricts();
        } else {
            showError("Error adding district");
        }
    }
    
    private void updateDistrict() {
        int selectedRow = districtsTable.getSelectedRow();
        if (selectedRow < 0) {
            showWarning("Please select a district");
            return;
        }
        
        int districtId = (int) districtModel.getValueAt(selectedRow, 0);
        String name = districtNameField.getText().trim();
        int provinceIndex = districtProvinceCombo.getSelectedIndex();
        
        if (name.isEmpty() || provinceIndex == 0) {
            showWarning("Please fill all fields");
            return;
        }
        
        int provinceId = provinceIndex;
        
        if (locationController.updateDistrict(districtId, name, provinceId)) {
            showSuccess("District updated successfully");
            loadDistricts();
        } else {
            showError("Error updating district");
        }
    }
    
    private void deleteDistrict() {
        int selectedRow = districtsTable.getSelectedRow();
        if (selectedRow < 0) {
            showWarning("Please select a district");
            return;
        }
        
        int districtId = (int) districtModel.getValueAt(selectedRow, 0);
        String districtName = (String) districtModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete " + districtName + "?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (locationController.deleteDistrict(districtId)) {
                showSuccess("District deleted successfully");
                clearDistrictForm();
                loadDistricts();
                loadConstituencies();
            } else {
                showError("Error deleting district");
            }
        }
    }
    
    private void addConstituency() {
        String numberStr = constituencyNumberField.getText().trim();
        int districtIndex = constituencyDistrictCombo.getSelectedIndex();
        
        if (numberStr.isEmpty() || districtIndex == 0) {
            showWarning("Please fill all fields");
            return;
        }
        
        try {
            int number = Integer.parseInt(numberStr);
            int districtId = districtIndex;
            
            if (locationController.addConstituency(number, districtId)) {
                showSuccess("Constituency added successfully");
                clearConstituencyForm();
                loadConstituencies();
            } else {
                showError("Error adding constituency");
            }
        } catch (NumberFormatException e) {
            showError("Invalid constituency number");
        }
    }
    
    private void updateConstituency() {
        int selectedRow = constituenciesTable.getSelectedRow();
        if (selectedRow < 0) {
            showWarning("Please select a constituency");
            return;
        }
        
        int constituencyId = (int) constituencyModel.getValueAt(selectedRow, 0);
        String numberStr = constituencyNumberField.getText().trim();
        int districtIndex = constituencyDistrictCombo.getSelectedIndex();
        
        try {
            int number = Integer.parseInt(numberStr);
            int districtId = districtIndex;
            
            if (locationController.updateConstituency(constituencyId, number, districtId)) {
                showSuccess("Constituency updated successfully");
                loadConstituencies();
            } else {
                showError("Error updating constituency");
            }
        } catch (NumberFormatException e) {
            showError("Invalid constituency number");
        }
    }
    
    private void deleteConstituency() {
        int selectedRow = constituenciesTable.getSelectedRow();
        if (selectedRow < 0) {
            showWarning("Please select a constituency");
            return;
        }
        
        int constituencyId = (int) constituencyModel.getValueAt(selectedRow, 0);
        String constituencyNo = String.valueOf(constituencyModel.getValueAt(selectedRow, 1));
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete Constituency " + constituencyNo + "?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (locationController.deleteConstituency(constituencyId)) {
                showSuccess("Constituency deleted successfully");
                clearConstituencyForm();
                loadConstituencies();
            } else {
                showError("Error deleting constituency");
            }
        }
    }
    
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, "✅ " + message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, "❌ " + message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, "⚠️ " + message, "Warning", JOptionPane.WARNING_MESSAGE);
    }
    
    private void clearProvinceForm() {
        provinceNameField.setText("");
        provinceNumberField.setText("");
        provincesTable.clearSelection();
    }
    
    private void clearDistrictForm() {
        districtNameField.setText("");
        districtProvinceCombo.setSelectedIndex(0);
        districtsTable.clearSelection();
    }
    
    private void clearConstituencyForm() {
        constituencyNumberField.setText("");
        constituencyDistrictCombo.setSelectedIndex(0);
        constituenciesTable.clearSelection();
    }
    
    private void refreshAll() {
        loadProvinces();
        loadDistricts();
        loadConstituencies();
        showSuccess("All data refreshed");
    }
}