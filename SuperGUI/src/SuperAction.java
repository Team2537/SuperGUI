/**
 * Right Click Actions
 * 
 * if adding new right click actions, modify SuperEnum
 */
public class SuperAction {
	private SuperEnum action;
	private double angle;

	public SuperAction(SuperEnum action, double angle) {
		this.action = action;
		this.angle = angle;
	}

	public SuperEnum getAction() {
		return action;
	}

	public double getAngle() {
		return angle;
	}

	public static SuperAction readSuperAction(String s) {
		int center = s.indexOf('@');
		SuperEnum action = SuperEnum.valueOf(s.substring(0, center));
		double angle = Double.parseDouble(s.substring(center + 1));
		return new SuperAction(action, angle);
	}
	
	@Override
	public String toString() {
		return action + "@" + angle;
	}
}
