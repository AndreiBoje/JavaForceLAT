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
import javafx.stage.Stage;


public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    boolean isSelected = false;

    @Override
    public void start(Stage window) throws Exception {
        double width = 1280;
        double height = 720;

       /* TextArea ta = new TextArea();
        ta.setMinWidth(300);
        Canvas canvas = new Canvas(1500, 1000);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        ScrollPane sp = new ScrollPane(canvas);

        FNodeManager fnm = new FNodeManager(gc,sp);
        FParser fp = new FParser(ta, fnm);
        fp.beginFParse();

        HBox vb = new HBox(sp, ta);
        Scene scene = new Scene(vb, width, height);*/
        TextArea mainTA = new TextArea();
        Canvas mainCanvas = new Canvas(1000, 1000);
        ScrollPane mainSP = new ScrollPane(mainCanvas);
        HBox scrollHB = new HBox(mainSP);
        HBox mainHB = new HBox(scrollHB, mainTA);
        scrollHB.setBorder(new Border(new BorderStroke(new Color(224 / 255f, 224 / 255f, 224 / 255f, 1),
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 10, 0, 0))));

        mainTA.setMinWidth(300);
        HBox.setHgrow(mainTA,Priority.ALWAYS);
        mainSP.setMinWidth(300);
        addResizeListeners(scrollHB, 10);

        Menu file = new Menu("File");
        MenuItem newFile = new MenuItem("New..");
        MenuItem loadFile = new MenuItem("Load..");
        MenuItem save = new MenuItem("Save");
        MenuItem saveAs = new MenuItem("Save as..");
        MenuItem exportImg = new MenuItem("Export image..");
        file.getItems().addAll(newFile, loadFile, save, saveAs, exportImg);
        MenuBar menuBar = new MenuBar(file);
        VBox mainVB = new VBox(menuBar, mainHB);

        FNodeManager fnm = new FNodeManager(mainCanvas.getGraphicsContext2D(),mainSP);
        FParser fp = new FParser(mainTA,fnm);
        fnm.initInteractivity();
        fp.beginFParse();
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