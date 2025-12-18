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
public class GraphicsPanel extends JPanel implements KeyListener, MouseListener{
	private Timer Timer;					// The bTimer is used to move objects at a consistent bTime interval.
	
	private int score; // score for how many meteors destroyed
	private int moves;
	private int scale; 
	private boolean blockSelected;
	private boolean collide;
	private int[] direction;
	private int[] sBlock;
	private Block[][] board;
	
	private Background background;	// background
	
	
	
	private boolean gameOver; // if the game is over
	
	
	public GraphicsPanel(){
		background = new DesertBackground();
		// establishes the beginning.
		score = 0;
		moves = 0;
		blockSelected = false;
		collide = false;
		direction = new int[2];
		sBlock = new int[2];
		sBlock[0] = -1;
		sBlock[1] = -1;
		board = new Block[4][4];
		// initialize initial board here -> 2 random blocks
		addRandomBlock();
		addRandomBlock();
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
		this.addMouseListener(this);
	
	    
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
				if (board[r][c] != null && board[r][c].getSelected()) {
					g.setColor(Color.YELLOW);
					Rectangle bounds = board[r][c].getBounds();
					g.drawRect((int) bounds.getX() - 2, (int) bounds.getY() - 2, (int) bounds.getWidth() + 4, (int) bounds.getHeight() + 4);
					g.setColor(Color.BLACK);
					board[r][c].draw(g2, this);
				}
				else if (board[r][c] != null) board[r][c].draw(g2, this);
			}
		}
		

	}
	
	// method:clock
	// description: This method is called by the clocklistener every 5 milliseconds.  You should update the coordinates
	//				of one of your characters in this method so that it moves as bTime changes.  After you update the
	//				coordinates you should repaint the panel.
	public void clock(){
		if (!collide) {
			board[sBlock[0]][sBlock[1]].move(direction);
		}
		else { // collision!!!
			// collision one step at a time
		}
		
		this.repaint();
	}
	
	public int sumArrayAbs(int[] arr) {
		int sum = 0;
		for (int i : arr) {
			sum += Math.abs(i);
		}
		return sum;
	}
	
	public void animate() {
		// set currently selected block = false
		if (sBlock[0] != -1) board[sBlock[0]][sBlock[1]].setSelected(false);
		
		// ending position
		int[] end = new int[2];
		end[0] = sBlock[0];
		end[1] = sBlock[1];
		
		// checks if next position exists (is not a wall) and if it is empty.
		while ((end[0] + direction[0] < board.length -1 && end[1] + direction[1] < board[end[1]].length -1) && (end[0] + direction[0] > 0 && end[1] + direction[1] > 0) && (board[end[0] + direction[0]][end[1] + direction[1]] == null)) {
			// if so, updates new end position.
			end[0] += direction[0];
			end[1] += direction[1];
		}
		// checks for collision, and collides if so
		if (board[end[0] + direction[0]][end[1] + direction[1]] == board[sBlock[0]][sBlock[1]]) {
			// need collision animation
			// happens before logic so that sBlock is right.
			int t = 0;
			Timer.start();
			while (t < 20 * scale * sumArrayAbs(end)) { // based on scale
				t++;
			}
			while (t < 20 * scale *(sumArrayAbs(end)+1)) { // based on scale
				collide = true;
				clock();
				collide = false;
			}
			Timer.stop();
			
			score += board[sBlock[0]][sBlock[1]].getValue() * 2;
			board[end[0] + direction[0]][end[1] + direction[1]] = board[sBlock[0]][sBlock[1]];
			board[end[0] + direction[0]][end[1] + direction[1]].doubleValue();
			board[sBlock[0]][sBlock[1]] = null;
			
			
		}
		// moves the block to its new position.
		else {
			// need moving animation
			// happens before logic so that sBlock is right.
			int t = 0;
			Timer.start();
			while (t < 20 * scale * sumArrayAbs(end)) { // based on scale
				t++;
			}
			Timer.stop();
			board[end[0]][end[1]] = board[sBlock[0]][sBlock[1]];
			board[sBlock[0]][sBlock[1]] = null;
		}
		
		// adding random new block
		addRandomBlock();
		
		blockSelected = false;
		sBlock[0] = -1;
		sBlock[1] = -1;
		direction[0] = 0;
		direction[1] = 0;
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
		
		int[] add = empty.get((int) (Math.random() * empty.size()));
		int value = 0;
		if (Math.random() >= 0.9) value = 4;
		else value = 2;
		board[add[0]][add[1]] = new Block(14 + add[1] * 122, 14 + add[0] * 122, value, 1);
	}
	
	// method: keyPressed()
	// description: This method is called when a key is pressed. You can determine which key is pressed using the
	//				KeyEvent object.  For example if(e.getKeyCode() == KeyEvent.VK_LEFT) would test to see if
	//				the left key was pressed.
	// parameters: KeyEvent e
	@Override public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
        if (blockSelected) {
			if (keyCode == KeyEvent.VK_UP) {
	        	direction[0] = -1;
				System.out.println("up");
	        }
	        else if (keyCode == KeyEvent.VK_DOWN) {
	        	direction[0] = 1;
				System.out.println("down");
	        }
	        else if (keyCode == KeyEvent.VK_LEFT) {
	        	direction[1] = -1;
				System.out.println("left");
	        }
	        else if (keyCode == KeyEvent.VK_RIGHT) {
	        	direction[1] = 1;
				System.out.println("right");
	        }
			animate();
        }
	}
	@Override public void keyTyped(KeyEvent e) {}
	@Override public void keyReleased(KeyEvent e) {}
	
	@Override
    public void mouseClicked(MouseEvent e) {
        // Code to execute when mouse is clicked
        int x = e.getX(); // Get x-coordinate of click
        int y = e.getY(); // Get y-coordinate of click
        System.out.println("Mouse clicked at: (" + x + ", " + y + ")");
        
        // designates which block you are moving
        // set based on scale
        //int row = (int) (y * 5 / (scale * 100));
        //int col = (int) (x * 5 / (scale * 100));
        
        int row = -1;
        int col = -1;
        for (int r = 0; r < board.length; r++) {
        	for (int c = 0; c < board[r].length; c++) {
        		if (board[r][c] != null) {
	        		Rectangle bounds = board[r][c].getBounds();
	        		if (bounds.contains(x,y)) {
	        			board[r][c].setSelected(true);
	        			System.out.println(r + ", " + c);
	        			row = r;
	        			col = c;
	        		}
        		}
        	}
        }
        
        //board[row][col].setSelected(true);
        // setSelected block to true
        
        if (blockSelected && sBlock[0] != -1) { // if another block has been selected
        	board[sBlock[0]][sBlock[1]].setSelected(false);
        }
        sBlock[0] = row; // corresponding to rows
        sBlock[1] = col; // corresponding to columns
        System.out.println(sBlock[0] + ", " + sBlock[1]);
        blockSelected = true;
        repaint(); // Request a repaint to update the graphics
    }

    // Other MouseListener methods (must be implemented even if empty)
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}
