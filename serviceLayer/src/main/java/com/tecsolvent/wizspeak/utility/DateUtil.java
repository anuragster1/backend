package com.tecsolvent.wizspeak.utility;

import org.apache.commons.lang.time.DateUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by jaison on 17/3/16.
 */
public class DateUtil {


	public static Date getDate(){

		Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);

		return today;
	}
}
