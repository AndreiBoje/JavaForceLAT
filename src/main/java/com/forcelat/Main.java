package com.forcelat;

import com.forcelat.drawingLogic.FNodeManager;
import com.forcelat.parsingLogic.FParser;
import com.forcelat.serializeLogic.FSerializer;
import com.forcelat.uiLogic.FSaveSense;
import com.forcelat.uiLogic.imageSizeUI;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.application.Application;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
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
    File projectPathFile = new File(System.getProperty("user.home")); //by default

    @Override
    public void start(Stage window) throws Exception {

        //MAIN UI INIT
        TextArea mainTA = new TextArea();
        Canvas mainCanvas = new Canvas(canvasW, canvasH);
        ScrollPane mainSP = new ScrollPane(mainCanvas);
        HBox scrollHB = new HBox(mainSP);
        HBox mainHB = new HBox(scrollHB, mainTA);
        scrollHB.setBorder(new Border(new BorderStroke(new Color(224 / 255f, 224 / 255f, 224 / 255f, 1),
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 10, 0, 0))));

        mainTA.setMinWidth(250);
        HBox.setHgrow(mainTA, Priority.ALWAYS);
        HBox.setHgrow(mainSP, Priority.ALWAYS);

        mainSP.setMinWidth(200);
        addResizeListeners(scrollHB, 10);

        Menu file = new Menu("File");
        MenuItem selRootFolder = new MenuItem("Set root folder..");
        MenuItem save = new MenuItem("Save");
        save.setDisable(true);
        MenuItem saveAs = new MenuItem("Save As..");
        MenuItem load = new MenuItem("Load");
        file.getItems().addAll(selRootFolder, save, saveAs, load);
        Menu ctx = new Menu("Context");
        MenuItem clear = new MenuItem("Clear");
        MenuItem imgSize = new MenuItem("Image size..");
        MenuItem exportImg = new MenuItem("Export image..");
        ctx.getItems().addAll(clear, imgSize, exportImg);
        MenuBar menuBar = new MenuBar(file, ctx);
        VBox mainVB = new VBox(menuBar, mainHB);
        VBox.setVgrow(mainHB, Priority.ALWAYS);

        FNodeManager fnm = new FNodeManager(mainCanvas.getGraphicsContext2D(), mainSP);
        FParser fp = new FParser(mainTA, fnm);
        fnm.initInteractivity();
        fp.beginFParse();

        //MENU LISTENERS
        clear.setOnAction(e -> {
            mainTA.clear();
            fnm.FNodeMap.clear();
            fnm.display();
        });

        selRootFolder.setOnAction(e -> {
            DirectoryChooser dc = new DirectoryChooser();
            dc.setInitialDirectory(new File(projectPathFile.getAbsolutePath()));
            File selectedDirectory = dc.showDialog(window);
            if (selectedDirectory != null) {
                projectPathFile = selectedDirectory;
                FSerializer.setLastProjectPath(projectPathFile.toString());
            }


        });

        imgSize.setOnAction(e -> {
            imageSizeUI.width = (int) mainCanvas.getWidth();
            imageSizeUI.height = (int) mainCanvas.getHeight();
            imageSizeUI.display();
            mainCanvas.setWidth(imageSizeUI.width);
            mainCanvas.setHeight(imageSizeUI.height);
            fnm.display();
        });
        save.setOnAction(e -> {
            FSerializer.serialize(mainTA, fnm, projectPathFile);
            window.setTitle("ForceLAT - " + FSerializer.saveFile.getName());
        });
        saveAs.setOnAction(e -> {
            String name = FSerializer.serializeAs(mainTA, fnm, projectPathFile);
            if (name != null) {
                save.setDisable(false);
                window.setTitle("ForceLAT - " + name);
            }
        });
        load.setOnAction(e -> {
            String name = FSerializer.deserialize(mainTA, fnm, projectPathFile);
            if (name != null) {
                save.setDisable(false);
                window.setTitle("ForceLAT - " + name);
            }
        });
        exportImg.setOnAction(e -> FSerializer.exportImage(fnm, projectPathFile));

        //SET PROJECT FOLDER TO BE THE LAST IT WAS USED
        projectPathFile = FSerializer.getLastProjectPath();

        //CHANGED SENSE
        FSaveSense.window=window;
        //INIT WINDOW AND SCENE
        Scene scene = new Scene(mainVB, width, height);

        scene.setOnKeyPressed(e -> {
            if (e.isControlDown() && e.getCode() == KeyCode.S) {
                if (save.isDisable()) {
                    String name = FSerializer.serializeAs(mainTA, fnm, projectPathFile);
                    if (name != null) {
                        save.setDisable(false);
                        window.setTitle("ForceLAT - " + name);
                    }
                } else {
                    FSerializer.serialize(mainTA, fnm, projectPathFile);
                    window.setTitle("ForceLAT - " + FSerializer.saveFile.getName());
                }
            }
        });

        window.setScene(scene);
        window.setTitle("ForceLat - untitled");
        window.show();
    }


    //UI HANDLER RESIZER
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