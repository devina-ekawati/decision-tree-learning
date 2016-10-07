import model.Tree;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.util.List;

/**
 * Created by Tifani on 10/7/2016.
 */
public class MyEvaluation {

    public static int[][] confusionMatrix(Instances dataset, Tree tree){
        int nclass = dataset.numClasses();
        int natr = dataset.numAttributes();
        int[][] matrix = new int[nclass][nclass];

        for (int j=0; j<nclass; j++){
            for (int k=0; k<nclass; k++){
                matrix[j][k]=0;
            }
        }

        for (int i=0; i < dataset.numInstances(); i++){
            double dataclass = dataset.instance(i).value(natr);
            for (int j=0; j<nclass; j++){ //actual values
                for (int k=0; k<nclass; k++){ //prediction
//                    if (dataclass == dataset.attribute(dataset.numAttributes()-1).value(j) && result.get(i).equals(classValues.get(k))){
//                        matrix[j][k]++;
//                    }
                }
            }
        }

        return matrix;
    }

    public static void crossValidation(Instances instances, int folds, Classifier classifier) {
        double sumacc = 0;
        double maxacc = 0;
        float [][] matrix = new float[instances.numClasses()][instances.numClasses()];
        int last = 0;
        int start;
        Instances trainingset = null;
        Instances testingset;

        // Initializing confusion matrix
        for (int j=0; j<instances.numClasses(); j++){
            for (int k=0; k<instances.numClasses(); k++){
                matrix[j][k] = 0;
            }
        }

        // Classify
        for (int i=0;i<folds;i++) {
            start = last;
            last = start + instances.numInstances()/folds;
            // System.out.println("i: " + i + " start: " + start + " last: " + last);
            if (i < instances.numInstances() % folds) {
                last++;
            }
            int toCopy = last - start;
            testingset = new Instances(instances, start, toCopy);
            System.out.println("tes set: " + start + " " + last);
            trainingset = null;
            if (start>0) {
                trainingset = new Instances(instances, 0, start);
                for(int j=last; j<instances.numInstances(); j++) {
                    trainingset.add(instances.instance(j));
                }
            } else {
                toCopy = instances.numInstances() - last;
                trainingset = new Instances(instances, last, toCopy);
            }

            if (classifier.getClass() == MyID3.class) {
                MyID3 myID3 = new MyID3();
                try {
                    myID3.buildClassifier(trainingset);
                    Tree tree = myID3.getTree();
                    double acc = myID3.calculateAccuracy(testingset, tree);
                    sumacc += acc;
                    if (acc > maxacc)
                        maxacc = acc;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

//            int[][] cmatrix = confusionMatrix(testingset,testSet(testingset,createModel(trainingset)));
//            for (int j=0; j<classValues.size(); j++){
//                for (int k=0; k<classValues.size(); k++){
//                    matrix[j][k] += (float)cmatrix[j][k];
//                }
//            }
        }
        float avgacc = (float)sumacc/(float)folds;
        int correct = 0;
        int incorrect = 0;
        for (int j=0; j<instances.numClasses(); j++){
            for (int k=0; k<instances.numClasses(); k++){
                if (j==k) correct+=matrix[j][k];
                else incorrect+=matrix[j][k];
            }
        }
//        System.out.println("Correctly Classified: "+correct);
//        System.out.println("Incorrectly Classified: "+incorrect);
//        System.out.println();
        System.out.println("Cross validation with " + folds + " fold(s) ");
        System.out.println("Average accuracy: "+avgacc*100+"%");
        System.out.println("Max accuracy: "+maxacc*100+"%");
        System.out.println();
//        System.out.println("Confusion Matrix");
//        for (int i=0; i<classValues.size();i++){
//            writer.print((char)(97+i) + "\t");
//        }
//        System.out.println("<-- classified as");
//        for (int j=0; j<classValues.size(); j++){
//            for (int k=0; k<classValues.size(); k++){
//                System.out.printf("%.0f",matrix[j][k]);
//                System.out.print("\t");
//            }
//            System.out.println("|  "+(char)(97+j) +" = " + classValues.get(j));
//        }
    }
}
