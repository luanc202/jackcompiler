package br.ufma.ecp.targetcode;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CodeWriter {
    private BufferedWriter writer;

    public CodeWriter(String outputFile) throws IOException {
        writer = new BufferedWriter(new FileWriter(outputFile, true)); // Modo append
    }

    public String writePush(String segment, int index) throws IOException {
        String assemblyCode = "";
        switch (segment) {
            case "constant":
                assemblyCode = "@" + index + "\nD=A\n@SP\nA=M\nM=D\n@SP\nM=M+1";
                break;
            case "local":
                assemblyCode = "@LCL\nD=M\n@" + index + "\nA=D+A\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1";
                break;
            case "argument":
                assemblyCode = "@ARG\nD=M\n@" + index + "\nA=D+A\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1";
                break;
            case "this":
                assemblyCode = "@THIS\nD=M\n@" + index + "\nA=D+A\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1";
                break;
            case "that":
                assemblyCode = "@THAT\nD=M\n@" + index + "\nA=D+A\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1";
                break;
            case "temp":
                assemblyCode = "@" + (5 + index) + "\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1";
                break;
            case "pointer":
                assemblyCode = "@" + (3 + index) + "\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1";
                break;
            case "static":
                assemblyCode = "@Foo." + index + "\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1";
                break;
        }
        return assemblyCode + "\n";
    }

    public String writePop(String segment, int index) throws IOException {
        String assemblyCode = "";
        switch (segment) {
            case "local":
                assemblyCode = "@LCL\nD=M\n@" + index + "\nD=D+A\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D";
                break;
            case "argument":
                assemblyCode = "@ARG\nD=M\n@" + index + "\nD=D+A\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D";
                break;
            case "this":
                assemblyCode = "@THIS\nD=M\n@" + index + "\nD=D+A\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D";
                break;
            case "that":
                assemblyCode = "@THAT\nD=M\n@" + index + "\nD=D+A\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D";
                break;
        }
        return assemblyCode + "\n";
    }

    public String writeArithmetic(String command) throws IOException {
        String assemblyCode = "";
        switch (command) {
            case "add":
                assemblyCode = "@SP\nAM=M-1\nD=M\nA=A-1\nM=M+D";
                break;
            case "sub":
                assemblyCode = "@SP\nAM=M-1\nD=M\nA=A-1\nM=M-D";
                break;
            case "neg":
                assemblyCode = "@SP\nA=M-1\nM=-M";
                break;
            case "eq":
                assemblyCode = "@SP\nAM=M-1\nD=M\nA=A-1\nD=M-D\n@TRUE\nD;JEQ\n@SP\nA=M-1\nM=0\n@END\n0;JMP\n(TRUE)\n@SP\nA=M-1\nM=-1\n(END)";
                break;
        }
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
        assemblyCode.append("@" + returnLabel + "\nD=A\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
        for (String segment : new String[]{"LCL", "ARG", "THIS", "THAT"}) {
            assemblyCode.append("@" + segment + "\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
        }
        assemblyCode.append("@SP\nD=M\n@" + (nArgs + 5) + "\nD=D-A\n@ARG\nM=D\n");
        assemblyCode.append("@SP\nD=M\n@LCL\nM=D\n");

        assemblyCode.append("@" + functionName + "\n0;JMP\n");
        assemblyCode.append("(" + returnLabel + ")\n");

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
