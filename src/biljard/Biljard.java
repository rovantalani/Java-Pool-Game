/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biljard;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import static biljard.Ball.RADIUS;

/**
 *
 * @author Joachim Parrow 2010 rev 2011, 2012, 2013, 2015, 2016
 *
 * Simulator for two balls
 */

public class Biljard {

    final static int UPDATE_FREQUENCY = 100;    // Global constant: fps, ie times per second to simulate

    public static void main(String[] args) {

        JFrame frame = new JFrame("No collisions!");          
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Table table = new Table();           

        frame.add(table);  
        frame.pack();
        frame.setVisible(true);
    }
}

/**
 * *****************************************************************************************
 * Coord
 *
 * A coordinate is a pair (x,y) of doubles. Also used to represent vectors. Here
 * are various utility methods to compute with vectors.
 *
 *
 */
class Coord {

    double x, y;

    Coord(double xCoord, double yCoord) {
        x = xCoord;
        y = yCoord;
    }
    
    Coord(MouseEvent event) {                   // Create a Coord from a mouse event
        x = event.getX();
        y = event.getY();
    }

    static final Coord ZERO = new Coord(0,0);
    
    static Coord zero() {
        return new Coord(0,0);
    }
    
    double magnitude() {                        
        return Math.sqrt(x * x + y * y);
    }

    Coord norm() {                              // norm: a normalised vector at the same direction
        return new Coord(x / magnitude(), y / magnitude());
    }

    void increase(Coord c) {           
        x += c.x;
        y += c.y;
    }
    
    void decrease(Coord c) {
        x -= c.x;
        y -= c.y;
    }
    
    static Coord add(Coord a, Coord b) {
        return new Coord(a.x + b.x, a.y + b.y);
    }
    
    static double scal(Coord a, Coord b) {      // scalar product
        return a.x * b.x + a.y * b.y;
    } 
    
    static Coord sub(Coord a, Coord b) {        
        return new Coord(a.x - b.x, a.y - b.y);
    }

    static Coord mul(double k, Coord c) {       // multiplication by a constant
        return new Coord(k * c.x, k * c.y);
    }

    static double distance(Coord a, Coord b) {
        return Coord.sub(a, b).magnitude();
    }
    
    static void paintLine(Graphics2D graph2D, Coord a, Coord b){  // paint line between points
        graph2D.setColor(Color.black);
        graph2D.drawLine((int)a.x, (int)a.y, (int)b.x, (int)b.y);
    }
}

/**
 * ****************************************************************************************
 * Table
 *
 * The table has some constants and instance variables relating to the graphics and
 * the balls. When simulating the balls it starts a timer
 * which fires UPDATE_FREQUENCY times per second. Each time the timer is
 * activated one step of the simulation is performed. The table reacts to
 * events to accomplish repaints and to stop or start the timer.
 *
 */
class Table extends JPanel implements MouseListener, MouseMotionListener, ActionListener {

    final int   TABLE_WIDTH    =  800;
    final int   TABLE_HEIGHT   = 500;
    final int   WALL_THICKNESS = 30;
    private final Color COLOR          = Color.green;
    private final Color WALL_COLOR     = Color.black;
           Ball  whiteBall;

    private final Timer simulationTimer;
    final int AMOUNT_BALLS = 10;
    final int AMOUNT_HOLES = 6;
    Ball[] otherBalls = new Ball[AMOUNT_BALLS];
    Hole[] holes = new Hole[AMOUNT_HOLES];
    Coord[] holePos = new Coord[AMOUNT_HOLES];
    int PLAYER1_POINTS = 0;
    int PLAYER2_POINTS = 0;
    boolean turn = true;
    private JLabel theLabel  = new JLabel();
    private JLabel theLabel2 = new JLabel();
    private JLabel theLabel3 = new JLabel();
    private JButton button = new JButton("New Game");

    
    Table() {
        
        setPreferredSize(new Dimension(TABLE_WIDTH + 2 * WALL_THICKNESS,
                                       TABLE_HEIGHT + 2 * WALL_THICKNESS));
        createInitialBalls();
        createHole();
        
        addMouseListener(this);
        addMouseMotionListener(this);
        theLabel.setForeground(Color.WHITE);
        theLabel2.setForeground(Color.WHITE);
        theLabel3.setForeground(Color.WHITE);
        add(theLabel);
        add(theLabel2);
        add(theLabel3);
        theLabel.setBounds(0, 600, 20, 20);
        theLabel2.setBounds(50, 600, 20, 20);
        theLabel3.setBounds(50,600,20,20);
        theLabel.setText("Player 1: " + String.valueOf(PLAYER1_POINTS));
        theLabel2.setText("Player 2: " + String.valueOf(PLAYER2_POINTS));
        theLabel3.setText("Player 1 tur ");
        
        add(button, BorderLayout.SOUTH);
        button.addActionListener(this);
        button.setVisible(true);


        simulationTimer = new Timer((int) (1000.0 / Biljard.UPDATE_FREQUENCY), this);
    }

    private void createInitialBalls(){
        final Coord whiteInitialPosition = new Coord(150, 100);
        final Coord secondInitialPosition = new Coord(150, 400);
        final Coord thirdInitialPosition = new Coord(150-Ball.RADIUS-2, 400-2*Ball.RADIUS-2);
        final Coord fourthInitialPosition = new Coord(150+Ball.RADIUS+2, 400-2*Ball.RADIUS-2);
        final Coord fifthInitialPosition = new Coord(150-2*Ball.RADIUS-2, 400-4*Ball.RADIUS-2);
        final Coord sixthInitialPosition = new Coord(150+2*Ball.RADIUS+2, 400-4*Ball.RADIUS-2);
        final Coord seventhInitialPosition = new Coord(150, 400-4*Ball.RADIUS-2);
        final Coord eigthInitialPosition = new Coord(150-3*Ball.RADIUS-4, 400-6*Ball.RADIUS-2);
        final Coord ninthInitialPosition = new Coord(150-Ball.RADIUS-2, 400-6*Ball.RADIUS-2);
        final Coord tenthInitialPosition = new Coord(150+Ball.RADIUS+2, 400-6*Ball.RADIUS-2);
        final Coord eleventhInitialPosition = new Coord(150+3*Ball.RADIUS+4, 400-6*Ball.RADIUS-2);
        whiteBall = new Ball(whiteInitialPosition, this);
        otherBalls[0] = new Ball(secondInitialPosition, this);
        otherBalls[1] = new Ball(thirdInitialPosition, this);
        otherBalls[2] = new Ball(fourthInitialPosition, this);
        otherBalls[3] = new Ball(fifthInitialPosition, this);
        otherBalls[4] = new Ball(sixthInitialPosition, this);
        otherBalls[5] = new Ball(seventhInitialPosition, this);
        otherBalls[6] = new Ball(eigthInitialPosition, this);
        otherBalls[7] = new Ball(ninthInitialPosition, this);
        otherBalls[8] = new Ball(tenthInitialPosition, this);
        otherBalls[9] = new Ball(eleventhInitialPosition, this);
    }
    private void createHole(){
        holePos [1]= new Coord(TABLE_WIDTH + WALL_THICKNESS,TABLE_HEIGHT+WALL_THICKNESS);
        holePos [2] = new Coord(WALL_THICKNESS,WALL_THICKNESS);
        holePos [3]= new Coord(TABLE_WIDTH + WALL_THICKNESS,WALL_THICKNESS);
        holePos [4] = new Coord(WALL_THICKNESS,TABLE_HEIGHT+WALL_THICKNESS);
        holePos [5] = new Coord(TABLE_WIDTH/2 + WALL_THICKNESS,WALL_THICKNESS);
        holePos [0] = new Coord(TABLE_WIDTH/2 + WALL_THICKNESS ,TABLE_HEIGHT +WALL_THICKNESS);
        
        for (int i = 0; i < 6; i++) {
            holes[i] = new Hole(holePos[i]);
        }
    }
    void scoreCount() {
        if (turn == true) {
            PLAYER1_POINTS ++;
            theLabel.setText(String.valueOf(PLAYER1_POINTS));
            theLabel.setText("Player 1: " + String.valueOf(PLAYER1_POINTS));

        } else {
            PLAYER2_POINTS ++;
            theLabel2.setText(String.valueOf(PLAYER2_POINTS));
            theLabel2.setText("Player 2: " + String.valueOf(PLAYER2_POINTS));

        }
    }
    
    public void actionPerformed(ActionEvent e) {  // Timer event
            whiteBall.move(otherBalls, holes);
            repaint();
            for (int i = 0; i < AMOUNT_BALLS; i++) {
                
                otherBalls[i].move(otherBalls, holes);

                repaint();

            }
            if (whiteBall.isSomethingMoving(otherBalls)) {

                simulationTimer.stop();
                //turn = !turn;
                if (turn == true) {
                    theLabel3.setText("Player 1 tur");
                } else {
                    theLabel3.setText("Player 2 tur");
                }
            }
            if (e.getSource() == button) {
                createInitialBalls();
                PLAYER1_POINTS = 0;
                PLAYER2_POINTS = 0;
                theLabel.setText("Player 1: " + String.valueOf(PLAYER1_POINTS));
                theLabel2.setText("Player 2: " + String.valueOf(PLAYER2_POINTS));
                theLabel3.setText("Player 1 tur ");
                turn = true;
            }
    }

    public void mousePressed(MouseEvent event) {
        Coord mousePosition = new Coord(event);
        if (whiteBall.isSomethingMoving(otherBalls)) {
            if (whiteBall.DEAD != true){
                whiteBall.setAimPosition(mousePosition);
                repaint();     //  To show aiming line
            } else if(whiteBall.allPosEmpty(mousePosition, otherBalls, holes)){

                whiteBall.position = mousePosition;
                whiteBall.velocity = Coord.zero();
                whiteBall.DEAD = false;
                repaint();
                    
                
            }
        }
    }

    public void mouseReleased(MouseEvent e) {
        whiteBall.shoot();
        if (!simulationTimer.isRunning()) {
            simulationTimer.start();      
        }
    }

    public void mouseDragged(MouseEvent event) {
        Coord mousePosition = new Coord(event);
        whiteBall.updateAimPosition(mousePosition);
        repaint();
    }

    // Obligatory empty listener methods
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
    
    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2D = (Graphics2D) graphics;
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // This makes the graphics smoother
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2D.setColor(WALL_COLOR);
        g2D.fillRect(0, 0, TABLE_WIDTH + 2 * WALL_THICKNESS, TABLE_HEIGHT + 2 * WALL_THICKNESS);

        g2D.setColor(COLOR);
        g2D.fillRect(WALL_THICKNESS, WALL_THICKNESS, TABLE_WIDTH, TABLE_HEIGHT);
        
        int localDeaths = 0;
        whiteBall.COLOR = Color.WHITE;
        whiteBall.paint(g2D);
        for (int i = 0; i < AMOUNT_BALLS; i++) {
            if (otherBalls[i].DEAD != true) {
                otherBalls[i].COLOR = Color.RED;
                otherBalls[i].paint(g2D);
                
            } else {
                otherBalls[i].position = Coord.zero();
                otherBalls[i].COLOR = Color.BLACK;
                otherBalls[i].velocity = Coord.zero();
                otherBalls[i].paint(g2D);


            }
        }
        for (int i = 0; i < AMOUNT_HOLES; i++) {
            holes[i].paint(g2D);
        }
    }
}// end class Table

/**
 * ****************************************************************************************
 * Ball:
 *
 * The ball has instance variables relating to its graphics and game state:
 * position, velocity, and the position from which a shot is aimed (if any).
 * 
 */
class Ball {

    static Color  COLOR               = Color.white;
    private final int    BORDER_THICKNESS    = 2;
    static final double RADIUS              = 15;
    private final double DIAMETER            = 2 * RADIUS;
    private final double FRICTION            = 0.015;                          // its friction constant (normed for 100 updates/second)
    private final double FRICTION_PER_UPDATE =                                 // friction applied each simulation step
                          1.0 - Math.pow(1.0 - FRICTION,                       // don't ask - I no longer remember how I got to this
                                         100.0 /Biljard.UPDATE_FREQUENCY);           
    Coord position;
    Coord velocity;
    private Coord aimPosition;              // if aiming for a shot, ow null
    Table theTable;
    boolean DEAD = false;
    
    Ball(Coord initialPosition, Table table) {
        theTable = table;
        position = initialPosition;
        velocity = Coord .zero();       // WARNING! Are initial velocities 
    }                                // clones or aliases? Is this important?

    private boolean isAiming() {
        return aimPosition != null;
    }
    
    boolean isMoving() {    // if moving too slow I am deemed to have stopped
        return velocity.magnitude() > FRICTION_PER_UPDATE;
        
    }
    
    boolean isSomethingMoving(Ball[] movingBalls) {
        for (int i = 0; i < movingBalls.length ; i++) {
            if (movingBalls[i].isMoving() || theTable.whiteBall.isMoving()) {
            return false;
            }
        }
        return true;
    }
    
    /// returnerar true ifall man trycker på en annan boll eler hål när man placeraar ut vita bollen. 
    boolean posEmpty(Coord mousePos) {
        if (Coord.distance(position, mousePos) > 2*RADIUS && Coord.distance(position, mousePos) > RADIUS + Hole.HOLE_RADIUS) {
            return true;
        } else {
            return false;
        }
    }
    // kollar där mann trycker så att den vita billen inte kan placeras på de andra bollarna, skickas in i den öer
    boolean allPosEmpty (Coord mousePos, Ball[] allBalls, Hole[] theHoles) {
        for (int i= 0; i < allBalls.length; i++) {
            if (!allBalls[i].posEmpty(mousePos)) {
                return false;
            }
        }
        for (int k=0; k < theHoles.length; k++){
            if (!theHoles[k].holePosEmpty(mousePos)) {
                return false;
            }
        }
        if (mousePos.x + RADIUS < theTable.TABLE_WIDTH + theTable.WALL_THICKNESS && mousePos.x + RADIUS > theTable.WALL_THICKNESS
            && mousePos.y + RADIUS < theTable.TABLE_HEIGHT + theTable.WALL_THICKNESS && mousePos.y + RADIUS > theTable.WALL_THICKNESS) {
            return true;
        }
        return false;
    }
    
    void setAimPosition(Coord grabPosition) {
        if (Coord.distance(position, grabPosition) <= RADIUS) {
            aimPosition = grabPosition;
        }
    }
  
    
    void updateBallPosition(Coord ballPos) {
                this.position = ballPos;
                this.velocity = Coord.zero();


    }

    void updateAimPosition(Coord newPosition) {
        if (isAiming()){
            aimPosition = newPosition;
        }
    }

    void shoot() {
        if (isAiming()) {
            Coord aimingVector = Coord.sub(position, aimPosition);
            velocity = Coord.mul(Math.sqrt(10.0 * aimingVector.magnitude() / Biljard.UPDATE_FREQUENCY),
                                 aimingVector.norm());  // don't ask - determined by experimentation
            aimPosition = null;
        }
    }
    
    
    void move(Ball[] ball, Hole[] hole) {
         if (isMoving() && this.DEAD != true) {                                   
            position.increase(velocity);  
            velocity.decrease(Coord.mul(FRICTION_PER_UPDATE, velocity.norm()));
            boolean correctMove = false;
            for (Hole hole1 : hole) {
                if(detectHoleCollision(hole1)){
                    correctMove = true;
                }
            }
            
            if(!correctMove) {
                theTable.turn = !theTable.turn;
            }
            for (Ball pop1 : ball) {

                // Så att de iinte krockar med sig själva 
            if (pop1 != this) {

                detectCollision(pop1);
                detectCollision(theTable.whiteBall);
            }

            detectWallCollision();
                                                  
            }
    }
}
    
    // paint: to draw the ball, first draw a black ball
    // and then a smaller ball of proper color inside
    // this gives a nice thick border
    void paint(Graphics2D g2D) {
        g2D.setColor(Color.black);
        g2D.fillOval(
                (int) (position.x - RADIUS + 0.5),
                (int) (position.y - RADIUS + 0.5),
                (int) DIAMETER,
                (int) DIAMETER);
        g2D.setColor(COLOR);
        g2D.fillOval(
                (int) (position.x - RADIUS + 0.5 + BORDER_THICKNESS),
                (int) (position.y - RADIUS + 0.5 + BORDER_THICKNESS),
                (int) (DIAMETER - 2 * BORDER_THICKNESS),
                (int) (DIAMETER - 2 * BORDER_THICKNESS));
        if (isAiming()) {
            paintAimingLine(g2D);
        }
    }
    
    private void paintAimingLine(Graphics2D graph2D) {
            Coord.paintLine(
                    graph2D,
                    aimPosition, 
                    Coord.sub(Coord.mul(2, position), aimPosition)
                           );
    } 
    void detectWallCollision (){
        if (position.x + RADIUS > theTable.TABLE_WIDTH + theTable.WALL_THICKNESS && this.velocity.x > 0) {
                this.collisionWallSide();
        }
        if (position.x - RADIUS < theTable.WALL_THICKNESS && this.velocity.x < 0) {
            this.collisionWallSide();
        }
        if (position.y + RADIUS > theTable.TABLE_HEIGHT + theTable.WALL_THICKNESS && this.velocity.y > 0) {
            this.collisionWallTop();
        }
        if (position.y - RADIUS < theTable.WALL_THICKNESS && this.velocity.y < 0) {
            this.collisionWallTop();
        }
        
    }
    boolean detectHoleCollision (Hole hole) {
        if (Coord.distance(hole.position, this.position) <= hole.HOLE_RADIUS) {
            this.DEAD = true;
            this.velocity = Coord.zero();
            if (this != theTable.whiteBall){
                theTable.scoreCount();
                return true;
            } else {
                theTable.turn = !theTable.turn;
                return true;

            }
            
        } else {
            return false;
        }

    }
    
    void collisionWallSide () {
        velocity.x = - velocity.x;
        velocity.y = velocity.y;
    }

    void collisionWallTop () {
        velocity.x = velocity.x;
        velocity.y = - velocity.y;  
    }

    
    
    void detectCollision(Ball ball) {
       double xCol = this.position.x - ball.position.x;
       double yCol = this.position.y - ball.position.y;
       double rad = 2*RADIUS+BORDER_THICKNESS;
       double ballRad = Math.sqrt((xCol * xCol) + (yCol * yCol));
       
       if (ballRad <= rad && willCollide(ball)) {
           collisionBall(ball);
       } 
       
       
    }
    
    boolean willCollide(Ball ball) {
        return Coord.distance(Coord.add(position, velocity), Coord.add(ball.position, ball.velocity)) < Coord.distance(position, ball.position);
    }
    
    void collisionBall(Ball ball) {
        double dx = this.position.x - ball.position.x;
        double dy = this.position.y - ball.position.y;
        double dxx = dx / Math.sqrt(Math.pow(dx,2)+Math.pow(dy, 2));
        double dyy = dy / Math.sqrt(Math.pow(dx,2)+Math.pow(dy,2));
        double impuls = ((ball.velocity.x*dxx) + (ball.velocity.y*dyy)) - ((this.velocity.x*dxx) + (this.velocity.y*dyy));
        this.velocity.x = this.velocity.x + (impuls*dxx);
        this.velocity.y = this.velocity.y + (impuls*dyy);
        ball.velocity.x = ball.velocity.x - (impuls*dxx);
        ball.velocity.y = ball.velocity.y - (impuls*dyy);
    }
}
// end  class Ball  




class Hole {
    static int HOLE_RADIUS = 30;
    int HOLE_DIAMETER = HOLE_RADIUS*2;
    Coord position;
    
    
    Hole(Coord initialPosition) {
        position = initialPosition;
    }
    
    // KOllar igwn vart man placerar ut bollen 
    boolean holePosEmpty(Coord mousePos) {
        if (Coord.distance(position, mousePos) > 2*RADIUS && Coord.distance(position, mousePos) > RADIUS + Hole.HOLE_RADIUS) {
            return true;
        } else {
            return false;
        }
    }
    
    void paint(Graphics2D g2D) {
        g2D.setColor(Color.black);
        g2D.fillOval(
            (int) (position.x - HOLE_RADIUS + 0.5),
            (int) (position.y - HOLE_RADIUS + 0.5),
            (int) HOLE_DIAMETER,
            (int) HOLE_DIAMETER);
    }
}
