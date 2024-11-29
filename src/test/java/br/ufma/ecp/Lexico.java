package br.ufma.ecp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Lexico {

    public static String geracaoLexica(File file) {
        if (file == null || !file.exists()) {
            System.err.println("Arquivo inválido");
            return null;
        }

        try {
            System.out.println("Análise léxica para o arquivo: " + file.getAbsolutePath());
            return Files.readString(file.toPath()); // Lê o conteúdo do arquivo como uma String
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
            return null;
        }
    }
}
