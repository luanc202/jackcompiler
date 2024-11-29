package br.ufma.ecp;

import static br.ufma.ecp.token.TokenType.EOF;

import java.io.*;

import javax.swing.JFileChooser;

import br.ufma.ecp.token.Token;


public class TokenTester {
    public static void main(String[] args) throws IOException {
    	JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(null);

        
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
    
            String input = Lexico.geracaoLexica(file);
            
            if (input == null || input.isBlank()) {
                System.err.println("Erro: Não foi possível processar o arquivo.");
                return;
            }
            
        Scanner scan = new Scanner (input.getBytes());
        while (true) {
            Token token = scan.nextToken();
            System.out.println(token);
            if (token.type == EOF) {
                break;
            }
        }
    }
}
}