# Casos de teste funcionais

Executados por `teste.ExecutarTestes` (código em `test/teste/`). A coluna Requisitos aponta os RF, RN e UC do documento da primeira entrega e os itens do enunciado final. Resultado da última execução.

## Modelo: diária e CNH de cada categoria (polimorfismo)

| Caso | Requisitos | O que verifica | Resultado |
|---|---|---|---|
| CT01 | RF01 | Carro automático: diária base +10% (150,00 vira 165,00) | Passou |
| CT02 | RF01 | Carro manual: diária igual à base | Passou |
| CT03 | RF01 | Moto: +20% só acima de 500 cc (500 cc paga a base, 600 cc paga 120,00) | Passou |
| CT04 | RF01 | Utilitário: diária base + R$ 0,02 por kg (200,00 e 1000 kg viram 220,00) | Passou |
| CT05 | RN03 | CNH exigida: Moto A, Carro B, Utilitário B até 1.500 kg e C acima | Passou |
| CT06 | RF01 | Cada subclasse informa a própria categoria e a diária é calculada sem conhecer o tipo | Passou |

## Modelo: valor da locação, desconto, seguro e multa

| Caso | Requisitos | O que verifica | Resultado |
|---|---|---|---|
| CT07 | RN06 | Desconto por período: 0% até 6, 5% de 7 a 14, 10% de 15 a 29, 15% a partir de 30 | Passou |
| CT08 | RF07, RN06, RN07 | Exemplo do PDF: carro automático (base 150), 10 diárias com seguro = R$ 1.817,50 | Passou |
| CT09 | RF07, RN07 | Seguro é opcional: o mesmo aluguel sem seguro custa 1.567,50 | Passou |
| CT10 | RF08, RN08 | Exemplo do PDF: 2 dias de atraso somam 495,00 de multa e o total é R$ 2.312,50 | Passou |
| CT11 | RF08 | Devolução no prazo não tem multa | Passou |
| CT12 | UC07 | Devolução antecipada recalcula pelas diárias usadas (8 de 10: 8 x 165 x 0,95 + 200 = 1.454,00) | Passou |
| CT13 | RN05 | Devolução no mesmo dia da retirada cobra o mínimo de uma diária | Passou |
| CT14 | RN05 | A devolução prevista tem de ser depois da retirada (igual ou antes é recusado) | Passou |

## Modelo: situação do veículo e da locação

| Caso | Requisitos | O que verifica | Resultado |
|---|---|---|---|
| CT15 | RN01 | locar() muda para Locado e um segundo locar() é recusado | Passou |
| CT16 | RF02 | Manutenção: veículo disponível vai e volta; veículo locado não pode ir | Passou |
| CT17 | RF08 | finalizar() encerra a locação, grava data e km e libera o veículo | Passou |
| CT18 | UC07 | Quilometragem menor que a da retirada é recusada e nada muda | Passou |
| CT19 | UC07 | Devolução antes da retirada é recusada | Passou |
| CT20 | UC07 | Finalizar uma locação já finalizada é recusado | Passou |
| CT21 | RF09, RN09 | cancelar() zera o valor, libera o veículo e só vale para locação ativa | Passou |
| CT22 | RF11 | isAtrasada: só a locação ativa depois da data prevista | Passou |
| CT23 | RN02 | Cliente: idade na data, CNH válida até o dia da validade (inclusive) e categorias de uma CNH AB | Passou |

## Modelo: códigos automáticos e sequenciais (atributos estáticos)

| Caso | Requisitos | O que verifica | Resultado |
|---|---|---|---|
| CT24 | RF12 | Clientes e funcionários compartilham a sequência de pessoas, sempre de 1 em 1 | Passou |
| CT25 | RF12 | Veículos de qualquer categoria usam a mesma sequência | Passou |
| CT26 | RF12 | Locações têm a própria sequência | Passou |
| CT27 | RF12, RF13 | atualizarContador reajusta a sequência depois de carregar o arquivo e nunca a faz voltar | Passou |
| CT28 | RF12, RN05 | Locação recusada por data inválida não consome código | Passou |

## Modelo: classes abstratas e interfaces

| Caso | Requisitos | O que verifica | Resultado |
|---|---|---|---|
| CT29 | Item h | Pessoa, Veiculo e RepositorioBase são abstratas; Identificavel, Tarifavel e Repositorio são interfaces | Passou |
| CT30 | Item h | Todo objeto do modelo é Identificavel e serializável; veículo é Tarifavel | Passou |

## Locadora: registrar locação (UC04, UC05, UC06)

| Caso | Requisitos | O que verifica | Resultado |
|---|---|---|---|
| CT31 | RF05, UC04 | Locação válida fica Ativa, o veículo fica Locado e o valor previsto é calculado | Passou |
| CT32 | RF07, UC06 | simularLocacao mostra o valor previsto sem gravar nada | Passou |
| CT33 | RN01, RF06 | Veículo Locado ou em manutenção não pode ser locado e nada é gravado | Passou |
| CT34 | RN04 | Cliente com locação ativa não pode alugar outro veículo; depois de devolver, pode | Passou |
| CT35 | RN05 | Datas inválidas na locação são recusadas e nada é gravado | Passou |
| CT36 | RF05 | Cliente, veículo ou funcionário inexistente: registro não encontrado | Passou |

## Locadora: habilitação do cliente (RN02, RN03)

| Caso | Requisitos | O que verifica | Resultado |
|---|---|---|---|
| CT37 | RN02 | Idade mínima de 21 anos na retirada: 21 anos exatos passa, 20 anos e 364 dias não | Passou |
| CT38 | RN02 | CNH vencida é recusada; CNH que vence no dia da retirada ainda vale | Passou |
| CT39 | RN03 | Moto exige CNH A: B é recusada; A e AB são aceitas | Passou |
| CT40 | RN03 | Carro exige CNH B: só A é recusada | Passou |
| CT41 | RN03 | Utilitário até 1.500 kg exige B; acima disso exige C | Passou |

## Locadora: exclusão e dados duplicados (RN09, RN10)

| Caso | Requisitos | O que verifica | Resultado |
|---|---|---|---|
| CT42 | RN09 | Veículo com locação ativa não pode ser excluído; depois da devolução pode | Passou |
| CT43 | RN09 | Cliente com locação ativa não pode ser excluído; locação cancelada libera a exclusão | Passou |
| CT44 | RN10 | Placa repetida é recusada na inclusão, sem diferenciar maiúsculas de minúsculas | Passou |
| CT45 | RN10 | CPF repetido de cliente é recusado | Passou |
| CT46 | RN10 | Funcionário: CPF e matrícula repetidos são recusados | Passou |

## Locadora: devolução, multa e cancelamento (UC07, UC08, UC09)

| Caso | Requisitos | O que verifica | Resultado |
|---|---|---|---|
| CT47 | RF08, UC07 | Devolução no prazo: valor final igual ao previsto, locação Finalizada, veículo Disponível | Passou |
| CT48 | RN08, UC08 | Devolução com 2 dias de atraso soma a multa: R$ 2.312,50 (exemplo do PDF) | Passou |
| CT49 | UC07 | Devolução: locação inexistente, já finalizada ou km menor que o da retirada são recusadas | Passou |
| CT50 | RF09, RN09 | Cancelar libera o veículo, não cobra nada e o cliente pode alugar de novo | Passou |
| CT51 | UC07 | A locação é localizada pelo código ou pela placa do veículo locado | Passou |

## Locadora: manutenção e consultas (UC01, UC10, UC11)

| Caso | Requisitos | O que verifica | Resultado |
|---|---|---|---|
| CT52 | RF02 | Manutenção: envia e retorna à frota; só volta quem está em manutenção; locado não vai | Passou |
| CT53 | RF10, UC10 | Frota disponível exclui Locados e em manutenção e filtra por categoria | Passou |
| CT54 | RF11, UC11 | Histórico por cliente e por veículo traz locações em qualquer situação | Passou |
| CT55 | RF11, UC11 | Locações em atraso: só as ativas depois da data prevista | Passou |

## CRUD: veículos das três categorias

| Caso | Requisitos | O que verifica | Resultado |
|---|---|---|---|
| CT56 | RF01, item i | Incluir, consultar (código e placa) e listar veículos de cada categoria | Passou |
| CT57 | RF01, item i | Alterar campos comuns e específicos de cada categoria | Passou |
| CT58 | RN10, item i | Alterar para a placa de outro veículo é recusado e o cadastro volta ao que estava gravado | Passou |
| CT59 | item i | Alterar um veículo mantém as locações apontando para o mesmo objeto | Passou |
| CT60 | RF01, item i | Excluir veículo remove da lista; consulta posterior não encontra | Passou |

## CRUD: clientes e funcionários

| Caso | Requisitos | O que verifica | Resultado |
|---|---|---|---|
| CT61 | RF03, item i | Cliente: incluir, consultar por CPF e código, listar, alterar e excluir | Passou |
| CT62 | RN10, item i | Cliente: alterar o CPF para o de outro é recusado e o cadastro volta ao que estava gravado | Passou |
| CT63 | RF04, item i | Funcionário: incluir, consultar por matrícula, listar, alterar cargo e excluir | Passou |
| CT64 | item i | Buscar por código, placa, CPF ou matrícula inexistentes lança RegistroNaoEncontradoException | Passou |

## Persistência: arquivo dados/locadora.dat por serialização

| Caso | Requisitos | O que verifica | Resultado |
|---|---|---|---|
| CT65 | RF13, item g | Salvar e carregar preserva todos os dados, inclusive o tipo de cada veículo | Passou |
| CT66 | RF13 | Depois de carregar, as locações apontam para os mesmos objetos das listas (sem cópias soltas) | Passou |
| CT67 | RF13 | Cada alteração é salva sozinha: uma locadora nova já enxerga o que foi feito, sem chamar salvarDados() | Passou |
| CT68 | RF13 | Sem arquivo ainda, carregar deixa a locadora vazia e sem erro | Passou |
| CT69 | RF13, item l | Arquivo corrompido gera LocadoraException com mensagem e não apaga o arquivo | Passou |
| CT70 | RF12, RF13 | Em outra execução do programa, os códigos continuam depois do maior código gravado | Passou |
| CT71 | item m | As listas lidas do arquivo são ArrayList e o repositório as manipula | Passou |
