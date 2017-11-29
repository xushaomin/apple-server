package com.appleframework.server.test.model;

import java.util.Hashtable;
import java.util.Map;

public class DeviceData {

    // 跟踪器编号--->客户端缓存(key:sn,value:ip:port)
    private static Map<String, String> TRACKERNO_REMOTEADDR = new Hashtable<String, String>();

    // 客户端--->跟踪器编号缓存(key:ip:port,value:sn)
    private static Map<String, String> REMOTEADDR_TRACKERNO = new Hashtable<String, String>();
    
    public static void add(String sn, String remoteAddr) {
    	TRACKERNO_REMOTEADDR.put(sn, remoteAddr);
    	REMOTEADDR_TRACKERNO.put(remoteAddr, sn);
    }
    
    public static String getSn(String remoteAddr) {
    	return REMOTEADDR_TRACKERNO.get(remoteAddr);
    }

}
