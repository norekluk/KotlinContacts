fun main() {
    val count = readln().toInt()
    val numbers = mutableListOf<Int>()
    repeat(count) {
        numbers.add(readln().toInt())
    }
    if (numbers.contains(readln().toInt()))    {
        println("YES")
    } else {
        println("NO")
    }
}