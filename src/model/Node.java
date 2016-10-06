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

    public int getParent() {
        return parent;
    }

    public double getName() {
        return name;
    }

    public double getLabel() {
        return label;
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

    public void print(Double childKey, ArrayList<Attribute> attribute) {
        if (!isLeaf())
            System.out.print(attribute.get((int) name).name() + " = " + attribute.get((int) name).value(childKey.intValue()-1));
        else
            System.out.print(attribute.get((int) name).value((int) label));
    }
}
