import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

/**
 * Panel
 *
 * @author Arden Zhang
 */
public class SuperPanel extends JPanel implements KeyListener, MouseMotionListener, MouseListener, MouseWheelListener, ActionListener {

	private static final int mouseSize = 8; // pixels
	private static final int toggleFollowCursorKey = KeyEvent.VK_SPACE;
	private static final int printCourseKey = KeyEvent.VK_ENTER;
	private static final int exitKey = KeyEvent.VK_ESCAPE;
	private static final int relativeAngleToggle = KeyEvent.VK_R;
	private static final int openSnapMenu = KeyEvent.VK_S;
	private static final int deleteAll = KeyEvent.VK_C;
	private static final int deleteLast = KeyEvent.VK_BACK_SPACE;

	private Image field;
	private boolean followCursor;
	private Point mousePos;
	private SuperPoint startingPoint;
	private int botTransparency;
	private JFrame jframe;
	private SuperMenu menu;
	private JPopupMenu snapMenu;
	public static boolean relativeAngles =false;

	public SuperPanel() {
		field = new ImageIcon("SuperGUI/FIELD.jpg").getImage();
		addKeyListener(this);
		addMouseMotionListener(this);
		addMouseListener(this);
		addMouseWheelListener(this);
		setPreferredSize(new Dimension((int) (SuperGUI.FIELD_LENGTH * SuperGUI.SCALE),
				(int) (SuperGUI.FIELD_WIDTH * SuperGUI.SCALE)));
		followCursor = true;
		mousePos = new Point(0, 0);
		botTransparency = 255;
		jframe = new JFrame();
		menu = new SuperMenu(this);
		snapMenu = new SuperSnapeMenu(this);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(field, 0, 0, null);

		if (startingPoint != null) {
			if (followCursor && !menu.isVisible()) startingPoint.point(mousePos);
			startingPoint.draw(g, botTransparency);
		}

		g.setColor(new Color(255, 255, 0));
		g.drawOval(mousePos.x - mouseSize, mousePos.y - mouseSize, mouseSize * 2, mouseSize * 2);
	}

	private void quit() {
		System.exit(0);
	}

	@Override
	public void keyPressed(KeyEvent k) {
		if(k.getKeyCode() == deleteAll){
			startingPoint = null;
		}
		if(k.getKeyCode() == deleteLast){
			if(startingPoint!= null){
				startingPoint.removeFinalSuperPoint();
			}
		}
		if(k.getKeyCode() == openSnapMenu){

			snapMenu.show(k.getComponent(),mousePos.x,mousePos.y);
			
		}
		if (k.getKeyCode() == toggleFollowCursorKey) followCursor = !followCursor;
		if (k.getKeyCode() == relativeAngleToggle) relativeAngles = !relativeAngles;
		if (k.getKeyCode() == printCourseKey) {
			System.out.println("Course================" + startingPoint.getNumBots());
			String mapName;
			if(SuperGUI.WRITE_MAP) {
				mapName = (String) JOptionPane.showInputDialog(jframe, "Enter map name:\n", "File Name",
						JOptionPane.PLAIN_MESSAGE, null, null, "");
				if (mapName != null) {
					File fl = new File("src/org/usfirst/frc/team2537/maps/" + mapName + ".java");
					try {
						BufferedWriter writer = new BufferedWriter(new FileWriter(fl));
						writer.write("package org.usfirst.frc.team2537.maps;\n\n");
						writer.write("import org.usfirst.frc.team2537.robot.auto.AutoRotateCommand;\n");
						writer.write("import org.usfirst.frc.team2537.robot.auto.CourseCorrect;\n");
						writer.write("import org.usfirst.frc.team2537.robot.auto.GearCommand;\n\n");
						writer.write("import edu.wpi.first.wpilibj.command.CommandGroup;\n\n");
						writer.write("public class " + mapName + " extends CommandGroup {\n");
						writer.write("\tpublic " + mapName + "() {\n");
						
						if(startingPoint.getPoint().x < SuperGUI.FIELD_LENGTH*SuperGUI.SCALE/2)
							SuperPrinter.printCourse(startingPoint, 0, writer);
						else
							SuperPrinter.printCourse(startingPoint, 180, writer);
						
						writer.write("\t}\n");
						writer.write("}\n");
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} else {
				if(startingPoint.getPoint().x < SuperGUI.FIELD_LENGTH*SuperGUI.SCALE/2)
					SuperPrinter.printCourse(startingPoint, 0, null);
				else
					SuperPrinter.printCourse(startingPoint, 180, null);				
			}

		}
		if (k.getKeyCode() == exitKey) quit();
		repaint();
	}

	@Override
	public void keyReleased(KeyEvent k) {
		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent m) {
		mousePos.x = m.getX();
		mousePos.y = m.getY();

		if (startingPoint != null && !followCursor){
			mousePos = snap(mousePos);
		}
		if(startingPoint == null){
			if(mousePos.x < SuperGUI.ROBOT_LENGTH*SuperGUI.SCALE/2)
				mousePos.x = (int) (SuperGUI.ROBOT_LENGTH*SuperGUI.SCALE/2);
			if(mousePos.x > (SuperGUI.FIELD_LENGTH-SuperGUI.ROBOT_LENGTH/2)*SuperGUI.SCALE)
				mousePos.x = (int) ((SuperGUI.FIELD_LENGTH-SuperGUI.ROBOT_LENGTH/2)*SuperGUI.SCALE);
			if(mousePos.y < SuperGUI.ROBOT_WIDTH*SuperGUI.SCALE/2)
				mousePos.y = (int) (SuperGUI.ROBOT_WIDTH*SuperGUI.SCALE/2);
			if(mousePos.y > (SuperGUI.FIELD_WIDTH-SuperGUI.ROBOT_WIDTH/2)*SuperGUI.SCALE)
				mousePos.y = (int) ((SuperGUI.FIELD_WIDTH-SuperGUI.ROBOT_WIDTH/2)*SuperGUI.SCALE);	
			
		}

		
		if(startingPoint != null && !menu.isVisible()){
			startingPoint.updateFinalDistance(mousePos);
		}

		repaint();
	}

	/**
	 * Snaps a point to the line that the SuperPoint is currently pointing to
	 *
	 * @param p
	 *            - point to snap to the angle of SuperPoint
	 * @return the point on the SuperPoint direction line closest to the inputted point
	 */
	private Point snap(Point p) {
		double slope = Math.tan(startingPoint.getFinalAngle()); // slope of final point
		double x;
		double y;
		Point result;
		if(slope == 0){
			result = new Point(p.x, startingPoint.getFinalPoint().y);
		} else {
			double invslope = -1 / slope; // slope of line perpendicular

			// y-intercept of perpendicular line
			double b_perp = startingPoint.getFinalPoint().y - p.y - invslope * (p.x - startingPoint.getFinalPoint().x); // of

			x = (b_perp - 0) / (slope - invslope);
			y = -slope * x + 0;
			result = new Point((int) (x + startingPoint.getFinalPoint().x), (int) (y + startingPoint.getFinalPoint().y));
		}
		
		if(result.distance(startingPoint.getFinalPoint()) <= SuperGUI.ROBOT_DIAMETER*SuperGUI.SCALE/5){
			return (Point) startingPoint.getFinalPoint().clone();
		}else{
			return result;
		}
	}

	@Override
	public void mouseClicked(MouseEvent m) {
		if (SwingUtilities.isRightMouseButton(m)) {
			menu.show(m.getComponent(), m.getX(), m.getY());
		} else {
			if (startingPoint == null)
				startingPoint = new SuperPoint(mousePos);
			else {
				startingPoint.add(mousePos);
				followCursor = true;
			}
		}
		repaint();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent m) {
		botTransparency -= 10 * m.getPreciseWheelRotation();
		if (botTransparency > 255) botTransparency = 255;
		if (botTransparency < 0) botTransparency = 0;
		repaint();
	}

	@Override
	public void keyTyped(KeyEvent k) {}

	@Override
	public void mousePressed(MouseEvent m) {}

	@Override
	public void mouseReleased(MouseEvent m) {}

	@Override
	public void mouseDragged(MouseEvent m) {
		mouseMoved(m);
	}

	@Override
	public void mouseEntered(MouseEvent m) {}

	@Override
	public void mouseExited(MouseEvent m) {}

	@Override
	public void actionPerformed(ActionEvent e){
		if(startingPoint != null){
			double angle = Math.atan2(startingPoint.getFinalPoint().y - mousePos.y, mousePos.x - startingPoint.getFinalPoint().x);
			for(int i = 0 ; i<SuperEnum.values().length;i++){
				if(e.getActionCommand().equals(SuperEnum.values()[i].name)){
					startingPoint.addAction(new SuperAction(SuperEnum.values()[i], angle));
				}
			}
		}
		for(int i = 0 ; i<SuperSnapEnum.values().length;i++){
			if(e.getActionCommand().equals(SuperSnapEnum.values()[i].name)){
				if (startingPoint == null)
					startingPoint = new SuperPoint(SuperSnapEnum.values()[i].p);
				else {
					startingPoint.point(SuperSnapEnum.values()[i].p);
					startingPoint.add(SuperSnapEnum.values()[i].p);
					followCursor = true;
				}
			}
		}
		repaint();
	}

	public boolean isRelativeAngles() {
		return relativeAngles;
	}

	public void setRelativeAngles(boolean relativeAngles) {
		this.relativeAngles = relativeAngles;
	}
}
