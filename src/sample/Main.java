package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.opencv.core.Core;
import java.io.File;


public class Main extends Application {

    private static Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Retinal Vessels Detector");
       primaryStage.setScene(new Scene(root, 1000, 700));
        primaryStage.show();
    }

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static File openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz plik do przetworzenia");
        File file = fileChooser.showOpenDialog(stage);
        return file;
    }

    public static void main(String[] args) {
        launch(args);
    }
}