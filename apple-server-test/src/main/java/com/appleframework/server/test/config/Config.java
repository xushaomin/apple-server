package com.appleframework.server.test.config;

import java.util.Map;

/**
 * 
 * GPS服务平台管理系统
 * 
 * @author sw
 * @date 2010-5-21
 * @version 1.0
 * 
 */
public class Config {

	public static Map<String, String> config = null;

	public static int trackerServerListenPort;
	public static String log4j;
	
	public static String consumseractiveAddress;
	public static String consumseractiveName;
	public static String consumserUsername;
    public static String consumserPassword;
    public static String consumserVhost;
    
	public static String produceractiveAddress;
	public static String producerActiveName;
	public static String producerUsername;
    public static String producerPassword;
    public static String producerVhost;
    public static String producerExchangeName;
    public static String producerExchangeType;
	
	public static String commandactiveAddress;
	public static String commandactiveName;
	public static String commandUsername;
    public static String commandPassword;
    public static String commandVhost;
    public static String commandExchangeName;
    public static String commandExchangeType;
    
	public static String epoFilePath;

	public static String epoTaskTime;
	
	
    public static String mongoDBHosts;// 192.168.51.100:2,192.168.51.100:2
    public static String mongoDBName;
    public static String mongoDBUsername;
    public static String mongoDBPassword;
    public static Integer mongoDBTimeOut;// 5秒
    public static Integer writeLogTime;// 日志写入任务间隔执行时间，分钟
	public static String Plam;
}
