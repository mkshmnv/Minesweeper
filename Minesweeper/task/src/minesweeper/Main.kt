package minesweeper

import kotlin.random.Random

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

fun Char.repeat(count: Int): String = this.toString().repeat(count)

fun main() {
    val width = 9
    val height = 9

    print("How many mines do you want on the field? ")
    val numberMines = readln().toInt()

    // Initialize with open mines field
    val fieldOpen = (Cells.EXPLORED.symbol.repeat(width * height - numberMines) +
            Cells.MINE.symbol.repeat(numberMines))
        .toList().shuffled()
        .chunked(width)
        .map { it.toMutableList() }

    // Initialize with hidden mines field
    val fieldHidden = Cells.UNEXPLORED.symbol.repeat(width * height)
        .chunked(width)
        .map { it.toMutableList() }


    printField(fieldOpen)

    // Calculate the number of mines around each empty cell
    for (row in fieldOpen.indices) {
        for (col in fieldOpen[row].indices) {

            if (fieldOpen[row][col] == Cells.EXPLORED.symbol) {

                var minesCount = 0

                for (i in -1..1) {
                    for (j in -1..1) {

                        val r = row + i
                        val c = col + j

                        if (r in 0 until height && c in 0 until width && fieldOpen[r][c] == Cells.MINE.symbol) {
                            minesCount++
                        }
                    }
                }
                if (minesCount > 0) {
                    fieldOpen[row][col] = '0' + minesCount
                }
            }
        }
    }

    // Start game, player enters two numbers as coordinates on the field
    game(fieldOpen, fieldHidden)

    println("Congratulations! You found all the mines!")
}

fun game(fieldOpenMines: List<MutableList<Char>>, fieldHiddenMines: List<MutableList<Char>>) {
    var x = 0
    var y = 0
    var mark = ""

    fun inputCoordinatesAndState() {
        print("Set/unset mine marks or claim a cell as free: ")
        val nextMove = readln().split(" ")

        x = nextMove[0].toInt() - 1
        y = nextMove[1].toInt() - 1
        mark = nextMove[2]
    }

    printField(fieldHiddenMines)

    while (fieldOpenMines.joinToString("") { it.joinToString("") }.contains(Cells.MINE.symbol)) {

        inputCoordinatesAndState()
        when (mark) {
            Mark.FREE.command -> {
                when {
                    fieldOpenMines[x][y].isDigit() -> fieldOpenMines[x][y] = fieldHiddenMines[x][y]
                    fieldOpenMines[x][y] == Cells.SAFE.symbol -> fieldOpenMines[x][y] = fieldHiddenMines[x][y]
                    fieldOpenMines[x][y] == Cells.MINE.symbol -> {
                        // TODO game over!
                    }
                }

            }
            Mark.MINE.command -> {
                when {
                    fieldOpenMines[x][y].isDigit() -> {
                        // TODO when marked mine to number
                    }
                    fieldOpenMines[x][y] == Cells.MINE.symbol -> {
                        fieldOpenMines[x][y] = Cells.MARKED.symbol
                        fieldHiddenMines[x][y] = Cells.MARKED.symbol
                    }
                }
            }
        }

        if (fieldOpenMines[x][y] == Cells.MINE.symbol) {
            fieldOpenMines[x][y] = Cells.MARKED.symbol
            fieldHiddenMines[x][y] = Cells.MARKED.symbol
        }
        printField(fieldHiddenMines)
    }
}

fun printField(field: List<List<Char>>) {
    println(
        """
        
         │123456789│
        —│—————————│
    """.trimIndent()
    )

    field.forEachIndexed { index, row -> println("${index + 1}│${row.joinToString("")}│") }

    println("—│—————————│")
}
