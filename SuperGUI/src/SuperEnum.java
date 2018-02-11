/**
 * List of right click actions
 * When adding actions, remember to modify switches in SuperPrinter and SuperPoint
 *
 */
public enum SuperEnum {
	SWITCH("SWITCH", "org.usfirst.frc.team2537.robot.auto.SwitchCommand"),
	SCALE("SCALE", "org.usfirst.frc.team2537.robot.auto.ScaleCommand"),
	PICKUP("PICKUP", "org.usfirst.frc.team2537.robot.auto.PickupCommand"),
	ROTATE("ROTATE", SuperGUI.AUTOROTATE_COMMAND),
	/* ACTION("ACTION NAME", "Location of command") */;

	final String name;
	final String command;
	private SuperEnum(String name, String command){
		this.name = name;
		this.command = command;
	}
}
