import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.Id3;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.instance.RemovePercentage;
import weka.filters.unsupervised.instance.Resample;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by Devina Ekawati on 9/28/2016.
 */
public class Main {

    public Instances loadData(String filename) {
        DataSource source;
        Instances data = null;
        try {
            source = new DataSource(filename);
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

    public Instances removeAttribute(Instances data, String attrIndex, boolean isInvert) throws Exception {
        Remove remove = new Remove();

        remove.setAttributeIndices(String.valueOf(attrIndex));
        remove.setInvertSelection(isInvert);
        remove.setInputFormat(data);
        Instances newData =  Filter.useFilter(data, remove);
        newData.setClassIndex(newData.numAttributes() - 1);

        return newData;
    }

    public Instances resample(Instances data) throws Exception {
        Resample resample = new Resample();

        resample.setInputFormat(data);
        resample.setNoReplacement(false);
        resample.setSampleSizePercent(100);

        Instances newData =  Filter.useFilter(data, resample);
        newData.setClassIndex(newData.numAttributes() - 1);

        return newData;
    }

    public Instances percentageSplit(Instances data, int percentage, boolean isInverted) throws Exception {
        RemovePercentage percentageSplit = new RemovePercentage();

        percentageSplit.setInvertSelection(isInverted);
        percentageSplit.setPercentage(percentage);
        percentageSplit.setInputFormat(data);

        Instances newData =  Filter.useFilter(data, percentageSplit);
        newData.setClassIndex(newData.numAttributes() - 1);

        return newData;
    }

    public Evaluation crossValidation(Instances data, Classifier tree) throws Exception {
        Evaluation testEval = null;
        testEval = new Evaluation(data);
        testEval.crossValidateModel(tree, data, 10, new Random(1));

        return testEval;
    }

    public void classify(Instances data, Classifier tree, char evaluation, int percentage) {
        try {

            Evaluation testEval;

            if (evaluation == 'c') {
                tree.buildClassifier(data);   // build classifier

                saveModel("model.dat", tree);

                testEval = crossValidation(data, tree);
            } else {
                Instances trainData = percentageSplit(data, percentage, false);
                tree.buildClassifier(trainData);

                Instances testData = percentageSplit(data, percentage, true);

                testEval = new Evaluation(trainData);
                testEval.evaluateModel(tree, testData);
            }


            System.out.println(testEval.toSummaryString("\nResults\n======\n", false));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveModel(String fileName, Classifier classifier) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
        oos.writeObject(classifier);
        oos.flush();
        oos.close();
    }

    public Classifier loadModel(String fileName) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));
        Classifier classifier = (Classifier) ois.readObject();
        ois.close();

        return classifier;
    }

    public static void main(String args[]) throws Exception {
        Main m = new Main();
        Instances data = m.loadData("data/weather.nominal.arff");

        MyID3 myID3 = new MyID3();
//        System.out.println(data);
//        System.out.println("Total Entropy : " + myID3.calculateEntropy(data));
//        System.out.println(data.attribute(0));
//        System.out.println("Entropy Outlook : " + myID3.calculateAttributeEntropy(data, data.attribute(0)));
//        System.out.println("Information Gain Outlook : " + myID3.calculateInformationGain(data, data.attribute(0)));
//        System.out.println("Information Gain Temperature : " + myID3.calculateInformationGain(data, data.attribute(1)));
//        System.out.println("Information Gain Humidity : " + myID3.calculateInformationGain(data, data.attribute(2)));
//        System.out.println("Information Gain Windy : " + myID3.calculateInformationGain(data, data.attribute(3)));

        myID3.buildClassifier(data);



//        if (data != null) {
////            System.out.println(data);
//
//            try {
////                Instances newData = m.resample(data);
////                Instances newData = m.removeAttribute(data, 1, false);
////                System.out.println(newData);
//
//                J48 tree = new J48();
//                m.classify(data, tree, 'c', 50);
//
//                Instance unseenData = new Instance(data.numAttributes());
//                data.add(unseenData);
//                unseenData.setDataset(data);
//
//                for (int i = 0; i < data.numAttributes() - 1; i++) {
//                    String[] attribute = data.attribute(i).toString().split(" ");
//                    System.out.print(attribute[1] + " " + attribute[2] + ": ");
//                    Scanner s = new Scanner(System.in);
//                    unseenData.setValue(data.attribute(i), s.nextLine());
//                }
//
//                System.out.println();
//                System.out.println("Instance: " + unseenData);
//                System.out.println();
//
//                Classifier classifier = m.loadModel("model.dat");
//                String[] classAttribute = data.classAttribute().toString().split(" ");
//                System.out.println(classAttribute[1] + " " + classAttribute[2] + ": " + data.classAttribute().value((int) classifier.classifyInstance(unseenData)));
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

    }
}
