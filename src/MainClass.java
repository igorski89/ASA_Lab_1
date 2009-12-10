import javax.swing.JFrame;


public class MainClass {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
    	MainFrame mainWindow = new MainFrame();
    	mainWindow.setLocation(100, 100);
    	mainWindow.setSize(1000, 600);
    	mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//    	mainWindow.setTitle("Main Window");
    	mainWindow.setVisible(true);
	}

}
