import java.util.ArrayList;

public class User {
    private int id;
    private ArrayList<Integer> adjacencyList;

    public User(int id) {
        this.id = id;
        adjacencyList = new ArrayList<Integer>();
    }

    public int getId() {
        return id;
    }

    public ArrayList<Integer> getAdjacencyList() {
        return adjacencyList;
    }

    public void addIdToAdjacencyList(int userId) {
        adjacencyList.add(userId);
    }
}