package filereader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileHelper {

	public static String getCurrentUsername() {
		return System.getProperty("user.name");
	}

	public static List<String> getFilesOfDirectory(String directory) {
		List<String> filenames = new ArrayList<String>();
		File[] files = new File(directory).listFiles();
		for (File file : files) {
			if (file.isFile()) {
				filenames.add(file.getName());
			}
		}
		return filenames;
	}

}
