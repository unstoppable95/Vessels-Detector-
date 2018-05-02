package sample;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgcodecs.Imgcodecs.imwrite;

public class ImageProceed {


    private Mat imageToProceed;
    private File filename;
    private File fileExpert;

    public ImageProceed(Mat image, File file, File filex){
        this.imageToProceed=image;
        this.filename=file;
        this.fileExpert=filex;
    }


    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static Mat changeBright(Mat mG ){
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

    public String checkConfusionMartix(int pxlExpert, int pxlResult){
        if(pxlExpert==1 && pxlResult==0) return "fn";
        if(pxlExpert==0 && pxlResult==0) return "tn";
        if(pxlExpert==1 && pxlResult==255) return "tp";
        if(pxlExpert==0 && pxlResult==255) return "fp";
        else return "";
    }

    public int checkArr(double[] channels){
        if(channels[0] ==0.0 && channels[1] ==0.0 && channels[2] ==0.0) return 0;
        else {
            return 1;
        }

    }
    public double calculateAccuracy(double tp, double tn, double fn, double fp ){
        return (tp+tn)/(tp+tn+fn+fp);
    }

    public double calculateSensitivity(double tp, double fn){
        return tp/(tp+fn);
    }

    public double[] calculeteStatistic(Mat imageExpert, Mat image2){
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

        System.out.println("TP: "+tp+"   FN: "+fn+"   FP: "+fp+"   TN: "+tn);
        System.out.println("Trafnosc: " + accuracy);
        System.out.println ("Czulosc: "+ sensitivity);
        System.out.println("Swoistosc "+specificity);
        System.out.println("Precyzja "+precision);

        double[] result = new double[4];
        result[0]=accuracy;
        result[1]=sensitivity;
        result[2]=specificity;
        result[3]=precision;

        return result;

    }
    public void startSave(PrintWriter out){

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
            ////COVARIANCE DIDNT ADDED
            out.println("@attribute mean1 numeric");
            out.println("@attribute mean2 numeric");
            out.println("@attribute mean3 numeric");
            out.println("@attribute mean4 numeric");
            out.println("@attribute mean5 numeric");
            out.println("@attribute isVessel {true,false}");
            out.println("@data");

    }


    public void saveLine(PrintWriter out,Moments mom, Mat hu, Mat avg, Mat cov, int isVessel){
        boolean isV;
        if(isVessel==0) isV=false;
        else isV=true;

        out.println(hu.get(0, 0)[0] + "," + hu.get(1, 0)[0] + "," + hu.get(2, 0)[0] + "," + hu.get(3, 0)[0]
                + "," + hu.get(4, 0)[0] + "," + hu.get(5, 0)[0] + "," + hu.get(6, 0)[0] + ","
                + mom.m00 + "," + mom.m01 + "," + mom.m02 + "," + mom.m03 + "," + mom.m10 + "," +
                mom.m11 + "," + mom.m12 + "," + mom.m20 + "," + mom.m21 + " " + mom.m30 + "," +
                avg.get(0, 0)[0] + "," + avg.get(1, 0)[0] + ","
                + avg.get(2, 0)[0] + "," + avg.get(3, 0)[0] + "," + avg.get(4, 0)[0] + "," + isV);

        }




    public void makeLearningInstance(Mat image, Mat expertMask){

    try {
        PrintWriter out = new PrintWriter(".\\Results\\data.txt");
        System.out.println("Zaczynam zapisywac");
        startSave(out);
        System.out.println("zapisalem wstep");
        for (int i = 0; i < image.rows() - 5; i += 5) {
            for (int j = 0; j < image.cols() - 5; j += 5) {
                //make small 5x5pxl square
                //  Mat tmp =new Mat(5,5,CvType.CV_8UC3 );
                Mat tmp = new Mat(5, 5, CvType.CV_8U);

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

                Mat avg = Mat.zeros(2, 1, CvType.CV_64FC1);
                Mat cov = Mat.zeros(2, 2, CvType.CV_64FC1);
                Core.calcCovarMatrix(tmp, cov, avg, Core.COVAR_COLS);

                int isVessel = checkArr(expertMask.get(i, j));
                saveLine(out,mom, hu, avg, cov, isVessel);
            }
        }
        System.out.println("DONE");
        out.close();
    }
    catch (Exception ex ){}


    }

    public void process ()
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

        imwrite("C://Users//Piotr//Dysk Google//SEMESTR 6//Informatyka_w_medycynie//Lab//Vessels_Detector//Results//Green.jpg", mG);

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
        imwrite("C://Users//Piotr//Dysk Google//SEMESTR 6//Informatyka_w_medycynie//Lab//Vessels_Detector//Results//Canny.jpg", imageCny);

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


        //statystyki i maszine learning
        Mat imageExpert = Imgcodecs.imread(fileExpert.getPath());

        double statistic[]=new double[3];

        statistic = calculeteStatistic(imageExpert,end);


        makeLearningInstance(imageToProceed,imageExpert);

    }



}
