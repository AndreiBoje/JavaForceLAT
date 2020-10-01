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


        FNodeManager fnm = new FNodeManager(gc);
        FParser fp = new FParser(ta,fnm);
        fp.beginFParse();



        //fnm.initInteractivity();
        /*fnm.addFNode(100, 230, 0);
        fnm.addFNode(300, 230, 1);
        fnm.addFNode(500, 230, 2);
        fnm.addFNode(700, 230, 3);

        fnm.jprFConnection(0, 3,"asd");
        fnm.unidFConnection(1, 2,"a,b,...,z");
        fnm.bidFConnection(0, 1,"ab","cd");
        fnm.selfFConnection(3,"aaa");

        fnm.display();*/

        ScrollPane sp = new ScrollPane(canvas);
        sp.setPannable(true);
        HBox vb = new HBox(sp,ta);
        Scene scene = new Scene(vb, width, height);
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
