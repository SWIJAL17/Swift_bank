import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class DashboardFrame extends JFrame {
    private final Account account;
    private JLabel balanceLabel, welcomeLabel;
    private JTextArea historyArea;
    private JPanel contentPanel;

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
        JPanel sidePanel = new JPanel();
        sidePanel.setBackground(new Color(33, 60, 115));
        sidePanel.setPreferredSize(new Dimension(240, getHeight()));
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));

        // Load Bank Icon (Image or Fallback Emoji)
        JLabel bankIcon;
        try {
            ImageIcon icon = new ImageIcon("images/bank.png"); // <-- place your bank.png in /images/
            if (icon.getIconWidth() > 0) {
                Image scaled = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                bankIcon = new JLabel(new ImageIcon(scaled), SwingConstants.CENTER);
            } else {
                throw new Exception("Image not found, using emoji fallback");
            }
        } catch (Exception e) {
            bankIcon = new JLabel("\uD83C\uDFE6", SwingConstants.CENTER); // 🏦
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

            if (item.contains("Logout")) {
                btn.addActionListener(e -> {
                    new LoginFrame().setVisible(true);
                    dispose();
                });
            }

            sidePanel.add(Box.createRigidArea(new Dimension(0, 8)));
            sidePanel.add(btn);
        }

        // ---------- MAIN CONTENT ----------
        contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBackground(new Color(245, 247, 250));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        welcomeLabel = new JLabel("Welcome, " + account.getName() + " 👋", SwingConstants.LEFT);
        welcomeLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 22));
        welcomeLabel.setForeground(new Color(33, 33, 33));
        topPanel.add(welcomeLabel, BorderLayout.WEST);

        balanceLabel = new JLabel("Current Balance: ₹" + account.getBalance(), SwingConstants.RIGHT);
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        balanceLabel.setForeground(new Color(25, 118, 210));
        topPanel.add(balanceLabel, BorderLayout.EAST);

        // ---------- DASHBOARD CARDS ----------
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        cardsPanel.setOpaque(false);

        cardsPanel.add(createInfoCard("💰", "Total Balance", "₹" + account.getBalance(), new Color(79, 195, 247)));
        cardsPanel.add(createInfoCard("⬆️", "Deposits", "Track your savings", new Color(102, 187, 106)));
        cardsPanel.add(createInfoCard("⬇️", "Withdrawals", "Monitor expenses", new Color(239, 108, 0)));

        // ---------- TRANSACTION HISTORY ----------
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

        // ---------- ACTION BUTTONS ----------
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        actionPanel.setOpaque(false);
        String[][] actions = {
                {"➕ Add Account", "#4CAF50"},
                {"🔄 Update Details", "#0288D1"},
                {"🔍 Search", "#7E57C2"},
                {"❌ Delete", "#E53935"},
                {"💵 Deposit", "#43A047"},
                {"💳 Withdraw", "#FB8C00"},
                {"📊 View Report", "#039BE5"}
        };

        for (String[] a : actions) {
            JButton b = new JButton(a[0]);
            b.setFont(new Font("Segoe UI", Font.BOLD, 14));
            b.setForeground(Color.WHITE);
            b.setBackground(Color.decode(a[1]));
            b.setFocusPainted(false);
            b.setBorderPainted(false);
            b.setPreferredSize(new Dimension(150, 40));
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            switch (a[0]) {
                case "💵 Deposit" -> b.addActionListener(e -> deposit());
                case "💳 Withdraw" -> b.addActionListener(e -> withdraw());
                case "📊 View Report" -> JOptionPane.showMessageDialog(this, "Report feature coming soon!");
            }

            actionPanel.add(b);
        }

        // ---------- CENTER LAYOUT ----------
        JPanel centerPanel = new JPanel(new BorderLayout(20, 20));
        centerPanel.setOpaque(false);
        centerPanel.add(cardsPanel, BorderLayout.NORTH);
        centerPanel.add(historyPanel, BorderLayout.CENTER);
        centerPanel.add(actionPanel, BorderLayout.SOUTH);

        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(centerPanel, BorderLayout.CENTER);

        add(sidePanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

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

    private void updateUIComponents() {
        balanceLabel.setText("Current Balance: ₹" + account.getBalance());
        updateHistory();
    }

    private void updateHistory() {
        List<String> history = account.getTransactionHistory();
        historyArea.setText("");
        for (String h : history) historyArea.append(h + "\n");
    }

    // ---------- ROUNDED PANEL ----------
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
