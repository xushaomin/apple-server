package com.appleframework.server.test.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.appleframework.server.test.config.Config;
import com.appleframework.server.test.model.AlarmStatus;
import com.appleframework.server.test.model.Status;
import com.appleframework.server.test.utils.ByteUtil;
import com.appleframework.server.test.utils.CRC16;
import com.appleframework.server.test.utils.Constant;
import com.appleframework.server.test.utils.DataUtil;
import com.appleframework.server.test.utils.DateUtil;
import com.appleframework.server.test.utils.HexUtil;

public class BY015 {
    private static final Log log = LogFactory.getLog(BY015.class);
    private byte[] original;
    
    private String sn;
    
    private int typeFlag;//1:粘包数据;0:未粘包

    private static InputStream in;
    
    private static byte[] epo = null;
    private static byte[] epodat = null;
    
    private static File file = null;
    
    private long createTime = 0;
    
    //static byte[] bRegister = { 0x78, 0x0D, 0x0A, 0x01, 0x22, 0x24, 0x26, 0x13 };
    
    public BY015(byte[] original) {
        this.original = original;
    }
    
    public boolean isValid() {
        //String t = CRC16.crcTable(this.original);
        //String t2 = this.toHexAsciis(this.getCheckCode(), 0, 2);
        return true;
    }
    
    public byte[] getResponse() {
        byte[] result = new byte[] { 0x00 };
        if(this.isLoginData() || this.isAlarmData() || this.isHeartbeatData()) {
            result = new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
            result[0] = this.getStartIndex()[0];
            result[1] = this.getStartIndex()[1];
            result[2] = 0x05;
            result[3] = this.getProtocolType();
//          result[4] = this.getDataSerialNumber()[0];
//          result[5] = this.getDataSerialNumber()[1];
            result[5] = 0x2B;
            String checkCode = CRC16.getCRC16(result, 2, result.length-4);
            byte[] checkCodeByte = ByteUtil.hexStr2Bytes(checkCode);
            result[6] = checkCodeByte[0];
            if(checkCodeByte.length > 1) {
                result[7] = checkCodeByte[1];   
            }
            result[8] = 0x0D;
            result[9] = 0x0A;
        } else if(this.isAGps()) {
            result = this.getAGpsResponse();
        }
        return result;
    }
    
    public byte[] getAGpsResponse() {
        byte[] result = new byte[] { 0x00 };
        if(this.getTransparentTransmissionType() == 0x00) {
            result = new byte[] { 0x79, 0x79, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x05, 0x00, 0x00, 0x0D, 0x0A };
//          System.out.println(result.length);
            result[3] = 0x1E;
            result[4] = (byte)(0xFD);
            System.arraycopy(this.getImei(), 0, result, 5, 8);
            result[14] = 0x0f;
            result[16] = 0x01;
            Calendar cal = Calendar.getInstance();
            int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);  
            int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);  
            cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));  
            int year = cal.get(Calendar.YEAR);  
            int month = cal.get(Calendar.MONTH)+1;  
            int day = cal.get(Calendar.DAY_OF_MONTH);  
            int hour = cal.get(Calendar.HOUR_OF_DAY);  
            int minute = cal.get(Calendar.MINUTE);
            int seconds = cal.get(Calendar.SECOND);
            
            result[17] = (byte) Integer.parseInt(String.valueOf(year).substring(2, 4));
            result[18] = (byte) month;
            result[19] = (byte) day;
            result[20] = (byte) hour;
            result[21] = (byte) minute;
            result[22] = (byte) seconds;
            for(int i=0; i<6; i++) {
                result[23+i] = (byte) 0xFF;
            }
            String checkCode = CRC16.getCRC16(result, 2, 32);
            if(checkCode.length() == 1) {
                checkCode = "000"+checkCode;
            } else if(checkCode.length() == 2) {
                checkCode = "00"+checkCode;
            } else if(checkCode.length() == 3) {
                checkCode = "0"+checkCode;
            }
            byte[] checkCodeByte = ByteUtil.hexStr2Bytes(checkCode);
            result[32] = checkCodeByte[0];
            result[33] = checkCodeByte[1];
        } else if(this.getTransparentTransmissionType() == 0x01 && this.getTransparentType() == 0x03) {
            //文件读取开始位置
            int startIndex = Integer.parseInt(HexUtil.bin2hexstr(ArrayUtils.subarray(this.original, 21, 25)), 16);
            //请求读取的长度
            int len = Integer.parseInt(HexUtil.bin2hexstr(ArrayUtils.subarray(this.original, 25, 27)), 16);
            int epolen = BY015.epodat.length;
            int sum = 28 + len + 6;
            result = new byte[sum];
            result[0] = 0x79;
            result[1] = 0x79;
            byte[] lenByte = HexUtil.hexStringToBytes(String.format("%04X", 28+len));
            result[2] = lenByte[0];
            result[3] = lenByte[1];
            result[4] = (byte)(0xFD);
            System.arraycopy(this.getImei(), 0, result, 5, 8);
            byte[] lenByteContent = HexUtil.hexStringToBytes(String.format("%04X", 13+len));
            result[13] = lenByteContent[0];
            result[14] = lenByteContent[1];
            result[15] = 0x01;
            result[16] = 0x04;
            result[17] = 0x01;
            if(startIndex > epolen){
                result[17] = 0x0C;
            }else if (epolen-startIndex<len-1){
                result[17] = 0x0B;
            }
            System.arraycopy(epoFileFlag(), 0, result, 18, 4);
            byte[] startByte = HexUtil.hexStringToBytes(String.format("%08X", startIndex));
            result[22] = startByte[0];
            result[23] = startByte[1];
            result[24] = startByte[2];
            result[25] = startByte[3];
            byte[] lenBytePage = HexUtil.hexStringToBytes(String.format("%04X", len));
            result[26] = lenBytePage[0];
            result[27] = lenBytePage[1];
            System.arraycopy(BY015.epodat, startIndex-1, result, 28, len);
            
            result[sum-5] = 0x05;
            String checkCode = CRC16.getCRC16(result, 2, sum-4);
            byte[] checkCodeByte = ByteUtil.hexStr2Bytes(checkCode);
            result[sum-4] = checkCodeByte[0];
            result[sum-3] = checkCodeByte[1];
            result[sum-2] = 0x0D;
            result[sum-1] = 0x0A;
            
        } else if(this.getTransparentTransmissionType() == 0x01 && this.getTransparentType() == 0x01) {
            result = new byte[] { 0x79, 0x79, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x05, 0x00, 0x00, 0x0D, 0x0A };
            result[3] = 0x2B;
            result[4] = (byte)(0xFD);
            System.arraycopy(this.getImei(), 0, result, 5, 8);
            result[14] = 0x1C;
            System.arraycopy(this.getgetTransparentTransmissionContent(), 0, result, 15, 28);
            String checkCode = CRC16.getCRC16(result, 2, 45);
            byte[] checkCodeByte = ByteUtil.hexStr2Bytes(checkCode);
            result[45] = checkCodeByte[0];
            result[46] = checkCodeByte[1];
        } else if(this.getTransparentTransmissionType() == 0x03) {
            result = new byte[] { 0x79, 0x79, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x05, 0x00, 0x00, 0x0D, 0x0A };
//          System.out.println(result.length);
            result[3] = 0x1C;
            result[4] = (byte)(0xFD);
            System.arraycopy(this.getImei(), 0, result, 5, 8);
            result[14] = 0x0D;
            result[15] = 0x03;
            result[16] = 0x01;
            result[24] = 0x03;
            result[25] = 0x20;
            result[26] = 0x10;
            String checkCode = CRC16.getCRC16(result, 2, 30);
            byte[] checkCodeByte = ByteUtil.hexStr2Bytes(checkCode);
            result[30] = checkCodeByte[0];
            result[31] = checkCodeByte[1];
        }
        return result;
    }
    
    private byte[] epoFileFlag() {
        /*byte[] result = new byte[4];
        Calendar cd = Calendar.getInstance();
        cd.setTimeInMillis(file.lastModified());
        String year=String.format("0%x %n", cd.get(Calendar.YEAR)); 
        result[0] = (byte) Integer.parseInt(year.substring(0, 2),16);
        result[1] = (byte) Integer.parseInt(year.substring(2, 4),16);
        result[2] = (byte) (cd.get(Calendar.MONTH)+1);
        result[3] = (byte) cd.get(Calendar.DAY_OF_MONTH);*/
        return new byte[]{ 0x07, (byte) 0xDF, 0x0A, 0x1C };
    }
    
    private void getEpo() {
        try {
            if(epo == null) {
                file = new File(Config.epoFilePath);
                in = new FileInputStream(file);
                byte[] tempbytes = new byte[18432];//276480;18432?//厂商那边设定只读18432个字节
                in.read(tempbytes);
                in.close();
                MessageDigest md5=MessageDigest.getInstance("MD5");
                epo = md5.digest(tempbytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void getEpodat(){
        try {
            file = new File(Config.epoFilePath);
            in = new FileInputStream(file);
            byte[] tempbytes = new byte[(int)file.length()];//276480;18432?
            in.read(tempbytes);
            in.close();
            epodat = tempbytes;
            MessageDigest md5=MessageDigest.getInstance("MD5");
            epo = md5.digest(ArrayUtils.subarray(tempbytes, 0, 18432));
//              System.out.println("--------------");
//              System.out.println(HexUtil.bin2hexstr(epodat));
            log.info("epo更新时间："+DateUtil.format(new Date(), DateUtil.STRING_FORMAT_YMDHMS));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void executeEpoTask(){
        //每隔24小时更新一次EPO.DAT文件缓存
        long period = 24 * 60 * 60 * 1000;
        String[] time = Config.epoTaskTime.split(":");
        
        Calendar calendar = Calendar.getInstance();  
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0])); 
        calendar.set(Calendar.MINUTE, Integer.parseInt(time[1]));   
        calendar.set(Calendar.SECOND, 0); 
        
        Date fristDate = calendar.getTime();
        Date nowDate = new Date();
        if(nowDate.after(fristDate)){
            calendar.add(Calendar.DATE, 1); 
        }  
        fristDate = calendar.getTime();
        log.info("epo定时加载时间："+DateUtil.format(fristDate, DateUtil.STRING_FORMAT_YMDHMS));
        
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                getEpodat();
            }
            
        },fristDate,period);
    }
    
    private byte[] getgetTransparentTransmissionContent() {
        byte[] result = new byte[28];
        getEpo();
        result[0] = 0x01;
        result[1] = 0x02;
        result[2] = 0x01;
        System.arraycopy(epoFileFlag(), 0, result, 3, 4);
        byte[] temp = HexUtil.hexStringToBytes(String.format("%08X", 18432));
        System.arraycopy(temp, 0, result, 7, 4);
        result[11] = 0x01;
//      epo = new byte[]{(byte) 0xD9,0x63,(byte) 0xDB,0x47,0x64,0x6D,(byte) 0xAA,(byte) 0xFB,0x71,(byte) 0xD8,(byte) 0x8C,(byte) 0xBE,0x3E,0x43,0x67,0x36 };
        System.arraycopy(epo, 0, result, 12, 16);
//      if(this.getTransparentTransmissionType() == 0x01 && this.getTransparentType() == 0x01) {
//          getEpo();
//          result = new byte[28];
//          result[0] = 0x01;
//          result[1] = 0x02;
//          result[2] = 0x01;
//          System.arraycopy(epoFileFlag(), 0, result, 3, 4);
////            System.arraycopy(epoEpoLength(), 0, result, 7, 4);
//          byte[] temp = HexUtil.hexStringToBytes(String.format("%08X", BY015.epodat.length));
//          System.arraycopy(temp, 0, result, 7, 4);
//          result[11] = 0x01;
//          epo = new byte[]{(byte) 0xD9,0x63,(byte) 0xDB,0x47,0x64,0x6D,(byte) 0xAA,(byte) 0xFB,0x71,(byte) 0xD8,(byte) 0x8C,(byte) 0xBE,0x3E,0x43,0x67,0x36 };
//          System.arraycopy(epo, 0, result, 12, 16);
//      } else if(this.getTransparentTransmissionType() == 0x01 && this.getTransparentType() == 0x03) {
//          getEpo();
//          result = new byte[28];
//          result[0] = 0x01;
//          result[1] = 0x04;
//          result[2] = 0x01;
//          System.arraycopy(epoFileFlag(), 0, result, 3, 4);
//          System.arraycopy(epoEpoLength(), 0, result, 7, 4);
//          result[11] = 0x03;
//          result[12] = (byte) 0xE8;
//      }
        return result;
    }
    
    public byte[] getDataSerialNumber() {
        byte[] result = new byte[] { 0x00, 0x00 };
        if(this.isLoginData()) {
            result = this.getDataSerialNumberAndCheckCodeByIndex(16);
        } else if(this.isGpsData()) {
            result = this.getDataSerialNumberAndCheckCodeByIndex(33);
        } else if(this.isAlarmData()) {
            result = this.getDataSerialNumberAndCheckCodeByIndex(36);
        } else if(this.isHeartbeatData()) {
            result = this.getDataSerialNumberAndCheckCodeByIndex(9);
        } else if(this.isAGps()) {
            result = this.getDataSerialNumberAndCheckCodeByIndex(15+this.getTransparentTransmissionLength());
        }
        return result;
    }
    
    private byte[] getDataSerialNumberAndCheckCodeByIndex(int index) {
        return new byte[] { this.original[index], this.original[++index] };
    }
    
    public byte[] getCheckCode() {
        byte[] result = new byte[] { 0x00, 0x00 };
        if(this.isLoginData()) {
            result = this.getDataSerialNumberAndCheckCodeByIndex(18);
        } else if(this.isGpsData()) {
            result = this.getDataSerialNumberAndCheckCodeByIndex(35);
        } else if(this.isAlarmData()) {
            result = this.getDataSerialNumberAndCheckCodeByIndex(38);
        } else if(this.isHeartbeatData()) {
            result = this.getDataSerialNumberAndCheckCodeByIndex(11);
        } else if(this.isAGps()) {
            result = this.getDataSerialNumberAndCheckCodeByIndex(17+this.getTransparentTransmissionLength());
        }
        return result;
    }
    
    public byte getProtocolType() {
        if(this.isStartWith0x78()) {
            return this.original[3];
        } else if(this.isStartWith0x79()) {
            return this.original[4];
        }
        return 0x00;
    }
    
    public boolean isEnd() {
        return this.original[this.original.length-1] == 0x0A && this.original[this.original.length-2] == 0x0D;
    }
    
    public byte[] getDataLength() {
        byte[] headerValues = this.getStartIndex();
        byte[] result = new byte[] {0x00, 0x00};
        if(headerValues[1] == 0x78) {
            return result = new byte[] {this.original[2]};
        } else if(headerValues[1] == 0x79) {
            return result = new byte[] {this.original[2],this.original[3]};
        }
        return result;
    }
    
    public byte[] getStartIndex() {
        byte[] result = new byte[] { 0x00, 0x00 };
        if(this.original[0] == 0x78) {
            result[0] = result[1] = 0x78;
        } else if(this.original[0] == 0x79) {
            result[0] = result[1] = 0x79;
        }
        return result;
    }
    
    public int getCommandType() {
        return this.original[8];
    }
    
    public String CMDResponseValueOf() {
        StringBuffer results = new StringBuffer();
        if(this.isCommandResponseData()) {
//          int commandType = this.getCommandType() == 17 ? 11 : this.getCommandType();
            int commandType = this.getCommandType();
            if(commandType >= 10){
                commandType = Integer.parseInt(Integer.toHexString(commandType));
            }
            
            results.append("{\"sn\":\"");
            results.append(this.getSn());
            results.append("\",\"CommandType\":");
            results.append(commandType);
            results.append(",\"Content\":"); 
            results.append("0");
            results.append(",\"Status\":"); 
            results.append(1);
            results.append(",\"mode\":"); 
            results.append("0");
            results.append("}");
        }
        return results.toString();
    }
    
    public String valueOf() {
        StringBuffer results = new StringBuffer();
        int[] status = this.getStatus();
        int msgType = Constant.MESSAGE_TYPE_POSITION;
        results.append("{\"plam\":"+Config.Plam+",");
        results.append("\"sn\":\"");
        results.append(this.getSn());
        results.append("\",\"deviceId\":");
        results.append(-1);
        results.append(",\"time\":\"");
        long time = this.getTime();
        /*if(status[3] == 2) {
            int i = ((int)(Math.random()*6)+1)*1000;
            time = time - i;
        }*/
        results.append("\\/Date("+time+")\\/");
        results.append("\",\"lat\":");
        results.append(this.getLat());
        results.append(",\"lng\":");
        results.append(this.getLng());
        results.append(",\"status\":");
        int trajectoryFlag = 1;
        if(this.isLoginData()){ //如果是登陆数据，则默认为省电模式
            msgType = Constant.MESSAGE_TYPE_LOGIN;
            results.append(Arrays.toString(new int[]{ 0x00, 0x02, 0x00, 0x00 }).replaceAll("\\s*",""));
        }else{
//          if(this.isHeartbeatData()){
//              if(((this.original[4] >> 2) & 1) == 1 ){
//                  //通电
//                  status[2] = status[2] & 247;
//              }else{
//                  //断电
//                  status[2] = status[2] | 8;
//              }
//          }
            if(this.isAlarmData()){
                msgType = Constant.MESSAGE_TYPE_ALARM;
            }
            if(this.getMcc() == 0){
                //如果mcc为0，不为基站定位
                status[2] = status[2] & 191;
            }else if ((status[3] & 2) == 0){
                //有基站，基站定位
                status[2] = status[2] | 96;
            }
            results.append(Arrays.toString(status).replaceAll("\\s*",""));
            
        }
        results.append(",\"alarm\":");
        if(this.isCommandResponseData() && this.getCommandType() == 9)
        {
            results.append("[0,0,0,0]");
            trajectoryFlag = 0;
            msgType = Constant.MESSAGE_TYPE_STATUS;
        }
        else
        {
            int[] alarm = this.getAlarmStatus();
//          if(this.isHeartbeatData()){
//              if(((this.original[4] >> 2) & 1) == 1 ){
//                  //通电
//                  alarm[2] = alarm[2] & 254;
//              }else{
//                  //断电
//                  alarm[2] = alarm[2] | 1;
//              }
//          }
//            if(this.isAlarmData()){
//                if((this.original[31] & 56) == 16){
//                    alarm[2] = alarm[2] | 1;
//                }else{
//                    alarm[2] = alarm[2] & 254;
//                }
//            }
            results.append(Arrays.toString(alarm).replaceAll("\\s*",""));
        }
        Integer level = this.getSignalLevel();
        Integer signal = this.getSignal(level);
        Integer satelliteCount = null;
        if(this.isAlarmData() || this.isGpsData()){
            String hexGps = HexUtil.bin2hexstr(ArrayUtils.subarray(this.original, 10, 11));
            hexGps = "0"+hexGps.substring(1);
            satelliteCount = Integer.parseInt(hexGps, 16);
        }
        int heartbeat = 0;
        if(this.isHeartbeatData()) {
            msgType = Constant.MESSAGE_TYPE_HEARTBEAT;
            heartbeat = 1;
        }
        if(this.isLoginData()) {
            heartbeat = 1;//登录信息
            trajectoryFlag = 0;//登录不加入轨迹
        }
        //results.append(Arrays.toString(this.getAlarmStatus()).replaceAll("\\s*",""));
        results.append(",\"trajectoryFlag\":");
        results.append(trajectoryFlag);
        results.append(",\"speed\":");
        results.append(this.getSpeed());
        results.append(",\"direction\":");
        results.append(this.getDirection());
        results.append(",\"mileage\":");
        results.append(0);
        results.append(",\"createTime\":\"");
        results.append("\\/Date("+this.getCreateTime()+")\\/");
        results.append("\",\"battery\":");
        results.append(this.getBattery());
        results.append(",\"iccid\":\"");
        results.append(this.getICcid());
        results.append("\",\"lac\":");
        results.append(this.getLac());
        results.append(",\"cid\":");
        results.append(this.getCellId());
        results.append(",\"mnc\":");
        results.append(this.getMnc());
        results.append(",\"mcc\":");
        results.append(this.getMcc());
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
    
    public String valueOfMcc() {
        StringBuffer results = new StringBuffer();
        int msgType = Constant.MESSAGE_TYPE_POSITION;
        int[] status = this.getStatus();
        status[3] = this.original[45];//ACC
        int mcc = Integer.parseInt(this.toHexAsciis(this.original, 11, 2),16);
        int mnc = Integer.parseInt(ByteUtil.toHexAscii(this.original[13]),16);
        int lac = 0;
        int cid = 0;
        int signal = 0;
        if(this.original[14]>0){
            status[2] = status[2] | 32;
            int index = 20;//默认取第一个基站
            //取信号最高的基站(,信号值越低，表示信号越强)
            for(int i =1;i<this.original[14];i++){
                int tempSignal = 20 + i * 6;
                if(this.original[tempSignal] < this.original[index]){
                    index = tempSignal;
                }
            }
            lac = Integer.parseInt(this.toHexAsciis(this.original, index - 5, 2),16);
            cid = Integer.parseInt(this.toHexAsciis(this.original, index - 3, 3),16);
            signal = 0 - this.original[index];//取负值
        }
        if(mcc > 0){
            status[2] = status[2] | 64;
        }
        int[] alarm = this.getAlarmStatus();
        results.append("{\"plam\":"+Config.Plam+",");
        results.append("\"sn\":\"");
        results.append(this.getSn());
        results.append("\",\"deviceId\":");
        results.append(-1);
        results.append(",\"time\":\"");
        long time = this.getCreateTime();
        results.append("\\/Date("+time+")\\/");
        results.append("\",\"lat\":");
        results.append(0);
        results.append(",\"lng\":");
        results.append(0);
        results.append(",\"status\":");
        results.append(Arrays.toString(status).replaceAll("\\s*",""));
        results.append(",\"alarm\":");
        results.append(Arrays.toString(alarm).replaceAll("\\s*",""));
        results.append(",\"trajectoryFlag\":");
        results.append(1);
        results.append(",\"speed\":");
        results.append(0);
        results.append(",\"direction\":");
        results.append(0);
        results.append(",\"mileage\":");
        results.append(0);
        results.append(",\"createTime\":\"");
        results.append("\\/Date("+time+")\\/");
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
        results.append( 0);
        results.append(",\"flag\":");
        results.append(2);
        results.append(",\"accesstype\":");
        results.append(1);//0:gps、1：基站;2:wifi网络
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
    
    public String getICcid() {
        if(this.isCommandResponseData() && this.getCommandType() == 9) {
            StringBuffer sb = new StringBuffer();
            for(int i=16; i<36; i++) {
                sb.append((char)this.original[i]);
            }
            return sb.toString();
        }
        return "";
    }
    
    public int getBattery() {
        if(this.isAlarmData()) {
            return this.original[32];
        }else if(this.isHeartbeatData()) {
            return this.original[5];
        }
        return 0;
    }
    
    public int[] getAlarmStatus() {
        //登录不参与报警处理逻辑 add by swh 20161224
        if(this.isLoginData()){
            return new int[]{ 0x00, 0x00, 0x00, 0x00 };
        } 
        return new AlarmStatus(this.original, this.sn).valueOf();
    }
    
    public int[] getStatus() {
        return new Status(this.original, this.sn).valueOf();
    }
    
    public int getLbsLength() {
        if(this.isAlarmData()) {
            if(isHavingHeight()) {
                return Integer.parseInt(ByteUtil.toHexAscii(this.original[24]),16);
            } else {
                return Integer.parseInt(ByteUtil.toHexAscii(this.original[22]),16);
            }
        }
        return 0;
    }
    
    public int getSupplements() {
        if(isGpsData()) {
            if(isHavingHeight()) {
                return Integer.parseInt(ByteUtil.toHexAscii(this.original[34]),16);
            } else {
                return Integer.parseInt(ByteUtil.toHexAscii(this.original[32]),16);
            }
        }
        return 0;
    }
    
    public int getDataUploadModel() {
        if(isGpsData()) {
            if(isHavingHeight()) {
                return Integer.parseInt(ByteUtil.toHexAscii(this.original[33]),16);
            } else {
                return Integer.parseInt(ByteUtil.toHexAscii(this.original[31]),16);
            }
        }
        return 0;
    }
    
    public int getAcc() {
        if(isGpsData()) {
            if(isHavingHeight()) {
                return Integer.parseInt(ByteUtil.toHexAscii(this.original[32]),16);
            } else {
                return Integer.parseInt(ByteUtil.toHexAscii(this.original[30]),16);
            }
        }
        return 0;
    }
    
    public int getCellId() {
        if(isGpsData()) {
            if(isHavingHeight()) {
                return Integer.parseInt(this.toHexAsciis(this.original, 29, 3),16);
            } else {
                return Integer.parseInt(this.toHexAsciis(this.original, 27, 3),16);
            }
        } 
//      else if(this.isAlarmData()) {
//          if(isHavingHeight()) {
//              return Integer.parseInt(this.toHexAsciis(this.original, 30, 2),16);
//          } else {
//              return Integer.parseInt(this.toHexAsciis(this.original, 28, 3),16);
//          }
//      }
        return 0;
    }
    
    public int getLac() {
        if(isGpsData()) {
            if(isHavingHeight()) {
                return Integer.parseInt(this.toHexAsciis(this.original, 27, 2),16);
            } else {
                return Integer.parseInt(this.toHexAsciis(this.original, 25, 2),16);
            }
        } 
//      else if(this.isAlarmData()) {
//          if(isHavingHeight()) {
//              return Integer.parseInt(this.toHexAsciis(this.original, 28, 2),16);
//          } else {
//              return Integer.parseInt(this.toHexAsciis(this.original, 26, 2),16);
//          }
//      }
        return 0;
    }
    
    public int getMnc() {
        if(isGpsData()) {
            if(isHavingHeight()) {
                return Integer.parseInt(ByteUtil.toHexAscii(this.original[26]),16);
            } else {
                return Integer.parseInt(ByteUtil.toHexAscii(this.original[24]),16);
            }
        } 
//      else if(this.isAlarmData()) {
//          if(isHavingHeight()) {
//              return Integer.parseInt(ByteUtil.toHexAscii(this.original[27]),16);
//          } else {
//              return Integer.parseInt(ByteUtil.toHexAscii(this.original[25]),16);
//          }
//      }
        return 0;
    }
    
    public int getMcc() {
        if(isGpsData()) {
            if(isHavingHeight()) {
                return Integer.parseInt(this.toHexAsciis(this.original, 25, 2),16);
            } else {
                return Integer.parseInt(this.toHexAsciis(this.original, 22, 2),16);
            }
        } 
//      else if(this.isAlarmData()) {
//          if(isHavingHeight()) {
//              return Integer.parseInt(this.toHexAsciis(this.original, 26, 2),16);
//          } else {
//              return Integer.parseInt(this.toHexAsciis(this.original, 23, 2),16);
//          }
//      }
        return 0;
    }
    
    public int getHeight() {
        if(isGpsData() && isHavingHeight()) {
            return Integer.parseInt(this.toHexAsciis(this.original, 22, 2),16);
        }
        return 0;
    }
    
    public byte[] getGpsStatus() {
        return null;
    }
    
    public int getDirection() {
        if(isGpsData() || this.isAlarmData()) {
            byte[] result = new byte[]{0x00, 0x00};
            result[0] = (byte) (this.original[20] & 0x03); //只保留右边最后两位
            result[1] = this.original[21];
            return Integer.parseInt(this.toHexAsciis(result,0,2),16);
        }
        return 0;
    }
    
    public int getSpeed() {
        if(isGpsData() || this.isAlarmData()) {
            return Integer.parseInt(ByteUtil.toHexAscii(this.original[19]),16);
        }
        return 0;
    }
    
    public double getLat() {
        if(isGpsData() || this.isAlarmData()) {
            int values = Integer.parseInt(this.toHexAsciis(this.original, 11, 4),16);
            return values/1800000.00;
        }
        return 0;
    }
    
    public double getLng() {
        if(isGpsData() || this.isAlarmData()) {
            int values = Integer.parseInt(this.toHexAsciis(this.original, 15, 4),16);
            return values/1800000.00;
        }
        return 0;
    }
    
    public int getGpsSatelliteNumber() {
        if(isGpsData() || this.isAlarmData()) {
            return Integer.parseInt(ByteUtil.toHexAscii(this.original[10]).substring(1, 2),16);
        }
        return 0;
    }
    
    public String getSn() {
        if(this.isLoginData()) {
            return this.toHexAsciis(original, 4, 8).replaceAll("^(0+)", "");
        }
        return this.sn;
    }
    
    public void setSn(String sn) {
        this.sn = sn;
    }
    
    public byte getTransparentType() {
        return this.original[16];
    }
    
    public byte getTransparentTransmissionType() {
        return this.original[15];
    }
    
    public int getTransparentTransmissionLength() {
        return Integer.parseInt(this.toHexAsciis(original, 13, 2),16);
    }
    
    public byte[] getImei() {
        byte[] result = new byte[8];
        for(int i=5; i<13; i++) {
            result[i-5] = this.original[i];
        }
        return result;
    }
    
    public boolean isAGps() {
        return this.original[4] == -3;
    }
    
    public boolean isStartWith0x78() {
        return this.original[0] == 0x78 && this.original[1] == 0x78;
    }
    
    public boolean isStartWith0x79() {
        return this.original[0] == 0x79 && this.original[1] == 0x79;
    }
    
    public boolean isHavingHeight() {
        if(this.isStartWith0x78()) {
            return this.original[3] ==0x24;
        } 
        return this.original[4] == 0x24;
    }
    
    public boolean isHeartbeatData() {
        if(this.isStartWith0x78()) {
            return this.original[3] == 0x13;
        } 
        return this.original[4] == 0x13;
    }
    
    public boolean isOnline() {
        return true;
    }
    
    public boolean isCommandResponseData() {
        return this.original[4] == 0x21;
    }
    
    public boolean isAlarmData() {
        if(this.isStartWith0x78()) {
            return this.original[3] == 0x26;
        } 
        return this.original[4] == 0x26;
    }
    
    public boolean isLoginData() {
        if(this.isStartWith0x78()) {
            return this.original[3] == 0x01;
        } 
        return this.original[4] == 0x01;
    }
    
    public boolean isGpsData() {
        if(this.isStartWith0x78()) {
            return this.original[3] == 0x22;
        } 
        return this.original[4] == 0x22;
    }
    
    public boolean isMccData() {
        if(this.isStartWith0x78()) {
            return this.original[3] == 0x24;
        } 
        return false;
    }
    
    public String getHeaderType() {
        return this.isStartWith0x78() || this.isStartWith0x79() ? "015" : "";
    }
    
    @SuppressWarnings("deprecation")
    public long getTime() {
        if(this.isGpsData() || this.isAlarmData() || this.isMccData()) {
            int year = Integer.parseInt(ByteUtil.toHexAscii(this.original[4]),16)+100;
            int month = Integer.parseInt(ByteUtil.toHexAscii(this.original[5]),16)-1;
            int day = Integer.parseInt(ByteUtil.toHexAscii(this.original[6]),16);
            int hour = Integer.parseInt(ByteUtil.toHexAscii(this.original[7]),16)+8;
            if(hour >= 24) {
                day += 1;
                hour -= 24;
            }
            int minute = Integer.parseInt(ByteUtil.toHexAscii(this.original[8]),16);
            int second = Integer.parseInt(ByteUtil.toHexAscii(this.original[9]),16);
            Date date = new Date(year,month,day,hour,minute,second);
            return date.getTime();
        }
        createTime = new Date().getTime();
        return createTime;
    }
    
    public long getCreateTime() {
        if(createTime == 0) {
            createTime = new Date().getTime();
        }
        return createTime;
    }
    
    private String toHexAsciis(byte[] original,int startIndex, int length) {
        StringBuffer result = new StringBuffer();
        for(int i=startIndex; i<(startIndex+length); i++) {
            result.append(ByteUtil.toHexAscii(original[i]));
        }
        return result.toString();
    }
    
    public int getTypeFlag() {
        return typeFlag;
    }

    public void setTypeFlag(int typeFlag) {
        this.typeFlag = typeFlag;
    }

    public String getDataType() {
        return "";
    }
    public static void main(String[] args) {
//      byte[] b = new byte[]{12,32,55,67,89,57,67,45};
//      b = ArrayUtils.remove(b, 3);
//      for(byte b1 : b) System.out.print(b1);
//      System.out.println("***********");
//      b = ArrayUtils.remove(b, 3);
//      for(byte b1 : b) System.out.print(b1);
//      byte a = 17;
//      int year = Integer.parseInt(ByteUtil.toHexAscii(a),16)+100;
//      System.out.println(year);
//      byte[] result = new byte[] { 0x00,0x03,0x0e };
//      String ten = new String(HexUtil.);
//      System.out.println(ten);
    }
    /**
     * 获取信号等级
     * @return
     */
    private Integer getSignalLevel(){
        Integer level = null;//登录包，定位包默认基站信号强度为0
        if(this.isAlarmData()){
            level = (int) this.original[33];
        }else if(this.isHeartbeatData()){
            level = (int) this.original[6];
        }
        return level;
    }
    /**
     * 取值范围0到-113，值越大，信号越强
     * @param signalLevel 信号等级，等级越大，信号越强
     * @return
     */
    private Integer getSignal(Integer signalLevel){
        if(signalLevel == null){
            return signalLevel;
        }
        int signal = -113;
        int svg = 113 / 4;//共4个等级
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
