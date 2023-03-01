package minesweeper

import kotlin.system.exitProcess

class Field {
    enum class Cells(val symbol: Char) {
        UNEXPLORED('.'),
        FREE('/'),
        MINE('X'),
        MARKED('*')
    }

    // Set parameters field
    private var width: Int = 9
    private var height: Int = 9
    private var numbersMines: Int = 0

    // Coordinates cells
    private var x: Int = 0
    private var y: Int = 0

    // Inner fields
    private val fieldInternal: List<MutableList<Char>>
    private val fieldExternal: List<MutableList<Char>>

    init {
        // Set numbers of mines
        startGame()

        // Initialize field with open mines
        fieldInternal = initField(false)

        // Initialize field with hidden mines
        fieldExternal = initField(true)
    }

    // Creating fields with mines
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
                    (Cells.FREE.symbol.repeat(width * height - numbersMines) + Cells.MINE.symbol.repeat(numbersMines)) // Create string with needed qty chars, with mines and explored marked cells
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

    private fun startGame() {
        print("How many mines do you want on the field? ")
        numbersMines = readln().toIntOrNull() ?: (width * height)
        if (numbersMines > width * height || numbersMines < 1) startGame()
    }
    fun makeMove() {
        print("Set/unset mine marks or claim a cell as free: ")
        val (xInput, yInput, action) = readln().split(" ")

        x = yInput.toInt() + 1
        y = xInput.toInt() + 1

        when (action) {
            // When command is free
            "free" -> {
                when (fieldInternal[x][y]) {
                    // Stepped on a marked cell
                    Cells.MARKED.symbol -> makeMove()

                    // Stepped on a digit
                    in "12345678" -> {
                        // Digit already open
                        if (fieldExternal[x][y].isDigit()) makeMove()

                        // Digit isn't open
                        fieldExternal[x][y] = fieldInternal[x][y]
                    }

                    // Stepped on a free cell
                    Cells.FREE.symbol -> {
                        // If free cell already explored
                        if (fieldExternal[x][y] == Cells.FREE.symbol) makeMove()

                        // Open all cells around
                        fieldExternal[x][y] = fieldInternal[x][y]
                        openCells(x,y)

                        printField(false)
                    }

                    // Stepped on a mine (game over)
                    Cells.MINE.symbol -> {

                        // Show all mines on field
                        for ((xRow, row) in fieldInternal.withIndex()) {
                            for ((yCol, cell) in row.withIndex()) {
                                if (cell == Cells.MINE.symbol) fieldExternal[xRow][yCol] = Cells.MINE.symbol
                            }
                        }

                        printField(false)

                        println("You stepped on a mine and failed!")
                        exitProcess(0)
                    }
                }
            } // When command is free

            // Command set or unset mines marks
            "mine" -> {
                when (fieldExternal[x][y]) {
                    // Stepped on a digit
                    in "12345678" -> {
                        println("There is a number here!")
                        makeMove()
                    }

                    // Set cell
                    Cells.UNEXPLORED.symbol -> {
                        fieldExternal[x][y] = Cells.MARKED.symbol
                        if (fieldInternal[x][y] == Cells.MINE.symbol) fieldInternal[x][y] = Cells.MARKED.symbol
                    }

                    // Unset cell
                    Cells.MARKED.symbol -> {
                        fieldExternal[x][y] = Cells.UNEXPLORED.symbol
                        if (fieldInternal[x][y] == Cells.MARKED.symbol) fieldInternal[x][y] = Cells.MINE.symbol
                    }
                }
            } // Command set or unset mines marks

            // Unknown command
            else -> makeMove()
        }
    }

    // Chosen free cell open all cells around
    private fun openCells(x: Int, y: Int) {
        // Init cells around chosen cell
        val north = x - 1
        val south = x + 1
        val east = y + 1
        val west = y - 1

        // Around cells have safe cells? Open them all
        fun repeatOpenCells (x: Int, y: Int) {
            if (fieldInternal[x][y] == Cells.MINE.symbol ||
                fieldExternal[x][y] == Cells.FREE.symbol ||
                fieldExternal[x][y].isDigit()
                ) return

            fieldExternal[x][y] = fieldInternal[x][y]

            if (fieldInternal[x][y] == Cells.FREE.symbol) openCells(x,y)
        }

        repeatOpenCells(x, east)
        repeatOpenCells(north, east)
        repeatOpenCells(north, y)
        repeatOpenCells(south, y)
        repeatOpenCells(south, east)
        repeatOpenCells(x, west)
        repeatOpenCells(north, west)
        repeatOpenCells(south, west)
    } // Chosen free cell open all cells around

    // Show field
    fun printField(internal: Boolean) {
        println("")
        if (internal) {
            fieldInternal.forEachIndexed { _, row -> println(row.joinToString("")) }
        } else {
            fieldExternal.forEachIndexed { _, row -> println(row.joinToString("")) }
        }
    } // Show field

    fun continueGame(): Boolean {
        // If even one mine is unmarked TODO fix continue
        if (fieldExternal.joinToString { it.joinToString("") }.count { it == Cells.MARKED.symbol } == numbersMines) {
            for ((rowIndex, row) in fieldInternal.withIndex()) {
                for (colIndex in row.indices) {
                    return if (fieldExternal[rowIndex][colIndex] == Cells.MARKED.symbol &&
                        fieldInternal[rowIndex][colIndex] != Cells.MARKED.symbol) {
                        true
                    } else {
                        printField(false)
                        println("Congratulations! You found all the mines!")
                        false
                    }
                }
            }
        }
        return true
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
