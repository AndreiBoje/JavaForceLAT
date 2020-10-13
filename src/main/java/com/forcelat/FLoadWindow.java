package com.forcelat;

import javafx.stage.Modality;
import javafx.stage.Stage;

public class FLoadWindow {

    public static void display(){
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Load");
    }
}
