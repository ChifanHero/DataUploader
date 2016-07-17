package app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import com.mongodb.MongoClient;

import app.config.GoogleConfig;
import app.config.MongoDBConfig;
import app.logger.StatusLogger;
import app.tasks.DataFilterTask;
import app.tasks.FileDataNormalizeTask;
import app.tasks.GeocodingTask;
import app.tasks.LoadExistingDataTask;
import app.tasks.ReadDataFromFileTask;
import app.tasks.SaveDataTask;
import data.mongodb.DBManager;
import data.mongodb.MongoClientFactory;
import filereader.FileHelper;
import github.familysyan.concurrent.tasks.TaskConfiguration;
import github.familysyan.concurrent.tasks.orchestrator.Orchestrator;
import github.familysyan.concurrent.tasks.orchestrator.OrchestratorFactory;

public class DataUploadApp {
	
	
	
	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException, TimeoutException {
		DBManager manager = new DBManager(MongoDBConfig.URI_STAGING);
		MongoClient client = manager.createClient();
		MongoClientFactory.setClient(client);
		intializeOrchestrator();
		List<String> filenames = getFilenames();
		String path = "/Users/" + FileHelper.getCurrentUsername() + "/Documents/chifanhero/data/upload/";
		List<String> fullPaths = new ArrayList<String>();
		for (String file : filenames) {
			String filepath = path + file;
			fullPaths.add(filepath);
		}
		Orchestrator orchestrator = OrchestratorFactory.getOrchestrator();
		ReadDataFromFileTask readTask = new ReadDataFromFileTask(fullPaths);
		orchestrator.acceptTask(readTask);
		LoadExistingDataTask loadTask = new LoadExistingDataTask(MongoDBConfig.DATABASE_STAGING);
		orchestrator.acceptTask(loadTask);
		GeocodingTask geoTask = new GeocodingTask(orchestrator, GoogleConfig.GEOCODING_API_KEY);
		TaskConfiguration geoTC = new TaskConfiguration(geoTask).addDependency(readTask);
		orchestrator.acceptTask(geoTask, geoTC);
		FileDataNormalizeTask normalizeTask = new FileDataNormalizeTask();
		TaskConfiguration normalizeTC = new TaskConfiguration(normalizeTask).addDependency(geoTask).addDependency(readTask);
		orchestrator.acceptTask(normalizeTask, normalizeTC);
		DataFilterTask fiterTask = new DataFilterTask();
		TaskConfiguration filterTC = new TaskConfiguration(fiterTask).addDependency(normalizeTask).addDependency(loadTask);
		orchestrator.acceptTask(fiterTask, filterTC);
		SaveDataTask saveTask = new SaveDataTask(MongoDBConfig.DATABASE_STAGING);
		TaskConfiguration saveTC = new TaskConfiguration(saveTask).addDependency(fiterTask);
		orchestrator.acceptTask(saveTask, saveTC);
		orchestrator.getTaskResult(saveTask.getUniqueTaskId());
		StatusLogger.getInstance().print();
		orchestrator.shutdown();
		client.close();
		System.exit(1);
		
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
		ExecutorService executor = Executors.newFixedThreadPool(55);
		Orchestrator orchestrator = new Orchestrator.Builder(executor).build(); 
		OrchestratorFactory.setOrchestrator(orchestrator);
	}

}
