package minesweeper

enum class Cells(val cell: Char) {
    MINE('X'),
    SAFE('.')
}

fun main() {
    print("How many mines do you want on the field? ")
    val numberOfMines = readln().toInt()
    createMinefield(9, 9, numberOfMines)
}


private fun createMinefield(fieldHorizontal: Int, fieldVertical: Int, qtyMines: Int) {
    val safeCells = List(fieldHorizontal * fieldVertical - qtyMines) { Cells.SAFE.cell }
    val minesCells = List(qtyMines) { Cells.MINE.cell }
    val minesField = (safeCells + minesCells).shuffled()
    minesField.forEachIndexed { index, c -> if ((index + 1) % fieldHorizontal != 0) print(c) else println(c) }
}
