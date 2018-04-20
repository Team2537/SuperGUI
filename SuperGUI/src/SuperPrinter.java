import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class SuperPrinter {

	public static void printCourse(SuperPoint p, BufferedWriter commandWriter, BufferedWriter mapWriter) {
//		printCourse(p, Math.toDegrees(p.getAngle()), commandWriter, mapWriter);
		printAbsoluteCourse(p, p.getPoint().x, p.getPoint().y);
	}
	
	public static void printAbsoluteCourse(SuperPoint point, double startx, double starty) {
		if (point == null) throw new IllegalArgumentException("Null point");
		System.out.println("x: " + ((point.getPoint().x - startx) * .3048));
		System.out.println("y: " + ((starty - point.getPoint().y) * .3048));
		System.out.println(Math.toDegrees(point.getAngle()));
		System.out.println();

		if(point.getNext() == null) return;
		printAbsoluteCourse(point.getNext(), startx, starty);
	}

	/**
	 * Prints the course
	 *
	 * @param point
	 *            - current point
	 * @param startAngle
	 *            - current angle (not bearing) in degrees
	 */
	public static void printCourse(SuperPoint point, double startAngle, BufferedWriter commandWriter, BufferedWriter mapWriter) {
		if (point == null) throw new IllegalArgumentException("Null point");

		double destinationAngle = Math.toDegrees(point.getAngle());
		try {
			if (mapWriter != null) {
				mapWriter.write((point.getPoint().x) + "\n");
				mapWriter.write((point.getPoint().y) + "\n");
				mapWriter.write("" + point.isBackwards() + "\n");
				for(SuperAction a : point.getActions()) {
					mapWriter.write(a.toString() + "\n");
				}
				mapWriter.write('\n');
			}

			double currAngle = startAngle;
			double angleDiff;
			for (SuperAction a : point.getActions()) {
				angleDiff = currAngle - Math.toDegrees(a.getAngle()); // -(destAngle - starAngle) angle -> bearing
				currAngle = Math.toDegrees(a.getAngle());
				//				if(a.getAction() == SuperEnum.SHOOT) {
				//					angleDiff += 180;
				//					currAngle += 180;
				//					while(currAngle > 180) currAngle -= 360;
				//					while(currAngle < -180) currAngle += 360;
				//				}
				while(angleDiff > 180) angleDiff -= 360;
				while(angleDiff < -180) angleDiff += 360;

				// turn to command
				if(angleDiff != 0){
					if(commandWriter != null) commandWriter.write("\t\taddSequential(new " + SuperGUI.AUTOROTATE_COMMAND.substring(SuperGUI.AUTOROTATE_COMMAND.lastIndexOf('.') + 1) + "(" + angleDiff + "));\n");
					System.out.println("Turn " + angleDiff);
				}

				// place gear/shoot
				if(a.getAction() != SuperEnum.ROTATE && commandWriter != null) {
					String commandName = a.getAction().command.substring(a.getAction().command.lastIndexOf('.') + 1);
					commandWriter.write("\t\taddSequential(new " + commandName + "());\n");
					System.out.println(commandName);
				}
			}

			if(point.getNext() == null) {
				writeAutoChooser();
				return;
			}

			angleDiff = currAngle - destinationAngle;
			while(angleDiff > 180) angleDiff -= 360;
			while(angleDiff < -180) angleDiff += 360;

			if (angleDiff != 0) {
				if(commandWriter != null) commandWriter.write("\t\taddSequential(new " + SuperGUI.AUTOROTATE_COMMAND.substring(SuperGUI.AUTOROTATE_COMMAND.lastIndexOf('.') + 1) + "(" + angleDiff + "));\n");
				System.out.println("Turn " + angleDiff);
			}

			// drive distance to next point
			double distance = point.getPoint().distance(point.getNext().getPoint()) * 12;
			if (point.isBackwards()) distance = -distance;

			if (distance != 0) {
				if(commandWriter != null) commandWriter.write("\t\taddSequential(new " + SuperGUI.AUTODRIVE_COMMAND.substring(SuperGUI.AUTODRIVE_COMMAND.lastIndexOf('.') + 1) + "(" + distance + "));\n");
				System.out.println("Drive " + distance);
			}


		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println();

		printCourse(point.getNext(), destinationAngle, commandWriter, mapWriter);
	}

	/**printCourse
	 * Writes the autoChooser file in the robot code
	 */
	public static void writeAutoChooser(){
		if(!SuperGUI.WRITE_COMMAND)
			return;
		PrintWriter autoWriter;
		try {
			autoWriter = new PrintWriter(SuperGUI.AUTOCHOOSER_LOCATION, "UTF-8");
			File dir = new File(SuperGUI.COMMANDS_DIRECTORY);
			File[] commandsList =  dir.listFiles();

			autoWriter.write("package " + SuperGUI.AUTOCHOOSER_LOCATION.substring(4, SuperGUI.AUTOCHOOSER_LOCATION.lastIndexOf("/")).replace('/', '.') + ";\n\n");

			if(commandsList != null){
				for(File command : commandsList){
					if(command.getPath().equals(SuperGUI.AUTOCHOOSER_LOCATION)) continue;
					String commandName = command.getName();
					autoWriter.write("import " + SuperGUI.COMMANDS_DIRECTORY.substring(4, SuperGUI.COMMANDS_DIRECTORY.length()).replace('/', '.') + commandName.substring(0, commandName.length() - 5) + ";\n");
				}
				autoWriter.write("\n");
			}

			autoWriter.write("import edu.wpi.first.wpilibj.command.Command;\n");
			autoWriter.write("import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;\n\n");

			autoWriter.write("public class AutoChooser extends SendableChooser<Command> {\n");
			autoWriter.write("\tpublic AutoChooser() {\n");

			if(commandsList != null){
				for(File command : commandsList){
					if(command.getPath().equals(SuperGUI.AUTOCHOOSER_LOCATION)) continue;
					String commandName = command.getName();
					commandName = commandName.substring(0, commandName.length() - 5);
					if(commandName.equals("DefaultAuto") || commandName.equals("DriveForward"))
						autoWriter.write("\t\taddDefault(\"" + commandName + "\", new " + commandName + "());\n");
					else
						autoWriter.write("\t\taddObject(\"" + commandName + "\", new " + commandName + "());\n");
				}
			}

			autoWriter.write("\t}\n");
			autoWriter.write("}");

			autoWriter.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
