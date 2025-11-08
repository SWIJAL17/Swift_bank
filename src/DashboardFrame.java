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
        sidePanel.setBackground(new Color(33, 60, 115));
        sidePanel.setPreferredSize(new Dimension(240, getHeight()));
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));

        JLabel bankIcon;
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/bank.png"));
            Image scaled = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            bankIcon = new JLabel(new ImageIcon(scaled), SwingConstants.CENTER);
        } catch (Exception e) {
            bankIcon = new JLabel("\uD83C\uDFE6", SwingConstants.CENTER);
            bankIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        }
        bankIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel bankName = new JLabel("SwiftBank Limited", SwingConstants.CENTER);
        bankName.setFont(new Font("Segoe UI Semibold", Font.BOLD, 18));
        bankName.setForeground(Color.WHITE);
        bankName.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidePanel.add(Box.createRigidArea(new Dimension(0, 30)));
        sidePanel.add(bankIcon);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidePanel.add(bankName);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 20)));

        String[] menuItems = {
                "🏠  Home",
                "👤  Profile",
                "💰  Account Details",
                "📈  Transactions",
                "🧾  Reports",
                "🔒  Change Password",
                "🚪  Logout"
        };

        for (String item : menuItems) {
            JButton btn = new JButton(item);
            btn.setMaximumSize(new Dimension(200, 45));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setBackground(new Color(41, 98, 174));
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            btn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    btn.setBackground(new Color(25, 118, 210));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    btn.setBackground(new Color(41, 98, 174));
                }
            });

            // 🎯 Add button functionality
            btn.addActionListener(e -> {
                switch (item) {
                    case "🏠  Home" -> showHomePanel();
                    case "👤  Profile" -> showProfilePanel();
                    case "💰  Account Details" -> showAccountDetails();
                    case "📈  Transactions" -> showTransactionPanel();
                    case "🧾  Reports" -> showReportsPanel();
                    case "🔒  Change Password" -> showChangePasswordPanel();
                    case "🚪  Logout" -> {
                        new LoginFrame().setVisible(true);
                        dispose();
                    }
                }
            });

            sidePanel.add(Box.createRigidArea(new Dimension(0, 8)));
            sidePanel.add(btn);
        }

        // ---------- MAIN CONTENT ----------
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(245, 247, 250));
        showHomePanel(); // default home view

        add(sidePanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    // 🏠 HOME PANEL
    private void showHomePanel() {
        contentPanel.removeAll();
        JPanel homePanel = new JPanel(new BorderLayout(20, 20));
        homePanel.setBackground(new Color(245, 247, 250));
        homePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        welcomeLabel = new JLabel("Welcome, " + account.getName() + " 👋");
        welcomeLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 22));

        balanceLabel = new JLabel("Current Balance: ₹" + account.getBalance());
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        balanceLabel.setForeground(new Color(25, 118, 210));

        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(balanceLabel, BorderLayout.EAST);

        // Info Cards
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        cardsPanel.setOpaque(false);
        cardsPanel.add(createInfoCard("💰", "Total Balance", "₹" + account.getBalance(), new Color(79, 195, 247)));
        cardsPanel.add(createInfoCard("⬆️", "Deposits", "Track your savings", new Color(102, 187, 106)));
        cardsPanel.add(createInfoCard("⬇️", "Withdrawals", "Monitor expenses", new Color(239, 108, 0)));

        // History Panel
        JPanel historyPanel = new RoundedPanel(25);
        historyPanel.setLayout(new BorderLayout(10, 10));
        historyPanel.setBackground(Color.WHITE);
        historyPanel.setBorder(BorderFactory.createTitledBorder("📜 Recent Transactions"));

        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(historyArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        historyPanel.add(scrollPane, BorderLayout.CENTER);
        updateHistory();

        // Action Buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        actionPanel.setOpaque(false);
        String[][] actions = {
                {"💵 Deposit", "#43A047"},
                {"💳 Withdraw", "#FB8C00"},
                {"➕ Add Account", "#4CAF50"},
                {"📊 View Report", "#039BE5"}
        };

        for (String[] a : actions) {
            JButton b = new JButton(a[0]);
            b.setFont(new Font("Segoe UI", Font.BOLD, 14));
            b.setForeground(Color.WHITE);
            b.setBackground(Color.decode(a[1]));
            b.setFocusPainted(false);
            b.setBorderPainted(false);
            b.setPreferredSize(new Dimension(160, 40));
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            switch (a[0]) {
                case "💵 Deposit" -> b.addActionListener(e -> deposit());
                case "💳 Withdraw" -> b.addActionListener(e -> withdraw());
                case "➕ Add Account" -> b.addActionListener(e -> addAccount());
                case "📊 View Report" ->
                        JOptionPane.showMessageDialog(this, "📈 Report feature coming soon!");
            }
            actionPanel.add(b);
        }

        homePanel.add(topPanel, BorderLayout.NORTH);
        homePanel.add(cardsPanel, BorderLayout.CENTER);
        homePanel.add(historyPanel, BorderLayout.SOUTH);
        homePanel.add(actionPanel, BorderLayout.PAGE_END);

        contentPanel.add(homePanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // 👤 PROFILE PANEL
    private void showProfilePanel() {
        contentPanel.removeAll();
        JPanel profilePanel = new JPanel(new BorderLayout());
        profilePanel.setBackground(Color.WHITE);
        profilePanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("👤 Profile Information", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JTextArea details = new JTextArea(
                "Name: " + account.getName() + "\n" +
                        "Account No: " + account.getAccountNo() + "\n" +
                        "Balance: ₹" + account.getBalance() + "\n" +
                        "Type: Saving"
        );
        details.setEditable(false);
        details.setFont(new Font("Consolas", Font.PLAIN, 14));

        profilePanel.add(title, BorderLayout.NORTH);
        profilePanel.add(details, BorderLayout.CENTER);

        contentPanel.add(profilePanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // 💰 ACCOUNT DETAILS
    private void showAccountDetails() {
        JOptionPane.showMessageDialog(this, "Account Details feature coming soon!");
    }

    // 📈 TRANSACTIONS
    private void showTransactionPanel() {
        JOptionPane.showMessageDialog(this, "Transactions module under development!");
    }

    // 🧾 REPORTS
    private void showReportsPanel() {
        JOptionPane.showMessageDialog(this, "Reports section coming soon!");
    }

    // 🔒 CHANGE PASSWORD
    private void showChangePasswordPanel() {
        JPasswordField oldPass = new JPasswordField();
        JPasswordField newPass = new JPasswordField();
        Object[] form = {
                "Old Password:", oldPass,
                "New Password:", newPass
        };
        int result = JOptionPane.showConfirmDialog(this, form, "Change Password", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(this, "Password changed successfully (mock-up).");
        }
    }

    // ---------- INFO CARD CREATOR ----------
    private JPanel createInfoCard(String icon, String title, String value, Color color) {
        JPanel card = new RoundedPanel(20);
        card.setLayout(new BorderLayout());
        card.setBackground(color);
        card.setPreferredSize(new Dimension(200, 100));

        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        iconLabel.setForeground(Color.WHITE);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(Color.WHITE);

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        valueLabel.setForeground(Color.WHITE);

        if (title.equals("Total Balance")) totalBalanceValueLabel = valueLabel;

        card.add(iconLabel, BorderLayout.NORTH);
        card.add(titleLabel, BorderLayout.CENTER);
        card.add(valueLabel, BorderLayout.SOUTH);
        return card;
    }

    // ---------- TRANSACTION LOGIC ----------
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
        JTextField nameField = new JTextField();
        JTextField accField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Saving", "Current"});
        JTextField balanceField = new JTextField();

        Object[] form = {
                "Name:", nameField,
                "Account No:", accField,
                "Password:", passField,
                "Account Type:", typeBox,
                "Initial Balance:", balanceField
        };

        int result = JOptionPane.showConfirmDialog(this, form, "Add New Account", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText();
                String accNo = accField.getText();
                String password = new String(passField.getPassword());
                String type = (String) typeBox.getSelectedItem();
                double balance = Double.parseDouble(balanceField.getText());

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

    // ---------- UI REFRESH ----------
    private void updateUIComponents() {
        double newBalance = account.getBalance();
        balanceLabel.setText("Current Balance: ₹" + newBalance);
        if (totalBalanceValueLabel != null)
            totalBalanceValueLabel.setText("₹" + newBalance);
        updateHistory();
    }

    private void updateHistory() {
        List<String> history = account.getTransactionHistory();
        historyArea.setText("");
        for (String h : history) historyArea.append(h + "\n");
    }

    // ---------- CUSTOM ROUNDED PANEL ----------
    static class RoundedPanel extends JPanel {
        private final int radius;
        public RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
        }
    }
}
