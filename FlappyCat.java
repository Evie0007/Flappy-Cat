import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

/*Sources: 
https://www.youtube.com/watch?v=Xw2MEG-FBsE&ab_channel=KennyYipCoding
https://www.youtube.com/watch?v=I1qTZaUcFX0&ab_channel=Jaryt
https://www.youtube.com/watch?v=GjJfRi3qpg4&t=155s&ab_channel=Th%E1%BA%A1chPh%E1%BA%A1mDev
*/

public class FlappyCat extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 1000;
    int boardHeight = 500;
    JFrame frame;

   
    Image backgroundImg;
    Image catImg; 
    Image topPipeImg;
    Image bottomPipeImg;

    // Cat class
    int catX = boardWidth / 8; 
    int catY = boardHeight / 2; 
    int catWidth = 70; 
    int catHeight = 70; 

    class Cat { 
        int x = catX;
        int y = catY;
        int width = catWidth;
        int height = catHeight;
        Image img;

        Cat(Image img) { 
            this.img = img;
        }
    }

    
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 80;  
    int pipeHeight = 500;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }

    // Game logic
    Cat cat; 
    int velocityX = -4; // Move pipes to the left speed 
    int velocityY = 0; // Move cat up/down speed
    int gravity = 1;
    double speedMultiplier = 1.0; 
    int scoreThreshold = 10; // Increase speed after every 10 points

    ArrayList<Pipe> pipes;
    Random random = new Random();

    Timer gameLoop;
    Timer placePipeTimer;
    boolean gameOver = false;
    double score = 0;
    static double highestScore = 0;

    FlappyCat(JFrame frame) {
        this.frame = frame;
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

       
        backgroundImg = new ImageIcon(getClass().getResource("./bkg_2.png")).getImage();
        catImg = new ImageIcon(getClass().getResource("./donutcat.png")).getImage(); 
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe1.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe1.png")).getImage();

        // Cat
        cat = new Cat(catImg); 
        pipes = new ArrayList<>();

        // Place pipes timer
        placePipeTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipeTimer.start();

        // Game timer
        gameLoop = new Timer(1000 / 60, this); 
        gameLoop.start();
    }

    void placePipes() {
        int randomPipeY = (int) (pipeY - pipeHeight / 3 - Math.random() * (pipeHeight / 2));
        int openingSpace = (int) (boardHeight / 2.5); 
    
        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);
    
        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // Background
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

        // Cat
        g.drawImage(cat.img, cat.x, cat.y, cat.width, cat.height, null); 

        // Pipes
        for (Pipe pipe : pipes) {
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        // Score
        g.setColor(Color.white);
        g.setFont(new Font("Calibri", Font.PLAIN, 32));
        g.drawString(String.valueOf((int) score), 10, 35);
    }

    public void move() {
        // Cat
        velocityY += gravity;
        cat.y += velocityY;
        cat.y = Math.max(cat.y, 0); 

        // Pipes
        for (Pipe pipe : pipes) {
            pipe.x += (int)(velocityX * speedMultiplier); 

            if (!pipe.passed && cat.x > pipe.x + pipe.width) {
                score += 0.5; //1 for each set of pipes
                pipe.passed = true;
            }

            if (collision(cat, pipe)) {
                gameOver = true;
            }
        }

        if (cat.y > boardHeight) {
            gameOver = true;
        }

        // Increase speed multiplier after every 10 points
        if (score >= scoreThreshold) {
            speedMultiplier += 0.2;
            scoreThreshold += 10; // Increment score threshold for the next speed increase
        }
    }

    boolean collision(Cat a, Pipe b) {
        return a.x < b.x + b.width &&   
               a.x + a.width > b.x &&   
               a.y < b.y + b.height &&  
               a.y + a.height > b.y;    
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placePipeTimer.stop();
            gameLoop.stop();
            if (score > highestScore) {
                highestScore = score;
            }
            showGameOverScreen();
        }
    }

    private void showGameOverScreen() {
        frame.getContentPane().removeAll();
        GameOverScreen gameOverScreen = new GameOverScreen(frame, boardWidth, boardHeight, score, highestScore);
        frame.add(gameOverScreen);
        frame.pack();
        gameOverScreen.requestFocus();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9;

            if (gameOver) {
                // Restart game by switching to title screen
                frame.getContentPane().removeAll();
                TitleScreen titleScreen = new TitleScreen(frame, boardWidth, boardHeight);
                frame.add(titleScreen);
                frame.pack();
                titleScreen.requestFocus();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
