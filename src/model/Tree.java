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
        int currentIdx = idx;
        table.put(currentIdx, node);
//        if (!node.isLeaf())
//            System.out.println("ADD NODE: [" + idx + "] " + node.getName());
//        else
//            System.out.println("ADD NODE: [" + idx + "] " + node.getLabel() + " > leaf");
//        System.out.println("PARENT: " + node.getParent() + " CHILD VALUE: " + childValue);
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

    public boolean checkAllChildrenIsLeaf (Integer idx) {
        boolean check = true;
        Node node = getNode(idx);
        //System.out.println(node.getChildren());
        HashMap<Double, Integer> children = node.getChildren();
        for (Map.Entry<Double, Integer> entry: children.entrySet()) {
            if (!getNode(entry.getValue()).isLeaf()) {
                check = false;
                break;
            }

        }
        return check;
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

//    public void printTable(ArrayList<Attribute> attributes) {
//        for (Map.Entry<Integer, Node> entry: table.entrySet()) {
//            if (entry.getValue().isLeaf()) {
//                System.out.println(entry.getKey() + ": " + attributes.get(attributes.size()-1).value((int) entry.getValue().getLabel()));
//            }
//            else {
//                System.out.println(entry.getKey() + ": " + attributes.get((int) entry.getValue().getName()).name());
//                HashMap<Double, Integer> children = entry.getValue().getChildren();
//                for (Map.Entry<Double, Integer> child: children.entrySet()) {
//                    System.out.println("\t" + "[" + child.getKey() + "] " + attributes.get((int) entry.getValue().getName()).value(child.getKey().intValue()-1) + " -> " + child.getValue());
//                }
//            }
//        }
//    }
}
