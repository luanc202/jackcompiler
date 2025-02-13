package br.ufma.ecp.targetcode;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CodeWriter {
    private final BufferedWriter writer;
    private int labelCounter = 0;
    private String fileName;

    public CodeWriter(String outputFile) throws IOException {
        writer = new BufferedWriter(new FileWriter(outputFile, false)); // Mode overwrite
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void writeInit() throws IOException {
        writer.write("@256\nD=A\n@SP\nM=D\n"); // Set SP to 256
        writeCall("Sys.init", 0); // Call Sys.init
    }

    public void writePush(String segment, int index) throws IOException {
        String assemblyCode = switch (segment) {
            case "constant" -> "@" + index + "\nD=A\n@SP\nA=M\nM=D\n@SP\nM=M+1";
            case "local" -> "@LCL\nD=M\n@" + index + "\nA=D+A\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1";
            case "argument" -> "@ARG\nD=M\n@" + index + "\nA=D+A\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1";
            case "this" -> "@THIS\nD=M\n@" + index + "\nA=D+A\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1";
            case "that" -> "@THAT\nD=M\n@" + index + "\nA=D+A\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1";
            case "temp" -> "@" + (5 + index) + "\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1";
            case "pointer" -> "@" + (3 + index) + "\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1";
            case "static" -> "@" + fileName + "." + index + "\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1";
            default -> "";
        };
        writer.write(assemblyCode + "\n");
    }

    public void writePop(String segment, int index) throws IOException {
        String assemblyCode = switch (segment) {
            case "local" -> "@LCL\nD=M\n@" + index + "\nD=D+A\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D";
            case "argument" -> "@ARG\nD=M\n@" + index + "\nD=D+A\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D";
            case "this" -> "@THIS\nD=M\n@" + index + "\nD=D+A\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D";
            case "that" -> "@THAT\nD=M\n@" + index + "\nD=D+A\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D";
            case "temp" -> "@SP\nAM=M-1\nD=M\n@" + (5 + index) + "\nM=D";
            case "pointer" -> "@SP\nAM=M-1\nD=M\n@" + (3 + index) + "\nM=D";
            case "static" -> "@SP\nAM=M-1\nD=M\n@" + fileName + "." + index + "\nM=D";
            default -> "";
        };
        writer.write(assemblyCode + "\n");
    }

    public void writeArithmetic(String command) throws IOException {
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
                assemblyCode = "@SP\nAM=M-1\nD=M\nA=A-1\nD=M-D\n@EQ" + labelCounter + "\nD;JEQ\n@SP\nA=M-1\nM=0\n@END" + labelCounter + "\n0;JMP\n(EQ" + labelCounter + ")\n@SP\nA=M-1\nM=-1\n(END" + labelCounter + ")";
                labelCounter++;
                break;
            case "gt":
                assemblyCode = "@SP\nAM=M-1\nD=M\nA=A-1\nD=M-D\n@GT" + labelCounter + "\nD;JGT\n@SP\nA=M\nM=0\n@END" + labelCounter + "\n0;JMP\n(GT" + labelCounter + ")\n@SP\nA=M\nM=-1\n(END" + labelCounter + ")";
                labelCounter++;
                break;
            case "lt":
                assemblyCode = "@SP\nAM=M-1\nD=M\nA=A-1\nD=M-D\n@LT" + labelCounter + "\nD;JLT\n@SP\nA=M\nM=0\n@END" + labelCounter + "\n0;JMP\n(LT" + labelCounter + ")\n@SP\nA=M\nM=-1\n(END" + labelCounter + ")";
                labelCounter++;
                break;
            case "and":
                assemblyCode = "@SP\nAM=M-1\nD=M\nA=A-1\nM=D&M";
                break;
            case "or":
                assemblyCode = "@SP\nAM=M-1\nD=M\nA=A-1\nM=D|M";
                break;
            case "not":
                assemblyCode = "@SP\nA=M-1\nM=!M";
                break;
        }
        writer.write(assemblyCode + "\n");
    }

    public void writeLabel(String label) throws IOException {
        writer.write("(" + fileName + "$" + label.toUpperCase() + ")\n");
    }

    public void writeGoto(String label) throws IOException {
        writer.write("@" + fileName + "$" + label.toUpperCase() + "\n0;JMP\n");
    }

    public void writeIf(String label) throws IOException {
        writer.write("@SP\nAM=M-1\nD=M\n@" + fileName + "$" + label.toUpperCase() + "\nD;JNE\n");
    }

    public void writeCall(String functionName, int nArgs) throws IOException {
        String returnLabel = "RETURN$" + functionName + "$" + labelCounter;
        labelCounter++;

        writer.write("@" + returnLabel + "\nD=A\n@SP\nA=M\nM=D\n@SP\nM=M+1\n"); // Push return address
        writer.write("@LCL\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n"); // Push LCL
        writer.write("@ARG\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n"); // Push ARG
        writer.write("@THIS\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n"); // Push THIS
        writer.write("@THAT\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n"); // Push THAT

        writer.write("@" + nArgs + "\nD=A\n@SP\nD=M-D\n@5\nD=D-A\n@ARG\nM=D\n"); // ARG = SP - nArgs - 5
        writer.write("@SP\nD=M\n@LCL\nM=D\n"); // LCL = SP
        writer.write("@" + functionName + "\n0;JMP\n"); // Jump to function
        writer.write("(" + returnLabel + ")\n"); // Return address label
    }

    public void writeFunction(String functionName, int nLocals) throws IOException {
        writer.write("(" + functionName + ")\n");
        for (int i = 0; i < nLocals; i++) {
            writer.write("@0\nD=A\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
        }
    }

    public void writeReturn() throws IOException {
        writer.write("@LCL\nD=M\n@R13\nM=D\n"); // R13 = FRAME (FRAME is LCL)
        writer.write("@5\nA=D-A\nD=M\n@R14\nM=D\n"); // R14 = RET (RET is FRAME - 5)

        writer.write("@SP\nAM=M-1\nD=M\n@ARG\nA=M\nM=D\n"); // *ARG = pop()
        writer.write("@ARG\nD=M+1\n@SP\nM=D\n"); // SP = ARG + 1

        writer.write("@R13\nA=M-1\nD=M\n@THAT\nM=D\n"); // THAT = *(FRAME - 1)
        writer.write("@2\nD=A\n@R13\nA=M-D\nD=M\n@THIS\nM=D\n"); // THIS = *(FRAME - 2)
        writer.write("@3\nD=A\n@R13\nA=M-D\nD=M\n@ARG\nM=D\n"); // ARG = *(FRAME - 3)
        writer.write("@4\nD=A\n@R13\nA=M-D\nD=M\n@LCL\nM=D\n"); // LCL = *(FRAME - 4)
        writer.write("@R14\nA=M\n0;JMP\n"); // Jump to RET
    }

    public void close() throws IOException {
        writer.close();
    }
}
