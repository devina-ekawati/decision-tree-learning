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

/**
 * Created by user on 01/10/2016.
 */
public class MyID3 extends Classifier {
    private Tree tree;

    public static Instances loadData(String filename) {
        ConverterUtils.DataSource source;
        Instances data = null;
        try {
            source = new ConverterUtils.DataSource(filename);
            data = source.getDataSet();
            // setting class attribute if the data format does not provide this information
            // For example, the XRFF format saves the class attribute information as well
            if (data.classIndex() == -1)
                data.setClassIndex(data.numAttributes() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    private double log2 (double x) {
        return log(x)/log(2);
    }

    private double[] countClassValue(Instances data) {
        double [] classCounts = new double[data.numClasses()];
        Enumeration instEnum = data.enumerateInstances();
        while (instEnum.hasMoreElements()) {
            Instance inst = (Instance) instEnum.nextElement();
            classCounts[(int) inst.classValue()]++;
        }

        return classCounts;
    }

    public double calculateEntropy(Instances data) {
        double result = 0;

        double [] classCounts = countClassValue(data);

        for (int i = 0; i < data.numClasses(); i++) {
            double proportion = classCounts[i]/(double) data.numInstances();
            if (proportion != 0) {
                result -= proportion*log2(proportion);
            }
        }
        return result;
    }

    private Instances[] splitData(Instances data, Attribute att) {
        Instances[] splitData = new Instances[att.numValues()];
        for (int j = 0; j < att.numValues(); j++) {
            splitData[j] = new Instances(data, data.numInstances());
        }
        Enumeration instEnum = data.enumerateInstances();
        while (instEnum.hasMoreElements()) {
            Instance inst = (Instance) instEnum.nextElement();
            splitData[(int) inst.value(att)].add(inst);
        }
        for (int i = 0; i < splitData.length; i++) {
            splitData[i].compactify();
        }
        return splitData;
    }

    public double calculateAttributeEntropy(Instances data, Attribute attribute) {
        double result = 0;
        Instances[] splitData = splitData(data, attribute);

        for (int i = 0; i < splitData.length; i++) {
            double probabilistic = splitData[i].numInstances()/(double) data.numInstances();
//            System.out.println("attr numInstance : " + splitData[i].numInstances());
//            System.out.println("numInstance : " + data.numInstances());
//            System.out.println("probabilistic : " + probabilistic + "\n");
            result +=  probabilistic * calculateEntropy(splitData[i]);
//            System.out.println("calculate entropy : " + calculateEntropy(splitData[i]) + "\n");
        }
        return result;
    }


    public double calculateInformationGain(Instances data, Attribute attribute) {
        return (calculateEntropy(data) - calculateAttributeEntropy(data,attribute));
    }

    private int findBestAttribute(Instances data) {
        int idxMax = 0;
        double infoGainMax = 0;
        double[] infoGain = new double[data.numAttributes()];
        for(int i=0; i<data.numAttributes()-1; i++) {
            infoGain[i] = calculateInformationGain(data, data.attribute(i));
            if (infoGainMax < infoGain[i]) {
                infoGainMax = infoGain[i];
                idxMax = i;
            }
        }

        return idxMax;
    }

    public double findMostCommonClass(Instances data) {
        double[] classCount = countClassValue(data);

        int maxIndex = 0;
        for (int i = 1; i < classCount.length; i++) {
            if (classCount[maxIndex] < classCount[i]) {
                maxIndex = i;
            }
        }

        return (double) maxIndex;
    }

    private boolean checkAttributesEmpty(ArrayList<Attribute> attributes) {
        boolean cek = true;
        for (int i = 0; i < attributes.size(); i++) {
            if (attributes.get(i) != null) {
                cek =  false;
                break;
            }
        }
        return cek;
    }

    public void buildTree (Instances data, Tree tree, int parent, ArrayList<Attribute> attributes, Double childValue) {

        if (checkAttributesEmpty(attributes)) {
            Node child = new Node(findMostCommonClass(data), parent);
            tree.addNode(child, childValue);
        }

        boolean isAllSameClass = true;
        for (int i = 1; i < data.numInstances(); i++) {
            if (data.instance(0).classValue() != data.instance(i).classValue()) {
                isAllSameClass = false;
                break;
            }
        }

        if (isAllSameClass) {
            // If all attribute have same label
            Node child = new Node((double) data.classAttribute().index(), parent);
            child.setLabel(data.instance(0).classValue());
            tree.addNode(child, childValue);
        } else {
            // Assign root to best attribute
            int bestAttribute = findBestAttribute(data);

            Node root = new Node((double) bestAttribute, parent);
            tree.addNode(root, childValue);

            int parentIndex = tree.getLastIdx();

            Instances[] splitData = splitData(data, data.attribute(bestAttribute));
            Enumeration enumAttr = data.attribute(bestAttribute).enumerateValues();

            int attrValue = 0;
            for(Instances instances : splitData) {
                attrValue++;
                if (instances.numInstances() == 0) {
                    // Assign child to most common value
                    Node child = new Node(findMostCommonClass(data), parent);
                    tree.addNode(child, (double) attrValue);
                } else {
                    attributes.set(bestAttribute,null);
                    buildTree(instances, tree, parentIndex, attributes, (double) attrValue);
                }
            }
        }
    }

    @Override
    public void buildClassifier(Instances instances) throws Exception {
        ArrayList<Attribute> attributes = new ArrayList<>();
        ArrayList<Attribute> fixedAttribute = new ArrayList<>();
        for (int i = 0; i < instances.numAttributes(); i++) {
            attributes.add(instances.attribute(i));
            fixedAttribute.add(instances.attribute(i));
        }

        tree = new Tree();
        buildTree(instances, tree, -1, attributes, null);
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

        return node.getLabel();
    }

    public static void main(String[] args) {
        MyID3 myID3 = new MyID3();
        Instances data = loadData("data/weather.nominal.arff");


//        System.out.println(data);
//        System.out.println("Total Entropy : " + myID3.calculateEntropy(data));
//        System.out.println(data.attribute(0));
//        System.out.println("Entropy Outlook : " + myID3.calculateAttributeEntropy(data, data.attribute(0)));
//        System.out.println("Information Gain Outlook : " + myID3.calculateInformationGain(data, data.attribute(0)));
//        System.out.println("Information Gain Temperature : " + myID3.calculateInformationGain(data, data.attribute(1)));
//        System.out.println("Information Gain Humidity : " + myID3.calculateInformationGain(data, data.attribute(2)));
//        System.out.println("Information Gain Windy : " + myID3.calculateInformationGain(data, data.attribute(3)));

        try {
            myID3.buildClassifier(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
