public class Block extends Item {
	private int value;
	private int scale;
	private boolean selected;
	public Block(int x_coordinate, int y_coordinate, int value, int scale) {
		super(x_coordinate, y_coordinate, "images/" + value + ".png/", scale);
		this.value = value;
		this.scale = scale;
		this.selected = false;
    }
	
	public void doubleValue() {
		value *= 2;
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
    
    public int getScale() {
    	return scale;
    }
    
    public void setSelected(boolean selected) {
    	this.selected = selected;
    }
    
    public boolean getSelected() {
    	return selected;
    }
    
    public void move(int[] dir){
		y_coordinate += dir[0];
		x_coordinate += dir[1];
    }
}
