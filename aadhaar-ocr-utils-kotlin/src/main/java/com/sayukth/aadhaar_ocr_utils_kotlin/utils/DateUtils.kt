package com.sayukth.aadhaar_ocr_utils_kotlin.utils

import com.sayukth.aadhaar_ocr_utils_kotlin.error.ActivityException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.StringTokenizer


object DateUtils {
    const val DB_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss"

    // If time stamp is required in ISO 8601 format
    const val API_DATE_FORMAT_ISO8601 = "yyyy-MM-dd'T'HH:mm:ssZ"
    var DATEFORMAT = "yyyy-MM-dd'T'HH:mm"
    private const val datePattern = "MM/dd/yyyy"
    private val timePattern: String = datePattern + " HH:mm:ss"
    private const val MillisecondstoMinuteSeconds = "dd MMM yyyy hh:mm a"
    private const val uiDateTimePattern = "dd MMM yyyy hh:mm a"
    private const val ABOUT_PAGE_DATE_TIME_PATTERN = "dd MMM yyyy HH:mm:ss "
    private const val MONTH_YEAR_PATTERN = "MMMM yyyy "
    private const val EDITABLE_FIELD_DATE_FORMAT = "dd-MM-yyyy"
    const val SURVEY_DISPLAY_DATE_PATTERN = "dd MMM yyyy hh:mm:ss"

    /**
     * To get the current datetime
     */
    @get:Throws(ActivityException::class)
    val dateTimeNow: String
        get() = try {
            val dateFormat = SimpleDateFormat(
                timePattern, Locale.getDefault()
            )
            val date = Date()
            dateFormat.format(date)
        } catch (e: Exception) {
            throw ActivityException(e)
        }
    val currentDate: Long
        get() {
            val date = Date()
            return date.time
        }

    @Throws(ActivityException::class)
    fun dateToMilliSeconds(myDate: String?): Long {
        return try {
            val sdf =
                SimpleDateFormat(timePattern)
            var date: Date? = null
            try {
                date = sdf.parse(myDate)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            date!!.time
        } catch (e: Exception) {
            throw ActivityException(e)
        }
    }

    /**
     * This method is used to take date as "dd MMM yyyy HH:mm:ss" format and return milliseconds
     *
     * @param surveyStartDateTime,surveyEndDateTime
     * @return
     */
    @Throws(ActivityException::class)
    fun surveyDateToMilliSeconds(surveyStartDateTime: String?, surveyEndDateTime: String?): Long {
        return try {
            val sdf = SimpleDateFormat(SURVEY_DISPLAY_DATE_PATTERN, Locale.ENGLISH)
            var d1: Date? = null
            var d2: Date? = null
            d1 = sdf.parse(surveyStartDateTime)
            d2 = sdf.parse(surveyEndDateTime)
            var diff = d2.time - d1.time
            if (diff < 0) {
                diff = -diff
            }
            diff
        } catch (e: Exception) {
            throw ActivityException(e)
        }
    }

    /**
     * This method is used to take milliseconds and returns hours,minutes and seconds
     *
     * @param millis
     * @return
     */
    @Throws(ActivityException::class)
    fun milliSecondsToHoursMinutes(millis: Long): String {
        try {
            val totalSecs = millis / 1000
            val hours = totalSecs / 3600
            val mins = totalSecs / 60 % 60
            val secs = totalSecs % 60
            val minsString = if (mins == 0L) "00" else if (mins < 10) "0$mins" else "" + mins
            val secsString = if (secs == 0L) "00" else if (secs < 10) "0$secs" else "" + secs
            if (hours > 0) {
                if (hours == 1L) {
                    return "$hours hr:$minsString mins:$secsString secs"
                } else if (hours > 1) {
                    return "$hours hrs:$minsString mins:$secsString secs"
                }
            } else if (mins > 0) {
                if (mins == 1L) {
                    return "$mins min:$secsString secs"
                } else if (mins > 1) {
                    return "$mins mins:$secsString secs"
                }
            } else {
                return "$secsString secs"
            }
        } catch (e: Exception) {
            throw ActivityException(e)
        }
        return ""
    }

    /**
     * This method returns the current date time in the format: MM/dd/yyyy HH:MM a
     *
     * @param theTime the current time
     * @return the current date/time
     */
    @Throws(ActivityException::class)
    fun getTimeNow(theTime: Date?): String {
        return getDateTime(timePattern, theTime)
    }

    /**
     * This method generates a string representation of a date's date/time in the
     * format you specify on input
     *
     * @param aMask the date pattern the string is in
     * @param aDate a date object
     * @return a formatted string representation of the date
     */
    @Throws(ActivityException::class)
    fun getDateTime(aMask: String?, aDate: Date?): String {
        return try {
            var df: SimpleDateFormat? = null
            var returnValue = ""
            if (aDate == null) {
                return ""
            } else {
                df = SimpleDateFormat(aMask)
                returnValue = df.format(aDate)
            }
            returnValue
        } catch (e: Exception) {
            throw ActivityException(e)
        }
    }

    /* This method take a String of date format and returns today date */
    @Throws(ActivityException::class)
    fun getCurrentDate(aMask: String?): String {
        return try {
            val date = Date()
            var returnValue = ""
            var df: SimpleDateFormat? = null
            if (aMask == null) {
                return ""
            } else {
                df = SimpleDateFormat(aMask)
                returnValue = df.format(date)
            }
            returnValue
        } catch (e: Exception) {
            throw ActivityException(e)
        }
    }

    @Throws(ActivityException::class)
    private fun yesterday(): Date {
        return try {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DATE, -1)
            calendar.time
        } catch (e: Exception) {
            throw ActivityException(e)
        }
    }

    @Throws(ActivityException::class)
    fun aAdhaarDateFormated(dateString: String?): String {
        return try {
            val delimeter = "/-"
            val year: String
            val month: String
            val day: String
            var str1 = String()
            var str2 = String()
            var str3 = String()
            val tokenizer = StringTokenizer(dateString, delimeter)
            while (tokenizer.hasMoreTokens()) {
                str1 = tokenizer.nextToken()
                str2 = tokenizer.nextToken()
                str3 = tokenizer.nextToken()
            }
            if (str1.length == 4) {
                year = str1
                month = str2
                day = str3
                String.format("%2s-%2s-%4s", day, month, year)
            } else {
                day = str1
                month = str2
                year = str3
                String.format("%2s-%2s-%4s", day, month, year)
            }
            //            return "";
        } catch (e: Exception) {
            throw ActivityException(e)
        }
    }
}