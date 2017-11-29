package com.appleframework.server.test.handler.by01579;

import java.util.Arrays;
import java.util.Date;

import com.appleframework.server.test.config.Config;
import com.appleframework.server.test.handler.ProtocolProcessing;
import com.appleframework.server.test.model.AlarmStatus;
import com.appleframework.server.test.model.DeviceData;
import com.appleframework.server.test.model.Status;
import com.appleframework.server.test.utils.ByteUtil;
import com.appleframework.server.test.utils.Constant;
import com.appleframework.server.test.utils.HexUtil;

public class BY01579GpsProtocol implements ProtocolProcessing {
    
    @Override
	public void handler(byte[] original, String remoteAddr) {
    	String sn = DeviceData.getSn(remoteAddr);
    	if(null == sn) {
    		sn = "1111111111";
    	}
		String mcc = this.valueOfMcc(original, sn);
		System.out.println(mcc);
	}
    
    public int[] getStatus(byte[] original, String sn) {
        return new Status(original, sn).valueOf();
    }
    
    public long getCreateTime() {
        return new Date().getTime();
    }
    
    public int[] getAlarmStatus(byte[] original, String sn) {
        return new AlarmStatus(original, sn).valueOf();
    }

	public String valueOfMcc(byte[] original, String sn) {
		StringBuffer results = new StringBuffer();
		int msgType = Constant.MESSAGE_TYPE_POSITION;
		int[] status = this.getStatus(original, sn);
		status[3] = original[45];// ACC
		int mcc = Integer.parseInt(HexUtil.toHexAsciis(original, 11, 2), 16);
		int mnc = Integer.parseInt(ByteUtil.toHexAscii(original[13]), 16);
		int lac = 0;
		int cid = 0;
		int signal = 0;
		if (original[14] > 0) {
			status[2] = status[2] | 32;
			int index = 20;// 默认取第一个基站
			// 取信号最高的基站(,信号值越低，表示信号越强)
			for (int i = 1; i < original[14]; i++) {
				int tempSignal = 20 + i * 6;
				if (original[tempSignal] < original[index]) {
					index = tempSignal;
				}
			}
			lac = Integer.parseInt(HexUtil.toHexAsciis(original, index - 5, 2), 16);
			cid = Integer.parseInt(HexUtil.toHexAsciis(original, index - 3, 3), 16);
			signal = 0 - original[index];// 取负值
		}
		if (mcc > 0) {
			status[2] = status[2] | 64;
		}
		int[] alarm = this.getAlarmStatus(original, sn);
		results.append("{\"plam\":" + Config.Plam + ",");
		results.append("\"sn\":\"");
		results.append(sn);
		results.append("\",\"deviceId\":");
		results.append(-1);
		results.append(",\"time\":\"");
		long time = this.getCreateTime();
		results.append("\\/Date(" + time + ")\\/");
		results.append("\",\"lat\":");
		results.append(0);
		results.append(",\"lng\":");
		results.append(0);
		results.append(",\"status\":");
		results.append(Arrays.toString(status).replaceAll("\\s*", ""));
		results.append(",\"alarm\":");
		results.append(Arrays.toString(alarm).replaceAll("\\s*", ""));
		results.append(",\"trajectoryFlag\":");
		results.append(1);
		results.append(",\"speed\":");
		results.append(0);
		results.append(",\"direction\":");
		results.append(0);
		results.append(",\"mileage\":");
		results.append(0);
		results.append(",\"createTime\":\"");
		results.append("\\/Date(" + time + ")\\/");
		results.append("\",\"battery\":");
		results.append(0);
		results.append(",\"iccid\":\"");
		results.append("");
		results.append("\",\"lac\":");
		results.append(lac);
		results.append(",\"cid\":");
		results.append(cid);
		results.append(",\"mnc\":");
		results.append(mnc);
		results.append(",\"mcc\":");
		results.append(mcc);
		results.append(",\"heartbeat\":");
		results.append(0);
		results.append(",\"flag\":");
		results.append(2);
		results.append(",\"accesstype\":");
		results.append(1);// 0:gps、1：基站;2:wifi网络
		results.append(",\"signal\":");
		results.append(signal);
		results.append(",\"satelliteCount\":");
		results.append("\"\"");
		results.append(",\"version\":\"\"");
		results.append(",\"model\":\"015\"");
		results.append(",\"msgType\":");
		results.append(msgType);
		results.append("}");
		return results.toString();
	}
   
}
