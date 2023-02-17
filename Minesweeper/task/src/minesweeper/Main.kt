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
    val originalField = List(height) { CharArray(width) { Cells.SAFE.symbol } }
    val hidedField = List(height) { CharArray(width) { Cells.SAFE.symbol } }


    print("How many mines do you want on the field? ")
    val qtyMines = readln().toInt()

    // Initialize the field with mines
    repeat(qtyMines) {
        var row: Int
        var col: Int
        do {
            row = Random.nextInt(height)
            col = Random.nextInt(width)
        } while (originalField[row][col] == Cells.MINE.symbol)
        originalField[row][col] = Cells.MINE.symbol
    }

    // Calculate the number of mines around each empty cell
    for (row in originalField.indices) {
        for (col in originalField[row].indices) {
            if (originalField[row][col] == Cells.SAFE.symbol) {
                var minesCount = 0
                for (i in -1..1) {
                    for (j in -1..1) {
                        val r = row + i
                        val c = col + j
                        if (r in 0 until height && c in 0 until width && originalField[r][c] == Cells.MINE.symbol) {
                            minesCount++
                        }
                    }
                }
                if (minesCount > 0) {
                    originalField[row][col] = '0' + minesCount
                }
            }
        }
    }

    // Initialize the field with mines
    for ((indexRow, row) in originalField.withIndex()) {
        for ((indexChar, c) in row.withIndex()) {
            if (c == 'X') {
                hidedField[indexRow][indexChar] = Cells.SAFE.symbol
            } else {
                hidedField[indexRow][indexChar] = originalField[indexRow][indexChar]
            }
        }
    }



//
//    println("originalField")
//    printField(originalField)
//    println("hidedField")
//    printField(hidedField)




    while (originalField.joinToString {it.joinToString("")}.contains(Cells.MINE.symbol) ) {
        // Print the field
        printField(hidedField)
//        minesMarks(originalField, hidedField)
        println("Set/delete mine marks (x and y coordinates): ")
        val (x, y) = readln().split(" ").map { it.toInt() }
    }

}

fun minesMarks(originalField: List<CharArray>): List<CharArray> {
//    val coordinates = mutableListOf<Pair<Int, Int>>()
//
//    for ((indexRow, row) in originalField.withIndex()) {
//        for ((indexChar, c) in row.withIndex()) {
//            if (c == 'X') {
//                coordinates.add(Pair(indexRow + 1, indexChar + 1))
//            }
//        }
//    }

    println("Set/delete mine marks (x and y coordinates): ")
    val (x, y) = readln().split(" ").map { it.toInt() }
//
//    if (coordinates.contains(Pair(x, y))) {
//        originalField[x][y] = Cells.MARKED.symbol
//        coordinates.remove(Pair(x, y))
//    }

    return originalField
}

fun printField(originalField: List<CharArray>) {
    println("""
        
         │123456789│
        —│—————————│
    """.trimIndent())

    originalField.forEachIndexed { index, row -> println("${index + 1}│${row.joinToString("")}│") }

    println("—│—————————│")
}
