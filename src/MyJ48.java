import model.DecisionTree;
import model.Node;
import model.Tree;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Discretize;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static model.DecisionTree.loadData;

/**
 * Created by Devina Ekawati on 10/4/2016.
 */
public class MyJ48 extends Classifier{
    private Tree tree;
    private double alpha = 0.25;
    private double zalpha2 = alpha*10/2;

    public MyJ48() {

    }

    private Instances discretizeInstances(Instances data) throws Exception {
        Discretize filter = new Discretize();
        filter.setInputFormat(data);

        return Filter.useFilter(data, filter);
    }

    public Instances filterMissingValue(Instances data) throws Exception {
        ReplaceMissingValues filter = new ReplaceMissingValues();
        filter.setInputFormat(data);

        return Filter.useFilter(data, filter);
    }

    public Instances normalizeDataset (Instances data) {
        try {
            data = discretizeInstances(data);
            data = filterMissingValue(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public double classifyInstance(Instance instance, Tree tree) {
        Node node = tree.getNode(0);
        while (!node.isLeaf()) {
            double attr = node.getName();
            Integer childIdx = node.findChild(instance.value((int) attr) + 1);
            node = tree.getNode(childIdx);
        }

        if ( (int) node.getLabel() == -1 )
            return Instance.missingValue();
        else
            return node.getLabel();
    }

    public double calculateAccuracy(Instances instances, Tree tree) {
        int truePrediction = 0;
        for(int i=0; i<instances.numInstances(); i++) {
            if (instances.instance(i).classValue() == classifyInstance(instances.instance(i), tree))
                truePrediction ++;
        }
        return truePrediction/instances.numInstances();
    }

    @Override
    public void buildClassifier(Instances instances) throws Exception {
        DecisionTree decisionTree = new DecisionTree();
        instances = normalizeDataset(instances);

        ArrayList<Attribute> attributes = new ArrayList<>();
        ArrayList<Attribute> fixedAttribute = new ArrayList<>();
        for (int i = 0; i < instances.numAttributes(); i++) {
            attributes.add(instances.attribute(i));
            fixedAttribute.add(instances.attribute(i));
        }

        tree = new Tree();
        decisionTree.buildTree(instances, tree, -1, attributes, null, 0);
        tree.print(fixedAttribute);
    }


    //Todo: Bikin fungsi buat hitung pessimistic error
    //Todo: Fungsi pruningnya

    public void postPruning (Instances instances, Tree tree) {
        try {
            //buildClassifier
            buildClassifier(instances);
            ArrayList<Integer> parentsOfLeafNodes = new ArrayList<>();

            parentsOfLeafNodes = tree.getAllParentOfLeafNodes();

            for (int i = 0; i < parentsOfLeafNodes.size(); i++) {
                Node node = tree.getNode(parentsOfLeafNodes.get(i));
                double prunedError = pessimisticError(instances, tree, node);
                double childError = 0;
                for (Map.Entry<Double, Integer> entry: node.getChildren().entrySet()) {
                    childError += pessimisticLeafError(instances, tree, node, entry.getKey());
                }

                if (prunedError < childError) {
                    tree.deleteNode(parentsOfLeafNodes.get(i), findMostCommonClass(instances,tree,node)); //kayaknya instances yang uda displit deh cuman bingung gmn
                }
            }

            //Dari node dengan level tertinggi cek prunning untuk setiap anaknya

        } catch (Exception e) {
            e.printStackTrace();
        }

        //
    }

    private void prune(Instances instances, Tree tree, Node node) {
        double prunedError = pessimisticError(instances, tree, node);
        double childError = 0;
        for (Map.Entry<Double, Integer> entry: node.getChildren().entrySet()) {
            childError = pessimisticLeafError(instances, tree, node, entry.getKey());
        }
        if (prunedError < childError) { // prune
            tree.deleteNode(node.getKey(), findMostCommonClass(instances, tree, node)); //TODO: classvalue
        }
    }

    private double findMostCommonClass (Instances instances, Tree tree, Node node) {
        HashMap<Double, Double> pathToRoot = new HashMap<>(); // attr, val

        Node evalNode = node;
        while(evalNode.getParent() != -1) {
            pathToRoot.put(tree.getNode(evalNode.getParent()).getName(), tree.getNode(evalNode.getParent()).findBranch(evalNode.getKey()));
            evalNode = tree.getNode(evalNode.getParent());
        }

        double [] classCounts = new double[instances.numClasses()];
        for(int i=0; i<instances.numInstances(); i++) {
            boolean correctBranch = true;
            for (Map.Entry<Double, Double> entry: pathToRoot.entrySet()) {
                if (!(instances.instance(i).value(entry.getKey().intValue()) == entry.getValue() )) { //TODO: tricky
                    correctBranch = false;
                    break;
                }
            }
            if (correctBranch) {
                for (int j = 0; j < classCounts.length; j++) {
                    if (j == instances.instance(i).classValue()) {
                        classCounts[j]++;
                    }
                }
            }
        }

        double max = 0;
        for (int i = 0; i < classCounts.length; i++) {
            if (max < classCounts[i]) {
                max = classCounts[i];
            }
        }

        return max;
    }

    private double pessimisticError(Instances instances, Tree tree, Node node) {
        int truePrediction = 0;
        int falsePrediction = 0;
        HashMap<Double, Double> pathToRoot = new HashMap<>(); // attr, val

        Node evalNode = node;
        while(evalNode.getParent() != -1) {
            pathToRoot.put(tree.getNode(evalNode.getParent()).getName(), tree.getNode(evalNode.getParent()).findBranch(evalNode.getKey()));
            evalNode = tree.getNode(evalNode.getParent());
        }

        for(int i=0; i<instances.numInstances(); i++) {
            boolean correctBranch = true;
            for (Map.Entry<Double, Double> entry: pathToRoot.entrySet()) {
                if (!(instances.instance(i).value(entry.getKey().intValue()) == entry.getValue() )) { //TODO: tricky
                    correctBranch = false;
                    break;
                }
            }
            if (correctBranch) {
                if (instances.instance(i).classValue() == classifyInstance(instances.instance(i), tree)) { //TODO: tricky
                    truePrediction++;
                } else {
                    falsePrediction++;
                }
            }
        }

        double n = truePrediction + falsePrediction;
        if (n != 0) {
            double p = ((double) falsePrediction + 1.0) / (n + 2.0);
            return n * (p + zalpha2 * Math.sqrt(p*(1.0-p) / (n+2.0)));
        } else
            return 0;
    }

    private double pessimisticLeafError(Instances instances, Tree tree, Node node, Double childValue) {
        int truePrediction = 0;
        int falsePrediction = 0;
        HashMap<Double, Double> pathToRoot = new HashMap<>(); // attr, val

        Node evalNode = node;
        while(evalNode.getParent() != -1) {
            pathToRoot.put(tree.getNode(evalNode.getParent()).getName(), tree.getNode(evalNode.getParent()).findBranch(evalNode.getKey()));
            evalNode = tree.getNode(evalNode.getParent());
        }

        for(int i=0; i<instances.numInstances(); i++) {
            boolean correctBranch = true;
            for (Map.Entry<Double, Double> entry: pathToRoot.entrySet()) {
                if (!(instances.instance(i).value(entry.getKey().intValue()) == entry.getValue() )) { //TODO: tricky
                    if (!(instances.instance(i).value((int) node.getName()) == childValue )) {
                        correctBranch = false;
                        break;
                    }
                }
            }
            if (correctBranch) {
                if (instances.instance(i).classValue() == classifyInstance(instances.instance(i), tree)) { //TODO: tricky
                    truePrediction++;
                } else {
                    falsePrediction++;
                }
            }
        }

        double n = truePrediction + falsePrediction;
        if (n != 0) {
            double p = ((double) falsePrediction + 1.0) / (n + 2.0);
            return n * (p + zalpha2 * Math.sqrt(p*(1.0-p) / (n+2.0)));
        } else
            return 0;
    }

    public static void main(String[] args) {
        MyJ48 myJ48 = new MyJ48();
        Instances data = loadData("data/weather.nominal.arff");

        try {
            myJ48.buildClassifier(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
