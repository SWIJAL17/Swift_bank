import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SplashScreen extends JFrame {
    private float opacityLevel = 0f;
    private int progress = 0;
    private final JProgressBar progressBar;
    private final JLabel loadingLabel;
    private final Timer fadeInTimer, progressTimer;
    private int gradientShift = 0;

    public SplashScreen() {
        setTitle("💳 SwiftBank Loading...");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setUndecorated(true); // borderless window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Gradient Panel with dynamic effect
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                gradientShift += 2;
                GradientPaint gp = new GradientPaint(
                        gradientShift % getWidth(), 0, new Color(21, 101, 192),
                        getWidth(), getHeight(), new Color(41, 182, 246), true
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(60, 40, 40, 40));

        // Bank Logo
        JLabel logo = new JLabel("💳", JLabel.CENTER);
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 70));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logo.setForeground(Color.WHITE);

        JLabel bankName = new JLabel("SwiftBank", JLabel.CENTER);
        bankName.setFont(new Font("Segoe UI Semibold", Font.BOLD, 36));
        bankName.setForeground(Color.WHITE);
        bankName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tagline = new JLabel("Empowering Digital Banking", JLabel.CENTER);
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tagline.setForeground(new Color(255, 255, 255, 200));
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Progress Bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setPreferredSize(new Dimension(400, 25));
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        progressBar.setForeground(new Color(255, 255, 255));
        progressBar.setBackground(new Color(255, 255, 255, 70));
        progressBar.setBorderPainted(false);
        progressBar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        progressBar.setStringPainted(true);

        // Loading Label
        loadingLabel = new JLabel("Initializing secure systems...");
        loadingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        loadingLabel.setForeground(new Color(255, 255, 255, 200));
        loadingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalGlue());
        panel.add(logo);
        panel.add(bankName);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(tagline);
        panel.add(Box.createVerticalGlue());
        panel.add(progressBar);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(loadingLabel);

        add(panel);

        // Timers
        fadeInTimer = new Timer(30, new FadeInEffect());
        progressTimer = new Timer(50, new ProgressEffect(panel));

        fadeInTimer.start();
        progressTimer.start();
    }

    private class FadeInEffect implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            opacityLevel += 0.02f;
            if (opacityLevel > 1f) opacityLevel = 1f;
            setOpacity(opacityLevel);
            if (opacityLevel >= 1f) fadeInTimer.stop();
        }
    }

    private class ProgressEffect implements ActionListener {
        private final JPanel panel;

        public ProgressEffect(JPanel panel) {
            this.panel = panel;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            progress += 1;
            progressBar.setValue(progress);

            // Fancy dynamic text
            if (progress < 25)
                loadingLabel.setText("Initializing secure systems...");
            else if (progress < 50)
                loadingLabel.setText("Connecting to SwiftBank servers...");
            else if (progress < 75)
                loadingLabel.setText("Fetching encrypted account data...");
            else if (progress < 90)
                loadingLabel.setText("Almost ready...");
            else
                loadingLabel.setText("Launching dashboard...");

            panel.repaint();

            if (progress >= 100) {
                progressTimer.stop();
                dispose();
                new LoginFrame().setVisible(true);
            }
        }
    }
}
