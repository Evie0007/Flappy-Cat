// App.java
import javax.swing.*;

public class App {
    public static void main(String[] args) {
        int boardWidth = 1000;
        int boardHeight = 500;

        JFrame frame = new JFrame("Flappy Cat");
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        TitleScreen titleScreen = new TitleScreen(frame, boardWidth, boardHeight);
        frame.add(titleScreen);
        frame.pack();
        frame.setVisible(true);
    }
}
