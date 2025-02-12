package br.ufma.ecp.targetcode;

import java.io.File;
import java.io.IOException;

public class VMApp {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Uso: java VMApp <arquivo.vm ou diretório>");
            return;
        }

        File input = new File(args[0]);
        String outputFile = "program.asm";

        try {
            VMTranslator translator = new VMTranslator(outputFile);

            if (input.isDirectory()) {
                // Traduz todos os arquivos .vm do diretório
                translator.translateDirectory(input);
                System.out.println("Tradução concluída para o diretório: " + input.getAbsolutePath());
            } else if (input.isFile() && input.getName().endsWith(".vm")) {
                // Traduz um único arquivo .vm
                translator.translateFile(input);
                System.out.println("Tradução concluída para o arquivo: " + input.getAbsolutePath());
            } else {
                System.err.println("Erro: Forneça um diretório ou um arquivo .vm válido.");
            }
        } catch (IOException e) {
            System.err.println("Erro ao traduzir os arquivos: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
