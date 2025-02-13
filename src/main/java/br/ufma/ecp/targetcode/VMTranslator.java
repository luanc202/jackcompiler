package br.ufma.ecp.targetcode;

import java.io.*;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.IOException;

public class VMTranslator {
    private CodeWriter codeWriter;

    public VMTranslator(String outputFile) throws IOException {
        codeWriter = new CodeWriter(outputFile);
    }

    public void translateDirectory(File directory) throws IOException {
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".vm"));
        if (files != null) {

            boolean hasSysInit = false;

            for (File file : files) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.split("//")[0].trim(); // Remove comentários
                        if (line.startsWith("function Sys.init")) {
                            hasSysInit = true;
                            break;
                        }
                    }
                }
                if (hasSysInit) break;
            }

            if (hasSysInit) {
                codeWriter.writeInit();
            }


            for (File file : files) {
                try (BufferedReader reader = new BufferedReader(new StringReader(fromFile(file)))) {
                    codeWriter.setFileName(file.getName().replace(".vm", ""));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.split("//")[0].trim(); // Remove comments and trim
                        if (line.isEmpty()) {
                            continue;
                        }
                        String[] parts = line.split("\\s+"); // Use regex to handle multiple spaces
                        String commandType = parts[0];
                        if (commandType == null || commandType.isEmpty()) {
                            throw new IllegalArgumentException("Comando inválido ou ausente: " + line);
                        }
                        switch (commandType) {
                            case "push":
                                if (parts.length != 3) {
                                    throw new IllegalArgumentException("Comando push inválido: " + line);
                                }
                                String segment = parts[1];
                                int index = Integer.parseInt(parts[2]);
                                codeWriter.writePush(segment, index);
                                break;
                            case "pop":
                                if (parts.length != 3) {
                                    throw new IllegalArgumentException("Comando pop inválido: " + line);
                                }
                                segment = parts[1];
                                index = Integer.parseInt(parts[2]);
                                codeWriter.writePop(segment, index);
                                break;
                            case "label":
                                if (parts.length != 2) {
                                    throw new IllegalArgumentException("Comando label inválido: " + line);
                                }
                                codeWriter.writeLabel(parts[1]);
                                break;
                            case "goto":
                                if (parts.length != 2) {
                                    throw new IllegalArgumentException("Comando goto inválido: " + line);
                                }
                                codeWriter.writeGoto(parts[1]);
                                break;
                            case "if-goto":
                                if (parts.length != 2) {
                                    throw new IllegalArgumentException("Comando if-goto inválido: " + line);
                                }
                                codeWriter.writeIf(parts[1]);
                                break;
                            case "add":
                            case "sub":
                            case "neg":
                            case "eq":
                            case "lt":
                            case "gt":
                            case "and":
                            case "or":
                            case "not":
                                codeWriter.writeArithmetic(commandType);
                                break;
                            case "call":
                                if (parts.length != 3) {
                                    throw new IllegalArgumentException("Comando call inválido: " + line);
                                }
                                String functionName = parts[1];
                                int nArgs = Integer.parseInt(parts[2]);
                                codeWriter.writeCall(functionName, nArgs);
                                break;
                            case "function":
                                if (parts.length != 3) {
                                    throw new IllegalArgumentException("Comando function inválido: " + line);
                                }
                                functionName = parts[1];
                                int nLocals = Integer.parseInt(parts[2]);
                                codeWriter.writeFunction(functionName, nLocals);
                                break;
                            case "return":
                                codeWriter.writeReturn();
                                break;
                            default:
                                throw new UnsupportedOperationException("Comando desconhecido: " + commandType);
                        }
                    }
                }
            }
            codeWriter.close();
        }
    }

    private String fromFile(File file) {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new java.io.FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }
}