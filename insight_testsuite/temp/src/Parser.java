import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;

public class Parser {
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private BufferedWriter backupWriter;
    private final int MAX_DEGREE = 4;
    // Parser constructor, used for feature 1 case
    public Parser(BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        this.bufferedReader = bufferedReader;
        this.bufferedWriter = bufferedWriter;
    }
    // Overload the constructor, used for feature 2 & 3 case
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
                // Pass the title line, and any possible lines that are empty
                if (line.startsWith("time") || line.isEmpty()) {
                    continue;
                }
                // Extract payer and receiver id
                items = line.split(", ");
                int id1 = Integer.parseInt(items[1]);
                int id2 = Integer.parseInt(items[2]);

                if (map.containsKey(id1)) {
                    // If payer is already in the user map
                    User payer = map.get(id1);
                    ArrayList<Integer> adjacencyList = payer.getAdjacencyList();
                    // If receiver is in payer's friend list,
                    // write result "trusted" into output1 and parse next record
                    if (adjacencyList.contains(id2)) {
                        bufferedWriter.write("trusted");
                    }
                    // If receiver is not in payer's friend list,
                    // write result "unverified" into output1,
                    // add receiver to payer's friend list and parse next record
                    else {
                        bufferedWriter.write("unverified");
                        payer.addIdToAdjacencyList(id2);
                        map.put(id1, payer);
                    }
                }
                // If payer is not yet in the user map,
                // write result "unverified" into output1, add payer to user map
                // and add receiver to payer's friend list
                else {
                    bufferedWriter.write("unverified");
                    User payer = new User(id1);
                    payer.addIdToAdjacencyList(id2);
                    map.put(id1, payer);
                }
                // If receiver is already in the user map
                if (map.containsKey(id2)) {
                    User receiver = map.get(id2);
                    ArrayList<Integer> adjacencyList = receiver.getAdjacencyList();
                    // And payer is not in receiver's friend list,
                    // add payer to receiver's friend list
                    if (!adjacencyList.contains(id1)) {
                        receiver.addIdToAdjacencyList(id1);
                    }
                }
                // If receiver is not in the user map,
                // add receiver to the user map and add payer to receiver's friend list
                else {
                    User receiver = new User(id2);
                    receiver.addIdToAdjacencyList(id1);
                    map.put(id2, receiver);
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

                // Feature 2 & 3

                // Feature 2 is just to check is receiver is at least a friend of friend of payer, the idea is
                    // 1. Check if receiver is in payer's friend list
                    // 2. If not, check if receiver is in any of payer's friends' friend lists
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

                bufferedWriter.newLine();

                // Feature 3
                // Feature 3 is to apply BFS to find if receiver is within 4th degree connection of payer
                // A HashSet is used to record visited users in the user map, avoid searching in infinite loops
                // Future work: If I have more time to work on this problem, I'll try to use a bi-directional BFS
                // to make the search more efficient, i.e. start BFS from the payer and receiver simultaneously,
                // and check if they have a same friend in the middle of network
                HashSet<Integer> visited = new HashSet<Integer>();
                visited.add(id1);
                Queue<Integer> queue = new LinkedList<Integer>();
                queue.offer(id1);

                while (!queue.isEmpty()) {
                    int size = queue.size();
                    for (int i = 0; i < size; i++) {
                        int userId = queue.poll();
                        if (userId == id2) {
                            backupWriter.write("trusted");
                            backupWriter.newLine();
                            continue outerloop;
                        }
                        User user = map.get(userId);
                        int degree = user.getDegree();
                        if (degree == MAX_DEGREE) {
                            continue;
                        }

                        ArrayList<Integer> userAdjacencyList = user.getAdjacencyList();
                        if (userAdjacencyList.contains(id2)) {
                            backupWriter.write("trusted");
                            backupWriter.newLine();
                            continue outerloop;
                        }
                        for (int userAdjacency : userAdjacencyList) {
                            if (visited.contains(userAdjacency)) {
                                continue;
                            }
                            User friend = map.get(userAdjacency);
                            friend.setDegree(degree+1);
                            map.put(userAdjacency, friend);
                            queue.offer(userAdjacency);
                            visited.add(userAdjacency);
                        }
                    }
                }
                backupWriter.write("unverified");
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