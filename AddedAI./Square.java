import javax.swing.*;
import java.awt.Color;


public class Square extends JPanel {
	
	private int xcoord, ycoord;
	private int color; //color of disc 0 - white, 1 - black, 2 - null
	private boolean enabled; //true if it disc can be placed, false if not
	
	public Square(int xcoord, int ycoord) {
		super();
		this.setSize(100, 100);
		this.xcoord = xcoord;
		this.ycoord = ycoord;
		this.color = 2;
		enabled = true;
		this.setBackground(Color.LIGHT_GRAY);
	}
	
	public void setColor(int newColor) { color = newColor; }
	public void displayColor() { 
		if(color == 0) { this.setBackground(Color.WHITE); }
		//System.out.println(xcoord + " " + ycoord);
		if(color == 1) { this.setBackground(Color.BLACK); }
	}
	public int getColor() { return color; }
	public boolean isEnabled() { return enabled; }
	public void disableSquare() { enabled = false; } 
	
	
	
	public void setXcoord(int value) { xcoord = value; }
	public void setYcoord(int value) { ycoord = value; }
	public int getXcoord() {return xcoord;}
	public int getYcoord() {return ycoord;}

	public void addActionListener(Object object) {
		//this.setBackground(Color.GREEN);
		
	}

}
