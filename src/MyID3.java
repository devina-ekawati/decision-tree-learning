import model.DecisionTree;
import model.Node;
import model.Tree;
import weka.classifiers.Classifier;
import weka.core.*;
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
        instances = normalizeDataset(instances);

        ArrayList<Attribute> attributes = new ArrayList<>();
        ArrayList<Attribute> fixedAttribute = new ArrayList<>();
        for (int i = 0; i < instances.numAttributes(); i++) {
            attributes.add(instances.attribute(i));
            fixedAttribute.add(instances.attribute(i));
        }

        tree = new Tree();
        decisionTree.buildTree(instances, tree, -1, attributes, null);
        System.out.println("Tree:");
        tree.print(fixedAttribute);
        System.out.println("Accuracy: " + calculateAccuracy(instances));


        Instance ins = instances.instance(4);
        System.out.println("Classify instance: " + ins);
        System.out.println("Result: " + classifyInstance(ins) + " " + fixedAttribute.get(fixedAttribute.size()-1).value((int) classifyInstance(ins)));
        tree.deleteNode(1,1.0);
        for (int i = 0; i < tree.getTable().size(); i++) {
            System.out.println(tree.checkAllChildrenIsLeaf(i));
        }

        //System.out.println(tree.deleteNode(3,));

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

    public double calculateAccuracy(Instances instances) {
        int truePrediction = 0;
        for(int i=0; i<instances.numInstances(); i++) {
            if (instances.instance(i).classValue() == classifyInstance(instances.instance(i)))
                truePrediction ++;
        }
        return truePrediction/instances.numInstances();
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
            myID3.buildClassifier(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
