package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tifani on 10/1/2016.
 */
public class Tree {
    private ArrayList<Node> nodes = new ArrayList<>();
    private HashMap<Integer, ArrayList<Integer>> table = new HashMap<>();

    public Tree() {

    }

    public Tree(ArrayList<Node> nodes, HashMap<Integer, ArrayList<Integer>> table) {
        this.nodes = nodes;
        this.table = table;
    }

    public void addNode(Node node) {
        nodes.add(node);
        table.put(nodes.size(), new ArrayList<>());
        if (node.getParent() != -1) {
            ArrayList<Integer> value = table.get(node.getParent());
            value.add(nodes.size()-1);
            table.put(node.getParent(), value);
        }
    }

    public boolean isEmpty() {
        return nodes.isEmpty()&&table.isEmpty();
    }
}
