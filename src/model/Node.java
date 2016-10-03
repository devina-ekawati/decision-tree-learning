package model;

import weka.core.Attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tifani on 10/1/2016.
 */
public class Node {
    private double name;
    private double label;
    private HashMap<Double, Integer> children = new HashMap<>();
    private int parent = -1;

    public Node(Double name, int parent) {
        this.name = name;
        this.parent = parent;
    }

    public double getName() {
        return name;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public void setLabel(double label) {
        this.label = label;
    }

    public HashMap<Double, Integer> getChildren() {
        return children;
    }

    public boolean isRoot() {
        return parent == -1;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public void addChild(Double value, Integer child) {
        children.put(value, child);
    }

    public void print(ArrayList<Attribute> attribute) {
        if (!isLeaf())
            System.out.println("Node " + attribute.get((int) name).name());
        else
            System.out.println("Node " + attribute.get((int) name).value((int) label));
        for (Map.Entry<Double, Integer> entry: children.entrySet()){
            System.out.println("\t" + entry.getKey().intValue() + " " + entry.getValue().intValue());
            // System.out.println("\t" + attribute.get((int) name).value(entry.getKey().intValue()-1) + " " + attribute.get(entry.getValue()).name());
        }
    }
}
