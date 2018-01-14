/**
 * List of right click actions
 * When adding actions, remember to modify switches in SuperPrinter and SuperPoint
 *
 */
public enum SuperEnum {
	SWITCH("SWITCH"),
	SCALE("SCALE"),
	PICKUP("PICKUP"),
	ROTATE("ROTATE"),
	/* ACTION("ACTION NAME") */;

	final String name;
	private SuperEnum(String name){
		this.name = name;
	}
}
