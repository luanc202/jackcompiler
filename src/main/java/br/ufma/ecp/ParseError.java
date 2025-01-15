package br.ufma.ecp;

public class ParseError extends RuntimeException {
    private String message;

    public ParseError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

