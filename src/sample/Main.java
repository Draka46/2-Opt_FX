package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double edge = 5;
        double width = screenSize.getWidth() - (screenSize.getWidth() / edge);
        double height = screenSize.getHeight() - (screenSize.getHeight() / edge);

        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("2-Opt");
        primaryStage.setScene(new Scene(root, width, height));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
