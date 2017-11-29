package com.appleframework.server.test.model;

import java.util.HashMap;
import java.util.Map;

public class AlarmStatus {
	
	private byte[] original;
	
	private String sn;
	
	public static Map<String, Integer> powerFailureAlarmMap = new HashMap<String, Integer>();
	private static Map<String, Integer> lowPowerAlarmMap = new HashMap<String, Integer>();

	public AlarmStatus(byte[] original, String sn) {
		this.original = original;
		this.sn = sn;
	}
	
	public int[] valueOf() {
		int[] result = new int[]{ 0x00, 0x00, 0x00, 0x00 };
		if(this.isAlarmData()) {
			lowPowerAlarmMap.put(sn, this.lowPowerAlarm_47());
			powerFailureAlarmMap.put(sn, this.powerFailureAlarm_31());
		}
		if(powerFailureAlarmMap.containsKey(sn)) {
			result[2] |= powerFailureAlarmMap.get(sn);
		} else
		if(lowPowerAlarmMap.containsKey(sn)) {
			result[3] |= lowPowerAlarmMap.get(sn);
		} 
		return result;
	}
	
	@SuppressWarnings("unused")
	private int circuitAlarm_12() {
		if(((this.original[31] >> 7) & 0x01) == 1) {
			return 1 << 1;
		}
		return 0;
	}
	
	@SuppressWarnings("unused")
	private int shockAlarm_21() {
		if(((this.original[31] >> 3) & 0x01) == 1) {
			return 1 ;
		}
		return 0;
	}
	
	private int powerFailureAlarm_31() {
	  //wubiao,注释掉原判断，此段会把低电报警作为断电报警解析
//      if(((this.original[31] >> 4) & 0x01) == 1) {
//          return 1 ;
//      }
//        if((this.original[31] & 56) == 16){
//            return 1;
//        }
	    //wubiao,20171101，根据希文的业务逻辑调整
        if((this.original[31] & 56) == 16) {
            return 1;
        }else if((this.original[31] & 56) == 0) {
            //000表示恢复
            return 0;
        }else{
            //如果即不为010又不为000，判断是否接电源充电
            return (this.original[31] & 4) == 4 ? 0 : 1;//接电时恢复断电,等于4是接电;
        }
	}
	
	private int lowPowerAlarm_47() {
		if(((this.original[31] >> 3) & 0x01) == 3) {
			return 1 << 7;
		}
		return 0;
	}
	
	public boolean isAlarmData() {
		if(this.isStartWith0x78()) {
			return this.original[3] == 0x26;
		} 
		return this.original[4] == 0x26;
	}
	
	public boolean isStartWith0x78() {
		return this.original[0] == 0x78 && this.original[1] == 0x78;
	}
}
