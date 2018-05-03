package sample;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.classifiers.Classifier;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.SerializationHelper;
public class ModelClassifier {

    private Attribute hu1;
    private Attribute hu2;
    private Attribute hu3;
    private Attribute hu4;
    private Attribute hu5;
    private Attribute hu6;
    private Attribute hu7;
    private Attribute centralMoment00;
    private Attribute centralMoment01;
    private Attribute centralMoment02;
    private Attribute centralMoment03;
    private Attribute centralMoment10;
    private Attribute centralMoment11;
    private Attribute centralMoment12;
    private Attribute centralMoment20;
    private Attribute centralMoment21;
    private Attribute centralMoment30;
    private Attribute mean1;
    private Attribute mean2;
    private Attribute mean3;
    private Attribute mean4;
    private Attribute mean5;
    private Attribute isVessel;

    private ArrayList attributes;
    private ArrayList classVal;
    private Instances dataRaw;


    public ModelClassifier() {

        hu1 = new Attribute("hu1");
        hu2 = new Attribute("hu2");
        hu3 = new Attribute("hu3");
        hu4 = new Attribute("hu4");
        hu5 = new Attribute("hu5");
        hu6 = new Attribute("hu6");
        hu7 = new Attribute("hu7");
        centralMoment00 = new Attribute("centralMoment00");
        centralMoment01 = new Attribute("centralMoment01");
        centralMoment02 = new Attribute("centralMoment02");
        centralMoment03 = new Attribute("centralMoment03");
        centralMoment10 = new Attribute("centralMoment10");
        centralMoment11 = new Attribute("centralMoment11");
        centralMoment12 = new Attribute("centralMoment12");
        centralMoment20 = new Attribute("centralMoment20");
        centralMoment21 = new Attribute("centralMoment21");
        centralMoment30 = new Attribute("centralMoment30");
        mean1 = new Attribute("mean1");
        mean2 = new Attribute("mean2");
        mean3 = new Attribute("mean3");
        mean4 = new Attribute("mean4");
        mean5 = new Attribute("mean5");
        // isVessel = new Attribute("isVessel");

        attributes = new ArrayList();
        classVal = new ArrayList();

        classVal.add("true");
        classVal.add("false");

        attributes.add(hu1);
        attributes.add(hu2);
        attributes.add(hu3);
        attributes.add(hu4);
        attributes.add(hu5);
        attributes.add(hu6);
        attributes.add(hu7);
        attributes.add(centralMoment00);
        attributes.add(centralMoment01);
        attributes.add(centralMoment02);
        attributes.add(centralMoment03);
        attributes.add(centralMoment10);
        attributes.add(centralMoment11);
        attributes.add(centralMoment12);
        attributes.add(centralMoment20);
        attributes.add(centralMoment21);
        attributes.add(centralMoment30);
        attributes.add(mean1);
        attributes.add(mean2);
        attributes.add(mean3);
        attributes.add(mean4);
        attributes.add(mean5);
        //attributes.add(mean5);
        //attributes.add(isVessel);

        attributes.add(new Attribute("class", classVal));
        dataRaw = new Instances("TestInstances", attributes, 0);
        dataRaw.setClassIndex(dataRaw.numAttributes() - 1);
    }


    public Instances createInstance(double hu1, double hu2, double hu3, double hu4, double hu5, double hu6, double hu7,
                                    double centralMoment00, double centralMoment01, double centralMoment02, double centralMoment03, double centralMoment10
            , double centralMoment11, double centralMoment12, double centralMoment20, double centralMoment30, double centralMoment21, double mean1
            , double mean2, double mean3, double mean4, double mean5) {
        dataRaw.clear();
        double[] instanceValue1 = new double[]{hu1, hu2,hu3,hu4,hu5,hu6,hu7,centralMoment00,centralMoment01,centralMoment02,
                centralMoment03,centralMoment10,centralMoment11,centralMoment12,centralMoment20,centralMoment21,centralMoment30,0};
        dataRaw.add(new DenseInstance(1.0, instanceValue1));
        return dataRaw;
    }


    public String classifiy(Instances insts, String path) {
        String result = "Not classified!!";
        Classifier cls = null;
        try {
            cls = (MultilayerPerceptron) SerializationHelper.read(path);
            result = (String) classVal.get((int) cls.classifyInstance(insts.firstInstance())); //added String casted
        } catch (Exception ex) {
            Logger.getLogger(ModelClassifier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }


    public Instances getInstance() {
        return dataRaw;
    }



}
