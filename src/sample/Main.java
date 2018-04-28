package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.CvType;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Size;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;

public class Main //extends Application {
{

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    //@Override
    //public void start(Stage primaryStage) throws Exception{
        /*
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();*/
    // }

        public static void main(String[] args) throws IOException {
            //launch(args)

            //create image as Mat
            Mat image;
            Mat imageCny = new Mat();
            Mat dst = new Mat();
            Mat dilate = new Mat();
            //read image
            image =Imgcodecs.imread("C://Users//Piotr//Dysk Google//SEMESTR 6//Informatyka_w_medycynie//Lab//Vessels_Detector//EyesPictures//01_h.jpg");

            //convert to green
            List<Mat> lRgb = new ArrayList<Mat>(3);
            Core.split(image, lRgb);
            Mat mG = lRgb.get(1);
            imwrite("C://Users//Piotr//Dysk Google//SEMESTR 6//Informatyka_w_medycynie//Lab//Vessels_Detector//Results//zapisaneGreyChannel.bmp",mG);

            //filtr Gausa
            Imgproc.GaussianBlur(mG,dst,new Size(5,5), 3,3);

            //convert to alpha - contrast ; beta - brightness
            dst.convertTo(dst,-1,3.0,50.0);

            //canny
            Imgproc.Canny(dst, imageCny, 10, 100, 3, true);
            imwrite("C://Users//Piotr//Dysk Google//SEMESTR 6//Informatyka_w_medycynie//Lab//Vessels_Detector//Results//zapisaneGCanny.bmp",imageCny);

            //Dylatacja
            Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(9, 9));
            Imgproc.dilate(imageCny,dilate, kernel);
            imwrite("C://Users//Piotr//Dysk Google//SEMESTR 6//Informatyka_w_medycynie//Lab//Vessels_Detector//Results//zapisaneGCannyDilata.bmp",dilate);
            

    }


}