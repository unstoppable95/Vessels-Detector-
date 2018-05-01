package sample;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.awt.*;
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

        imwrite("C://Users//Piotr//Dysk Google//SEMESTR 6//Informatyka_w_medycynie//Lab//Vessels_Detector//Results//Green.bmp", mG);

/*
        //filtr Gausa
        Imgproc.GaussianBlur(mG, dst, new Size(5, 5), 3, 3);

        //convert to alpha - contrast ; beta - brightness
       // dst.convertTo(dst, -1, 3.0, 50.0);

        //canny
        Imgproc.Canny(dst, imageCny, 10, 100, 3, true);
        imwrite("C://Users//Piotr//Dysk Google//SEMESTR 6//Informatyka_w_medycynie//Lab//Vessels_Detector//Results//zapisaneGCanny.bmp", imageCny);

        //Dylatacja
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(9, 9));
        Imgproc.dilate(imageCny, dilate, kernel);
*/

        //canny
        Imgproc.Canny(mG, imageCny, 150, 255, 5, true);
        imwrite("C://Users//Piotr//Dysk Google//SEMESTR 6//Informatyka_w_medycynie//Lab//Vessels_Detector//Results//Canny.bmp", imageCny);

        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3, 3));
        Imgproc.dilate(imageCny, dilate, kernel);

        Imgproc.medianBlur(dilate,dst,3);

        dilate = new Mat();
        kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(9, 9));
        Imgproc.dilate(dst, end, kernel);

        //Imgproc.GaussianBlur(dilate, end, new Size(9, 9), 5, 5);
        //Imgproc.medianBlur(dilate,end,15);

        //result file save
        String save = ".\\Results\\" + filename.getName().split("\\.")[0] + ".bmp";
        imwrite(save, end);



    }

}
