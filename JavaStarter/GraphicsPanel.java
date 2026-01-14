// Class: GraphicsPanel
// Written by: Mr. Swope
// Date: 1/27/2020
// Description: This class is the main class for this project.  It extends the Jpanel class and will be drawn on
// 				on the JPanel in the GraphicsMain class.
//
// Edited by: Jacqueline Bellaria, Jessie Liao, Nathaniel Gao, Yicheng Long
// Date: 3/11/2025
// Description: Main class for the game, Dino Doom. A player pilots a dinosaur to destroy as many meteors as possible, 
//              all while avoiding getting hit by the meteors. The dinosaur also has to eat pigs to replenish it's hunger,
//              or else it will starve to death.
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.Timer;
public class GraphicsPanel extends JPanel implements KeyListener{
	private Timer Timer;					// The bTimer is used to move objects at a consistent bTime interval.
	
	private int score; // score for how many meteors destroyed
	private int moves;
	private int scale; 
	private int[] direction; // direction that blocks are moving
	private int[] start; // indicating starting row/column that must move first
	private Block[][] board;
	
	private Background background;	// background
	
	
	
	private boolean gameOver; // if the game is over
	
	
	public GraphicsPanel(){
		background = new DesertBackground();
		// establishes the beginning.
		score = 0;
		moves = 0;
		direction = new int[2];
		start = new int[2];
		board = new Block[4][4];
		// initialize initial board here -> 2 random blocks
		addRandomBlock();
		addRandomBlock();
		
		// TEST
		//board[2][2] = new Block(14 + 2 * 122, 14 + 2 * 122, 2, 1);
		// TEST
		
		scale = 1; // size of board / Blocks
		
		// This line of code sets the dimension of the panel equal to the dimensions
		// of the background image.
		setPreferredSize(new Dimension(background.getWidth(),background.getHeight()));									
		
		Timer = new Timer(5, new ClockListener(this));   // This object will call the ClockListener's
													 	 // action performed method every 5 milliseconds once the
													 	 // bTimer is started. You can change how frequently this
													 	 // method is called by changing the first parameter.
		//Timer.start();
		this.setFocusable(true);					     // for keylistener
		this.addKeyListener(this);
	
	    
	    gameOver = false;
	}
	
	// method: paintComponent
	// description: This method will paint the items onto the graphics panel.  This method is called when the panel is
	//   			first rendered.  It can also be called by this.repaint(). You'll want to draw each of your objects.
	//				This is the only place that you can draw objects.
	// parameters: Graphics g - This object is used to draw your images onto the graphics panel.
	public void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D) g;
		
		// draws the background of the video game
		background.draw(this, g);
		
		
		//draw words, first sets font, then color
		Font stringFont = new Font( "SansSerif", Font.BOLD, 20 );
	    g.setFont(stringFont);
		g.setColor(Color.BLACK);
		// prints the score of the player (based on how many meteors they shoot)
		g.drawString("Score: " + score, 20, 55);
		
		
		// draw all the blocks
		for (int r = 0; r < board.length; r++) {
			for (int c = 0; c < board[r].length; c++) {
				if (board[r][c] != null) board[r][c].draw(g2, this);
			}
		}
		

	}
	
	// method:clock
	// description: This method is called by the clocklistener every 5 milliseconds.  You should update the coordinates
	//				of one of your characters in this method so that it moves as bTime changes.  After you update the
	//				coordinates you should repaint the panel.
	public void clock(){
		// step-by-step animation will occur here
		this.repaint();
	}
	
	public void move(int d) {
		if (d == 1) { // up
			for (int r = 1; r < board.length; r++) { // looping through rows 1, 2, & 3
				for (int c = 0; c < board[r].length; c++) {
					if (board[r][c] != null) {
						// move the blocks until they collide with something, stop at a different numbered block, or hit the walls
						// if they collide, the formed block can't collide with anything else
						// score doubles during collision
					}
				}
			}
		} else if (d == 2) { // down
			for (int r = board.length - 2; r >= 0; r--) { // looping through rows 2, 1, & 0
				for (int c = 0; c < board[r].length; c++) {
					if (board[r][c] != null) {
						// move the blocks until they collide with something, stop at a different numbered block, or hit the walls
						// if they collide, the formed block can't collide with anything else
						// score doubles during collision
					}
				}
			}
		} else if (d == 3) { // left
			for (int c = 1; c < board[0].length; c++) { // looping through columns 1, 2, & 3
				for (int r = 0; r < board.length; r++) {
					if (board[r][c] != null) {
						// move the blocks until they collide with something, stop at a different numbered block, or hit the walls
						// if they collide, the formed block can't collide with anything else
						// score doubles during collision
					}
				}
			}
		} else if (d == 4) { // right
			for (int c = board[1].length - 2; c >= 0; c--) { // looping through columns 2, 1, & 0
				for (int r = 0; r < board.length; r++) {
					if (board[r][c] != null) {
						// move the blocks until they collide with something, stop at a different numbered block, or hit the walls
						// if they collide, the formed block can't collide with anything else
						// score doubles during collision
					}
				}
			}
		}
		
		for (int r = 0; r < board.length; r++) { // resetting combined boolean for all blocks.
			for (int c = 0; c < board[r].length; c++) {
				if (board[r][c] != null && board[r][c].getCombined()) {
					board[r][c].setCombined(false);
				}
			}
		}
		
		moves++;
		addRandomBlock();
		
		repaint();
	}
	
	public void addRandomBlock() {
		// create 2d ArrayList of empty spaces on board
		ArrayList<int[]> empty = new ArrayList<>();
		
		// adds empty spaces to ArrayList
		// loop through the board and add the null spaces to empty
		
		
		// game over code
		if (empty.isEmpty()) {
			gameOver = true;
			System.exit(0);
		}
		
		// add new block
		int[] add = empty.get((int) (Math.random() * empty.size()));
		int value = 0;
		
		// set value of new block, 2 = 90%, 4 = 10%
		value = 2;
		
		board[add[0]][add[1]] = new Block(14 + add[1] * 122, 14 + add[0] * 122, value, 1);
	}
	
	// method: keyPressed()
	// description: This method is called when a key is pressed. You can determine which key is pressed using the
	//				KeyEvent object.  For example if(e.getKeyCode() == KeyEvent.VK_LEFT) would test to see if
	//				the left key was pressed.
	// parameters: KeyEvent e
	@Override public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_UP) {
        	direction[0] = -1;
        	start[0] = 1;
			System.out.println("up");
			move(1);
        }
        else if (keyCode == KeyEvent.VK_DOWN) {
        	direction[0] = 1;
        	start[0] = 2;
			System.out.println("down");
			move(2);
        }
        else if (keyCode == KeyEvent.VK_LEFT) {
        	direction[1] = -1;
        	start[1] = 1;
			System.out.println("left");
			move(3);
        }
        else if (keyCode == KeyEvent.VK_RIGHT) {
        	direction[1] = 1;
        	start[1] = 2;
			System.out.println("right");
			move(4);
        }
		
	}
	@Override public void keyTyped(KeyEvent e) {}
	@Override public void keyReleased(KeyEvent e) {}
}
