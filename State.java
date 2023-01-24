
/* NOT CURRENTLY USED */
public class State {
	
	private Square[][] squares;
	
	public State(Square[][] squares) {
		this.squares = squares;
		
	}
	
	public Square[][] getState() { return this.squares; }
	
	public void display() { 
		for(int i = 0; i < squares.length; i++) {
			for(int j = 0; j < squares[i].length; j++) {
				System.out.println(squares[i][j].getColor() + " " + i + " " + j);
			}
		}
	}

}
