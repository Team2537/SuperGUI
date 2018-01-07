import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class SuperPrinter {

	public static void printCourse(SuperPoint p, BufferedWriter commandWriter, BufferedWriter mapWriter) {
		printCourse(p, Math.toDegrees(p.getAngle()), commandWriter, mapWriter);
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
				mapWriter.write("X" + (1d*point.getPoint().x/SuperGUI.SCALE) + "\n");
				mapWriter.write("Y" + (1d*point.getPoint().y/SuperGUI.SCALE) + "\n");
			}

			
			double currAngle = startAngle;
			double angleDiff;
			for (SuperAction a : point.getActions()) {
				angleDiff = currAngle - Math.toDegrees(a.getAngle()); // -(destAngle - starAngle) angle -> bearing
				currAngle = Math.toDegrees(a.getAngle());
				if(a.getAction() == SuperEnum.SHOOT) {
					angleDiff += 180;
					currAngle += 180;
					while(currAngle > 180) currAngle -= 360;
					while(currAngle < -180) currAngle += 360;
				}
				while(angleDiff > 180) angleDiff -= 360;
				while(angleDiff < -180) angleDiff += 360;
				
				// turn to command
				if(angleDiff != 0){
					if(commandWriter != null) commandWriter.write("\t\taddSequential(new AutoRotateCommand(" + angleDiff + "));\n");
					System.out.println("Turn " + angleDiff);					
				}
				
				// place gear/shoot
				switch (a.getAction()) {
				case GEAR:
					if(commandWriter != null) commandWriter.write("\t\taddSequential(new GearCommand());\n");
					System.out.println("Place Gear");
					break;
				case SHOOT:
					if(commandWriter != null) commandWriter.write("\t\taddSequential(new ShootCommand());\n");
					System.out.println("Shoot");
					break;
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
				if(commandWriter != null) commandWriter.write("\t\taddSequential(new AutoRotateCommand(" + angleDiff + "));\n");
				System.out.println("Turn " + angleDiff);
			}
			
			// drive distance to next point
			int y2 = point.getNext().getPoint().y;
			int x2 = point.getNext().getPoint().x;
			double distance = Math.sqrt(Math.pow(y2 - point.getPoint().y, 2) + Math.pow(x2 - point.getPoint().x, 2))
					/ SuperGUI.SCALE * 12; // distance in inches
			if (point.isBackwards()) distance = -distance;
			
			if (distance != 0) {
				if(commandWriter != null) commandWriter.write("\t\taddSequential(new CourseCorrect(" + distance + "));\n");
				System.out.println("Drive " + distance);
			}


		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println();

		printCourse(point.getNext(), destinationAngle, commandWriter, mapWriter);
	}
	
	/**
	 * Writes the autoChooser file in the robot code
	 */
	public static void writeAutoChooser(){
		if(!SuperGUI.WRITE_COMMAND)
			return;
		PrintWriter autoWriter;
		try {
			autoWriter = new PrintWriter("src/org/usfirst/frc/team2537/robot/auto/AutoChooser.java", "UTF-8");
			File dir = new File("src/org/usfirst/frc/team2537/maps/");
			File[] mapsList =  dir.listFiles();
			
			autoWriter.write("package org.usfirst.frc.team2537.robot.auto;\n\n");
			
			if(mapsList != null){
				for(File map : mapsList){
					String mapName = map.getName();
					autoWriter.write("import org.usfirst.frc.team2537.maps." + mapName.substring(0, mapName.length() - 5) + ";\n");
				}
				autoWriter.write("\n");
			}
			
			autoWriter.write("import edu.wpi.first.wpilibj.command.Command;\n");
			autoWriter.write("import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;\n\n");
						
			autoWriter.write("public class AutoChooser extends SendableChooser<Command> {\n");
			autoWriter.write("\tpublic AutoChooser() {\n");
			
			if(mapsList != null){
				for(File map : mapsList){
					String mapName = map.getName();
					mapName = mapName.substring(0, mapName.length() - 5);
					if(mapName.equals("DefaultAuto") || mapName.equals("DriveForward"))
						autoWriter.write("\t\taddDefault(\"" + mapName + "\", new " + mapName + "());\n");
					else
						autoWriter.write("\t\taddObject(\"" + mapName + "\", new " + mapName + "());\n");
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
