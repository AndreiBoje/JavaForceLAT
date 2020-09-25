package com.forcelat;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;


public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }


    Stack<Integer> prevID = new Stack<>();

    boolean isDragging = false;
    int selectedFNode = -1;

    @Override
    public void start(Stage window) throws Exception {
        double width = 900;
        double height = 600;

        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        FNodeManager fnm = new FNodeManager(gc);
       //fnm.initListeners();
        fnm.setConnectionColor(Color.BLUE);
        int id1 = fnm.addFNode(new Point2D(400,80),30,Color.RED);
        int id2 = fnm.addFNode(new Point2D(300,480),30,Color.RED);
        int id3 = fnm.addFNode(new Point2D(700,180),30,Color.RED);
        int id4 = fnm.addFNode(new Point2D(200,180),30,Color.RED);
        fnm.addConnection(id1,id2);
        fnm.addConnection(id2,id1);
        fnm.addConnection(id3,id3);
        fnm.display();

        StackPane sp = new StackPane(canvas);
        Scene scene = new Scene(sp, width, height);

        window.setScene(scene);
        window.setTitle("NFA");
        window.show();
    }
}
