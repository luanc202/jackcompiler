package br.ufma.ecp.targetcode;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CodeWriter {
    private final BufferedWriter writer;

    public CodeWriter(String outputFile) throws IOException {
        writer = new BufferedWriter(new FileWriter(outputFile, true)); // Modo append
    }

    public String writePush(String segment, int index) throws IOException {
        String assemblyCode = switch (segment) {
            case "constant" -> "@" + index + "\nD=A\n@SP\nA=M\nM=D\n@SP\nM=M+1";
            case "local" -> "@LCL\nD=M\n@" + index + "\nA=D+A\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1";
            case "argument" -> "@ARG\nD=M\n@" + index + "\nA=D+A\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1";
            case "this" -> "@THIS\nD=M\n@" + index + "\nA=D+A\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1";
            case "that" -> "@THAT\nD=M\n@" + index + "\nA=D+A\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1";
            case "temp" -> "@" + (5 + index) + "\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1";
            case "pointer" -> "@" + (3 + index) + "\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1";
            case "static" -> "@Foo." + index + "\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1";
            default -> "";
        };
        return assemblyCode + "\n";
    }

    public String writePop(String segment, int index) throws IOException {
        String assemblyCode = switch (segment) {
            case "local" -> "@LCL\nD=M\n@" + index + "\nD=D+A\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D";
            case "argument" -> "@ARG\nD=M\n@" + index + "\nD=D+A\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D";
            case "this" -> "@THIS\nD=M\n@" + index + "\nD=D+A\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D";
            case "that" -> "@THAT\nD=M\n@" + index + "\nD=D+A\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D";
            default -> "";
        };
        return assemblyCode + "\n";
    }

    public String writeArithmetic(String command) throws IOException {
        String assemblyCode = switch (command) {
            case "add" -> "@SP\nAM=M-1\nD=M\nA=A-1\nM=M+D";
            case "sub" -> "@SP\nAM=M-1\nD=M\nA=A-1\nM=M-D";
            case "neg" -> "@SP\nA=M-1\nM=-M";
            case "eq" ->
                    "@SP\nAM=M-1\nD=M\nA=A-1\nD=M-D\n@TRUE\nD;JEQ\n@SP\nA=M-1\nM=0\n@END\n0;JMP\n(TRUE)\n@SP\nA=M-1\nM=-1\n(END)";
            default -> "";
        };
        return assemblyCode + "\n";
    }

    public String writeLabel(String label) throws IOException {
        return ("(" + label + ")\n");
    }

    public String writeGoto(String label) throws IOException {
        return ("@SP\nA=M-1\nD=M\n@" + label + "\nD;JNE\n@SP\nM=M-1\n");
    }

    public String writeIf(String label) throws IOException {
        return ("@SP\nAM=M-1\nD=M\n@" + label + "\nD;JNE\n");
    }


    public String writeCall(String functionName, int nArgs) throws IOException {
        StringBuilder assemblyCode = new StringBuilder();
        String returnLabel = "RETURN_" + functionName;
        assemblyCode.append("@").append(returnLabel).append("\nD=A\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
        for (String segment : new String[]{"LCL", "ARG", "THIS", "THAT"}) {
            assemblyCode.append("@").append(segment).append("\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
        }
        assemblyCode.append("@SP\nD=M\n@").append(nArgs + 5).append("\nD=D-A\n@ARG\nM=D\n");
        assemblyCode.append("@SP\nD=M\n@LCL\nM=D\n");

        assemblyCode.append("@").append(functionName).append("\n0;JMP\n");
        assemblyCode.append("(").append(returnLabel).append(")\n");

        return assemblyCode.toString();
    }


    public String writeFunction(String functionName, int nLocals) throws IOException {
        StringBuilder assemblyCode = new StringBuilder();
        assemblyCode.append("(" + functionName + ")\n");
        for (int i = 0; i < nLocals; i++) {
            assemblyCode.append("@SP\nA=M\nM=0\n@SP\nM=M+1\n");
        }
    
        return assemblyCode.toString();
    }

    public void close() throws IOException {
        writer.close();
    }
}
