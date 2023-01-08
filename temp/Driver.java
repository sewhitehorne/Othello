import java.io.IOException;

import javax.swing.UIManager;
public class Driver {
	
	public static void main(String[] args) throws IOException
	{
		try
		{
		   UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName() );
		}
		catch (Exception e)
		{
		   e.printStackTrace();

		}
		
		// create a new GUI window
		new BoardGUI();
		//GameBoard game = new GameBoard();
		
		//Kingdom demo = new Kingdom();
	}

}
