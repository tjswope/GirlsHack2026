// Class: GraphicsPanel
// Written by: Mr. Swope
// Date: 1/27/2020
// Description: This class is the main class for this project.  It extends the Jpanel class and will be drawn on
// 				on the JPanel in the GraphicsMain class.
//
// Edited by: Jacqueline Bellaria, Lucy Meyer, Valentina Hernandez, Ellie Wu
// Date: 01/13/2026
// Description: Main class for the game, 2048. Use the arrow keys to move the blocks so like numbers collide, 
//				forming the largest number possible before space runs out.
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
	
	public int sumArray(int[] arr) {
		int sum = 0;
		for (int i : arr) {
			sum += i;
		}
		return sum;
	}
	
	public void animate() {
		// start with the second from last row/column depending on direction
			// see if the blocks there can move in the right direction
			// or combine, combine = true, score update, image update.
		// then go to next row/column, then last
		
		// after loop:
			// loop through and set combine = false
			// addRandomBlock()
		
		while (sumArray(start) >= 0 && sumArray(start) < 4) { // has not yet shifted through all the rows/columns
			if (start[0] != 0 && start[1] == 0) { // moving up/down
				for (int c = 0; c < board[start[0]].length; c++) { // shifting through the blocks in a row
					if (board[start[0]][c] != null) { // makes sure not trying to move null space
						int end = start[0]; // updating what row block is on
						
						while (end + direction[0] < board.length 
								&& end + direction[0] >= 0 
								&& board[end + direction[0]][c] == null) {
							end += direction[0]; // moves to empty spaces
						}
						
						// updates block location here
						if (end + direction[0] < board.length 
								&& end + direction[0] >= 0 
								&& board[end + direction[0]][c] != null 
								&& board[end + direction[0]][c].getValue() == board[start[0]][c].getValue()
								&& !board[end + direction[0]][c].getCombined()) { // still block hasn't yet been combined
							// collision
							// need animation here
							board[end + direction[0]][c].doubleValue();
							board[end + direction[0]][c].setCombined(true);
							// update graphics location
							board[end + direction[0]][c].setY(14 + 122 * (end + direction[0]));
							
							score += board[end + direction[0]][c].getValue();
							board[start[0]][c] = null;
						} else if (end != start[0]) {
							// need animation here
							board[end][c] = board[start[0]][c];
							// update graphics location
							board[end][c].setY(14 + 122 * end);
							
							board[start[0]][c] = null;
						}
					}
				}
			} else if (start[0] == 0 && start[1] != 0) { // moving left/right
				for (int r = 0; r < board.length; r++) { // shifting through the blocks in a column
					if (board[r][start[1]] != null) { // makes sure not trying to move null space
						int end = start[1]; // updating what column block is on
						
						while (end + direction[1] < board[r].length 
								&& end + direction[1] >= 0 
								&& board[r][end + direction[1]] == null) {
							end += direction[1]; // moves to empty space
						}
						
						// updates block location here
						if (end + direction[1] < board[r].length 
								&& end + direction[1] >= 0 
								&& board[r][end + direction[1]] != null 
								&& board[r][end + direction[1]].getValue() == board[r][start[1]].getValue() 
								&& !board[r][end + direction[1]].getCombined()) { // still block hasn't yet been combined
							// collision
							// need animation here
							board[r][end + direction[1]].doubleValue();
							board[r][end + direction[1]].setCombined(true);
							// update graphics location
							board[r][end + direction[1]].setX(14 + 122 * (end + direction[1]));
							
							score += board[r][end + direction[1]].getValue();
							board[r][start[1]] = null;
						} else if (end != start[1]) {
							// need animation here
							board[r][end] = board[r][start[1]];
							// update graphics location
							board[r][end].setX(14 + 122 * end);
							
							board[r][start[1]] = null;
						}
					}
				}
			}
			
			start[0] -= direction[0]; // moving down/up a row/column
			start[1] -= direction[1];
		}
		
		// setting all combined values to false for next play.
		for (int r = 0; r < board.length; r++) {
			for (int c = 0; c < board[r].length; c++) {
				if (board[r][c] != null && board[r][c].getCombined()) {
					board[r][c].setCombined(false);
				}
			}
		}
		
		start[0] = 0;
		start[1] = 0;
		moves++;
		addRandomBlock();
		
		// in the areas with animation will want the repaint() there. 
		// Maybe after a full row/column has moved in logic so they all move at once?
		// Don't know how to make that happen though
		repaint();
	}
	
	public void move(int d) {
		if (d == 1) { // up
			for (int r = 1; r < board.length; r++) { // looping through rows 1, 2, & 3
				for (int c = 0; c < board[r].length; c++) {
					if (board[r][c] != null) {
						int endRow = r;
						
						while (endRow + direction[0] >= 0 && board[endRow + direction[0]][c] == null) {
							endRow += direction[0]; // moves to empty spaces
						}
						
						if (endRow + direction[0] >= 0 
								&& board[endRow + direction[0]][c] != null 
								&& board[endRow + direction[0]][c].getValue() == board[r][c].getValue() 
								&& !board[endRow + direction[0]][c].getCombined()) { // collision
							// collision
							// need animation here
							board[endRow + direction[0]][c].doubleValue();
							board[endRow + direction[0]][c].setCombined(true);
							// update graphics location
							// shouldn't need to update Y b/c staying in same place
							
							score += board[endRow + direction[0]][c].getValue();
							board[r][c] = null;
						} else if (endRow != r) { // no collision
							// need animation here
							board[endRow][c] = board[r][c];
							// update graphics location
							board[endRow][c].setY(14 + 122 * endRow);
							
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
							// need animation here
							board[endRow + direction[0]][c].doubleValue();
							board[endRow + direction[0]][c].setCombined(true);
							// update graphics location
							// shouldn't need to update Y b/c staying in same place
							
							score += board[endRow + direction[0]][c].getValue();
							board[r][c] = null;
						} else if (endRow != r) { // no collision
							// need animation here
							board[endRow][c] = board[r][c];
							// update graphics location
							board[endRow][c].setY(14 + 122 * endRow);
							
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
							// need animation here
							board[r][endCol + direction[1]].doubleValue();
							board[r][endCol + direction[1]].setCombined(true);
							// update graphics location
							// shouldn't need to update X b/c staying in same place
							
							score += board[r][endCol + direction[1]].getValue();
							board[r][c] = null;
						} else if (endCol != c) { // no collision
							// need animation here
							board[r][endCol] = board[r][c];
							// update graphics location
							board[r][endCol].setX(14 + 122 * endCol);
							
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
							// need animation here
							board[r][endCol + direction[1]].doubleValue();
							board[r][endCol + direction[1]].setCombined(true);
							// update graphics location
							// shouldn't need to update X b/c staying in same place
							
							score += board[r][endCol + direction[1]].getValue();
							board[r][c] = null;
						} else if (endCol != c) { // no collision
							// need animation here
							board[r][endCol] = board[r][c];
							// update graphics location
							board[r][endCol].setX(14 + 122 * endCol);
							
							board[r][c] = null;
						}
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
		
		// in the areas with animation will want the repaint() there. 
		// Maybe after a full row/column has moved in logic so they all move at once?
		// Don't know how to make that happen though
		repaint();
	}
	
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
		
		// TEST
		System.out.println("Empty: ");
		for (int[] place : empty) {
			System.out.print("(");
			for (int i : place) {
				System.out.print(i + " ");
			}
			System.out.print(") ");
		}
		System.out.println("");
		// TEST
		
		// game over code
		if (empty.isEmpty()) {
			gameOver = true;
			System.exit(0);
		}
		
		// add new block
		int[] add = empty.get((int) (Math.random() * empty.size()));
		int value = 0;
		if (Math.random() >= 0.9) value = 4;
		else value = 2;
		board[add[0]][add[1]] = new Block(14 + add[1] * 122, 14 + add[0] * 122, value, 1);
	}
	
    // TEST
	public void printBoard() {
		for (int r = 0; r < board.length; r++) {
			for (int c = 0; c < board[r].length; c++) {
				if (board[r][c] != null) System.out.print(board[r][c].getValue() + " ");
				else System.out.print("0 ");
			}
			System.out.println("");
		}
	}
    // TEST
	
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
		
		// TEST
		printBoard();
		// TEST
	}
	@Override public void keyTyped(KeyEvent e) {}
	@Override public void keyReleased(KeyEvent e) {}
}
