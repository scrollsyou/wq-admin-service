package com.yywh.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @author you
 *
 */
public class DateUtil {

	public static String getNowStr() {
		SimpleDateFormat simple = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
		return simple.format(new Date());
	}
	
	public static Date stringToDate(String yyyymmdd) throws ParseException {
		SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
		return simple.parse(yyyymmdd);
	}
}
