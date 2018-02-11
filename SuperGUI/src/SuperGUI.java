import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

/**
 * Creates an interactive gui to plan robot actions
 *
 * When customizing for specific games:
 *  - Confirm field and robot dimensions are correct
 *  - Add robot actions in SuperEnum (eg. rotate, place gear)
 *  - Customize those actions in the switch statements in SuperPrinter and SuperPoint
 *  - Customize obstacles and their positions in SuperObstacle
 *  - Customize the preset positions in SuperSnapEnum
 *
 * Keybindings:
 * lclick - create robot
 * rclick - open SuperMenu (do some action)
 * space - stop turning to follow cursor, snap cursor to line. Useful for going backwards.
 * h - hide obstacles
 * c - clear all points
 * bakaspace - remove latest point
 * s - open SuperSnapMenu (go to preset point)
 * o - open map file
 * enter - print course
 * esc - quit
 *
 *
 * @author Arden Zhang
 * @author Andrew Wollack
 * @author Alex Taber (sort of)
 *
 */
public class SuperGUI {

	public static final double FIELD_LENGTH = 54; // feet
	public static final double FIELD_WIDTH = 27; // feet
	public static final double CORNER_LENGTH = 2 + 11d/12; // length of corners (x) in feet
	public static final double CORNER_WIDTH = (FIELD_WIDTH-22)/2; // width of corners (y) in feet
	public static final double FENCE_WIDTH = 0.1; // width of the field borders in feet. Temporarily set to 0.1
	public static final double SCALE; // px/ft

	public static final double ROBOT_LENGTH = 33d / 12; // feet
	public static final double ROBOT_WIDTH = 28d / 12; // feet
	public static final double ROBOT_DIAMETER = Math.sqrt(Math.pow(ROBOT_LENGTH, 2) + Math.pow(ROBOT_WIDTH, 2));

	public static final boolean WRITE_COMMAND = true; // write the code to a command and generate autoChooser
	public static final boolean WRITE_MAP = true; // write the code to a readable map file

	public static final String COMMANDS_DIRECTORY = "src/commands/"; // src/org/usfirst/frc/team2537/robot/autocommands/
	public static final String MAPS_DIRECTORY = "src/maps/";
	public static final String AUTOCHOOSER_LOCATION = "src/commands/AutoChooser.java"; // src/org/usfirst/frc/team2537/robot/auto/AutoChooser.java

	public static final String AUTOROTATE_COMMAND = "org.usfirst.frc.team2537.robot.auto.RotateCommand";
	public static final String AUTODRIVE_COMMAND = "org.usfirst.frc.team2537.robot.auto.DriveStraightCommand";

	public static final Color cursorColor = new Color(255, 0, 0);
	public static final Color obstacleColor = new Color(0, 0, 0, 200);

	static {
		Dimension w = Toolkit.getDefaultToolkit().getScreenSize();
		SCALE = w.getHeight()/FIELD_WIDTH*2/3;
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("SuperGUI");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		//		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
		//		frame.getContentPane().setCursor(blankCursor);

		SuperPanel panel = new SuperPanel();

		frame.add(panel);
		frame.addKeyListener(panel);
		panel.requestFocusInWindow();

		frame.setUndecorated(true);
		frame.setResizable(false);

		frame.pack();
		frame.setVisible(true);
	}

}
