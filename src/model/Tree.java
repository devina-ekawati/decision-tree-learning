package model;

import weka.core.Attribute;

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

    public HashMap<Integer, ArrayList<Integer>> getTable() {
        return table;
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public void addNode(Node node, Double childValue) {
        nodes.add(node);

        table.put(nodes.size() - 1, new ArrayList<>());
        if (node.getParent() != -1) {
            ArrayList<Integer> value = table.get(node.getParent());
            value.add(nodes.size()-1);
            table.put(node.getParent(), value);
            // nodes.get(node.getParent()).addChild(childValue, node.getName());
        }
    }

    public Node getNode(Integer index) {
        return nodes.get(index);
    }

    public int getLastNode() {
        return nodes.size()-1;
    }

    public boolean isEmpty() {
        return nodes.isEmpty()&&table.isEmpty();
    }

    public void print(ArrayList<Attribute> attributes) {
        for (Map.Entry<Integer, ArrayList<Integer>> entry: table.entrySet()){
            nodes.get(entry.getKey()).print(attributes);
        }
    }
}
