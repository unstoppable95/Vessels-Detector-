package sample;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import weka.*;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.*;

import java.io.BufferedReader;
import java.io.FileReader;
import weka.classifiers.trees.J48;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;


import weka.core.converters.ArffLoader;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;

import static org.opencv.imgcodecs.Imgcodecs.imwrite;

public class ImageProceed {


    private Mat imageToProceed;
    private Mat imageExpert;
    private File filename;

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public Mat getImageToProceed() {
        return imageToProceed;
    }

    public Mat getImageExpert() {
        return imageExpert;
    }

    public ImageProceed(Mat image, File file, File fileExpert){
        this.imageToProceed=image;
        this.filename=file;
        this.imageExpert=Imgcodecs.imread(fileExpert.getPath());
    }

    public void makeLearningInstance(Mat image, Mat expertMask){

        int positive=0;
        int negative=0;
        try {
            PrintWriter out = new PrintWriter(".\\Results\\learnInstance.arff");

            startSave(out);

            for (int i = 0; i < image.rows() - 5; i +=3) {
                for (int j = 0; j < image.cols() - 5; j+=3) {
                    //make small 5x5pxl square
                    Mat tmp = Mat.zeros(5, 5, CvType.CV_8U);
                    for (int x = 0; x < 5; x++) {
                        for (int y = 0; y < 5; y++) {
                            //int npixels = image.total() * image.elemSize();
                            // byte[] pixels = new byte[npixels];
                            //image.get(i+x,j+y,pixels);
                            // Imgproc.CvtColor(source, BlackWhite, Imgproc. ColorBgr2gray);
                            double[] tmp2 = image.get(i + x, j + y);
                            tmp.put(x, y, tmp2);
                        }
                    }

                    //calculates Hu Moments and moments
                    Moments mom = Imgproc.moments(tmp);
                    Mat hu = new Mat();
                    Imgproc.HuMoments(mom, hu);
                    //System.out.println("Central Moments: " +mom.m00 +" "+ mom.m01 +" "+ mom.m02 + " "+mom.m03 +" "+ mom.m10 +" "+ mom.m11 +" "+ mom.m12 +" "+ mom.m20 +" "+ mom.m21 +" "+ mom.m30);

                    //calculate variance
                    Mat avg = Mat.zeros(2, 1, CvType.CV_64FC1);
                    Mat cov = Mat.zeros(2, 2, CvType.CV_64FC1);
                    Core.calcCovarMatrix(tmp, cov, avg, Core.COVAR_COLS);
                    //System.out.println("\n\nCOVAR: "+cov.dump()+"   MEAN: "+avg.dump());
                    //System.out.println("Zaczynam zapisywac");
                    int isVessel = checkArr(expertMask.get(i+2, j+2));
                    if(isVessel==1) positive++;
                    //if(isVessel==0) negative++;

                    if(positive>=negative) {
                        //System.out.println("JESTEM W IFIE");
                        saveLine(out,mom, hu, avg, cov, isVessel);
                        //if(isVessel==1) positive++;
                        if(isVessel==0) negative++;
                    }
                    //if(isVessel==1) System.out.println("True: i= "+i+" j= "+j);

                }
            }
            System.out.println("----- Dla pliku  -----" + "\n" + filename);
            System.out.println("----- Save learnInstance.arff file ! -----");
            out.close();
        } catch( Exception e)
        {
        }


    }

    public double[] process ()
    {
        //create image as Mat
        Mat end= new Mat();
        Mat imageCny = new Mat();
        Mat dst = new Mat();
        Mat dilate = new Mat();

        //read image
        //convert to green
        List<Mat> lRgb = new ArrayList<Mat>(3);
        Core.split(imageToProceed, lRgb);
        Mat mG = lRgb.get(1);

        mG=changeBright(mG);

        //imwrite("C://Users//Piotr//Dysk Google//SEMESTR 6//Informatyka_w_medycynie//Lab//Vessels_Detector//Results//Green.jpg", mG);

//
//        //filtr Gausa
//        Imgproc.GaussianBlur(mG, dst, new Size(5, 5), 3, 3);
//
//        //convert to alpha - contrast ; beta - brightness
//       // dst.convertTo(dst, -1, 3.0, 50.0);
//
//        //canny
//        Imgproc.Canny(dst, imageCny, 10, 100, 3, true);
//        imwrite("C://Users//Piotr//Dysk Google//SEMESTR 6//Informatyka_w_medycynie//Lab//Vessels_Detector//Results//zapisaneGCanny.bmp", imageCny);
//
//        //Dylatacja
//        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(9, 9));
//        Imgproc.dilate(imageCny, dilate, kernel);


        //canny
        Imgproc.Canny(mG, imageCny, 150, 255, 5, true);
        //imwrite("C://Users//Piotr//Dysk Google//SEMESTR 6//Informatyka_w_medycynie//Lab//Vessels_Detector//Results//Canny.jpg", imageCny);

        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3, 3));
        Imgproc.dilate(imageCny, dilate, kernel);

        Imgproc.medianBlur(dilate,dst,3);

        //dilate = new Mat();
        kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(9, 9));
        Imgproc.dilate(dst, end, kernel);

        //Imgproc.GaussianBlur(dilate, end, new Size(9, 9), 5, 5);
        //Imgproc.medianBlur(dilate,end,15);

        //result file save
        String save = ".\\Results\\" + filename.getName().split("\\.")[0] + ".jpg";
        imwrite(save, end);

        double statistic[];

        statistic = calculeteStatistic(imageExpert,end);



        return statistic;

    }


    private static Mat changeBright(Mat mG ){
        for (int i = 0 ; i<mG.rows() ; i++){
            for (int j =0 ; j< mG.cols() ; j++){
                double [] pom = mG.get(i,j);

                for (int k = 0; k < mG.channels(); k++)
                {
                    pom[k] = pom[k] * 0.6;
                }
                mG.put(i,j,pom);
            }
        }
        return  mG;
    }

    private String checkConfusionMartix(int pxlExpert, int pxlResult){
        if(pxlExpert==1 && pxlResult==0) return "fn";
        if(pxlExpert==0 && pxlResult==0) return "tn";
        if(pxlExpert==1 && pxlResult==255) return "tp";
        if(pxlExpert==0 && pxlResult==255) return "fp";
        else return "";
    }

    private int checkArr(double[] channels){
        if(channels[0] ==0.0 && channels[1] ==0.0 && channels[2] ==0.0) return 0;
        else {
            return 1;
        }

    }

    private double calculateAccuracy(double tp, double tn, double fn, double fp ){
        return (tp+tn)/(tp+tn+fn+fp);
    }

    private double calculateSensitivity(double tp, double fn){
        return tp/(tp+fn);
    }

    private double[] calculeteStatistic(Mat imageExpert, Mat image2){
        double tp=0.0;
        double fn=0.0;
        double fp=0.0;
        double tn=0.0;

        for(int i=0; i<imageExpert.rows(); i++){
            for(int j=0; j<imageExpert.cols(); j++){
                //checking confusion matrix
                int channel = (int)image2.get(i,j)[0];
                String x=checkConfusionMartix(checkArr(imageExpert.get(i,j)), channel); //1 -> 0/1    2 -> 0/255
                if(x.equals("tp")) tp++;
                if(x.equals("fn")) fn++;
                if(x.equals("fp")) fp++;
                if(x.equals("tn")) tn++;

            }
        }

        double accuracy = calculateAccuracy(tp,tn,fn,fp); //trafnosc
        double sensitivity = calculateSensitivity(tp,fn); //czulosc
        double specificity = tn/(fp+tn); //swoistosc
        double precision = tp/(tp+fp);

        System.out.println("--- Statystki obrazu " + filename + " ---");
        System.out.println("TP: "+tp+"   FN: "+fn+"   FP: "+fp+"   TN: "+tn);
        System.out.println("Trafnosc: " + accuracy);
        System.out.println ("Czulosc: "+ sensitivity);
        System.out.println("Swoistosc "+specificity);
        System.out.println("Precyzja "+precision);
        System.out.println("--- KONIEC !!! ---");

        double[] result = new double[4];
        result[0]=accuracy;
        result[1]=sensitivity;
        result[2]=specificity;
        result[3]=precision;

        return result;

    }

    private void startSave(PrintWriter out){

            out.println("%");
            out.println("@relation 'dataset'");
            out.println("@attribute hu1 numeric");
            out.println("@attribute hu2 numeric");
            out.println("@attribute hu3 numeric");
            out.println("@attribute hu4 numeric");
            out.println("@attribute hu5 numeric");
            out.println("@attribute hu6 numeric");
            out.println("@attribute hu7 numeric");
            out.println("@attribute centralMoment00 numeric");
            out.println("@attribute centralMoment01 numeric");
            out.println("@attribute centralMoment02 numeric");
            out.println("@attribute centralMoment03 numeric");
            out.println("@attribute centralMoment10 numeric");
            out.println("@attribute centralMoment11 numeric");
            out.println("@attribute centralMoment12 numeric");
            out.println("@attribute centralMoment20 numeric");
            out.println("@attribute centralMoment21 numeric");
            out.println("@attribute centralMoment30 numeric");
            out.println("@attribute mean1 numeric");
            out.println("@attribute mean2 numeric");
            out.println("@attribute mean3 numeric");
            out.println("@attribute mean4 numeric");
            out.println("@attribute mean5 numeric");
            out.println("@attribute isVessel {true,false}");
            out.println("@data");
    }

    private void saveLine(PrintWriter out,Moments mom, Mat hu, Mat avg, Mat cov, int isVessel){
        boolean isV;
        if(isVessel==0) isV=false;
        else isV=true;

        out.println(hu.get(0, 0)[0] + "," + hu.get(1, 0)[0] + "," + hu.get(2, 0)[0] + "," + hu.get(3, 0)[0]
                + "," + hu.get(4, 0)[0] + "," + hu.get(5, 0)[0] + "," + hu.get(6, 0)[0] + ","
                + mom.m00 + "," + mom.m01 + "," + mom.m02 + "," + mom.m03 + "," + mom.m10 + "," +
                mom.m11 + "," + mom.m12 + "," + mom.m20 + "," + mom.m21 + "," + mom.m30 + "," +
                avg.get(0, 0)[0] + "," + avg.get(1, 0)[0] + ","
                + avg.get(2, 0)[0] + "," + avg.get(3, 0)[0] + "," + avg.get(4, 0)[0] + "," + isV);
        }

    //////////////////////////////////////////
    public Instances loadDataset(String path) {
        Instances dataset = null;
        try {
            dataset = DataSource.read(path);
            if (dataset.classIndex() == -1) {
                dataset.setClassIndex(dataset.numAttributes() - 1);
            }
        } catch (Exception ex) {
        }

        return dataset;
    }

    public Classifier buildClassifier(Instances traindataset) {
        MultilayerPerceptron m = new MultilayerPerceptron();
        try {
            m.buildClassifier(traindataset);

        } catch (Exception ex) {
        }
        return m;
    }

    public String evaluateModel(Classifier model, Instances traindataset, Instances testdataset) {
        Evaluation eval = null;
        try {
            // Evaluate classifier with test dataset
            eval = new Evaluation(traindataset);
            eval.evaluateModel(model, testdataset);
        } catch (Exception ex) {
        }
        return eval.toSummaryString("", true);
    }


    public void saveModel(Classifier model, String modelpath) {

        try {
            SerializationHelper.write(modelpath, model);
        } catch (Exception ex) {
        }
    }

    public void makeModel(Mat image){
        int ileInnychNizZero=0;
        Mat image2 =new Mat(image.rows()-4,image.cols()-4,CvType.CV_8U );

        Instances dataset = loadDataset(".\\Results\\learnInstance.arff");
        Filter filter = new Normalize();
        dataset.randomize(new Debug.Random(1));
        System.out.println("TU JESTEM1");
        //Normalize dataset
        try {
            //filter.setInputFormat(dataset);
            //Instances datasetnor = Filter.useFilter(dataset, filter);
            int trainSize = (int) Math.round(dataset.numInstances());//0.8
            int testSize = dataset.numInstances() - trainSize;

            dataset.randomize(new Debug.Random(1));

            //Normalize dataset
            filter.setInputFormat(dataset);
            Instances datasetnor = Filter.useFilter(dataset, filter);
            trainSize=2300;////
            Instances traindataset = new Instances(datasetnor, 0, trainSize);
            Instances testdataset = new Instances(datasetnor, trainSize, testSize);
            // build classifier with train dataset
            ///////////////////////////////////////////
            //MultilayerPerceptron ann = (MultilayerPerceptron) buildClassifier(traindataset); //TU SIE SYPIE
            MultilayerPerceptron ann = new MultilayerPerceptron();/////////////////////////
            ann.buildClassifier(traindataset);/////////////////////////
            ///////////////////////////
            // Evaluate classifier with test dataset
            String evalsummary = evaluateModel(ann, traindataset, testdataset);
            System.out.println("TU JESTEM4");
            System.out.println("Evaluation: " + evalsummary);


            //Save model
            saveModel(ann, ".\\Results\\model.model");
            System.out.println("TU JESTEM5");

            //classifiy a single instance
            //   ModelClassifier cls = new ModelClassifier();
            //   String classname =cls.classifiy(Filter.useFilter(cls.createInstance(hu.get(0, 0)[0] + "," + hu.get(1, 0)[0] + "," + hu.get(2, 0)[0] + "," + hu.get(3, 0)[0]
            //            + "," + hu.get(4, 0)[0] + "," + hu.get(5, 0)[0] + "," + hu.get(6, 0)[0] + ","
            //            + mom.m00 + "," + mom.m01 + "," + mom.m02 + "," + mom.m03 + "," + mom.m10 + "," +
            //           mom.m11 + "," + mom.m12 + "," + mom.m20 + "," + mom.m21 + " " + mom.m30 + "," +
            //          avg.get(0, 0)[0] + "," + avg.get(1, 0)[0] + ","
            //         + avg.get(2, 0)[0] + "," + avg.get(3, 0)[0] + "," + avg.get(4, 0)[0] ), filter),".\\Results\\model.model");
            //

            ////////////////////////////////////////////////////////////////////////////////////////////////////
            System.out.println("TU JESTEM6");
            System.out.println(image.rows()+"  "+image.cols());
            for (int i = 0; i < image.rows() - 5; i++) {
                for (int j = 0; j < image.cols() - 5; j++) {
                    Mat tmp = Mat.zeros(5, 5, CvType.CV_8U);
                    // System.out.println("a");
                    for (int x = 0; x < 5; x++) {
                        for (int y = 0; y < 5; y++) {
                            double[] tmp2 = image.get(i + x, j + y);
                            tmp.put(x, y, tmp2);
                        }
                    }
                    //calculates Hu Moments and moments
                    Moments mom = Imgproc.moments(tmp);
                    Mat hu = new Mat();
                    Imgproc.HuMoments(mom, hu);

                    //calculate variance
                    Mat avg = Mat.zeros(2, 1, CvType.CV_64FC1);
                    Mat cov = Mat.zeros(2, 2, CvType.CV_64FC1);
                    Core.calcCovarMatrix(tmp, cov, avg, Core.COVAR_COLS);
                    // System.out.println("c");
                    //System.out.println("\n\nCOVAR: "+cov.dump()+"   MEAN: "+avg.dump());
                    //System.out.println("Zaczynam zapisywac");
                    //try{
                    ModelClassifier cls = new ModelClassifier();
                    //}
                    // catch(Exception e)
                    // {
                    //    System.out.println("Jestem tu i dupa "+e.getMessage());
                    // }
                    // /*
                    //System.out.println("d");

                    // String classname = cls.classifiy(Filter.useFilter(cls.createInstance(hu.get(0, 0)[0], hu.get(1, 0)[0],
                    //         hu.get(2, 0)[0], hu.get(3, 0)[0], hu.get(4, 0)[0],
                    //         hu.get(5, 0)[0], hu.get(6, 0)[0], mom.m00, mom.m01, mom.m02, mom.m03,
                    //        mom.m10, mom.m11, mom.m12, mom.m20, mom.m21, mom.m30, avg.get(0, 0)[0], avg.get(1, 0)[0],
                    //        avg.get(2, 0)[0], avg.get(3, 0)[0], avg.get(4, 0)[0]), filter),
                    //        ".\\Results\\model.model");

                    List<Double> valuesList = Arrays.asList(hu.get(0, 0)[0], hu.get(1, 0)[0],
                            hu.get(2, 0)[0], hu.get(3, 0)[0], hu.get(4, 0)[0],
                            hu.get(5, 0)[0], hu.get(6, 0)[0], mom.m00, mom.m01, mom.m02, mom.m03,
                            mom.m10, mom.m11, mom.m12, mom.m20, mom.m21, mom.m30, avg.get(0, 0)[0], avg.get(1, 0)[0],
                            avg.get(2, 0)[0], avg.get(3, 0)[0], avg.get(4, 0)[0]);

                    //Instance instance = new Instance((int) Math.pow(valuesList.size()+ 1));
                    Instance instance = new DenseInstance(valuesList.size()+1);
                    instance.setDataset(dataset);
                    for (int z = 0; z < valuesList.size(); z++) {
                        instance.setValue(z, valuesList.get(z));
                    }
//                    System.out.println("xxxxx");
//                    if(i%100==0 && j%100==0) {
//                        System.out.println("Instance: "+instance.toString());
//                    }
//                    System.out.println("yyyyyy");

                    double[] clsLabel = ann.distributionForInstance(instance);

                    double pixel;
                    //System.out.println("Tablica: "+clsLabel[0]+" > "+clsLabel[1]);
                    if(clsLabel[0]>clsLabel[1])
                    {
                        pixel=255.0;
                        ileInnychNizZero++;
                    }
                    else pixel=0.0;

                    if(i%100==0 && j%100==0) {
                        System.out.println("Values list: "+valuesList);
                    }

//                    double[] pixelsToAdd = new double[3];
//                    pixelsToAdd[0]=pixel;
//                    pixelsToAdd[1]=pixel;
//                    pixelsToAdd[2]=pixel;
                    double pixelsToAdd=pixel;
                    image2.put(i,j,pixelsToAdd);

                }
            }

            System.out.println("TU JESTEM 7");
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            //Mat image2=new Mat();

            //Imgproc.cvtColor(image,image2,CvType.CV_8UC3);
            System.out.println("Channels image: "+image2.channels());
            System.out.println("Channels image 1: "+image2.get(100,1000)[0]);

            System.out.println("Ile innych niz 0: "+ileInnychNizZero);

            String fileNAME = ".\\Results\\LEARN" + filename.getName().split("\\.")[0]+".jpg";
            System.out.println(fileNAME);


            imwrite(fileNAME, image2);
            // System.out.println("Image2: "+image2.);
            System.out.println("!!!!ZAPISANE!!!!");

        }catch(Exception e){
            System.out.println("!!!!blad!!!!");
        }

    }

    ///////////////////////////////////

}
