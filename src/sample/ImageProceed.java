package sample;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

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

    public void process ()

    {
        //create image as Mat
        Mat gray= new Mat();
        Mat imageCny = new Mat();
        Mat dst = new Mat();
        Mat dilate = new Mat();
        //read image

        //convert to green
        List<Mat> lRgb = new ArrayList<Mat>(3);
        Core.split(imageToProceed, lRgb);
        Mat mG = lRgb.get(1);
        imwrite("C://Users//Piotr//Dysk Google//SEMESTR 6//Informatyka_w_medycynie//Lab//Vessels_Detector//Results//zapisaneGreenChannel.bmp", mG);

        //Imgproc.cvtColor(mG,gray,Imgproc.COLOR_BGR2GRAY);

        //filtr Gausa
        Imgproc.GaussianBlur(mG, dst, new Size(5, 5), 3, 3);

        //convert to alpha - contrast ; beta - brightness
        dst.convertTo(dst, -1, 3.0, 50.0);

        //canny
        Imgproc.Canny(dst, imageCny, 10, 100, 3, true);
        imwrite("C://Users//Piotr//Dysk Google//SEMESTR 6//Informatyka_w_medycynie//Lab//Vessels_Detector//Results//zapisaneGCanny.bmp", imageCny);

        //Dylatacja
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(9, 9));
        Imgproc.dilate(imageCny, dilate, kernel);

        String save = ".\\Results\\" + filename.getName().split("\\.")[0] + ".bmp";
        System.out.println(save);
        imwrite(save, dilate);









    }

}
