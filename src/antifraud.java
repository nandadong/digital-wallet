// example of program that detects suspicious transactions
// fraud detection algorithm

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.HashMap;

public class antifraud {
    private static BufferedReader bufferedReader;
    private static BufferedWriter bufferedWriter;
    private static BufferedWriter backupWriter;

    public static void main(String[] args) {
        bufferedReader = null;
        bufferedWriter = null;
        backupWriter = null;

        if (args.length < 5) {
            System.out.println("Command line: java antifraud <batch payment file name> <stream payment file name> <output1> <output2> <output3>");
            System.exit(0);
        }

        // Create parser for feature 1
        try {
            bufferedReader = new BufferedReader(new FileReader(args[0]));
        }
        catch (FileNotFoundException fileNotFoundException) {
            System.err.println("Cannot find file: " + args[0]);
            System.exit(1);
        }

        try {
            bufferedWriter = new BufferedWriter(new FileWriter(args[2], true));
        }
        catch (IOException ioException) {
            System.err.println("Cannot create: " + args[2]);
            System.exit(1);
        }

        HashMap<Integer, User> map = new HashMap<Integer, User>();

        Parser batchParser = new Parser(bufferedReader, bufferedWriter);
        batchParser.parseBatch(map);

        // Create parser for feature 2
        try {
            bufferedReader = new BufferedReader(new FileReader(args[1]));
        }
        catch (FileNotFoundException fileNotFoundException) {
            System.err.println("Cannot find file: " + args[1]);
            System.exit(1);
        }

        try {
            bufferedWriter = new BufferedWriter(new FileWriter(args[3], true));
        }
        catch (IOException ioException) {
            System.err.println("Cannot create: " + args[3]);
            System.exit(1);
        }

        try {
            backupWriter = new BufferedWriter(new FileWriter(args[4], true));
        }
        catch (IOException ioException) {
            System.err.println("Cannot create: " + args[4]);
            System.exit(1);
        }

        Parser streamParser = new Parser(bufferedReader, bufferedWriter, backupWriter);
        streamParser.parseStream(map);
    }
}