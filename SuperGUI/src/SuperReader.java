import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class SuperReader {
	public static SuperPoint readCourse(File f) {
		SuperPoint start = new SuperPoint(new Point(0, 0));
		try {
			BufferedReader fileReader = new BufferedReader(new FileReader(f));
			String line;
			int startx = 0;
			int starty = 0;
			int x;
			int y;
			boolean backwards = false;
			while ((line = fileReader.readLine()) != null) {
				x = (int) (Double.parseDouble(line) * SuperGUI.SCALE);
				y = (int) (Double.parseDouble(fileReader.readLine()) * SuperGUI.SCALE);
				Point tmp = new Point(x, y);
				if(!backwards)
					start.point(tmp);
				else {
					start.point(new Point(2*startx - x, 2*starty - y));
				}
				start.add(tmp);

				startx = x;
				starty = y;
				backwards = Boolean.parseBoolean(fileReader.readLine());
				fileReader.readLine();
			}
			fileReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return start.getNext();
	}
}
