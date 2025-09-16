import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class TitleScreen extends JPanel {
    private JFrame frame;
    private Image backgroundImg;

    public TitleScreen(JFrame frame, int boardWidth, int boardHeight) {
        this.frame = frame;
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        requestFocusInWindow();

        backgroundImg = new ImageIcon(getClass().getResource("./bkg_1.png")).getImage();

        // Add key listener to detect the space button press
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    startGame();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), null);
    }

    private void startGame() {
        frame.getContentPane().removeAll();
        FlappyCat flappyCat = new FlappyCat(frame);
        frame.add(flappyCat);
        frame.pack();
        flappyCat.requestFocus();
    }
}
