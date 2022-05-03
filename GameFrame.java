import javax.swing.*;

public class GameFrame extends JFrame {


    GameFrame(){
        this.add(new GamePanel());
        this.pack();
        this.setTitle("The Game");

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        this.setVisible(true);



    }


}
