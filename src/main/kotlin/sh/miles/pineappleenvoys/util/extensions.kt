package sh.miles.pineappleenvoys.util

import java.time.LocalTime

fun LocalTime.hourMinuteEquals(localTime: LocalTime): Boolean {
    return hour == localTime.hour && minute == localTime.minute
}

fun LocalTime.isNowOrAfter(localTime: LocalTime): Boolean {
    return isAfter(localTime) || hourMinuteEquals(localTime)
}

fun LocalTime.isNowOrBefore(localTime: LocalTime): Boolean {
    return isBefore(localTime) || hourMinuteEquals(localTime)
}
