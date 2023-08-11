import kotlinx.datetime.*

fun nextMonth(date: String): String {
    // Write your code here
    val inputInstant = Instant.parse(date)
    return inputInstant.plus(DateTimePeriod(months = 1), TimeZone.UTC).toString()
}

fun main() {
    val date = readln()
    println(nextMonth(date))
}