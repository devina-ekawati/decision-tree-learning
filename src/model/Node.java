package model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tifani on 10/1/2016.
 */
public class Node {
    private String name;
    private HashMap<String, String> children = new HashMap<>();
    private int parent = -1;

    public Node(String name, int parent) {
        this.name = name;
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public HashMap<String, String> getChildren() {
        return children;
    }

    public boolean isRoot() {
        return parent == -1;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public void addChild(String value, String child) {
        children.put(value, child);
    }

    public void print() {
        System.out.println("Node " + name);
        for (Map.Entry<String, String> entry: children.entrySet()){
            System.out.println("\t" + entry.getKey() + " " + entry.getValue());
        }
    }
}
