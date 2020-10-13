package com.forcelat;

import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.application.Application;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;


public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    boolean isSelected = false;

    @Override
    public void start(Stage window) throws Exception {
        double width = 1280;
        double height = 720;

        TextArea mainTA = new TextArea();
        Canvas mainCanvas = new Canvas(1000, 1000);
        ScrollPane mainSP = new ScrollPane(mainCanvas);
        HBox scrollHB = new HBox(mainSP);
        HBox mainHB = new HBox(scrollHB, mainTA);
        scrollHB.setBorder(new Border(new BorderStroke(new Color(224 / 255f, 224 / 255f, 224 / 255f, 1),
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 10, 0, 0))));

        mainTA.setMinWidth(300);
        HBox.setHgrow(mainTA, Priority.ALWAYS);
        HBox.setHgrow(mainSP, Priority.ALWAYS);

        mainSP.setMinWidth(300);
        addResizeListeners(scrollHB, 10);

        Menu file = new Menu("File");
        MenuItem save = new MenuItem("Save");
        MenuItem load = new MenuItem("Load");
        file.getItems().addAll(save, load);
        Menu ctx = new Menu("Context");
        MenuItem clear = new MenuItem("Clear");
        MenuItem imgSize = new MenuItem("Image size..");
        MenuItem exportImg = new MenuItem("Export image..");
        ctx.getItems().addAll(clear, imgSize, exportImg);
        MenuBar menuBar = new MenuBar(file, ctx);
        VBox mainVB = new VBox(menuBar, mainHB);

        FNodeManager fnm = new FNodeManager(mainCanvas.getGraphicsContext2D(), mainSP);
        FParser fp = new FParser(mainTA, fnm);
        fnm.initInteractivity();
        fp.beginFParse();


        save.setOnAction(e -> {
            final FileChooser fc = new FileChooser();
            fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("FLAT", "*.flat"));
            File saveFile = fc.showSaveDialog(window);
            if(saveFile==null)
                return;

            FSerializer fser = new FSerializer();
            fser.serialize(mainTA.getText(), fnm,saveFile);
        });

        load.setOnAction(e -> {
            //TODO: Open window and choose where to load from
            final FileChooser fc = new FileChooser();
            fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("FLAT", "*.flat"));
            File loadFile = fc.showOpenDialog(window);
            if(loadFile==null)
                return;
            FSerializer fser = new FSerializer();
            fser.deserialize(mainTA, fnm,loadFile);
            fnm.display();
        });

        Scene scene = new Scene(mainVB, width, height);

        window.setScene(scene);
        window.setTitle("ForceLat");
        window.show();
    }

    public void addResizeListeners(HBox m, int handleSize) {
        m.addEventFilter(MouseEvent.MOUSE_MOVED, e -> {
            if (e.getSceneX() >= m.getWidth() - handleSize)
                m.setCursor(Cursor.MOVE);
            else
                m.setCursor(Cursor.DEFAULT);
        });
        m.addEventFilter(MouseEvent.MOUSE_EXITED, e -> {
            if (!isSelected)
                m.setCursor(Cursor.DEFAULT);
        });
        m.setOnMouseReleased(e -> {
            isSelected = false;
            m.setCursor(Cursor.DEFAULT);
        });
        m.setOnMouseDragged(e -> {
            if ((e.getSceneX() >= m.getWidth() - 10) || isSelected) {
                m.setPrefWidth(e.getX());
                isSelected = true;
            }
        });

    }
}