import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class SuperReader {
	public static SuperPoint readCourse(File f) {
		SuperPoint start = new SuperPoint(new Point(0, 0));
		try {
			BufferedReader fileReader = new BufferedReader(new FileReader(f));
			String line;
			double startx = 0;
			double starty = 0;
			double x;
			double y;
			boolean backwards = false;
			while ((line = fileReader.readLine()) != null) {
				x = Double.parseDouble(line);
				y = Double.parseDouble(fileReader.readLine());

				Point2D.Double tmp = new Point2D.Double(x, y);
				if(!backwards)
					start.point(tmp);
				else {
					start.point(new Point2D.Double(2*startx - x, 2*starty - y));
				}
				start.add(tmp);

				startx = x;
				starty = y;
				backwards = Boolean.parseBoolean(fileReader.readLine());

				while((line = fileReader.readLine()).length() > 0) {
					start.addAction(SuperAction.readSuperAction(line));
				}
			}
			fileReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return start.getNext();
	}
}
