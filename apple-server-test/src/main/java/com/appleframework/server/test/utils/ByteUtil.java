package com.appleframework.server.test.utils;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.ByteBuffer;

import org.apache.commons.lang.ArrayUtils;

/**
 * 
 * GPS服务平台管理系统
 * 
 * @author sw
 * @date 2010-8-12
 * @version 1.0
 * 
 */
public class ByteUtil {
	
	/**
	 * int到byte[]
	 * @param i
	 * @return
	 */
	public static byte[] intToByteArray(int i) {   
		  byte[] result = new byte[4];   
		  //由高位到低位
		  result[0] = (byte)((i >> 24) & 0xFF);
		  result[1] = (byte)((i >> 16) & 0xFF);
		  result[2] = (byte)((i >> 8) & 0xFF); 
		  result[3] = (byte)(i & 0xFF);
		  return result;
	}
	
	private static byte uniteBytes(String src0, String src1) {  
	    byte b0 = Byte.decode("0x" + src0).byteValue();  
	    b0 = (byte) (b0 << 4);  
	    byte b1 = Byte.decode("0x" + src1).byteValue();  
	    byte ret = (byte) (b0 | b1);  
	    return ret;  
	}
	
	/*public static void main(String[] args) {
		byte[] hexStr2Bytes = hexStr2Bytes("22");
	}*/
	
	public static byte[] hexStr2Bytes(String src) {  
	    int m = 0, n = 0;  
	     while(src.length() < 4){
	    	 src = "0"+src;
	     }
	    int l = src.length() / 2;  
	    byte[] ret = new byte[l];  
	    for (int i = 0; i < l; i++) {  
	        m = i * 2 + 1;  
	        n = m + 1;  
	        ret[i] = uniteBytes(src.substring(i * 2, m), src.substring(m, n));  
	    }  
	    return ret;  
	} 
	
	public static String toHexString(String s) {
		String str = "";
		for (int i = 0; i < s.length(); i++) {
			int ch = (int) s.charAt(i);
			String s4 = Integer.toHexString(ch);
			str = str + s4;
		}
		return str;
	}
	
	/**
	 * 将GBK编码转化为UTF-8字节
	 * 
	 * @param str
	 * @return
	 */
	public static byte[] gbk2utf8(byte[] gbkbyte) {
		String str = new String(gbkbyte);
		ByteBuffer bf = ByteBuffer.allocate(str.length() * 3);
		int strlen = str.length();
		for (int i = 0; i < strlen; i++) {
			int charValue = (int) str.charAt(i);
			// ASCII码直接返回
			if (charValue <= 128) {
				byte b = (byte) charValue;
				bf.put((byte) b);
			} else {
				// 字节头部加上1110(三字节)
				int a = charValue | 0xE0000;
				// 移位后加10
				int b = a & 0x3F | 0x80;
				// 移位后加10
				int b1 = a >> 6 & 0x3F | 0x80;
				int b2 = a >> 12;
				bf.put((byte) b2);
				bf.put((byte) b1);
				bf.put((byte) b);
			}
		}
		return ArrayUtils.subarray(bf.array(), 0, bf.position());
	}

	public static short toUnsigned(byte b) {
		return (short) (b >= 0 ? b : 256 + b);
	}

	static void addHexAscii(byte b, StringWriter sw) {
		short ub = toUnsigned(b);
		int h1 = ub / 16;
		int h2 = ub % 16;
		sw.write(toHexDigit(h1));
		sw.write(toHexDigit(h2));
	}

	public static String toHexAscii(byte b) {
		StringWriter sw = new StringWriter(2);
		addHexAscii(b, sw);
		return sw.toString();
	}

	public static String toHexAscii(byte[] bytes) {
		int len = bytes.length;
		StringWriter sw = new StringWriter(len * 2);
		for (int i = 0; i < len; i++)
			addHexAscii(bytes[i], sw);

		return sw.toString();
	}

	private static char toHexDigit(int h) {
		char out;
		if (h <= 9)
			out = (char) (h + 48);
		else
			out = (char) (h + 55);
		return out;
	}

	public static byte[] fromHexAscii(String s) throws NumberFormatException {
		try {
			int len = s.length();
			if (len % 2 != 0)
				throw new NumberFormatException("Hex ascii must be exactly two digits per byte.");
			int out_len = len / 2;
			byte out[] = new byte[out_len];
			int i = 0;
			StringReader sr = new StringReader(s);
			while (i < out_len) {
				int val = 16 * fromHexDigit(sr.read()) + fromHexDigit(sr.read());
				out[i++] = (byte) val;
			}
			return out;
		} catch (IOException e) {
			throw new InternalError("IOException reading from StringReader?!?!");
		}
	}

	private static int fromHexDigit(int c) throws NumberFormatException {
		if (c >= 48 && c < 58)
			return c - 48;
		if (c >= 65 && c < 71)
			return c - 55;
		if (c >= 97 && c < 103)
			return c - 87;
		else
			throw new NumberFormatException((39 + c) + "' is not a valid hexadecimal digit.");
	}
}
