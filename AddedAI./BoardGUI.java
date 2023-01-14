
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.border.EtchedBorder;

public class BoardGUI extends JFrame implements ActionListener, MouseListener {

	
	private JFrame frame;
	private JPanel gameBoardPanel, playerPanel;
	private JLabel playerLabel;
	private Square [][] squares;
	
	public BoardGUI() {
		
		frame = new JFrame("Othello");
		frame.setSize(750,750);
		
		gameBoardPanel = new JPanel(new GridLayout(8,8));
		playerLabel = new JLabel("Player 1's Turn");
		playerPanel = new JPanel();
		playerPanel.add(playerLabel);
		
		squares = new Square[8][8];
		
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				squares[i][j] = new Square(i, j);
				//squares[i][j].addActionListener(this);
				squares[i][j].setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED, Color.gray, Color.gray));
				gameBoardPanel.add(squares[i][j]);
			}
		}
		
		initializeGame();
		
		//gameBoardPanel.setBackground(Color.BLACK);
		//gameBoardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		frame.add(gameBoardPanel, BorderLayout.CENTER);
		frame.add(playerPanel, BorderLayout.NORTH);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		frame.setVisible(true);
		
		new BoardController(this);
		
		
		
	}
	
	public Square[][] getSquares() { return squares; }
	
	private void initializeGame() {
		squares[3][3].setColor(0);
		squares[3][3].displayColor();
		squares[3][3].disableSquare();
		squares[4][4].setColor(0);
		squares[4][4].displayColor();
		squares[4][4].disableSquare();
		squares[3][4].setColor(1);
		squares[3][4].displayColor();
		squares[3][4].disableSquare();
		squares[4][3].setColor(1);
		squares[4][3].displayColor();
		squares[4][3].disableSquare();
		
	}
	
	public void placeDisc(int xcoord, int ycoord, int color) {
		squares[xcoord][ycoord].setColor(color);
		squares[xcoord][ycoord].displayColor();
	}
	
	public void changeLabel(String labelText) {
		playerLabel.setText(labelText);
	}
	
	
	
	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void actionPerformed(ActionEvent e) {}
	

}
