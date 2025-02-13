package br.ufma.ecp.targetcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;

import java.io.File;
import java.io.IOException;

public class VMApp {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java VMApp <directory>");
            return;
        }

        File directory = new File(args[0]);
        if (!directory.isDirectory()) {
            System.err.println("The provided path is not a directory.");
            return;
        }

        String outputFile = "program.asm";
        try {
            VMTranslator translator = new VMTranslator(outputFile);
            translator.translateDirectory(directory);
            System.out.println("Tradução concluída com sucesso! Arquivo gerado: " + outputFile);
        } catch (IOException e) {
            System.err.println("Erro ao traduzir os arquivos VM: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
