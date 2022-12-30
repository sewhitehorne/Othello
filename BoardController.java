import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class BoardController implements MouseListener {
	
	private BoardGUI board;
	private Players player;
	private Start start;
	private Square [][] squares;
	private int upperBound;
	private int lowerBound;
	private int upperBoundHorizontal;
	private int lowerBoundHorizontal;
	private int upperBoundDiagonalDecX;
	private int lowerBoundDiagonalDecX;
	private int upperBoundDiagonalDecY;
	private int lowerBoundDiagonalDecY;
	private int upperBoundDiagonalIncX;
	private int lowerBoundDiagonalIncX;
	private int upperBoundDiagonalIncY;
	private int lowerBoundDiagonalIncY;
	private Players player1, player2;
	
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
		if (selected instanceof Square && ((Square) selected).isEnabled()) {
			boolean outFlanked = checkIfOutflanked(player, ((Square) selected).getXcoord(),((Square) selected).getYcoord());
			boolean outFlankedH = checkIfOutflankedHorizontal(player, ((Square) selected).getXcoord(),((Square) selected).getYcoord());
			boolean outFlankedDiagDec = checkIfOutflankedDiagonalDecreasing(player, ((Square) selected).getXcoord(),((Square) selected).getYcoord());
			boolean outFlankedDiagInc = checkIfOutflankedDiagonalIncreasing(player, ((Square) selected).getXcoord(),((Square) selected).getYcoord());
			if(outFlanked || outFlankedH || outFlankedDiagDec || outFlankedDiagInc) {
				((Square) selected).setColor(player.getColor());
				((Square) selected).displayColor();
				((Square) selected).disableSquare();
				if(outFlanked) { switchDiscs(player, ((Square) selected).getXcoord(),((Square) selected).getYcoord()); }
				if(outFlankedH) { switchDiscsHorizontal(player, ((Square) selected).getXcoord(),((Square) selected).getYcoord()); }
				if(outFlankedDiagDec) { switchDiscsDiagonalDec(player); }
				if(outFlankedDiagInc) { switchDiscsDiagonalInc(player); }
				if(player.getDiscs().size() == 0) {
					start.updateCurrPlayer();
					Players newPlayer = start.getCurrPlayer();
					newPlayer.decreaseDiscs();
					start.updateCurrPlayer();
					player = start.getCurrPlayer();
				}
				else {
					player.decreaseDiscs();
				}
				
				System.out.println("Number of discs: " + player.getDiscs().size());
				
				start.updateCurrPlayer();
				player = start.getCurrPlayer();
				board.changeLabel("Player " + player.getPlayerNum() + "'s Turn");
				
				if(!anyValidMoves(player)) {
					start.updateCurrPlayer();
					player = start.getCurrPlayer();
					board.changeLabel("No valid moves. Player " + player.getPlayerNum() + "'s Turn");
					
				}
				if((!anyValidMoves(player1) && !anyValidMoves(player2)) || (player1.getDiscs().size() == 0 && player2.getDiscs().size() == 0)) {
					System.out.println("Here");
					EndGame endGame = new EndGame(board);
					int winner = endGame.getWinner();
					board.changeLabel("End Of Game: Player " + winner + "wins!");
					for(int i = 0; i < squares.length; i++) {
						for(int j = 0; j < squares[i].length; j++) {
							squares[i][j].disableSquare();
						}
					}
				}
			}
		}
	}
	
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
	
	private boolean checkIfOutflanked(Players player, int xcoord, int ycoord) {
		int playerColour = player.getColor();
		for(int i = xcoord+1; i <= 7; i++) {
			if(squares[xcoord+1][ycoord].getColor() == playerColour) { return false; } //right next to placed square
			//System.out.println(i + " " +ycoord);
			if(squares[i][ycoord].getColor() == 2) { break; }
			if(squares[i][ycoord].getColor() == playerColour) {
				upperBound = xcoord+1;
				lowerBound = i-1;
				
				return true;
			}
		}
		for(int j = xcoord-1; j >= 0; j--) {
			if(squares[xcoord-1][ycoord].getColor() == playerColour) { return false; } //right next to placed square
			//System.out.println(j + " " +ycoord);
			if(squares[j][ycoord].getColor() == 2) { break; }
			if(squares[j][ycoord].getColor() == playerColour) {
				upperBound = j+1;
				lowerBound = xcoord-1;
				
				return true;
			}
		}
		
		return false;
	}
	
	private boolean checkIfOutflankedHorizontal(Players player, int xcoord, int ycoord) {
		int playerColour = player.getColor();
		for(int i = ycoord+1; i <= 7; i++) {
			if(squares[xcoord][ycoord+1].getColor() == playerColour) { return false; } //right next to placed square
			//System.out.println(i + " " +ycoord);
			if(squares[xcoord][i].getColor() == 2) { break; }
			if(squares[xcoord][i].getColor() == playerColour) {
				upperBoundHorizontal = ycoord+1;
				lowerBoundHorizontal = i-1;
				
				return true;
			}
		}
		for(int j = ycoord-1; j >= 0; j--) {
			if(squares[xcoord][ycoord-1].getColor() == playerColour) { return false; } //right next to placed square
			//System.out.println(j + " " +ycoord);
			if(squares[xcoord][j].getColor() == 2) { break; }
			if(squares[xcoord][j].getColor() == playerColour) {
				upperBoundHorizontal = j+1;
				lowerBoundHorizontal = ycoord-1;
				
				return true;
			}
		}
		
		return false;
	}
	
	private boolean checkIfOutflankedDiagonalDecreasing(Players player, int xcoord, int ycoord) {
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
	
	private boolean checkIfOutflankedDiagonalIncreasing(Players player, int xcoord, int ycoord) {
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
			System.out.println(i + " " + upperBoundDiagonalIncY);
			squares[i][upperBoundDiagonalIncY].setColor(playerColour);
			squares[i][upperBoundDiagonalIncY].displayColor();
			upperBoundDiagonalIncY--;
		}
	}
	

	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}

}
