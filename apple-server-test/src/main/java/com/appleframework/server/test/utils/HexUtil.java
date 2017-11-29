package com.appleframework.server.test.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * GPS服务平台管理系统
 * 
 * @author sw
 * @date 2010-5-20
 * @version 1.0
 * 
 */
public class HexUtil {
	
	private static final Log logger = LogFactory.getLog(HexUtil.class);
	/**
	 * @param src
	 * @return
	 */
	public static String bin2hexstr(byte[] src) {
		return bin2hexstr(src, 0, src.length);
	}

	public static String ten2Hex(int ten) {
		byte b = (byte) ten;
		StringBuffer sb = new StringBuffer();
		// b在位移时会先自动转换成int后再位移，这是取出高四位
		sb.append(HEX[(b >> 4) & 0x0f]);
		sb.append(HEX[b & 0x0f]);// 取出低4位
		return sb.toString();
	}

	private static String bin2hexstr(byte[] src, int start, int len) {
		char[] hex = new char[2];
		StringBuffer strBuffer = new StringBuffer(len * 2);
		int abyte;
		for (int i = start; i < start + len; i++) {
			abyte = src[i] < 0 ? 256 + src[i] : src[i];
			hex[0] = HEX[abyte / 16];
			hex[1] = HEX[abyte % 16];
			strBuffer.append(hex);
		}
		return strBuffer.toString();
	}
	
	public static String toHexAsciis(byte[] original,int startIndex, int length) {
        StringBuffer result = new StringBuffer();
        for(int i=startIndex; i<(startIndex+length); i++) {
            result.append(ByteUtil.toHexAscii(original[i]));
        }
        return result.toString();
    }

	private static final char[] HEX = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	public static String reverse2(String s) {
		  int length = s.length();
		  String reverse = "";
		  for (int i = 0; i < length; i++)
		   reverse = s.charAt(i) + reverse;
		  return reverse;
		} 
	
	
	/**
	 * 解析补传数据
	 * @param status
	 * @param strk
	 */
	public static void dealWithK(int[] status,String strk){
		//K S0 S1 S2 S3 S4
		//K10000
		int SO = Integer.parseInt(strk.substring(1, 2));
		System.out.println("status[3]"+status[3]);
		if((SO & 1) == 1){//盲区补偿数据
			int ss = (int)status[3] | 128;
			logger.info(ss);
			status[3] = status[3] | 128;
		}
		if((SO & 2) == 2){//省电模式
			status[1] = status[1] | 2;
		}
		
		
	}
	/**
	 * 转二进制，不足补0
	 * @param str
	 * @return
	 */
	public static String toBinary(String str){
		String binaryString = Integer.toBinaryString(Integer.parseInt(str));
		//长度不够4位补0.
		for (int i = 4; i > binaryString.length(); i--) {
			binaryString = "0"+binaryString;
		}
		return binaryString;
	}
	public static StringBuffer toStringBuffer(byte alarm){
		String one = Integer.toBinaryString(Integer.parseInt(alarm+""));
		StringBuffer onesb = new StringBuffer();
		for (int i = 8; i > one.length(); i--) {
			onesb.append("0");
		}
		onesb.append(one);
		return onesb;
	}
	
	
	
	/**
     * 16进制转int数组
     * @param hex
     * @return
     */
     public static int[] hexStringToByte(String hex) {
           int len = (hex.length() / 2);
           int[] result = new int[len];
           char[] achar = hex.toCharArray();
           for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (int) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
           }
           return result;
          }

     public static byte[] hexStringToBytes(String hex) {
           int len = (hex.length() / 2);
           byte[] result = new byte[len];
           char[] achar = hex.toCharArray();
           for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
           }
           return result;
          }
     /**
      * 
      * @param c
      * @return
      */
     public static String intTohexString(int[] src){
        int start = 0;
        int len = src.length;
         char[] hex = new char[2];
            StringBuffer strBuffer = new StringBuffer(len * 2);
            int abyte;
            for (int i = start; i < start + len; i++) {
                abyte = src[i] < 0 ? 256 + src[i] : src[i];
                hex[0] = HEX[abyte / 16];
                hex[1] = HEX[abyte % 16];
                    strBuffer.append(hex);
            }
            String newStr = strBuffer.toString().replaceAll("^(0+)", "");
            return newStr;
     }
     public static String intTohexSS(int[] src){
            int start = 0;
            int len = src.length;
             char[] hex = new char[2];
                StringBuffer strBuffer = new StringBuffer(len * 2);
                int abyte;
                for (int i = start; i < start + len; i++) {
                    abyte = src[i] < 0 ? 256 + src[i] : src[i];
                    hex[0] = HEX[abyte / 16];
                    hex[1] = HEX[abyte % 16];
                        strBuffer.append(hex);
                }
                return strBuffer.toString();
         }
     public static String intTohexSS1(byte[] src){
            int start = 0;
            int len = src.length;
             char[] hex = new char[2];
                StringBuffer strBuffer = new StringBuffer(len * 2);
                int abyte;
                for (int i = start; i < start + len; i++) {
                    abyte = src[i] < 0 ? 256 + src[i] : src[i];
                    hex[0] = HEX[abyte / 16];
                    hex[1] = HEX[abyte % 16];
                        strBuffer.append(hex);
                }
                return strBuffer.toString();
         }
     public static String byteTohexString(byte[] src){
            int start = 0;
            int len = src.length;
             char[] hex = new char[2];
                StringBuffer strBuffer = new StringBuffer(len * 2);
                int abyte;
                for (int i = start; i < start + len; i++) {
                    abyte = src[i] < 0 ? 256 + src[i] : src[i];
                    hex[0] = HEX[abyte / 16];
                    hex[1] = HEX[abyte % 16];
                        strBuffer.append(hex);
                }
                String newStr = strBuffer.toString().replaceAll("^(0+)", "");
                return newStr;
         }
     private static int toByte(char c) {
            int b = (int) "0123456789ABCDEF".indexOf(c);
            return b;
         }
}
