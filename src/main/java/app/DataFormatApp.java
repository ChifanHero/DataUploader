package app;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import com.mongodb.MongoClient;

import app.config.GoogleConfig;
import app.config.MongoDBConfig;
import app.logger.StatusLogger;
import app.tasks.FileDataNormalizeTask;
import app.tasks.GeocodingTask;
import app.tasks.LoadExistingDataTask;
import app.tasks.SaveDataTask;
import data.mongodb.DBManager;
import data.mongodb.MongoClientFactory;
import github.familysyan.concurrent.tasks.TaskConfiguration;
import github.familysyan.concurrent.tasks.orchestrator.Orchestrator;
import github.familysyan.concurrent.tasks.orchestrator.OrchestratorFactory;

public class DataFormatApp {
	
	public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
		
		DBManager manager = new DBManager(MongoDBConfig.URI);
		MongoClient client = manager.createClient();
		MongoClientFactory.setClient(client);
		intializeOrchestrator();
		Orchestrator orchestrator = OrchestratorFactory.getOrchestrator();
		LoadExistingDataTask loadTask = new LoadExistingDataTask(MongoDBConfig.DATABASE);
		orchestrator.acceptTask(loadTask);
		GeocodingTask geoTask = new GeocodingTask(orchestrator, GoogleConfig.GEOCODING_API_KEY);
		TaskConfiguration geoTC = new TaskConfiguration(geoTask).addDependency(loadTask);
		orchestrator.acceptTask(geoTask, geoTC);
		FileDataNormalizeTask normalizeTask = new FileDataNormalizeTask();
		TaskConfiguration normalizeTC = new TaskConfiguration(normalizeTask).addDependency(geoTask).addDependency(loadTask);
		orchestrator.acceptTask(normalizeTask, normalizeTC);
//		DataFilterTask fiterTask = new DataFilterTask();
//		TaskConfiguration filterTC = new TaskConfiguration(fiterTask).addDependency(normalizeTask).addDependency(loadTask);
//		orchestrator.acceptTask(fiterTask, filterTC);
		SaveDataTask saveTask = new SaveDataTask(MongoDBConfig.DATABASE);
		TaskConfiguration saveTC = new TaskConfiguration(saveTask).addDependency(normalizeTask);
		orchestrator.acceptTask(saveTask, saveTC);
		orchestrator.getTaskResult(saveTask.getUniqueTaskId());
		StatusLogger.getInstance().print();
		orchestrator.shutdown();
		client.close();
		System.exit(1);
		
	}
	
	private static void intializeOrchestrator() {
		ExecutorService executor = Executors.newFixedThreadPool(100);
		Orchestrator orchestrator = new Orchestrator.Builder(executor).build(); 
		OrchestratorFactory.setOrchestrator(orchestrator);
	}
	
	
	

}
