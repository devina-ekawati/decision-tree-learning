import model.DecisionTree;
import model.Node;
import model.Tree;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;

import static java.lang.Math.log;
import static model.DecisionTree.loadData;

/**
 * Created by user on 01/10/2016.
 */
public class MyID3 extends Classifier {
    private Tree tree;

    @Override
    public void buildClassifier(Instances instances) throws Exception {
        DecisionTree decisionTree = new DecisionTree();

        ArrayList<Attribute> attributes = new ArrayList<>();
        ArrayList<Attribute> fixedAttribute = new ArrayList<>();
        for (int i = 0; i < instances.numAttributes(); i++) {
            attributes.add(instances.attribute(i));
            fixedAttribute.add(instances.attribute(i));
        }

        tree = new Tree();
        decisionTree.buildTree(instances, tree, -1, attributes, null);
        tree.print(fixedAttribute);

        Instance ins = instances.instance(4);
        System.out.println("Classify instance: " + ins);
        System.out.println("Result: " + classifyInstance(ins) + " " + fixedAttribute.get(fixedAttribute.size()-1).value((int) classifyInstance(ins)));
    }

    @Override
    public double classifyInstance(Instance instance) {

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

    public static void main(String[] args) {
        MyID3 myID3 = new MyID3();
        Instances data = loadData("data/contact-lenses.arff");

        try {
            myID3.buildClassifier(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
