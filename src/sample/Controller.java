package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import javafx.scene.control.Button;

import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;



public class Controller {

    private File fileName;
    private File fileExpert;
    private File fileResult;

    public Button buttonChooseFile;
    public Button proceedButton;
    public ImageView outFile;
    public ImageView inFile;
    public ImageView expertFile;
    public Label labelIn;
    public Label labelOut;
    public Label labelExpert;


    public void chooseFile(ActionEvent event){
        fileName=Main.openFileChooser();
        BufferedImage in=null;
        try{
        in= ImageIO.read(fileName);}
        catch (Exception ex){
            System.out.println("Go wrong");
        }
        Image inImage =  SwingFXUtils.toFXImage(in, null);
        inFile.setImage(inImage);
        labelIn.setText("Obrazek wejsciowy");


        labelExpert.setText("Obrazek eksperta");
        fileExpert=Menager.fileExpert(fileName);
        BufferedImage inEx=null;
        try{
            inEx= ImageIO.read(fileExpert);
        }
        catch (Exception ex){
            System.out.println("Go wrong");
        }
        Image inImage1 =  SwingFXUtils.toFXImage(inEx, null);
        expertFile.setImage(inImage1);

    }

    public void proceed (ActionEvent event){
        Mat image1 = Imgcodecs.imread(fileName.getPath());
        ImageProceed myImage = new ImageProceed(image1,fileName,fileExpert);
        myImage.process();

        fileResult= Menager.fileResult(fileName);
        BufferedImage image =null;
       try {
           image = ImageIO.read(fileResult);
       }
       catch (Exception e ){
           System.out.println("Cos zle poszlo");
       }
       Image imageGUI= SwingFXUtils.toFXImage(image, null);

       //add results to GUI
       outFile.setImage(imageGUI);
       labelOut.setText("Obrazek po przetworzeniu");
    }


}
