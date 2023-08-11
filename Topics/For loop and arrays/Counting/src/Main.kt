fun main() {
    val numbers = mutableListOf<Int>()
    repeat(readln().toInt()) {
        numbers.add(readln().toInt())
    }
    val number = readln().toInt()
    println(numbers.filter { it == number}.size)
}