import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;

public class Parser {
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private BufferedWriter backupWriter;

    public Parser(BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        this.bufferedReader = bufferedReader;
        this.bufferedWriter = bufferedWriter;
    }
    public Parser(BufferedReader bufferedReader, BufferedWriter bufferedWriter, BufferedWriter backupWriter) {
        this.bufferedReader = bufferedReader;
        this.bufferedWriter = bufferedWriter;
        this.backupWriter = backupWriter;
    }

    // Feature 1, load batch payment file, verify if user has previous payment to another user
    public void parseBatch(HashMap<Integer, User> map) {
        String line;
        String[] items;

        try {
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("time") || line.isEmpty()) {
                    continue;
                }

                items = line.split(", ");
                int id1 = Integer.parseInt(items[1]);
                int id2 = Integer.parseInt(items[2]);

                if (map.containsKey(id1)) {
                    User payer = map.get(id1);
                    ArrayList<Integer> adjacencyList = payer.getAdjacencyList();

                    if (adjacencyList.contains(id2)) {
                        bufferedWriter.write("trusted");
                    }
                    else {
                        bufferedWriter.write("unverified");
                        payer.addIdToAdjacencyList(id2);
                        map.put(id1, payer);
                    }
                }
                else {
                    bufferedWriter.write("unverified");
                    User payer = new User(id1);
                    payer.addIdToAdjacencyList(id2);
                    map.put(id1, payer);
                }

                bufferedWriter.newLine();
            }
            bufferedReader.close();
            bufferedWriter.close();
        } catch (IOException ioException) {
            System.err.println("Cannot read batch payment file.");
        }

    }

    // Feature 2 & 3, load stream payment file, verify if user has 2nd/4th degree
    public void parseStream(HashMap<Integer, User> map) {
        String line;
        String[] items;

        try {
            outerloop:
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("time") || line.isEmpty()) {
                    continue;
                }

                items = line.split(", ");
                int id1 = Integer.parseInt(items[1]);
                int id2 = Integer.parseInt(items[2]);

                // Feature 2
                User payer = map.get(id1);
                ArrayList<Integer> adjacencyList = payer.getAdjacencyList();
                if (adjacencyList.contains(id2)) {
                    bufferedWriter.write("trusted");
                }
                else {
                    for (int adjacency : adjacencyList) {
                        User friend = map.get(adjacency);
                        ArrayList<Integer> friendAdjacencyList = friend.getAdjacencyList();
                        if (friendAdjacencyList.contains(id2)) {
                            bufferedWriter.write("trusted");
                            bufferedWriter.newLine();
                            continue outerloop;
                        }
                    }
                    bufferedWriter.write("unverified");
                }
                //TODO: Feature 3

                bufferedWriter.newLine();
                backupWriter.newLine();
            }
            bufferedReader.close();
            bufferedWriter.close();
            backupWriter.close();
        } catch (IOException ioException) {
            System.err.println("Cannot read stream payment file.");
        }
    }

}