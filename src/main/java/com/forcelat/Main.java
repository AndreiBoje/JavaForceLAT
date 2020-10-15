package com.forcelat;

import com.forcelat.drawingLogic.FNodeManager;
import com.forcelat.parsingLogic.FParser;
import com.forcelat.serializeLogic.FSerializer;
import com.forcelat.uiLogic.imageSizeUI;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
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
    int canvasW = 1000;
    int canvasH = 1000;
    int width = 1280;
    int height = 720;

    @Override
    public void start(Stage window) throws Exception {

        TextArea mainTA = new TextArea();
        Canvas mainCanvas = new Canvas(canvasW, canvasH);
        ScrollPane mainSP = new ScrollPane(mainCanvas);
        HBox scrollHB = new HBox(mainSP);
        HBox mainHB = new HBox(scrollHB, mainTA);
        scrollHB.setBorder(new Border(new BorderStroke(new Color(224 / 255f, 224 / 255f, 224 / 255f, 1),
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 10, 0, 0))));

        mainTA.setMinWidth(250);
        //mainTA.setMaxHeight(2000);
        HBox.setHgrow(mainTA, Priority.ALWAYS);
        HBox.setHgrow(mainSP, Priority.ALWAYS);

        mainSP.setMinWidth(200);
        addResizeListeners(scrollHB, 10);

        Menu file = new Menu("File");
        MenuItem newProj = new MenuItem("New Project..");
        MenuItem save = new MenuItem("Save");
        MenuItem saveAs = new MenuItem("Save As..");
        MenuItem load = new MenuItem("Load");
        file.getItems().addAll(newProj, save, saveAs, load);
        Menu ctx = new Menu("Context");
        MenuItem clear = new MenuItem("Clear");
        MenuItem imgSize = new MenuItem("Image size..");
        MenuItem exportImg = new MenuItem("Export image..");
        ctx.getItems().addAll(clear, imgSize, exportImg);
        MenuBar menuBar = new MenuBar(file, ctx);
        VBox mainVB = new VBox(menuBar, mainHB);
        VBox.setVgrow(mainHB,Priority.ALWAYS);

        FNodeManager fnm = new FNodeManager(mainCanvas.getGraphicsContext2D(), mainSP);
        FParser fp = new FParser(mainTA, fnm);
        fnm.initInteractivity();
        fp.beginFParse();

        imgSize.setOnAction(e -> {
            imageSizeUI.width = canvasW;
            imageSizeUI.height = canvasH;
            imageSizeUI.display();
            canvasW = imageSizeUI.width;
            canvasH = imageSizeUI.height;

            mainCanvas.setWidth(canvasW);
            mainCanvas.setHeight(canvasH);
            fnm.display();
        });

        save.setOnAction(e -> {
            final FileChooser fc = new FileChooser();
            fc.setInitialDirectory(new File(System.getProperty("user.home")));
            fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("FLAT", "*.flat"));
            File saveFile = fc.showSaveDialog(window);
            if (saveFile == null)
                return;
            FSerializer.serialize(mainTA.getText(), fnm, saveFile);
        });

        load.setOnAction(e -> {
            final FileChooser fc = new FileChooser();
            fc.setInitialDirectory(new File(System.getProperty("user.home")));
            fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("FLAT", "*.flat"));
            File loadFile = fc.showOpenDialog(window);
            if (loadFile == null)
                return;
            FSerializer.deserialize(mainTA, fnm, loadFile);
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