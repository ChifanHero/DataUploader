package app.logger;

public class StatusLogger implements Logger{
	
	private static StatusLogger instance = new StatusLogger();
	
	public GoogleGeocodingLogger geoCodingLogger = new GoogleGeocodingLogger();
	public MongoDBLogger mongoLogger = new MongoDBLogger();
	public SummaryLogger summaryLogger = new SummaryLogger();
	
	private final static String SEPERATOR = "=================================================================";
	
	private StatusLogger() {
		
	}
	
	public static StatusLogger getInstance() {
		return instance;
	}

	@Override
	public void print() {
		mongoLogger.print();
		System.out.println(SEPERATOR);
		geoCodingLogger.print();
		System.out.println(SEPERATOR);
		summaryLogger.print();
	}

}
