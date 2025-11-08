import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

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

    // 🎨 --- Theme ---
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

        // --- Sidebar ---
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

        // --- Content Area ---
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Theme.BACKGROUND);
        showHomePanel();

        // --- Default Active ---
        setActiveMenuButton(homeBtn);

        add(sidePanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    // 🧭 Sidebar Button Creator
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
            @Override
            public void mouseEntered(MouseEvent e) {
                if (panel != activeMenuButton) panel.setBackground(Theme.PRIMARY_LIGHT);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (panel != activeMenuButton) panel.setBackground(Theme.PRIMARY);
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                setActiveMenuButton(panel);
                action.actionPerformed(null);
            }
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

        JPanel home = new JPanel();
        home.setLayout(new BorderLayout(20, 20));
        home.setBackground(Theme.BACKGROUND);
        home.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Header
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        welcomeLabel = new JLabel("Welcome, " + account.getName() + " 👋");
        welcomeLabel.setFont(Theme.FONT_TITLE);
        balanceLabel = new JLabel("Balance: ₹" + account.getBalance());
        balanceLabel.setFont(Theme.FONT_SUBTITLE);
        balanceLabel.setForeground(Theme.INFO);
        top.add(welcomeLabel, BorderLayout.WEST);
        top.add(balanceLabel, BorderLayout.EAST);

        // Info Cards
        cardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 10));
        cardsPanel.setOpaque(false);
        updateCards();

        // Recent Transactions
        JPanel history = createCardPanel("📜 Recent Transactions");
        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(Theme.FONT_BODY);
        historyArea.setBackground(Theme.CARD_BG);
        JScrollPane scrollPane = new JScrollPane(historyArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        history.add(scrollPane, BorderLayout.CENTER);
        updateHistory();

        // Group middle (cards + history)
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout(20, 20));
        centerPanel.setOpaque(false);
        centerPanel.add(cardsPanel, BorderLayout.NORTH);
        centerPanel.add(history, BorderLayout.CENTER);

        // Actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        actions.setOpaque(false);
        actions.add(createActionButton("💵 Deposit", Theme.SUCCESS, e -> deposit()));
        actions.add(createActionButton("💳 Withdraw", Theme.WARNING, e -> withdraw()));
        actions.add(createActionButton("➕ Add Account", Theme.INFO, e -> addAccount()));

        home.add(top, BorderLayout.NORTH);
        home.add(centerPanel, BorderLayout.CENTER);
        home.add(actions, BorderLayout.SOUTH);

        contentPanel.add(home, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // 🎴 Info Cards
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
        card.setBorder(new CompoundBorder(
                new LineBorder(Theme.BORDER, 1, true),
                new EmptyBorder(20, 15, 15, 15)
        ));

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
        panel.setBorder(new CompoundBorder(
                new LineBorder(Theme.BORDER, 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));
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
        button.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { button.setBackground(color.darker()); }
            @Override public void mouseExited(MouseEvent e) { button.setBackground(color); }
        });
        return button;
    }

    // 💵 Deposit / Withdraw / Add Account
    private void deposit() {
        String input = JOptionPane.showInputDialog(this, "Enter deposit amount:");
        if (input != null && !input.isEmpty()) {
            try {
                double amt = Double.parseDouble(input);
                account.deposit(amt);
                updateUIComponents();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid amount!");
            }
        }
    }

    private void withdraw() {
        String input = JOptionPane.showInputDialog(this, "Enter withdrawal amount:");
        if (input != null && !input.isEmpty()) {
            try {
                double amt = Double.parseDouble(input);
                if (!account.withdraw(amt)) {
                    JOptionPane.showMessageDialog(this, "Insufficient balance!");
                }
                updateUIComponents();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid amount!");
            }
        }
    }

    private void addAccount() {
        JOptionPane.showMessageDialog(this, "Add Account feature coming soon.");
    }

    private void updateUIComponents() {
        balanceLabel.setText("Balance: ₹" + account.getBalance());
        updateCards();
        updateHistory();
    }

    private void updateHistory() {
        List<String> history = account.getTransactionHistory();
        historyArea.setText("");
        if (history.isEmpty()) historyArea.setText("No transactions yet.");
        else for (String h : history) historyArea.append(h + "\n");
    }

    private void showProfilePanel() { JOptionPane.showMessageDialog(this, "Profile feature coming soon."); }
    private void showAccountDetails() { JOptionPane.showMessageDialog(this, "Account Details coming soon."); }
    private void showTransactionPanel() { JOptionPane.showMessageDialog(this, "Transaction History coming soon."); }
    private void showChangePasswordPanel() { JOptionPane.showMessageDialog(this, "Change Password feature coming soon."); }
}
