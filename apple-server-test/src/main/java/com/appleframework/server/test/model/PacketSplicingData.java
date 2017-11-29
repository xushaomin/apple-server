package com.appleframework.server.test.model;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;


public class PacketSplicingData implements Serializable {

	private static final long serialVersionUID = -1L;

	private static Map<String, byte[]> data = new Hashtable<String, byte[]>();

	public static void put(String key, byte[] value) {
		data.put(key, value);
	}
	
	public static byte[] get(String key) {
		return data.get(key);
	}
	
	public static void remove(String key) {
		data.remove(key);
	}

}
