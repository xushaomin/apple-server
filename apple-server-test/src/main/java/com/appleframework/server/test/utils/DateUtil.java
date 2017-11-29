package com.appleframework.server.test.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * 日期工具类
 * 
 * @author Learnmate
 * 
 */
public class DateUtil {
	public static final String LABEL_DAYS = "days";
	public static final String LABEL_HOURS = "hours";
	public static final String LABEL_MINUTES = "minutes";
	public static final String LABEL_SECONDS = "second";
	
	public static final String STRING_FORMAT_YMD = "yyyy-MM-dd";
	public static final String STRING_FORMAT_YMDHMS = "yyyy-MM-dd HH:mm:ss";
	public final static String DATETIME_FORMAT1 = "yyyyMMddHHmmss";
	
	/**
	 * 功能描述：格式化日期
	 * 
	 * @param dateStr
	 *            String 字符型日期
	 * @param format
	 *            String 格式
	 * @return Date 日期
	 */
	public static Date parseDate(String dateStr, String format) {
	    if(StringUtils.isEmpty( dateStr )) return null;
	    Date date = null;
		try {
		    SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			if (dateStr.length() < format.length()) {
				dateStr += format.substring(dateStr.length()).replaceAll("[YyMmDdHhSs]",
						"0");
			}
			date = (Date) dateFormat.parse(dateStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}
	
	public static String formatDate(String dateStr, String format) {
		try {
		    SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			dateStr = dateFormat.format(Timestamp.valueOf(dateStr));
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return dateStr;
	}

	public static String processDateStr(String dateStr, String separator){
		Pattern pattern = Pattern.compile("([^"+separator+"])+");
		Matcher matcher = pattern.matcher(dateStr);
		StringBuffer result = new StringBuffer();
		while(matcher.find()){
			result.append(matcher.group().length() < 2 ? "0" + matcher.group() : matcher.group()).append(separator);
		}
		return result.toString().substring(0, result.length() - separator.length());
	}

	/**
	 * 格式化输出日期
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static String format(Date date, String format) {
		String result = "";
		try {
			if (date != null) {
			    SimpleDateFormat dateFormat = new SimpleDateFormat(format);
				result = dateFormat.format(date);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 格式化日期为yyyy/MM/dd
	 * 
	 * @param date
	 * @return
	 */
	public static String format(Date date) {
		return format(date, "yyyy/MM/dd");
	}

	/**
	 * 返回年份
	 * 
	 * @param date
	 * @return
	 */
	public static int getYear(Date date) {
	    Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.YEAR);
	}

	/**
	 * 返回月份
	 * 
	 * @param date
	 * @return
	 */
	public static int getMonth(Date date) {
	    Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.MONTH) + 1;
	}

	/**
	 * 返回日份
	 * 
	 * @param date
	 * @return
	 */
	public static int getDay(Date date) {
	    Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 返回小时
	 * 
	 * @param date
	 * @return
	 */
	public static int getHour(Date date) {
	    Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * 返回分钟
	 * 
	 * @param date
	 * @return
	 */
	public static int getMinute(Date date) {
	    Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.MINUTE);
	}

	/**
	 * 返回秒钟
	 * 
	 * @param date
	 * @return
	 */
	public static int getSecond(Date date) {
	    Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.SECOND);
	}

	/**
	 * 返回毫秒
	 * 
	 * @param date
	 * @return
	 */
	public static long getMillis(Date date) {
	    Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.getTimeInMillis();
	}
	
	/**
	 * 返回星期
	 * 
	 * @param date
	 * @return
	 */
	public static int getWeek(Date date) {
	    Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * 返回字符型日期
	 * 
	 * @param date
	 * @return
	 */
	public static String getDate(Date date) {
		return format(date, "yyyy/MM/dd");
	}

	/**
	 * 返回字符型时间
	 * 
	 * @param date
	 * @return
	 */
	public static String getTime(Date date) {
		return format(date, "HH:mm:ss");
	}

	/**
	 * 返回字符型日期时间
	 * 
	 * @param date
	 * @return
	 */
	public static String getDateTime(Date date) {
		return format(date, STRING_FORMAT_YMDHMS);
	}

	/**
	 * 日期相加
	 * 
	 * @param date
	 * @param day
	 * @return
	 */
	public static Date addDate(Date date, int day) {
	    Calendar calendar = Calendar.getInstance();
		long millis = getMillis(date) + ((long) day) * 24 * 3600 * 1000;
		calendar.setTimeInMillis(millis);
		return calendar.getTime();
	}

	/**
	 * 日期相减
	 * 
	 * @param date
	 * @param date1
	 * @return
	 */
	public static int diffDate(Date date, Date date1) {
		return (int) ((getMillis(date) - getMillis(date1)) / (24 * 3600 * 1000));
	}

	/**
	 * 以指定的格式来格式化日期
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static String formatDateByFormat(Date date, String format) {
		String result = "";
		if (date != null) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(format);
				result = sdf.format(date);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}
	
	public static Timestamp getCurDate() {
		return new Timestamp(System.currentTimeMillis());
	}
	
	/**
	 * 获取时间的秒数
	 * @param time 格式为HH:MM:SS.sss
	 * @return
	 */
	public static int getTimeSeconds(String time) {
		int timeSeconds = 0;
		try {
			String[] time_data = time.split(":");
			int hour = Integer.parseInt(time_data[0]);
			int minute = Integer.parseInt(time_data[1]);
			int second = Integer.parseInt(time_data[2].split("\\.")[0]);
			timeSeconds =  hour * 3600 + minute * 60 + second;
		}catch(Exception e){
			e.printStackTrace();
		}
		return timeSeconds;
	}
	
	/**
	 * 根据秒数获取时间
	 * @param time 格式为HH:MM:SS.sss
	 * @return
	 */
	public static String getTimeBySeconds(int seconds) {
		StringBuffer time = new StringBuffer();
		int hour = seconds / 3600;
		int minute = (seconds % 3600) / 60;
		int second = (seconds % 3600) % 60;
		if (hour == 0) {
			time.append("00");
		} else if (hour < 10) {
			time.append("0").append(hour);
		}else{
			time.append(hour);
		}
		time.append(":");
		if (minute == 0) {
			time.append("00");
		} else if (minute < 10) {
			time.append("0").append(minute);
		}else{
			time.append(minute);
		}
		time.append(":");
		if (second == 0) {
			time.append("00");
		} else if (second < 10) {
			time.append("0").append(second);
		}else{
			time.append(second);
		}
		time.append(".0");
		return time.toString();
	}
	
	/**  
	 *   
	 * @param 要转换的毫秒数  
	 * @return 该毫秒数转换为 * days * hours * minutes 后的格式  
	 */ 
	@SuppressWarnings( "rawtypes" )
    public static String formatDuration(long mss, Map labelMap) {
		long days = mss / (60 * 60 * 24);
		long hours = (mss % (60 * 60 * 24)) / (60 * 60);
		long minutes = (mss % (60 * 60)) / (60);
		long seconds = (mss % 60);
		
		StringBuffer result = new StringBuffer();
		if (days > 0) {
			result.append(days).append("").append(labelMap.get(LABEL_DAYS)).append(" ");
		}
		if(days > 0 || hours > 0) {
			result.append(hours).append("").append(labelMap.get(LABEL_HOURS)).append(" ");
		}
		if(days > 0 || hours > 0 || minutes > 0) {
			result.append(minutes).append("").append(labelMap.get(LABEL_MINUTES));
		}
		if(days == 0 && hours == 0 && minutes == 0 && seconds > 0) {
			result.append(seconds).append("").append(labelMap.get(LABEL_SECONDS));
		}

		return result.toString();
	}
	
	public static Timestamp getDayEndTime(Timestamp time) {
		return Timestamp.valueOf(DateUtil.format(time, "yyyy-MM-dd 23:59:59.999"));
	}
	
	/**
	 * 通过给定日期和工作日计算工作日以后的日期
	 * 
	 * @param beginDate 指定日期
	 * @param workDays 工作日
	 */
	public static Date addWorkDays(Date beginDate, int workDays) {
		Date endDate = beginDate;
		Calendar calBegin = Calendar.getInstance();
		calBegin.setTime(beginDate);
		
		int count = 1;
		while (count <= workDays) {
			int tempBeginWeek = calBegin.get(Calendar.DAY_OF_WEEK);
			if (tempBeginWeek < 6 && tempBeginWeek > 0) {
				count++;
			}
			calBegin.add(Calendar.DATE, 1);
		}
		endDate = calBegin.getTime();
		return endDate;
	}
	
	/**
	 * 比较两个日期
	 * @param DATE1
	 * @param DATE2
	 * @return
	 */
	public static int compare_date(String DATE1, String DATE2) {
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		try {
			Date dt1 = df.parse(DATE1);
			Date dt2 = df.parse(DATE2);
			if (dt1.getTime() > dt2.getTime()) {
				System.out.println("dt1 在dt2前");
				return 1;
			} else if (dt1.getTime() < dt2.getTime()) {
				System.out.println("dt1在dt2后");
				return -1;
			} else {
				return 0;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * 格式化日期
	 * @param dateStr2
	 * @return
	 */
	public static Timestamp formatTimestamp(String dateStr2) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		format.setLenient(false);
		Timestamp ts = null;
		try {
			ts = new Timestamp(format.parse(dateStr2).getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return ts;
	}
	
	/**
	 * 通过给定日期和分钟计算工作日以后的分钟
	 * 
	 * @param beginDate 指定日期
	 * @param second 秒钟
	 */
	public static Date addWorkSecond(Date beginDate, int second) {
		Date endDate = beginDate;
		Calendar calBegin = Calendar.getInstance();
		calBegin.setTime(beginDate);
		calBegin.add(Calendar.SECOND, second);
		endDate = calBegin.getTime();
		return endDate;
	}
	
	/**
     * 处理时间，转化成时间戳 。格式为：Data(1481697128000)
     * @param hhmmss
     * @param ddmmyy
     * @return
     */
    public static String getNowTime(){
        StringBuffer sb = new StringBuffer();
        sb.append("/Date(");
        //String time = "20"+ddmmyy.substring(4, 6)+"-"+ddmmyy.substring(2, 4)+"-"+ddmmyy.substring(0, 2);
        //System.out.println(time);
        //time +=" "+hhmmss.substring(0, 2)+":"+hhmmss.substring(2, 4)+":"+hhmmss.substring(4, 6);
        //System.out.println(time);
        //Date parseDateTime = parseDateTime(time);
        Date data = new Date();
        sb.append(data.getTime());
        sb.append(")/");
        return sb.toString();
    }
    
    public static String toYYMMDDHHMMSS(Date date){
        String format = new SimpleDateFormat(DATETIME_FORMAT1).format(date);
        return format.substring(2);
    }
    
    public static String dealWithTime018(String hhmmss,String ddmmyy){
        StringBuffer sb = new StringBuffer();
        sb.append("/Date(");
        String time = "20"+ddmmyy.substring(0, 2)+"-"+ddmmyy.substring(2, 4)+"-"+ddmmyy.substring(4, 6);
        //System.out.println(time);
        time +=" "+hhmmss.substring(0, 2)+":"+hhmmss.substring(2, 4)+":"+hhmmss.substring(4, 6);
        //System.out.println(time);
        Date parseDateTime = parseDate(time,STRING_FORMAT_YMDHMS);
        sb.append(parseDateTime.getTime());
        sb.append(")/");
        return sb.toString();
    }
    /**
     * 处理时间，转化成时间戳 。格式为：Data(1481697128000)
     * @param hhmmss
     * @param ddmmyy
     * @return
     */
    public static String dealWithTime(String hhmmss,String ddmmyy){
        StringBuffer sb = new StringBuffer();
        sb.append("/Date(");
        String time = "20"+ddmmyy.substring(4, 6)+"-"+ddmmyy.substring(2, 4)+"-"+ddmmyy.substring(0, 2);
        //System.out.println(time);
        time +=" "+hhmmss.substring(0, 2)+":"+hhmmss.substring(2, 4)+":"+hhmmss.substring(4, 6);
        //System.out.println(time);
        Date parseDateTime = parseDate(time,STRING_FORMAT_YMDHMS);;
        sb.append(parseDateTime.getTime());
        sb.append(")/");
        return sb.toString();
    }
}
