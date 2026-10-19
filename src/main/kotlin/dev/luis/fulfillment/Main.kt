package dev.luis.fulfillment

import java.nio.file.Files
import java.nio.file.Path

fun main(args: Array<String>) {
    require(args.size == 3) { "Usage: <orders.tsv> <stock.tsv> <report.html>" }
    val plan = Planner().calculate(Tsv.orders(Path.of(args[0])), Tsv.stock(Path.of(args[1])))
    val output = Path.of(args[2]); output.parent?.let { Files.createDirectories(it) }
    Files.writeString(output, Report.html(plan))
    println("${plan.allocatedCount} orders allocated, ${plan.backlogCount} in backlog, ${plan.allocatedUnits} units reserved. Report: $output")
}
