package dev.luis.fulfillment

import java.time.LocalDate

data class Order(val id: String, val sku: String, val quantity: Int, val priority: Int, val deadline: LocalDate)
data class Stock(val sku: String, val available: Int)
data class Decision(val order: Order, val status: Status, val reason: String)
enum class Status { ALLOCATED, BACKLOG }
data class Plan(val decisions: List<Decision>, val remaining: Map<String, Int>) {
    val allocatedCount: Int get() = decisions.count { it.status == Status.ALLOCATED }
    val backlogCount: Int get() = decisions.size - allocatedCount
    val allocatedUnits: Int get() = decisions.filter { it.status == Status.ALLOCATED }.sumOf { it.order.quantity }
}

class Planner {
    fun calculate(orders: List<Order>, stock: List<Stock>): Plan {
        require(orders.map { it.id }.distinct().size == orders.size) { "Order IDs must be unique" }
        require(stock.map { it.sku }.distinct().size == stock.size) { "Stock SKUs must be unique" }
        require(orders.all { it.id.isNotBlank() && it.sku.isNotBlank() && it.quantity > 0 && it.priority in 0..100 }) { "Invalid order" }
        require(stock.all { it.sku.isNotBlank() && it.available >= 0 }) { "Invalid stock" }
        val remaining = stock.associate { it.sku to it.available }.toMutableMap()
        val sorted = orders.sortedWith(compareByDescending<Order> { it.priority }.thenBy { it.deadline }.thenBy { it.id })
        val decisions = sorted.map { order ->
            val available = remaining[order.sku] ?: 0
            if (available >= order.quantity) {
                remaining[order.sku] = available - order.quantity
                Decision(order, Status.ALLOCATED, "Reserva completa")
            } else {
                Decision(order, Status.BACKLOG, if (order.sku !in remaining) "SKU sem estoque cadastrado" else "Saldo insuficiente: $available unidade(s)")
            }
        }
        return Plan(decisions, remaining.toSortedMap())
    }
}
