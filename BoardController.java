import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
/**
 * 
 * 
 * @author Sadie Whitehorne
 * 
 * Class that handles controlling the BoardGUI and performing turns for both human and computer player (computer player moves determined by minimax algorithm)
 *
 */
/*TO DO: Improve eval function. Fix bug where it's not displaying who's turn it is correctly. Take out print statements. Try implementing alpha-beta.  */
public class BoardController implements MouseListener {
	
	private BoardGUI board;
	private Players player, player1, player2; 
	private Start start; 
	private Square [][] squares;
	private int upperBound, lowerBound, upperBoundHorizontal, lowerBoundHorizontal, upperBoundDiagonalDecX, lowerBoundDiagonalDecX, upperBoundDiagonalDecY,
	lowerBoundDiagonalDecY, upperBoundDiagonalIncX, lowerBoundDiagonalIncX, upperBoundDiagonalIncY, lowerBoundDiagonalIncY; //used for determining what discs to flip when outflanked
	private int maxDepth = 2;
	private boolean miniMax = false;
	
	
	public BoardController(BoardGUI board) {
		this.board = board;
		this.start = new Start();
		this.player = start.getCurrPlayer();
		this.squares = board.getSquares();
		
		this.player1 = start.getPlayer1();
		this.player2 = start.getPlayer2();
		
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j ++) {
				squares[i][j].addMouseListener(this);
			}
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		Object selected = e.getSource();
		if (selected instanceof Square && ((Square) selected).isEnabled() && !player.getComputerPlayer() && validMove(player, ((Square) selected).getXcoord(), ((Square) selected).getYcoord(), squares)) { //human player turn (player 1)
			turn(((Square) selected));
			checkIfComputer();
		}
	}
	
	/**
	  
	 * Method that performs a turn.  Checks if the selected square on the board is a valid move, if
	 * it is then the turn is continued (discs are flipped over, number of discs are decreased, etc.). If it is not a valid move, then nothing happens.
	 * 
	 * @param Square square, square on the board that was clicked by the human player.
	 */
	
	private void turn(Square square) {
		
		board.changeLabel("Player " + player.getPlayerNum() + "'s Turn");
		
		boolean outFlanked = checkIfOutflanked(player, square.getXcoord(),square.getYcoord(), squares); //checks column of selected square
		boolean outFlankedH = checkIfOutflankedHorizontal(player, square.getXcoord(),square.getYcoord(), squares); //checks row of selected square
		boolean outFlankedDiagDec = checkIfOutflankedDiagonalDecreasing(player, square.getXcoord(),square.getYcoord(), squares); //checks diagonally of selected square
		boolean outFlankedDiagInc = checkIfOutflankedDiagonalIncreasing(player, square.getXcoord(),square.getYcoord(), squares); //check diagonally of selected square
		
		if(outFlanked || outFlankedH || outFlankedDiagDec || outFlankedDiagInc) {
			
			square.setColor(player.getColor()); //sets the square to the current player's
			square.displayColor(); 
			square.disableSquare(); 
			
			if(outFlanked) { switchDiscs(player, square.getXcoord(),square.getYcoord(), squares); }
			if(outFlankedH) { switchDiscsHorizontal(player, square.getXcoord(),square.getYcoord(), squares); }
			if(outFlankedDiagDec) { switchDiscsDiagonalDec(player, squares); }
			if(outFlankedDiagInc) { switchDiscsDiagonalInc(player, squares); }
			if(player.getDiscs().size() == 0) { // if the player can make a valid move but they have no discs left, then the other player must give them a disc to make the move
				start.updateCurrPlayer(); //go to next player
				Players newPlayer = start.getCurrPlayer(); //next player
				newPlayer.decreaseDiscs(); //next player gives a disc to the current player
				start.updateCurrPlayer(); //go back to current player
				player = start.getCurrPlayer();
			}
			else { //if a valid move can be made and the current player has discs left to use
				player.decreaseDiscs();
			}
			
			start.updateCurrPlayer(); //go to next player
			player = start.getCurrPlayer();
			//board.changeLabel("Player " + player.getPlayerNum() + "'s Turn");
			

			if(!anyValidMoves(player, squares)) { //if there are not any valid moves for the new current player, then switch to the other player
				System.out.println("Here");
				start.updateCurrPlayer();
				player = start.getCurrPlayer();
				checkIfComputer();
				board.changeLabel("No valid moves. Player " + player.getPlayerNum() + " 's Turn");
				
			}
			
			if(isTerminal(squares)) { //game is over
				EndGame endGame = new EndGame(board);
				int winner = endGame.getWinner();
				if(winner != 0) {board.changeLabel("End Of Game: Player " + winner + " wins!");}
				else {board.changeLabel("There's a tie!");}
				for(int i = 0; i < squares.length; i++) {
					for(int j = 0; j < squares[i].length; j++) {
						squares[i][j].disableSquare();
					}
				}
			}
			
		}
		
	}
	
	/**
	 * 
	 * Method that checks if the current player is the computer and if so, starts the computer player's move
	 */
	private void checkIfComputer() { 
		if(player.getComputerPlayer()) {
			board.changeLabel("Player " + player.getPlayerNum() + "'s Turn");
			computerPlayerMove(); }
	}
	
	/**
	 
	 * Method that returns true if the game is over, false if not. Game is over when there are no valid moves for player 1 and player 2, or
	 * if both players have 0 discs left.
	 */
	private boolean isTerminal(Square[][] squares) {
		return (!anyValidMoves(player1, squares) && !anyValidMoves(player2, squares)) || (player1.getDiscs().size() == 0 && player2.getDiscs().size() == 0);
	}
	
	/**
	
	 * Method that checks if there are any valid moves on the board for given player and returns true if there is
	 * 
	 * @param Players player, given player to check if there are any valid moves available
	 */
	private boolean anyValidMoves(Players player, Square[][] squares) {
		
		for(int i = 0; i < squares.length; i++) {
			for(int j = 0; j < squares[i].length; j++) {
				if(checkIfOutflanked(player, i, j, squares) && squares[i][j].getColor() == 2) { return true; }
				if(checkIfOutflankedHorizontal(player, i , j, squares)  && squares[i][j].getColor() == 2) { return true; }
				if(checkIfOutflankedDiagonalDecreasing(player, i, j, squares) && squares[i][j].getColor() == 2) { return true; }
				if(checkIfOutflankedDiagonalIncreasing(player, i, j, squares) && squares[i][j].getColor() == 2) { return true; }
			}
		}
		
		return false;
	}
	
	/**
	
	 * Method that checks if there is a valid move for a given player at a given x,y position on the board
	 * and returns true if there is.
	 * 
	 * @param Players player, given player to check if there are any valid moves available
	 * @param int xcoord, int ycoord, given x and y position to check validity of on the board
	 */
	private boolean validMove(Players player, int xcoord, int ycoord, Square[][] squares) {
		if(checkIfOutflanked(player, xcoord, ycoord, squares) && squares[xcoord][ycoord].getColor() == 2) { return true; }
		if(checkIfOutflankedHorizontal(player, xcoord, ycoord, squares)  && squares[xcoord][ycoord].getColor() == 2) { return true; }
		if(checkIfOutflankedDiagonalDecreasing(player, xcoord, ycoord, squares) && squares[xcoord][ycoord].getColor() == 2) { return true; }
		if(checkIfOutflankedDiagonalIncreasing(player, xcoord, ycoord, squares) && squares[xcoord][ycoord].getColor() == 2) { return true; }
		
		return false;
	}
	
	/**

	 * Method that returns a 2D ArrayList of all the valid moves available on the board for a given player
	 * 
	 *  @param Players player, given player to check if there are any valid moves available
	 *  @return ArrayList<Integer[]>, an arraylist containing n amount of lists that each contain the x,y position of a valid move
	 */
	private ArrayList<Integer[]> getValidMoves(Players player){
		ArrayList<Integer[]> validMoves = new ArrayList<>();
		for(int i = 0; i < squares.length; i++) {
			for(int j = 0; j < squares[i].length; j++) {
				boolean valid = validMove(player, i, j, squares);
				if(valid) {
					Integer[] coordinates = {i, j};
					validMoves.add(coordinates);
				}
			}
		}
		return validMoves;
	}
		
	
	
	/**
	 * 
	 * Method that checks the column of a given x,y position on the board to see if it will cause an outflanking
	 * for the given player, if so it returns true
	 * @param Players player, given player to check for 
	 * @param int xcoord, x (row) position
	 * @param int ycoord, y (column) position
	 * @return true if outflanked and false if not
	 */
	private boolean checkIfOutflanked(Players player, int xcoord, int ycoord, Square[][] squares) {
		int playerColour = player.getColor();
		
		for(int i = xcoord+1; i <= 7; i++) { //check down from given x position 
			if(squares[xcoord+1][ycoord].getColor() == playerColour) {
				return false; } //right next to placed square
			if(squares[i][ycoord].getColor() == 2) { break; } //if a grey square is encountered then stop checking
			if(squares[i][ycoord].getColor() == playerColour) { //encountered another square belonging to the player which indicates that the discs inbetween will be outflanked
				upperBound = xcoord+1; 
				lowerBound = i-1;
				
				return true;
			}
		}
		for(int j = xcoord-1; j >= 0; j--) { //check up from given x position
			if(squares[xcoord-1][ycoord].getColor() == playerColour) {
				return false; } //right next to placed square
			if(squares[j][ycoord].getColor() == 2) { break; }
			if(squares[j][ycoord].getColor() == playerColour) {
				upperBound = j+1;
				lowerBound = xcoord-1;
				
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 
	 * Method that checks the row of a given x,y position on the board to see if it will cause an outflanking
	 * for the given player, if so it returns true
	 * @param Players player, given player to check for 
	 * @param int xcoord, x (row) position
	 * @param int ycoord, y (column) position
	 * @return true if outflanked and false if not
	 */
	private boolean checkIfOutflankedHorizontal(Players player, int xcoord, int ycoord, Square[][] squares) {
		int playerColour = player.getColor();
		for(int i = ycoord+1; i <= 7; i++) { //check to right from given y position
			if(squares[xcoord][ycoord+1].getColor() == playerColour) { return false; } //right next to placed square
			if(squares[xcoord][i].getColor() == 2) { break; }
			if(squares[xcoord][i].getColor() == playerColour) {
				upperBoundHorizontal = ycoord+1;
				lowerBoundHorizontal = i-1;
				
				return true;
			}
		}
		for(int j = ycoord-1; j >= 0; j--) { //check to left from given y position
			
			if(squares[xcoord][ycoord-1].getColor() == playerColour) { return false; } //right next to placed square
			if(squares[xcoord][j].getColor() == 2) { break; }
			if(squares[xcoord][j].getColor() == playerColour) {
				upperBoundHorizontal = j+1;
				lowerBoundHorizontal = ycoord-1;
				
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * Method that checks diagonally (top left to bottom right) of a given x,y position on the board to see if it will cause an outflanking
	 * for the given player, if so it returns true
	 * @param Players player, given player to check for 
	 * @param int xcoord, x (row) position
	 * @param int ycoord, y (column) position
	 * @return true if outflanked and false if not
	 */
	private boolean checkIfOutflankedDiagonalDecreasing(Players player, int xcoord, int ycoord, Square[][] squares) {
		int x = xcoord;
		int y = ycoord;
		int playerColour = player.getColor();
		while(x > 0 && y > 0 && xcoord-1 >= 0 && xcoord-1 < squares.length  && ycoord-1 >= 0 && ycoord-1 < squares.length) {
			if(squares[xcoord-1][ycoord-1].getColor() == playerColour) { return false; }
			if(squares[x-1][y-1].getColor() == 2) { break; }
			if(squares[x-1][y-1].getColor() == playerColour) {
				upperBoundDiagonalDecX = x;
				upperBoundDiagonalDecY = y;
				lowerBoundDiagonalDecX = xcoord-1;
				lowerBoundDiagonalDecY = ycoord-1;
				return true;
				
			}
			
			else {
				x = x-1;
				y = y-1;
			}
		}
		
		x = xcoord;
		y = ycoord; 
		
		while(x < squares.length-1 && y < squares.length-1 && xcoord+1 >= 0 && xcoord+1 < squares.length  && ycoord+1 >= 0 && ycoord+1 < squares.length) {
			if(squares[xcoord+1][ycoord+1].getColor() == playerColour) { return false; }
			if(squares[x+1][y+1].getColor() == 2) { break; }
			if(squares[x+1][y+1].getColor() == playerColour) {
				upperBoundDiagonalDecX = xcoord+1;
				upperBoundDiagonalDecY = ycoord+1;
				lowerBoundDiagonalDecX = x;
				lowerBoundDiagonalDecY = y;
				return true;
				
			}
			
			else {
				x = x+1;
				y = y+1;
			}
		}
		
		return false;
	}
	
	/**
	 * 
	 * Method that checks diagonally (bottom left to top right) of a given x,y position on the board to see if it will cause an outflanking
	 * for the given player, if so it returns true
	 * @param Players player, given player to check for 
	 * @param int xcoord, x (row) position
	 * @param int ycoord, y (column) position
	 * @return true if outflanked and false if not
	 */
	private boolean checkIfOutflankedDiagonalIncreasing(Players player, int xcoord, int ycoord, Square[][] squares) {
		int x = xcoord;
		int y = ycoord;
		int playerColour = player.getColor();
		while(x > 0 & y < squares.length-1 && xcoord-1 >= 0 && xcoord-1 < squares.length  && ycoord+1 >= 0 && ycoord+1 < squares.length) {
			if(squares[xcoord-1][ycoord+1].getColor() == playerColour) { return false; }
			if(squares[x-1][y+1].getColor() == 2) { break; }
			if(squares[x-1][y+1].getColor() == playerColour) {
				upperBoundDiagonalIncX = x;
				upperBoundDiagonalIncY = y;
				lowerBoundDiagonalIncX = xcoord-1;
				lowerBoundDiagonalIncY = ycoord+1;
				return true;
				
			}
			
			else {
				x = x-1;
				y = y+1;
			}
		}
		x = xcoord;
		y = ycoord;
		
		while(x < squares.length-1 && y > 0 && xcoord+1 >= 0 && xcoord+1 < squares.length  && ycoord-1 >= 0 && ycoord-1 < squares.length) {
			if(squares[xcoord+1][ycoord-1].getColor() == playerColour) { return false; }
			if(squares[x+1][y-1].getColor() == 2) { break; }
			if(squares[x+1][y-1].getColor() == playerColour) {
				upperBoundDiagonalIncX = xcoord+1;
				upperBoundDiagonalIncY = ycoord-1;
				lowerBoundDiagonalIncX = x;
				lowerBoundDiagonalIncY = y;
				return true;
				
			}
			
			else {
				x = x+1;
				y = y-1;
			}
		}
		
		return false;
		
	}
	
	
	/**
	 * 
	 * Method that flips the appropriate discs over on the gameboard diagonally from top left to bottom right
	 * @param player, player to switch the discs for
	 * @param squares, game board
	 */
	private void switchDiscsDiagonalDec(Players player, Square[][] squares) {
		
		int playerColour = player.getColor();
		for(int i = upperBoundDiagonalDecX; i <= lowerBoundDiagonalDecX; i++) {
			squares[i][upperBoundDiagonalDecY].setColor(playerColour);
			if(!miniMax) {squares[i][upperBoundDiagonalDecY].displayColor();} //only display when it's an actual turn
			upperBoundDiagonalDecY++;
		}
	}
	
	/**
	 * 
	 * Method that flips the appropriate discs over on the gameboard diagonally from bottom left to top right
	 * @param player, player to switch the discs for
	 * @param squares, game board
	 */
	private void switchDiscsDiagonalInc(Players player, Square[][] squares) {
		int playerColour = player.getColor();
		for(int i = upperBoundDiagonalIncX; i <= lowerBoundDiagonalIncX; i++) {
			squares[i][upperBoundDiagonalIncY].setColor(playerColour);
			if(!miniMax) { squares[i][upperBoundDiagonalIncY].displayColor(); } //only display when it's an actual turn
			upperBoundDiagonalIncY--;
		}
	}
	
	/**
	 * 
	 * Method that flips the appropriate discs over on the gameboard vertically
	 * @param player, player to switch the discs for
	 * @param squares, game board
	 * @param int ycoord, column where the discs are being flipped 
	 */
	private void switchDiscs(Players player, int xcoord, int ycoord, Square[][] squares) {
		
		int playerColour = player.getColor();
		for(int i = upperBound; i <= lowerBound; i++) {
			squares[i][ycoord].setColor(playerColour);
			if(!miniMax) {squares[i][ycoord].displayColor();} //only display when it's an actual turn
		}
	}
	
	/**
	 * 
	 * Method that flips the appropriate discs over on the gameboard horizontally
	 * @param player, player to switch the discs for
	 * @param squares, game board
	 * @param int xcoord, row where the discs are being flipped 
	 */
	private void switchDiscsHorizontal(Players player, int xcoord, int ycoord, Square[][] squares) {
		
		int playerColour = player.getColor();
		for(int i = upperBoundHorizontal; i <= lowerBoundHorizontal; i++) {
			squares[xcoord][i].setColor(playerColour);
			if(!miniMax) {squares[xcoord][i].displayColor();} //only display when it's an actual turn
		}
	}
	
	/**
	 * 
	 * Method that does a deep copy of the provided game state
	 * @param board, game state that will be copied
	 * @return a copy of the game state
	 */
	private Square[][] copyBoard(Square[][] board) {
		
		Square[][] newGameBoard = new Square[board.length][];
		for(int i = 0; i < board.length; i++) {
			newGameBoard[i] = new Square[board[i].length];
			for(int j = 0; j < board[i].length; j++) {
				newGameBoard[i][j] = new Square(i,j);
				newGameBoard[i][j].setColor(board[i][j].getColor());
			}
		}
		return newGameBoard;
	}
	
	/**
	 * 
	 * Method that is used by the minimax algorithm to update the given game state based on the move made at the provided x,y coordinates
	 * for the specified player
	 * @param player, player making the move
	 * @param xcoord, x coordinate of move to be made
	 * @param ycoord, y coordinate of move to be made
	 * @param gameBoard, game state to update
	 */
	private void updateBoard(Players player, int xcoord, int ycoord, Square[][] gameBoard) {
		
		boolean outFlanked = checkIfOutflanked(player, xcoord, ycoord, gameBoard); //checks column of selected square
		boolean outFlankedH = checkIfOutflankedHorizontal(player, xcoord, ycoord, gameBoard); //checks row of selected square
		boolean outFlankedDiagDec = checkIfOutflankedDiagonalDecreasing(player, xcoord, ycoord, gameBoard); //checks diagonally of selected square
		boolean outFlankedDiagInc = checkIfOutflankedDiagonalIncreasing(player, xcoord, ycoord, gameBoard); //check diagonally of selected square
		if(outFlanked) { switchDiscs(player, xcoord, ycoord, gameBoard); }
		if(outFlankedH) { switchDiscsHorizontal(player, xcoord, ycoord, gameBoard); }
		if(outFlankedDiagDec) { switchDiscsDiagonalDec(player, gameBoard); }
		if(outFlankedDiagInc) { switchDiscsDiagonalInc(player, gameBoard); }
		
	}
	
	
	
	/**
	 * 
	 * Method that evaluates a value that determines how beneficial it is for the current player to be at the current state.
	 * Right now it just counts the number of discs for player 2 (computer player at the moment). Need to update to better heuristic
	 * 
	 * @return count of number of discs for player 2
	 */
	private int eval(Square[][] squares) {
		
		int count = 0;
		for(int x = 0; x < squares.length; x++) {
			for(int y = 0; y < squares[x].length; y++) {
				if(squares[x][y].getColor() == player2.getColor()) { count++; }
			}
		}
		return count;
		
		
	}
	
	
	/**
	 * 
	 * Method that takes the computer player move, uses minimax algorithm to determines the best move
	 */
	private void computerPlayerMove() {
		board.changeLabel("Computer is thinking!");
		miniMax = true;
		int maxValue = -10000; //initialize max value to be very low
		int moveXcoord = 0; //initialize x coordinate of chosen move
		int moveYcoord = 0; //initialize y coordinate of chosen move
		for(int i = 0; i < squares.length; i++) { //for each position on the board
			for(int j = 0; j < squares[i].length; j++) {
				if(validMove(player2, i, j, squares)){ //if there is a valid move available at position i,j
					Square[][] newGameBoard = copyBoard(squares);
					//State state = new State(newGameBoard);
					System.out.println("Before" + i + " " + j);
					newGameBoard[i][j].setColor(player2.getColor()); //make the move
					updateBoard(player2, i, j, newGameBoard);
					int value = miniMax(newGameBoard, 0, false); //use miniMax to determine the value of making that move
					System.out.println(value + " " + i + " " + j);
					if(value > maxValue) { //determine the max value
						maxValue = value;
						moveXcoord = i;
						moveYcoord = j;
					}
				}
			}
		}
		miniMax = false;
		System.out.println("Chosen move" + " " + moveXcoord + " " + moveYcoord); //delete
		turn(squares[moveXcoord][moveYcoord]); //make the turn at the chosen move
		
	}
	
	/**
	 * 
	 * Method that uses the MiniMax algorithm to determine what is the best move for the computer player to make to a given depth
	 * 
	 * @param int depth, given depth to search to
	 * @param boolean max, true if it is the maximizing player
	 * @return value of the best move
	 */
	private int miniMax(Square[][] squares, int depth, boolean max) {
		
		if(isTerminal(squares) || depth > maxDepth) { //if the game is over or if the search is at the max depth
			
			return eval(squares); //return the value of the state
		}
		
		if(max) {
			int maxValue = -10000; //-Infinity
			for(int i = 0; i < squares.length; i++) { //for each position on the board
				for(int j = 0; j < squares[i].length; j++) {
					if(validMove(player2, i, j, squares)) { //if a valid move is available for player 1 at i,j position
						Square[][] newGameBoard = copyBoard(squares);
						//State state = new State(newGameBoard);
						newGameBoard[i][j].setColor(player2.getColor()); //make the move
						updateBoard(player2, i, j, newGameBoard);
						int value = miniMax(newGameBoard, depth+1, false); //do minimax at next depth 
						if(value > maxValue) { //take maximum between value and maxValue
							maxValue = value;
						}
					}
				}
			}
			return maxValue; //return maxValue
		}
		else {
			int maxValue = 10000; //+Infinity
			for(int i = 0; i < squares.length; i++) { 
				for(int j = 0; j < squares[i].length; j++) {
					if(validMove(player1, i, j, squares)) {
						Square[][] newGameBoard = copyBoard(squares);
						//State state = new State(newGameBoard);
						newGameBoard[i][j].setColor(player1.getColor());
						updateBoard(player1, i, j, newGameBoard);
						int value = miniMax(newGameBoard, depth+1, true);
						if(value < maxValue) { //take minimum between value and maxValue
							maxValue = value;
						}
					}
				}
			}
			return maxValue; //return maxValue
		}
	}
	
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}

}
