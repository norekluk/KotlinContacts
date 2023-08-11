package contacts

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.datetime.Clock
import java.io.File

fun main(args: Array<String>) {
    val phoneBook = PhoneBook(args)
}





class PhoneBook(vararg: Array<String>) {
    private val records = mutableListOf<Record>()
    private var file = File("")
    private val moshi = Moshi.Builder().add(
        PolymorphicJsonAdapterFactory.of(Record::class.java, "type")
            .withSubtype(Person::class.java, Person::class.java.canonicalName).withSubtype(Organization::class.java,
                Organization::class.java.canonicalName
            )
    ).add(KotlinJsonAdapterFactory()).build()
    private val type = Types.newParameterizedType(MutableList::class.java, Record::class.java)
    private val recordListAdapter = moshi.adapter<MutableList<Record>>(type)

    init {
        if (vararg.isNotEmpty()) {
            file = File(vararg[0])
            importFromFile(file)
            println("open ${vararg[0]}")

        }
        while (true) {
            print("[menu] Enter action (add, list, search, count, exit): ")
            when (val action = readln()) {
                "add" -> add()
                "list" -> listAll()
                "search" -> search()
                "count" -> count()
                "info" -> info()
                "exit" -> {
                    exportToFile(file)
                    println()
                    break
                }

                else -> println("Unknown action: $action")
            }
            println()
        }

    }

    private fun importFromFile(file: File) {
        val import = recordListAdapter.fromJson(file.readText())
        //val import = Json.decodeFromString<MutableList<Record>>(file.readText())
        records.addAll(import!!)
    }

    private fun exportToFile(file: File) {
        // file.writeText(Json.encodeToString(records))
    }

    private fun add() {
        print("Enter the type (person, organization): ")
        when (readln()) {
            "person" -> records.add(Person())
            "organization" -> records.add(Organization())
        }
        println("The record added.")
        exportToFile(file)
    }


    private fun remove(index: Int) {
        if (records.isEmpty()) {
            println("No records to remove!")
        } else {
            records.removeAt(index)
            println("The record removed")
        }
        exportToFile(file)
    }

    private fun edit(index: Int) {
        if (records.isEmpty()) {
            println("No records to edit!")
        } else {
            records[index].edit()
            println("Saved")
            println(records[index])
            exportToFile(file)
        }
    }

    private fun count() {
        println("The Phone Book has ${records.size} records.")
    }

    private fun info() {
        listAll()
        print("Enter index to show info: ")
        println(records[readln().toInt() - 1])
    }

    private fun listAll() {
        list(records)
        print("[search] Enter action ([number], back): ")
        when (val input = readln()) {
            "back" -> return
            else -> {
                val inputNumber = input.toIntOrNull()
                if (inputNumber != null) {
                    println(records[inputNumber - 1])
                    println()
                    recordMenu(inputNumber - 1)
                }
            }
        }
    }

    private fun list(list: List<Record>) {
        list.forEachIndexed { index, record -> println("${index + 1}. ${record.wholeName}") }
    }

    private fun search() {
        print("Enter search query: ")
        val query = readln()
        val result = records.mapIndexed { index, record -> index to record }.toMap()
            .filterValues { it.contains(query) }
        println("Found ${result.size} results:")
        println()
        list(result.values.toList())
        print("[search] Enter action ([number], back, again): ")
        when (val input = readln()) {
            "back" -> return
            "again" -> search()
            else -> {
                val inputNumber = input.toIntOrNull()
                if (inputNumber != null) {
                    val winnerIndex = result.keys.elementAt(inputNumber - 1)
                    println(records[winnerIndex])
                    println()
                    recordMenu(winnerIndex)
                } else {
                    println("No such action: $input")
                }
            }
        }
    }

    private fun recordMenu(index: Int) {
        println("[record] Enter action (edit, delete, menu): ")
        when (val action = readln()) {
            "edit" -> edit(index)
            "delete" -> remove(index)
            "menu" -> return
            else -> println("Unknown action: $action")
        }
    }
}

sealed class Record {
    private val defaultValue = "[no number]"
    private val numberRegex =
        """(?:(?:\+?[a-zA-Z0-9]+[ -]\([a-zA-Z0-9]{2,}\))|(?:\+?\([a-zA-Z0-9]+\)[ -][a-zA-Z0-9]{2,})|(?:\+?[a-zA-Z0-9]+[ -][a-zA-Z0-9]{2,})|\+?[a-zA-Z0-9]+|\+?\([a-zA-Z0-9]+\))(?:[ -][a-zA-Z0-9]{2,})*"""
    var wholeName: String = "Record"
    var number: String = defaultValue
        set(value) {
            field = if (checkNumberFormat(value)) {
                value
            } else {
                defaultValue
            }
        }
    open val timeCreated = Clock.System.now().toString()
    open var lastEditTime = Clock.System.now().toString()

    constructor(wholeName: String, number: String) {
        this.wholeName = wholeName
        this.number = number
    }

    private fun checkNumberFormat(number: String): Boolean {
        return if (numberRegex.toRegex().matches(number)) {
            true
        } else {
            println("Wrong number format!")
            false
        }
    }

    open fun edit() {
        println("Record has nothing to edit")
    }

    override fun toString(): String {
        return wholeName
    }

    open fun allValues(): String {
        return "$wholeName $number".lowercase()
    }

    fun contains(query: String): Boolean {
        return allValues().contains(query.toRegex())
    }
}

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
