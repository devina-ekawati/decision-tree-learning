package model;

import java.util.HashMap;

/**
 * Created by Tifani on 10/1/2016.
 */
public class Node {
    private String name;
    private HashMap<String, Node> children = new HashMap<>();

    public Node(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public HashMap<String, Node> getChildren() {
        return children;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public void addChild(String childName, Node child) {
        children.put(childName, child);
    }

    public Node getChild(String value) {
        return children.get(value);
    }
}
