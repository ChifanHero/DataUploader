package app;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import com.mongodb.MongoClient;

import app.bean.formatter.blacklist.NameBlackList;
import app.config.MongoDBConfig;
import app.logger.StatusLogger;
import app.tasks.DeleteDataTask;
import app.tasks.LoadExistingDataTask;
import data.mongodb.DBManager;
import data.mongodb.MongoClientFactory;
import github.familysyan.concurrent.tasks.Task;
import github.familysyan.concurrent.tasks.TaskConfiguration;
import github.familysyan.concurrent.tasks.orchestrator.Orchestrator;
import github.familysyan.concurrent.tasks.orchestrator.OrchestratorFactory;

public class DeleteNonChineseFoodApp {
	
	public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
		
		DBManager manager = new DBManager(MongoDBConfig.URI);
		MongoClient client = manager.createClient();
		MongoClientFactory.setClient(client);
		intializeOrchestrator();
		Orchestrator orchestrator = OrchestratorFactory.getOrchestrator();
		LoadExistingDataTask loadTask = new LoadExistingDataTask(MongoDBConfig.DATABASE);
		orchestrator.acceptTask(loadTask);
		DeleteNonChineseFoodTask wrongNameTask = new DeleteNonChineseFoodTask();
		TaskConfiguration tc = new TaskConfiguration(wrongNameTask);
		tc.addDependency(loadTask);
		orchestrator.acceptTask(wrongNameTask, tc);
		DeleteDataTask deleteTask = new DeleteDataTask(MongoDBConfig.DATABASE);
		TaskConfiguration tc2 = new TaskConfiguration(deleteTask).addDependency(wrongNameTask);
		orchestrator.acceptTask(deleteTask, tc2);
		orchestrator.getTaskResult(deleteTask.getUniqueTaskId());
		StatusLogger.getInstance().print();
		orchestrator.shutdown();
		System.exit(1);
		
	}
	
	private static void intializeOrchestrator() {
		ExecutorService executor = Executors.newFixedThreadPool(100);
		Orchestrator orchestrator = new Orchestrator.Builder(executor).build(); 
		OrchestratorFactory.setOrchestrator(orchestrator);
	}
	
	private static class DeleteNonChineseFoodTask implements Task<List<Map<String, Object>>> {

		@Override
		public String getUniqueTaskId() {
			return this.getClass().getName();
		}

		@SuppressWarnings("unchecked")
		@Override
		public List<Map<String, Object>> execute(List<Object> dependencies) {
			List<Map<String, Object>> restaurants = (List<Map<String, Object>>) dependencies.get(0);
			List<Map<String, Object>> illegalRestaurants = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> restaurant : restaurants) {
				String name = (String)restaurant.get("name");
				String englishName = (String)restaurant.get("english_name");
				for (String element : NameBlackList.blackList) {
					if (name != null && name.contains(element)) {
						illegalRestaurants.add(restaurant);
						System.out.println("name = " + name + "; english_name = " + englishName); 
						break;
					}
					if (englishName != null && englishName.toLowerCase().contains(element)) {
						illegalRestaurants.add(restaurant);
						System.out.println("name = " + name + "; english_name = " + englishName); 
						break;
					}
				}
			}
			return illegalRestaurants;
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
