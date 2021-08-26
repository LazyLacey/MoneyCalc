package com.example.moneycalc

import java.util.*

fun Int.toRubbles() : String {
    return (this / 100).toString() + "." + if (this % 100 < 10) ("0" + (this % 100).toString()) else (this % 100).toString()
}

fun getDateString(calendar: Calendar = Calendar.getInstance()) : String {
    return (calendar.get(Calendar.DAY_OF_MONTH)).toString()+"."+(calendar.get(
        Calendar.MONTH)+1).toString()+"."+calendar.get(Calendar.YEAR).toString()
}