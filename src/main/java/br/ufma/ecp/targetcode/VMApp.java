package br.ufma.ecp.targetcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class VMApp {

    public static void saveToFile(String fileName, String output) {
        try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
            byte[] strToBytes = output.getBytes();
            outputStream.write(strToBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String fromFile(File file) {
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(file.toPath());
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void main(String[] args) {
        String outputFile = "program.asm";
        StringBuilder fullAssemblyCode = new StringBuilder();

        if (args.length != 1) {
            System.err.println("Please provide a single file path argument.");
            System.exit(1);
        }

        File file = new File(args[0]);
        if (!file.exists()) {
            System.err.println("The file doesn't exist.");
            System.exit(1);
        }

        // Adicionando a inicialização ao arquivo de saída
        fullAssemblyCode.append("// Bootstrap\n");
        fullAssemblyCode.append("@256\nD=A\n@SP\nM=D\n"); // Inicializa o stack pointer
        fullAssemblyCode.append("@Sys.init\n0;JMP\n"); // Chama a função sys.init

        // Compilando cada arquivo .vm da pasta
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                if (f.isFile() && f.getName().endsWith(".vm")) {
                    System.out.println("compiling " + f.getAbsolutePath());
                    String programavm = fromFile(f);
                    try {
                        VMTranslator translator = new VMTranslator(outputFile);
                        String translatedCode = translator.translate(programavm);
                        fullAssemblyCode.append(translatedCode);
                    } catch (IOException e) {
                        System.err.println("Erro ao traduzir o programa VM: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        } else if (file.isFile()) {
            if (!file.getName().endsWith(".vm")) {
                System.err.println("Please provide a file name ending with .vm");
                System.exit(1);
            }
            System.out.println("compiling " + file.getAbsolutePath());
            String programavm = fromFile(file);
            try {
                VMTranslator translator = new VMTranslator(outputFile);
                String translatedCode = translator.translate(programavm);
                fullAssemblyCode.append(translatedCode);
            } catch (IOException e) {
                System.err.println("Erro ao traduzir o programa VM: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Salva todo o código gerado no arquivo de saída
        saveToFile(outputFile, fullAssemblyCode.toString());
        System.out.println("Tradução concluída com sucesso! Arquivo gerado: " + outputFile);
    }
}
