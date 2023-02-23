package minesweeper

import kotlin.system.exitProcess

enum class Cells(val symbol: Char) {
    UNEXPLORED('.'),
    EXPLORED('/'),
    MINE('X'),
    MARKED('*')
}

enum class Mark {
    MINE,
    FREE
}

class Field {

    // Parameters field
    private var qtyMines: Int
    private var width: Int
    private var height: Int

    // Coordinates
    private var x: Int = 0
    private var y: Int = 0
    private lateinit var mark: Mark

    // Inner fields
    private val open: List<MutableList<Char>>
    private val hidden: List<MutableList<Char>>

    init {
        print("How many mines do you want on the field? ")
        // TODO fix if (NULL)
        // TODO fix if qty > cells on field
        qtyMines = readln().toInt()

        // Set field size
        width = 9
        height = 9

        // Initialize field with open mines
        open = initField(false)

        // Initialize field with hidden mines
        hidden = initField(true)
    }

    private fun initField(hidden: Boolean): List<MutableList<Char>> {
        fun Char.repeat(count: Int): String = this.toString().repeat(count)
        return when (hidden) {
            true -> {
                Cells.UNEXPLORED.symbol.repeat(width * height) // Create string with needed qty unexplored cells
                    .chunked(width) // split string to lists
                    .map { it.toMutableList() }
            }

            false -> {
                val field =
                    (Cells.EXPLORED.symbol.repeat(width * height - qtyMines) + Cells.MINE.symbol.repeat(qtyMines)) // Create string with needed qty chars, with mines and explored marked cells
                        .toList().shuffled() // shuffled chars
                        .chunked(width) // split string to lists
                        .map { it.toMutableList() }

                // Calculate the number of mines around each empty cell
                for (row in field.indices) {
                    for (col in field[row].indices) {

                        if (field[row][col] == Cells.EXPLORED.symbol) {

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
                field
            }
        }
    }

    fun makeMove() {
        print("Set/unset mine marks or claim a cell as free: ")
        val splitInput = readln().split(" ")

        x = splitInput[1].toInt() - 1
        y = splitInput[0].toInt() - 1

        if (splitInput[2] == "free") {
            mark = Mark.FREE
        } else if (splitInput[2] == "mine") {
            mark = Mark.MINE
        } else {
            makeMove()
        }

        when (mark) {
            // When command is free
            Mark.FREE -> {
                when {
                    // If stepped on a mine
                    open[x][y] == Cells.MINE.symbol -> {

                        open.forEachIndexed { indexRow, row ->
                            row.forEachIndexed { indexCell, cell ->
                                if (cell == Cells.MINE.symbol) {
                                    hidden[indexRow][indexCell] = Cells.MINE.symbol
                                }
                            }
                        }

                        printField()

                        println("You stepped on a mine and failed!")

                        exitProcess(0)
                    }

                    // If stepped on a marked cell
                    open[x][y] == Cells.MARKED.symbol -> {
                        makeMove()
                    }

                    // If stepped on a free cell
                    open[x][y] == Cells.EXPLORED.symbol -> {
                        if (hidden[x][y] == Cells.EXPLORED.symbol) {
                            // If free cell already explored
                            makeMove()
                        } else {
                            // If free cell is unexplored
                            // TODO open all around cells
                            hidden[x][y] = Cells.EXPLORED.symbol
                        }
                    }

                    // If cell is a digit
                    open[x][y].isDigit() -> {
                        if (hidden[x][y].isDigit()) {
                            // If digit already open
                            makeMove()
                        } else {
                            // If digit isn't open
                            hidden[x][y] = open[x][y]
                        }
                    }
                }
            }

            // When command set or unset mines marks
            Mark.MINE -> {
                if (hidden[x][y] == Cells.UNEXPLORED.symbol) {
                    hidden[x][y] = Cells.MARKED.symbol

                    if (open[x][y] == Cells.MINE.symbol) {
                        open[x][y] = Cells.MARKED.symbol
                    }
                } else if (hidden[x][y] == Cells.MARKED.symbol) {
                    hidden[x][y] = Cells.UNEXPLORED.symbol

                    if (open[x][y] == Cells.MARKED.symbol) {
                        open[x][y] = Cells.MINE.symbol
                    }
                }
            }
        }
    }

    fun printField() {
        println(
            """
        
         │123456789│
        —│—————————│
    """.trimIndent()
        )

        hidden.forEachIndexed { index, row -> println("${index + 1}│${row.joinToString("")}│") }

        println("—│—————————│")
    }

    // TODO DELETE FOR TESTS
    fun printOpenField() {
        println(
            """
        
         │123456789│
        —│—————————│
    """.trimIndent()
        )

        open.forEachIndexed { index, row -> println("${index + 1}│${row.joinToString("")}│") }

        println("—│—————————│")
    }

    fun continueGame(): Boolean {

        val openFieldToString = open.joinToString("") { it.joinToString("") }
        val hiddenFieldToString = hidden.joinToString("") { it.joinToString("") }

        return if (openFieldToString.contains(Cells.MINE.symbol) ||
            openFieldToString.filter { it == Cells.UNEXPLORED.symbol } !=
            hiddenFieldToString.filter { it == Cells.MINE.symbol }) {
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
        field.printField()
        field.printOpenField()
        field.makeMove()
    } while (field.continueGame())

}
