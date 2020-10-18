package com.forcelat.uiLogic;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class imageSizeUI {

    public static Integer width, height;
    public static TextField heightTextField = new TextField();
    public static TextField widthTextField = new TextField();

    boolean err = false;
    static final int W_LIMIT = 4096,H_LIMIT = 4096,MIN_W_LIMIT=300,MIN_H_LIMIT=300;

    public static void display() {
        Stage window = new Stage();
        Button okButton = new Button("Apply");
        Button exitButton = new Button("Cancel");
        Label widthLabel = new Label("Width");
        Label heightLabel = new Label("Height");
        widthTextField = new TextField(width.toString());
        heightTextField = new TextField(height.toString());
        okButton.setFont(new Font("Calibri", 16));
        exitButton.setFont(new Font("Calibri", 16));
        heightLabel.setFont(new Font("Calibri", 16));
        widthLabel.setFont(new Font("Calibri", 16));
        heightTextField.setFont(new Font("Calibri", 16));
        widthTextField.setFont(new Font("Calibri", 16));

        GridPane grid = new GridPane();
        grid.add(widthLabel, 0, 0);
        grid.add(widthTextField, 1, 0);
        grid.add(heightLabel, 0, 1);
        grid.add(heightTextField, 1, 1);
        HBox optsHBox = new HBox(okButton, exitButton);
        optsHBox.setAlignment(Pos.CENTER_RIGHT);
        grid.add(optsHBox, 0, 2, 2, 1);
        grid.setHgap(10);
        grid.setVgap(10);
        HBox mainHB = new HBox(grid);
        mainHB.setAlignment(Pos.CENTER);
        VBox mainVB = new VBox(mainHB);
        mainVB.setAlignment(Pos.CENTER);
        Scene s = new Scene(mainVB);

        okButton.setOnMouseClicked(e -> {
            try {
                int H = Integer.parseInt(heightTextField.getText());
                int W = Integer.parseInt(widthTextField.getText());
                if ( H >= MIN_H_LIMIT &&  W >= MIN_W_LIMIT && H <= H_LIMIT && W <= W_LIMIT) {
                    height = Integer.parseInt(heightTextField.getText());
                    width = Integer.parseInt(widthTextField.getText());
                    window.close();
                } else {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setHeaderText("Width or Height doesn't satisfy min/max bounds !");
                    errorAlert.showAndWait();
                }
            } catch (NumberFormatException ex) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Width or Height not a number !");
                errorAlert.showAndWait();
            }
        });
        exitButton.setOnMouseClicked(e -> window.close());

        window.setScene(s);
        window.setWidth(300);
        window.setHeight(180);
        window.resizableProperty().setValue(Boolean.FALSE);
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("ForceLAT Image Size");
        window.showAndWait();
    }
}
