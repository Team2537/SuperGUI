import java.awt.Polygon;
import java.awt.geom.Path2D;

public enum SuperObstacle {
	RED_SWITCH(14 - 7d/3, 13.5 - 12.855/2, 14d/3, 12.855),
	SCALE(25, 8, 4, 11),
	BLUE_SWITCH(40 - 7d/3, 13.5 - 12.855/2, 14d/3, 12.855),

	TOP_LEFT_CORNER(0, SuperGUI.CORNER_WIDTH, SuperGUI.CORNER_LENGTH, 0, SuperGUI.FENCE_WIDTH),
	TOP_RIGHT_CORNER(SuperGUI.FIELD_LENGTH - SuperGUI.CORNER_LENGTH, 0, SuperGUI.FIELD_LENGTH, SuperGUI.CORNER_WIDTH, SuperGUI.FENCE_WIDTH),
	BOTTOM_LEFT_CORNER(0, SuperGUI.FIELD_WIDTH - SuperGUI.CORNER_WIDTH, SuperGUI.CORNER_LENGTH, SuperGUI.FIELD_WIDTH, SuperGUI.FENCE_WIDTH),
	BOTTOM_RIGHT_CORNER(SuperGUI.FIELD_LENGTH - SuperGUI.CORNER_LENGTH, SuperGUI.FIELD_WIDTH, SuperGUI.FIELD_LENGTH, SuperGUI.FIELD_WIDTH - SuperGUI.CORNER_WIDTH, SuperGUI.FENCE_WIDTH);

	final Path2D.Double shape;
	final Polygon scaledShape;

	/**
	 * Creates a rectangular obstacle (eg. Powerup switch)
	 */
	private SuperObstacle(double x, double y, double width, double height) {
		this(new double[] {x, x+width, x+width, x}, new double[] {y, y, y+height, y+height});
	}

	/**
	 * Creates a thin barrier (eg. field corners)
	 */
	private SuperObstacle(double x1, double y1, double x2, double y2, double width) {
		double angle = Math.atan2(-y2 + y1, x2 - x1);

		shape = new Path2D.Double();
		shape.moveTo(x1 + .5 * width * Math.cos(angle + Math.PI / 2), y1 - .5 * width * Math.sin(angle + Math.PI / 2));
		shape.lineTo(x1 + .5 * width * Math.cos(angle - Math.PI / 2), y1 - .5 * width * Math.sin(angle - Math.PI / 2));
		shape.lineTo(x2 + .5 * width * Math.cos(angle - Math.PI / 2), y2 - .5 * width * Math.sin(angle - Math.PI / 2));
		shape.lineTo(x2 + .5 * width * Math.cos(angle + Math.PI / 2), y2 - .5 * width * Math.sin(angle + Math.PI / 2));
		shape.closePath();

		scaledShape = new Polygon(
				new int[] {
						(int) (x1*SuperGUI.SCALE + .5 * width * SuperGUI.SCALE * Math.cos(angle + Math.PI / 2)),
						(int) (x1*SuperGUI.SCALE + .5 * width * SuperGUI.SCALE * Math.cos(angle - Math.PI / 2)),
						(int) (x2*SuperGUI.SCALE + .5 * width * SuperGUI.SCALE * Math.cos(angle - Math.PI / 2)),
						(int) (x2*SuperGUI.SCALE + .5 * width * SuperGUI.SCALE * Math.cos(angle + Math.PI / 2)) },

				new int[] {
						(int) (y1*SuperGUI.SCALE - .5 * width * SuperGUI.SCALE * Math.sin(angle + Math.PI / 2)),
						(int) (y1*SuperGUI.SCALE - .5 * width * SuperGUI.SCALE * Math.sin(angle - Math.PI / 2)),
						(int) (y2*SuperGUI.SCALE - .5 * width * SuperGUI.SCALE * Math.sin(angle - Math.PI / 2)),
						(int) (y2*SuperGUI.SCALE - .5 * width * SuperGUI.SCALE * Math.sin(angle + Math.PI / 2)) },
				4);
	}

	/**
	 * Creates a obstacle that is a polygon (eg. Steamworks airships)
	 */
	private SuperObstacle(double[] xpoints, double[] ypoints) {
		if(xpoints.length != ypoints.length) throw new IllegalArgumentException();

		shape = new Path2D.Double();
		shape.moveTo(xpoints[0], ypoints[0]);
		for(int i = 1; i < xpoints.length; i++) {
			shape.lineTo(xpoints[i], ypoints[i]);
		}
		shape.closePath();

		int[] scaledXpoints = new int[xpoints.length];
		int[] scaledYpoints = new int[ypoints.length];
		for(int i = 0; i < xpoints.length; i++) {
			scaledXpoints[i] = (int) (xpoints[i] * SuperGUI.SCALE);
			scaledYpoints[i] = (int) (ypoints[i] * SuperGUI.SCALE);
		}
		scaledShape = new Polygon(scaledXpoints, scaledYpoints, xpoints.length);
	}
}
