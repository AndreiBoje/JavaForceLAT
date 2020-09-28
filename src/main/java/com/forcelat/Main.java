package com.forcelat;

import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import testing.forcelat.FNodeManager2;

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

        Canvas canvas = new Canvas(900, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();


        FNodeManager2 fnm = new FNodeManager2(gc);
        fnm.initInteractivity();
        fnm.addFNode(100,230,0);
        fnm.addFNode(300,230,1);
        fnm.addFNode(500,230,2);

        //fnm.selfFConnection(0);
        //fnm.selfFConnection(2);
        //fnm.bidFConnection(1,2);
        fnm.unidFConnection(0,1);
        //fnm.jprFConnection(2,0);
        fnm.display();



        VBox vb = new VBox(canvas);
        Scene scene = new Scene(vb, 900, 600);
        window.setScene(scene);
        window.setTitle("NFA");
        window.show();


    }
}




//def -rad 30 -col ff0000 <- override some default values
//add -id 0 -pos 300 200 -txt "This is text" -rad 30 -col ff0000  <- last 2 can be set by default
//add -id 1 -pos 400 200 -txt "This is text2" -rad 30 -col ff0000 <- first 2 mandatory
//con -from 0 -to 1 -txt "This is conn text" -size 20 -col ff0000 <- first 2 mandatory
//con -from 0 -to 1 -txt "This is conn text" -size 20 -col ff0000 <- last 2 can be set by default
//canvas.addEventFilter(MouseEvent.MOUSE_CLICKED, (e) -> canvas.requestFocus());
//canvas.addEventFilter(MouseEvent.MOUSE_DRAGGED, (e) -> canvas.requestFocus());
