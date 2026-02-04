
// Class: GraphicsPanel
// Written by: Mr. Swope
// Date: 1/27/2020
// Description: This class is the main class for this project.  It extends the Jpanel class and will be drawn on
// 				on the JPanel in the GraphicsMain class.
//
// Edited by: Jacqueline, Valentina, Lucy, Ellie
// Date: 01/30/2025
// Description: Main class for the game, 2048. 
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
	private Timer Timer;					// The Timer is used to move objects at a consistent bTime interval.
	
	private int score; // score for how many meteors destroyed
	private int moves;
	private int[] direction; // direction that blocks are moving
	private Block[][] board;
	private Block[][] cBoard; // board with the blocks that are combining
	
	private int width; 
	private int buffer;
	private Background background;	// background
	
	public GraphicsPanel(){
		// establishes the size of the board and blocks based on block width
		width = 106; // size of Blocks in px. Width = height, 106 = default
		background = new Background(width * 500/106); // change numerator of fraction for board size, 500 = default
		buffer = (int)(width * 15/106); // change numerator of fraction, 15 = default
		
		// establishes the beginning.
		score = 0;
		moves = 0;
		direction = new int[2];
		board = new Block[4][4];
		cBoard = new Block[4][4];
		// initialize initial board here -> 2 random blocks
		addRandomBlock();
		addRandomBlock();
		
		
		
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
		Font stringFont = new Font("SansSerif", Font.PLAIN, 30);
	    g.setFont(stringFont);
		g.setColor(Color.BLACK);
		// prints the score of the player (based on how many meteors they shoot)
		g.drawString("" + score, 250, 529);
		
		
		// draw all the blocks
		for (int r = 0; r < board.length; r++) {
			for (int c = 0; c < board[r].length; c++) { 
				if (cBoard[r][c] != null) cBoard[r][c].draw(g2, this); // draw combining blocks first
				if (board[r][c] != null) board[r][c].draw(g2, this);
			}
		}
		

	}
	
	// method:clock
	// description: This method is called by the clocklistener every 5 milliseconds.  You should update the coordinates
	//				of one of your characters in this method so that it moves as bTime changes.  After you update the
	//				coordinates you should repaint the panel.
	public void clock(){
		boolean isRunning = false;
		for (int r = 0; r < board.length; r++) {
			for (int c = 0; c < board[r].length; c++) {
				if ((board[r][c] != null && board[r][c].getMoving()) || cBoard[r][c] != null) isRunning = true;
				if (board[r][c] != null 
						&& board[r][c].getMoving() 
						&& (board[r][c].getX() != (buffer + (width + buffer) * c) 
						|| board[r][c].getY() != (buffer + (width + buffer) * r))) {
					// if the block exists, is moving, isn't colliding, isn't in the right position
					// one space moved = 121 px, 11 * 11
					board[r][c].setX(board[r][c].getX() + direction[1] * 11);
					board[r][c].setY(board[r][c].getY() + direction[0] * 11);
				} else if (cBoard[r][c] != null 
						&& (cBoard[r][c].getX() != board[r][c].getX() 
						|| cBoard[r][c].getY() != board[r][c].getY())) {
					// if the block exists, is colliding, and isn't in the right position 
					cBoard[r][c].setX(cBoard[r][c].getX() + direction[1] * 11);
					cBoard[r][c].setY(cBoard[r][c].getY() + direction[0] * 11);
				} else if (cBoard[r][c] != null
						&& cBoard[r][c].getX() == (buffer + (width + buffer) * c) 
						&& cBoard[r][c].getY() == (buffer + (width + buffer) * r)) {
					// if the block exists, is moving, is colliding, and is in the right location
					cBoard[r][c].setMoving(false);
					board[r][c].doubleValue(); // technically should set combined = false, but just in case:
					score += board[r][c].getValue();
					cBoard[r][c] = null;
				} else if (board[r][c] != null 
						&& board[r][c].getMoving() 
						&& board[r][c].getX() == (buffer + (width + buffer) * c) 
						&& board[r][c].getY() == (buffer + (width + buffer) * r)) {
					// if the block exists, is moving, isn't colliding, and is in the right location
					board[r][c].setMoving(false);
				}
			}
		}
		
		if (!isRunning) {
			Timer.stop();
			addRandomBlock();
			direction[0] = 0;
			direction[1] = 0;
			for (int r = 0; r < board.length; r++) {
				for (int c = 0; c < board[r].length; c++) {
					if (board[r][c] != null) board[r][c].setCombined(false);
				}
			}
		}
		this.repaint();
	}
	
	// Method: move
	// Description: the logic portion of how the blocks move. Will move all the blocks and collide as needed.
	// Parameters: int d, the direction.
	// Return: N/A
	public void move(int d) {
		if (d == 1) { // up
			for (int r = 1; r < board.length; r++) { // looping through rows 1, 2, & 3
				for (int c = 0; c < board[r].length; c++) {
					if (board[r][c] != null) {
						int endRow = r;
						
						while (endRow + direction[0] >= 0 && board[endRow + direction[0]][c] == null) {
							endRow += direction[0]; // moves to empty spaces
						}
						//
						if (endRow + direction[0] >= 0 
								&& board[endRow + direction[0]][c] != null 
								&& board[endRow + direction[0]][c].getValue() == board[r][c].getValue() 
								&& !board[endRow + direction[0]][c].getCombined()) { // collision
							// if block can move up, and there is a block above it, and the block above it has = value & hasn't been combined.
							board[endRow + direction[0]][c].setCombined(true);
							board[r][c].setMoving(true);
							cBoard[endRow + direction[0]][c] = board[r][c]; // sets shadow block so that it can move
							board[r][c] = null;
						} else if (endRow != r) { // no collision
							// block is moving
							board[r][c].setMoving(true);
							// block is at new position on array
							board[endRow][c] = board[r][c];
							// old position is empty
							board[r][c] = null;
						}
					}
				}
			}
		} else if (d == 2) { // down
			for (int r = board.length - 2; r >= 0; r--) { // looping through rows 2, 1, & 0
				for (int c = 0; c < board[r].length; c++) {
					if (board[r][c] != null) {
						int endRow = r;
						
						while (endRow + direction[0] < board.length && board[endRow + direction[0]][c] == null) {
							endRow += direction[0]; // moves to empty spaces
						}
						
						if (endRow + direction[0] < board.length 
								&& board[endRow + direction[0]][c] != null 
								&& board[endRow + direction[0]][c].getValue() == board[r][c].getValue() 
								&& !board[endRow + direction[0]][c].getCombined()) { // collision
							// collision
							board[endRow + direction[0]][c].setCombined(true);
							board[r][c].setMoving(true);
							cBoard[endRow + direction[0]][c] = board[r][c]; // sets shadow block so that it can move
							board[r][c] = null;
						} else if (endRow != r) { // no collision
							// block is moving
							board[r][c].setMoving(true);
							// block is at new position on array
							board[endRow][c] = board[r][c];
							// old position is empty
							board[r][c] = null;
						}
					}
				}
			}
		} else if (d == 3) { // left
			for (int c = 1; c < board[0].length; c++) { // looping through columns 1, 2, & 3
				for (int r = 0; r < board.length; r++) {
					if (board[r][c] != null) {
						int endCol = c;
						
						while (endCol + direction[1] >= 0 && board[r][endCol + direction[1]] == null) {
							endCol += direction[1]; // moves to empty spaces
						}
						
						if (endCol + direction[1] >= 0 
								&& board[r][endCol + direction[1]] != null 
								&& board[r][endCol + direction[1]].getValue() == board[r][c].getValue() 
								&& !board[r][endCol + direction[1]].getCombined()) { // collision
							// collision
							board[r][endCol + direction[1]].setCombined(true);
							board[r][c].setMoving(true);
							cBoard[r][endCol + direction[1]] = board[r][c]; // sets shadow block so that it can move
							board[r][c] = null;
						} else if (endCol != c) { // no collision
							// block is moving
							board[r][c].setMoving(true);
							// block is at new position on array
							board[r][endCol] = board[r][c];
							// old position is empty
							board[r][c] = null;
						}
					}
				}
			}
		} else if (d == 4) { // right
			for (int c = board[1].length - 2; c >= 0; c--) { // looping through columns 2, 1, & 0
				for (int r = 0; r < board.length; r++) {
					if (board[r][c] != null) {
						int endCol = c;
						
						while (endCol + direction[1] < board[c].length && board[r][endCol + direction[1]] == null) {
							endCol += direction[1]; // moves to empty spaces
						}
						
						if (endCol + direction[1] < board[r].length 
								&& board[r][endCol + direction[1]] != null 
								&& board[r][endCol + direction[1]].getValue() == board[r][c].getValue() 
								&& !board[r][endCol + direction[1]].getCombined()) { // collision
							// collision
							board[r][endCol + direction[1]].setCombined(true);
							board[r][c].setMoving(true);
							cBoard[r][endCol + direction[1]] = board[r][c]; // sets shadow block so that it can move
							board[r][c] = null;
						} else if (endCol != c) { // no collision
							// block is moving
							board[r][c].setMoving(true);
							// block is at new position on array
							board[r][endCol] = board[r][c];
							// old position is empty
							board[r][c] = null;
						}
					}
				}
			}
		}
		
		moves++;
	}
	
	// Method: addRandomBlock
	// Description: finds all the empty spaces in the array and randomly adds a block to one of the empty spaces. 
	// 				The block has 90% chance of being a 2, 10% chance of being a 4.
	// Parameters: N/A
	// Return: N/A
	public void addRandomBlock() {
		// create 2d ArrayList of empty spaces on board
		ArrayList<int[]> empty = new ArrayList<>();
		
		// adds empty spaces to ArrayList
		for (int r = 0; r < board.length; r++) {
			for (int c = 0; c < board[r].length; c++) {
				if (board[r][c] == null) {
					int[] e = {r, c};
					empty.add(e);
				}
			}
		}
		
		// game over code
		if (empty.isEmpty()) {
			System.exit(0);
		}
		
		// add new block
		int[] add = empty.get((int) (Math.random() * empty.size()));
		int value = 0;
		if (Math.random() >= 0.9) value = 4;
		else value = 2;
		// blocks height & width = 106 px, padding of 14 px.
		board[add[0]][add[1]] = new Block(buffer + add[1] * (width + buffer), buffer + add[0] * (width + buffer), value, width);
	}
	
	// method: keyPressed()
	// description: This method is called when a key is pressed. You can determine which key is pressed using the
	//				KeyEvent object.  For example if(e.getKeyCode() == KeyEvent.VK_LEFT) would test to see if
	//				the left key was pressed.
	// parameters: KeyEvent e
	@Override public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_UP && !Timer.isRunning()) {
        	direction[0] = -1;
			move(1);
			Timer.start();
        }
        else if (keyCode == KeyEvent.VK_DOWN && !Timer.isRunning()) {
        	direction[0] = 1;
			move(2);
			Timer.start();
        }
        else if (keyCode == KeyEvent.VK_LEFT && !Timer.isRunning()) {
        	direction[1] = -1;
			move(3);
			Timer.start();
        }
        else if (keyCode == KeyEvent.VK_RIGHT && !Timer.isRunning()) {
        	direction[1] = 1;
			move(4);
			Timer.start();
        }
	}
	@Override public void keyTyped(KeyEvent e) {}
	@Override public void keyReleased(KeyEvent e) {}
}
