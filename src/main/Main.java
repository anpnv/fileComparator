package main;

import javafx.application.Application;
import javafx.stage.Stage;

import model.*;
import view.*;
import mvvm.*;

/**
 *
 * @author PONAMAREV Andrei - ROBERFROID Julien
 */
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        new View(primaryStage, new VM(new Model()));
    }

}
