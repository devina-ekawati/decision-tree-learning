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
    private int depth = 0;

    public Tree() {

    }

    public HashMap<Integer, Node> getTable() {
        return table;
    }

    public int getLastIdx() {
        return idx-1;
    }

    public void addNode(Node node, Double childValue) {
        int currentIdx = idx;
        node.setKey(idx);
        table.put(currentIdx, node);
        if (node.getLevel() > depth)
            depth = node.getLevel();
        if (node.getParent() != -1) {
            table.get(node.getParent()).addChild(childValue, currentIdx);
        }
        idx++;
    }

    public void deleteNode(Integer idx, Double classValue) { //classValue: kelas yang paling banyak dari node yang akan dihapus
        Node node = getNode(idx);
        HashMap<Double, Integer> children = node.getChildren();
        if (checkAllChildrenIsLeaf(idx)) {
            ArrayList<Integer> childrenIdx = new ArrayList<>();

            //Hapus list childrennya
            for (Map.Entry<Double, Integer> entry: children.entrySet()) {
                childrenIdx.add(entry.getValue());
            }

            for (int i = 0; i < childrenIdx.size(); i++) {
                table.get(idx).deleteChild(childrenIdx.get(i));
            }

            //Set nodenya dengan label classValue => ClassValue ditentukan dari kelas dengan instance terbanyak dari node sebelumnya
            node.setLabel(classValue);

        } else {
            //Rekursif disini kalau dia anaknya bukan leaf semua hrus hapus pohon anaknya juga
            ArrayList<Integer> leafChildrenIdx = new ArrayList<>();
            ArrayList<Integer> notLeafChildrenIdx = new ArrayList<>();


            for (Map.Entry<Double, Integer> entry: children.entrySet()) {
                if (!getNode(entry.getValue()).isLeaf()) {
                    notLeafChildrenIdx.add(entry.getValue());
                }
                leafChildrenIdx.add(entry.getValue());
            }

            for (int i = 0; i < notLeafChildrenIdx.size(); i++) {
                deleteNode(notLeafChildrenIdx.get(i), classValue);
            }

            for (int i = 0; i < leafChildrenIdx.size(); i++) {
                table.get(idx).deleteChild(leafChildrenIdx.get(i));
            }
        }
    }

    public ArrayList<Integer> getAllParentOfLeafNodes () {
        ArrayList<Integer> parentsOfLeafNodes = new ArrayList<>();
        for (Map.Entry<Integer, Node> entry: table.entrySet()) {
            if (checkAllChildrenIsLeaf(entry.getKey())) {
                parentsOfLeafNodes.add(entry.getKey());
            }
        }
        return parentsOfLeafNodes;
    }

    public int getDepth() {
        return depth;
    }

    public boolean checkAllChildrenIsLeaf (Integer idx) {
        Node node = getNode(idx);

        if(node.isLeaf()) { //Invalid for leaf
            return false;
        } else {
            boolean check = true;
            HashMap<Double, Integer> children = node.getChildren();
            for (Map.Entry<Double, Integer> entry: children.entrySet()) {
                if (!getNode(entry.getValue()).isLeaf()) {
                    check = false;
                    break;
                }

            }
            return check;
        }
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
            if ((int) node.getLabel() == -1) // leaf
                System.out.println(" : null");
            else
                System.out.println(" : " + attributes.get(attributes.size()-1).value((int) node.getLabel()));
        }
    }
}
