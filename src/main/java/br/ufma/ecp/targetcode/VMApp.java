package br.ufma.ecp.targetcode;

import java.io.IOException;

public class VMApp {

    // O programa VM como string
    private static final String programavm = """
        // Exemplo de comandos VM
        push constant 10
        pop local 0
        push constant 21
        push constant 22
        pop argument 2
        pop argument 1
        push constant 36
        pop this 6
        push constant 42
        push constant 45
        pop that 5
        pop that 2
        push constant 510
        pop temp 6
        push local 0
        push that 5
        add
        push argument 1
        sub
        push this 6
        push this 6
        add
        sub
        push temp 6
        add
        """;

    public static void main(String[] args) {
        String outputFile = "program.asm"; // Arquivo de saída

        try {
            VMTranslator translator = new VMTranslator(outputFile);
            translator.translate(programavm);
            System.out.println("Tradução concluída com sucesso! Arquivo gerado: " + outputFile);
        } catch (IOException e) {
            System.err.println("Erro ao traduzir o programa VM: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
