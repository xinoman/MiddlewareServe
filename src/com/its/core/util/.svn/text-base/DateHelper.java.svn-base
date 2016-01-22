/**
 * 
 */
package com.its.core.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 创建日期 2012-9-19 下午04:18:17
 * @author GuoPing.Wu QQ:365175040
 * Copyright: ITS Technologies CO.,LTD.
 */
public class DateHelper {
	
	/**
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static String dateToString(java.util.Date date, String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		String dateString = "";
		if (date != null) {
			dateString = formatter.format(date);
		}
		return dateString;
	}

    public static Date parseDateString(String basicDate, String dataFormat) throws ParseException{
	    if(basicDate == null || basicDate.trim().equals("") || "null".equalsIgnoreCase(basicDate))
	        return null;
	    SimpleDateFormat df = new SimpleDateFormat(dataFormat);
	    Date date = df.parse(basicDate);
	    return date;
    }
    
	/**
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static String dateToString(long millis, String format) {
		java.util.Date date = new java.util.Date(millis);
		return DateHelper.dateToString(date,format);
	}    
	
	/**
	 * 日期加减天	
	 * @param currDate 		当前日期字符串，格式"yyyy-MM-dd HH:mm:ss"
	 * @param count    		多少天.
	 * @return String	   	日期字符串
	 * @throws BaseAppException 
	 */
	public static String addDay(String currDate, int count ) throws Exception {
		final Date D1= str2date(currDate, "-1");
		final Calendar cal = Calendar.getInstance();
		cal.setTime(D1);
	    cal.add(Calendar.MINUTE, +(count * 60 * 24 ));
		final Date result = cal.getTime();
		final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(result);
		 
	}
	
	/**
	 * 字符串转换成指定格式日期
	 * @param currDate		当前日期字符串
	 * @param format		格式字符串
	 * @return Date			日期对象
	 * @throws BaseAppException
	 */
	public static Date str2date(String currDate, String format) throws Exception {
		Date retDate = null;
		if (format.equals("-1")) {
			format = "yyyy-MM-dd HH:mm:ss";
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		try {
			retDate = dateFormat.parse(currDate);
			return retDate;
		} catch (Exception e) {
			dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				retDate = dateFormat.parse(currDate);
				return retDate;
			} catch (Exception ex) {
				throw new Exception();
			}
		}
	}
	
	/**
	 * 将毫秒数转化为时分秒格式
	 * @param millisecond
	 * @return
	 */
	public static String parseMillisecond(long millisecond){
		//long[] arrTime = new long[]{0,0,0};
		long hour = 0,minute = 0,second = 0;
		second = millisecond/1000;
		if(second>=3600){
			hour = second/3600;
			second -= hour*3600;
		}
		if(second>=60){
			minute = second/60;
			second -= minute*60;				
		}			
		
		StringBuilder timeBuilder = new StringBuilder();
		if(hour!=0) 	timeBuilder.append(hour).append("小时");
		if(minute!=0) 	timeBuilder.append(minute).append("分");
		if(second!=0) 	timeBuilder.append(second).append("秒");
		return timeBuilder.toString();
	}
	
	/**
	 * 得到二个日期间的间隔天数
	 */
	public static long getTwoDay(String sj1, String sj2) throws Exception{
		SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
		long day = 0;
		java.util.Date date = myFormatter.parse(sj1);
		java.util.Date mydate = myFormatter.parse(sj2);
		day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
		return day;
	}

	/**
	 * 两个时间之间的天数
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static long getDays(String date1, String date2) {
		if (date1 == null || date1.equals(""))
			return 0;
		if (date2 == null || date2.equals(""))
			return 0;
		// 转换为标准时间
		SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date date = null;
		java.util.Date mydate = null;
		try {
			date = myFormatter.parse(date1);
			mydate = myFormatter.parse(date2);
		} catch (Exception e) {
		}
		long day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
		return day;
	}
	
	public static int getDaysBetween(String beginDate, String endDate) throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date bDate = format.parse(beginDate);
		Date eDate = format.parse(endDate);
		Calendar d1 = new GregorianCalendar();
		d1.setTime(bDate);
		Calendar d2 = new GregorianCalendar();
		d2.setTime(eDate);
		int days = d2.get(Calendar.DAY_OF_YEAR) - d1.get(Calendar.DAY_OF_YEAR);
		int y2 = d2.get(Calendar.YEAR);
		if (d1.get(Calendar.YEAR) != y2) {
			d1 = (Calendar) d1.clone();
			do {
				days += d1.getActualMaximum(Calendar.DAY_OF_YEAR);// 得到当年的实际天数
				d1.add(Calendar.YEAR, 1);
			} while (d1.get(Calendar.YEAR) != y2);
		}
		return days;
	} 
	
	public static Date getDateAdd(Date date, int dateType, int value) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        switch (dateType) {
            case Calendar.YEAR:
                cal.add(Calendar.YEAR, value);
                break;
            case Calendar.MONTH:
                cal.add(Calendar.MONTH, value);
                break;
            case Calendar.DATE:
                cal.add(Calendar.DATE, value);
                break;
        }
        return cal.getTime();
    }
	
	public static Date CreateDateFromYYYYMMDD(String yyyymmdd) throws NumberFormatException {
	    if (yyyymmdd == null)
	        return null;
	    int val = Integer.parseInt(yyyymmdd);
	    Date date = null;
	    if (val >= 0) {
	        int year = val / 10000;
	        if (year <= 9999) {
	            int month = (val / 100) % 100;
	            int day = val % 100;
	            Calendar calendar = Calendar.getInstance();
	            calendar.clear();
	            calendar.set(year, month - 1, day);
	            date = calendar.getTime();
	        }    
	    }	        
	    if (date == null)   
	        throw new NumberFormatException("incorrect YYYYMMDD format [" + yyyymmdd + "]");
	    return date;
	}
	
	/**
	 * @param yyyymmdd int value like <code>19001231</code>; 0 will return null
	 */
	public static Date CreateDateFromYYYYMMDDValue(int yyyymmdd) throws NumberFormatException {
		if (yyyymmdd != 0) {
		    long year = (yyyymmdd / 10000) % 10000;
		    long month = (yyyymmdd / 100) % 100;
		    long day = yyyymmdd % 100;
		    if (year < 0 || year > 9999 ||
		        month < 0 || month > 99 ||
		        day < 0 || day > 99)
		        throw new NumberFormatException("incorrect YYYYMMDD format [" + yyyymmdd + "]");
		    Calendar c = Calendar.getInstance();
		    c.clear();
		    c.set((int) year, (int) month - 1, (int) day);
		    return c.getTime();
		}
		return null;
	}
	
    /**
     * @param yyyymmdd int value like <code>19001231</code>; 0 will return null
     */
    public static Calendar CreateCalendarFromYYYYMMDDValue(int yyyymmdd) throws NumberFormatException {
        Date dt = CreateDateFromYYYYMMDDValue(yyyymmdd);
        Calendar cal = null;
        if (dt != null) {
            cal = Calendar.getInstance();
            cal.setTime(dt);
        }    
        return cal;
    }
    
    public static int GetYYYYMMDDValueFromDate(Date date) {
        if (date == null)
            return 0;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return GetYYYYMMDDValueFromCalendar(cal);
    }
    
	public static int GetYYYYMMDDValueFromCalendar(Calendar cal) {
	    if (cal == null)
	        return 0;
	    int year = cal.get(Calendar.YEAR);
	    int month = cal.get(Calendar.MONTH) + 1;
	    int day = cal.get(Calendar.DATE);
	    int value = day + 100 * (month + (100 *  year));
	    return value;
	}

}
