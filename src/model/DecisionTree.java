package model;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.util.ArrayList;
import java.util.Enumeration;

import static java.lang.Math.log;

/**
 * Created by Devina Ekawati on 10/4/2016.
 */
public class DecisionTree {

    /**
     * Melakukan load data arff
     * @param filename nama file arff
     * @return Instances hasil pembacaan file arff
     */
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

    /**
     * Menghitung 2log(x)
     * @param x
     * @return hasil perhitungan logaritma
     */
    private double log2 (double x) {
        return log(x)/log(2);
    }

    /**
     * Menghitung total instance untuk setiap label
     * @param data dataset yang digunakan
     * @return array total instance untuk setiap label kelas
     */
    private double[] countClassValue(Instances data) {
        double [] classCounts = new double[data.numClasses()];
        Enumeration instEnum = data.enumerateInstances();
        while (instEnum.hasMoreElements()) {
            Instance inst = (Instance) instEnum.nextElement();
            classCounts[(int) inst.classValue()]++;
        }

        return classCounts;
    }

    /**
     * Menghitung entropy keseluruhan berdasarkan jumlah instance setiap kelas
     * @param data dataset yang digunakan
     * @return hasil perhitungan entropy
     */
    private double calculateEntropy(Instances data) {
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

    /**
     * Memisahakan dataset berdasarkan nilai atribut
     * @param data dataset yang digunakan
     * @param att atribut yang digunakan untuk memisahkan dataset
     * @return dataset yang telah dipisah
     */
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

    /**
     * Menghitung entropy untuk sebuah atribut
     * @param data dataset yang digunakan
     * @param attribute atribut yang digunakan untuk menghitung entropy
     * @return hasil perhitungan entropy
     */
    public double calculateAttributeEntropy(Instances data, Attribute attribute) {
        double result = 0;
        // System.out.println("SUMMARY:");
        // System.out.println(data.toSummaryString());
        try {
            Instances[] splitData = splitData(data, attribute);

            for (int i = 0; i < splitData.length; i++) {
                double probabilistic = splitData[i].numInstances()/(double) data.numInstances();
//            System.out.println("attr numInstance : " + splitData[i].numInstances());
//            System.out.println("numInstance : " + data.numInstances());
//            System.out.println("probabilistic : " + probabilistic + "\n");
                if (probabilistic != 0)
                    result +=  probabilistic * calculateEntropy(splitData[i]);
//            System.out.println("calculate entropy : " + calculateEntropy(splitData[i]) + "\n");
            }
            splitData = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        // System.out.println("Attr entropy: " + result);
        return result;
    }

    /**
     * Menghitung information gain
     * @param data dataset yang digunakan
     * @param attribute atribut yang digunakan untuk menghitung information gain
     * @return hasil perhitungan information gain
     */
    public double calculateInformationGain(Instances data, Attribute attribute) {
        return (calculateEntropy(data) - calculateAttributeEntropy(data,attribute));
    }

    /**
     * Mencari best attribute berdasarkan information gain
     * @param data dataset yang digunakan
     * @param attributes list atribut yang akan dipilih
     * @return atribut yang terpilih
     */
    private int findBestAttribute(Instances data, ArrayList<Attribute> attributes) {
        int idxMax = 0;
        while (attributes.get(idxMax) == null) {
            idxMax++;
        }
        double infoGainMax = calculateInformationGain(data, data.attribute(idxMax));
        double[] infoGain = new double[data.numAttributes()];
        // System.out.println("Info gain max: " + infoGainMax);
        for(int i=idxMax; i<data.numAttributes()-1; i++) {
            infoGain[i] = calculateInformationGain(data, data.attribute(i));
            // System.out.println("Info gain ke-" + i + ": " + infoGain[i]);
            if (infoGainMax < infoGain[i] && attributes.get(i) != null) {
                infoGainMax = infoGain[i];
                idxMax = i;
            }
        }
        return idxMax;
    }

    /**
     * Mencari kelas yang paling banyak dimiliki instances
     * @param data dataset yang digunakan
     * @return kelas yang paling banyak dimiliki instances dalam bentuk double
     */
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

    /**
     * Memeriksa apakah list attributes telah kosong
     * @param attributes list attribute yang akan diproses
     * @return true jika list attributes kosong dan false jika tidak
     */
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

    /**
     * Membangun model decision tree berdasarkan dataset yang ada
     * @param data dataset yang digunakan untuk membangun decision tree
     * @param tree pohon yang terbentuk
     * @param parent indeks parent saat ini
     * @param attributes attribute yang digunakan untuk membangun pohon
     * @param childValue nilai cabang yang menghubungkan parent ke child
     * @param level ketinggian pohon saat ini
     */
    public void buildTree (Instances data, Tree tree, int parent, ArrayList<Attribute> attributes, Double childValue, int level) {
        if (checkAttributesEmpty(attributes)) {
            Node child = new Node(findMostCommonClass(data), parent, level);
            tree.addNode(child, childValue);
        } else {
            boolean isAllSameClass = true;
            for (int i = 1; i < data.numInstances(); i++) {
                if (data.instance(0).classValue() != data.instance(i).classValue()) {
                    isAllSameClass = false;
                    break;
                }
            }

            if (isAllSameClass) {
                // If all attribute have same label
                Node child = new Node((double) data.classAttribute().index(), parent, level);
                child.setLabel(data.instance(0).classValue());
                tree.addNode(child, childValue);
                // System.out.println("Add node -> parent: " + child.getParent() + " node: " + child.getName() + " leaf: " + child.getLabel() + " child value: " + childValue );
            } else {
                // Assign root to best attribute
                ArrayList<Attribute> newAttributes = new ArrayList<>();
                for (int i = 0; i < attributes.size(); i++) {
                    newAttributes.add(attributes.get(i));
                }

                int bestAttribute = findBestAttribute(data, newAttributes);
                // System.out.println("Best attr: " + bestAttribute + " " + data.attribute(bestAttribute).name());

                Node root = new Node((double) bestAttribute, parent, level);
                tree.addNode(root, childValue);
                // System.out.println("Add node -> parent: " + root.getParent() + " node: " + root.getName() + " leaf: " + root.getLabel() + " child value: " + childValue );

                int parentIndex = tree.getLastIdx();
                int currentLevel = level + 1;

                Instances[] splitData = splitData(data, data.attribute(bestAttribute));
                Enumeration enumAttr = data.attribute(bestAttribute).enumerateValues();

                int attrValue = 0;
                for(Instances instances : splitData) {
                    attrValue++;
                    if (instances.numInstances() == 0) {
                        // Assign child to most common value
                        Node child = new Node(findMostCommonClass(data), parentIndex, currentLevel);
                        child.setLabel(findMostCommonClass(data));
                        tree.addNode(child, (double) attrValue);
                    } else {
                        newAttributes.set(bestAttribute, null);
                        buildTree(instances, tree, parentIndex, newAttributes, (double) attrValue, currentLevel);
                    }
                }
            }
        }
    }

    /**
     * Melaukan klasifikasi instance berdasarkan pohon yang telah dibuat
     * @param instance data yang akan diklasifikasi
     * @param tree model pohon yang digunakan untuk melakukan klasifikasi
     * @return kelas hasil klasifikasi
     */
    public static double classifyInstance(Instance instance, Tree tree) {
        Node node = tree.getNode(0);

        while (!node.isLeaf()) {
            double attr = node.getName();
            // System.out.println(attr);
            Integer childIdx = node.findChild(instance.value((int) attr) + 1);
            node = tree.getNode(childIdx);
        }

        if ( (int) node.getLabel() == -1 )
            return Instance.missingValue();
        else
            return node.getLabel();
    }

    /**
     * Menghitung akurasi model decision tree
     * @param instances dataset yang digunakan untuk menghitung akurasi
     * @param tree model decision tree yang akan dievaluasi
     * @return akurasi dari model decision tree
     */
    public double calculateAccuracy(Instances instances, Tree tree) {
        int truePrediction = 0;
        for(int i=0; i<instances.numInstances(); i++) {
            if (instances.instance(i).classValue() == classifyInstance(instances.instance(i), tree))
                truePrediction ++;
        }
        return truePrediction/instances.numInstances();
    }
}
