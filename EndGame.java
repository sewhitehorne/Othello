/**
 * 
 * @author Sadie Whitehorne
 * 
 * Class used by BoardController that counts the number of discs on the board and determines the winner.
 *
 */
public class EndGame {
	
	private BoardGUI board;
	private Square[][] squares;
	private int whiteCount = 0;
	private int blackCount = 0;
	private int winner;
	
	public EndGame(BoardGUI board) {
		this.board = board;
		this.squares = board.getSquares();
		
		for(int i = 0; i < squares.length; i++) {
			for(int j = 0; j < squares[i].length; j++) {
				if(squares[i][j].getColor() == 0) { whiteCount++; }
				if(squares[i][j].getColor() == 1) { blackCount++; }
			}
		}
		
		if(whiteCount > blackCount) { winner = 2; }
		if(whiteCount < blackCount) { winner = 1; }
		if(whiteCount == blackCount) { winner = 0; }
		
	}
	
	public int getWinner() { return winner; }

}
