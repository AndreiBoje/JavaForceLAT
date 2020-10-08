package com.forcelat;

import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import javafx.application.Application;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;


public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage window) throws Exception {
        double width = 900;
        double height = 600;

        TextArea ta = new TextArea();
        ta.setMinWidth(300);
        Canvas canvas = new Canvas(1500, 1000);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        ScrollPane sp = new ScrollPane(canvas);

        FNodeManager fnm = new FNodeManager(gc,sp);
        FParser fp = new FParser(ta, fnm);
        fp.beginFParse();

        HBox vb = new HBox(sp, ta);
        Scene scene = new Scene(vb, width, height);

        window.setScene(scene);
        window.setTitle("NFA");
        window.show();


    }
}