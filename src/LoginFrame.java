import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginFrame extends JFrame {
    private final JTextField accNoField;
    private final JPasswordField passwordField;
    private final JButton loginBtn;
    private final JLabel statusLabel;
    private float hueShift = 0f; // For animated gradient

    public LoginFrame() {
        setTitle("💳 SwiftBank - Secure Login");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // ---------- Animated Dark Gradient Background ----------
        JPanel bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                hueShift += 0.001f;
                Color c1 = new Color(15, 20, 40);
                Color c2 = new Color(25, 35, 55);
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bgPanel.setLayout(new GridBagLayout());
        Timer t = new Timer(50, e -> bgPanel.repaint());
        t.start();

        // ---------- Login Card ----------
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(360, 280));
        card.setBackground(new Color(35, 40, 55)); // slightly lighter dark panel
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(79, 195, 247), 1),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("SwiftBank Login", JLabel.CENTER);
        title.setFont(new Font("Segoe UI Semibold", Font.BOLD, 22));
        title.setForeground(new Color(79, 195, 247));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(title);
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        // ---------- Account No Field ----------
        JLabel accLabel = new JLabel("Account Number:");
        accLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        accLabel.setForeground(Color.LIGHT_GRAY);
        accLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(accLabel);

        accNoField = new JTextField();
        accNoField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        accNoField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        accNoField.setBackground(new Color(48, 54, 70));
        accNoField.setForeground(Color.WHITE);
        accNoField.setCaretColor(Color.WHITE);
        accNoField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(79, 195, 247), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        card.add(accNoField);
        card.add(Box.createRigidArea(new Dimension(0, 10)));

        // ---------- Password Field ----------
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passLabel.setForeground(Color.LIGHT_GRAY);
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBackground(new Color(48, 54, 70));
        passwordField.setForeground(Color.WHITE);
        passwordField.setCaretColor(Color.WHITE);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(79, 195, 247), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        card.add(passwordField);
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        // ---------- Login Button ----------
        loginBtn = new JButton("Login");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        loginBtn.setForeground(Color.BLACK);
        loginBtn.setBackground(new Color(79, 195, 247));
        loginBtn.setFocusPainted(false);
        loginBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(loginBtn);

        // Hover effect
        loginBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                loginBtn.setBackground(new Color(129, 212, 250));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                loginBtn.setBackground(new Color(79, 195, 247));
            }
        });

        // ---------- Status Label ----------
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(200, 200, 200));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(statusLabel);

        // ---------- Add Action ----------
        loginBtn.addActionListener(e -> login());
        passwordField.addActionListener(e -> login());
        accNoField.addActionListener(e -> login());

        bgPanel.add(card);
        add(bgPanel);
    }

    private void login() {
        String accNo = accNoField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (accNo.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both account number and password.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        statusLabel.setForeground(Color.LIGHT_GRAY);
        statusLabel.setText("🔒 Verifying credentials...");
        loginBtn.setEnabled(false);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try (Connection con = DBConnection.getConnection()) {
                    if (con == null) {
                        statusLabel.setForeground(Color.RED);
                        statusLabel.setText("❌ Database connection failed.");
                        return null;
                    }

                    PreparedStatement ps = con.prepareStatement(
                            "SELECT * FROM accounts WHERE account_no = ? AND password = ?");
                    ps.setString(1, accNo);
                    ps.setString(2, password);
                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {
                        String name = rs.getString("name");
                        double balance = rs.getDouble("balance");
                        Account account = new Account(name, accNo, balance);

                        SwingUtilities.invokeLater(() -> {
                            dispose();
                            new DashboardFrame(account).setVisible(true);
                        });
                    } else {
                        statusLabel.setForeground(Color.RED);
                        statusLabel.setText("❌ Invalid account number or password.");
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText("⚠️ Database error.");
                } finally {
                    SwingUtilities.invokeLater(() -> loginBtn.setEnabled(true));
                }
                return null;
            }
        };
        worker.execute();
    }

    // For testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
