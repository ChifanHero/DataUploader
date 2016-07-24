package app;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import com.mongodb.MongoClient;

import app.bean.formatter.RestaurantFormatter;
import app.config.MongoDBConfig;
import app.logger.StatusLogger;
import app.tasks.LoadExistingDataTask;
import app.tasks.SaveDataTask;
import data.mongodb.DBManager;
import data.mongodb.MongoClientFactory;
import github.familysyan.concurrent.tasks.Task;
import github.familysyan.concurrent.tasks.TaskConfiguration;
import github.familysyan.concurrent.tasks.orchestrator.Orchestrator;
import github.familysyan.concurrent.tasks.orchestrator.OrchestratorFactory;

public class EnglishNameFormatApp {
	
	public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
		
		DBManager manager = new DBManager(MongoDBConfig.URI);
		MongoClient client = manager.createClient();
		MongoClientFactory.setClient(client);
		intializeOrchestrator();
		Orchestrator orchestrator = OrchestratorFactory.getOrchestrator();
		LoadExistingDataTask loadTask = new LoadExistingDataTask(MongoDBConfig.DATABASE);
		orchestrator.acceptTask(loadTask);
		EnglishnameFormatTask formatTask = new EnglishnameFormatTask();
		TaskConfiguration tc = new TaskConfiguration(formatTask);
		tc.addDependency(loadTask);
		orchestrator.acceptTask(formatTask, tc);
		SaveDataTask saveTask = new SaveDataTask(MongoDBConfig.DATABASE);
		TaskConfiguration tc2 = new TaskConfiguration(saveTask).addDependency(formatTask);
		orchestrator.acceptTask(saveTask, tc2);
		orchestrator.getTaskResult(saveTask.getUniqueTaskId());
		StatusLogger.getInstance().print();
		orchestrator.shutdown();
		System.exit(1);
		
	}
	
	private static void intializeOrchestrator() {
		ExecutorService executor = Executors.newFixedThreadPool(100);
		Orchestrator orchestrator = new Orchestrator.Builder(executor).build(); 
		OrchestratorFactory.setOrchestrator(orchestrator);
	}
	
	private static class EnglishnameFormatTask implements Task<List<Map<String, Object>>> {

		@Override
		public String getUniqueTaskId() {
			return this.getClass().getName();
		}

		@SuppressWarnings("unchecked")
		@Override
		public List<Map<String, Object>> execute(List<Object> dependencies) {
			List<Map<String, Object>> restaurants = (List<Map<String, Object>>) dependencies.get(0);
			Iterator<Map<String, Object>> iterator = restaurants.iterator();
			while (iterator.hasNext()) {
				Map<String, Object> restaurant = iterator.next();
				if (restaurant.get("english_name") != null) {
					iterator.remove();
				} else {
					String name = (String)restaurant.get("name");
					if (name != null && !RestaurantFormatter.containsHanScript(name)) {
						restaurant.put("english_name", name);
					}
				}
			}
			return restaurants;
		}

		@Override
		public void failedToComplete() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public long getTimeout() {
			// TODO Auto-generated method stub
			return 0;
		}
		
	}

}
