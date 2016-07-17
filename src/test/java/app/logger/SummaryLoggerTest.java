package app.logger;

import org.junit.Test;

public class SummaryLoggerTest {
	
	StatusLogger logger = StatusLogger.getInstance();

	@Test
	public void test() {
		logger.summaryLogger.logSkippedRestaurant("韶山冲", "failed to get address info");
		logger.summaryLogger.logSkippedRestaurant("巴蜀风", "failed to get address info");
		logger.summaryLogger.logTotalRecordsFromDB(1000);
		logger.summaryLogger.logTotalRows(800);
		logger.summaryLogger.print();
	}

}
