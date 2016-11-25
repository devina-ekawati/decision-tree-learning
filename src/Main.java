import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.Id3;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.instance.RemovePercentage;
import weka.filters.unsupervised.instance.Resample;

import java.io.*;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by Devina Ekawati on 9/28/2016.
 */
public class Main {

    /**
     * Membaca file arff
     * @param filename nama file arff
     * @return Instances hasil pembacaan file arff
     */
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

    /**
     * Menghapus atribut dari dataset
     * @param data dataset yang akan diproses
     * @param attrIndex indeks atribut yang ingin dihapus dari instances
     * @param isInvert true jika indeks atribut yang ingin dihapus diinversi dan false jika indeks atribut yang ingin dihapus tidak diinversi
     * @return Instances yang telah dihapus atributnya
     * @throws Exception
     */
    public Instances removeAttribute(Instances data, String attrIndex, boolean isInvert) throws Exception {
        Remove remove = new Remove();

        remove.setAttributeIndices(String.valueOf(attrIndex));
        remove.setInvertSelection(isInvert);
        remove.setInputFormat(data);
        Instances newData =  Filter.useFilter(data, remove);
        newData.setClassIndex(newData.numAttributes() - 1);

        return newData;
    }

    /**
     * Melakukan resampling terhadap dataset
     * @param data dataset yang akan diproses
     * @return Instances yang telah di-resampling
     * @throws Exception
     */
    public Instances resample(Instances data) throws Exception {
        Resample resample = new Resample();

        resample.setInputFormat(data);
        resample.setNoReplacement(false);
        resample.setSampleSizePercent(100);

        Instances newData =  Filter.useFilter(data, resample);
        newData.setClassIndex(newData.numAttributes() - 1);

        return newData;
    }

    /**
     * Membagi data berdasarkan persentase tertentu
     * @param data dataset yang akan diproses
     * @param percentage persentasi untuk membagi dataset
     * @param isInverted true jika persentase split diinversi dan false jika tidak
     * @return dataset yang telah dibagi
     * @throws Exception
     */
    public Instances percentageSplit(Instances data, int percentage, boolean isInverted) throws Exception {
        RemovePercentage percentageSplit = new RemovePercentage();

        percentageSplit.setInvertSelection(isInverted);
        percentageSplit.setPercentage(percentage);
        percentageSplit.setInputFormat(data);

        Instances newData =  Filter.useFilter(data, percentageSplit);
        newData.setClassIndex(newData.numAttributes() - 1);

        return newData;
    }

    /**
     * Melakukan evaluasi dengan metode 10 fold cross validation
     * @param data data yang akan dievaluasi
     * @param tree jenis classifier yang digunakan untuk melakukan evaluasi
     * @return hasil evaluasi 10 fold cross validation
     * @throws Exception
     */
    public Evaluation crossValidation(Instances data, Classifier tree) throws Exception {
        Evaluation testEval = null;
        testEval = new Evaluation(data);
        testEval.crossValidateModel(tree, data, 10, new Random(1));

        return testEval;
    }

    /**
     * Melakukan klasifikasi, menyimpan model ke dalam file "model.dat", dan mencetak hasilnya ke layar
     * @param data dataset yang digunakan
     * @param tree classifier yang digunakan untuk melakukan klasifikasi
     * @param evaluation 'c' jika evaluasi yang digunakan adalah 10 fold cross validation dan
     *                   'p' jika evaluasi yang digunakan adalah percentage split
     * @param percentage persentase split data untuk evaluasi percentage split
     */
    public void classify(Instances data, Classifier tree, char evaluation, int percentage) {
        try {

            Evaluation testEval = null;

            if (evaluation == 'c') {
                tree.buildClassifier(data);   // build classifier

                testEval = crossValidation(data, tree);
            } else if (evaluation == 'p'){
                Instances trainData = percentageSplit(data, percentage, false);
                tree.buildClassifier(trainData);

                Instances testData = percentageSplit(data, percentage, true);

                testEval = new Evaluation(trainData);
                testEval.evaluateModel(tree, testData);
            }

            saveModel("model.dat", tree);

            System.out.println(testEval.toSummaryString("\nResults\n======\n", false));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Menyimpan model
     * @param fileName nama file model yang akan disimpan
     * @param classifier jenis classifier yang digunakan
     * @throws IOException
     */
    public void saveModel(String fileName, Classifier classifier) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
        oos.writeObject(classifier);
        oos.flush();
        oos.close();
    }

    /**
     * Melakukan load model
     * @param fileName nama fle model yang akan di-load
     * @return classifier hasil dari load model
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Classifier loadModel(String fileName) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));
        Classifier classifier = (Classifier) ois.readObject();
        ois.close();

        return classifier;
    }

    /**
     * Melakukan klasifikasi unseen data (data yang tidak diketahui kelasnya)
     * @param data data yang digunakan untuk melakukan klasifikasi
     * @throws Exception
     */
    public void classifyUnseenData(Instances data) throws Exception {
        Instance unseenData = new Instance(data.numAttributes());
        data.add(unseenData);
        unseenData.setDataset(data);

        for (int i = 0; i < data.numAttributes() - 1; i++) {
            String[] attribute = data.attribute(i).toString().split(" ");
            System.out.print(attribute[1] + " " + attribute[2] + ": ");
            Scanner s = new Scanner(System.in);
            unseenData.setValue(data.attribute(i), s.nextLine());
        }

        System.out.println();
        System.out.println("Instance: " + unseenData);
        System.out.println();

        Classifier classifier = loadModel("model.dat");
        String[] classAttribute = data.classAttribute().toString().split(" ");
        System.out.println(classAttribute[1] + " " + classAttribute[2] + ": " + data.classAttribute().value((int) classifier.classifyInstance(unseenData)));
    }

    public static void main(String args[]) throws Exception {
        Main m = new Main();
        Instances data = m.loadData("data/weather.nominal.arff");

        if (data != null) {
            try {
                System.out.println("----------- ID3 -----------");
                Id3 id3 = new Id3();
                m.classify(data, id3, 'c', 50);
                m.classifyUnseenData(data);

                System.out.println("----------- J48 -----------");
                J48 j48 = new J48();
                m.classify(data, j48, 'p', 50);
                m.classifyUnseenData(data);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
