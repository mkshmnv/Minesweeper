package minesweeper

import kotlin.random.Random

enum class Cells(val symbol: Char) {
    MINE('X'),
    MARKED('*'),
    SAFE('.')
}

enum class Command(val mark: String) {
    MINE("mine"),
    FREE("free")
}

fun main() {

    val width = 9
    val height = 9
    val fieldOpenMines = List(height) { CharArray(width) { Cells.SAFE.symbol } }
    val fieldHiddenMines = List(height) { CharArray(width) { Cells.SAFE.symbol } }

    print("How many mines do you want on the field? ")
    val numberMines = readln().toInt()

    // Initialize the field with open mines
    repeat(numberMines) {
        var row: Int
        var col: Int

        do {
            row = Random.nextInt(height)
            col = Random.nextInt(width)
        } while (fieldOpenMines[row][col] == Cells.MINE.symbol)
        fieldOpenMines[row][col] = Cells.MINE.symbol
    }

    // Calculate the number of mines around each empty cell
    for (row in fieldOpenMines.indices) {
        for (col in fieldOpenMines[row].indices) {
            if (fieldOpenMines[row][col] == Cells.SAFE.symbol) {
                var minesCount = 0
                for (i in -1..1) {
                    for (j in -1..1) {
                        val r = row + i
                        val c = col + j
                        if (r in 0 until height && c in 0 until width && fieldOpenMines[r][c] == Cells.MINE.symbol) {
                            minesCount++
                        }
                    }
                }
                if (minesCount > 0) {
                    fieldOpenMines[row][col] = '0' + minesCount
                }
            }
        }
    }

    // Initialize the field with hidden mines
    for ((indexRow, row) in fieldOpenMines.withIndex()) {
        for ((indexChar, c) in row.withIndex()) {
            if (c == 'X') {
                fieldHiddenMines[indexRow][indexChar] = Cells.SAFE.symbol
            } else {
                fieldHiddenMines[indexRow][indexChar] = fieldOpenMines[indexRow][indexChar]
            }
        }
    }

    // Start game, player enters two numbers as coordinates on the field
    game(fieldOpenMines, fieldHiddenMines)

    println("Congratulations! You found all the mines!")
}

fun game(fieldOpenMines: List<CharArray>, fieldHiddenMines: List<CharArray>) {
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

//    val a = fieldOpenMines.joinToString("") { it.joinToString("") }.contains(Cells.MINE.symbol)

    while (fieldOpenMines.joinToString("") { it.joinToString("") }.contains(Cells.MINE.symbol)) {

        inputCoordinatesAndState()
        when (mark) {
            Command.FREE.mark -> {
                when {
                    fieldOpenMines[x][y].isDigit() -> fieldOpenMines[x][y] = fieldHiddenMines[x][y]
                    fieldOpenMines[x][y] == Cells.MINE.symbol -> {
                        // TODO game over!
                    }
                }

            }
            Command.MINE.mark -> {
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

fun printField(field: List<CharArray>) {
    println(
        """
        
         │123456789│
        —│—————————│
    """.trimIndent()
    )

    field.forEachIndexed { index, row -> println("${index + 1}│${row.joinToString("")}│") }

    println("—│—————————│")
}
