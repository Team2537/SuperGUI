import java.awt.Rectangle;

public enum SuperObstacle {
	RED_SWITCH(14 - 7d/3, 7 + 5d/48, 14d/3, 12 + 9.5/12),
	SCALE(25, 8, 4, 11),
	BLUE_SWITCH(113d/3, 7 + 5d/48, 14d/3, 12 + 9.5/12);
	
	final Rectangle shape;
	private SuperObstacle(double x, double y, double width, double height) {
		shape = new Rectangle((int) (x*SuperGUI.SCALE), (int) (y*SuperGUI.SCALE), (int) (width*SuperGUI.SCALE), (int) (height*SuperGUI.SCALE));
	}
}
