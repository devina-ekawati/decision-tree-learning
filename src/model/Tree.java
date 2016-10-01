package model;

import java.util.HashMap;

/**
 * Created by Tifani on 10/1/2016.
 */
public class Tree {
    private String root;
    private HashMap<String, Node> nodes = new HashMap<>();

    public Tree() {

    }

    public Tree(String rootName, Node rootNode) {
        this.root = rootName;
        nodes.put(rootName, rootNode);
    }

    public String getRoot() {
        return root;
    }

    public HashMap<String, Node> getNodes() {
        return nodes;
    }

    public Node getNode(String nodeName) {
        return nodes.get(nodeName);
    }

    public void addNode(String parent, String value, String nodeName, Node node) {
        nodes.put(nodeName, node);
        nodes.get(parent).addChild(value, node);
    }
}
