import java.awt.Point;


public enum SuperSnapEnum {
	CENTER(SuperGUI.FIELD_LENGTH/2,SuperGUI.FIELD_WIDTH/2,"Center", false),
	/*, SNAP_POSITION(xpos_feet, ypos_feet, "pos name" */;

	final Point point;
	final String name;
	final boolean isStartingPos;
	private SuperSnapEnum(double xpos, double ypos, String posName, boolean isStartingPos){
		point = new Point((int)(xpos*SuperGUI.SCALE),(int)(ypos*SuperGUI.SCALE));
		name = posName;
		this.isStartingPos = isStartingPos;
	}

}
