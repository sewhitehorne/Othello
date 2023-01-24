import java.util.ArrayList;
/**
 * 
 * @author Sadie Whitehorne
 * 
 * Class that holds all of a player's attributes. Player number is 1 for player 1 and 2 for player 2. Their colour disc is denoted as 0 for white (player 2) and 1 for black (player 1). 
 * Also each player holds their 30 disc objects (in Othello each player gets 32, but 2 of them are put on the board in the very beginning).
 *
 */
public class Players {
	
	private int playerNum;
	private int color;
	private ArrayList<Disc> discs;
	private boolean computer;
	
	public Players(int playerNum, int color, boolean computer) {
		this.playerNum = playerNum;
		this.color = color; //0 if white, 1 if black
		discs = new ArrayList<Disc>();
		this.computer = computer;
		
		createDiscs();
		
	}
	
	public int getColor() { return color; }
	
	public int getPlayerNum() { return playerNum; }
	
	public void setComputerPlayer() { computer = true; }
	
	public boolean getComputerPlayer() { return computer; }
	
	public ArrayList<Disc> getDiscs() { return discs; }
	
	private void createDiscs() { 
		for(int i = 0; i < 30; i++) {
			discs.add(new Disc(color));
		}
	}
	
	public void decreaseDiscs() {
		if(discs.size() != 0) {
			discs.remove(discs.size() - 1);
		}
	}

}
