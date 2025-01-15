package br.ufma.ecp;

import br.ufma.ecp.token.Token;
import br.ufma.ecp.token.TokenType;
import br.ufma.ecp.SymbolTable.Kind;

import java.util.Objects;

public class Parser {

    private Scanner scan;
    private Token currentToken;
    private Token peekToken;
    private StringBuilder xmlOutput = new StringBuilder();
    private VMWriter vmWriter = new VMWriter();
    private Integer ifLabelNum = 0;
    private Integer whileLabelNum = 0;
    private SymbolTable symTable = new SymbolTable();

    private String className = "";


    public Parser(byte[] input) {
        scan = new Scanner(input);
        nextToken();

    }

    static public boolean isOperator(String op) {
        return op != "" && "+-*/<>=~&|".contains(op);
    }

    private static void report(int line, String where,
                               String message) {
        System.err.println(
                "[line " + line + "] Error" + where + ": " + message);
    }

    public void parse() {
        parseClass();
    }

    
    void parseTerm() {
        printNonTerminal("term");
        switch (peekToken.type) {
            case NUMBER:
                expectPeek(TokenType.NUMBER);
                vmWriter.writePush(VMWriter.Segment.CONST, Integer.parseInt(currentToken.lexeme));
                break;
            case STRING:
                expectPeek(TokenType.STRING);
                String strValue = currentToken.lexeme;
                vmWriter.writePush(VMWriter.Segment.CONST, strValue.length());
                vmWriter.writeCall("String.new", 1);
                for (int i = 0; i < strValue.length(); i++) {
                    vmWriter.writePush(VMWriter.Segment.CONST, strValue.charAt(i));
                    vmWriter.writeCall("String.appendChar", 2);
                }
                break;
            case FALSE:
            case NULL:
            case TRUE:
                expectPeek(TokenType.FALSE, TokenType.NULL, TokenType.TRUE);
                vmWriter.writePush(VMWriter.Segment.CONST, 0);
                if (currentToken.type == TokenType.TRUE) {
                    vmWriter.writeArithmetic(Command.NOT);
                }
                break;
            case THIS:
                expectPeek(TokenType.THIS);
                vmWriter.writePush(VMWriter.Segment.POINTER, 0);
                break;
            case IDENT:
                expectPeek(TokenType.IDENT);
                SymbolTable.Symbol sym = symTable.resolve(currentToken.lexeme);
    
                if (peekTokenIs(TokenType.LPAREN) || peekTokenIs(TokenType.DOT)) {
                    parseSubroutineCall();
                } else if (peekTokenIs(TokenType.LBRACKET)) {
                    expectPeek(TokenType.LBRACKET);
                    parseExpression(); // Calcula o índice
                    expectPeek(TokenType.RBRACKET);
    
                    vmWriter.writePush(kindToSegment(sym.kind()), sym.index()); // Push base address
                    vmWriter.writeArithmetic(Command.ADD);                     // Base + Index
                    vmWriter.writePop(VMWriter.Segment.POINTER, 1);            // Pop para pointer 1
                    vmWriter.writePush(VMWriter.Segment.THAT, 0);              // Push o valor no endereço
                } else {
                    vmWriter.writePush(kindToSegment(sym.kind()), sym.index());
                }
                break;
            case LPAREN:
                expectPeek(TokenType.LPAREN);
                parseExpression();
                expectPeek(TokenType.RPAREN);
                break;
            case MINUS:
            case NOT:
                expectPeek(TokenType.NOT, TokenType.MINUS);
                TokenType operator = currentToken.type;
                parseTerm();
    
                if (operator.equals(TokenType.MINUS)) {
                    vmWriter.writeArithmetic(Command.NEG);
                } else {
                    vmWriter.writeArithmetic(Command.NOT);
                }
                break;
            default:
                throw error(peekToken, "term expected");
        }
    
        printNonTerminal("/term");
    }
    
    void parseLet() {
        boolean isArray = false;
    
        printNonTerminal("letStatement");
    
        expectPeek(TokenType.LET);
        expectPeek(TokenType.IDENT);
    
        SymbolTable.Symbol symbol = symTable.resolve(currentToken.lexeme);
    
        if (peekTokenIs(TokenType.LBRACKET)) { // Array
            expectPeek(TokenType.LBRACKET);
            parseExpression(); 
            vmWriter.writePush(kindToSegment(symbol.kind()), symbol.index()); 
            vmWriter.writeArithmetic(Command.ADD); 
            expectPeek(TokenType.RBRACKET);
            isArray = true;
        }
    
        expectPeek(TokenType.EQUALS);
        parseExpression();
    
        if (isArray) {
            vmWriter.writePop(VMWriter.Segment.TEMP, 0); 
            vmWriter.writePop(VMWriter.Segment.POINTER, 1); 
            vmWriter.writePush(VMWriter.Segment.TEMP, 0); 
            vmWriter.writePop(VMWriter.Segment.THAT, 0); 
        } else {
            vmWriter.writePop(kindToSegment(symbol.kind()), symbol.index());
        }
    
        expectPeek(TokenType.SEMICOLON);
    
        printNonTerminal("/letStatement");
    }

    void number() {
        System.out.println(currentToken.lexeme);
        match(TokenType.NUMBER);
    }

    private void nextToken() {
        currentToken = peekToken;
        peekToken = scan.nextToken();
    }

    private void match(TokenType t) {
        if (currentToken.type == t) {
            nextToken();
        } else {
            throw new Error("syntax error");
        }
    }

    void oper() {
        if (currentToken.type == TokenType.PLUS) {
            match(TokenType.PLUS);
            number();
            System.out.println("add");
            oper();
        } else if (currentToken.type == TokenType.MINUS) {
            match(TokenType.MINUS);
            number();
            System.out.println("sub");
            oper();
        } else if (currentToken.type == TokenType.EOF) {
            // vazio
        } else {
            throw new Error("syntax error");
        }
    }

    private ParseError error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
        return new ParseError(message);
    }

    void parseClassVarDec() {
        printNonTerminal("classVarDec");
        expectPeek(TokenType.FIELD, TokenType.STATIC);

        SymbolTable.Kind kind = Kind.STATIC;
        if (currentTokenIs(TokenType.FIELD))
            kind = Kind.FIELD;

        // 'int' | 'char' | 'boolean' | className
        expectPeek(TokenType.INT, TokenType.CHAR, TokenType.BOOLEAN, TokenType.IDENT);
        String type = currentToken.lexeme;

        expectPeek(TokenType.IDENT);
        String name = currentToken.lexeme;

        symTable.define(name, type, kind);
        while (peekTokenIs(TokenType.COMMA)) {
            expectPeek(TokenType.COMMA);
            expectPeek(TokenType.IDENT);

            name = currentToken.lexeme;
            symTable.define(name, type, kind);
        }

        expectPeek(TokenType.SEMICOLON);
        printNonTerminal("/classVarDec");
    }

    void parseClass() {
        printNonTerminal("class");
        expectPeek(TokenType.CLASS);
        expectPeek(TokenType.IDENT);
        className = currentToken.lexeme;
        expectPeek(TokenType.LBRACE);

        while (peekTokenIs(TokenType.STATIC) || peekTokenIs(TokenType.FIELD)) {
            parseClassVarDec();
        }

        while (peekTokenIs(TokenType.FUNCTION) || peekTokenIs(TokenType.CONSTRUCTOR) || peekTokenIs(TokenType.METHOD)) {
            parseSubroutineDec();
        }

        expectPeek(TokenType.RBRACE);

        printNonTerminal("/class");
    }

    void parseVarDec() {
        printNonTerminal("varDec");
        expectPeek(TokenType.VAR);

        SymbolTable.Kind kind = Kind.VAR;

        expectPeek(TokenType.INT, TokenType.CHAR, TokenType.BOOLEAN, TokenType.IDENT);
        String type = currentToken.lexeme;

        expectPeek(TokenType.IDENT);
        String name = currentToken.lexeme;
        symTable.define(name, type, kind);

        while (peekTokenIs(TokenType.COMMA)) {
            expectPeek(TokenType.COMMA);
            expectPeek(TokenType.IDENT);

            name = currentToken.lexeme;
            symTable.define(name, type, kind);
        }
        expectPeek(TokenType.SEMICOLON);
        printNonTerminal("/varDec");
    }

    void parseParameterList() {
        printNonTerminal("parameterList");

        SymbolTable.Kind kind = Kind.ARG;

        if (!peekTokenIs(TokenType.RPAREN)) // verifica se tem pelo menos uma expressao
        {
            expectPeek(TokenType.INT, TokenType.CHAR, TokenType.BOOLEAN, TokenType.IDENT);
            String type = currentToken.lexeme;

            expectPeek(TokenType.IDENT);
            String name = currentToken.lexeme;
            symTable.define(name, type, kind);

            while (peekTokenIs(TokenType.COMMA)) {
                expectPeek(TokenType.COMMA);
                expectPeek(TokenType.INT, TokenType.CHAR, TokenType.BOOLEAN, TokenType.IDENT);
                type = currentToken.lexeme;

                expectPeek(TokenType.IDENT);
                name = currentToken.lexeme;

                symTable.define(name, type, kind);
            }
        }

        printNonTerminal("/parameterList");
    }

    void parseStatement() {

        switch (peekToken.type) {
            case LET:
                parseLet();
                break;
            case WHILE:
                parseWhile();
                break;
            case IF:
                parseIf();
                break;
            case RETURN:
                parseReturn();
                break;
            case DO:
                parseDo();
                break;
            default:
                throw error(peekToken, "Expected a statement");
        }
    }

    void parseSubroutineCall() {     
        
        var nArgs = 0;

        var ident = currentToken.lexeme;
        var symbol = symTable.resolve(ident); // classe ou objeto
        var functionName = ident + ".";

        if (peekTokenIs(TokenType.LPAREN)) { // método da propria classe
            expectPeek(TokenType.LPAREN);
            vmWriter.writePush(VMWriter.Segment.POINTER, 0);
            nArgs = parseExpressionList() + 1;
            expectPeek(TokenType.RPAREN);
            functionName = className + "." + ident;
        } else {
            // pode ser um metodo de um outro objeto ou uma função
            expectPeek(TokenType.DOT);
            expectPeek(TokenType.IDENT); // nome da função

            if (symbol != null) { // é um metodo
                functionName = symbol.type() + "." + currentToken.lexeme;
                vmWriter.writePush(kindToSegment(symbol.kind()), symbol.index());
                nArgs = 1; // do proprio objeto
            } else {
                functionName += currentToken.lexeme; // é uma função
            }

            expectPeek(TokenType.LPAREN);
            nArgs += parseExpressionList();

            expectPeek(TokenType.RPAREN);
        }

        vmWriter.writeCall(functionName, nArgs);
  }
    
    void parseSubroutineDec() {
        printNonTerminal("subroutineDec");
    
        ifLabelNum = 0;
        whileLabelNum = 0;
    
        symTable.startSubroutine();
    
        expectPeek(TokenType.CONSTRUCTOR, TokenType.FUNCTION, TokenType.METHOD);
        TokenType subroutineType = currentToken.type;
    
        if (subroutineType == TokenType.METHOD) {
            symTable.define("this", className, Kind.ARG);
        }
    
        expectPeek(TokenType.VOID, TokenType.INT, TokenType.CHAR, TokenType.BOOLEAN, TokenType.IDENT);
        //String returnType = currentToken.lexeme;
    
        expectPeek(TokenType.IDENT);
        String subroutineName = currentToken.lexeme;
    
        expectPeek(TokenType.LPAREN);
        parseParameterList();
        expectPeek(TokenType.RPAREN);
    
        parseSubroutineBody(className + "." + subroutineName, subroutineType);
    
        printNonTerminal("/subroutineDec");
    }
    
    void parseSubroutineBody(String functionName, TokenType subroutineType) {

        printNonTerminal("subroutineBody");
        expectPeek(TokenType.LBRACE);
        while (peekTokenIs(TokenType.VAR)) {
            parseVarDec();
        }
        var nlocals = symTable.varCount(Kind.VAR);

        vmWriter.writeFunction(functionName, nlocals);

        if (subroutineType == TokenType.CONSTRUCTOR) {
            vmWriter.writePush(VMWriter.Segment.CONST, symTable.varCount(Kind.FIELD));
            vmWriter.writeCall("Memory.alloc", 1);
            vmWriter.writePop(VMWriter.Segment.POINTER, 0);
        }

        if (subroutineType == TokenType.METHOD) {
            vmWriter.writePush(VMWriter.Segment.ARG, 0);
            vmWriter.writePop(VMWriter.Segment.POINTER, 0);
        }

        parseStatements();
        expectPeek(TokenType.RBRACE);
        printNonTerminal("/subroutineBody");
    }
    
    void parseStatements() {
        printNonTerminal("statements");
        while (peekToken.type == TokenType.WHILE || peekToken.type == TokenType.IF ||
               peekToken.type == TokenType.LET || peekToken.type == TokenType.DO ||
               peekToken.type == TokenType.RETURN) {
            parseStatement();
        }
        printNonTerminal("/statements");
    }

    void parseDo() {
        printNonTerminal("doStatement");
    
        expectPeek(TokenType.DO);  
        nextToken();
        parseSubroutineCall();  
        expectPeek(TokenType.SEMICOLON); 
        
        vmWriter.writePop(VMWriter.Segment.TEMP, 0); 
    
        printNonTerminal("/doStatement");
    }
    
    private void expectPeek(TokenType... types) {
        for (TokenType type : types) {
            if (peekToken.type == type) {
                expectPeek(type);
                return;
            }
        }

        throw error(peekToken, "Expected a statement");

    }

    private void expectPeek(TokenType type) {
        if (peekToken.type == type) {
            nextToken();
            xmlOutput.append(String.format("%s\r\n", currentToken.toString()));
        } else {
            throw error(peekToken, "Expected " + type.name());
        }
    }



    void parseExpression() {
        printNonTerminal("expression");
        parseTerm();
        while (isOperator(peekToken.lexeme)) {
            TokenType operator = peekToken.type;
            expectPeek(peekToken.type);
            parseTerm();
            compileOperators(operator);
        }
        printNonTerminal("/expression");
    }

    

    boolean peekTokenIs(TokenType type) {
        return peekToken.type == type;
    }

    boolean currentTokenIs(TokenType type) {
        return currentToken.type == type;
    }

    public String VMOutput() {
        return vmWriter.vmOutput();
    }

    public String XMLOutput() {
        return xmlOutput.toString();
    }

    private void printNonTerminal(String nterminal) {
        xmlOutput.append(String.format("<%s>\r\n", nterminal));
    }

    int parseExpressionList() {
        printNonTerminal("expressionList");
   
        var nArgs = 0;
   
        if (!peekTokenIs(TokenType.RPAREN)) {
            parseExpression();
            nArgs = 1;
        }
   
        while (peekTokenIs(TokenType.COMMA)) {
            expectPeek(TokenType.COMMA);
            parseExpression();
            nArgs++;
        }
   
        printNonTerminal("/expressionList");
        return nArgs;
    }
   
    void parseReturn() {
        printNonTerminal("returnStatement");

        expectPeek(TokenType.RETURN);
        if (!peekTokenIs(TokenType.SEMICOLON)) {
            parseExpression();
        } else {
            vmWriter.writePush(VMWriter.Segment.CONST, 0);
        }
        expectPeek(TokenType.SEMICOLON);
        vmWriter.writeReturn();

        printNonTerminal("/returnStatement");
    }

    private Command typeOperator(TokenType type) {
        if (type == TokenType.PLUS)
            return Command.ADD;
        if (type == TokenType.MINUS)
            return Command.SUB;
        if (type == TokenType.LOWER)
            return Command.LT;
        if (type == TokenType.GREATER)
            return Command.GT;
        if (type == TokenType.EQUALS)
            return Command.EQ;
        if (type == TokenType.AND)
            return Command.AND;
        if (type == TokenType.OR)
            return Command.OR;
        return null;
    }

    public void compileOperators(TokenType type) {

        if (type == TokenType.ASTERISK) {
            vmWriter.writeCall("Math.multiply", 2);
        } else if (type == TokenType.SLASH) {
            vmWriter.writeCall("Math.divide", 2);
        } else {
            vmWriter.writeArithmetic(Objects.requireNonNull(typeOperator(type)));
        }
    }


    void parseIf() {
        printNonTerminal("ifStatement");

        var labelTrue = "IF_TRUE" + ifLabelNum;
        var labelFalse = "IF_FALSE" + ifLabelNum;
        var labelEnd = "IF_END" + ifLabelNum;

        ifLabelNum++;

        expectPeek(TokenType.IF);
        expectPeek(TokenType.LPAREN);
        parseExpression();
        expectPeek(TokenType.RPAREN);

        vmWriter.writeIf(labelTrue);
        vmWriter.writeGoto(labelFalse);
        vmWriter.writeLabel(labelTrue);

        expectPeek(TokenType.LBRACE);
        parseStatements();
        expectPeek(TokenType.RBRACE);

        if (peekTokenIs(TokenType.ELSE)) {
            vmWriter.writeGoto(labelEnd);
        }

        vmWriter.writeLabel(labelFalse);

        if (peekTokenIs(TokenType.ELSE)) {
            expectPeek(TokenType.ELSE);
            expectPeek(TokenType.LBRACE);
            parseStatements();
            expectPeek(TokenType.RBRACE);
            vmWriter.writeLabel(labelEnd);
        }

        printNonTerminal("/ifStatement");
    }

    void parseWhile() {
        printNonTerminal("whileStatement");

        var labelTrue = "WHILE_EXP" + whileLabelNum;
        var labelFalse = "WHILE_END" + whileLabelNum;
        whileLabelNum++;

        vmWriter.writeLabel(labelTrue);

        expectPeek(TokenType.WHILE);
        expectPeek(TokenType.LPAREN);
        parseExpression();

        vmWriter.writeArithmetic(Command.NOT);
        vmWriter.writeIf(labelFalse);

        expectPeek(TokenType.RPAREN);
        expectPeek(TokenType.LBRACE);
        parseStatements();

        vmWriter.writeGoto(labelTrue);
        vmWriter.writeLabel(labelFalse);

        expectPeek(TokenType.RBRACE);
        printNonTerminal("/whileStatement");
    }

    private VMWriter.Segment kindToSegment(Kind kind) {
        if (kind == Kind.STATIC)
            return VMWriter.Segment.STATIC;
        if (kind == Kind.FIELD)
            return VMWriter.Segment.THIS;
        if (kind == Kind.VAR)
            return VMWriter.Segment.LOCAL;
        if (kind == Kind.ARG)
            return VMWriter.Segment.ARG;
        return null;
    }
}
