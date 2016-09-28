import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.Id3;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import java.util.Random;

/**
 * Created by Devina Ekawati on 9/28/2016.
 */
public class Main {

    public static Instances loadData(String filename) {
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

    public static void J48(Instances data) {
        J48 tree = new J48();         // new instance of tree
        try {
            tree.buildClassifier(data);   // build classifier

            Evaluation testEval = new Evaluation(data);

            testEval.crossValidateModel(tree, data, 10, new Random(1));
            System.out.println(testEval.toSummaryString("\nResults\n======\n", false));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ID3(Instances data) {
        Id3 tree = new Id3();         // new instance of tree
        try {
            tree.buildClassifier(data);   // build classifier

            Evaluation testEval = new Evaluation(data);

            testEval.crossValidateModel(tree, data, 10, new Random(1));
            System.out.println(testEval.toSummaryString("\nResults\n======\n", false));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        Instances data = loadData("data/weather.nominal.arff");

        if (data != null) {
            System.out.println(data);

            ID3(data);
        }

    }
}
