package minesweeper

import kotlin.random.Random

enum class Cells(val symbol: Char) {
    MINE('X'),
    MARKED('*'),
    SAFE('.')

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

    fun inputCoordinates() {
        println("Set/delete mine marks (x and y coordinates): ")
        val coordinates = readln().split(" ").map { it.toInt() }
        x = coordinates[0] - 1
        y = coordinates[1] - 1

        if (fieldOpenMines[x][y] == Cells.MINE.symbol) {
            fieldOpenMines[x][y] = Cells.MARKED.symbol
            fieldHiddenMines[x][y] = Cells.MARKED.symbol
        }
    }

    printField(fieldHiddenMines)

    while (fieldOpenMines.joinToString("").contains(Cells.MINE.symbol)) {

        inputCoordinates()
        if (fieldOpenMines[x][y].toString().toInt() in 0..9) {
            println("There is a number here!")
            inputCoordinates()
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
