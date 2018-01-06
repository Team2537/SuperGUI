
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

	@Override
	public String toString() {
		return action + "@" + angle;
	}
}
