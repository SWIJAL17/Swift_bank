import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class DashboardFrame extends JFrame {
    private final Account account;
    private JLabel balanceLabel, welcomeLabel;
    private JTextArea historyArea;
    private JPanel contentPanel, sidePanel, cardsPanel;
    private JPanel activeMenuButton;

    public DashboardFrame(Account account) {
        this.account = account;
        setTitle("🏦 SwiftBank Dashboard");
        setSize(1180, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        initUI();
    }

    // 🎨 Theme
    static class Theme {
        static final Color PRIMARY = new Color(33, 60, 115);
        static final Color PRIMARY_LIGHT = new Color(41, 98, 174);
        static final Color BACKGROUND = new Color(246, 248, 252);
        static final Color CARD_BG = Color.WHITE;
        static final Color BORDER = new Color(220, 225, 235);
        static final Color TEXT_PRIMARY = new Color(35, 35, 35);
        static final Color TEXT_SECONDARY = new Color(120, 120, 120);
        static final Color SUCCESS = new Color(46, 204, 113);
        static final Color WARNING = new Color(255, 152, 0);
        static final Color INFO = new Color(66, 165, 245);
        static final Font FONT_TITLE = new Font("Segoe UI Semibold", Font.BOLD, 24);
        static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 18);
        static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
        static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Sidebar
        sidePanel = new JPanel();
        sidePanel.setBackground(Theme.PRIMARY);
        sidePanel.setPreferredSize(new Dimension(240, getHeight()));
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBorder(new EmptyBorder(30, 0, 30, 0));

        JLabel bankLogo = new JLabel("🏦", SwingConstants.CENTER);
        bankLogo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        bankLogo.setForeground(Color.WHITE);
        bankLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel bankTitle = new JLabel("SwiftBank");
        bankTitle.setFont(Theme.FONT_SUBTITLE);
        bankTitle.setForeground(Color.WHITE);
        bankTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidePanel.add(bankLogo);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidePanel.add(bankTitle);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 25)));

        JPanel homeBtn = addSidebarButton("🏠 Home", e -> showHomePanel());
        addSidebarButton("👤 Profile", e -> showProfilePanel());
        addSidebarButton("💰 Account Details", e -> showAccountDetails());
        addSidebarButton("📈 Transactions", e -> showTransactionPanel());
        addSidebarButton("🔒 Change Password", e -> showChangePasswordPanel());

        sidePanel.add(Box.createVerticalGlue());
        addSidebarButton("🚪 Logout", e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Theme.BACKGROUND);
        showHomePanel();
        setActiveMenuButton(homeBtn);

        add(sidePanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel addSidebarButton(String text, ActionListener action) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setMaximumSize(new Dimension(240, 45));
        panel.setBackground(Theme.PRIMARY);
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel label = new JLabel(text);
        label.setFont(Theme.FONT_BOLD);
        label.setForeground(Color.WHITE);
        label.setBorder(new EmptyBorder(0, 25, 0, 0));
        panel.add(label, BorderLayout.CENTER);

        panel.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { if (panel != activeMenuButton) panel.setBackground(Theme.PRIMARY_LIGHT); }
            @Override public void mouseExited(MouseEvent e) { if (panel != activeMenuButton) panel.setBackground(Theme.PRIMARY); }
            @Override public void mouseReleased(MouseEvent e) { setActiveMenuButton(panel); action.actionPerformed(null); }
        });

        sidePanel.add(panel);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 8)));
        return panel;
    }

    private void setActiveMenuButton(JPanel panel) {
        if (activeMenuButton != null) activeMenuButton.setBackground(Theme.PRIMARY);
        activeMenuButton = panel;
        activeMenuButton.setBackground(Theme.PRIMARY_LIGHT);
    }

    // 🏠 Home Panel
    private void showHomePanel() {
        contentPanel.removeAll();
        JPanel home = new JPanel(new BorderLayout(20, 20));
        home.setBackground(Theme.BACKGROUND);
        home.setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        welcomeLabel = new JLabel("Welcome, " + account.getName() + " 👋");
        welcomeLabel.setFont(Theme.FONT_TITLE);
        balanceLabel = new JLabel("Balance: ₹" + account.getBalance());
        balanceLabel.setFont(Theme.FONT_SUBTITLE);
        balanceLabel.setForeground(Theme.INFO);
        top.add(welcomeLabel, BorderLayout.WEST);
        top.add(balanceLabel, BorderLayout.EAST);

        cardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 10));
        cardsPanel.setOpaque(false);
        updateCards();

        JPanel history = createCardPanel("📜 Recent Transactions");
        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(Theme.FONT_BODY);
        JScrollPane scrollPane = new JScrollPane(historyArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        history.add(scrollPane, BorderLayout.CENTER);
        updateHistory();

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        actions.setOpaque(false);
        actions.add(createActionButton("💵 Deposit", Theme.SUCCESS, e -> deposit()));
        actions.add(createActionButton("💳 Withdraw", Theme.WARNING, e -> withdraw()));

        home.add(top, BorderLayout.NORTH);
        home.add(cardsPanel, BorderLayout.CENTER);
        home.add(history, BorderLayout.SOUTH);
        home.add(actions, BorderLayout.PAGE_END);
        contentPanel.add(home);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // 💵 Deposit
    private void deposit() {
        String input = JOptionPane.showInputDialog(this, "Enter deposit amount:");
        if (input == null || input.isEmpty()) return;
        try (Connection con = DBConnection.getConnection()) {
            double amt = Double.parseDouble(input);
            account.deposit(amt);
            PreparedStatement ps1 = con.prepareStatement("UPDATE accounts SET balance=? WHERE account_no=?");
            ps1.setDouble(1, account.getBalance());
            ps1.setString(2, account.getAccountNo());
            ps1.executeUpdate();

            PreparedStatement ps2 = con.prepareStatement("INSERT INTO transactions (account_no, type, amount) VALUES (?, 'Deposit', ?)");
            ps2.setString(1, account.getAccountNo());
            ps2.setDouble(2, amt);
            ps2.executeUpdate();

            JOptionPane.showMessageDialog(this, "✅ Deposited ₹" + amt);
            updateUIComponents();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "⚠️ Error: " + e.getMessage());
        }
    }

    // 💳 Withdraw
    private void withdraw() {
        String input = JOptionPane.showInputDialog(this, "Enter withdrawal amount:");
        if (input == null || input.isEmpty()) return;
        try (Connection con = DBConnection.getConnection()) {
            double amt = Double.parseDouble(input);
            if (!account.withdraw(amt)) {
                JOptionPane.showMessageDialog(this, "Insufficient balance!");
                return;
            }

            PreparedStatement ps1 = con.prepareStatement("UPDATE accounts SET balance=? WHERE account_no=?");
            ps1.setDouble(1, account.getBalance());
            ps1.setString(2, account.getAccountNo());
            ps1.executeUpdate();

            PreparedStatement ps2 = con.prepareStatement("INSERT INTO transactions (account_no, type, amount) VALUES (?, 'Withdrawal', ?)");
            ps2.setString(1, account.getAccountNo());
            ps2.setDouble(2, amt);
            ps2.executeUpdate();

            JOptionPane.showMessageDialog(this, "💳 Withdrawn ₹" + amt);
            updateUIComponents();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "⚠️ Error: " + e.getMessage());
        }
    }

    // 📜 Recent Transactions
    private void updateHistory() {
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT type, amount, timestamp FROM transactions WHERE account_no=? ORDER BY timestamp DESC LIMIT 10");
            ps.setString(1, account.getAccountNo());
            ResultSet rs = ps.executeQuery();
            historyArea.setText("");
            while (rs.next()) {
                historyArea.append(String.format("%s of ₹%.2f at %s%n",
                        rs.getString("type"),
                        rs.getDouble("amount"),
                        rs.getTimestamp("timestamp")));
            }
        } catch (SQLException e) {
            historyArea.setText("⚠️ Could not load transactions.");
        }
    }

    // 📈 Full Transaction Page
    private void showTransactionPanel() {
        contentPanel.removeAll();

        JPanel transactionsPanel = new JPanel(new BorderLayout(20, 20));
        transactionsPanel.setBackground(Theme.BACKGROUND);
        transactionsPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("📈 Transaction History", SwingConstants.LEFT);
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_PRIMARY);

        String[] columns = {"ID", "Type", "Amount (₹)", "Date & Time"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT id, type, amount, timestamp FROM transactions WHERE account_no=? ORDER BY timestamp DESC");
            ps.setString(1, account.getAccountNo());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("type"),
                        String.format("%.2f", rs.getDouble("amount")),
                        rs.getTimestamp("timestamp")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "⚠️ Error loading transactions: " + e.getMessage());
        }

        JTable table = new JTable(model);
        table.setFont(Theme.FONT_BODY);
        table.setRowHeight(28);
        table.getTableHeader().setFont(Theme.FONT_BOLD);
        table.getTableHeader().setBackground(Theme.PRIMARY_LIGHT);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(Theme.PRIMARY_LIGHT);
        table.setSelectionForeground(Color.WHITE);

        JButton backButton = new JButton("← Back to Home");
        backButton.setFont(Theme.FONT_BOLD);
        backButton.setBackground(Theme.INFO);
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setPreferredSize(new Dimension(180, 40));
        backButton.addActionListener(e -> showHomePanel());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(title, BorderLayout.WEST);
        topPanel.add(backButton, BorderLayout.EAST);

        transactionsPanel.add(topPanel, BorderLayout.NORTH);
        transactionsPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        contentPanel.add(transactionsPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showProfilePanel() {
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM customers c JOIN accounts a ON c.id=a.customer_id WHERE a.account_no=?");
            ps.setString(1, account.getAccountNo());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String info = String.format("""
                        👤 Name: %s
                        ✉️ Email: %s
                        🪪 Customer ID: %d
                        🕓 Joined: %s
                        """,
                        rs.getString("name"), rs.getString("email"),
                        rs.getInt("customer_id"), rs.getTimestamp("created_at"));
                JOptionPane.showMessageDialog(this, new JTextArea(info), "Profile Details", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "⚠️ Error loading profile: " + e.getMessage());
        }
    }

    private void showAccountDetails() {
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts WHERE account_no=?");
            ps.setString(1, account.getAccountNo());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String info = String.format("""
                        🏦 Account No: %s
                        📘 Type: %s
                        💰 Balance: ₹%.2f
                        ⏰ Created At: %s
                        """,
                        rs.getString("account_no"), rs.getString("type"),
                        rs.getDouble("balance"), rs.getTimestamp("created_at"));
                JOptionPane.showMessageDialog(this, new JTextArea(info), "Account Details", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "⚠️ Error loading account: " + e.getMessage());
        }
    }

    private void showChangePasswordPanel() {
        JPasswordField oldPass = new JPasswordField();
        JPasswordField newPass = new JPasswordField();
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Old Password:"));
        panel.add(oldPass);
        panel.add(new JLabel("New Password:"));
        panel.add(newPass);

        int result = JOptionPane.showConfirmDialog(this, panel, "🔒 Change Password", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection con = DBConnection.getConnection()) {
                PreparedStatement ps = con.prepareStatement("""
                    UPDATE customers 
                    SET password=? 
                    WHERE id=(SELECT customer_id FROM accounts WHERE account_no=?) 
                    AND password=?""");
                ps.setString(1, new String(newPass.getPassword()));
                ps.setString(2, account.getAccountNo());
                ps.setString(3, new String(oldPass.getPassword()));
                int rows = ps.executeUpdate();
                if (rows > 0)
                    JOptionPane.showMessageDialog(this, "✅ Password changed successfully!");
                else
                    JOptionPane.showMessageDialog(this, "❌ Incorrect old password!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "⚠️ Error: " + e.getMessage());
            }
        }
    }

    private void updateUIComponents() {
        balanceLabel.setText("Balance: ₹" + account.getBalance());
        updateCards();
        updateHistory();
    }

    private void updateCards() {
        cardsPanel.removeAll();
        cardsPanel.add(createInfoCard("💰", "Total Balance", "₹" + account.getBalance(), Theme.INFO));
        cardsPanel.add(createInfoCard("⬆️", "Deposits", "Track savings", Theme.SUCCESS));
        cardsPanel.add(createInfoCard("⬇️", "Withdrawals", "Track expenses", Theme.WARNING));
    }

    private JPanel createInfoCard(String icon, String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(15, 10));
        card.setBackground(Theme.CARD_BG);
        card.setPreferredSize(new Dimension(260, 120));
        card.setBorder(new CompoundBorder(new LineBorder(Theme.BORDER, 1, true),
                new EmptyBorder(20, 15, 15, 15)));
        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Theme.FONT_BOLD);
        titleLabel.setForeground(Theme.TEXT_SECONDARY);
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(Theme.FONT_SUBTITLE);
        valueLabel.setForeground(color);
        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(valueLabel);
        card.add(iconLabel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createCardPanel(String titleText) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.CARD_BG);
        panel.setBorder(new CompoundBorder(new LineBorder(Theme.BORDER, 1, true),
                new EmptyBorder(15, 15, 15, 15)));
        JLabel title = new JLabel(titleText);
        title.setFont(Theme.FONT_BOLD);
        title.setForeground(Theme.TEXT_PRIMARY);
        panel.add(title, BorderLayout.NORTH);
        return panel;
    }

    private JButton createActionButton(String text, Color color, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(Theme.FONT_BOLD);
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(150, 40));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(listener);
        return button;
    }
}
