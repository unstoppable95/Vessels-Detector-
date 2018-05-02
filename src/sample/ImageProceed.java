package sample;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgcodecs.Imgcodecs.imwrite;

public class ImageProceed {


    private Mat imageToProceed;
    private File filename;

    public ImageProceed(Mat image, File file){
        this.imageToProceed=image;
        this.filename=file;
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
        //System.out.println("EKSPERT: col: "+imageExpert.cols()+" rows: "+imageExpert.rows());
        //System.out.println("NASZ: col: "+image2.cols()+" rows: "+image2.rows());
        //System.out.println("EKSPERT: "+imageExpert.channels());
        //System.out.println("NASZ: "+image2.channels());
        for(int i=0; i<imageExpert.rows(); i++){
            for(int j=0; j<imageExpert.cols(); j++){
                //checking confusion matrix
                int channel = (int)image2.get(i,j)[0];
                //System.out.println("XXX: "+checkArr(imageExpert.get(i,j)));
                //String x=checkConfusionMartix(checkArr(imageExpert.get(i,j)), checkArr(image2.get(i,j)));
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

    public void makeLearningInstance(Mat image, Mat expertMask){


            Mat image2=new Mat();
            Imgproc.cvtColor(image, image2, Imgproc.COLOR_BGR2GRAY);
            for(int i=0; i<image.rows()-5; i+=5){
                for(int j=0; j<image.cols()-5; j+=5) {
                    //make small 5x5pxl square
                    Mat tmp = new Mat();
                    for (int x=0;x<5;x++){
                        for(int y=0; y<5; y++) {

                            double [] tmp2 = image2.get(i+x,j+y);

                            tmp.put(x, y, tmp2);
                        }
                    }


                    String name = ".\\Results\\"+ "tmpI"+i+"J"+j+".bmp" ;
                    imwrite(name, tmp);

                }
            }


    }

    public void process (File fileExpert)
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

        imwrite("C://Users//Piotr//Dysk Google//SEMESTR 6//Informatyka_w_medycynie//Lab//Vessels_Detector//Results//Green.bmp", mG);

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
        imwrite("C://Users//Piotr//Dysk Google//SEMESTR 6//Informatyka_w_medycynie//Lab//Vessels_Detector//Results//Canny.bmp", imageCny);

        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3, 3));
        Imgproc.dilate(imageCny, dilate, kernel);

        Imgproc.medianBlur(dilate,dst,3);

        //dilate = new Mat();
        kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(9, 9));
        Imgproc.dilate(dst, end, kernel);

        //Imgproc.GaussianBlur(dilate, end, new Size(9, 9), 5, 5);
        //Imgproc.medianBlur(dilate,end,15);

        //result file save
        String save = ".\\Results\\" + filename.getName().split("\\.")[0] + ".bmp";
        imwrite(save, end);


        //statystyki i maszine learning
        Mat imageExpert = Imgcodecs.imread(fileExpert.getPath());

        double statistic[]=new double[3];
        statistic = calculeteStatistic(imageExpert,end);
        //makeLearningInstance(imageExpert,end);



    }

}
