package dev.luis.fulfillment

import java.nio.file.Files
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class PlannerTest {
    private val day = LocalDate.parse("2026-10-19")

    @Test fun `prioridade e prazo definem ordem sem vender estoque inexistente`() {
        val orders = listOf(
            Order("normal", "A", 3, 10, day),
            Order("urgente", "A", 4, 90, day.plusDays(1)),
            Order("segundo", "A", 2, 90, day)
        )
        val plan = Planner().calculate(orders, listOf(Stock("A", 6)))
        assertEquals(listOf("segundo", "urgente", "normal"), plan.decisions.map { it.order.id })
        assertEquals(listOf(Status.ALLOCATED, Status.ALLOCATED, Status.BACKLOG), plan.decisions.map { it.status })
        assertEquals(0, plan.remaining["A"])
        assertEquals(6, plan.allocatedUnits)
    }

    @Test fun `ids duplicados e estoque negativo são recusados`() {
        val order = Order("same", "A", 1, 10, day)
        assertFailsWith<IllegalArgumentException> { Planner().calculate(listOf(order, order), listOf(Stock("A", 2))) }
        assertFailsWith<IllegalArgumentException> { Planner().calculate(listOf(order), listOf(Stock("A", -1))) }
    }

    @Test fun `relatório escapa valores não confiáveis`() {
        val plan = Planner().calculate(listOf(Order("<script>", "A", 1, 1, day)), listOf(Stock("A", 1)))
        val html = Report.html(plan)
        assertTrue(html.contains("&lt;script&gt;"))
        assertTrue(!html.contains("<script>"))
    }

    @Test fun `tsv recusa cabeçalho incorreto com contexto`() {
        val path = Files.createTempFile("orders", ".tsv")
        try {
            Files.writeString(path, "wrong\theader\n")
            assertFailsWith<IllegalArgumentException> { Tsv.orders(path) }
        } finally { Files.deleteIfExists(path) }
    }
}
