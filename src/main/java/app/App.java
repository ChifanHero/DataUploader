package app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import filereader.FileHelper;
import github.familysyan.concurrent.tasks.orchestrator.Orchestrator;
import github.familysyan.concurrent.tasks.orchestrator.OrchestratorFactory;

public class App {
	
	
	
	public static void main(String[] args) throws IOException {
		
		intializeOrchestrator();
		List<String> filenames = getFilenames();
		String path = "/Users/" + FileHelper.getCurrentUsername() + "/Documents/chifanhero/data/upload/";
		String file = path + filenames.get(0);
		System.out.println(file);
		BufferedReader br = new BufferedReader(new FileReader(file));
		for (int i = 0; i < 5; i++) {
			System.out.println(br.readLine());
		}
	}

	private static List<String> getFilenames() {
		String path = "/Users/" + FileHelper.getCurrentUsername() + "/Documents/chifanhero/data/upload";
		System.out.println(path);
		List<String> files = FileHelper.getFilesOfDirectory(path);
		Iterator<String> iterator = files.iterator();
		while (iterator.hasNext()) {
			String file = iterator.next();
			if (".DS_Store".equals(file)) {
				iterator.remove();
			}
		}
		return files;
	}

	private static void intializeOrchestrator() {
		ExecutorService executor = Executors.newFixedThreadPool(5);
		Orchestrator orchestrator = new Orchestrator.Builder(executor).build(); 
		OrchestratorFactory.setOrchestrator(orchestrator);
	}

}
