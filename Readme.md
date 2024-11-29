---

# Atividade 2 - Compiladores 2024.2

## Lexer

O **Lexer** (ou Analisador Léxico) foi implementado para processar arquivos da linguagem **Jack** e realizar a tokenização, que é o processo de dividir o código em unidades significativas, chamadas de *tokens*. O objetivo é transformar o código-fonte escrito em Jack em uma sequência de tokens que o compilador pode entender e manipular em estágios posteriores.

### Progresso

- [x] Leitura de **keywords** (palavras-chave)
- [x] Leitura de **símbolos** (operadores e delimitadores)
- [x] Leitura de **strings** (cadeias de caracteres)
- [x] **Ignorar comentários** (comentários de linha e bloco)
- [x] **Contagem de linhas** (para exibir a localização dos tokens)

### 📋 Pré-requisitos

- **Java JDK 11+** - Necessário para compilar e rodar o projeto
- **Maven** - Para gerenciamento de dependências e construção do projeto

### 🔧 Instalação

1. Clone o repositório:
    ```bash
    git clone https://github.com/profsergiocosta/lab-jackcompiler
    ```

2. Acesse o diretório do projeto:
    ```bash
    cd lab-jackcompiler
    ```

3. Compile o projeto com o Maven:
    ```bash
    mvn package
    ```

### ⚙️ Executando o Compilador

Após compilar o projeto, você pode rodar o compilador usando o seguinte comando:

```bash
java -cp target/jackcompiler-1.0-SNAPSHOT.jar br.ufma.ecp.App
```

### 📝 Testando a Leitura de Tokens

Para testar a leitura e impressão de tokens, basta rodar a classe `TokenTester.java`. O arquivo Jack que será analisado está localizado em `file_teste_jack/projects/10/Square/Main.jack`.

### 🔧 Executando os Testes

Os testes podem ser executados rodando a classe `TokenTester.java` diretamente no seu ambiente de desenvolvimento. O arquivo `Main.jack` estará disponível no diretório `file_teste_jack/projects/10/Square/`, e o objetivo dos testes é garantir que os tokens sejam corretamente reconhecidos e impressos a partir do arquivo Jack.

### 📦 Implantação

Este projeto é um compilador da linguagem Jack, que visa implementar um compilador básico para a linguagem, incluindo análise léxica, sintática e eventualmente semântica. O projeto se integra ao ambiente de desenvolvimento Java para compilar e testar as funcionalidades.

---

## 🛠️ Construído com

Ferramentas e tecnologias usadas para criar o projeto:

- **Java 11+** - Linguagem utilizada para o desenvolvimento do compilador
- **Maven** - Ferramenta para gerenciamento de dependências e construção do projeto
- **JUnit** - Para realização de testes unitários

## 📌 Versão

Nós usamos [GitHub](https://github.com) para controle de versão. Para as versões disponíveis, observe as [tags neste repositório](https://github.com/seu_usuario/nome_do_repositorio/tags).

## ✒️ Autores

- **Nerval Junior** - *Documentação, Desenvolvimento, Engenharia* - [Perfil GitHub](https://github.com/nervaljunior)
- **Luan Costa** - *Documentação, Desenvolvimento* - [Perfil GitHub](https://github.com/luanc202)
- **Rodrigo Lima** - *Documentação, Desenvolvimento* - [Perfil GitHub](https://github.com/rodrigolsdev)

---
