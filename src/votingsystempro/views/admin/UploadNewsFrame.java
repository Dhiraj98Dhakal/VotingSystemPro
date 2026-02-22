package votingsystempro.views.admin;

import votingsystempro.controllers.NewsController;
import votingsystempro.models.News;
import votingsystempro.utils.ImageUtil;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UploadNewsFrame extends JFrame {
    private NewsController newsController;
    
    private JTable newsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField, titleField, summaryField;
    private JTextArea contentArea;
    private JCheckBox featuredCheckBox;
    private JLabel imageLabel;
    private JButton uploadImageBtn, addBtn, updateBtn, deleteBtn, refreshBtn, backBtn, clearBtn;
    private JLabel totalNewsLabel, featuredNewsLabel, totalCommentsLabel, totalLikesLabel;
    
    private String imagePath;
    private int adminUserId;
    private String adminName;
    private int selectedNewsId = -1;
    
    // Modern color scheme (same as Login/Register)
    private final Color GRADIENT_START = new Color(30, 60, 114); // #1e3c72
    private final Color GRADIENT_END = new Color(42, 82, 152); // #2a5298
    private final Color PRIMARY_BLUE = new Color(41, 128, 185); // #2980B9
    private final Color SUCCESS_GREEN = new Color(39, 174, 96); // #27AE60
    private final Color WARNING_YELLOW = new Color(241, 196, 15);
    private final Color DANGER_RED = new Color(231, 76, 60);
    private final Color LIGHT_GREY = new Color(213, 216, 220); // #D5D8DC
    
    public UploadNewsFrame(int adminUserId, String adminName) {
        this.newsController = new NewsController();
        this.adminUserId = adminUserId;
        this.adminName = adminName;
        
        initComponents();
        loadNewsList();
        loadStatistics();
    }
    
    private void initComponents() {
        setTitle("News Management - Election Commission of Nepal");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(1400, 850);
        setMinimumSize(new Dimension(1300, 750));
        setLocationRelativeTo(null);
        
        // Top Panel - Gradient Header
        JPanel topPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int w = getWidth(), h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, GRADIENT_START, w, 0, GRADIENT_END);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
                
                g2d.setColor(new Color(255, 255, 255, 20));
                for (int i = 0; i < w; i += 30) {
                    g2d.drawLine(i, 0, i, h);
                }
            }
        };
        topPanel.setPreferredSize(new Dimension(1400, 100));
        topPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("📰 NEWS MANAGEMENT", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        
        JLabel subTitleLabel = new JLabel("Create, Edit and Manage News Articles", SwingConstants.CENTER);
        subTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subTitleLabel.setForeground(new Color(255, 255, 255, 220));
        topPanel.add(subTitleLabel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);
        
        // ==================== STATISTICS PANEL ====================
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setBackground(new Color(44, 62, 80));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        
        totalNewsLabel = createModernStatCard("📰 Total News", "0", PRIMARY_BLUE);
        featuredNewsLabel = createModernStatCard("⭐ Featured", "0", WARNING_YELLOW);
        totalCommentsLabel = createModernStatCard("💬 Comments", "0", SUCCESS_GREEN);
        totalLikesLabel = createModernStatCard("👍 Likes", "0", new Color(155, 89, 182));
        
        statsPanel.add(totalNewsLabel);
        statsPanel.add(featuredNewsLabel);
        statsPanel.add(totalCommentsLabel);
        statsPanel.add(totalLikesLabel);
        
        add(statsPanel, BorderLayout.NORTH);
        
        // ==================== CENTER PANEL - SPLIT PANE ====================
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(600);
        splitPane.setDividerSize(5);
        splitPane.setBorder(null);
        
        // ==================== LEFT PANEL - NEWS TABLE ====================
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(new Color(245, 245, 250));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 10));
        
        // Search Panel
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(LIGHT_GREY, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        searchPanel.add(searchIcon, gbc);
        
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GREY, 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        searchField.addActionListener(e -> searchNews());
        searchPanel.add(searchField, gbc);
        
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0;
        refreshBtn = createModernButton("🔄 Refresh", PRIMARY_BLUE);
        refreshBtn.setPreferredSize(new Dimension(100, 40));
        refreshBtn.addActionListener(e -> {
            loadNewsList();
            loadStatistics();
        });
        searchPanel.add(refreshBtn, gbc);
        
        leftPanel.add(searchPanel, BorderLayout.NORTH);
        
        // News Table - Professional styling
        String[] columns = {"ID", "Title", "Featured", "Views", "Likes", "Comments", "Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        newsTable = new JTable(tableModel);
        newsTable.setRowHeight(40);
        newsTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        newsTable.setForeground(new Color(50, 50, 50));
        newsTable.setBackground(Color.WHITE);
        newsTable.setSelectionBackground(new Color(173, 216, 230));
        newsTable.setSelectionForeground(Color.BLACK);
        newsTable.setGridColor(LIGHT_GREY);
        newsTable.setShowGrid(true);
        
        // Center align text
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setForeground(new Color(50, 50, 50));
        for (int i = 0; i < newsTable.getColumnCount(); i++) {
            newsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Table Header - BLACK TEXT (Fixed)
        JTableHeader header = newsTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(240, 240, 240));
        header.setForeground(Color.BLACK); // Black text color
        header.setPreferredSize(new Dimension(0, 45));
        
        // Set column widths
        newsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        newsTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        newsTable.getColumnModel().getColumn(2).setPreferredWidth(70);
        newsTable.getColumnModel().getColumn(3).setPreferredWidth(70);
        newsTable.getColumnModel().getColumn(4).setPreferredWidth(70);
        newsTable.getColumnModel().getColumn(5).setPreferredWidth(70);
        newsTable.getColumnModel().getColumn(6).setPreferredWidth(120);
        
        newsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedNews();
            }
        });
        
        JScrollPane tableScroll = new JScrollPane(newsTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(LIGHT_GREY, 1));
        tableScroll.getViewport().setBackground(Color.WHITE);
        
        leftPanel.add(tableScroll, BorderLayout.CENTER);
        
        splitPane.setLeftComponent(leftPanel);
        
        // ==================== RIGHT PANEL - NEWS FORM ====================
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(new Color(245, 245, 250));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 15));
        
        // Form Panel
        JPanel formPanel = createModernNewsForm();
        rightPanel.add(formPanel, BorderLayout.CENTER);
        
        // Action Buttons Panel
        JPanel actionPanel = new JPanel(new GridBagLayout());
        actionPanel.setBackground(Color.WHITE);
        actionPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(LIGHT_GREY, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(5, 5, 5, 5);
        gbc2.fill = GridBagConstraints.HORIZONTAL;
        gbc2.gridwidth = 1;
        
        addBtn = createModernButton("➕ ADD NEWS", SUCCESS_GREEN);
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addBtn.addActionListener(e -> addNews());
        
        updateBtn = createModernButton("✏️ UPDATE", PRIMARY_BLUE);
        updateBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        updateBtn.addActionListener(e -> updateNews());
        updateBtn.setEnabled(false);
        
        deleteBtn = createModernButton("🗑️ DELETE", DANGER_RED);
        deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        deleteBtn.addActionListener(e -> deleteNews());
        deleteBtn.setEnabled(false);
        
        clearBtn = createModernButton("🔄 CLEAR", new Color(100, 100, 100));
        clearBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        clearBtn.addActionListener(e -> clearForm());
        
        backBtn = createModernButton("◀ BACK", new Color(52, 73, 94));
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backBtn.addActionListener(e -> dispose());
        
        gbc2.gridx = 0; gbc2.gridy = 0; actionPanel.add(addBtn, gbc2);
        gbc2.gridx = 1; gbc2.gridy = 0; actionPanel.add(updateBtn, gbc2);
        gbc2.gridx = 0; gbc2.gridy = 1; actionPanel.add(deleteBtn, gbc2);
        gbc2.gridx = 1; gbc2.gridy = 1; actionPanel.add(clearBtn, gbc2);
        gbc2.gridx = 0; gbc2.gridy = 2; gbc2.gridwidth = 2; actionPanel.add(backBtn, gbc2);
        
        rightPanel.add(actionPanel, BorderLayout.SOUTH);
        
        splitPane.setRightComponent(rightPanel);
        
        add(splitPane, BorderLayout.CENTER);
        
        // Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(new Color(52, 73, 94));
        footerPanel.setPreferredSize(new Dimension(1400, 30));
        
        JLabel footerLabel = new JLabel("Election Commission of Nepal");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerLabel.setForeground(Color.WHITE);
        footerPanel.add(footerLabel);
        
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private JLabel createModernStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBackground(color);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 2),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        card.setPreferredSize(new Dimension(200, 65));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valueLabel.setForeground(Color.WHITE);
        
        card.add(titleLabel, BorderLayout.WEST);
        card.add(valueLabel, BorderLayout.EAST);
        
        JLabel wrapper = new JLabel();
        wrapper.setLayout(new BorderLayout());
        wrapper.add(card, BorderLayout.CENTER);
        
        return wrapper;
    }
    
    private JButton createModernButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
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
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
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
        button.setPreferredSize(new Dimension(120, 40));
        
        return button;
    }
    
    private JPanel createModernNewsForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder(new LineBorder(PRIMARY_BLUE, 2), "News Details", 
                TitledBorder.LEFT, TitledBorder.TOP, 
                new Font("Segoe UI", Font.BOLD, 14), PRIMARY_BLUE),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Image Upload Section
        JPanel imagePanel = new JPanel(new BorderLayout(10, 10));
        imagePanel.setBackground(Color.WHITE);
        imagePanel.setBorder(BorderFactory.createLineBorder(LIGHT_GREY, 1));
        imagePanel.setPreferredSize(new Dimension(250, 160));
        
        imageLabel = new JLabel("📰", SwingConstants.CENTER);
        imageLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        imageLabel.setForeground(new Color(150, 150, 150));
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        panel.add(imagePanel, gbc);
        row++;
        
        gbc.gridy = row;
        uploadImageBtn = createModernButton("📤 Upload Image", PRIMARY_BLUE);
        uploadImageBtn.setPreferredSize(new Dimension(250, 35));
        uploadImageBtn.addActionListener(e -> uploadImage());
        panel.add(uploadImageBtn, gbc);
        row++;
        
        // Title Field - LARGER
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = row;
        JLabel titleLabel = new JLabel("Title:");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(60, 60, 60));
        panel.add(titleLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = row;
        titleField = new JTextField();
        titleField.setPreferredSize(new Dimension(380, 45)); // Increased height
        titleField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        titleField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GREY, 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        panel.add(titleField, gbc);
        row++;
        
        // Summary Field - LARGER
        gbc.gridx = 0; gbc.gridy = row;
        JLabel summaryLabel = new JLabel("Summary:");
        summaryLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        summaryLabel.setForeground(new Color(60, 60, 60));
        panel.add(summaryLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = row;
        summaryField = new JTextField();
        summaryField.setPreferredSize(new Dimension(380, 45)); // Increased height
        summaryField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        summaryField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GREY, 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        panel.add(summaryField, gbc);
        row++;
        
        // Content Area - MUCH LARGER
        gbc.gridx = 0; gbc.gridy = row;
        JLabel contentLabel = new JLabel("Content:");
        contentLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        contentLabel.setForeground(new Color(60, 60, 60));
        panel.add(contentLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = row;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        
        contentArea = new JTextArea(8, 30); // More rows
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setFont(new Font("Segoe UI", Font.PLAIN, 15)); // Larger font
        contentArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GREY, 1),
            BorderFactory.createEmptyBorder(12, 15, 12, 15) // More padding
        ));
        
        JScrollPane contentScroll = new JScrollPane(contentArea);
        contentScroll.setPreferredSize(new Dimension(380, 200)); // Much taller
        panel.add(contentScroll, gbc);
        row++;
        
        // Featured Checkbox
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;
        gbc.weighty = 0;
        
        featuredCheckBox = new JCheckBox("Mark as Featured News");
        featuredCheckBox.setBackground(Color.WHITE);
        featuredCheckBox.setFont(new Font("Segoe UI", Font.BOLD, 13));
        featuredCheckBox.setForeground(PRIMARY_BLUE);
        panel.add(featuredCheckBox, gbc);
        row++;
        
        return panel;
    }
    
    private void loadStatistics() {
        List<News> allNews = newsController.getAllNews();
        
        int totalNews = allNews.size();
        int featuredCount = 0;
        int totalComments = 0;
        int totalLikes = 0;
        
        for (News news : allNews) {
            if (news.isFeatured()) featuredCount++;
            totalComments += news.getCommentsCount();
            totalLikes += news.getLikesCount();
        }
        
        Component[] components = ((JPanel) getContentPane().getComponent(1)).getComponents();
        int index = 0;
        for (Component comp : components) {
            if (comp instanceof JLabel) {
                JLabel wrapper = (JLabel) comp;
                JPanel card = (JPanel) wrapper.getComponent(0);
                JLabel valueLabel = (JLabel) card.getComponent(1);
                
                if (index == 0) valueLabel.setText(String.valueOf(totalNews));
                else if (index == 1) valueLabel.setText(String.valueOf(featuredCount));
                else if (index == 2) valueLabel.setText(String.valueOf(totalComments));
                else if (index == 3) valueLabel.setText(String.valueOf(totalLikes));
                index++;
            }
        }
    }
    
    private void loadNewsList() {
        tableModel.setRowCount(0);
        List<News> newsList = newsController.getAllNews();
        
        for (News news : newsList) {
            tableModel.addRow(new Object[]{
                news.getNewsId(),
                news.getTitle(),
                news.isFeatured() ? "⭐ Yes" : "No",
                news.getViews(),
                news.getLikesCount(),
                news.getCommentsCount(),
                new SimpleDateFormat("dd MMM yyyy").format(news.getPublishedDate())
            });
        }
    }
    
    private void searchNews() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadNewsList();
            return;
        }
        
        tableModel.setRowCount(0);
        List<News> newsList = newsController.searchNews(keyword);
        
        for (News news : newsList) {
            tableModel.addRow(new Object[]{
                news.getNewsId(),
                news.getTitle(),
                news.isFeatured() ? "⭐ Yes" : "No",
                news.getViews(),
                news.getLikesCount(),
                news.getCommentsCount(),
                new SimpleDateFormat("dd MMM yyyy").format(news.getPublishedDate())
            });
        }
    }
    
    private void loadSelectedNews() {
        int selectedRow = newsTable.getSelectedRow();
        if (selectedRow < 0) return;
        
        selectedNewsId = (int) tableModel.getValueAt(selectedRow, 0);
        News news = newsController.getNewsById(selectedNewsId);
        
        if (news != null) {
            titleField.setText(news.getTitle());
            summaryField.setText(news.getSummary());
            contentArea.setText(news.getContent());
            featuredCheckBox.setSelected(news.isFeatured());
            
            if (news.getImagePath() != null && !news.getImagePath().isEmpty()) {
                ImageIcon icon = ImageUtil.createImageIcon(news.getImagePath(), 220, 140);
                if (icon != null) {
                    imageLabel.setIcon(icon);
                    imageLabel.setText("");
                    imagePath = news.getImagePath();
                }
            }
            
            updateBtn.setEnabled(true);
            deleteBtn.setEnabled(true);
        }
    }
    
    private void uploadImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select News Image");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Image files (*.jpg, *.jpeg, *.png, *.gif)", "jpg", "jpeg", "png", "gif"));
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home") + "/Pictures"));
        
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
            uploadImageBtn.setEnabled(false);
            
            SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() throws Exception {
                    return ImageUtil.saveImage(selectedFile, "news_images");
                }
                
                @Override
                protected void done() {
                    try {
                        imagePath = get();
                        if (imagePath != null) {
                            ImageIcon icon = ImageUtil.createImageIcon(imagePath, 220, 140);
                            if (icon != null) {
                                imageLabel.setIcon(icon);
                                imageLabel.setText("");
                                JOptionPane.showMessageDialog(UploadNewsFrame.this,
                                    "✅ Image uploaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(UploadNewsFrame.this,
                            "❌ Error uploading image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        setCursor(Cursor.getDefaultCursor());
                        uploadImageBtn.setEnabled(true);
                    }
                }
            };
            worker.execute();
        }
    }
    
    private void addNews() {
        if (!validateInputs()) return;
        
        News news = new News();
        news.setTitle(titleField.getText().trim());
        news.setSummary(summaryField.getText().trim());
        news.setContent(contentArea.getText().trim());
        news.setImagePath(imagePath);
        news.setAuthorId(adminUserId);
        news.setAuthorName(adminName);
        news.setFeatured(featuredCheckBox.isSelected());
        news.setPublishedDate(new Date());
        
        if (newsController.addNews(news)) {
            JOptionPane.showMessageDialog(this, "✅ News published successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadNewsList();
            loadStatistics();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Error publishing news.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateNews() {
        if (selectedNewsId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a news to update", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!validateInputs()) return;
        
        News news = new News();
        news.setNewsId(selectedNewsId);
        news.setTitle(titleField.getText().trim());
        news.setSummary(summaryField.getText().trim());
        news.setContent(contentArea.getText().trim());
        news.setImagePath(imagePath);
        news.setFeatured(featuredCheckBox.isSelected());
        
        if (newsController.updateNews(news)) {
            JOptionPane.showMessageDialog(this, "✅ News updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadNewsList();
            loadStatistics();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Error updating news.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteNews() {
        if (selectedNewsId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a news to delete", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this news?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (newsController.deleteNews(selectedNewsId)) {
                JOptionPane.showMessageDialog(this, "✅ News deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadNewsList();
                loadStatistics();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Error deleting news.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private boolean validateInputs() {
        if (titleField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter news title", "Validation Error", JOptionPane.WARNING_MESSAGE);
            titleField.requestFocus();
            return false;
        }
        
        if (contentArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter news content", "Validation Error", JOptionPane.WARNING_MESSAGE);
            contentArea.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void clearForm() {
        titleField.setText("");
        summaryField.setText("");
        contentArea.setText("");
        featuredCheckBox.setSelected(false);
        imageLabel.setIcon(null);
        imageLabel.setText("📰");
        imagePath = null;
        selectedNewsId = -1;
        updateBtn.setEnabled(false);
        deleteBtn.setEnabled(false);
        newsTable.clearSelection();
    }
}