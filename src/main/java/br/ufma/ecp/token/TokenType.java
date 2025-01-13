package br.ufma.ecp.token;

import java.util.List;

public enum TokenType {
    PLUS("+"),
    MINUS("-"),
    SLASH("/"),
    ASTERISK("*"),
    DOT("."),
    AND("&"),
    OR("|"),
    NOT("~"),
    GREATER(">"),
    LOWER("<"),
    EQUALS("="),
    LPAREN("("),
    RPAREN(")"),
    LBRACKET("["),
    RBRACKET("]"),
    LBRACE("{"),
    RBRACE("}"),
    SEMICOLON(";"),
    COMMA(","),


    // Literals.
    NUMBER("0123456789"),
    STRING("STRING"),

    IDENT("IDENT"),


    // keywords
    METHOD("method"),
    WHILE("while"),
    IF("if"),
    CLASS("class"),
    CONSTRUCTOR("constructor"),
    FUNCTION("function"),
    FIELD("field"),
    STATIC("static"),
    VAR("var"),
    CHAR("char"),
    BOOLEAN("boolean"),
    VOID("void"),
    TRUE("true"),
    FALSE("false"),
    NULL("null"),
    THIS("this"),
    LET("let"),
    DO("do"),
    ELSE("else"),
    RETURN("return"),
    INT("int"),

    EOF("\0"),

    ILLEGAL("ILLEGAL");

    public final String value;
    private String string;

    TokenType(String value) {
        this.value = value;
    }

    static public boolean isSymbol(char c) {
        String symbols = "{}()[].,;+-*/&|<>=~";
        return symbols.indexOf(c) > -1;
    }

    static public boolean isKeyword(TokenType type) {
        List<TokenType> keywords =
                List.of(
                        METHOD,
                        WHILE,
                        IF,
                        CLASS,
                        CONSTRUCTOR
                );
        return keywords.contains(type);
    }

    public String getString() {
        return this.string;
    }

}
