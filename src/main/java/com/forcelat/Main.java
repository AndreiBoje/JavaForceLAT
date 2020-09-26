package com.forcelat;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;


public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage window) throws Exception {
        double width = 900;
        double height = 600;

        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        FNodeManager fnm = new FNodeManager(gc);
        fnm.initListeners();

        fnm.addFNode(new Point2D(100, 280), "A", 30, Color.RED, 0);
        fnm.addFNode(new Point2D(600, 280), "B", 30, Color.RED, 1);
        fnm.addFNode(new Point2D(200, 480), "C", 30, Color.RED, 2);
        fnm.addFNode(new Point2D(500, 480), "D", 30, Color.RED, 3);


        //def -rad 30 -col ff0000 <- override some default values
        //add -id 0 -pos 300 200 -txt "This is text" -rad 30 -col ff0000  <- last 3 can be set by default
        //add -id 1 -pos 400 200 -txt "This is text2" -rad 30 -col ff0000 <- first 2 mandatory
        //con -from 0 -to 1 -txt "This is conn text" -size 20 -col ff0000 <- first 2 mandatory
        //con -from 0 -to 1 -txt "This is conn text" -size 20 -col ff0000 <- last 2 can be set by default


        fnm.addConnection(0, 1, "Heila");
        fnm.addConnection(1, 1, "abc");
        fnm.addConnection(1, 2, "b");
        fnm.addConnection(0,3,"DDD");
        fnm.addConnection(1, 0, "Hola");
        fnm.display();

        //FParser fp = new FParser(tb)
        //while(fp.hasCommands()){
        /*
           switch(fp.getNextCommand()){
                case "add" :
                    fnm.addFNode(fp.getArg(0),fp.getArg(1),fp.getArg(2),fp.getArg(3),fp.getArg(4));
                    fp.consumeCommand();
                    break;
                case "con" :
                    fnm.addConnection(fp.getArg(0),fp.getArg(1),fp.getArg(2),fp.getArg(3),fp.getArg(4));
                    break;
                case "def" :
                    fnm.setDefaultRadius(fp.getArg(0));
                    fnm.setDefaultNodeColor(fp.getArg(1));
                    fnm.setDefaultConnColor(fp.getArg(2));  //order matters while pushing the args
                    ...
                    break;
           }
           fp.consumeCommand();
        }
         */

        TextArea a = new TextArea("ceva");
        VBox sp = new VBox(canvas);
        Scene scene = new Scene(sp, width, height);
        //canvas.addEventFilter(MouseEvent.MOUSE_CLICKED, (e) -> canvas.requestFocus());
        //canvas.addEventFilter(MouseEvent.MOUSE_DRAGGED, (e) -> canvas.requestFocus());
        window.setScene(scene);
        window.setTitle("NFA");
        window.show();
    }
}
