import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class DashboardFrame extends JFrame {
    private final Account account;
    private JLabel balanceLabel, welcomeLabel, totalBalanceValueLabel;
    private JTextArea historyArea;
    private JPanel contentPanel, sidePanel;
    private JPanel cardsPanel;

    // --- Field to track the active menu button ---
    private JPanel activeMenuButton;

    // --- 1. A CENTRALIZED THEME ---
    static class Theme {
        // Primary Palette
        static final Color COLOR_PRIMARY_DARK = new Color(25, 42, 86);
        static final Color COLOR_PRIMARY = new Color(33, 60, 115);
        static final Color COLOR_PRIMARY_LIGHT = new Color(41, 98, 174);
        static final Color COLOR_ACCENT = new Color(25, 118, 210);
        static final Color COLOR_MENU_ACTIVE = new Color(25, 118, 210);

        // UI Colors
        static final Color COLOR_BACKGROUND = new Color(245, 247, 250);
        static final Color COLOR_WHITE = Color.WHITE;
        static final Color COLOR_CARD_BACKGROUND = Color.WHITE;
        static final Color COLOR_BORDER = new Color(224, 224, 224);

        // Text Colors
        static final Color COLOR_TEXT_PRIMARY = new Color(33, 33, 33);
        static final Color COLOR_TEXT_SECONDARY = new Color(117, 117, 117);
        static final Color COLOR_TEXT_ON_PRIMARY = Color.WHITE;

        // Action Colors
        static final Color COLOR_SUCCESS = new Color(67, 160, 71);
        static final Color COLOR_WARNING = new Color(251, 140, 0);

        // Fonts
        static final Font FONT_HEADING = new Font("Segoe UI Semibold", Font.BOLD, 24);
        static final Font FONT_SUBHEADING = new Font("Segoe UI", Font.BOLD, 20);
        static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 14);
        static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
        static final Font FONT_CODE = new Font("Consolas", Font.PLAIN, 14);
        static final Font FONT_ICON = new Font("Segoe UI Emoji", Font.PLAIN, 18);
    }

    public DashboardFrame(Account account) {
        this.account = account;
        setTitle("🏦 SwiftBank - Dashboard");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // ---------- LEFT MENU PANEL ----------
        sidePanel = new JPanel();
        sidePanel.setBackground(Theme.COLOR_PRIMARY);
        sidePanel.setPreferredSize(new Dimension(240, getHeight()));
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel bankIcon;
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/bank.png"));
            Image scaled = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            bankIcon = new JLabel(new ImageIcon(scaled), SwingConstants.CENTER);
        } catch (Exception e) {
            bankIcon = new JLabel("\uD83C\uDFE6", SwingConstants.CENTER);
            bankIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 50));
            bankIcon.setForeground(Theme.COLOR_TEXT_ON_PRIMARY);
        }
        bankIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel bankName = new JLabel("SwiftBank Limited", SwingConstants.CENTER);
        bankName.setFont(Theme.FONT_SUBHEADING);
        bankName.setForeground(Theme.COLOR_TEXT_ON_PRIMARY);
        bankName.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidePanel.add(bankIcon);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidePanel.add(bankName);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // --- Store the 'Home' button to set it as active ---
        JPanel homeButtonPanel = addMenuButton("🏠  Home", e -> showHomePanel());
        addMenuButton("👤  Profile", e -> showProfilePanel());
        addMenuButton("💰  Account Details", e -> showAccountDetails());
        addMenuButton("📈  Transactions", e -> showTransactionPanel());
        addMenuButton("🧾  Reports", e -> showReportsPanel());
        addMenuButton("🔒  Change Password", e -> showChangePasswordPanel());

        // --- Fill the empty space ---
        sidePanel.add(Box.createVerticalGlue()); // Pushes everything below it to the bottom
        sidePanel.add(createHelpPanel()); // Add our new help card
        sidePanel.add(Box.createRigidArea(new Dimension(0, 10))); // Small spacer

        // Add logout button at the very bottom
        addMenuButton("🚪  Logout", e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        // ---------- MAIN CONTENT ----------
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Theme.COLOR_BACKGROUND);

        // --- Set the 'Home' button as active by default ---
        showHomePanel();
        setActiveMenuButton(homeButtonPanel); // Set the active state

        add(sidePanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    /**
     * --- This method now returns the JPanel it creates ---
     */
    private JPanel addMenuButton(String text, ActionListener action) {
        JPanel menuPanel = new JPanel(new BorderLayout());
        menuPanel.setBackground(Theme.COLOR_PRIMARY);
        menuPanel.setMaximumSize(new Dimension(240, 50));
        menuPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel label = new JLabel(text);
        label.setFont(Theme.FONT_BODY_BOLD);
        label.setForeground(Theme.COLOR_TEXT_ON_PRIMARY);
        label.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));

        menuPanel.add(label, BorderLayout.CENTER);

        // --- Mouse listener for hover effects and click action ---
        menuPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (menuPanel != activeMenuButton) {
                    menuPanel.setBackground(Theme.COLOR_PRIMARY_LIGHT);
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (menuPanel != activeMenuButton) {
                    menuPanel.setBackground(Theme.COLOR_PRIMARY);
                }
            }
            @Override
            public void mousePressed(MouseEvent e) {
                menuPanel.setBackground(Theme.COLOR_MENU_ACTIVE.darker());
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                setActiveMenuButton(menuPanel);
                action.actionPerformed(null); // Fire the action
            }
        });

        sidePanel.add(menuPanel);
        return menuPanel; // Return the panel
    }

    /**
     * --- Method to create the "Need Help?" card ---
     */
    private JPanel createHelpPanel() {
        RoundedPanel helpPanel = new RoundedPanel(15);
        helpPanel.setBackground(Theme.COLOR_PRIMARY_LIGHT);
        helpPanel.setLayout(new BoxLayout(helpPanel, BoxLayout.Y_AXIS));
        helpPanel.setMaximumSize(new Dimension(190, 100));
        helpPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel iconLabel = new JLabel("❓");
        iconLabel.setFont(Theme.FONT_ICON.deriveFont(20f));
        iconLabel.setForeground(Theme.COLOR_TEXT_ON_PRIMARY);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Need Help?");
        title.setFont(Theme.FONT_BODY_BOLD);
        title.setForeground(Theme.COLOR_TEXT_ON_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel description = new JLabel("Contact support");
        description.setFont(Theme.FONT_BODY.deriveFont(12f));
        description.setForeground(Theme.COLOR_TEXT_ON_PRIMARY);
        description.setAlignmentX(Component.CENTER_ALIGNMENT);

        helpPanel.add(iconLabel);
        helpPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        helpPanel.add(title);
        helpPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        helpPanel.add(description);

        return helpPanel;
    }

    /**
     * --- Method to manage the active menu button state ---
     */
    private void setActiveMenuButton(JPanel button) {
        if (activeMenuButton != null) {
            activeMenuButton.setBackground(Theme.COLOR_PRIMARY);
        }
        activeMenuButton = button;
        activeMenuButton.setBackground(Theme.COLOR_MENU_ACTIVE);
    }

    // 🏠 HOME PANEL
    private void showHomePanel() {
        contentPanel.removeAll();
        JPanel homePanel = new JPanel(new BorderLayout(20, 20));
        homePanel.setBackground(Theme.COLOR_BACKGROUND);
        homePanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        welcomeLabel = new JLabel("Welcome, " + account.getName() + " 👋");
        welcomeLabel.setFont(Theme.FONT_HEADING);
        welcomeLabel.setForeground(Theme.COLOR_TEXT_PRIMARY);
        balanceLabel = new JLabel("Current Balance: ₹" + account.getBalance());
        balanceLabel.setFont(Theme.FONT_SUBHEADING);
        balanceLabel.setForeground(Theme.COLOR_ACCENT);
        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(balanceLabel, BorderLayout.EAST);

        cardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 10));
        cardsPanel.setOpaque(false);
        updateCards();

        JPanel historyPanel = new RoundedPanel(15);
        historyPanel.setLayout(new BorderLayout(10, 10));
        historyPanel.setBackground(Theme.COLOR_CARD_BACKGROUND);
        historyPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel historyTitle = new JLabel("📜 Recent Transactions");
        historyTitle.setFont(Theme.FONT_BODY_BOLD);
        historyTitle.setForeground(Theme.COLOR_TEXT_PRIMARY);
        historyTitle.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(Theme.FONT_CODE);
        historyArea.setBackground(Theme.COLOR_BACKGROUND);
        historyArea.setForeground(Theme.COLOR_TEXT_SECONDARY);
        historyArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(historyArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.COLOR_BORDER));
        historyPanel.add(historyTitle, BorderLayout.NORTH);
        historyPanel.add(scrollPane, BorderLayout.CENTER);
        updateHistory();

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        actionPanel.setOpaque(false);
        actionPanel.add(createActionButton("💵 Deposit", Theme.COLOR_SUCCESS, e -> deposit()));
        actionPanel.add(createActionButton("💳 Withdraw", Theme.COLOR_WARNING, e -> withdraw()));
        actionPanel.add(createActionButton("➕ Add Account", Theme.COLOR_ACCENT, e -> addAccount()));
        actionPanel.add(createActionButton("📊 View Report", Theme.COLOR_TEXT_SECONDARY, e ->
                JOptionPane.showMessageDialog(this, "📈 Report feature coming soon!", "SwiftBank", JOptionPane.INFORMATION_MESSAGE)
        ));

        homePanel.add(topPanel, BorderLayout.NORTH);
        homePanel.add(cardsPanel, BorderLayout.CENTER);
        JPanel bottomWrapper = new JPanel(new BorderLayout(10, 20));
        bottomWrapper.setOpaque(false);
        bottomWrapper.add(historyPanel, BorderLayout.CENTER);
        bottomWrapper.add(actionPanel, BorderLayout.SOUTH);
        homePanel.add(bottomWrapper, BorderLayout.SOUTH);
        contentPanel.add(homePanel, BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // --- Helper to create styled action buttons ---
    private JButton createActionButton(String text, Color bgColor, ActionListener action) {
        JButton b = new JButton(text);
        b.setFont(Theme.FONT_BODY_BOLD);
        b.setForeground(Color.WHITE);
        b.setBackground(bgColor);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setPreferredSize(new Dimension(160, 40));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(action);
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { b.setBackground(bgColor.darker()); }
            @Override
            public void mouseExited(MouseEvent e) { b.setBackground(bgColor); }
        });
        return b;
    }

    // --- Helper to create/update the info cards ---
    private void updateCards() {
        cardsPanel.removeAll();
        cardsPanel.add(createInfoCard("💰", "Total Balance", "₹" + account.getBalance(), new Color(79, 195, 247)));
        cardsPanel.add(createInfoCard("⬆️", "Deposits", "Track your savings", new Color(102, 187, 106)));
        cardsPanel.add(createInfoCard("⬇️", "Withdrawals", "Monitor expenses", new Color(239, 108, 0)));
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    // 👤 PROFILE PANEL
    private void showProfilePanel() {
        contentPanel.removeAll();
        JPanel profilePanel = new RoundedPanel(15);
        profilePanel.setBackground(Theme.COLOR_CARD_BACKGROUND);
        profilePanel.setBorder(new CompoundBorder(
                BorderFactory.createEmptyBorder(40, 40, 40, 40),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        profilePanel.setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("👤 Profile Information", SwingConstants.CENTER);
        title.setFont(Theme.FONT_HEADING);
        title.setForeground(Theme.COLOR_TEXT_PRIMARY);

        String profileText = String.format(
                "%-15s %s\n\n" +
                        "%-15s %s\n\n" +
                        "%-15s ₹%.2f\n\n" +
                        "%-15s %s",
                "Name:", account.getName(),
                "Account No:", account.getAccountNo(),
                "Balance:", account.getBalance(),
                "Type:", "Saving"
        );

        JTextArea details = new JTextArea(profileText);
        details.setEditable(false);
        details.setFont(Theme.FONT_CODE);
        details.setForeground(Theme.COLOR_TEXT_SECONDARY);
        details.setBackground(Theme.COLOR_BACKGROUND);
        details.setMargin(new Insets(20, 20, 20, 20));

        profilePanel.add(title, BorderLayout.NORTH);
        profilePanel.add(new JScrollPane(details), BorderLayout.CENTER);

        contentPanel.add(profilePanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showAccountDetails() {
        JOptionPane.showMessageDialog(this, "Account Details feature coming soon!");
    }

    private void showTransactionPanel() {
        JOptionPane.showMessageDialog(this, "Transactions module under development!");
    }

    private void showReportsPanel() {
        JOptionPane.showMessageDialog(this, "Reports section coming soon!");
    }

    private void showChangePasswordPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel oldLabel = new JLabel("Old Password:");
        oldLabel.setFont(Theme.FONT_BODY);
        JPasswordField oldPass = new JPasswordField();
        oldPass.setFont(Theme.FONT_BODY);

        JLabel newLabel = new JLabel("New Password:");
        newLabel.setFont(Theme.FONT_BODY);
        JPasswordField newPass = new JPasswordField();
        newPass.setFont(Theme.FONT_BODY);

        panel.add(oldLabel);
        panel.add(oldPass);
        panel.add(newLabel);
        panel.add(newPass);

        int result = JOptionPane.showConfirmDialog(this, panel, "Change Password",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(this, "Password changed successfully (mock-up).");
        }
    }

    // ---------- INFO CARD CREATOR ----------
    private JPanel createInfoCard(String icon, String title, String value, Color iconBgColor) {
        JPanel card = new RoundedPanel(18);
        card.setBackground(Theme.COLOR_CARD_BACKGROUND);
        card.setPreferredSize(new Dimension(250, 120));
        card.setBorder(new CompoundBorder(
                new LineBorder(Theme.COLOR_BORDER, 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));
        card.setLayout(new BorderLayout(15, 0));

        JPanel iconPanel = new RoundedPanel(30);
        iconPanel.setBackground(iconBgColor);
        iconPanel.setPreferredSize(new Dimension(60, 60));
        iconPanel.setLayout(new GridBagLayout());

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        iconLabel.setForeground(Theme.COLOR_WHITE);
        iconPanel.add(iconLabel);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Theme.FONT_BODY_BOLD);
        titleLabel.setForeground(Theme.COLOR_TEXT_SECONDARY);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(Theme.FONT_SUBHEADING);
        valueLabel.setForeground(Theme.COLOR_TEXT_PRIMARY);

        if (title.equals("Total Balance")) totalBalanceValueLabel = valueLabel;

        textPanel.add(Box.createVerticalGlue());
        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(valueLabel);
        textPanel.add(Box.createVerticalGlue());

        card.add(iconPanel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    private void deposit() {
        String input = JOptionPane.showInputDialog(this, "Enter deposit amount:");
        if (input != null && !input.isEmpty()) {
            try {
                double amount = Double.parseDouble(input);
                account.deposit(amount);
                updateUIComponents();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void withdraw() {
        String input = JOptionPane.showInputDialog(this, "Enter withdrawal amount:");
        if (input != null && !input.isEmpty()) {
            try {
                double amount = Double.parseDouble(input);
                if (!account.withdraw(amount)) {
                    JOptionPane.showMessageDialog(this, "Insufficient balance!", "Error", JOptionPane.ERROR_MESSAGE);
                }
                updateUIComponents();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addAccount() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Name:"));
        JTextField nameField = new JTextField();
        panel.add(nameField);

        panel.add(new JLabel("Account No:"));
        JTextField accField = new JTextField();
        panel.add(accField);

        panel.add(new JLabel("Password:"));
        JPasswordField passField = new JPasswordField();
        panel.add(passField);

        panel.add(new JLabel("Account Type:"));
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Saving", "Current"});
        panel.add(typeBox);

        panel.add(new JLabel("Initial Balance:"));
        JTextField balanceField = new JTextField();
        panel.add(balanceField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Account",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                String accNo = accField.getText().trim();
                String password = new String(passField.getPassword());
                String type = (String) typeBox.getSelectedItem();
                double balance = Double.parseDouble(balanceField.getText().trim());

                // This is a placeholder. You'll need to implement the real logic
                // in your Account class or a database.
                if (Account.addNewAccount(name, accNo, password, type, balance)) {
                    JOptionPane.showMessageDialog(this, "✅ New account created successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "⚠️ Failed to create account.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please check the values.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateUIComponents() {
        double newBalance = account.getBalance();
        balanceLabel.setText("Current Balance: ₹" + newBalance);
        updateCards();
        updateHistory();
    }

    private void updateHistory() {
        List<String> history = account.getTransactionHistory();
        historyArea.setText("");
        if (history.isEmpty()) {
            historyArea.setText("No transactions yet.");
        } else {
            for (String h : history) {
                historyArea.append(h + "\n");
            }
        }
        historyArea.setCaretPosition(0);
    }

    /**
     * --- THIS IS THE MISSING CLASS ---
     * This class provides the rounded corners for the panels.
     * Place it inside the DashboardFrame class, at the very bottom.
     */
    static class RoundedPanel extends JPanel {
        private final int radius;

        public RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
        }
    }
}