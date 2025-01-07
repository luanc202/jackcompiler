package br.ufma.ecp;

import br.ufma.ecp.token.Token;
import br.ufma.ecp.token.TokenType;

import java.util.Objects;

public class Parser {

    private Scanner scan;
    private Token currentToken;
    private Token peekToken;
    private StringBuilder xmlOutput = new StringBuilder();
    private VMWriter vmWriter = new VMWriter();
    private Integer ifLabelNum = 0 ;
    private Integer whileLabelNum = 0;
    
    public Parser (byte[] input) {
        scan = new Scanner(input);
        nextToken();
        
    }

    public void parse () {
        expr();
    }

    void expr() {
        number();
        oper();
    }

    void number () {
        System.out.println(currentToken.lexeme);
        match(TokenType.NUMBER);
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
                break;
            case NOT:
                expectPeek(TokenType.NOT, TokenType.MINUS);
                TokenType operator = currentToken.type;
                parseTerm();

                if (operator.equals(TokenType.MINUS)) {
                    vmWriter.writeArithmetic(Command.NEG);
                } else {
                    vmWriter.writeArithmetic(Command.NOT);
                }
            default:
                throw error(peekToken, "term expected");
        }

        printNonTerminal("/term");
    }

    private void nextToken () {
        currentToken = peekToken;
        peekToken = scan.nextToken();
    }

   private void match(TokenType t) {
        if (currentToken.type == t) {
            nextToken();
        }else {
            throw new Error("syntax error");
        }
   }

    void oper () {
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
    
    
    void parseLet() {
        printNonTerminal("letStatement");  
        
        expectPeek(TokenType.LET);         
        expectPeek(TokenType.IDENT);       

        if (peekTokenIs(TokenType.LBRACKET)) {  
            expectPeek(TokenType.LBRACKET);
            parseExpression();
            expectPeek(TokenType.RBRACKET);
        }

        expectPeek(TokenType.EQUALS);          
        parseExpression();                 
        expectPeek(TokenType.SEMICOLON);   

        printNonTerminal("/letStatement"); 
    }



    void parseExpression() {
        printNonTerminal("expression");
        parseTerm ();
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

    static public boolean isOperator(String op) {
        return op!= "" && "+-*/<>=~&|".contains(op);
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
            throw error(peekToken, "Expected "+type.name());
        }
    }

    private static void report(int line, String where,
                               String message) {
        System.err.println(
                "[line " + line + "] Error" + where + ": " + message);
    }

    private ParseError error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
        return new ParseError(message);
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
    
    void parseExpressionList() {
        if (peekTokenIs(TokenType.RPAREN)) {
            return; 
        }

        parseExpression();
        while (peekTokenIs(TokenType.COMMA)) {
            expectPeek(TokenType.COMMA);
            parseExpression();
        }
    }


	public void parseSubroutineCall() {	
	    expectPeek(TokenType.IDENT); 
	    
	    if (peekTokenIs(TokenType.DOT)) {
	    	expectPeek(TokenType.DOT);
	    	expectPeek(TokenType.IDENT); 
	    }
	    
	    expectPeek(TokenType.LPAREN); 
	    parseExpressionList();        
	    expectPeek(TokenType.RPAREN);
	}

	void parseDo() {
		printNonTerminal("doStatement");
	    expectPeek(TokenType.DO);
	    parseSubroutineCall();
	    expectPeek(TokenType.SEMICOLON);
	    printNonTerminal("/doStatement");
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

    void parseSubroutineDec() {
        printNonTerminal("subroutineDec");

        ifLabelNum = 0;
        whileLabelNum = 0;
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
        parseReturn();
        expectPeek(TokenType.RBRACE);
        if (peekTokenIs(TokenType.ELSE)){
            vmWriter.writeGoto(labelEnd);
        }

        vmWriter.writeLabel(labelFalse);

        if (peekTokenIs(TokenType.ELSE))
        {
            expectPeek(TokenType.ELSE);
            expectPeek(TokenType.LBRACE);
            parseReturn();
            expectPeek(TokenType.RBRACE);
            vmWriter.writeLabel(labelEnd);
        }

        printNonTerminal("/ifStatement");
    }

}
