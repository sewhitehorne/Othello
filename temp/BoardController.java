import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class BoardController implements MouseListener {
	
	private BoardGUI board;
	private Players player; //current player
	private Start start; 
	private Square [][] squares;
	private int upperBound, lowerBound, upperBoundHorizontal, lowerBoundHorizontal, upperBoundDiagonalDecX, lowerBoundDiagonalDecX, upperBoundDiagonalDecY,
	lowerBoundDiagonalDecY, upperBoundDiagonalIncX, lowerBoundDiagonalIncX, upperBoundDiagonalIncY, lowerBoundDiagonalIncY; //used for determining what discs to flip when outflanked
	private Players player1, player2;
	private int outflankedCount; //used for evaluation for minimax algorithm, indicates the number of discs that are outflanked when a move is made
	private int maxDepth = 2;
	
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
		if (selected instanceof Square && ((Square) selected).isEnabled() && !player.getComputerPlayer() && validMove(player, ((Square) selected).getXcoord(), ((Square) selected).getYcoord())) { //human player turn (player 1)
			turn(((Square) selected));
			//computerPlayerMove();  //computer player's turn
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
		System.out.println("Player: " + player.getPlayerNum());
		boolean outFlanked = checkIfOutflanked(player, square.getXcoord(),square.getYcoord()); //checks column of selected square
		boolean outFlankedH = checkIfOutflankedHorizontal(player, square.getXcoord(),square.getYcoord()); //checks row of selected square
		boolean outFlankedDiagDec = checkIfOutflankedDiagonalDecreasing(player, square.getXcoord(),square.getYcoord()); //checks diagonally of selected square
		boolean outFlankedDiagInc = checkIfOutflankedDiagonalIncreasing(player, square.getXcoord(),square.getYcoord()); //check diagonally of selected square
		if(outFlanked || outFlankedH || outFlankedDiagDec || outFlankedDiagInc) {
			
			square.setColor(player.getColor()); //sets the square to the current player's
			square.displayColor(); 
			square.disableSquare(); 
			
			if(outFlanked) { switchDiscs(player, square.getXcoord(),square.getYcoord()); }
			if(outFlankedH) { switchDiscsHorizontal(player, square.getXcoord(),square.getYcoord()); }
			if(outFlankedDiagDec) { switchDiscsDiagonalDec(player); }
			if(outFlankedDiagInc) { switchDiscsDiagonalInc(player); }
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
			board.changeLabel("Player " + player.getPlayerNum() + "'s Turn");
			
			
			System.out.println("Hello");
			if(!anyValidMoves(player)) { //if there are not any valid moves for the new current player, then switch to the other player
				System.out.println("Here");
				start.updateCurrPlayer();
				player = start.getCurrPlayer();
				checkIfComputer();
				board.changeLabel("No valid moves. Player " + player.getPlayerNum() + " 's Turn");
				
			}
			
			if(isTerminal()) { //game is over
				EndGame endGame = new EndGame(board);
				int winner = endGame.getWinner();
				board.changeLabel("End Of Game: Player " + winner + "wins!");
				for(int i = 0; i < squares.length; i++) {
					for(int j = 0; j < squares[i].length; j++) {
						squares[i][j].disableSquare();
					}
				}
			}
			
			outflankedCount = 0; //used for computer player
			System.out.println("Hi");
		}
		
	}
	
	private void checkIfComputer() { 
		if(player.getComputerPlayer()) {
			board.changeLabel("Player " + player.getPlayerNum() + "'s Turn");
			computerPlayerMove(); }
			
			/**
			if(!anyValidMoves(player)) {
				start.updateCurrPlayer();
				player = start.getCurrPlayer();
				board.changeLabel("No valid moves. Player " + player.getPlayerNum() + " 's Turn");
				turn(square);
			}
			else {
				computerPlayerMove();
			}
			*/
		
		
	}
	
	/**
	 
	 * Method that returns true if the game is over, false if not. Game is over when there are no valid moves for player 1 and player 2, or
	 * if both players have 0 discs left.
	 */
	private boolean isTerminal() {
		return (!anyValidMoves(player1) && !anyValidMoves(player2)) || (player1.getDiscs().size() == 0 && player2.getDiscs().size() == 0);
	}
	
	/**
	
	 * Method that checks if there are any valid moves on the board for given player and returns true if there is
	 * 
	 * @param Players player, given player to check if there are any valid moves available
	 */
	private boolean anyValidMoves(Players player) {
		
		for(int i = 0; i < squares.length; i++) {
			for(int j = 0; j < squares[i].length; j++) {
				if(checkIfOutflanked(player, i, j) && squares[i][j].getColor() == 2) { return true; }
				if(checkIfOutflankedHorizontal(player, i , j)  && squares[i][j].getColor() == 2) { return true; }
				if(checkIfOutflankedDiagonalDecreasing(player, i, j) && squares[i][j].getColor() == 2) { return true; }
				if(checkIfOutflankedDiagonalIncreasing(player, i, j) && squares[i][j].getColor() == 2) { return true; }
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
	private boolean validMove(Players player, int xcoord, int ycoord) {
		if(checkIfOutflanked(player, xcoord, ycoord) && squares[xcoord][ycoord].getColor() == 2) { return true; }
		if(checkIfOutflankedHorizontal(player, xcoord, ycoord)  && squares[xcoord][ycoord].getColor() == 2) { return true; }
		if(checkIfOutflankedDiagonalDecreasing(player, xcoord, ycoord) && squares[xcoord][ycoord].getColor() == 2) { return true; }
		if(checkIfOutflankedDiagonalIncreasing(player, xcoord, ycoord) && squares[xcoord][ycoord].getColor() == 2) { return true; }
		
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
				boolean valid = validMove(player, i, j);
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
	 * @return boolean, true if outflanked and false if not
	 */
	private boolean checkIfOutflanked(Players player, int xcoord, int ycoord) {
		int playerColour = player.getColor();
		for(int i = xcoord+1; i <= 7; i++) { //check down from given x position 
			if(squares[xcoord+1][ycoord].getColor() == playerColour) { return false; } //right next to placed square
			if(squares[i][ycoord].getColor() == 2) { break; } //if a grey square is encountered then stop checking
			outflankedCount++; //increase outflanked count for every disc which would be outflanked
			if(squares[i][ycoord].getColor() == playerColour) { //encountered another square belonging to the player which indicates that the discs inbetween will be outflanked
				upperBound = xcoord+1; 
				lowerBound = i-1;
				
				return true;
			}
		}
		for(int j = xcoord-1; j >= 0; j--) { //check up from given x position
			if(squares[xcoord-1][ycoord].getColor() == playerColour) { return false; } //right next to placed square
			if(squares[j][ycoord].getColor() == 2) { break; }
			outflankedCount++;
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
	 * @return boolean, true if outflanked and false if not
	 */
	private boolean checkIfOutflankedHorizontal(Players player, int xcoord, int ycoord) {
		int playerColour = player.getColor();
		for(int i = ycoord+1; i <= 7; i++) { //check to right from given y position
			if(squares[xcoord][ycoord+1].getColor() == playerColour) { return false; } //right next to placed square
			if(squares[xcoord][i].getColor() == 2) { break; }
			outflankedCount++;
			if(squares[xcoord][i].getColor() == playerColour) {
				upperBoundHorizontal = ycoord+1;
				lowerBoundHorizontal = i-1;
				
				return true;
			}
		}
		for(int j = ycoord-1; j >= 0; j--) { //check to left from given y position
			if(squares[xcoord][ycoord-1].getColor() == playerColour) { return false; } //right next to placed square
			if(squares[xcoord][j].getColor() == 2) { break; }
			outflankedCount++;
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
	 * @return boolean, true if outflanked and false if not
	 */
	private boolean checkIfOutflankedDiagonalDecreasing(Players player, int xcoord, int ycoord) {
		int x = xcoord;
		int y = ycoord;
		int playerColour = player.getColor();
		while(x > 0 && y > 0 && xcoord-1 >= 0 && xcoord-1 < squares.length  && ycoord-1 >= 0 && ycoord-1 < squares.length) {
			if(squares[xcoord-1][ycoord-1].getColor() == playerColour) { return false; }
			if(squares[x-1][y-1].getColor() == 2) { break; }
			outflankedCount++;
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
			outflankedCount++;
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
	 * @return boolean, true if outflanked and false if not
	 */
	private boolean checkIfOutflankedDiagonalIncreasing(Players player, int xcoord, int ycoord) {
		int x = xcoord;
		int y = ycoord;
		int playerColour = player.getColor();
		while(x > 0 & y < squares.length-1 && xcoord-1 >= 0 && xcoord-1 < squares.length  && ycoord+1 >= 0 && ycoord+1 < squares.length) {
			if(squares[xcoord-1][ycoord+1].getColor() == playerColour) { return false; }
			if(squares[x-1][y+1].getColor() == 2) { break; }
			outflankedCount++;
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
			outflankedCount++;
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
	
	private void switchDiscsDiagonalDec(Players player) {
		
		int playerColour = player.getColor();
		for(int i = upperBoundDiagonalDecX; i <= lowerBoundDiagonalDecX; i++) {
			squares[i][upperBoundDiagonalDecY].setColor(playerColour);
			
			squares[i][upperBoundDiagonalDecY].displayColor();
			upperBoundDiagonalDecY++;
		}
	}
	
	private void switchDiscsDiagonalInc(Players player) {
		int playerColour = player.getColor();
		for(int i = upperBoundDiagonalIncX; i <= lowerBoundDiagonalIncX; i++) {
			squares[i][upperBoundDiagonalIncY].setColor(playerColour);
			squares[i][upperBoundDiagonalIncY].displayColor();
			upperBoundDiagonalIncY--;
		}
	}
	private void switchDiscs(Players player, int xcoord, int ycoord) {
		
		int playerColour = player.getColor();
		for(int i = upperBound; i <= lowerBound; i++) {
			squares[i][ycoord].setColor(playerColour);
			squares[i][ycoord].displayColor();
		}
	}
	
	private void switchDiscsHorizontal(Players player, int xcoord, int ycoord) {
		
		int playerColour = player.getColor();
		for(int i = upperBoundHorizontal; i <= lowerBoundHorizontal; i++) {
			squares[xcoord][i].setColor(playerColour);
			squares[xcoord][i].displayColor();
		}
	}
	
	
	
	/**
	 * 
	 * Method that evaluates a score or value that determines how beneficial it is for the current player to be at the current state.
	 * Uses an ArrayList of valid moves. Each valid move is checked and the move that provides the highest number of outflanked discs
	 * (most discs of the opposite player to be flipped over) is found. It then returns this number and that is the value given to being
	 * in that state. 
	 * @return 
	 */
	private int eval() {
		
		
		ArrayList<Integer[]> validMoves = getValidMoves(player); //ArrayList containing a number of lists of [x,y] positions of valid moves. 
		int maxCount = 0;
		for(int i = 0; i < validMoves.size(); i++) {
			checkIfOutflanked(player, validMoves.get(i)[0], validMoves.get(i)[1]);
			checkIfOutflankedHorizontal(player, validMoves.get(i)[0], validMoves.get(i)[1]);
			checkIfOutflankedDiagonalDecreasing(player, validMoves.get(i)[0], validMoves.get(i)[1]);
			checkIfOutflankedDiagonalIncreasing(player, validMoves.get(i)[0], validMoves.get(i)[1]);
			if(outflankedCount > maxCount) { maxCount = outflankedCount; }
			outflankedCount = 0; //reset outflankedCount for next iteration
		}
		return maxCount;
		
		
		/**
		checkIfOutflanked(player,i, j);
		checkIfOutflankedHorizontal(player,i,j);
		checkIfOutflankedDiagonalDecreasing(player,i, j);
		checkIfOutflankedDiagonalIncreasing(player,i, j);
		int count = outflankedCount;
		outflankedCount = 0;
		return count;
		*/
		/**
		int count = 0;
		for(int x = 0; x < squares.length; x++) {
			for(int y = 0; y < squares[x].length; y++) {
				if(squares[x][y].getColor() == player.getColor()) { count++; }
			}
		}
		return count;
		*/
		
	}
	
	/**
	 * 
	 * Method that takes the computer player move, uses minimax algorithm to determine the best move
	 */
	private void computerPlayerMove() {
		System.out.println("Beginning");
		int maxValue = -10000; //initialize max value to be very low
		int moveXcoord = 0; //initialize x coordinate of chosen move
		int moveYcoord = 0; //initialize y coordinate of chosen move
		for(int i = 0; i < squares.length; i++) { //for each position on the board
			for(int j = 0; j < squares[i].length; j++) {
				if(validMove(player, i, j)){ //if there is a valid move available at position i,j
					squares[i][j].setColor(player.getColor()); //make the move
					int value = miniMax(0, false); //use miniMax to determine the value of making that move
					squares[i][j].setColor(2); //undo or reset move, set the square back to grey
					if(value > maxValue) { //determine the max value
						maxValue = value;
						moveXcoord = i;
						moveYcoord = j;
					}
				}
			}
		}
		
		System.out.println(moveXcoord + " " + moveYcoord);
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
	private int miniMax(int depth, boolean max) {
		
		if(isTerminal() || depth > maxDepth) { //if the game is over or if the search is at the max depth
			return eval(); //return the value of the state
		}
		
		if(max) {
			int maxValue = -10000; //-Infinity
			for(int i = 0; i < squares.length; i++) { //for each position on the board
				for(int j = 0; j < squares[i].length; j++) {
					if(validMove(player2, i, j)) { //if a valid move is available for player 1 at i,j position
						squares[i][j].setColor(player2.getColor()); //make the move
						int value = miniMax(depth+1, false); //do minimax at next depth 
						squares[i][j].setColor(2); //undo move
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
					if(validMove(player1, i, j)) {
						squares[i][j].setColor(player1.getColor());
						int value = miniMax(depth+1, true);
						squares[i][j].setColor(2);
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
