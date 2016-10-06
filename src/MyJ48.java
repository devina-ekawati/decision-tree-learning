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
        decisionTree.buildTree(instances, tree, -1, attributes, null);
        tree.print(fixedAttribute);
    }


    //Todo: Bikin fungsi buat hitung pessimistic error
    //Todo: Nodenya ditambahin level atau ada fungsi buat ngetrack node itu levelnya di mana dari hash table
    //Todo: Fungsi pruningnya

    public void postPruning (Instances instances, Tree tree) {
        try {
            //buildClassifier
            buildClassifier(instances);

            //sepertinya perlu level di nodenya untuk pruning

            //Dari node dengan level tertinggi cek prunning untuk setiap anaknya

            //

        } catch (Exception e) {
            e.printStackTrace();
        }

        //
    }

    public static void main(String[] args) {
        MyJ48 myJ48 = new MyJ48();
        Instances data = loadData("data/weather.nominal - Copy.arff");

        try {
            myJ48.buildClassifier(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
