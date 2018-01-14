import java.awt.Polygon;
import java.awt.Rectangle;

public enum SuperObstacle {
	RED_SWITCH(14 - 7d/3, 7 + 5d/48, 14d/3, 12 + 9.5/12),
	SCALE(25, 8, 4, 11),
	BLUE_SWITCH(113d/3, 7 + 5d/48, 14d/3, 12 + 9.5/12);
	
	final Polygon shape;
	private SuperObstacle(double x, double y, double width, double height) {
		Rectangle tmp = new Rectangle((int) (x*SuperGUI.SCALE), (int) (y*SuperGUI.SCALE), (int) (width*SuperGUI.SCALE), (int) (height*SuperGUI.SCALE));
		int minX = (int) tmp.getMinX();
		int maxX = (int) tmp.getMaxX();
		int minY = (int) tmp.getMinY();
		int maxY = (int) tmp.getMaxY();
		shape = new Polygon(new int[] {minX, maxX, maxX, minX}, new int[] {minY, minY, maxY, maxY}, 4);
	}
	
	private SuperObstacle(double[] xpoints, double[] ypoints) {
		if(xpoints.length != ypoints.length) throw new IllegalArgumentException();
		int[] scaledXpoints = new int[xpoints.length];
		int[] scaledYpoints = new int[ypoints.length];
		for(int i = 0; i < xpoints.length; i++) {
			scaledXpoints[i] = (int) (xpoints[i] * SuperGUI.SCALE);
			scaledYpoints[i] = (int) (ypoints[i] * SuperGUI.SCALE);
		}
		shape = new Polygon(scaledXpoints, scaledYpoints, xpoints.length);
	}
}
