package com.appleframework.server.test.utils;

import com.appleframework.server.netty.connection.Connection;

/**
 * 
 * GPS服务平台管理系统
 * 
 * @author sw
 * @date 2010-5-20
 * @version 1.0
 * 
 */
public class NettyUtil {
	
	/**
	 * @param src
	 * @return
	 */
	public static String getRemoteAddress(Connection connection) {
		return connection.getChannel().remoteAddress().toString();
	}

	
}
