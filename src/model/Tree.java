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
        System.out.println(node.getName());
        nodes.add(node);

        table.put(nodes.size() - 1, new ArrayList<>());
        if (node.getParent() != -1) {
            ArrayList<Integer> value = table.get(node.getParent());
            value.add(nodes.size()-1);
            table.put(node.getParent(), value);
        }
    }

    public int getLastNode() {
        return nodes.size()-1;
    }

    public boolean isEmpty() {
        return nodes.isEmpty()&&table.isEmpty();
    }

    public void print() {
        for (Map.Entry<Integer, ArrayList<Integer>> entry: table.entrySet()){
            nodes.get(entry.getKey()).print();
            for (int i = 0; i < entry.getValue().size(); i++) {
                System.out.println("\t" + nodes.get(entry.getValue().get(i)).getName());
            }
        }
    }
}
