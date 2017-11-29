package com.appleframework.server.test.handler.by01578;

import java.util.Arrays;
import java.util.Date;

import org.apache.commons.lang.ArrayUtils;

import com.appleframework.server.test.config.Config;
import com.appleframework.server.test.handler.ProtocolProcessing;
import com.appleframework.server.test.model.AlarmStatus;
import com.appleframework.server.test.model.DeviceData;
import com.appleframework.server.test.model.Status;
import com.appleframework.server.test.utils.ByteUtil;
import com.appleframework.server.test.utils.Constant;
import com.appleframework.server.test.utils.DataUtil;
import com.appleframework.server.test.utils.HexUtil;

public class BY01578GpsProtocol implements ProtocolProcessing {
    
    @Override
	public void handler(byte[] original, String remoteAddr) {
    	String sn = DeviceData.getSn(remoteAddr);
    	if(null == sn) {
    		sn = "1111111111";
    	}
		String mcc = this.valueOf(original, sn);
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
    
	@SuppressWarnings("deprecation")
	public long getTime(byte[] original) {
		int year = Integer.parseInt(ByteUtil.toHexAscii(original[4]), 16) + 100;
		int month = Integer.parseInt(ByteUtil.toHexAscii(original[5]), 16) - 1;
		int day = Integer.parseInt(ByteUtil.toHexAscii(original[6]), 16);
		int hour = Integer.parseInt(ByteUtil.toHexAscii(original[7]), 16) + 8;
		if (hour >= 24) {
			day += 1;
			hour -= 24;
		}
		int minute = Integer.parseInt(ByteUtil.toHexAscii(original[8]), 16);
		int second = Integer.parseInt(ByteUtil.toHexAscii(original[9]), 16);
		Date date = new Date(year, month, day, hour, minute, second);
		return date.getTime();
	}
	
	public double getLat(byte[] original) {
		int values = Integer.parseInt(HexUtil.toHexAsciis(original, 11, 4), 16);
		return values/1800000.00;
    }
	
	public double getLng(byte[] original) {
		int values = Integer.parseInt(HexUtil.toHexAsciis(original, 15, 4), 16);
		return values/1800000.00;
    }
	
	public int getSpeed(byte[] original) {
		return Integer.parseInt(ByteUtil.toHexAscii(original[19]),16);
    }
	
	public int getDirection(byte[] original) {
		byte[] result = new byte[] { 0x00, 0x00 };
		result[0] = (byte) (original[20] & 0x03); // 只保留右边最后两位
		result[1] = original[21];
		return Integer.parseInt(HexUtil.toHexAsciis(result, 0, 2), 16);
	}

    public String valueOf(byte[] original, String sn) {
        StringBuffer results = new StringBuffer();
        int[] status = this.getStatus(original, sn);
        int msgType = Constant.MESSAGE_TYPE_POSITION;
        results.append("{\"plam\":"+Config.Plam+",");
        results.append("\"sn\":\"");
        results.append(sn);
        results.append("\",\"deviceId\":");
        results.append(-1);
        results.append(",\"time\":\"");
        long time = this.getTime(original);
        /*if(status[3] == 2) {
            int i = ((int)(Math.random()*6)+1)*1000;
            time = time - i;
        }*/
        results.append("\\/Date("+time+")\\/");
        results.append("\",\"lat\":");
        results.append(this.getLat(original));
        results.append(",\"lng\":");
        results.append(this.getLng(original));
        results.append(",\"status\":");
        int trajectoryFlag = 1;

		if (this.getMcc(original) == 0) {
			// 如果mcc为0，不为基站定位
			status[2] = status[2] & 191;
		} else if ((status[3] & 2) == 0) {
			// 有基站，基站定位
			status[2] = status[2] | 96;
		}
		results.append(Arrays.toString(status).replaceAll("\\s*", ""));            
     
        results.append(",\"alarm\":");
        if(this.getCommandType(original) == 9) {
            results.append("[0,0,0,0]");
            trajectoryFlag = 0;
            msgType = Constant.MESSAGE_TYPE_STATUS;
        }
        else {
            int[] alarm = this.getAlarmStatus(original, sn);
            results.append(Arrays.toString(alarm).replaceAll("\\s*",""));
        }
        Integer level = this.getSignalLevel();
        Integer signal = this.getSignal(level);
        Integer satelliteCount = null;

        String hexGps = HexUtil.bin2hexstr(ArrayUtils.subarray(original, 10, 11));
        hexGps = "0"+hexGps.substring(1);
        satelliteCount = Integer.parseInt(hexGps, 16);
        
        int heartbeat = 0;
        //results.append(Arrays.toString(this.getAlarmStatus()).replaceAll("\\s*",""));
        results.append(",\"trajectoryFlag\":");
        results.append(trajectoryFlag);
        results.append(",\"speed\":");
        results.append(this.getSpeed(original));
        results.append(",\"direction\":");
        results.append(this.getDirection(original));
        results.append(",\"mileage\":");
        results.append(0);
        results.append(",\"createTime\":\"");
        results.append("\\/Date("+this.getCreateTime()+")\\/");
        results.append("\",\"battery\":");
        results.append(this.getBattery());
        results.append(",\"iccid\":\"");
        results.append(this.getICcid(original));
        results.append("\",\"lac\":");
        results.append(this.getLac(original));
        results.append(",\"cid\":");
        results.append(this.getCellId(original));
        results.append(",\"mnc\":");
        results.append(this.getMnc(original));
        results.append(",\"mcc\":");
        results.append(this.getMcc(original));
        results.append(",\"heartbeat\":");
        results.append(heartbeat);
        results.append(",\"flag\":");
        results.append(2);
        results.append(",\"accesstype\":");
        results.append(DataUtil.getAccesstype(status));//0:gps、1：基站;2:wifi网络
        results.append(",\"signal\":");
        results.append(signal==null?"\"\"":signal);
        results.append(",\"satelliteCount\":");
        results.append(satelliteCount==null?"\"\"":satelliteCount);
        results.append(",\"version\":\"\"");
        results.append(",\"model\":\""+Constant.MODEL_015+"\"");
        results.append(",\"msgType\":");
        results.append(msgType);
        results.append("}");
        return results.toString();
    }
    
    public int getBattery() {
        return 0;
    }
    
	public int getLac(byte[] original) {
		return Integer.parseInt(HexUtil.toHexAsciis(original, 25, 2), 16);
	}
	
	public int getCellId(byte[] original) {
		return Integer.parseInt(HexUtil.toHexAsciis(original, 27, 3), 16);
	}
	
	public int getMnc(byte[] original) {
		return Integer.parseInt(ByteUtil.toHexAscii(original[24]), 16);
	}
	
	public int getMcc(byte[] original) {
		return Integer.parseInt(HexUtil.toHexAsciis(original, 22, 2),16);
    }
	
	public String getICcid(byte[] original) {
		if (this.getCommandType(original) == 9) {
			StringBuffer sb = new StringBuffer();
			for (int i = 16; i < 36; i++) {
				sb.append((char) original[i]);
			}
			return sb.toString();
		}
		return "";
	}

	public int getCommandType(byte[] original) {
		return original[8];
	}
	
	/**
     * 获取信号等级
     * @return
     */
    private Integer getSignalLevel(){
		Integer level = null; // 登录包，定位包默认基站信号强度为0
        return level;
    }
    
    /**
     * 取值范围0到-113，值越大，信号越强
     * @param signalLevel 信号等级，等级越大，信号越强
     * @return
     */
	private Integer getSignal(Integer signalLevel) {
		if (signalLevel == null) {
			return signalLevel;
		}
		int signal = -113;
		int svg = 113 / 4;// 共4个等级
		switch (signalLevel) {
		case 1:
			signal = (1 - 4) * svg;
			break;
		case 2:
			signal = (2 - 4) * svg;
			break;
		case 3:
			signal = (3 - 4) * svg;
			break;
		case 4:
			signal = (4 - 4) * svg;
			break;
		default:
			break;
		}
		return signal;
	}
   
}
