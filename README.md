<p align="center"><img src="assets/hero.svg" alt="Fulfillment Planner — planejamento de atendimento" width="100%"></p>

# Fulfillment Planner

Um motor de alocação de pedidos em Kotlin. Recebe uma posição de estoque e uma carteira de pedidos, aplica uma política determinística e gera um relatório HTML para discutir o resultado com operação e comercial. A pergunta central é simples: quem pode ser atendido hoje, e por que o restante ficou para trás?

O modelo evita duas armadilhas comuns: reserva parcial escondida e decisão impossível de reproduzir. Cada pedido é alocado por inteiro ou fica no backlog com motivo explícito. Em caso de empate, a ordem é prioridade maior, prazo mais próximo e ID.

## Funcionalidades

- Leitura de arquivos TSV com contrato e erros que apontam para a linha problemática.
- Validação de IDs duplicados, quantidades negativas, SKUs repetidos e prioridades fora da faixa.
- Alocação sem oversell e resultado reproduzível para a mesma entrada.
- Relatório HTML responsivo com indicadores, tabela de decisões e escape dos dados de entrada.
- Testes para prioridade, integridade do saldo, rejeição de dados inválidos e proteção contra HTML injetado.

## Executar

Requer JDK 21 e Gradle 9.7. O build usa Kotlin 2.4.21. Na raiz:

```bash
gradle test
gradle run --args="samples/orders.tsv samples/stock.tsv out/report.html"
```

Abra `out/report.html` no navegador. Não é preciso serviço externo, banco ou cadastro. O projeto também roda integralmente na CI; os arquivos de demonstração são fictícios.

### Contrato dos arquivos

`orders.tsv`: `id`, `sku`, `quantity`, `priority`, `deadline` (ISO `YYYY-MM-DD`). `stock.tsv`: `sku`, `available`. O separador é tabulação; a primeira linha deve corresponder exatamente ao cabeçalho. O formato foi escolhido para ser fácil de exportar e auditar sem depender de parser CSV com variantes de quoting.

```text
orders.tsv + stock.tsv → validação → ordenação estável → reserva integral → relatório.html
```

## Decisões de arquitetura e limites

É um simulador de decisão, não um serviço de reserva concorrente. O estoque de entrada é uma fotografia; para confirmar reservas em produção seria necessário transação no banco e controle de versão por SKU. Não há substituição automática de produtos, split de pedido ou otimização global: a política é gulosa e explícita para ser fácil de explicar e reproduzir. O próximo passo natural seria comparar políticas com cenários e medir impacto antes de adotá-las.

Para deploy, publique o artefato JVM ou execute via Gradle em um job com os TSV de entrada e grave o HTML em storage interno. Evite colocar dados reais sensíveis em repositório público. Rollback significa voltar ao commit/binário anterior e regenerar o relatório a partir dos arquivos originais; nenhuma migração de schema é necessária.

---

<p align="center"><img src="https://github.com/LuisInacioDasilva.png?size=72" alt="Foto de Luis da Silva" width="72" height="72"><br><strong>Luis da Silva</strong><br>Back-end, dados e regras de negócio que precisam ser auditáveis.</p>
