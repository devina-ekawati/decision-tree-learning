package model;

import weka.core.Attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tifani on 10/1/2016.
 */
public class Tree {
    private HashMap<Integer, Node> table = new HashMap<>();
    private int idx = 0;

    public Tree() {

    }

    public HashMap<Integer, Node> getTable() {
        return table;
    }

    public int getLastIdx() {
        return idx-1;
    }

    public void addNode(Node node, Double childValue) {
        table.put(idx, node);

        if (node.getParent() != -1) {
            table.get(node.getParent()).addChild(childValue, idx);
        }
        idx++;
    }

    public Node getNode(Integer index) {
        return table.get(index);
    }

    public void print(ArrayList<Attribute> attributes) {
        int rootIdx = 0;
        printTree(rootIdx, "", attributes);
    }

    public void printTree (Integer nodeIdx, String tab, ArrayList<Attribute> attributes) {
        Node node = table.get(nodeIdx);
        HashMap<Double, Integer> children = table.get(nodeIdx).getChildren();
        for (Map.Entry<Double, Integer> entry: children.entrySet()) {
            int childIdx = entry.getValue();
            if (node.isRoot())
                node.print(entry.getKey(), attributes);
            else {
                System.out.print(tab);
                node.print(entry.getKey(), attributes);
            }
            if (!table.get(childIdx).isLeaf())
                System.out.println("");
            printTree(childIdx, tab + "|\t", attributes);
        }
        if (node.isLeaf()) {
            System.out.println(" : " + attributes.get(attributes.size()-1).value((int) node.getLabel()));
        }
    }
}
