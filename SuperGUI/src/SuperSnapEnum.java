import java.awt.Point;


public enum SuperSnapEnum {
	RED_CENTER(16.2,161.4,"Red Center Start"),RED_TOP(16.2,72.6, "Red Top Start"),RED_BOTTOM(16.2,228,"Red Bottom Start"),BLUE_CENTER(635.4,161.4,"Blue Center Start"),BLUE_TOP(635.4,72.6,"Blue Top Start"),BLUE_BOTTOM(635.4,228, "Blue Bottom Start"),RED_GEAR_CENTER(113.4,160.8, "Red Center Gear Placement"),RED_GEAR_TOP(114.3,142.2,"Red Top Gear Placement"),RED_GEAR_BOTTOM(114.3,183, "Red Bottom Gear Placement"),BLUE_GEAR_CENTER(537.7,160.8, "Blue Center Grea Placement"),BLUE_GEAR_TOP(537.7,142.2,"Blue Gear Top Placement"),BLUE_GEAR_BOTTOM(537.7,183, "Blue Gear Bottom Placement") /*, SNAP_POSITION(xpos_inches, ypos_inches, "pos name" */;
	Point p;
	String name;
	private SuperSnapEnum(double xpos, double ypos, String posName){
		p = new Point((int)(xpos*SuperGUI.SCALE/12),(int)(ypos*SuperGUI.SCALE/12));
		name = posName;
		
	}

}
