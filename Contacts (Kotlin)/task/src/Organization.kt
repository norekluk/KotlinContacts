package contacts

import com.squareup.moshi.JsonClass
import kotlinx.datetime.Clock

@JsonClass(generateAdapter = true)
class Organization(
    private var name: String,
    private var address: String,
    private val inputNumber: String
) : Record(name, inputNumber) {

    constructor() : this("", "", "1") {
        enterName()
        enterAddress()
        enterNumber()
    }

    override fun allValues(): String {
        return buildString {
            append(name)
            append(' ')
            append(address)
            append(' ')
            append(number)
        }.lowercase()
    }

    private fun enterName() {
        print("Enter the name: ")
        name = readln()
        this.wholeName = name
    }

    private fun enterAddress() {
        print("Enter the address: ")
        address = readln()
    }

    private fun enterNumber() {
        print("Enter the number: ")
        number = readln()
    }

    override fun toString(): String {
        return "Organization name: $name\n" +
                "Address: $address\n" +
                "Number: $number\n" +
                "Time created: $timeCreated\n" +
                "Time last edit: $lastEditTime"
    }

    override fun edit() {
        print("Select a field (name, address, number): ")
        when (val field = readln()) {
            "name" -> enterName()
            "address" -> enterAddress()
            "number" -> enterNumber()
            else -> {
                println("Unknown field: $field")
                return
            }
        }
        lastEditTime = Clock.System.now().toString()
    }
}
