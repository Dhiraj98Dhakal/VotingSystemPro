package votingsystempro.views;

import votingsystempro.controllers.PartyController;
import votingsystempro.models.Party;
import votingsystempro.utils.ImageUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ManagePartiesFrame extends JFrame {
    private PartyController partyController;
    
    private JTable partiesTable;
    private DefaultTableModel tableModel;
    private JTextField searchField, nameField;
    private JTextArea descArea;
    private JFormattedTextField estDateField;
    private JButton uploadLogoBtn, addBtn, updateBtn, deleteBtn, refreshBtn, backBtn, clearBtn;
    private JLabel logoLabel;
    private String logoPath;
    private int selectedPartyId = -1; // Track selected party ID
    
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
    
    public ManagePartiesFrame() {
        partyController = new PartyController();
        initComponents();
        loadParties();
        applyModernEffects();
    }
    
    private void applyModernEffects() {
        setUndecorated(true);
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
        setTitle("Manage Political Parties - Election Commission of Nepal");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setUndecorated(true);
        
        // Custom title bar
        JPanel titleBar = createTitleBar();
        add(titleBar, BorderLayout.NORTH);
        
        // Main Content
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(600);
        splitPane.setBorder(null);
        splitPane.setBackground(CONTENT_BG);
        
        // Left Panel - Parties Table
        JPanel leftPanel = createLeftPanel();
        splitPane.setLeftComponent(leftPanel);
        
        // Right Panel - Party Form
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
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        leftPanel.setOpaque(false);
        
        JLabel logoLabel = new JLabel("🎯");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        logoLabel.setForeground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Party Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        
        leftPanel.add(logoLabel);
        leftPanel.add(titleLabel);
        
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        centerPanel.setOpaque(false);
        
        JLabel countIcon = new JLabel("📊");
        countIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        countIcon.setForeground(Color.WHITE);
        
        JLabel countLabel = new JLabel("Total Parties: Loading...");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        countLabel.setForeground(Color.WHITE);
        countLabel.setName("countLabel");
        
        centerPanel.add(countIcon);
        centerPanel.add(countLabel);
        
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
        
        JLabel headerLabel = new JLabel("🎯 Registered Political Parties");
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
        searchField.addActionListener(e -> searchParties());
        searchPanel.add(searchField, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0.2;
        refreshBtn = createStyledButton("🔄 Refresh", BLUE, -1, 35);
        refreshBtn.addActionListener(e -> loadParties());
        searchPanel.add(refreshBtn, gbc);
        
        panel.add(searchPanel, BorderLayout.CENTER);
        
        // Parties Table
        String[] columns = {"ID", "Party Name", "Established", "Description", "Candidates"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class;
                if (columnIndex == 4) return Integer.class;
                return String.class;
            }
        };
        
        partiesTable = new JTable(tableModel);
        partiesTable.setRowHeight(45);
        partiesTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        partiesTable.setShowGrid(true);
        partiesTable.setGridColor(CARD_BORDER);
        partiesTable.setSelectionBackground(new Color(173, 216, 230, 50));
        partiesTable.setSelectionForeground(TEXT_PRIMARY);
        
        JTableHeader header = partiesTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(240, 240, 240));
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(0, 40));
        
        partiesTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        partiesTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        partiesTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        partiesTable.getColumnModel().getColumn(3).setPreferredWidth(250);
        partiesTable.getColumnModel().getColumn(4).setPreferredWidth(70);
        
        partiesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showPartyDetails();
            }
        });
        
        JScrollPane tableScroll = new JScrollPane(partiesTable);
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
        
        JLabel headerLabel = new JLabel("📋 Party Information");
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
        
        addBtn = createStyledButton("➕ ADD PARTY", GREEN, -1, 45);
        addBtn.addActionListener(e -> addParty());
        
        updateBtn = createStyledButton("✏️ UPDATE", BLUE, -1, 45);
        updateBtn.addActionListener(e -> updateParty());
        
        deleteBtn = createStyledButton("🗑️ DELETE", RED, -1, 45);
        deleteBtn.addActionListener(e -> deleteParty());
        
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
        
        JLabel versionLabel = new JLabel("🎯 Party Management | Election Commission of Nepal");
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
        gbc.insets = new Insets(12, 15, 12, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Logo Upload Section
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        
        JPanel logoContainer = new JPanel(new BorderLayout());
        logoContainer.setBackground(Color.WHITE);
        logoContainer.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 2));
        logoContainer.setPreferredSize(new Dimension(180, 180));
        
        logoLabel = new JLabel("🏛️", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        logoLabel.setForeground(TEXT_SECONDARY);
        logoLabel.setBackground(Color.WHITE);
        logoLabel.setOpaque(true);
        logoContainer.add(logoLabel, BorderLayout.CENTER);
        
        panel.add(logoContainer, gbc);
        row++;
        
        gbc.gridy = row;
        gbc.insets = new Insets(5, 15, 15, 15);
        uploadLogoBtn = createStyledButton("📁 Upload Logo", PURPLE, -1, 35);
        uploadLogoBtn.addActionListener(e -> uploadLogo());
        panel.add(uploadLogoBtn, gbc);
        row++;
        
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Party Name
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel nameLabel = new JLabel("Party Name:");
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
        
        // Established Date
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel dateLabel = new JLabel("Established:");
        dateLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        dateLabel.setForeground(TEXT_PRIMARY);
        panel.add(dateLabel, gbc);
        
        gbc.gridx = 1;
        estDateField = new JFormattedTextField(new SimpleDateFormat("yyyy-MM-dd"));
        estDateField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        estDateField.setPreferredSize(new Dimension(250, 35));
        estDateField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        estDateField.setValue(new Date());
        panel.add(estDateField, gbc);
        row++;
        
        // Description
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        descLabel.setForeground(TEXT_PRIMARY);
        panel.add(descLabel, gbc);
        
        gbc.gridx = 1;
        descArea = new JTextArea(5, 20);
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setPreferredSize(new Dimension(250, 100));
        descScroll.setBorder(null);
        
        panel.add(descScroll, gbc);
        row++;
        
        // Add some extra space at bottom
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 0.1;
        panel.add(Box.createVerticalGlue(), gbc);
        
        return panel;
    }
    
    private void loadParties() {
        tableModel.setRowCount(0);
        List<Party> parties = partyController.getAllParties();
        
        if (parties != null && !parties.isEmpty()) {
            for (Party party : parties) {
                int candidateCount = partyController.getTotalVotesForParty(party.getPartyId());
                tableModel.addRow(new Object[]{
                    party.getPartyId(),
                    party.getPartyName(),
                    party.getFormattedEstablishedDate() != null ? party.getFormattedEstablishedDate() : "N/A",
                    party.getDescription() != null ? party.getDescription() : "-",
                    candidateCount
                });
            }
            
            // Update title bar count
            updateTitleBarCount(parties.size());
        }
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
                                if (comp instanceof JLabel && ((JLabel) comp).getText().startsWith("Total Parties:")) {
                                    ((JLabel) comp).setText("Total Parties: " + total);
                                    break;
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
    
    private void searchParties() {
        String searchText = searchField.getText().toLowerCase().trim();
        if (searchText.isEmpty()) {
            loadParties();
            return;
        }
        
        tableModel.setRowCount(0);
        List<Party> parties = partyController.getAllParties();
        
        if (parties != null) {
            for (Party party : parties) {
                if (party.getPartyName().toLowerCase().contains(searchText)) {
                    int candidateCount = partyController.getTotalVotesForParty(party.getPartyId());
                    tableModel.addRow(new Object[]{
                        party.getPartyId(),
                        party.getPartyName(),
                        party.getFormattedEstablishedDate() != null ? party.getFormattedEstablishedDate() : "N/A",
                        party.getDescription() != null ? party.getDescription() : "-",
                        candidateCount
                    });
                }
            }
        }
    }
    
    private void showPartyDetails() {
        int selectedRow = partiesTable.getSelectedRow();
        if (selectedRow < 0) return;
        
        selectedPartyId = (int) tableModel.getValueAt(selectedRow, 0);
        Party party = partyController.getPartyById(selectedPartyId);
        
        if (party != null) {
            nameField.setText(party.getPartyName());
            descArea.setText(party.getDescription() != null ? party.getDescription() : "");
            if (party.getEstablishedDate() != null) {
                estDateField.setValue(party.getEstablishedDate());
            } else {
                estDateField.setValue(new Date());
            }
            
            // Load logo
            if (party.getPartyLogoPath() != null && !party.getPartyLogoPath().isEmpty()) {
                ImageIcon icon = ImageUtil.createImageIcon(party.getPartyLogoPath(), 170, 170);
                if (icon != null) {
                    logoLabel.setIcon(icon);
                    logoLabel.setText("");
                    logoPath = party.getPartyLogoPath();
                } else {
                    logoLabel.setIcon(null);
                    logoLabel.setText("🏛️");
                    logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
                    logoPath = null;
                }
            } else {
                logoLabel.setIcon(null);
                logoLabel.setText("🏛️");
                logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
                logoPath = null;
            }
            
            // Enable update/delete, disable add
            addBtn.setEnabled(false);
            updateBtn.setEnabled(true);
            deleteBtn.setEnabled(true);
        }
    }
    
    private void uploadLogo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Party Logo");
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
            uploadLogoBtn.setEnabled(false);
            
            SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() throws Exception {
                    return ImageUtil.saveImage(selectedFile, "party_logos");
                }
                
                @Override
                protected void done() {
                    try {
                        logoPath = get();
                        if (logoPath != null) {
                            ImageIcon icon = ImageUtil.createImageIcon(logoPath, 170, 170);
                            if (icon != null) {
                                logoLabel.setIcon(icon);
                                logoLabel.setText("");
                            }
                            JOptionPane.showMessageDialog(ManagePartiesFrame.this,
                                "✅ Logo uploaded successfully!",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(ManagePartiesFrame.this,
                            "❌ Error uploading logo: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        setCursor(Cursor.getDefaultCursor());
                        uploadLogoBtn.setEnabled(true);
                    }
                }
            };
            worker.execute();
        }
    }
    
    private void addParty() {
        if (!validateInputs()) return;
        
        Party party = new Party();
        party.setPartyName(nameField.getText().trim().toUpperCase());
        party.setDescription(descArea.getText().trim());
        party.setEstablishedDate((Date) estDateField.getValue());
        party.setPartyLogoPath(logoPath);
        
        if (partyController.addParty(party)) {
            JOptionPane.showMessageDialog(this,
                "✅ Party added successfully!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadParties();
        } else {
            JOptionPane.showMessageDialog(this,
                "❌ Error adding party. Party name may already exist.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateParty() {
        if (selectedPartyId == -1) {
            JOptionPane.showMessageDialog(this,
                "⚠️ Please select a party to update",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!validateInputs()) return;
        
        Party party = partyController.getPartyById(selectedPartyId);
        
        if (party != null) {
            party.setPartyName(nameField.getText().trim().toUpperCase());
            party.setDescription(descArea.getText().trim());
            party.setEstablishedDate((Date) estDateField.getValue());
            if (logoPath != null && !logoPath.isEmpty()) {
                party.setPartyLogoPath(logoPath);
            }
            
            if (partyController.updateParty(party)) {
                JOptionPane.showMessageDialog(this,
                    "✅ Party updated successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadParties();
            } else {
                JOptionPane.showMessageDialog(this,
                    "❌ Error updating party",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void deleteParty() {
        if (selectedPartyId == -1) {
            JOptionPane.showMessageDialog(this,
                "⚠️ Please select a party to delete",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String partyName = nameField.getText().trim();
        if (partyName.isEmpty()) {
            partyName = "this party";
        }
        
        int candidateCount = 0;
        if (partiesTable.getSelectedRow() >= 0) {
            candidateCount = (int) tableModel.getValueAt(partiesTable.getSelectedRow(), 4);
        }
        
        String message = "Are you sure you want to delete " + partyName + "?";
        if (candidateCount > 0) {
            message += "\n\n⚠️ This party has " + candidateCount + " candidate(s).\nThey will also be deleted!";
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            message,
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (partyController.deleteParty(selectedPartyId)) {
                JOptionPane.showMessageDialog(this,
                    "✅ Party deleted successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadParties();
                selectedPartyId = -1;
            } else {
                JOptionPane.showMessageDialog(this,
                    "❌ Error deleting party",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private boolean validateInputs() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "⚠️ Please enter party name",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            nameField.requestFocus();
            return false;
        }
        return true;
    }
    
    private void clearForm() {
        nameField.setText("");
        descArea.setText("");
        estDateField.setValue(new Date());
        logoLabel.setIcon(null);
        logoLabel.setText("🏛️");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        logoPath = null;
        selectedPartyId = -1;
        partiesTable.clearSelection();
        
        // Enable add, disable update/delete
        addBtn.setEnabled(true);
        updateBtn.setEnabled(false);
        deleteBtn.setEnabled(false);
    }
}