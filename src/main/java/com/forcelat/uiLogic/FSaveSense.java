package com.forcelat.uiLogic;

import javafx.stage.Stage;

public class FSaveSense {
    public static Stage window = null;

    public static void changed() {
        if (!window.getTitle().endsWith("*"))
            window.setTitle(window.getTitle() + "*");
    }
}
