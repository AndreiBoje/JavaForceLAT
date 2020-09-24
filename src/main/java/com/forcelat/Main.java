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
        fnm.setBezierHeight(10);
        fnm.setConnectionColor(Color.BLUE);


        canvas.setOnMouseReleased(e -> {
            //place
            if (e.getButton() == MouseButton.PRIMARY && !isDragging) {
                Point2D pos = new Point2D(e.getX(), e.getY());
                int currID = fnm.addFNode(pos, 30, Color.BLUE);

                if (fnm.getFNodeCount() > 1)
                    fnm.addConnection(prevID.pop(), currID);

                prevID.push(currID);
                fnm.display();
            }
        });

        canvas.setOnMousePressed(e -> {
            isDragging = false;
            selectedFNode = -1;
        });

        canvas.setOnMouseDragged(e -> {

            //fnm.selectAndMove(pos);
            isDragging = true;
            //select nearest fnode and move it
            if (e.getButton() == MouseButton.PRIMARY) {
                Point2D pos = new Point2D(e.getX(), e.getY());

                if (selectedFNode == -1)
                    selectedFNode = fnm.getFNodeInRange(pos, 30);
                if (selectedFNode != -1)
                    fnm.moveFNodeTo(selectedFNode, pos);
                fnm.display();
            }
        });

        StackPane sp = new StackPane(canvas);
        Scene scene = new Scene(sp, width, height);


        scene.addEventHandler(KeyEvent.KEY_RELEASED, (key) -> {
            //
        });

        window.setScene(scene);
        window.setTitle("NFA");
        window.show();
    }
}
