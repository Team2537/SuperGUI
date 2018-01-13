import java.awt.Rectangle;

public enum SuperObstacle {
	RED_SWITCH(5, 5, 5, 5, "RED SWITCH"),
	SCALE(20, 10, 4, 4, "SCALE");
	
	final Rectangle shape;
	final String name;
	private SuperObstacle(double x, double y, double width, double height, String name) {
		shape = new Rectangle((int) (x*SuperGUI.SCALE), (int) (y*SuperGUI.SCALE), (int) (width*SuperGUI.SCALE), (int) (height*SuperGUI.SCALE));
		this.name = name;
	}
}
