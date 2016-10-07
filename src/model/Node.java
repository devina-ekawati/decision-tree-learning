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

    /**
     * Memeriksa apakah sebuah node merupakan root atau bukan
     * @return true jika node merupakan root dan false jika tidak
     */
    public boolean isRoot() {
        return parent == -1;
    }

    /**
     * Memeriksa apakah sebuah node merupakan leaf atau bukan
     * @return true jika node merupakan leaf dan false jika tidak
     */
    public boolean isLeaf() {
        return children.isEmpty();
    }

    /**
     * Menambah child sebuah node
     * @param value nilai dari cabang yang menuju anak
     * @param child id dari anak
     */
    public void addChild(Double value, Integer child) {
        children.put(value, child);
    }

    /**
     * Menhapus child dari node
     * @param child id child yang akan dihapus
     */
    public void deleteChild(Integer child) {
        for (Map.Entry<Double, Integer> entry: children.entrySet()) {
            if (entry.getValue() == child) {
                children.remove(entry.getKey());
                break;
            }
        }
        System.out.println(children);
    }

    /**
     * Memperoleh key anak pada hash tabel berdasarkan value branch pohon
     * @param value indeks atribut pada dataset
     * @return hash key anak berdasarkan value
     */
    public Integer findChild(Double value) {
        return children.get(value);
    }

    /**
     * Memperoleh nilai branch yang mnghubungkan child ke sebuah node
     * @param child id child
     * @return nilai branch dalam bentuk double
     */
    public Double findBranch(Integer child) {
        for (Map.Entry<Double, Integer> entry: children.entrySet()) {
            if (entry.getValue() == child) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Mencetak node ke layar
     * @param childKey id dari anak
     * @param attribute list atribut pada dataset yang digunakan untuk membangun pohon
     */
    public void print(Double childKey, ArrayList<Attribute> attribute) {
        if (!isLeaf())
            System.out.print("Level " + level + ": " + attribute.get((int) name).name() + " = " + attribute.get((int) name).value(childKey.intValue()-1));
        else
            System.out.print("Level " + level + ": " + attribute.get((int) name).value((int) label));
    }
}
