package app;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import com.mongodb.MongoClient;

import app.config.MongoDBConfig;
import app.logger.StatusLogger;
import app.tasks.LoadExistingDataTask;
import app.tasks.SaveDataTask;
import app.tasks.util.GeoId;
import data.mongodb.DBManager;
import data.mongodb.MongoClientFactory;
import github.familysyan.concurrent.tasks.Task;
import github.familysyan.concurrent.tasks.TaskConfiguration;
import github.familysyan.concurrent.tasks.orchestrator.Orchestrator;
import github.familysyan.concurrent.tasks.orchestrator.OrchestratorFactory;

public class DataProcessApp {
	
	public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
		DBManager manager = new DBManager(MongoDBConfig.URI);
		MongoClient client = manager.createClient();
		MongoClientFactory.setClient(client);
		Orchestrator orchestrator = new Orchestrator.Builder().build();
		OrchestratorFactory.setOrchestrator(orchestrator);
		LoadExistingDataTask loadTask = new LoadExistingDataTask(MongoDBConfig.DATABASE);
		orchestrator.acceptTask(loadTask);
		CalculateGeoIdTask calculateTask = new CalculateGeoIdTask();
		TaskConfiguration tc = new TaskConfiguration(calculateTask);
		tc.addDependency(loadTask);
		orchestrator.acceptTask(calculateTask, tc);
		SaveDataTask saveTask = new SaveDataTask(MongoDBConfig.DATABASE);
		TaskConfiguration tc2 = new TaskConfiguration(saveTask).addDependency(calculateTask);
		orchestrator.acceptTask(saveTask, tc2);
		orchestrator.getTaskResult(saveTask.getUniqueTaskId());
		StatusLogger.getInstance().print();
		orchestrator.shutdown();
		System.exit(1);
		
	}
	
	private static class CalculateGeoIdTask implements Task<List<Map<String, Object>>> {

		@Override
		public String getUniqueTaskId() {
			return this.getClass().getName();
		}

		@SuppressWarnings("unchecked")
		@Override
		public List<Map<String, Object>> execute(List<Object> dependencies) {
			List<Map<String, Object>> restaurants = (List<Map<String, Object>>) dependencies.get(0);
			for (Map<String, Object> restaurant : restaurants) {
				List<Double> coordinates = (List<Double>) restaurant.get("coordinates");
				if (coordinates == null) {
					continue;
				}
				String geoId = GeoId.from(coordinates.get(1), coordinates.get(0));
				restaurant.put("geo_id", geoId);
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
