// Class: Block
// Written By: Jacqueline, Valentina, Lucy, Ellie
// Date: 01/30/2026
// Description: the Block object for the game 2048
public class Block extends Item {
	private int value;
	private int width;
	private boolean combined; // the stationary block that combines
	private boolean moving;
	
	public Block(int x_coordinate, int y_coordinate, int value, int width) {
		super(x_coordinate, y_coordinate, "images/" + value + ".png", width);
		this.value = value;
		this.width = width;
		this.combined = false;
		this.moving = false;
    }
	
	public void doubleValue() {
		value *= 2;
		Block newBlock = new Block(this.x_coordinate, this.y_coordinate, this.value, this.width);
		this.image = newBlock.image;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setX(int x_coordinate) {
    	this.x_coordinate = x_coordinate;
    }
    
    public void setY(int y_coordinate) {
    	this.y_coordinate = y_coordinate;
    }
    
    public void setCombined(boolean combined) {
    	this.combined = combined;
    }
    
    public boolean getCombined() {
    	return combined;
    }
    
    // current rendered icon size = 106 px
    public int getWidth() {
    	return image.getIconWidth();
    }
    
    public int getHeight() {
    	return image.getIconHeight();
    }
    
    public void move(int[] dir){
		y_coordinate += dir[0];
		x_coordinate += dir[1];
    }
    
    public void setMoving(boolean moving) {
    	this.moving = moving;
    }
    
    public boolean getMoving() {
    	return moving;
    }
}
