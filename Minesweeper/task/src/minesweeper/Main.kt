package minesweeper

import kotlin.system.exitProcess

enum class Cells(val symbol: Char) {
    UNEXPLORED('.'),
    FREE('/'),
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
    private var y: Int = 0
    private var x: Int = 0
    private lateinit var mark: Mark

    // Inner fields
    // TODO implement fieldInternal: List<List<Char>>
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

    private fun initField(isHidden: Boolean): List<MutableList<Char>> {
        fun Char.repeat(count: Int): String = this.toString().repeat(count)

        var firstString = " │"
        var lastString = "—│"

        val resultField: MutableList<MutableList<Char>> = when (isHidden) {
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
        val splitInput = readln().split(" ")

        x = splitInput[0].toInt() + 1
        y = splitInput[1].toInt() + 1

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
                    fieldInternal[y][x] == Cells.MINE.symbol -> {

                        fieldInternal.forEachIndexed { indexRow, row ->
                            row.forEachIndexed { indexCell, cell ->
                                if (cell == Cells.MINE.symbol) {
                                    fieldExternal[indexRow][indexCell] = Cells.MINE.symbol
                                }
                            }
                        }

                        printField()

                        println("You stepped on a mine and failed!")

                        exitProcess(0)
                    }

                    // If stepped on a marked cell
                    fieldInternal[y][x] == Cells.MARKED.symbol -> {
                        makeMove()
                    }

                    // If stepped on a free cell
                    fieldInternal[y][x] == Cells.FREE.symbol -> {
                        if (fieldExternal[y][x] == Cells.FREE.symbol) {
                            // If free cell already explored
                            makeMove()
                        } else {
                            // If free cell is unexplored
                            // TODO open all around cells

                            var isAllOpened = true

                            fun openAroundCell(col: Int, row: Int) {
                                for (i in -1..1) {
                                    for (j in -1..1) {
                                        val c = col - i
                                        val r = row - j
                                        if (fieldInternal[r][c] == Cells.FREE.symbol) {
                                            if (fieldExternal[r][c] == '@' ) {
                                                fieldExternal[r][c] = fieldInternal[r][c]
                                            } else {
                                                fieldExternal[r][c] = '@'
                                            }
                                        } else {
                                            fieldExternal[r][c] = fieldInternal[r][c]
                                        }
                                    }
                                }
                            }

                            while (fieldInternal.joinToString("") { it.joinToString("") }.contains('@')) {
                                for ((indexRow, row) in fieldExternal.withIndex()) {
                                    for ((indexCol, symbol) in row.withIndex()) {
                                        if (symbol == Cells.FREE.symbol) {
                                            openAroundCell(indexCol, indexRow)
                                        }
                                    }
                                }
                            }

                            openAroundCell(x, y)



                            for ((indexRow, row) in fieldExternal.withIndex()) {
                                for ((indexCol, symbol) in row.withIndex()) {
                                    if (symbol == Cells.FREE.symbol) {
                                        openAroundCell(indexCol, indexRow)
                                    }
                                }
                            }


//
//                            fieldExternal[y][x] = fieldInternal[y][x]
//
//                            fun clickOnCell(col: Int, row: Int) {
//                                for (i in -1..1) {
//                                    for (j in -1..1) {
//                                        val c = col - i
//                                        val r = row - j
//                                        fieldExternal[r][c] = fieldInternal[r][c]
//                                    }
//                                }
//                            }
//
//                            fun click() {
//
//                                for ((indexRow, row) in fieldExternal.withIndex()) {
//                                    for ((indexCol, symbol) in row.withIndex()) {
//                                        if (symbol == Cells.FREE.symbol) {
//                                            clickOnCell(indexCol, indexRow)
//                                        }
//                                    }
//                                }
//
//                            }
//
//                            click()


                            printField()
                        }
                    }

                    // If cell is a digit
                    fieldInternal[y][x].isDigit() -> {
                        if (fieldExternal[y][x].isDigit()) {
                            // If digit already open
                            makeMove()
                        } else {
                            // If digit isn't open
                            fieldExternal[y][x] = fieldInternal[y][x]
                        }
                    }
                }
            }

            // When command set or unset mines marks
            Mark.MINE -> {
                if (fieldExternal[y][x] == Cells.UNEXPLORED.symbol) {
                    fieldExternal[y][x] = Cells.MARKED.symbol

                    if (fieldInternal[y][x] == Cells.MINE.symbol) {
                        fieldInternal[y][x] = Cells.MARKED.symbol
                    }
                } else if (fieldExternal[y][x] == Cells.MARKED.symbol) {
                    fieldExternal[y][x] = Cells.UNEXPLORED.symbol

                    if (fieldInternal[y][x] == Cells.MARKED.symbol) {
                        fieldInternal[y][x] = Cells.MINE.symbol
                    }
                }
            }
        }
    }

    fun printField() {
        println("")
        fieldExternal.forEachIndexed { index, row -> println(row.joinToString("")) }
    }

    // TODO DELETE FOR TESTS
    fun printInternalField() {
        println("")
        fieldInternal.forEachIndexed { index, row -> println(row.joinToString("")) }
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
        field.printField()
        field.printInternalField()
        field.makeMove()
    } while (field.continueGame())

}
