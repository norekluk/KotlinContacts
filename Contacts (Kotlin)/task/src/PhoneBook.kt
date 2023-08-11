package contacts

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.datetime.Clock
import java.io.File

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
