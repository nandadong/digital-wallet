import java.util.ArrayList;

public class User {
    private int id;
    private ArrayList<Integer> adjacencyList;
    private int degree;

    public User(int id) {
        this.id = id;
        adjacencyList = new ArrayList<Integer>();
        degree = 0;
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

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public int getDegree() {
        return degree;
    }
}