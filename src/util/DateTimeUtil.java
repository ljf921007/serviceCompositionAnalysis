package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;



public class DateTimeUtil {

	public static final String tag = DateTimeUtil.class.getName();

//	@SuppressLint("SimpleDateFormat")
	public static void main(String[] args) {
		Date date = new Date();
		Long time = date.getTime();
		System.out.println(" now time is :  " + time);

		Date d = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(" second time is :  " + sdf.format(d));
		
		time = time2Millions(sdf.format(d), "");
		System.out.println(" exchange time is :  " + time);
	}

	/**
	 * 得到当前的天数与具体某一天之间相差的天数
	 * 
	 * @param year
	 * @param mon
	 * @param date
	 * @return
	 */
	public static int getDays(String year, String mon, String date) {
		Date today = new Date();
		Calendar cal = Calendar.getInstance();
		long todayMilliseconds = today.getTime();
		long todayMilliseconds1 = cal.getTimeInMillis();

		int yearInt = 2013;
		if (year != null && !year.trim().equals("")) {
			yearInt = Integer.parseInt(year);
		}

		int monInt = 0;
		if (mon != null && !mon.trim().equals("")) {
			monInt = Integer.parseInt(mon);
		}

		int dateInt = 0;
		if (date != null && !date.trim().equals("")) {
			dateInt = Integer.parseInt(date);
		}

		cal.set(Calendar.YEAR, yearInt);
		cal.set(Calendar.MONTH, monInt - 1);
		cal.set(Calendar.DAY_OF_MONTH, dateInt);
		// long compareMilliseconds=compareDay.getTime();
		long compareMilliseconds = cal.getTimeInMillis();
		// 获得两个日期之间的毫秒差。
		long differenceMilliseconds = todayMilliseconds - compareMilliseconds;
		// 一天的毫秒数
		long oneDayMilliseconds = 24 * 60 * 60 * 1000;
		// 除以一天的毫秒数，就是相差的天数。
		int result = (int) (differenceMilliseconds / oneDayMilliseconds);
		return result;
	}

	public static String getMonthAndDate() {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		return month + "/" + day;
	}
	
	public static long getDayNumber() {
		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.YEAR)*1000+cal.get(Calendar.DAY_OF_YEAR);
		return day;
	}

	// static SimpleDateFormat sdf = new
	// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static String getTime(long millionSecond) {
//		Log.i(tag, millionSecond + "");
		Date d = new Date(millionSecond);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(d);
	}

	public static String getTime(long millionSecond, String timeFormate) {
//		Log.i(tag, millionSecond + "");
		SimpleDateFormat sdf = new SimpleDateFormat(timeFormate);
		Date d = new Date(millionSecond);
		return sdf.format(d);
	}

	/**
	 * 得到time1-time2的毫秒数
	 * 
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static long compareTime(String time1, String time2) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			long d1 = sdf.parse(time1).getTime();
			long d2 = sdf.parse(time2).getTime();
			return d1 - d2;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0L;
	}
	
	public static boolean isLessCurTime(String time1, String df) {
		SimpleDateFormat sdf = new SimpleDateFormat(df);
		try {
			long d1 = sdf.parse(time1).getTime();
			long d2 = System.currentTimeMillis();
			if((d1 - d2)>0){
				return false;
			}
		} catch (ParseException e) {
//			e.printStackTrace();
			return false;
		}
		return true;
	}
	

	public static String getTime(String millionSecond) {

		if (millionSecond != null && !millionSecond.trim().equals("")) {
			try {
				Long lg = Long.parseLong(millionSecond);
				return getTime(lg);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}

		}
		return millionSecond.trim();
	}

	/**
	 * 得到前一秒的时间
	 * 
	 * @param millionSecond
	 * @return
	 */
	public static String getPreTime(String millionSecond) {

		if (millionSecond != null && !millionSecond.trim().equals("")) {
			try {
				Long lg = Long.parseLong(millionSecond) - 1000;
				return getTime(lg);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}

		}
		return millionSecond.trim();
	}

	public static long getTime(int delay) {

		if (delay > 0) {
			return (System.currentTimeMillis() + delay * 60 * 1000);
		}
		return (System.currentTimeMillis());
	}

	public static String getCurDate() {

		return getTime(System.currentTimeMillis(), "yyyy-MM-dd");
	}
	
	public static String getCurDate(String sdf ) {

		return getTime(System.currentTimeMillis(), sdf);
	}

	public static String getCurTime() {

		return getTime(System.currentTimeMillis(), "HH:mm:ss");
	}

	public static String getCurDateAndTime() {

		return getTime(System.currentTimeMillis());
	}

	public static Long time2Millions(String time, String formate) {

		SimpleDateFormat format = null ;
		if (formate == null || "".equals(formate)) {
			format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}else{
			format = new SimpleDateFormat(formate);
		}

		Date date;
		try {
			date = format.parse(time);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			return cal.getTimeInMillis();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return null;

	}

}
