package com.forcelat.serializeLogic;

import java.io.Serializable;

//JavaFX Point2D is not serializable so..
public class FPoint2D implements Serializable {
    double x, y;

    public FPoint2D(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
