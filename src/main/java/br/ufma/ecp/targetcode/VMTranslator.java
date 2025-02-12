package br.ufma.ecp.targetcode;

import java.io.*;

public class VMTranslator {
    private final CodeWriter codeWriter;

    public VMTranslator(String outputFile) throws IOException {
        this.codeWriter = new CodeWriter(outputFile);
    }

    public void translateFile(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            codeWriter.setFileName(file.getName().replace(".vm", ""));
            String line;

            /* codeWriter.writeInit(); */
            while ((line = reader.readLine()) != null) {
                line = cleanLine(line);
                if (!line.isEmpty()) {
                    processCommand(line);
                }
            }
        }
    }

    public void translateDirectory(File directory) throws IOException {
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".vm"));
        if (files != null) {
            for (File file : files) {
                translateFile(file);
            }
        }
        codeWriter.close();
    }

    private void processCommand(String line) throws IOException {
        String[] parts = line.split("\\s+");
        String commandType = parts[0];

        switch (commandType) {
            case "push":
                validateCommand(parts, 3, "push");
                codeWriter.writePush(parts[1], Integer.parseInt(parts[2]));
                break;
            case "pop":
                validateCommand(parts, 3, "pop");
                codeWriter.writePop(parts[1], Integer.parseInt(parts[2]));
                break;
            case "label":
                validateCommand(parts, 2, "label");
                codeWriter.writeLabel(parts[1]);
                break;
            case "goto":
                validateCommand(parts, 2, "goto");
                codeWriter.writeGoto(parts[1]);
                break;
            case "if-goto":
                validateCommand(parts, 2, "if-goto");
                codeWriter.writeIf(parts[1]);
                break;
            case "call":
                validateCommand(parts, 3, "call");
                codeWriter.writeCall(parts[1], Integer.parseInt(parts[2]));
                break;
            case "function":
                validateCommand(parts, 3, "function");
                codeWriter.writeFunction(parts[1], Integer.parseInt(parts[2]));
                break;
            case "return":
                codeWriter.writeReturn();
                break;
            default:
                codeWriter.writeArithmetic(commandType);
        }
    }

    private String cleanLine(String line) {
        return line.split("//")[0].trim(); // Remove comentários e espaços extras
    }

    private void validateCommand(String[] parts, int expectedLength, String command) {
        if (parts.length != expectedLength) {
            throw new IllegalArgumentException("Comando " + command + " inválido: " + String.join(" ", parts));
        }
    }

}