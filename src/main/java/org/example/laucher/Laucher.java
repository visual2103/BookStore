package org.example.laucher;

import javafx.application.Application;
import javafx.stage.Stage;

public class Laucher extends Application{
    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        LoginComponentFactory.getInstance(false, primaryStage);
        //adminul trebuie adaugat manual in aplicatie
        // scoate orice sql injection
    }
}