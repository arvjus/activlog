package org.zv.activlog.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
	public static final String YYYYMMDD = "yyyymmdd";
	public static final String MMDDYYYY = "mmddyyyy";
	public static final String DDMMYYYY = "ddmmyyyy";

	private static String defaultFormat = YYYYMMDD;

	private static final SimpleDateFormat DATE_FORMAT_YYYYMMDD = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat DATE_FORMAT_MMDDYYYY = new SimpleDateFormat("MM/dd/yyyy");
	private static final SimpleDateFormat DATE_FORMAT_DDMMYYYY = new SimpleDateFormat("dd/MM/yyyy");
	
	private static Pattern datePatternYYYYMMDD = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}");
	private static Pattern datePatternMMDDYYYY = Pattern.compile("[0-9]{2}/[0-9]{2}/[0-9]{4}");
	private static Pattern datePatternDDMMYYYY = Pattern.compile("[0-9]{2}/[0-9]{2}/[0-9]{4}");
	private static Pattern numericalPattern = Pattern.compile("[0-9-.]+");


	public static String getDefaultFormat() {
		return defaultFormat;
	}

	public static void setDefaultFormat(String defaultFormat) {
		Utils.defaultFormat = defaultFormat;
	}

	public static String getDateFormatString(boolean shortFormat) {
		if (YYYYMMDD.equals(defaultFormat)) {
			return shortFormat ? "yy-MM" : "yyyy-MM-dd";
		} else if (DDMMYYYY.equals(defaultFormat)) {
			return shortFormat ? "MM/yy" : "dd/MM/yyyy";
		} else if (MMDDYYYY.equals(defaultFormat)) {
			return shortFormat ? "MM/yy" : "MM/dd/yyyy";
		}
		return null;
	}
	
	public static SimpleDateFormat getDateFormat() {
		if (YYYYMMDD.equals(defaultFormat)) {
			return DATE_FORMAT_YYYYMMDD;
		} else if (DDMMYYYY.equals(defaultFormat)) {
			return DATE_FORMAT_DDMMYYYY;
		} else if (MMDDYYYY.equals(defaultFormat)) {
			return DATE_FORMAT_MMDDYYYY;
		}
		return null;
	}
	
	public static Date stringYYYYMMDDToDate(String string) {
		try {
			if (string.length() == 7) {
				string += "-01";
			}
			return DATE_FORMAT_YYYYMMDD.parse(string);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String stringToYYYYMMDD(String string) {
		try {
			Date date = getDateFormat().parse(string);
			return DATE_FORMAT_YYYYMMDD.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String dateToString(Date date) {
		return getDateFormat().format(date);
	}
	
	public static int dateDiff(String dateFrom, String dateTo) {
		int diff = 0;
		if (dateFrom != null && dateTo != null) {
			try {
				Date from = getDateFormat().parse(dateFrom);
				Date to = getDateFormat().parse(dateTo);
				if (from != null && to != null) {
					diff = (int)((to.getTime() - from.getTime())/24/60/60/1000);  
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return diff;
	}

	public static boolean isValidDate(String value) {
		if (value == null) {
			return false;
		}
		
		Pattern pattern = null;
		if (YYYYMMDD.equals(defaultFormat)) {
			pattern = datePatternYYYYMMDD;
		} else if (DDMMYYYY.equals(defaultFormat)) {
			pattern = datePatternMMDDYYYY;
		} else if (MMDDYYYY.equals(defaultFormat)) {
			pattern = datePatternDDMMYYYY;
		}
		
		Matcher matcher = pattern.matcher(value);
		return matcher.matches();
	}

	public static boolean isValidNumerical(String value) {
		if (value == null) {
			return false;
		}
		Matcher matcher = numericalPattern.matcher(value);
		return matcher.matches();
	}
}
