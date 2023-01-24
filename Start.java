/**
 * 
 * @author Sadie Whitehorne
 * 
 * Class that is used by BoardController that creates the 2 player objects and keeps track of who the current player is.
 * Right now player 1 is human player and player 2 is computer
 *
 */
/* TO DO: allow user to select the player options */
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
