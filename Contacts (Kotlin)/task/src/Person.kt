package contacts

import com.squareup.moshi.JsonClass
import kotlinx.datetime.Clock

@JsonClass(generateAdapter = true)
class Person(
    private var name: String,
    private var surname: String,
    private var birthDate: String,
    private var gender: String,
    private var inputNumber: String
) : Record("$name $surname", inputNumber) {
    private val genderRegex = """[MF]"""
    private val noData = "[no data]"

    constructor() : this("", "", "", "", "1") {
        enterName()
        enterSurname()
        this.wholeName = "$name $surname"
        enterBirthDate()
        enterGender()
        enterNumber()
    }

    override fun allValues(): String {
        return buildString {
            append(name)
            append(' ')
            append(surname)
            append(' ')
            append(birthDate)
            append(' ')
            append(gender)
            append(' ')
            append(number)
        }.lowercase()
    }

    private fun enterName() {
        print("Enter the name: ")
        name = readln()
        this.wholeName = "$name $surname"
    }

    private fun enterSurname() {
        print("Enter the surname: ")
        surname = readln()
        this.wholeName = "$name $surname"
    }

    private fun enterBirthDate() {
        print("Enter the birth date: ")
        val input = readln()
        if (input.isEmpty()) {
            println("Bad birth date!")
            this.birthDate = noData
        } else {
            this.birthDate = input
        }
    }

    private fun enterGender() {
        print("Enter the gender (M, F): ")
        val input = readln()
        if (genderRegex.toRegex().matches(input)) {
            this.gender = input
        } else {
            println("Bad gender!")
            this.gender = noData
        }
    }

    private fun enterNumber() {
        print("Enter the number: ")
        number = readln()
    }

    override fun edit() {
        print("Select a field (name, surname, birth, gender, number): ")
        when (val field = readln()) {
            "name" -> enterName()
            "surname" -> enterSurname()
            "birth" -> enterBirthDate()
            "gender" -> enterGender()
            "number" -> enterNumber()
            else -> {
                println("Unknown field: $field")
                return
            }
        }
        lastEditTime = Clock.System.now().toString()
    }

    override fun toString(): String {
        return "Name: $name\n" +
                "Surname: $surname\n" +
                "Birth date: $birthDate\n" +
                "Gender: $gender\n" +
                "Number: $number\n" +
                "Time created: $timeCreated\n" +
                "Time last edit: $lastEditTime"
    }


}
