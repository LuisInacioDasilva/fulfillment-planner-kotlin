package dev.luis.fulfillment

object Report {
    private fun escape(value: String): String = value.replace("&", "&amp;").replace("<", "&lt;")
        .replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;")

    fun html(plan: Plan): String = buildString {
        append("""<!doctype html><html lang="pt-BR"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Fulfillment Planner — Relatório</title><style>
            :root{--bg:#101322;--panel:#1a2034;--line:#343e56;--ink:#eef2ff;--muted:#a7b2c7;--violet:#bd9cff;--green:#9be6c3;--amber:#ffc781}*{box-sizing:border-box}body{margin:0;background:var(--bg);color:var(--ink);font-family:Inter,Segoe UI,Arial,sans-serif}.glow{position:fixed;inset:0;pointer-events:none;background:radial-gradient(circle at 80% 10%,#604ba344,transparent 32%),linear-gradient(#56608612 1px,transparent 1px),linear-gradient(90deg,#56608612 1px,transparent 1px);background-size:auto,38px 38px,38px 38px}.shell{position:relative;width:min(1160px,calc(100% - 44px));margin:auto}header{border-bottom:1px solid var(--line);height:80px;display:flex;align-items:center;justify-content:space-between}header strong{letter-spacing:.19em;font-size:15px}header span{font:700 10px monospace;letter-spacing:.16em;color:var(--violet)}.intro{padding:90px 0 65px}.eyebrow{font:700 11px monospace;letter-spacing:.2em;color:var(--violet)}h1{font-size:clamp(50px,7vw,85px);letter-spacing:-.07em;line-height:1;margin:20px 0}h1 em{font-style:normal;color:var(--violet)}p{color:var(--muted);line-height:1.7}.cards{display:grid;grid-template-columns:repeat(3,1fr);gap:12px}.cards article{border:1px solid var(--line);background:linear-gradient(135deg,#222b45,#171e31);padding:25px}.cards span{font:700 10px monospace;letter-spacing:.12em;color:var(--muted)}.cards strong{display:block;font-size:56px;letter-spacing:-.06em;margin:8px 0}.cards article:nth-child(2) strong{color:var(--amber)}.cards article:nth-child(3) strong{color:var(--green)}section.board{margin:72px 0 85px}.board h2{font-size:35px;letter-spacing:-.04em}.table{overflow:auto;border:1px solid var(--line)}table{border-collapse:collapse;width:100%;min-width:760px}th{text-align:left;padding:18px;background:#242d46;color:#aab6d1;font:700 10px monospace;letter-spacing:.1em}td{padding:18px;border-top:1px solid var(--line);font-size:13px}tr:hover td{background:#202945}.badge{display:inline-block;padding:7px 9px;border:1px solid var(--green);color:var(--green);font:700 10px monospace}.badge.backlog{border-color:var(--amber);color:var(--amber)}td.reason{color:var(--muted)}footer{border-top:1px solid var(--line);padding:27px 0 50px;color:var(--muted);font:700 10px monospace;letter-spacing:.1em}@media(max-width:650px){.cards{grid-template-columns:1fr}.intro{padding:65px 0 45px}header span{display:none}}@media(prefers-reduced-motion:reduce){*,*:before,*:after{animation:none!important}}.cards article{animation:rise .6s both}.cards article:nth-child(2){animation-delay:.1s}.cards article:nth-child(3){animation-delay:.2s}@keyframes rise{from{opacity:0;transform:translateY(15px)}to{opacity:1;transform:none}}
            </style></head><body><div class="glow" aria-hidden="true"></div><div class="shell"><header><strong>FULFILLMENT PLANNER</strong><span>ALLOCATION / REPORT</span></header><main><div class="intro"><span class="eyebrow">POLÍTICA DETERMINÍSTICA / SEM OVERSELL</span><h1>Decisões claras.<br><em>Estoque íntegro.</em></h1><p>Pedidos priorizados, reservas completas e motivos explícitos para cada item em backlog.</p></div><div class="cards"><article><span>PEDIDOS ALOCADOS</span><strong>""")
        append(plan.allocatedCount)
        append("""</strong></article><article><span>EM BACKLOG</span><strong>""")
        append(plan.backlogCount)
        append("""</strong></article><article><span>UNIDADES RESERVADAS</span><strong>""")
        append(plan.allocatedUnits)
        append("""</strong></article></div><section class="board"><span class="eyebrow">01 / PLANO DE ATENDIMENTO</span><h2>Ordem de execução</h2><div class="table"><table><thead><tr><th>PEDIDO</th><th>SKU</th><th>QUANTIDADE</th><th>PRIORIDADE</th><th>PRAZO</th><th>DECISÃO</th><th>MOTIVO</th></tr></thead><tbody>""")
        for (decision in plan.decisions) {
            val order = decision.order
            append("<tr><td>").append(escape(order.id)).append("</td><td>").append(escape(order.sku))
                .append("</td><td>").append(order.quantity).append("</td><td>").append(order.priority)
                .append("</td><td>").append(order.deadline).append("</td><td><span class=\"badge")
                .append(if (decision.status == Status.BACKLOG) " backlog" else "")
                .append("\">").append(if (decision.status == Status.ALLOCATED) "ALOCADO" else "BACKLOG")
                .append("</span></td><td class=\"reason\">").append(escape(decision.reason)).append("</td></tr>")
        }
        append("""</tbody></table></div></section></main><footer>BUILT BY LUIS DA SILVA · KOTLIN / RULES / OPERATIONS</footer></div></body></html>""")
    }
}
