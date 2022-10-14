package com.thurainx.matchguard.utils

import com.thurainx.matchguard.data.vos.DateVO
import java.text.SimpleDateFormat
import java.util.*


class DateUtils {
    fun getNextWeekDates(): List<DateVO> {
        var dayList: ArrayList<DateVO> = arrayListOf()
        val formatter = SimpleDateFormat("EEE\ndd")
        val dayFormatter = SimpleDateFormat("dd")
        val standardFormatter = SimpleDateFormat("dd-MMMM-yyyy")
        val calendar = Calendar.getInstance()

        dayList.add(
            DateVO(
                rawDate = calendar.time.toString(),
                formatDate = formatter.format(calendar.time),
                day = dayFormatter.format(calendar.time),
                standardMatchDate = standardFormatter.format(calendar.time)
            )
        )
        for (i in 0..6) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            dayList.add(
                DateVO(
                    rawDate = calendar.time.toString(),
                    formatDate = formatter.format(calendar.time),
                    day = dayFormatter.format(calendar.time),
                    standardMatchDate = standardFormatter.format(calendar.time)
                )
            )

        }

        return dayList.toList()
    }

    fun convertToReadableTime(millis: String) : String{
        val format = SimpleDateFormat("hh:mm a")

        val netDate = Date(millis.toLong() * 1000)
        return format.format(netDate)

    }

    fun convertToStandardDate(millis: String) : String{
        val format = SimpleDateFormat("dd-MMMM-yyyy")

        val netDate = Date(millis.toLong() * 1000)
        return format.format(netDate)

    }

    fun unixTimeToMillis(millis: String) : Long{
        val netDate = Date(millis.toLong() * 1000)
        return netDate.time
    }




}

fun String.toApiDateFormat() : String{
    val formatter = SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy",Locale.ENGLISH)

    val parsedDate: Date = formatter.parse(this)
    val print = SimpleDateFormat("yyyy-MM-dd")

    return print.format(parsedDate)

}

fun String.toCinemaDisplayDateFormat() : String{
    val formatter = SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy",Locale.ENGLISH)

    val parsedDate: Date = formatter.parse(this)
    val print = SimpleDateFormat("E, dd MMMM yyyy")

    return print.format(parsedDate)

}

fun String.toReceiptDateFormat() : String{
    val formatter = SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy",Locale.ENGLISH)

    val parsedDate: Date = formatter.parse(this)
    val print = SimpleDateFormat("dd MMMM")

    return print.format(parsedDate)

}

fun formatCard(cardNumber: String?): String? {
    if (cardNumber == null) return null
    val delimiter = ' '
    return cardNumber.replace(".{4}(?!$)".toRegex(), "$0$delimiter")
}



fun getCurrentTimeInMilli() : Long{
    val now = Calendar.getInstance()
    return now.timeInMillis
}

fun unixToMilli(str: String) : Long{
    val netDate = Date(str.toLong() * 1000)
   return netDate.time
}
