package com.appleframework.server.test.utils;

public class DataUtil {

	public static int getAccesstype(int[] status) {
		int accesstype = -1;// 0:gps、1：基站;2:wifi网络;-1非定位包
		if ((status[3] & 2) == 2) {
			accesstype = 0;
		} else if ((status[2] & 64) == 64) {
			accesstype = 1;
		} else if ((status[1] & 32) == 32) {
			accesstype = 2;
		}
		return accesstype;
	}
}
