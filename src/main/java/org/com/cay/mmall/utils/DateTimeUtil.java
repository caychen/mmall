package org.com.cay.mmall.utils;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * Created by Caychen on 2018/7/5.
 */
public class DateTimeUtil {

	//joda-time
	private static final String STANDARD_FORMATTER = "yyyy-MM-dd HH:mm:ss";

	//str -> date
	public static Date strToDate(String dateTimeStr, String formatter) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(formatter);

		return dateTimeFormatter.parseDateTime(dateTimeStr).toDate();
	}

	//date -> str
	public static String dateToStr(Date date, String formatter) {
		if (date == null) {
			return StringUtils.EMPTY;
		}
		DateTime dateTime = new DateTime(date);
		return dateTime.toString(formatter);
	}

	//str -> date
	public static Date strToDate(String dateTimeStr) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(STANDARD_FORMATTER);

		return dateTimeFormatter.parseDateTime(dateTimeStr).toDate();
	}

	//date -> str
	public static String dateToStr(Date date) {
		if (date == null) {
			return StringUtils.EMPTY;
		}
		DateTime dateTime = new DateTime(date);
		return dateTime.toString(STANDARD_FORMATTER);
	}

	public static void main(String[] args) {
		System.out.println(DateTimeUtil.strToDate("2017-11-11 23:21:33", "yyyy-MM-dd HH:mm:ss"));
		System.out.println(DateTimeUtil.dateToStr(new Date(), "yyyy-MM-dd HH:mm:ss"));

	}
}
