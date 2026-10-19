package dev.luis.fulfillment

import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDate
import java.time.format.DateTimeParseException

object Tsv {
    fun orders(path: Path): List<Order> = rows(path, listOf("id", "sku", "quantity", "priority", "deadline")).map { (line, cells) ->
        try {
            Order(cells[0], cells[1], cells[2].toInt(), cells[3].toInt(), LocalDate.parse(cells[4]))
        } catch (error: NumberFormatException) { throw IllegalArgumentException("${path.fileName}: line $line has an invalid number", error)
        } catch (error: DateTimeParseException) { throw IllegalArgumentException("${path.fileName}: line $line has an invalid date", error) }
    }

    fun stock(path: Path): List<Stock> = rows(path, listOf("sku", "available")).map { (line, cells) ->
        try { Stock(cells[0], cells[1].toInt()) }
        catch (error: NumberFormatException) { throw IllegalArgumentException("${path.fileName}: line $line has an invalid quantity", error) }
    }

    private fun rows(path: Path, header: List<String>): List<Pair<Int, List<String>>> {
        val lines = Files.readAllLines(path)
        require(lines.isNotEmpty() && lines.first().trimEnd('\r').split('\t') == header) { "${path.fileName}: invalid header" }
        return lines.drop(1).mapIndexedNotNull { index, raw ->
            if (raw.isBlank()) null else {
                val cells = raw.trimEnd('\r').split('\t')
                require(cells.size == header.size) { "${path.fileName}: line ${index + 2} has ${cells.size} columns, expected ${header.size}" }
                (index + 2) to cells
            }
        }
    }
}
