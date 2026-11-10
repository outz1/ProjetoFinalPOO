# Sistema de Gerenciamento de Locadora de Veículos

## Sinopse

Projeto final da disciplina de Programação Orientada a Objetos (UFG). Aplicativo desktop em Java, com interface em JOptionPane, que substitui as planilhas e fichas de papel de uma locadora: controla a frota (carros, motos e utilitários), os clientes, os funcionários e as locações, calcula o valor de cada locação e guarda tudo em arquivo.

## Integrantes

- Arthur Morais
- Eduardo de Brito
- Leandro Gabriel
- Pedro Cabral

## Funções

- Incluir, alterar, excluir, consultar e listar veículos (carro, moto e utilitário), clientes e funcionários.
- Colocar um veículo em manutenção e devolvê-lo à frota.
- Registrar locação, conferindo se o veículo está disponível e se o cliente pode dirigi-lo (21 anos ou mais, CNH válida e da categoria certa, uma locação ativa por vez). O valor previsto, com desconto por período e seguro opcional, aparece antes da confirmação.
- Registrar devolução, com valor final e multa por atraso.
- Cancelar uma locação ativa.
- Consultar os veículos disponíveis por categoria e o histórico de locações por cliente, por veículo ou em atraso.
- Gerar códigos automáticos e sequenciais, salvar os dados em `dados/locadora.dat` a cada alteração e carregá-los ao abrir.

## Como rodar

No IntelliJ, execute a classe `Main` (pasta `src`). Pela linha de comando, na pasta do projeto:

```
javac -encoding UTF-8 -d build/src $(find src -name '*.java')
java -cp build/src Main
```

Na primeira vez, cadastre um funcionário, um veículo e um cliente antes de registrar a primeira locação (a matrícula do funcionário é pedida na locação).

Testes: no IntelliJ, execute `teste.ExecutarTestes` (pasta `test`). Pela linha de comando:

```
javac -encoding UTF-8 -cp build/src -d build/test $(find test -name '*.java')
java -cp build/src:build/test teste.ExecutarTestes
```

Os casos estão listados em `docs/casos-de-teste.md`.
