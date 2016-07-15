package data.mongodb;

import java.util.Random;

public class IdGenerator {
	
	public static String getNewObjectId() {
		return randomString(10);
	}

	private static String randomString(int size) {
		if (size == 0) {
		    throw new IllegalArgumentException("Zero-length randomString is useless.");
		  }
		char[] chars= "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
		  String objectId = "";
		  byte[] bytes = new byte[size];
		  new Random().nextBytes(bytes);
		  for (int i = 0; i < bytes.length; ++i) {
		    objectId += chars[UIntFromByte(bytes[i]) % chars.length];
		  }
		  return objectId;
	}
	
	public static int UIntFromByte(byte b) {
		int value = b;
		if (value < 0) value += 256;
		return value;
	}
	
	public static void main(String[] args) {
		for (int i=0; i < 10; i++) {
			System.out.println(getNewObjectId());
		}
		
	}

}
