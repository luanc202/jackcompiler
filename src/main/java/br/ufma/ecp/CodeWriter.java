package br.ufma.ecp;

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
            case "argument":
            case "this":
            case "that":
                assemblyCode = "@" + segment + "\nD=M\n@" + index + "\nA=D+A\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1";
                break;
            case "static":
                assemblyCode = "@Foo." + index + "\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1";
                break;
            // Adicione mais segmentos conforme necessário
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
            // Adicione mais operações aritméticas e lógicas conforme necessário
        }
        writer.write(assemblyCode + "\n");
    }

    public void close() throws IOException {
        writer.close();
    }
}
