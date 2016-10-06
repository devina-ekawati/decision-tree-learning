package model;

import weka.core.Attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tifani on 10/1/2016.
 */
public class Node {
    private int key;
    private double name;
    private double label;
    private HashMap<Double, Integer> children = new HashMap<>();
    private int parent = -1;
    private int level;

    public Node(Double name, int parent, int level) {
        this.name = name;
        this.parent = parent;
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public int getParent() {
        return parent;
    }

    public double getName() {
        return name;
    }

    public double getLabel() {
        return label;
    }

    public int getKey() {
        return key;
    }

    public void setLabel(double label) {
        this.label = label;
    }

    public void setKey(int key) {
        this.key = key;
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

    public void deleteChild(Integer child) {
        for (Map.Entry<Double, Integer> entry: children.entrySet()) {
            if (entry.getValue() == child) {
                children.remove(entry.getKey());
                break;
            }
        }
        System.out.println(children);
    }

    public Integer findChild(Double value) {
        return children.get(value);
    }

    public Double findBranch(Integer child) {
        for (Map.Entry<Double, Integer> entry: children.entrySet()) {
            if (entry.getValue() == child) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void print(Double childKey, ArrayList<Attribute> attribute) {
        if (!isLeaf())
            System.out.print("Level " + level + ": " + attribute.get((int) name).name() + " = " + attribute.get((int) name).value(childKey.intValue()-1));
        else
            System.out.print("Level " + level + ": " + attribute.get((int) name).value((int) label));
    }
}
