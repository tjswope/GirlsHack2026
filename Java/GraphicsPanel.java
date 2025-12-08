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
	private int time; // main running bTimer
	private boolean blockSelected;
	private boolean dirSelected;
	private int[] direction;
	private int[] sBlock;
	private PracticeBlock[][] board;
	
	private Background background;	// background
	
	
	
	private boolean gameOver; // if the game is over
	
	
	public GraphicsPanel(){
		background = new DesertBackground();	
		time = 0;
		score = 0;
		moves = 0;
		blockSelected = false;
		dirSelected = false;
		direction = new int[2];
		sBlock = new int[2];
		sBlock[0] = -1;
		sBlock[1] = -1;
		board = new PracticeBlock[5][5];
		// initialize initial board here
		
		// This line of code sets the dimension of the panel equal to the dimensions
		// of the background image.
		setPreferredSize(new Dimension(background.getWidth(),background.getHeight()));									
		
		Timer = new Timer(5, new ClockListener(this));   // This object will call the ClockListener's
													 	 // action performed method every 5 milliseconds once the
													 	 // bTimer is started. You can change how frequently this
													 	 // method is called by changing the first parameter.
		Timer.start();
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
		
		//DRAW HEALTH BAR
		//border around
		g2.setColor(Color.BLACK);
		// the health bar
		g2.fillRect(895,65,210,30);
		// the starvation bar
		g2.fillRect(895,135,210,30);
		
		// Creates bar background for when levels decrease
		g2.setColor(Color.GRAY);
		// for the health bar
		g2.fillRect(900, 70, 200, 20);
		// for the starvation bar
		g2.fillRect(900, 140, 200, 20);
		
		
		

	}
	
	// method:clock
	// description: This method is called by the clocklistener every 5 milliseconds.  You should update the coordinates
	//				of one of your characters in this method so that it moves as bTime changes.  After you update the
	//				coordinates you should repaint the panel.
	public void clock(){
		time += 1;
		// update logic
		if (dirSelected) {
			check(sBlock[0], sBlock[1], direction);
			board[sBlock[0]][sBlock[1]].setSelected(false);
			
			// move everything
			// need a while function here that directs the block to move until it reaches its final destination.
			// could trigger this in the check function, but need it to occur over time
			// but then could have the collision function be a move to endRow & endCol + collision during final movement.
			board[sBlock[0]][sBlock[1]].move(direction);
			
			dirSelected = false;
			blockSelected = false;
			sBlock[0] = -1;
			sBlock[1] = -1;
			direction[0] = 0;
			direction[1] = 1;
		}
		
		this.repaint();
	}
	
	
	
	public void check(int row, int col, int[] direct) {
		// end positions, temporary
		int endR = row;
		int endC = col;
		
		// checks if next position exists (is not a wall) and if it is empty.
		while ((endR + direct[0] < board.length && endC + direct[1] < board[endC].length) && (board[endR + direct[0]][endC + direct[1]] == null)) {
			// if so, updates new end position.
			endR += direct[0];
			endC += direct[1];
		}
		// checks for collision, and collides if so
		if (board[endR + direct[0]][endC + direct[1]] == board[row][col]) {
			score += board[row][col].getValue() * 2;
			board[endR + direct[0]][endC + direct[1]] = board[row][col];
			board[endR + direct[0]][endC + direct[1]].doubleValue();
			board[row][col] = null;
			// need collision animation
		}
		// moves the block to its new position.
		else {
			board[endR][endC] = board[row][col];
			board[row][col] = null;
			// need moving animation
		}
		
		// adding random new block
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
	        }
	        else if (keyCode == KeyEvent.VK_DOWN) {
	        	direction[0] = 1;
	        }
	        else if (keyCode == KeyEvent.VK_LEFT) {
	        	direction[1] = -1;
	        }
	        else if (keyCode == KeyEvent.VK_RIGHT) {
	        	direction[1] = 1;
	        }
		dirSelected = true;
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
        // setSelected block to true
        
        if (blockSelected) { // if another block has been selected
        	// unselect previous block using setSelected
        }
        sBlock[0] = y; // corresponding to rows
        sBlock[1] = x; // corresponding to columns
        blockSelected = true;
        repaint(); // Request a repaint to update the graphics
    }

    // Other MouseListener methods (must be implemented even if empty)
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}
