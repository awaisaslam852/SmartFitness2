package com.axles.smartfitness.engine

import java.text.SimpleDateFormat
import java.util.*

fun Date.toLongString(): String {
	val dateFormat = SimpleDateFormat("MMMM dd, yyyy")
	return dateFormat.format(this)
}

fun Date.toShortString(): String {
	val dateFormat = SimpleDateFormat("MM/dd/yyyy")
	return dateFormat.format(this)
}

fun Date.toString(format: String): String {
	val dateFormat = SimpleDateFormat(format)
	return dateFormat.format(this)
}

class DateHelper {
	companion object {
		fun date(year: Int, month: Int, day: Int): Date {
			val calendar = Calendar.getInstance()
			calendar.set(Calendar.YEAR, year)
			calendar.set(Calendar.MONTH, month)
			calendar.set(Calendar.DAY_OF_MONTH, day)
			return calendar.time
		}

		fun dateToText(date: Date?): String? {
			val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
			return dateFormat.format(date)
		}

		fun textToDate(strDate: String?): Date? {
			try {
				val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				return dateFormat.parse(strDate)
			} catch (e: Exception) {
				e.printStackTrace()
			}
			return null
		}

		fun birthdayToDate(strDate: String?): Date? {
			try {
				val dateFormat = SimpleDateFormat("MM/dd/yyyy")
				return dateFormat.parse(strDate)
			} catch (e: Exception) {
				e.printStackTrace()
			}
			return null
		}

		fun birthdayToText(date: Date?): String? {
			val dateFormat = SimpleDateFormat("MM/dd/yyyy")
			return dateFormat.format(date)
		}

		fun year(date: Date?): Int {
			if (date == null) return 0
			val dateFormat = SimpleDateFormat("yyyy")
			return dateFormat.format(date).toInt()
		}

		fun month(date: Date?): Int {
			if (date == null) return 0
			val dateFormat = SimpleDateFormat("MM")
			return dateFormat.format(date).toInt()-1
		}

		fun day(date: Date?): Int {
			if (date == null) return 0
			val dateFormat = SimpleDateFormat("dd")
			return dateFormat.format(date).toInt()
		}

		fun prevDay(date: Date?): Date? {
			val cal: Calendar = Calendar.getInstance()
			cal.setTime(date)
			cal.add(Calendar.DATE, -1)
			return cal.getTime()
		}

		fun nextDay(date: Date?): Date? {
			val cal: Calendar = Calendar.getInstance()
			cal.setTime(date)
			cal.add(Calendar.DATE, 1)
			return cal.getTime()
		}

		fun isStartOfMonth(date: Date?): Boolean {
			val c: Calendar = Calendar.getInstance() // this takes current date
			c.setTime(date)
			c.set(Calendar.DAY_OF_MONTH, 1)
			val start: Date = c.getTime()
			return (year(date) == year(start)
					&& month(date) == month(start)
					&& day(date) == day(start))
		}

		fun isEndOfMonth(date: Date?): Boolean {
			val c: Calendar = Calendar.getInstance()
			c.setTime(date)
			c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH))
			val end: Date = c.getTime()
			return (year(date) == year(end)
					&& month(date) == month(end)
					&& day(date) == day(end))
		}

		fun isSameDay(date1: Date?, date2: Date?): Boolean {
			if (date1 == null || date2 == null) {
				return false
			}
			val dateFormat = SimpleDateFormat("dd/MM/yyyy")
			val strDate1: String = dateFormat.format(date1)
			val strDate2: String = dateFormat.format(date2)
			return strDate1 == strDate2
		}
	}
}