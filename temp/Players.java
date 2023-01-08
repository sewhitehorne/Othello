import java.util.ArrayList;

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
	
	public void setComputerPlayer() {
		computer = true;
	}
	
	public boolean getComputerPlayer() {
		return computer;
	}
	
	public ArrayList<Disc> getDiscs() {
		return discs;
	}
	
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
