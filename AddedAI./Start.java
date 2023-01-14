
public class Start {
	
	private Players player1, player2;
	private Players currPlayer;
	private Players winner;
	
	public Start() {
		player1 = new Players(1, 1, false);
		player2 = new Players(2, 0, true);
		currPlayer = player1;
		
	}
	
	public Players getCurrPlayer() { return currPlayer; }
	
	public void updateCurrPlayer() {
		if(currPlayer == player1) { currPlayer = player2; }
		else { currPlayer = player1; }
	}
	
	public Players getPlayer1() { return player1; }
	public Players getPlayer2() { return player2; }
	
	
	

}
