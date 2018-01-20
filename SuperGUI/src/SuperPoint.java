import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.text.DecimalFormat;
import java.util.LinkedList;

public class SuperPoint {

	private SuperPoint next;
	private Point position;
	private double angle; // radians
	private LinkedList<SuperAction> actions;
	private int alpha;
	private boolean backwards;
	private double startAngle;
	private static Point midpoint = null;
	private static DecimalFormat df = new DecimalFormat("#.##");

	/**
	 * Creates a new SuperBot
	 * @param p - position of the bot (pixels)
	 */
	public SuperPoint(Point p) {
		angle = 550;
		this.position = (Point) p.clone();
		next = null;
		actions = new LinkedList<>();
		alpha = 255;
		backwards = false;
	}

	/**
	 * Adds a new bot to the end of the list
	 * @param p - position of the last bot (pixels)
	 */
	public void add(Point p) {
		if (next == null) {
			next = new SuperPoint(p);
			next.startAngle = this.angle;
			if (angle > -Math.PI / 2 && angle < Math.PI / 2 && p.x < this.position.x) {
				backwards = true;
			}
			if ((angle < -Math.PI / 2 || angle > Math.PI / 2) && p.x > this.position.x) {
				backwards = true;
			}
			if (angle == Math.PI / 2 && p.y > this.position.y) {
				backwards = true;
			}
			if (angle == -Math.PI / 2 && p.y < this.position.y) {
				backwards = true;
			}
		} else {
			next.add(p);
		}
	}

	/**
	 * Makes the final robot point towards a point
	 * @param p - The point to point to (pixels)
	 */
	public void point(Point p) {
		if (next != null) {
			next.point(p);
			return;
		}

		if(p.equals(this.position)) angle = startAngle;
		else angle = Math.atan2(this.position.y - p.y, p.x - this.position.x);

		midpoint = new Point((p.x+this.position.x)/2,(p.y+this.position.y)/2);
	}

	public double getAngle() {
		return angle;
	}

	public double getFinalAngle() {
		if (next == null) return angle;
		return next.getFinalAngle();
	}

	public boolean removeFinalSuperPoint() {
		if (next != null && next.next== null){
			next = null;
			return true;
		}
		return next.removeFinalSuperPoint();
	}

	public boolean isBackwards() {
		return backwards;
	}

	/**
	 * Returns if placing a robot in this position would be a valid move.
	 * A valid move is defined as a move where the robot would not run into any objects
	 *
	 * @param nextPoint
	 * @return
	 */
	public boolean isValidMove(Point nextPoint, boolean followingCursor, boolean botOnly) {
		if(next != null) return next.isValidMove(nextPoint, followingCursor, botOnly);
		double angle = Math.atan2(-nextPoint.y + this.position.y, nextPoint.x - this.position.x);

		// Use the front/back of both robots instead of the center
		Point startPoint = new Point(this.position);
		if(followingCursor) {
			startPoint.x = (int) (position.x - SuperGUI.ROBOT_LENGTH/2 * SuperGUI.SCALE * Math.cos(angle));
			startPoint.y = (int) (position.y + SuperGUI.ROBOT_LENGTH/2 * SuperGUI.SCALE * Math.sin(angle));
		}
		Point endPoint = new Point();
		if(!botOnly) {
			endPoint.x = (int) (nextPoint.x + SuperGUI.ROBOT_LENGTH/2 * SuperGUI.SCALE * Math.cos(angle));
			endPoint.y = (int) (nextPoint.y - SuperGUI.ROBOT_LENGTH/2 * SuperGUI.SCALE * Math.sin(angle));
		} else {
			endPoint.x = (int) (position.x + SuperGUI.ROBOT_LENGTH/2 * SuperGUI.SCALE * Math.cos(angle));
			endPoint.y = (int) (position.y - SuperGUI.ROBOT_LENGTH/2 * SuperGUI.SCALE * Math.sin(angle));
		}

		Area path = new Area(new Polygon(
				new int[] {
						startPoint.x + (int) (.5 * SuperGUI.ROBOT_WIDTH * SuperGUI.SCALE * Math.cos(angle + Math.PI / 2)),
						startPoint.x + (int) (.5 * SuperGUI.ROBOT_WIDTH * SuperGUI.SCALE * Math.cos(angle - Math.PI / 2)),
						endPoint.x + (int) (.5 * SuperGUI.ROBOT_WIDTH * SuperGUI.SCALE * Math.cos(angle - Math.PI / 2)),
						endPoint.x + (int) (.5 * SuperGUI.ROBOT_WIDTH * SuperGUI.SCALE * Math.cos(angle + Math.PI / 2)) },
				new int[] {
						startPoint.y - (int) (.5 * SuperGUI.ROBOT_WIDTH * SuperGUI.SCALE * Math.sin(angle + Math.PI / 2)),
						startPoint.y - (int) (.5 * SuperGUI.ROBOT_WIDTH * SuperGUI.SCALE * Math.sin(angle - Math.PI / 2)),
						endPoint.y - (int) (.5 * SuperGUI.ROBOT_WIDTH * SuperGUI.SCALE	* Math.sin(angle - Math.PI / 2)),
						endPoint.y - (int) (.5 * SuperGUI.ROBOT_WIDTH * SuperGUI.SCALE	* Math.sin(angle + Math.PI / 2)) },
				4));

		double robotDiagonal = Math.atan2(SuperGUI.ROBOT_WIDTH, SuperGUI.ROBOT_LENGTH) * 180/Math.PI;
		double angleDiff = (angle-startAngle)*180/Math.PI;
		while(angleDiff > 180) angleDiff -= 360;
		while(angleDiff < -180) angleDiff += 360;
		Arc2D frontLeft = new Arc2D.Double(position.x - SuperGUI.ROBOT_DIAMETER*SuperGUI.SCALE/2, position.y - SuperGUI.ROBOT_DIAMETER*SuperGUI.SCALE/2, SuperGUI.ROBOT_DIAMETER*SuperGUI.SCALE, SuperGUI.ROBOT_DIAMETER*SuperGUI.SCALE, startAngle * 180/Math.PI - robotDiagonal, angleDiff, Arc2D.PIE);
		Arc2D frontRight = new Arc2D.Double(position.x - SuperGUI.ROBOT_DIAMETER*SuperGUI.SCALE/2, position.y - SuperGUI.ROBOT_DIAMETER*SuperGUI.SCALE/2, SuperGUI.ROBOT_DIAMETER*SuperGUI.SCALE, SuperGUI.ROBOT_DIAMETER*SuperGUI.SCALE, startAngle * 180/Math.PI + robotDiagonal, angleDiff, Arc2D.PIE);
		Arc2D backLeft = new Arc2D.Double(position.x - SuperGUI.ROBOT_DIAMETER*SuperGUI.SCALE/2, position.y - SuperGUI.ROBOT_DIAMETER*SuperGUI.SCALE/2, SuperGUI.ROBOT_DIAMETER*SuperGUI.SCALE, SuperGUI.ROBOT_DIAMETER*SuperGUI.SCALE, 180 + startAngle * 180/Math.PI - robotDiagonal, angleDiff, Arc2D.PIE);
		Arc2D backRight = new Arc2D.Double(position.x - SuperGUI.ROBOT_DIAMETER*SuperGUI.SCALE/2, position.y - SuperGUI.ROBOT_DIAMETER*SuperGUI.SCALE/2, SuperGUI.ROBOT_DIAMETER*SuperGUI.SCALE, SuperGUI.ROBOT_DIAMETER*SuperGUI.SCALE, 180 + startAngle * 180/Math.PI + robotDiagonal, angleDiff, Arc2D.PIE);

		path.add(new Area(frontLeft));
		path.add(new Area(frontRight));
		path.add(new Area(backLeft));
		path.add(new Area(backRight));

		// Obstacle collision
		for(SuperObstacle o : SuperObstacle.values()) {
			Area a = new Area(o.scaledShape);
			a.intersect(path);
			if(!a.isEmpty()) return false;
		}

		// Border collision
		if(path.intersects(0, -1, SuperGUI.FIELD_LENGTH*SuperGUI.SCALE, 1)) return false;
		if(path.intersects(-1, 0, 1, SuperGUI.FIELD_WIDTH*SuperGUI.SCALE)) return false;
		if(path.intersects(0, SuperGUI.FIELD_WIDTH*SuperGUI.SCALE, SuperGUI.FIELD_LENGTH*SuperGUI.SCALE, 1)) return false;
		if(path.intersects(SuperGUI.FIELD_LENGTH*SuperGUI.SCALE, 0, 1, SuperGUI.FIELD_WIDTH*SuperGUI.SCALE)) return false;
		return true;
	}

	/**
	 * removes a robot at an index > 0
	 *
	 * @param index
	 * @return success or fail
	 */
	public boolean remove(int index) {
		if (index <= 0) return false;
		if (index == 1) {
			next = null;
			return true;
		}
		if (next == null) return false;
		return next.remove(index - 1);
	}

	public void draw(Graphics g, int alpha) {
		Graphics2D g2 = (Graphics2D) g;
		this.alpha = alpha;
		if (next != null) {
			// draw path to next point
			if (!backwards) {
				g2.setColor(new Color(255, 155, 0, alpha));
			} else {
				g2.setColor(new Color(255, 0, 0, alpha));
			}
			g2.setStroke(new BasicStroke((float) (SuperGUI.ROBOT_WIDTH * SuperGUI.SCALE)));
			g2.draw(new Line2D.Float(position, next.position));
			g2.setStroke(new BasicStroke());
			g2.setColor(Color.CYAN);
			AffineTransform defaultTransform = g2.getTransform();
			AffineTransform at = new AffineTransform();
			if(angle > Math.PI/2 || angle < -Math.PI/2){
				at.rotate(Math.PI, (position.x+next.position.x)/2, (position.y +next.position.y)/2);
			}
			at.rotate(2*Math.PI-angle, (position.x+next.position.x)/2, (position.y +next.position.y)/2);
			g2.setFont(new Font(null,Font.PLAIN,20));
			g2.setTransform(at);
			g2.drawString(df.format(Point.distance(position.x,position.y,next.position.x,next.position.y)/ SuperGUI.SCALE * 12),(position.x+next.position.x)/2, (position.y+next.position.y)/2);
			g2.setTransform(defaultTransform);
		}

		// Increase alpha for points
		int botAlpha = alpha + 100;
		if (botAlpha > 255) botAlpha = 255;

		// Draw arc
		g.setColor(new Color(0, 255, 0, alpha / 5));
		int robotDiagonal = (int) (Math.atan2(SuperGUI.ROBOT_WIDTH, SuperGUI.ROBOT_LENGTH) * 180/Math.PI);

		int angleDiff;
		double initialRotateAngle = startAngle;
		for(int i = 0; i <= actions.size(); i++) {
			if(i == actions.size()) angleDiff = (int) ((angle-initialRotateAngle)*180/Math.PI);
			else angleDiff = (int) ((actions.get(i).getAngle()-initialRotateAngle)*180/Math.PI);
			while(angleDiff > 180) angleDiff -= 360;
			while(angleDiff < -180) angleDiff += 360;

			g.fillArc(position.x - (int) (SuperGUI.ROBOT_DIAMETER*SuperGUI.SCALE/2), position.y - (int) (SuperGUI.ROBOT_DIAMETER*SuperGUI.SCALE/2), (int) (SuperGUI.ROBOT_DIAMETER*SuperGUI.SCALE), (int) (SuperGUI.ROBOT_DIAMETER*SuperGUI.SCALE), (int) (initialRotateAngle * 180/Math.PI - robotDiagonal), angleDiff);
			g.fillArc(position.x - (int) (SuperGUI.ROBOT_DIAMETER*SuperGUI.SCALE/2), position.y - (int) (SuperGUI.ROBOT_DIAMETER*SuperGUI.SCALE/2), (int) (SuperGUI.ROBOT_DIAMETER*SuperGUI.SCALE), (int) (SuperGUI.ROBOT_DIAMETER*SuperGUI.SCALE), (int) (initialRotateAngle * 180/Math.PI + robotDiagonal), angleDiff);
			g.fillArc(position.x - (int) (SuperGUI.ROBOT_DIAMETER*SuperGUI.SCALE/2), position.y - (int) (SuperGUI.ROBOT_DIAMETER*SuperGUI.SCALE/2), (int) (SuperGUI.ROBOT_DIAMETER*SuperGUI.SCALE), (int) (SuperGUI.ROBOT_DIAMETER*SuperGUI.SCALE), 180 + (int) (initialRotateAngle * 180/Math.PI - robotDiagonal), angleDiff);
			g.fillArc(position.x - (int) (SuperGUI.ROBOT_DIAMETER*SuperGUI.SCALE/2), position.y - (int) (SuperGUI.ROBOT_DIAMETER*SuperGUI.SCALE/2), (int) (SuperGUI.ROBOT_DIAMETER*SuperGUI.SCALE), (int) (SuperGUI.ROBOT_DIAMETER*SuperGUI.SCALE), 180 + (int) (initialRotateAngle * 180/Math.PI + robotDiagonal), angleDiff);

			if(i < actions.size()) initialRotateAngle = actions.get(i).getAngle();
		}

		// draw square for bot
		g.setColor(new Color(0, 255, 0, botAlpha));
		double cornerAngle = Math.atan2(SuperGUI.ROBOT_WIDTH, SuperGUI.ROBOT_LENGTH);
		g.fillPolygon(
				new int[] {
						position.x + (int) (.5 * SuperGUI.ROBOT_DIAMETER * SuperGUI.SCALE * Math.cos(cornerAngle + angle)),
						position.x + (int) (.5 * SuperGUI.ROBOT_DIAMETER * SuperGUI.SCALE * Math.cos(angle - cornerAngle)),
						position.x + (int) (.5 * SuperGUI.ROBOT_DIAMETER * SuperGUI.SCALE * Math.cos(Math.PI + cornerAngle + angle)),
						position.x + (int) (.5 * SuperGUI.ROBOT_DIAMETER * SuperGUI.SCALE * Math.cos(Math.PI + angle - cornerAngle)) },
				new int[] {
						position.y - (int) (.5 * SuperGUI.ROBOT_DIAMETER * SuperGUI.SCALE * Math.sin(cornerAngle + angle)),
						position.y - (int) (.5 * SuperGUI.ROBOT_DIAMETER * SuperGUI.SCALE * Math.sin(angle - cornerAngle)),
						position.y - (int) (.5 * SuperGUI.ROBOT_DIAMETER * SuperGUI.SCALE * Math.sin(Math.PI + cornerAngle + angle)),
						position.y - (int) (.5 * SuperGUI.ROBOT_DIAMETER * SuperGUI.SCALE * Math.sin(Math.PI + angle - cornerAngle)) },
				4);

		// draw arrow within bot
		g.setColor(new Color(0, 0, 255, botAlpha));
		double distance = SuperGUI.ROBOT_DIAMETER * SuperGUI.SCALE / 2.0;
		double arrowSize = distance / 2;
		g.drawPolyline(
				new int[] {
						position.x + (int) (distance * 2 / 5 * Math.cos(angle + Math.PI)),
						position.x + (int) (distance * 3 / 5 * Math.cos(angle)),
						position.x + (int) (distance * 3 / 5 * Math.cos(angle) + arrowSize * Math.cos(angle + Math.PI * 5 / 6)),
						position.x + (int) (distance * 3 / 5 * Math.cos(angle)),
						position.x + (int) (distance * 3 / 5 * Math.cos(angle) + arrowSize * Math.cos(angle - Math.PI * 5 / 6)) },
				new int[] {
						position.y - (int) (distance * 2 / 5 * Math.sin(angle + Math.PI)),
						position.y - (int) (distance * 3 / 5 * Math.sin(angle)),
						position.y - (int) (distance * 3 / 5 * Math.sin(angle) + arrowSize * Math.sin(angle + Math.PI * 5 / 6)),
						position.y - (int) (distance * 3 / 5 * Math.sin(angle)),
						position.y - (int) (distance * 3 / 5 * Math.sin(angle) + arrowSize * Math.sin(angle - Math.PI * 5 / 6)) },
				5);

		// draw actions
		for (SuperAction a : actions) {
			// draw arrow
			switch (a.getAction()) {
			case SWITCH:
				g.setColor(new Color(0, 0, 255));
				break;
			case SCALE:
				g.setColor(new Color(255, 0, 0));
				break;
			case PICKUP:
				g.setColor(new Color(0, 255, 0));
				break;
			case ROTATE:
				g.setColor(new Color(255, 255, 255));
				break;
			}

			double arrowStart = SuperGUI.ROBOT_DIAMETER * SuperGUI.SCALE / 2; // distance of start of arrow from center of point
			double arrowEnd = SuperGUI.ROBOT_DIAMETER * SuperGUI.SCALE; // distance of tip of arrow from center of point
			g.drawPolyline(
					new int[] {
							(int) (position.x + arrowStart * Math.cos(a.getAngle())),
							(int) (position.x + arrowEnd * Math.cos(a.getAngle())),
							(int) (position.x + arrowEnd * Math.cos(a.getAngle()) + (arrowEnd - arrowStart) / 2 * Math.cos(a.getAngle() + Math.PI * 5 / 6)),
							(int) (position.x + arrowEnd * Math.cos(a.getAngle())),
							(int) (position.x + arrowEnd * Math.cos(a.getAngle()) + (arrowEnd - arrowStart) / 2 * Math.cos(a.getAngle() - Math.PI * 5 / 6)) },
					new int[] {
							(int) (position.y - arrowStart * Math.sin(a.getAngle())),
							(int) (position.y - arrowEnd * Math.sin(a.getAngle())),
							(int) (position.y - arrowEnd * Math.sin(a.getAngle()) - (arrowEnd - arrowStart) / 2 * Math.sin(a.getAngle() + Math.PI * 5 / 6)),
							(int) (position.y - arrowEnd * Math.sin(a.getAngle())),
							(int) (position.y - arrowEnd * Math.sin(a.getAngle()) - (arrowEnd - arrowStart) / 2 * Math.sin(a.getAngle() - Math.PI * 5 / 6)) },
					5);
		}

		if (next != null) next.draw(g, alpha);
		else {
			//drawing user info
			AffineTransform defaultTransform = g2.getTransform();
			AffineTransform at = new AffineTransform();
			if(angle > Math.PI/2 || angle < -Math.PI/2){
				at.rotate(Math.PI, midpoint.x, midpoint.y);
			}
			at.rotate(2*Math.PI-angle, midpoint.x, midpoint.y);
			g2.setTransform(at);
			g2.setColor(Color.CYAN);
			g2.setFont(new Font(null,Font.PLAIN,14));
			g2.drawString(df.format(Point.distance(position.x,position.y, midpoint.x,midpoint.y)/ SuperGUI.SCALE * 12*2),midpoint.x,midpoint.y);

			at = new AffineTransform();
			if(angle > Math.PI/2 || angle < -Math.PI/2){
				at.rotate(Math.PI,(midpoint.x+position.x)/2, (midpoint.y+position.y)/2);
			}
			at.rotate(2*Math.PI-angle, (midpoint.x+position.x)/2, (midpoint.y+position.y)/2);
			g2.setTransform(at);
			g2.setColor(Color.WHITE);
			if(SuperPanel.relativeAngles){
				g2.drawString(df.format((angle-startAngle)*180/Math.PI)+Character.toString((char) 176), (midpoint.x+position.x)/2, (midpoint.y+position.y)/2);
			}
			else{
				g2.drawString(df.format((angle)*180/Math.PI)+Character.toString((char) 176), (midpoint.x+position.x)/2, (midpoint.y+position.y)/2);
			}
			g2.setTransform(defaultTransform);

			g2.setColor(new Color(0, 0, 0, 25));
			g2.setStroke(new BasicStroke((float) (SuperGUI.ROBOT_WIDTH*SuperGUI.SCALE)));
			if (angle == Math.PI / 2 || angle == -Math.PI / 2) {
				g2.draw(new Line2D.Float(position.x, 0, position.x, (int) (SuperGUI.FIELD_WIDTH * SuperGUI.SCALE)));
			} else {
				g2.draw(new Line2D.Float(0, position.y + (int) (Math.tan(angle) * position.x),
						(int) (SuperGUI.FIELD_LENGTH * SuperGUI.SCALE),
						(int) (position.y - Math.tan(angle) * (SuperGUI.FIELD_LENGTH * SuperGUI.SCALE - position.x))));
			}
			g2.setStroke(new BasicStroke());
		}
	}

	/**
	 * Adds an action to the final robot
	 * @param a
	 */
	public void addAction(SuperAction a) {
		if (next == null) actions.add(a);
		else next.addAction(a);
	}

	public LinkedList<SuperAction> getActions() {
		return actions;
	}

	public int getNumBots() {
		if (next == null) return 1;
		return 1 + next.getNumBots();
	}

	public Point getPoint() {
		return position;
	}

	public Point getFinalPoint() {
		if (next == null) return position;
		return next.getFinalPoint();
	}

	public SuperPoint getNext() {
		return next;
	}
	public void updateFinalDistance(Point p){
		if (next != null) {
			next.updateFinalDistance(p);
			return;
		}
		midpoint = new Point((int) ((p.x+this.position.x)*(0.5)),(int) ((p.y+this.position.y)*(0.5)));
	}

}
