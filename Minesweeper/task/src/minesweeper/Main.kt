package minesweeper

import kotlin.random.Random

enum class Cells(val cell: Char) {
    MINE('X'),
    SAFE('.')
}

fun main() {

    print("How many mines do you want on the field? ")
    val width = 9
    val height = 9
    val minesCount = readln().toInt()

    val field = Array(height) { CharArray(width) { Cells.SAFE.cell } }

    // Initialize the field with mines
    repeat(minesCount) {
        var row: Int
        var col: Int
        do {
            row = Random.nextInt(height)
            col = Random.nextInt(width)
        } while (field[row][col] == Cells.MINE.cell)
        field[row][col] = Cells.MINE.cell
    }

    // Calculate the number of mines around each empty cell
    for (row in field.indices) {
        for (col in field[row].indices) {
            if (field[row][col] == Cells.SAFE.cell) {
                var minesCount = 0
                for (i in -1..1) {
                    for (j in -1..1) {
                        val r = row + i
                        val c = col + j
                        if (r in 0 until height && c in 0 until width && field[r][c] == Cells.MINE.cell) {
                            minesCount++
                        }
                    }
                }
                if (minesCount > 0) {
                    field[row][col] = '0' + minesCount
                }
            }
        }
    }

    // Print the field
    for (row in field) {
        println(row.joinToString(""))
    }
}
