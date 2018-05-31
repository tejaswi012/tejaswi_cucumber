package com.core.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	public String convertDateToString(Date date, String dateFormatter) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormatter);
		String dateStr = sdf.format(date);

		return dateStr;

	}

	public Date convertCalenderToDate() {

		Calendar calendar = Calendar.getInstance();
		return calendar.getTime();
	}

}
