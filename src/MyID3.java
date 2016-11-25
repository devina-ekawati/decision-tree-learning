import model.DecisionTree;
import model.Tree;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Random;

import static model.DecisionTree.loadData;

/**
 * Created by user on 01/10/2016.
 */
public class MyID3 extends Classifier {
    private Tree tree;

    public double calculateAccuracy(Instances instances, Tree tree) {
        MyID3 myID3 = new MyID3();
        myID3.setTree(tree);
        int truePrediction = 0;
        for(int i=0; i<instances.numInstances(); i++) {
            if (instances.instance(i).classValue() == myID3.classifyInstance(instances.instance(i)))
                truePrediction ++;
        }
        return (double) truePrediction/ (double) instances.numInstances();
    }

    public void setTree(Tree tree) {
        this.tree = tree;
    }

    public Tree getTree() {
        return tree;
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

    @Override
    public double classifyInstance(Instance instance) {
        return DecisionTree.classifyInstance(instance, tree);
    }

    public Instances normalizeDataset (Instances instances) throws Exception {
        Enumeration enu = instances.enumerateAttributes();
        while (enu.hasMoreElements()) {
            Attribute attribute = (Attribute) enu.nextElement();
            if (attribute.type() != Attribute.NOMINAL) {
                throw new UnsupportedAttributeTypeException("MyID3 handles nominal variables only. Non-nominal variable in dataset detected.");
            }
            Enumeration enu2  = instances.enumerateInstances();
            while (enu2.hasMoreElements()) {
                if (((Instance)enu2.nextElement()).isMissing(attribute)) {
                    throw new NoSupportForMissingValuesException("MyID3 : No missing values, please!");
                }
            }
        }
        return instances;
    }

    public static void main(String[] args) {
        MyID3 myID3 = new MyID3();
        Instances data = loadData("data/weather.nominal.arff");
        try {
            // myID3.buildClassifier(data);

            ArrayList<Attribute> fixedAttribute = new ArrayList<>();
            for (int i = 0; i < data.numAttributes(); i++) {
                fixedAttribute.add(data.attribute(i));
            }
//
//            System.out.println("===TREE===");
//            myID3.getTree().print(fixedAttribute);
////            System.out.println("Accuracy: " + myID3.calculateAccuracy(data, myID3.getTree()));
////            System.out.println("");
////            Instance ins = data.instance(4);
////            System.out.println("===TRY TO CLASSIFY INSTANCE===");
////            System.out.println("Classify instance: " + ins);
////            System.out.println("Result: " + myID3.classifyInstance(ins) + " " + fixedAttribute.get(fixedAttribute.size()-1).value((int) myID3.classifyInstance(ins)));
//            System.out.println("");
//            System.out.println("===CROSS VALIDATION===");
//            // MyEvaluation.crossValidation(data, 7, myID3);
            Evaluation testEval = null;
            testEval = new Evaluation(data);
            Classifier temp = Classifier.makeCopy((myID3));
            myID3.buildClassifier(data);
            testEval.crossValidateModel(temp, data, 10, new Random(1));
            System.out.println("----------- ID3 -----------");

            System.out.println();
            System.out.println("Tree");
            System.out.println("====");
            System.out.println();
            myID3.getTree().print(fixedAttribute);
            System.out.println();
            System.out.println();

            System.out.println("Results");
            System.out.println("=======");
            System.out.println(testEval.toSummaryString());
            System.out.println(testEval.toClassDetailsString());
            System.out.println(testEval.toMatrixString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
