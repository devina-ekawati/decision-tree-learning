import model.DecisionTree;
import model.Node;
import model.Tree;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Discretize;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

import java.util.*;

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

    public Tree getTree() {
        return tree;
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
    }

    public void postPruning (Instances instances, Tree tree) {
        instances = normalizeDataset(instances);
        try {
            //buildClassifier
            buildClassifier(instances);
            ArrayList<Integer> parentsOfLeafNodes;

            parentsOfLeafNodes = tree.getAllParentOfLeafNodes();

            for (int i = 0; i < parentsOfLeafNodes.size(); i++) {
                Node node = tree.getNode(parentsOfLeafNodes.get(i));
                Integer parent = node.getParent();
                if (prune(instances, tree, node)) {
                    if (parent != 0)
                        parentsOfLeafNodes.add(parent);
                }
            }
            this.tree = tree;

            //Dari node dengan level tertinggi cek prunning untuk setiap anaknya

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean prune(Instances instances, Tree tree, Node node) {
        double prunedError = pessimisticError(instances, tree, node);
        double childError = 0;
        for (Map.Entry<Double, Integer> entry: node.getChildren().entrySet()) {
            childError += pessimisticError(instances, tree, tree.getNode(entry.getValue()));
        }

        if (prunedError < childError) { // prune
            tree.deleteNode(node.getKey(), findMostCommonClass(instances, tree, node));
            return true;
        } else {
            return false;
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
                if (!(instances.instance(i).value(entry.getKey().intValue()) == entry.getValue()-1 )) {
                    correctBranch = false;
                    break;
                }
            }
            if (correctBranch) {
                classCounts[(int) instances.instance(i).value(instances.numAttributes()-1)]++;
            }
        }

        double id = 0.0;
        if (classCounts.length > 0) {
            double max = classCounts[0];
            for (int i = 1; i < classCounts.length; i++) {
                if (max < classCounts[i]) {
                    max = classCounts[i];
                    id = (double) i;
                }

            }
        }

        return id;
    }

    private double pessimisticError(Instances instances, Tree tree, Node node) {
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
                if (!(instances.instance(i).value(entry.getKey().intValue()) == entry.getValue()-1 )) {
                    correctBranch = false;
                    break;
                }
            }
            if (correctBranch) {
                classCounts[(int) instances.instance(i).value(instances.numAttributes()-1)]++;
            }
        }
        double n = classCounts[0];
        double wrong = classCounts[0];
        for(int i=1; i<instances.numClasses(); i++) {
            n += classCounts[i];
            if (wrong > classCounts[i])
                wrong = classCounts[i];
        }
        if (n != 0) {
            double p = (wrong + 1.0) / (n + 2.0);
            return n * (p + zalpha2 * Math.sqrt(p*(1.0-p) / (n+2.0)));
        } else
            return 0;
    }

    @Override
    public double classifyInstance(Instance instance) {
        Node node = tree.getNode(0);

        while (!node.isLeaf()) {
            double attr = node.getName();
            // System.out.println(attr);
            Integer childIdx = node.findChild(instance.value((int) attr) + 1);
            Node temp = tree.getNode(childIdx);
            if (temp == null) {
                break;
            } else {
                node = temp;
            }

        }

        if ( (int) node.getLabel() == -1 )
            return Instance.missingValue();
        else
            return node.getLabel();
    }

    public static void main(String[] args) {
        MyJ48 myJ48 = new MyJ48();
        Instances data = loadData("data/weather.numeric.arff");

        try {
            ArrayList<Attribute> fixedAttribute = new ArrayList<>();
            for (int i = 0; i < data.numAttributes(); i++) {
                fixedAttribute.add(data.attribute(i));
            }
            Evaluation testEval = null;
            testEval = new Evaluation(data);
            Classifier temp = Classifier.makeCopy(myJ48);
            myJ48.buildClassifier(data);
            myJ48.postPruning(data, myJ48.getTree());
            // myJ48.getTree().print(fixedAttribute);
            Instances norm = myJ48.normalizeDataset(data);

            testEval.crossValidateModel(temp, norm, 10, new Random(1));
            System.out.println("----------- J48 -----------");

            System.out.println();
            System.out.println("Tree");
            System.out.println("====");
            System.out.println();
            myJ48.getTree().print(fixedAttribute);
            System.out.println();
            System.out.println();

            System.out.println("Results");
            System.out.println("=======");
            System.out.println(testEval.toSummaryString());
            System.out.println(testEval.toClassDetailsString());
            System.out.println(testEval.toMatrixString());

//            myJ48.buildClassifier(data);
//            System.out.println("===TREE (BEFORE PRUNING) ===");
//            myJ48.getTree().print(fixedAttribute);
//            System.out.println();
//
//            myJ48.postPruning(data, myJ48.getTree());
//            System.out.println("===TREE (AFTER PRUNING) ===");
//            myJ48.getTree().print(fixedAttribute);
//
//            System.out.println();
//            Instances norm = myJ48.normalizeDataset(data);
//            Instance ins = norm.instance(1);
////            System.out.println("===TRY TO CLASSIFY INSTANCE===");
////            System.out.println("Classify instance: " + ins);
////            System.out.println("Result: " + myJ48.classifyInstance(ins) + " " + fixedAttribute.get(fixedAttribute.size()-1).value((int) myJ48.classifyInstance(ins)));
////            System.out.println();
//
//            System.out.println("===CROSS VALIDATION===");
//            MyEvaluation.crossValidation(norm, 10, myJ48);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
