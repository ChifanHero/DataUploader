package app.logger;

import org.junit.Test;

public class MongoDBLoggerTest {
	
	MongoDBLogger logger = StatusLogger.getInstance().mongoLogger;

	@Test
	public void test1() {
		logger.setSuccess(true);
		logger.print();
	}
	
	@Test
	public void test2() {
		logger.setSuccess(false);
		logger.addError("Exception happened");
		logger.print();
	}

}
