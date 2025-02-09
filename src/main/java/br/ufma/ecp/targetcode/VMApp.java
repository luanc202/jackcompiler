package br.ufma.ecp.targetcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class VMApp {

    public static void saveToFile(String fileName, String output) {
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(fileName, true); // Abre em modo append
            byte[] strToBytes = output.getBytes();
            outputStream.write(strToBytes);
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String fromFile(File file) {
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(file.toPath());
            return new String(bytes, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void main(String[] args) {
        String outputFile = "program.asm";

        if (args.length != 1) {
            System.err.println("Please provide a single directory path argument.");
            System.exit(1);
        }

        File file = new File(args[0]);

        if (!file.exists()) {
            System.err.println("The file doesn't exist.");
            System.exit(1);
        }

        // Processa todos os arquivos .vm na pasta fornecida
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                if (f.isFile() && f.getName().endsWith(".vm")) {
                    String inputFileName = f.getAbsolutePath();
                    System.out.println("Compiling " + inputFileName);
                    String programavm = fromFile(f);

                    // Passa o nome do arquivo para o tradutor para usar nas variáveis estáticas
                    try {
                        VMTranslator translator = new VMTranslator(outputFile, f.getName().replace(".vm", ""));
                        translator.translate(programavm);
                        System.out.println("Tradução concluída com sucesso! Arquivo gerado: " + outputFile);
                    } catch (IOException e) {
                        System.err.println("Erro ao traduzir o programa VM: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        } else {
            System.err.println("Please provide a directory containing .vm files.");
            System.exit(1);
        }
    }
}
