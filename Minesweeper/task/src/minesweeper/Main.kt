package minesweeper

import kotlin.system.exitProcess

enum class Cells(val symbol: Char) {
    UNEXPLORED('.'),
    EXPLORED('/'),
    MINE('X'),
    MARKED('*')
}

enum class Mark(val command: String) {
    MINE("mine"),
    FREE("free")
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
        qtyMines = readln().toInt() // TODO fix if (NULL)

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

        x = splitInput[0].toInt()
        y = splitInput[1].toInt()


        if (splitInput[2] == "free") {
            mark = Mark.FREE
        } else if (splitInput[2] == "mine") {
            mark = Mark.MINE
        } else {
            makeMove()
        }

        // TODO implement all turns varies --- separate code to makeMove and update field
        when (mark) {
            Mark.FREE -> {
                mark = Mark.FREE

                when (open[x][y]) {
                    Cells.MINE.symbol -> {
                        println("You stepped on a mine and failed!")
                        exitProcess(0)
                    }

                    Cells.MARKED.symbol -> {}
                    Cells.EXPLORED.symbol -> {}
                    Cells.UNEXPLORED.symbol -> {}
                }
            }

            Mark.MINE -> {
                mark = Mark.MINE
                when (open[x][y]) {
                    Cells.MINE.symbol -> {
                        open[x][y] = Cells.MARKED.symbol
                        hidden[x][y] = Cells.MARKED.symbol
                    }

                    Cells.MARKED.symbol -> {}
                    Cells.EXPLORED.symbol -> {}
                    Cells.UNEXPLORED.symbol -> {}
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

        return openFieldToString.contains(Cells.MINE.symbol) ||
                openFieldToString.filter { it == Cells.UNEXPLORED.symbol } != hiddenFieldToString.filter { it == Cells.MINE.symbol }
    }
}


fun main() {
    // Initialize field
    val field = Field()

    // Start game, player enters two numbers as coordinates on the field
    do {
        field.printField()
        field.makeMove()
    } while (field.continueGame())

    println("Congratulations! You found all the mines!")
}
