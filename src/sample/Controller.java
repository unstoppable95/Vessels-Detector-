package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;





public class Controller {


    public static class TableValues {
        public double getA() {
            return a;
        }

        public double getB() {
            return b;
        }

        public double getC() {
            return c;
        }

        public double getD() {
            return d;
        }

        private double a;
        private double b;
        private double c;
        private double d;

        public TableValues(double a1,double b1, double c1, double d1) {
            this.a=a1;
            this.b = b1;
            this. c= c1;
            this.d = d1;
        }

    }



    private File fileName;
    private File fileExpert;
    private File fileResult;
    private File fileResultLearn;
    private File fileEyeMask;

    //make instance files
    private File fileLearnInstance;
    private File fileExpertLearnInstance;

    public Button buttonChooseFile;
    public Button proceedButton;
    public Button buttonLearnIstance;
    public ImageView outFile;
    public ImageView inFile;
    public ImageView expertFile;
    public ImageView learnFile;
    public Label labelIn;
    public Label labelOut;
    public Label labelExpert;
    public Label labelLearn;
    public TableView<TableValues> parametres;

    public void learnInstance(ActionEvent event){
        fileLearnInstance=Main.openFileChooser();
        fileExpertLearnInstance=Menager.fileExpert(fileLearnInstance);
        fileEyeMask = Menager.fileEye(fileLearnInstance);

        Mat learnImage = Imgcodecs.imread(fileLearnInstance.getPath());

        ImageProceed myImage = new ImageProceed(learnImage,fileLearnInstance,fileExpertLearnInstance,fileEyeMask);
        myImage.makeLearningInstance(myImage.getImageToProceed(), myImage.getImageExpert(), myImage.getEyeMask());
    }


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

        fileEyeMask = Menager.fileEye(fileName);

        ImageProceed myImage = new ImageProceed(image1,fileName,fileExpert,fileEyeMask);

        double [] stat = myImage.process();



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



        double [] statModel= myImage.makeModel(myImage.getImageToProceed());




        fileResultLearn= Menager.fileLearn(fileName);
        BufferedImage imageL =null;
        try {
            imageL = ImageIO.read(fileResultLearn);
        }
        catch (Exception e ){
            System.out.println("Cos zle poszlo");
        }
        Image imageGUILearn= SwingFXUtils.toFXImage(imageL, null);

        //add results to GUI
        learnFile.setImage(imageGUILearn);
        labelLearn.setText("Obrazek po WECE");


        //tableView menagment
        final ObservableList<TableValues> data =
                FXCollections.observableArrayList(
                        new TableValues(stat[0],stat[1],stat[2],stat[3]),
                        new TableValues(statModel[0],statModel[1],statModel[2],statModel[3])
                );

        TableColumn accuracy = new TableColumn("Acc");
        TableColumn sensitivity = new TableColumn("Sens");
        TableColumn specificity = new TableColumn("Spec");
        TableColumn precision = new TableColumn("Prec");

        accuracy.prefWidthProperty().bind(parametres.widthProperty().multiply(0.25));
        sensitivity.prefWidthProperty().bind(parametres.widthProperty().multiply(0.25));
        specificity.prefWidthProperty().bind(parametres.widthProperty().multiply(0.25));
        precision.prefWidthProperty().bind(parametres.widthProperty().multiply(0.25));
        parametres.getColumns().addAll(accuracy,sensitivity,specificity,precision);

        accuracy.setCellValueFactory(
                new PropertyValueFactory<TableValues,Double>("a")
        );
        sensitivity.setCellValueFactory(
                new PropertyValueFactory<TableValues,Double>("b")
        );
        specificity.setCellValueFactory(
                new PropertyValueFactory<TableValues,Double>("c")
        );
        precision.setCellValueFactory(
                new PropertyValueFactory<TableValues,Double>("d")
        );

        parametres.setItems(data);
        parametres.setVisible(true);




    }


}
