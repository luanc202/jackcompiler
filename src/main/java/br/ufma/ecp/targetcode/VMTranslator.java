package br.ufma.ecp.targetcode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class VMTranslator {
    private CodeWriter codeWriter;

    public VMTranslator(String outputFile) throws IOException {
        codeWriter = new CodeWriter(outputFile);
    }

    public void translate(String vmCode) throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(vmCode));
        String line;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("//")) {
                continue;
            }

            String[] parts = line.split(" ");
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

                case "add":  
                case "sub":             
                case "neg":
                case "eq":
                    codeWriter.writeArithmetic(commandType);
                    break;

                default:
                    throw new UnsupportedOperationException("Comando desconhecido: " + commandType);
            }
        }

        codeWriter.close();
    }
}
