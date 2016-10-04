import model.DecisionTree;
import model.Tree;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Discretize;

import java.util.ArrayList;

import static model.DecisionTree.loadData;

/**
 * Created by Devina Ekawati on 10/4/2016.
 */
public class MyJ48 extends Classifier{
    private Tree tree;

    public MyJ48() {

    }

    private Instances discretizeInstances(Instances data) throws Exception {
        Discretize filter = new Discretize();
        filter.setInputFormat(data);

        return Filter.useFilter(data, filter);
    }

    @Override
    public void buildClassifier(Instances instances) throws Exception {
        DecisionTree decisionTree = new DecisionTree();
        instances = discretizeInstances(instances);

        ArrayList<Attribute> attributes = new ArrayList<>();
        ArrayList<Attribute> fixedAttribute = new ArrayList<>();
        for (int i = 0; i < instances.numAttributes(); i++) {
            attributes.add(instances.attribute(i));
            fixedAttribute.add(instances.attribute(i));
        }

        tree = new Tree();
        decisionTree.buildTree(instances, tree, -1, attributes, null);
        tree.print(fixedAttribute);
    }

    public static void main(String[] args) {
        MyJ48 myJ48 = new MyJ48();
        Instances data = loadData("data/weather.numeric.arff");

        try {
            myJ48.buildClassifier(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
