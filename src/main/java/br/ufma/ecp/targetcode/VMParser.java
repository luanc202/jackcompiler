package br.ufma.ecp.targetcode;

import java.io.*;
import java.util.*;

public class VMParser {
    private BufferedReader reader;
    private String currentLine;
    private StringTokenizer tokenizer;

    public VMParser(String input) throws IOException {
        if (input.contains("\n") || input.contains("\r")) {
            // Trata entrada como string (programavm)
            reader = new BufferedReader(new StringReader(input));
        } else {
            // Trata entrada como caminho de arquivo
            reader = new BufferedReader(new FileReader(input));
        }
    }

    public boolean hasMoreCommands() throws IOException {
        currentLine = reader.readLine();
        return currentLine != null;
    }

    public void advance() {
        tokenizer = new StringTokenizer(currentLine);
    }

    public String commandType() {
        if (tokenizer.hasMoreTokens()) {
            String command = tokenizer.nextToken();
            switch (command) {
                case "push":
                    return "C_PUSH";
                case "pop":
                    return "C_POP";
                case "add":
                case "sub":
                case "neg":
                case "eq":
                case "gt":
                case "lt":
                case "and":
                case "or":
                case "not":
                    return "C_ARITHMETIC";
                default:
                    return "UNKNOWN";
            }
        }
        return null;
    }

    public String arg1() {
        if (commandType().equals("C_ARITHMETIC")) {
            return tokenizer.nextToken();
        } else if (tokenizer.hasMoreTokens()) {
            return tokenizer.nextToken();
        }
        return null;
    }

    public int arg2() {
        if (tokenizer.hasMoreTokens()) {
            return Integer.parseInt(tokenizer.nextToken());
        }
        return 0;
    }

    public void close() throws IOException {
        reader.close();
    }
}
