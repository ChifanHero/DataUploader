package app.bean;

public class BeanProperties {
	public static String NAME = "name";
	public static String ENGLISH_NAME = "english_name";
	public static String ADDRESS = "address";
	public static String PHONE = "phone";
	
	public static boolean contains(String property) {
		return NAME.equals(property) || ENGLISH_NAME.equals(property) || ADDRESS.equals(property) || PHONE.equals(property);
	}
}
