package votingsystempro.views;

import votingsystempro.controllers.CandidateController;
import votingsystempro.controllers.PartyController;
import votingsystempro.controllers.LocationController;
import votingsystempro.models.Candidate;
import votingsystempro.models.Party;
import votingsystempro.utils.ImageUtil;

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
import java.util.HashMap;

public class ManageCandidatesFrame extends JFrame {
    private CandidateController candidateController;
    private PartyController partyController;
    private LocationController locationController;
    
    private JTable candidatesTable;
    private DefaultTableModel tableModel;
    private JTextField searchField, nameField, bioField;
    private JComboBox<String> partyCombo, positionCombo, provinceCombo, districtCombo, constituencyCombo;
    private JButton uploadPhotoBtn, addBtn, updateBtn, deleteBtn, refreshBtn, backBtn, clearBtn;
    private JLabel photoLabel, titleLabel;
    private JLabel countLabel;
    
    private String photoPath;
    private int selectedCandidateId = -1;
    
    private Map<Integer, String> parties;
    private Map<Integer, String> provinces;
    private Map<Integer, String> districts;
    private Map<Integer, String> constituencies;
    
    // Modern premium color scheme
    private final Color SIDEBAR_BG = new Color(15, 23, 42);
    private final Color SIDEBAR_HOVER = new Color(30, 41, 59);
    private final Color CONTENT_BG = new Color(248, 250, 252);
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
    
    public ManageCandidatesFrame() {
        candidateController = new CandidateController();
        partyController = new PartyController();
        locationController = new LocationController();
        
        parties = new HashMap<>();
        provinces = new HashMap<>();
        districts = new HashMap<>();
        constituencies = new HashMap<>();
        
        initComponents();
        loadParties();
        loadProvinces();
        loadCandidates();
        applyModernEffects();
    }
    
    private void applyModernEffects() {
        setUndecorated(true);
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
        setTitle("Manage Candidates - Election Commission of Nepal");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setUndecorated(true);
        
        // Custom title bar
        JPanel titleBar = createTitleBar();
        add(titleBar, BorderLayout.NORTH);
        
        // Main Content
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(600);
        splitPane.setDividerSize(2);
        splitPane.setBorder(null);
        splitPane.setBackground(CONTENT_BG);
        
        // Left Panel - Candidates Table
        JPanel leftPanel = createLeftPanel();
        splitPane.setLeftComponent(leftPanel);
        
        // Right Panel - Candidate Form
        JPanel rightPanel = createRightPanel();
        splitPane.setRightComponent(rightPanel);
        
        add(splitPane, BorderLayout.CENTER);
        
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
        titleBar.setName("titleBar");
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        leftPanel.setOpaque(false);
        
        JLabel logoLabel = new JLabel("👤");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        logoLabel.setForeground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Candidate Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        
        leftPanel.add(logoLabel);
        leftPanel.add(titleLabel);
        
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        centerPanel.setOpaque(false);
        
        JLabel countIcon = new JLabel("📊");
        countIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        countIcon.setForeground(Color.WHITE);
        
        JLabel countText = new JLabel("Total Candidates: Loading...");
        countText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        countText.setForeground(Color.WHITE);
        countText.setName("countLabel");
        
        centerPanel.add(countIcon);
        centerPanel.add(countText);
        
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
    
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel headerLabel = new JLabel("👥 Registered Candidates");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(PURPLE);
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
        
        gbc.gridx = 1; gbc.weightx = 0.7;
        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        searchField.addActionListener(e -> searchCandidates());
        searchPanel.add(searchField, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0.2;
        refreshBtn = createStyledButton("🔄 Refresh", BLUE, -1, 35);
        refreshBtn.addActionListener(e -> loadCandidates());
        searchPanel.add(refreshBtn, gbc);
        
        panel.add(searchPanel, BorderLayout.CENTER);
        
        // Candidates Table
        String[] columns = {"ID", "Name", "Party", "Position", "Constituency"};
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
        
        candidatesTable = new JTable(tableModel);
        candidatesTable.setRowHeight(40);
        candidatesTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        candidatesTable.setShowGrid(true);
        candidatesTable.setGridColor(CARD_BORDER);
        candidatesTable.setSelectionBackground(new Color(173, 216, 230, 50));
        candidatesTable.setSelectionForeground(TEXT_PRIMARY);
        
        JTableHeader header = candidatesTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(240, 240, 240));
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(0, 40));
        
        // Set column widths
        candidatesTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        candidatesTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        candidatesTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        candidatesTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        candidatesTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        
        // Custom renderer for position column
        candidatesTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if ("🏛️ FPTP".equals(value)) {
                    c.setForeground(BLUE);
                    setFont(getFont().deriveFont(Font.BOLD));
                } else if ("📊 PR".equals(value)) {
                    c.setForeground(PURPLE);
                    setFont(getFont().deriveFont(Font.BOLD));
                }
                return c;
            }
        });
        
        candidatesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showCandidateDetails();
            }
        });
        
        JScrollPane tableScroll = new JScrollPane(candidatesTable);
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
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel headerLabel = new JLabel("📋 Candidate Information");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(GREEN);
        headerPanel.add(headerLabel, BorderLayout.WEST);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Form Panel
        JPanel formPanel = createFormPanel();
        panel.add(formPanel, BorderLayout.CENTER);
        
        // Action Buttons
        JPanel actionPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        actionPanel.setOpaque(false);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        addBtn = createStyledButton("➕ ADD CANDIDATE", GREEN, -1, 45);
        addBtn.addActionListener(e -> addCandidate());
        
        updateBtn = createStyledButton("✏️ UPDATE", BLUE, -1, 45);
        updateBtn.addActionListener(e -> updateCandidate());
        
        deleteBtn = createStyledButton("🗑️ DELETE", RED, -1, 45);
        deleteBtn.addActionListener(e -> deleteCandidate());
        
        clearBtn = createStyledButton("🧹 CLEAR", ORANGE, -1, 45);
        clearBtn.addActionListener(e -> clearForm());
        
        backBtn = createStyledButton("← BACK", SIDEBAR_BG, -1, 45);
        backBtn.addActionListener(e -> {
            new AdminDashboard().setVisible(true);
            dispose();
        });
        
        actionPanel.add(addBtn);
        actionPanel.add(updateBtn);
        actionPanel.add(deleteBtn);
        actionPanel.add(clearBtn);
        actionPanel.add(backBtn);
        
        panel.add(actionPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createStatusBar() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(SIDEBAR_BG);
        bottomPanel.setPreferredSize(new Dimension(1400, 35));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        
        JPanel leftStatus = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        leftStatus.setOpaque(false);
        
        JLabel versionLabel = new JLabel("👥 Candidate Management | Election Commission of Nepal");
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
                
                if (!isEnabled()) {
                    g2.setColor(new Color(180, 180, 180));
                } else if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                if (getModel().isRollover() && isEnabled()) {
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
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 1));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Photo Section
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        
        JPanel photoContainer = new JPanel(new BorderLayout());
        photoContainer.setBackground(Color.WHITE);
        photoContainer.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 2));
        photoContainer.setPreferredSize(new Dimension(140, 140));
        
        photoLabel = new JLabel("📷", SwingConstants.CENTER);
        photoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        photoLabel.setForeground(TEXT_SECONDARY);
        photoLabel.setBackground(Color.WHITE);
        photoLabel.setOpaque(true);
        photoContainer.add(photoLabel, BorderLayout.CENTER);
        
        panel.add(photoContainer, gbc);
        row++;
        
        gbc.gridy = row;
        gbc.insets = new Insets(5, 12, 15, 12);
        uploadPhotoBtn = createStyledButton("📁 Upload Photo", PURPLE, -1, 35);
        uploadPhotoBtn.addActionListener(e -> uploadPhoto());
        panel.add(uploadPhotoBtn, gbc);
        row++;
        
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Candidate Name
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel nameLabel = new JLabel("Candidate Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLabel.setForeground(TEXT_PRIMARY);
        panel.add(nameLabel, gbc);
        
        gbc.gridx = 1;
        nameField = new JTextField();
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        nameField.setPreferredSize(new Dimension(250, 35));
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        panel.add(nameField, gbc);
        row++;
        
        // Party
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel partyLabel = new JLabel("Party:");
        partyLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        partyLabel.setForeground(TEXT_PRIMARY);
        panel.add(partyLabel, gbc);
        
        gbc.gridx = 1;
        partyCombo = new JComboBox<>();
        partyCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        partyCombo.setPreferredSize(new Dimension(250, 35));
        partyCombo.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 1));
        panel.add(partyCombo, gbc);
        row++;
        
        // Position
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel positionLabel = new JLabel("Position:");
        positionLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        positionLabel.setForeground(TEXT_PRIMARY);
        panel.add(positionLabel, gbc);
        
        gbc.gridx = 1;
        positionCombo = new JComboBox<>(new String[]{"Select Position", "🏛️ FPTP", "📊 PR"});
        positionCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        positionCombo.setPreferredSize(new Dimension(250, 35));
        positionCombo.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 1));
        positionCombo.addActionListener(e -> onPositionChange());
        panel.add(positionCombo, gbc);
        row++;
        
        // Province
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel provinceLabel = new JLabel("Province:");
        provinceLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        provinceLabel.setForeground(TEXT_PRIMARY);
        panel.add(provinceLabel, gbc);
        
        gbc.gridx = 1;
        provinceCombo = new JComboBox<>();
        provinceCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        provinceCombo.setPreferredSize(new Dimension(250, 35));
        provinceCombo.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 1));
        provinceCombo.addActionListener(e -> loadDistricts());
        panel.add(provinceCombo, gbc);
        row++;
        
        // District
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel districtLabel = new JLabel("District:");
        districtLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        districtLabel.setForeground(TEXT_PRIMARY);
        panel.add(districtLabel, gbc);
        
        gbc.gridx = 1;
        districtCombo = new JComboBox<>();
        districtCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        districtCombo.setPreferredSize(new Dimension(250, 35));
        districtCombo.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 1));
        districtCombo.addActionListener(e -> loadConstituencies());
        panel.add(districtCombo, gbc);
        row++;
        
        // Constituency
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel constituencyLabel = new JLabel("Constituency:");
        constituencyLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        constituencyLabel.setForeground(TEXT_PRIMARY);
        panel.add(constituencyLabel, gbc);
        
        gbc.gridx = 1;
        constituencyCombo = new JComboBox<>();
        constituencyCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        constituencyCombo.setPreferredSize(new Dimension(250, 35));
        constituencyCombo.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 1));
        panel.add(constituencyCombo, gbc);
        row++;
        
        // Biography
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel bioLabel = new JLabel("Biography:");
        bioLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        bioLabel.setForeground(TEXT_PRIMARY);
        panel.add(bioLabel, gbc);
        
        gbc.gridx = 1;
        bioField = new JTextField();
        bioField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        bioField.setPreferredSize(new Dimension(250, 35));
        bioField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        panel.add(bioField, gbc);
        row++;
        
        return panel;
    }
    
    private void loadParties() {
        parties.clear();
        List<Party> partyList = partyController.getAllParties();
        partyCombo.removeAllItems();
        partyCombo.addItem("Select Party");
        
        if (partyList != null) {
            for (Party party : partyList) {
                partyCombo.addItem(party.getPartyName());
                parties.put(party.getPartyId(), party.getPartyName());
            }
        }
    }
    
    private void loadProvinces() {
        provinces = locationController.getAllProvinces();
        provinceCombo.removeAllItems();
        provinceCombo.addItem("Select Province");
        if (provinces != null) {
            for (Map.Entry<Integer, String> entry : provinces.entrySet()) {
                provinceCombo.addItem(entry.getValue());
            }
        }
    }
    
    private void loadDistricts() {
        int selectedIndex = provinceCombo.getSelectedIndex();
        if (selectedIndex > 0) {
            String selectedProvince = (String) provinceCombo.getSelectedItem();
            int provinceId = -1;
            for (Map.Entry<Integer, String> entry : provinces.entrySet()) {
                if (entry.getValue().equals(selectedProvince)) {
                    provinceId = entry.getKey();
                    break;
                }
            }
            
            if (provinceId != -1) {
                districts = locationController.getDistrictsByProvince(provinceId);
                districtCombo.removeAllItems();
                districtCombo.addItem("Select District");
                if (districts != null) {
                    for (Map.Entry<Integer, String> entry : districts.entrySet()) {
                        districtCombo.addItem(entry.getValue());
                    }
                }
            }
        }
    }
    
    private void loadConstituencies() {
        int selectedIndex = districtCombo.getSelectedIndex();
        if (selectedIndex > 0) {
            String selectedDistrict = (String) districtCombo.getSelectedItem();
            int districtId = -1;
            for (Map.Entry<Integer, String> entry : districts.entrySet()) {
                if (entry.getValue().equals(selectedDistrict)) {
                    districtId = entry.getKey();
                    break;
                }
            }
            
            if (districtId != -1) {
                constituencies = locationController.getConstituenciesByDistrict(districtId);
                constituencyCombo.removeAllItems();
                constituencyCombo.addItem("Select Constituency");
                if (constituencies != null) {
                    for (Map.Entry<Integer, String> entry : constituencies.entrySet()) {
                        constituencyCombo.addItem(entry.getValue());
                    }
                }
            }
        }
    }
    
    private void onPositionChange() {
        String position = (String) positionCombo.getSelectedItem();
        boolean isFPTP = "🏛️ FPTP".equals(position);
        
        provinceCombo.setEnabled(isFPTP);
        districtCombo.setEnabled(isFPTP);
        constituencyCombo.setEnabled(isFPTP);
        
        if (!isFPTP) {
            provinceCombo.setSelectedIndex(0);
            districtCombo.removeAllItems();
            districtCombo.addItem("Select District");
            constituencyCombo.removeAllItems();
            constituencyCombo.addItem("Select Constituency");
        }
    }
    
    private void loadCandidates() {
        tableModel.setRowCount(0);
        List<Candidate> candidates = candidateController.getAllCandidates();
        
        if (candidates != null) {
            for (Candidate candidate : candidates) {
                String position = "fptp".equals(candidate.getPosition()) ? "🏛️ FPTP" : "📊 PR";
                String location = candidate.getConstituencyNumber() != null ? 
                    candidate.getConstituencyNumber() : "N/A";
                
                tableModel.addRow(new Object[]{
                    candidate.getCandidateId(),
                    candidate.getCandidateName(),
                    candidate.getPartyName() != null ? candidate.getPartyName() : "Independent",
                    position,
                    location
                });
            }
        }
        
        // Update title bar count
        updateTitleBarCount(tableModel.getRowCount());
    }
    
    private void updateTitleBarCount(int total) {
        try {
            Container contentPane = getContentPane();
            if (contentPane.getComponentCount() > 0) {
                Component comp0 = contentPane.getComponent(0);
                if (comp0 instanceof JPanel) {
                    JPanel titleBar = (JPanel) comp0;
                    if (titleBar.getComponentCount() > 1) {
                        Component comp1 = titleBar.getComponent(1);
                        if (comp1 instanceof JPanel) {
                            JPanel centerPanel = (JPanel) comp1;
                            for (Component comp : centerPanel.getComponents()) {
                                if (comp instanceof JLabel) {
                                    JLabel label = (JLabel) comp;
                                    if (label.getText().startsWith("Total Candidates:")) {
                                        label.setText("Total Candidates: " + total);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Ignore
        }
    }
    
    private void searchCandidates() {
        String searchText = searchField.getText().toLowerCase().trim();
        if (searchText.isEmpty()) {
            loadCandidates();
            return;
        }
        
        tableModel.setRowCount(0);
        List<Candidate> candidates = candidateController.getAllCandidates();
        
        if (candidates != null) {
            for (Candidate candidate : candidates) {
                if (candidate.getCandidateName().toLowerCase().contains(searchText) ||
                    (candidate.getPartyName() != null && candidate.getPartyName().toLowerCase().contains(searchText))) {
                    
                    String position = "fptp".equals(candidate.getPosition()) ? "🏛️ FPTP" : "📊 PR";
                    String location = candidate.getConstituencyNumber() != null ? 
                        candidate.getConstituencyNumber() : "N/A";
                    
                    tableModel.addRow(new Object[]{
                        candidate.getCandidateId(),
                        candidate.getCandidateName(),
                        candidate.getPartyName() != null ? candidate.getPartyName() : "Independent",
                        position,
                        location
                    });
                }
            }
        }
    }
    
    private void showCandidateDetails() {
        int selectedRow = candidatesTable.getSelectedRow();
        if (selectedRow < 0) return;
        
        selectedCandidateId = (int) tableModel.getValueAt(selectedRow, 0);
        Candidate candidate = candidateController.getCandidateById(selectedCandidateId);
        
        if (candidate != null) {
            nameField.setText(candidate.getCandidateName());
            partyCombo.setSelectedItem(candidate.getPartyName());
            
            String position = "fptp".equals(candidate.getPosition()) ? "🏛️ FPTP" : "📊 PR";
            positionCombo.setSelectedItem(position);
            
            bioField.setText(candidate.getBiography() != null ? candidate.getBiography() : "");
            
            // Load photo
            if (candidate.getPhotoPath() != null && !candidate.getPhotoPath().isEmpty()) {
                ImageIcon icon = ImageUtil.createImageIcon(candidate.getPhotoPath(), 130, 130);
                if (icon != null) {
                    photoLabel.setIcon(icon);
                    photoLabel.setText("");
                    photoPath = candidate.getPhotoPath();
                } else {
                    photoLabel.setIcon(null);
                    photoLabel.setText("📷");
                    photoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
                    photoPath = null;
                }
            } else {
                photoLabel.setIcon(null);
                photoLabel.setText("📷");
                photoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
                photoPath = null;
            }
            
            // Load location for FPTP
            if ("fptp".equals(candidate.getPosition())) {
                if (candidate.getProvinceId() > 0) {
                    for (Map.Entry<Integer, String> entry : provinces.entrySet()) {
                        if (entry.getKey().equals(candidate.getProvinceId())) {
                            provinceCombo.setSelectedItem(entry.getValue());
                            break;
                        }
                    }
                }
                
                if (candidate.getDistrictId() > 0) {
                    loadDistricts();
                    for (Map.Entry<Integer, String> entry : districts.entrySet()) {
                        if (entry.getKey().equals(candidate.getDistrictId())) {
                            districtCombo.setSelectedItem(entry.getValue());
                            break;
                        }
                    }
                }
                
                if (candidate.getConstituencyId() > 0) {
                    loadConstituencies();
                    for (Map.Entry<Integer, String> entry : constituencies.entrySet()) {
                        if (entry.getKey().equals(candidate.getConstituencyId())) {
                            constituencyCombo.setSelectedItem(entry.getValue());
                            break;
                        }
                    }
                }
            }
            
            // Enable update/delete, disable add
            addBtn.setEnabled(false);
            updateBtn.setEnabled(true);
            deleteBtn.setEnabled(true);
        }
    }
    
    private void uploadPhoto() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Candidate Photo");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Image files (*.jpg, *.jpeg, *.png, *.gif)", "jpg", "jpeg", "png", "gif"));
        
        int result = fileChooser.showOpenDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            
            if (!selectedFile.exists()) {
                JOptionPane.showMessageDialog(this, "❌ File does not exist!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (selectedFile.length() > 5 * 1024 * 1024) {
                JOptionPane.showMessageDialog(this, "❌ File too large! (Max 5MB)", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            uploadPhotoBtn.setEnabled(false);
            
            SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() throws Exception {
                    return ImageUtil.saveImage(selectedFile, "candidate_photos");
                }
                
                @Override
                protected void done() {
                    try {
                        photoPath = get();
                        if (photoPath != null) {
                            ImageIcon icon = ImageUtil.createImageIcon(photoPath, 130, 130);
                            if (icon != null) {
                                photoLabel.setIcon(icon);
                                photoLabel.setText("");
                                JOptionPane.showMessageDialog(ManageCandidatesFrame.this,
                                    "✅ Photo uploaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(ManageCandidatesFrame.this,
                            "❌ Error uploading photo: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        setCursor(Cursor.getDefaultCursor());
                        uploadPhotoBtn.setEnabled(true);
                    }
                }
            };
            worker.execute();
        }
    }
    
    private void addCandidate() {
        if (!validateInputs()) return;
        
        Candidate candidate = new Candidate();
        candidate.setCandidateName(nameField.getText().trim());
        candidate.setPartyId(getSelectedPartyId());
        candidate.setPartyName((String) partyCombo.getSelectedItem());
        
        String position = (String) positionCombo.getSelectedItem();
        candidate.setPosition("🏛️ FPTP".equals(position) ? "fptp" : "pr");
        
        candidate.setBiography(bioField.getText().trim());
        candidate.setPhotoPath(photoPath);
        
        if ("fptp".equals(candidate.getPosition())) {
            candidate.setProvinceId(getSelectedProvinceId());
            candidate.setDistrictId(getSelectedDistrictId());
            candidate.setConstituencyId(getSelectedConstituencyId());
        }
        
        if (candidateController.addCandidate(candidate)) {
            JOptionPane.showMessageDialog(this, "✅ Candidate added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadCandidates();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Error adding candidate.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateCandidate() {
        if (selectedCandidateId == -1) {
            JOptionPane.showMessageDialog(this, "⚠️ Please select a candidate to update", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!validateInputs()) return;
        
        Candidate candidate = candidateController.getCandidateById(selectedCandidateId);
        
        if (candidate != null) {
            candidate.setCandidateName(nameField.getText().trim());
            candidate.setPartyId(getSelectedPartyId());
            
            String position = (String) positionCombo.getSelectedItem();
            candidate.setPosition("🏛️ FPTP".equals(position) ? "fptp" : "pr");
            
            candidate.setBiography(bioField.getText().trim());
            if (photoPath != null && !photoPath.isEmpty()) {
                candidate.setPhotoPath(photoPath);
            }
            
            if ("fptp".equals(candidate.getPosition())) {
                candidate.setProvinceId(getSelectedProvinceId());
                candidate.setDistrictId(getSelectedDistrictId());
                candidate.setConstituencyId(getSelectedConstituencyId());
            }
            
            if (candidateController.updateCandidate(candidate)) {
                JOptionPane.showMessageDialog(this, "✅ Candidate updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadCandidates();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Error updating candidate.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void deleteCandidate() {
        if (selectedCandidateId == -1) {
            JOptionPane.showMessageDialog(this, "⚠️ Please select a candidate to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String candidateName = nameField.getText().trim();
        if (candidateName.isEmpty()) {
            candidateName = "this candidate";
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete " + candidateName + "?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (candidateController.deleteCandidate(selectedCandidateId)) {
                JOptionPane.showMessageDialog(this, "✅ Candidate deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadCandidates();
                selectedCandidateId = -1;
            } else {
                JOptionPane.showMessageDialog(this, "❌ Error deleting candidate.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private boolean validateInputs() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠️ Please enter candidate name", "Validation Error", JOptionPane.WARNING_MESSAGE);
            nameField.requestFocus();
            return false;
        }
        
        if (partyCombo.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "⚠️ Please select a party", "Validation Error", JOptionPane.WARNING_MESSAGE);
            partyCombo.requestFocus();
            return false;
        }
        
        if (positionCombo.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "⚠️ Please select a position", "Validation Error", JOptionPane.WARNING_MESSAGE);
            positionCombo.requestFocus();
            return false;
        }
        
        String position = (String) positionCombo.getSelectedItem();
        if ("🏛️ FPTP".equals(position)) {
            if (provinceCombo.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(this, "⚠️ Please select a province for FPTP candidate", "Validation Error", JOptionPane.WARNING_MESSAGE);
                provinceCombo.requestFocus();
                return false;
            }
            if (districtCombo.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(this, "⚠️ Please select a district for FPTP candidate", "Validation Error", JOptionPane.WARNING_MESSAGE);
                districtCombo.requestFocus();
                return false;
            }
            if (constituencyCombo.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(this, "⚠️ Please select a constituency for FPTP candidate", "Validation Error", JOptionPane.WARNING_MESSAGE);
                constituencyCombo.requestFocus();
                return false;
            }
        }
        
        return true;
    }
    
    private int getSelectedPartyId() {
        String selectedParty = (String) partyCombo.getSelectedItem();
        for (Map.Entry<Integer, String> entry : parties.entrySet()) {
            if (entry.getValue().equals(selectedParty)) {
                return entry.getKey();
            }
        }
        return -1;
    }
    
    private int getSelectedProvinceId() {
        String selectedProvince = (String) provinceCombo.getSelectedItem();
        for (Map.Entry<Integer, String> entry : provinces.entrySet()) {
            if (entry.getValue().equals(selectedProvince)) {
                return entry.getKey();
            }
        }
        return -1;
    }
    
    private int getSelectedDistrictId() {
        String selectedDistrict = (String) districtCombo.getSelectedItem();
        for (Map.Entry<Integer, String> entry : districts.entrySet()) {
            if (entry.getValue().equals(selectedDistrict)) {
                return entry.getKey();
            }
        }
        return -1;
    }
    
    private int getSelectedConstituencyId() {
        String selectedConstituency = (String) constituencyCombo.getSelectedItem();
        for (Map.Entry<Integer, String> entry : constituencies.entrySet()) {
            if (entry.getValue().equals(selectedConstituency)) {
                return entry.getKey();
            }
        }
        return -1;
    }
    
    private void clearForm() {
        nameField.setText("");
        partyCombo.setSelectedIndex(0);
        positionCombo.setSelectedIndex(0);
        provinceCombo.setSelectedIndex(0);
        districtCombo.removeAllItems();
        districtCombo.addItem("Select District");
        constituencyCombo.removeAllItems();
        constituencyCombo.addItem("Select Constituency");
        bioField.setText("");
        photoLabel.setIcon(null);
        photoLabel.setText("📷");
        photoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        photoPath = null;
        selectedCandidateId = -1;
        candidatesTable.clearSelection();
        
        // Enable add, disable update/delete
        addBtn.setEnabled(true);
        updateBtn.setEnabled(false);
        deleteBtn.setEnabled(false);
    }
}