package minesweeper

import kotlin.system.exitProcess

enum class Cells(val symbol: Char) {
    UNEXPLORED('.'),
    FREE('/'),
    MINE('X'),
    MARKED('*')
}

class Field {

    // Parameters field
    private var qtyMines: Int
    private var width: Int
    private var height: Int

    // Coordinates
    private var x: Int = 0
    private var y: Int = 0

    // Inner fields
    // TODO implement fieldInternal - could not change the field  -> List<List<Char>>
    private val fieldInternal: List<MutableList<Char>>
    private val fieldExternal: List<MutableList<Char>>

    init {
        print("How many mines do you want on the field? ")
        // TODO fix if (NULL)
        // TODO fix if qty > cells on field
        qtyMines = readln().toInt()

        // Set field size
        width = 9
        height = 9

        // Initialize field with open mines
        fieldInternal = initField(false)

        // Initialize field with hidden mines
        fieldExternal = initField(true)
    }

    private fun initField(internal: Boolean): List<MutableList<Char>> {
        fun Char.repeat(count: Int): String = this.toString().repeat(count)

        var firstString = " │"
        var lastString = "—│"

        val resultField: MutableList<MutableList<Char>> = when (internal) {
            true -> {
                val field =
                    Cells.UNEXPLORED.symbol.repeat(width * height) // Create string with needed qty unexplored cells
                        .chunked(width) // split string to lists
                        .map { it.toMutableList() }.toMutableList()

                for ((index, row) in field.withIndex()) {
                    row.add(0, '1' + index)
                    row.add(1, '│')
                    row.add('│')
                }

                field
            }

            false -> {
                val field =
                    (Cells.FREE.symbol.repeat(width * height - qtyMines) + Cells.MINE.symbol.repeat(qtyMines)) // Create string with needed qty chars, with mines and explored marked cells
                        .toList().shuffled() // shuffled chars
                        .chunked(width) // split string to lists
                        .map { it.toMutableList() }.toMutableList()

                // Calculate the number of mines around each empty cell
                for (row in field.indices) {
                    for (col in field[row].indices) {

                        if (field[row][col] == Cells.FREE.symbol) {

                            var minesCount = 0

                            for (i in -1..1) {
                                for (j in -1..1) {

                                    val r = row + i
                                    val c = col + j

                                    if (r in 0 until height && c in 0 until width && field[r][c] == Cells.MINE.symbol) {
                                        minesCount++
                                    }
                                }
                            }
                            if (minesCount > 0) field[row][col] = '0' + minesCount
                        }
                    }
                }

                for ((index, row) in field.withIndex()) {
                    row.add(0, '1' + index)
                    row.add(1, '│')
                    row.add('│')
                }

                field
            }
        }

        (1..width).forEach { firstString += it.toString() }

        (1..width).forEach { lastString += "—" }

        resultField.add(0, ("$firstString│").toMutableList())
        resultField.add(1, ("$lastString│").toMutableList())
        resultField.add(("$lastString│").toMutableList())
        return resultField
    }

    fun makeMove() {
        print("Set/unset mine marks or claim a cell as free: ")
        val (xString, yString, toDo) = readln().split(" ")

        x = yString.toInt() + 1
        y = xString.toInt() + 1

        // TODO delete Mark enum class
        when (toDo) {
            "free" -> {

                // Stepped on a mine
                if (fieldInternal[x][y] == Cells.MINE.symbol) {
                    printField(false)
                    println("You stepped on a mine and failed!")
                    exitProcess(0)
                }

                // Stepped on a marked cell
                if (fieldInternal[x][y] == Cells.MARKED.symbol) {
                    makeMove()
                }

                // If cell is a digit
                if (fieldInternal[x][y].isDigit()) {
                    if (fieldExternal[x][y].isDigit()) {
                        // If digit already open
                        makeMove()
                    } else {
                        // If digit isn't open
                        fieldExternal[x][y] = fieldInternal[x][y]
                    }
                }

                // Stepped on a free cell
                if (fieldInternal[x][y] == Cells.FREE.symbol) {
                    if (fieldExternal[x][y] == Cells.FREE.symbol) {
                        // If free cell already explored
                        makeMove()
                    } else {
                        // TODO open free cells implement fun openCells()
//                        openCells(x, y)
                    }
                    printField(false)
                }

//                done = gameOverAllMarked() || gameOverAllShown()

            } // When command is free

            "mine" -> {
                when (fieldInternal[x][y]) {
                    in "12345678" -> {
                        println("There is a number here!")
                        makeMove()
                    }

                    Cells.UNEXPLORED.symbol -> {
                        fieldExternal[x][y] = Cells.MARKED.symbol
                    }

                    Cells.MARKED.symbol -> {
                        fieldExternal[x][y] = Cells.UNEXPLORED.symbol
                    }
                }
            } // Command set or unset mines marks

            else -> makeMove() // Unknown command
        }
    }

    fun printField(internal: Boolean) {
        println("")
        if (internal) {
            fieldExternal.forEachIndexed { _, row -> println(row.joinToString("")) }
        } else {
            fieldInternal.forEachIndexed { _, row -> println(row.joinToString("")) }
        }

    }

    fun continueGame(): Boolean {

        val openFieldToString = fieldInternal.joinToString("") { it.joinToString("") }
        val hiddenFieldToString = fieldExternal.joinToString("") { it.joinToString("") }

        return if (openFieldToString.contains(Cells.MINE.symbol) ||
            openFieldToString.filter { it == Cells.UNEXPLORED.symbol } !=
            hiddenFieldToString.filter { it == Cells.MINE.symbol }
        ) {
            true
        } else {
            println("Congratulations! You found all the mines!")
            false
        }
    }
}

fun main() {
    // Initialize field
    val field = Field()

    // Start game where player enters two numbers as coordinates and command on the field
    do {
        field.printField(false)
        field.printField(true) // check internal field
        field.makeMove()
    } while (field.continueGame())

}
