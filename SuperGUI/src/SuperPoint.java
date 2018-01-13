import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.text.DecimalFormat;
import java.util.LinkedList;

public class SuperPoint {

	private SuperPoint next;
	private Point p;
	private double angle; // radians
	private LinkedList<SuperAction> actions;
	private int alpha;
	private boolean backwards;
	private Double startAngle = null;
	private static Point midpoint = null;
	private static DecimalFormat df = new DecimalFormat("#.##");

	/**
	 * Adds a SuperBot to the end of the bot list
	 *
	 * @param p
	 *            - position of the last bot in the chain
	 */
	public SuperPoint(Point p) {
		angle = 550;
		this.p = (Point) p.clone();
		next = null;
		actions = new LinkedList<>();
		alpha = 255;
		backwards = false;
	}

	// adds new point in pixel position
	public void add(Point p) {
		if (next == null) {
			next = new SuperPoint(p);
			if (angle > -Math.PI / 2 && angle < Math.PI / 2 && p.x < this.p.x) {
				backwards = true;
			}
			if ((angle < -Math.PI / 2 || angle > Math.PI / 2) && p.x > this.p.x) {
				backwards = true;
			}
			if (angle == Math.PI / 2 && p.y > this.p.y) {
				backwards = true;
			}
			if (angle == -Math.PI / 2 && p.y < this.p.y) {
				backwards = true;
			}
		} else {
			next.add(p);
		}
	}

	/**
	 * Makes the final robot point towards a point
	 *
	 * @param p
	 *            - The point to point to
	 */
	public void point(Point p) {
		if (next != null) {
			
			if(next.next == null){
				next.startAngle = angle ;
			}
			next.point(p);
			return;
		}

		if(startAngle == null){
			startAngle = (double) 0; 
		}
		angle = Math.atan2(this.p.y - p.y, p.x - this.p.x);

		midpoint = new Point((p.x+this.p.x)/2,(p.y+this.p.y)/2);
	}
	
	public double getAngle() {
		return angle;
	}

	public double getFinalAngle() {
		if (next == null) return angle;
		else return next.getFinalAngle();
	}
	public boolean removeFinalSuperPoint() {
		if (next != null && next.next== null){
			next = null;
			return true;
		}
		else return next.removeFinalSuperPoint();
	}

	public boolean isBackwards() {
		return backwards;
	}

	/**
	 * returns the index of the robot containing point p
	 *
	 * @param p
	 * @return
	 */
	public int contains(Point p) {
		return contains(p, 0);
	}

	public int contains(Point p, int index) {
		// bot collision
		if (Math.sqrt(Math.pow(p.x - this.p.x, 2) + Math.pow(p.y - this.p.y, 2)) <= SuperGUI.ROBOT_DIAMETER / 2
				* SuperGUI.SCALE)
			return index;

		// path collision
		if (next != null && alpha > 50) {
			double angle = Math.atan2(-next.getPoint().y + this.p.y, next.getPoint().x - this.p.x);
			Polygon path = new Polygon(new int[] {
					this.p.x + (int) (.5 * SuperGUI.ROBOT_WIDTH * SuperGUI.SCALE * Math.cos(angle + Math.PI / 2)),
					this.p.x + (int) (.5 * SuperGUI.ROBOT_WIDTH * SuperGUI.SCALE * Math.cos(angle - Math.PI / 2)),
					next.getPoint().x
							+ (int) (.5 * SuperGUI.ROBOT_WIDTH * SuperGUI.SCALE * Math.cos(angle - Math.PI / 2)),
					next.getPoint().x
							+ (int) (.5 * SuperGUI.ROBOT_WIDTH * SuperGUI.SCALE * Math.cos(angle + Math.PI / 2)) },

					new int[] { this.p.y
							- (int) (.5 * SuperGUI.ROBOT_WIDTH * SuperGUI.SCALE * Math.sin(angle + Math.PI / 2)),
							this.p.y - (int) (.5 * SuperGUI.ROBOT_WIDTH * SuperGUI.SCALE
									* Math.sin(angle - Math.PI / 2)),
							next.getPoint().y - (int) (.5 * SuperGUI.ROBOT_WIDTH * SuperGUI.SCALE
									* Math.sin(angle - Math.PI / 2)),
							next.getPoint().y - (int) (.5 * SuperGUI.ROBOT_WIDTH * SuperGUI.SCALE
									* Math.sin(angle + Math.PI / 2)) },

					4);
			if (path.contains(p)) return index + 1;
		}

		if (next == null) return -1;

		return next.contains(p, index + 1);
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
			g2.draw(new Line2D.Float(p, next.p));
			g2.setStroke(new BasicStroke());
			g2.setColor(Color.CYAN);
			AffineTransform defaultTransform = g2.getTransform();
			AffineTransform at = new AffineTransform();
			if(angle > Math.PI/2 || angle < -Math.PI/2){
				at.rotate(Math.PI, (p.x+next.p.x)/2, (p.y +next.p.y)/2);
			}
			at.rotate(2*Math.PI-angle, (p.x+next.p.x)/2, (p.y +next.p.y)/2);
			g2.setFont(new Font(null,Font.PLAIN,20));
			g2.setTransform(at);
			g2.drawString(df.format(Point.distance(p.x,p.y,next.p.x,next.p.y)/ SuperGUI.SCALE * 12),(p.x+next.p.x)/2, (p.y+next.p.y)/2);
			g2.setTransform(defaultTransform);
			// draw arrow within path
			// g2.setColor(new Color(255, 255, 255, alpha));
			// double distance = Math.sqrt(Math.pow(next.getPoint().x - p.x, 2)
			// + Math.pow(-next.getPoint().y + p.y, 2));
			// double arrowSize = distance / 15;
			// if (arrowSize > SuperGUI.ROBOT_DIAMETER * SuperGUI.SCALE / 2.0)
			// arrowSize = SuperGUI.ROBOT_DIAMETER * SuperGUI.SCALE / 2.0;
			// g.drawPolyline(new int[]{
			// p.x + (int) (distance * 2 / 5 * Math.cos(angle)),
			// p.x + (int) (distance * 3 / 5 * Math.cos(angle)),
			// p.x + (int) (distance * 3 / 5 * Math.cos(angle)
			// + arrowSize * Math.cos(angle + Math.PI * 5 / 6)),
			// p.x + (int) (distance * 3 / 5 * Math.cos(angle)),
			// p.x + (int) (distance * 3 / 5 * Math.cos(angle)
			// + arrowSize * Math.cos(angle - Math.PI * 5 / 6))},
			//
			// new int[]{p.y - (int) (distance * 2 / 5 * Math.sin(angle)),
			// p.y - (int) (distance * 3 / 5 * Math.sin(angle)),
			// p.y - (int) (distance * 3 / 5 * Math.sin(angle)
			// + arrowSize * Math
			// .sin(angle + Math.PI * 5 / 6)),
			// p.y - (int) (distance * 3 / 5 * Math.sin(angle)),
			// p.y - (int) (distance * 3 / 5 * Math.sin(angle)
			// + arrowSize * Math
			// .sin(angle - Math.PI * 5 / 6))},
			//
			// 5);
		}

		// Increase alpha for points
		int botAlpha = alpha + 100;
		if (botAlpha > 255) botAlpha = 255;

		// Draw translucent circle around point
		g.setColor(new Color(0, 255, 0, alpha / 2));
		g.fillOval(p.x - (int) (SuperGUI.ROBOT_DIAMETER / 2 * SuperGUI.SCALE),
				p.y - (int) (SuperGUI.ROBOT_DIAMETER / 2 * SuperGUI.SCALE),
				(int) (SuperGUI.SCALE * SuperGUI.ROBOT_DIAMETER), (int) (SuperGUI.SCALE * SuperGUI.ROBOT_DIAMETER));

		// draw square for bot
		g.setColor(new Color(0, 255, 0, botAlpha));
		double cornerAngle = Math.atan2(SuperGUI.ROBOT_WIDTH, SuperGUI.ROBOT_LENGTH);
		g.fillPolygon(new int[] {
				p.x + (int) (.5 * SuperGUI.ROBOT_DIAMETER * SuperGUI.SCALE * Math.cos(cornerAngle + angle)),
				p.x + (int) (.5 * SuperGUI.ROBOT_DIAMETER * SuperGUI.SCALE * Math.cos(angle - cornerAngle)),
				p.x + (int) (.5 * SuperGUI.ROBOT_DIAMETER * SuperGUI.SCALE * Math.cos(Math.PI + cornerAngle + angle)),
				p.x + (int) (.5 * SuperGUI.ROBOT_DIAMETER * SuperGUI.SCALE * Math.cos(Math.PI + angle - cornerAngle)) },

				new int[] { p.y - (int) (.5 * SuperGUI.ROBOT_DIAMETER * SuperGUI.SCALE * Math.sin(cornerAngle + angle)),
						p.y - (int) (.5 * SuperGUI.ROBOT_DIAMETER * SuperGUI.SCALE * Math.sin(angle - cornerAngle)),
						p.y - (int) (.5 * SuperGUI.ROBOT_DIAMETER * SuperGUI.SCALE
								* Math.sin(Math.PI + cornerAngle + angle)),
						p.y - (int) (.5 * SuperGUI.ROBOT_DIAMETER * SuperGUI.SCALE
								* Math.sin(Math.PI + angle - cornerAngle)) },

				4);
		// draw arrow within bot
		g.setColor(new Color(0, 0, 255, botAlpha));
		double distance = SuperGUI.ROBOT_DIAMETER * SuperGUI.SCALE / 2.0;
		double arrowSize = distance / 2;
		g.drawPolyline(new int[] { p.x + (int) (distance * 2 / 5 * Math.cos(angle + Math.PI)),
				p.x + (int) (distance * 3 / 5 * Math.cos(angle)),
				p.x + (int) (distance * 3 / 5 * Math.cos(angle) + arrowSize * Math.cos(angle + Math.PI * 5 / 6)),
				p.x + (int) (distance * 3 / 5 * Math.cos(angle)),
				p.x + (int) (distance * 3 / 5 * Math.cos(angle) + arrowSize * Math.cos(angle - Math.PI * 5 / 6)) },

				new int[] { p.y - (int) (distance * 2 / 5 * Math.sin(angle + Math.PI)),
						p.y - (int) (distance * 3 / 5 * Math.sin(angle)),
						p.y - (int) (distance * 3 / 5 * Math.sin(angle)
								+ arrowSize * Math.sin(angle + Math.PI * 5 / 6)),
						p.y - (int) (distance * 3 / 5 * Math.sin(angle)),
						p.y - (int) (distance * 3 / 5 * Math.sin(angle)
								+ arrowSize * Math.sin(angle - Math.PI * 5 / 6)) },

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

			double arrowStart = SuperGUI.ROBOT_DIAMETER * SuperGUI.SCALE / 2; // distance of start of arrow from center
																				// of point
			double arrowEnd = SuperGUI.ROBOT_DIAMETER * SuperGUI.SCALE; // distance of tip of arrow from center of point
			g.drawPolyline(
					new int[] { (int) (p.x + arrowStart * Math.cos(a.getAngle())),
							(int) (p.x + arrowEnd * Math.cos(a.getAngle())),
							(int) (p.x + arrowEnd * Math.cos(a.getAngle())
									+ (arrowEnd - arrowStart) / 2 * Math.cos(a.getAngle() + Math.PI * 5 / 6)),
							(int) (p.x + arrowEnd * Math.cos(a.getAngle())),
							(int) (p.x + arrowEnd * Math.cos(a.getAngle())
									+ (arrowEnd - arrowStart) / 2 * Math.cos(a.getAngle() - Math.PI * 5 / 6)) },
					new int[] { (int) (p.y - arrowStart * Math.sin(a.getAngle())),
							(int) (p.y - arrowEnd * Math.sin(a.getAngle())),
							(int) (p.y - arrowEnd * Math.sin(a.getAngle())
									- (arrowEnd - arrowStart) / 2 * Math.sin(a.getAngle() + Math.PI * 5 / 6)),
							(int) (p.y - arrowEnd * Math.sin(a.getAngle())),
							(int) (p.y - arrowEnd * Math.sin(a.getAngle())
									- (arrowEnd - arrowStart) / 2 * Math.sin(a.getAngle() - Math.PI * 5 / 6)) },
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
			g2.drawString(df.format(Point.distance(p.x,p.y, midpoint.x,midpoint.y)/ SuperGUI.SCALE * 12*2),midpoint.x,midpoint.y);
			
			at = new AffineTransform();
			if(angle > Math.PI/2 || angle < -Math.PI/2){
				at.rotate(Math.PI,(midpoint.x+p.x)/2, (midpoint.y+p.y)/2);
			}
			at.rotate(2*Math.PI-angle, (midpoint.x+p.x)/2, (midpoint.y+p.y)/2);
			g2.setTransform(at);
			g2.setColor(Color.WHITE);
			if(SuperPanel.relativeAngles){
				g2.drawString(df.format((angle-startAngle)*180/Math.PI)+Character.toString((char) 176), (midpoint.x+p.x)/2, (midpoint.y+p.y)/2);
			}
			else{
				g2.drawString(df.format((angle)*180/Math.PI)+Character.toString((char) 176), (midpoint.x+p.x)/2, (midpoint.y+p.y)/2);
			}
			g2.setTransform(defaultTransform);
			
			g2.setColor(new Color(0, 0, 0, 25));
			g2.setStroke(new BasicStroke((float) (SuperGUI.ROBOT_WIDTH*SuperGUI.SCALE)));
			if (angle == Math.PI / 2 || angle == -Math.PI / 2) {
				g2.draw(new Line2D.Float(p.x, 0, p.x, (int) (SuperGUI.FIELD_WIDTH * SuperGUI.SCALE)));
			} else {
				g2.draw(new Line2D.Float(0, p.y + (int) (Math.tan(angle) * p.x),
						(int) (SuperGUI.FIELD_LENGTH * SuperGUI.SCALE),
						(int) (p.y - Math.tan(angle) * (SuperGUI.FIELD_LENGTH * SuperGUI.SCALE - p.x))));
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
		return p;
	}

	public Point getFinalPoint() {
		if (next == null) return p;
		else return next.getFinalPoint();
	}

	public SuperPoint getNext() {
		return next;
	}
	public void updateFinalDistance(Point p){
		if (next != null) {
			next.updateFinalDistance(p);
			return;
		}
		midpoint = new Point((int) ((p.x+this.p.x)*(0.5)),(int) ((p.y+this.p.y)*(0.5)));
	}

}
