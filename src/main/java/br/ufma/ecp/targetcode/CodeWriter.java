package br.ufma.ecp.targetcode;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CodeWriter {
    private BufferedWriter writer;

    public CodeWriter(String outputFile) throws IOException {
        writer = new BufferedWriter(new FileWriter(outputFile));
    }

    public void writePush(String segment, int index) throws IOException {
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
        writer.write(assemblyCode + "\n");
    }
    
    public void writePop(String segment, int index) throws IOException {
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
            case "temp":
                assemblyCode = "@SP\nAM=M-1\nD=M\n@" + (5 + index) + "\nM=D";
                break;
            case "pointer":
                assemblyCode = "@SP\nAM=M-1\nD=M\n@" + (3 + index) + "\nM=D";
                break;
            case "static":
                assemblyCode = "@SP\nAM=M-1\nD=M\n@Foo." + index + "\nM=D";
                break;
        }
        writer.write(assemblyCode + "\n");
    }
    

    public void writeArithmetic(String command) throws IOException {
        String assemblyCode = "";
        switch (command) {
            case "add":
                assemblyCode = "@SP\nAM=M-1\nD=M\nA=A-1\nM=D+M";
                break;
            case "sub":
                assemblyCode = "@SP\nAM=M-1\nD=M\nA=A-1\nM=M-D";
                break;
            case "neg":
                assemblyCode = "@SP\nAM=M-1\nM=-M";
                break;
            case "eq":
                assemblyCode = "@SP\nAM=M-1\nD=M\nA=A-1\nD=M-D\n@TRUE\nD;JEQ\n@SP\nA=M\nM=0\n@END\n0;JMP\n(TRUE)\n@SP\nA=M\nM=-1\n(END)";
                break;
        }
        writer.write(assemblyCode + "\n");
    }

    public void close() throws IOException {
        writer.close();
    }
}
