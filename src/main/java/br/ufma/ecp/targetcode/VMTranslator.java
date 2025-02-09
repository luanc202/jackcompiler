package br.ufma.ecp.targetcode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class VMTranslator {
    private CodeWriter codeWriter;

    public VMTranslator(String outputFile) throws IOException {
        codeWriter = new CodeWriter(outputFile);
    }

    public String translate(String vmCode) throws IOException {
        StringBuilder assemblyCode = new StringBuilder();
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
                    assemblyCode.append(codeWriter.writePush(segment, index));
                    break;

                case "pop":
                    if (parts.length != 3) {
                        throw new IllegalArgumentException("Comando pop inválido: " + line);
                    }
                    segment = parts[1];
                    index = Integer.parseInt(parts[2]);
                    assemblyCode.append(codeWriter.writePop(segment, index));
                    break;
                case "label":
                    if (parts.length != 2) {
                        throw new IllegalArgumentException("Comando label inválido: " + line);
                    }
                    assemblyCode.append(codeWriter.writeLabel(parts[1]));
                    break;
                case "goto":
                    if (parts.length != 2) {
                        throw new IllegalArgumentException("Comando goto inválido: " + line);
                    }
                    assemblyCode.append(codeWriter.writeGoto(parts[1]));
                    break;
                case "if-goto":
                    if (parts.length != 2) {
                        throw new IllegalArgumentException("Comando if-goto inválido: " + line);
                    }
                    assemblyCode.append(codeWriter.writeIf(parts[1]));
                    break;
                case "add":
                case "sub":
                case "neg":
                case "eq":
                    assemblyCode.append(codeWriter.writeArithmetic(commandType));
                    break;

                case "call":
                    if (parts.length != 3) {
                        throw new IllegalArgumentException("Comando call inválido: " + line);
                    }
                    String functionName = parts[1];
                    int nArgs = Integer.parseInt(parts[2]);
                    assemblyCode.append(codeWriter.writeCall(functionName, nArgs));
                    break;

                case "function":
                    if (parts.length != 3) {
                        throw new IllegalArgumentException("Comando function inválido: " + line);
                    }
                    functionName = parts[1];
                    nArgs = Integer.parseInt(parts[2]);
                    assemblyCode.append(codeWriter.writeFunction(functionName, nArgs));
                    break;

                default:
                    throw new UnsupportedOperationException("Comando desconhecido: " + commandType);
            }
        }

        return assemblyCode.toString();
    }
}
