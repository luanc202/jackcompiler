package br.ufma.ecp;

import java.io.IOException;

public class VMTranslator {
    private VMParser parser;
    private CodeWriter codeWriter;

    public VMTranslator(String inputFile, String outputFile) throws IOException {
        parser = new VMParser(inputFile);
        codeWriter = new CodeWriter(outputFile);
    }

    public void translate() throws IOException {
        while (parser.hasMoreCommands()) {
            parser.advance();
            String commandType = parser.commandType();
            if (commandType.equals("C_PUSH")) {
                String segment = parser.arg1();
                int index = parser.arg2();
                codeWriter.writePush(segment, index);
            } else if (commandType.equals("C_POP")) {
                String segment = parser.arg1();
                int index = parser.arg2();
                codeWriter.writePush(segment, index);
            } else if (commandType.equals("C_ARITHMETIC")) {
                String command = parser.arg1();
                codeWriter.writeArithmetic(command);
            }
        }
        codeWriter.close();
        parser.close();
    }
}
