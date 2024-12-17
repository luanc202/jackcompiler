package br.ufma.ecp.token;
public class Token {

    public final TokenType type;
    public final String lexeme;
    public final int line;

    public Token (TokenType type, String lexeme, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.line = line;
    }

    public String toString() {
    	
    	//criação da variavel categoria para atualização do metodo
    	String categoria = type.toString().toLowerCase();
    	
    	String valor = lexeme;
    	

    	if (TokenType.isSymbol(lexeme.charAt(0))) {
            categoria = "symbol";
            switch (valor) {
                case ">":
                    valor = "&gt;";
                    break;
                case "<":
                    valor = "&lt;";
                    break;
                case "\"":
                    valor = "&quot;";
                    break;
                case "&":
                    valor = "&amp;";
                    break;
            }
        } else if (categoria.equals("number")) {
            categoria = "integerConstant";
        } else if (categoria.equals("ident")) {
            categoria = "identifier";
        } else if (categoria.equals("string")) {
            categoria = "stringConstant";
        } else {
            categoria = "keyword";
        }

        return "<" + categoria + "> " + valor + " </" + categoria + ">";
        
        
        /*
         * 
         * dessa forma printou 
         <integerConstant> 45 </integerConstant>
<stringConstant> hello </stringConstant>
<identifier> variavel </identifier>
<symbol> + </symbol>
<keyword> while </keyword>
<symbol> &lt; </symbol>
<symbol> , </symbol>
<keyword> if </keyword>*/
        
    }
    
}
