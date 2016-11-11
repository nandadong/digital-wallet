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
    // Define BufferedReader and BufferedWriter to parse info from payment file and write result into output files
    // bufferedReader is for reading payment info from batch_payment and stream_payment
    // bufferedWriter is used to write results into output1 and output2
    // backupWriter is to write results into output3
    private static BufferedReader bufferedReader;
    private static BufferedWriter bufferedWriter;
    private static BufferedWriter backupWriter;

    public static void main(String[] args) {
        // Initialize bufferedReader, bufferedWriter and backupWriter as null
        bufferedReader = null;
        bufferedWriter = null;
        backupWriter = null;
        // Check if argument is valid
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
        // Create a HashMap object to store the all the users by <UserID, User> pair
        // Feature 1 is to build this user graph and feature 2 & 3 use it
        HashMap<Integer, User> map = new HashMap<Integer, User>();

        Parser batchParser = new Parser(bufferedReader, bufferedWriter);
        batchParser.parseBatch(map);

        // Create parser for feature 2 & 3
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