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
        HashSet<KeyCode> kb = new HashSet<>();

        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        FNodeManager fnm = new FNodeManager(gc);
        fnm.initListeners();
        fnm.setConnectionColor(Color.RED);
       // int id1 = fnm.addFNode(new Point2D(100,280),30,Color.RED);
        //int id2 = fnm.addFNode(new Point2D(600,480),30,Color.RED);

        //fnm.addConnection(id1,id2);
        //fnm.addConnection(id1,id1);
        //fnm.addConnection(id2,id2);
        //fnm.addConnection(id2,id1);
        fnm.display();






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
