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
- Painel de alterações: janela separada que abre junto com o programa, fica ao lado do menu e se atualiza sozinha. Tem um resumo e uma aba por categoria (veículos, clientes, funcionários e locações) com tudo o que foi incluído, alterado ou excluído, o antes e o depois de cada campo e filtro por tipo. Se for fechada, a opção "Mostrar painel de alterações" do menu a abre de novo. O histórico é gravado no mesmo arquivo de dados.
- Gerar códigos automáticos e sequenciais, salvar os dados em `dados/locadora.dat` a cada alteração e carregá-los ao abrir.

## Como rodar

Há três formas: IntelliJ, VS Code ou linha de comando. Em qualquer uma é preciso ter o JDK 17 ou mais novo instalado (`java -version` e `javac -version` devem funcionar no terminal).

### IntelliJ

Abra a pasta do projeto e execute a classe `Main` (pasta `src`).

### VS Code

1. Instale o JDK e o [VS Code](https://code.visualstudio.com/).
2. No VS Code, instale a extensão **Extension Pack for Java** (da Microsoft). Ela inclui suporte à linguagem, depurador e executor de testes.
3. Abra a **pasta raiz do projeto** (`File > Open Folder`, escolha `ProjetoFinalPOO`). Abrir uma subpasta como `src` quebra os caminhos.
4. Para o VS Code enxergar também a pasta de testes, crie o arquivo `.vscode/settings.json` com:

   ```json
   {
     "java.project.sourcePaths": ["src", "test"],
     "java.project.outputPath": "build/vscode",
     "files.encoding": "utf8"
   }
   ```

5. Espere o canto inferior direito terminar de carregar o projeto Java (ícone de relógio ou "Importing"). Se aparecerem erros falsos, use `Ctrl+Shift+P` e rode **Java: Clean Java Language Server Workspace**.
6. Abra `src/Main.java` e clique em **Run** (acima do `main`) ou aperte `F5`. A janela do JOptionPane abre em separado.
7. Para os testes, abra `test/teste/ExecutarTestes.java` e clique em **Run** acima do `main`. A saída aparece no terminal integrado.

O VS Code roda o programa a partir da pasta raiz do projeto, então o arquivo `dados/locadora.dat` é criado ali, igual nas outras formas. Se a janela do programa não aparecer, confira se ela não ficou atrás do VS Code.

### Linha de comando

Na pasta do projeto:

```
javac -encoding UTF-8 -d build/src $(find src -name '*.java')
java -cp build/src Main
```

As pastas `build/` e `out/` (código compilado) e `dados/` (arquivo de dados do programa) ficam fora do git, ver `.gitignore`.

Na primeira vez, cadastre um funcionário, um veículo e um cliente antes de registrar a primeira locação (a matrícula do funcionário é pedida na locação).

Testes: no IntelliJ, execute `teste.ExecutarTestes` (pasta `test`). Pela linha de comando:

```
javac -encoding UTF-8 -cp build/src -d build/test $(find test -name '*.java')
java -cp build/src:build/test teste.ExecutarTestes
```

Os casos estão listados em `docs/casos-de-teste.md`.
