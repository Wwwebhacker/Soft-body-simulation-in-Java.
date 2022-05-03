import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
public class GamePanel extends JPanel implements ActionListener,MouseListener, MouseMotionListener {
    static final int SCREEN_WIDTH=1800;
    static final int SCREEN_HEIGHT=900;
    final int scale=100;
    final int BOX_H=SCREEN_HEIGHT/scale;
    final int BOX_W=SCREEN_WIDTH/scale;
    static final double BALL_SIZE=0.3;
    static final int DELAY=1000/600;
    long Time=0;

    long duration=0;
    boolean running=false;
    static boolean gameOn = false;
    Timer timer;
    Random random;


    ArrayList<Ball> balls=new ArrayList<>();
    ArrayList<Spring> springs=new ArrayList<>();
    static final int BALLS_AMT=2;
    static final int SPRINGS_AMT=4;

    Vector mpos=new Vector(0,0);
    boolean mouse=false;
    GamePanel(){

        random=new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MykeyAdapter());
        addMouseListener(this);
        addMouseMotionListener(this);
        startGame();
    }
    public void pause() {
        GamePanel.gameOn = true;
        timer.stop();
    }

    public void resume() {
        GamePanel.gameOn = false;
        timer.start();
    }
    public void startGame(){

        running=true;
        timer=new Timer(DELAY,this);
        double spacing=0.7;
        double mass=0.1;
        int d=10;

        for (int i=0;i<d;i++){
            for (int j=0;j<d;j++){
                balls.add(new Ball(BOX_W/2+j*spacing,BALL_SIZE/2+i*spacing,mass));
            }
            for (int j=1;j<d;j++){
                springs.add(new Spring(balls.get(j-1+i*d),balls.get(j+i*d),spacing));
            }
        }
        for (int i=1;i<d;i++){
            for (int j=0;j<d;j++){
                springs.add(new Spring(balls.get(j+(i-1)*d),balls.get(j+i*d),spacing));
            }

        }
        for (int k=-(d-2);k<d+2+1;k++){
            for (int i=0;i<d-1;i++){
                for (int j=0;j<d-1;j++){
                    if (i==j+k){
                        springs.add(new Spring(balls.get(j+i*d),balls.get((j+1)+(i+1)*d),spacing*Math.sqrt(2)));
                    }

                }

            }
        }
        for (int k=-(d-2);k<d+2+1;k++) {
            for (int i = 0; i < d - 1; i++) {
                for (int j = d - 1; j > 0; j--) {
                    if (i == (d - 1) - j + k) {
                        springs.add(new Spring(balls.get(j+d*i),balls.get((j-1)+(i+1)*d),spacing*Math.sqrt(2)));

                    }

                }

            }
        }


        timer.start();



    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g){
        if (running){
            for (Spring s:springs){
                g.setColor(new Color(255,(int)(255-Math.min(255,5*scale*Math.abs(Vector.sub(s.b.pos,s.a.pos).mag()-s.getRestlen())) ),0));
                double s_a_x=s.a.pos.x*scale;
                double s_a_y=s.a.pos.y*scale;
                double s_b_x=s.b.pos.x*scale;
                double s_b_y=s.b.pos.y*scale;

                g.drawLine((int)s_a_x,(int)s_a_y,(int)s_b_x,(int)s_b_y);
            }

            g.setColor(Color.GRAY);

            for (Ball b : balls){
                double x=b.pos.x-BALL_SIZE/2;
                x*=scale;
                double y=b.pos.y-BALL_SIZE/2;
                y*=scale;
                double b_size=BALL_SIZE*scale;

                g.fillOval((int)x,(int)y,(int)b_size,(int)b_size);
            }

        }

    }

    public void windForce(Ball b){
        Vector wind=new Vector(b.vel);

        wind.normalize();
        double diametr=BALL_SIZE;
        wind.mul(-(diametr*diametr*b.vel.mag()*b.vel.mag()));

        b.applyForce(wind);
    }

    public void gravityForce(Ball b){
        Vector grav=new Vector(0,5.8);
        b.applyForce(grav.mul(b.mass));

    }
    public void collision(Ball ball){



        if (ball.pos.y>BOX_H-BALL_SIZE/2){

            ball.vel.y=0;

            ball.pos.y=BOX_H-BALL_SIZE/2;

        }
        if  (ball.pos.y<BALL_SIZE/2){

            ball.vel.y=0;

            ball.pos.y=BALL_SIZE/2;
        }
        if (ball.pos.x<BALL_SIZE/2){

            ball.vel.x=0;

            ball.pos.x=BALL_SIZE/2;
        }
        if (ball.pos.x>BOX_W-BALL_SIZE/2){

            ball.vel.x=0;

            ball.pos.x=BOX_W-BALL_SIZE/2;

        }


    }
    int t_i=0;
    public void update(double delta){

        for (Spring s:springs){
            s.applyForce();
        }
        for (Ball b:balls){
            //windForce(b);
            gravityForce(b);
            collision(b);
        }









        if (mouse){
            Vector v=new Vector(mpos.x,mpos.y);
            v.div(scale);
            v.sub(balls.get(t_i).pos);
            v.mul(100*balls.get(t_i).mass);
            //v.limit(150);

            //balls.get(t_i).vel.mul(0);
            balls.get(t_i).applyForce(v);


        }

        for (Ball b:balls){
            b.calcPhysics(delta);
        }





    }



    @Override
    public void actionPerformed(ActionEvent e) {
        //long startTime = System.nanoTime();
        duration=System.nanoTime()-Time;
        duration = duration/1000000;
        if (running && duration<1000){

            update((double)(duration));




        }

        repaint();

        duration=0;
        Time = System.nanoTime();

       // long endTime = System.nanoTime();

        //long duration = (endTime - startTime)/1000000;

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //System.out.println("screen(X,Y) = " + e.getX() + "," + e.getY());

    }
    boolean found=false;
    @Override
    public void mousePressed(MouseEvent e) {
        //System.out.println("screen(X,Y) = " + e.getX() + "," + e.getY());
        mouse=true;
        mpos.x=e.getX();
        mpos.y=e.getY();
        if (!found){
            found=true;
            t_i=0;
            double dist=1000000000;
            Vector m=new Vector(mpos.x,mpos.y);
            m.div(scale);
            Vector p=new Vector(balls.get(0).pos);

            for(int i=0;i<balls.size();i++){
                p.set(balls.get(i).pos);
                p.sub(m);
                if (p.mag()<dist){
                    t_i=i;
                    dist=p.mag();
                }
            }
        }

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //System.out.println("screen(X,Y) = " + e.getX() + "," + e.getY());
        mouse=false;
        found=false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
       // System.out.println("screen(X,Y) = " + e.getX() + "," + e.getY());
    }

    @Override
    public void mouseExited(MouseEvent e) {
       // System.out.println("screen(X,Y) = " + e.getX() + "," + e.getY());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        //System.out.println("screen(X,Y) = " + e.getX() + "," + e.getY());
        mpos.x=e.getX();
        mpos.y=e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        //System.out.println("screen(X,Y) = " + e.getX() + "," + e.getY());


    }


    public class MykeyAdapter extends KeyAdapter{


        @Override

        public void keyPressed(KeyEvent e){
            switch (e.getKeyCode()){
                case KeyEvent.VK_LEFT:


                    break;
                case KeyEvent.VK_RIGHT:

                    break;
                case KeyEvent.VK_UP:


                    break;
                case KeyEvent.VK_DOWN:


                    break;
                case KeyEvent.VK_SPACE:
                    if(GamePanel.gameOn) {
                        resume();
                    } else {
                        pause();
                    }
                    break;
            }
        }
    }



}
//public class GamePanel extends JPanel implements ActionListener {
//    static final int SCREEN_WIDTH=800;
//    static final int SCREEN_HEIGHT=800;
//    static final int UNIT_SIZE=50;
//    static final int GAME_UNITS=(SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;
//    static final int DELAY=1000/10;
//    final int x[]=new int[GAME_UNITS];
//    final int y[]=new int[GAME_UNITS];
//    int bodyParts=6;
//    int applesEaten;
//    int appleX;
//    int appleY;
//    char direction='R';
//    boolean running=false;
//    static boolean gameOn = false;
//    Timer timer;
//    Random random;
//    GamePanel(){
//        random=new Random();
//        this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
//        this.setBackground(Color.BLACK);
//        this.setFocusable(true);
//        this.addKeyListener(new MykeyAdapter());
//        startGame();
//    }
//    public void pause() {
//        GamePanel.gameOn = true;
//        timer.stop();
//    }
//
//    public void resume() {
//        GamePanel.gameOn = false;
//        timer.start();
//    }
//    public void startGame(){
//        newApple();
//        running=true;
//        timer=new Timer(DELAY,this);
//
//        timer.start();
//    }
//    public void paintComponent(Graphics g){
//        super.paintComponent(g);
//        draw(g);
//    }
//    public void draw(Graphics g){
//        if (running){
//            //drawing grid
//            for (int i=0;i<SCREEN_HEIGHT/UNIT_SIZE;i++){
//                g.drawLine(i*UNIT_SIZE,0,i*UNIT_SIZE,SCREEN_HEIGHT);
//                g.drawLine(0,i*UNIT_SIZE,SCREEN_WIDTH,i*UNIT_SIZE);
//            }
//            //drawing apple
//            g.setColor(Color.RED);
//            g.fillOval(appleX,appleY,UNIT_SIZE,UNIT_SIZE);
//            //drawing snake
//            for (int i=0;i<bodyParts;i++){
//                if (i==0){
//                    g.setColor(Color.GREEN);
//                    g.fillRect(x[i],y[i],UNIT_SIZE,UNIT_SIZE);
//                }else {
//                    g.setColor(new Color(45,180,0));
//                    g.fillRect(x[i],y[i],UNIT_SIZE,UNIT_SIZE);
//                }
//            }
//            g.setColor(Color.RED);
//            g.setFont(new Font("Ink Free",Font.BOLD,40));
//            FontMetrics metrics=getFontMetrics(g.getFont());
//            g.drawString("Score: "+applesEaten,(SCREEN_WIDTH-metrics.stringWidth("Score: "+applesEaten))/2,g.getFont().getSize());
//        }else {
//            gameOver(g);
//        }
//
//    }
//    public void newApple(){
//        appleX=random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
//        appleY=random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;
//    }
//    public void move(){
//        for (int i=bodyParts;i>0;i--){
//            x[i]=x[i-1];
//            y[i]=y[i-1];
//        }
//        switch (direction){
//            case 'U':
//                y[0]=y[0]-UNIT_SIZE;
//                break;
//            case 'D':
//                y[0]=y[0]+UNIT_SIZE;
//                break;
//            case 'L':
//                x[0]=x[0]-UNIT_SIZE;
//                break;
//            case 'R':
//                x[0]=x[0]+UNIT_SIZE;
//                break;
//
//        }
//    }
//    public void checkApple(){
//        if ((x[0]==appleX)&&(y[0]==appleY)){
//            bodyParts++;
//            applesEaten++;
//            newApple();
//        }
//    }
//    public void checkCollisions(){
//        //checks if head collides with body
//        for (int i=bodyParts;i>0;i--){
//            if ((x[0]==x[i])&&(y[0]==y[i])){
//                running=false;
//            }
//        }
//        //check if head touches left border
//
//        if(x[0]<0){
//            x[0]=SCREEN_WIDTH;
//
//        }else
//        if(x[0]>SCREEN_WIDTH-UNIT_SIZE){
//            x[0]=0;
//        }else
//        //check if head touches top border
//        if(y[0]<0){
//            y[0]=SCREEN_HEIGHT;
//        }else
//        //check if head touches bottom border
//        if(y[0]>SCREEN_HEIGHT-UNIT_SIZE){
//            y[0]=0;
//        }
//        if (!running){
//            timer.stop();
//        }
//
//    }
//    public void gameOver(Graphics g){
//        //Score
//        g.setColor(Color.RED);
//        g.setFont(new Font("Ink Free",Font.BOLD,40));
//        FontMetrics metrics1=getFontMetrics(g.getFont());
//        g.drawString("Score: "+applesEaten,(SCREEN_WIDTH-metrics1.stringWidth("Score: "+applesEaten))/2,g.getFont().getSize());
//        //Game Over text
//        g.setColor(Color.RED);
//        g.setFont(new Font("Ink Free",Font.BOLD,75));
//        FontMetrics metrics2=getFontMetrics(g.getFont());
//        g.drawString("Game Over",(SCREEN_WIDTH-metrics2.stringWidth("Game Over"))/2,SCREEN_HEIGHT/2);
//    }
//    @Override
//    public void actionPerformed(ActionEvent e) {
//        if (running){
//            move();
//            checkApple();
//            checkCollisions();
//        }
//        repaint();
//    }
//    public class MykeyAdapter extends KeyAdapter{
//        @Override
//        public void keyPressed(KeyEvent e){
//            switch (e.getKeyCode()){
//                case KeyEvent.VK_LEFT:
//                    if (direction!='R'){
//                        direction='L';
//                    }
//                    break;
//                case KeyEvent.VK_RIGHT:
//                    if (direction!='L'){
//                        direction='R';
//                    }
//                    break;
//                case KeyEvent.VK_UP:
//                    if (direction!='D'){
//                        direction='U';
//                    }
//                    break;
//                case KeyEvent.VK_DOWN:
//                    if (direction!='U'){
//                        direction='D';
//                    }
//                    break;
//                case KeyEvent.VK_SPACE:
//                    if(GamePanel.gameOn) {
//                        resume();
//                    } else {
//                        pause();
//                    }
//                    break;
//            }
//        }
//    }
//
//
//
//}
