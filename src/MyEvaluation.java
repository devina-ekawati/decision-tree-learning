import model.Tree;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.util.List;

/**
 * Created by Tifani on 10/7/2016.
 */
public class MyEvaluation {

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
            if (i < instances.numInstances() % folds) {
                last++;
            }
            int toCopy = last - start;
            testingset = new Instances(instances, start, toCopy);
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
            } else {
                MyJ48 myJ48 = new MyJ48();
                try {
                    myJ48.buildClassifier(trainingset);
                    Tree tree = myJ48.getTree();
                    double acc = myJ48.calculateAccuracy(testingset, tree);
                    sumacc += acc;
                    if (acc > maxacc)
                        maxacc = acc;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        float avgacc = (float)sumacc/(float)folds;

        System.out.println("Cross validation with " + folds + " fold(s) ");
        System.out.println("Average accuracy: "+avgacc*100+"%");
        System.out.println("Max accuracy: "+maxacc*100+"%");
        System.out.println();
    }


}
