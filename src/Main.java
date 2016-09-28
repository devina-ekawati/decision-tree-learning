import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
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
        J48 treeJ48 = new J48();         // new instance of tree
        try {
            treeJ48.buildClassifier(data);   // build classifier

            Evaluation testEval = new Evaluation(data);

            testEval.crossValidateModel(treeJ48, data, 10, new Random(1));
            System.out.println(testEval.toSummaryString("\nResults\n======\n", false));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        Instances data = loadData("data/weather.nominal.arff");

        if (data != null) {
            System.out.println(data);

            J48(data);
        }

    }
}
