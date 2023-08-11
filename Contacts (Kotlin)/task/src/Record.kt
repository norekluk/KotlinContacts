package contacts

import kotlinx.datetime.Clock

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



