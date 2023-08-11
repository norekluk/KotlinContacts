fun main() {
    val companyCount = readln().toInt()
    val income = DoubleArray(companyCount)
    repeat(companyCount) {
        income[it] = readln().toDouble()
    }
    val taxes = DoubleArray(companyCount)
    repeat(companyCount) {
        taxes[it] = readln().toDouble()
    }
    val computedTaxes = DoubleArray(companyCount)
    repeat(companyCount) {
        computedTaxes[it] = income[it] * (taxes[it] / 100)
    }
    val max = computedTaxes.maxOf { it }
    println(computedTaxes.indexOfFirst { it == max } + 1)
}