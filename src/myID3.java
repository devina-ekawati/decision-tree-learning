import org.w3c.dom.Attr;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.core.matrix.Matrix;

import java.util.Enumeration;
import java.util.LinkedHashSet;

import static java.lang.Math.log;

/**
 * Created by user on 01/10/2016.
 */
public class myID3 extends Classifier {
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

    private double calculateEntropy(Instances data) {
        double result = 0;

        double [] classCounts = new double[data.numClasses()];
        Enumeration instEnum = data.enumerateInstances();
        while (instEnum.hasMoreElements()) {
            Instance inst = (Instance) instEnum.nextElement();
            classCounts[(int) inst.classValue()]++;
        }

        for (int i = 0; i < data.numClasses(); i++) {
            double proportion = classCounts[i]/(double) data.numInstances();
            result -= proportion*log2(proportion);
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

    private double calculateAttributeEntropy(Instances data, Attribute attribute) {
        double result = 0;
        Instances[] splitData = splitData(data, attribute);

        for (int i = 0; i < splitData.length; i++) {
            double probabilistic = splitData[i].numInstances()/data.numInstances();
            result +=  probabilistic * calculateEntropy(splitData[i]);
        }
        return result;
    }


    private double calculateInformationGain(Instances data, Attribute attribute) {
        return (calculateEntropy(data) - calculateAttributeEntropy(data,attribute));
    }

    @Override
    public void buildClassifier(Instances instances) throws Exception {

    }
}
