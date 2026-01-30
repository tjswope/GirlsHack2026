public class Block extends Item {
	private int value;
	private int scale;
	private boolean combined;
	
	private int prevX;
	private int prevY;
	
	public Block(int x_coordinate, int y_coordinate, int value, int scale) {
		super(x_coordinate, y_coordinate, "images/" + value + ".png/", scale);
		this.value = value;
		this.scale = scale;
		this.combined = false;
		
		this.prevX=x_coordinate;
		this.prevY=y_coordinate;
		
    }
	
	public void doubleValue() {
		value *= 2;
		Block newBlock = new Block(this.x_coordinate, this.y_coordinate, this.value, this.scale);
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
    
    public int getScale() {
    	return scale;
    }
    
    public void setCombined(boolean combined) {
    	this.combined = combined;
    }
    
    public boolean getCombined() {
    	return combined;
    }
    
    public void move(int[] dir){
    	this.prevX=this.x_coordinate;
    	this.prevY=this.y_coordinate;
    	
		y_coordinate += dir[0];
		x_coordinate += dir[1];
		
    }
    
    public void resetAnimation() {
    	this.prevX=this.x_coordinate;
    	this.prevY=this.y_coordinate;
    	
    }
    
    
    public int getVisualX(double t) {
    	return(int)(prevX+(x_coordinate-prevX)*t);
    	
    }
   
    public int getVisualY(double t) {
    	return(int)(prevY+(y_coordinate-prevY)*t);
    	
    }
    
}
