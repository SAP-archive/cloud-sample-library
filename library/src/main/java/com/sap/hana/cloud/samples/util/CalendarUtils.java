package com.sap.hana.cloud.samples.util;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * This class is used for calculating the deadlines for returning a book after it has been reserved.
 * */
public class CalendarUtils {

	/**
	 * This method calculates how many days are between two dates.
	 *
	 * @param date1
	 * @param date2
	 * @return the number of days between these two dates, ignoring the hour, minute, seconds and milliseconds.
	 */
	public static int getDaysBetween(Date date1, Date date2){
		Date normalizedDate1 = normalize(date1);
		Date normalizedDate2 = normalize(date2);

		long diffInMiliseconds = normalizedDate2.getTime() - normalizedDate1.getTime();

		return (int)TimeUnit.DAYS.convert(diffInMiliseconds,TimeUnit.MILLISECONDS);
	}

	/**
	 * This method returns new date by adding (subtracting) days to the original date.
	 *
	 * @param date
	 * 			base date
	 * @param daysToAdd
	 * 			amount of days to add or subtract (negative values)
	 * @return the returned date is normalized
	 */
	public static Date getDateByAddDays(Date date, int daysToAdd){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 1);
		calendar.add(Calendar.DAY_OF_MONTH, daysToAdd);
		return calendar.getTime();
	}

	/**
	 * This method returns new date by adding (subtracting) months to the original date.
	 *
	 * @param date
	 * 			base date
	 * @param monthsToAdd
	 * 			amount of months to add or subtract (negative values)
	 * @return the returned date is normalized
	 */
	public static Date getDateByAddMonths(Date date, int monthsToAdd){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 1);
		calendar.add(Calendar.MONTH, monthsToAdd);
		return calendar.getTime();
	}

	/**
	 * This method normalizes date by removing all information abut the hour, minutes, seconds and milliseconds.
	 * Only information about the day, month and year are left.
	 *
	 * @param date
	 * @return
	 */
	public static Date normalize(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 1);
		return calendar.getTime();
	}

}
