import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameOverScreen extends JPanel {
    private JFrame frame;
    private Image backgroundImg;
    private double score;
    private double highestScore;

    public GameOverScreen(JFrame frame, int boardWidth, int boardHeight, double score, double highestScore) {
        this.frame = frame;
        this.score = score;
        this.highestScore = highestScore;
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        requestFocusInWindow();

        
        backgroundImg = new ImageIcon(getClass().getResource("./bkg_3.png")).getImage();

        // Add mouse listener to detect mouse click
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                restartGame();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), null);

        // Display total score and highest score
        g.setColor(Color.white);
        g.setFont(new Font("Calibri", Font.BOLD, 24));
        String scoreText = "Score: " + (int) score;
        String highestScoreText = "Highest Score: " + (int) highestScore;

        FontMetrics fm = g.getFontMetrics();
        int scoreWidth = fm.stringWidth(scoreText);
        int highestScoreWidth = fm.stringWidth(highestScoreText);

        g.drawString(scoreText, (getWidth() - scoreWidth) / 2, 70);
        g.drawString(highestScoreText, (getWidth() - highestScoreWidth) / 2, 110);
    }

    private void restartGame() {
        frame.getContentPane().removeAll();
        FlappyCat flappyCat = new FlappyCat(frame);
        frame.add(flappyCat);
        frame.pack();
        flappyCat.requestFocus();
    }
}
