// Class: Background
// Written by: Mr. Swope
// Date: 1/27/2020
// Description: This class provides the implementation for a DesertBackground. You probably won't want to modify this
//              class.
// Edited By: Jacqueline
// Date: 01/30/2026
// Description: folded DesertBackground & Background into the same class. Now provides the board for 2048.
// 				I wouldn't modify this if I was you, but you may be able to change the image. Take note of the new
// 				image's pixel width though, everything is based on that.
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;

public class Background {
	private int width; // default is 500 px
	protected ImageIcon image;
	protected int scale;
	
	public Background(int width) {
		ClassLoader cldr = this.getClass().getClassLoader();	// These five lines of code load the background picture.
		String imagePath = "images/board_2048.png";				// Change this line if you want to use a different 
		URL imageURL = cldr.getResource(imagePath);				// background image.  The image should be saved in the
		this.width = width;
		
		image = new ImageIcon(imageURL);
		double scale = (this.width + 0.0) / image.getIconWidth();
		int height = (int)(scale * image.getIconHeight());
		Image scaled = image.getImage().getScaledInstance(this.width, 
				height, Image.SCALE_SMOOTH);
		
		image = new ImageIcon(scaled);
	}
	
	public void draw(Component c, Graphics g) {
		image.paintIcon(c, g, 0, 0);
	}
	
	public int getHeight() {
		return image.getIconHeight();
	}
	
	public int getWidth() {
		return image.getIconWidth();
	}
}
