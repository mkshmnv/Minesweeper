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

class Field(private val qtyMines: Int = 0, private val width: Int = 9, private val height: Int = 9) {

    // coordinates
    private var x = 0
    private var y = 0
    private lateinit var mark: Mark

    private fun Char.repeat(count: Int): String = this.toString().repeat(count)

    // Initialize field with open mines
    val open = initField()

    private fun initField(): List<MutableList<Char>> {
        val field = (Cells.EXPLORED.symbol.repeat(width * height - qtyMines) + Cells.MINE.symbol.repeat(qtyMines)) // Create string with needed qty chars, with mines and explored marked cells
            .toList().shuffled() // shuffled chars
            .chunked(width) // split string to lists
            .map { it.toMutableList() }

        // Calculate the number of mines around each empty cell
        for (row in field.indices)
        {
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
        return field
    }

    // Initialize field with hidden mines
    private val hidden = Cells.UNEXPLORED.symbol.repeat(width * height) // Create string with needed qty unexplored cells
        .chunked(width) // split string to lists
        .map { it.toMutableList() }

    fun makeMove() {
        print("Set/unset mine marks or claim a cell as free: ")
        val splitInput = readln().split(" ")

        x = splitInput[0].toInt()
        y = splitInput[1].toInt()

        // TODO implement all turns varies --- separate code to makeMove and update field
        when (splitInput[2]) {
            Mark.FREE.command -> {
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
            Mark.MINE.command -> {
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
}



fun main() {
    print("How many mines do you want on the field? ")
    val mines = readln().toInt() // TODO fix if (NULL)

    // Initialize field
    val field = Field(mines)

    // Start game, player enters two numbers as coordinates on the field
    field.printField()

    while (field.open.joinToString("") { it.joinToString("") }.contains(Cells.MINE.symbol)) {
        field.makeMove()

        field.printField()
    }

    println("Congratulations! You found all the mines!")
}
