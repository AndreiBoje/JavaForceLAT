package com.forcelat.drawingLogic;

import com.forcelat.parsingLogic.FOptions;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

abstract class FConnection {
    GraphicsContext gcFConnection;
    FNode fromFNode, toFNode;
    String textTo;
    String textFrom;
    FOptions opts;

    public abstract void draw();

    public void updatePos(FNode updatedFrom) {
        this.fromFNode = updatedFrom;
    }

    public void drawArrow(Point2D arrowBase, double angle) {

        // angle -= Math.PI;
        double arrowWidth = 10;
        double arrowHeight = 10;

        double xBase = arrowBase.getX();
        double yBase = arrowBase.getY();

        double xTip = Math.cos(angle) * arrowHeight + xBase;
        double yTip = Math.sin(angle) * arrowHeight + yBase;

        double xArrowLeft = Math.cos(angle + Math.PI / 2) * arrowWidth + xBase;
        double yArrowLeft = Math.sin(angle + Math.PI / 2) * arrowWidth + yBase;

        double xArrowRight = Math.cos(angle - Math.PI / 2) * arrowWidth + xBase;
        double yArrowRight = Math.sin(angle - Math.PI / 2) * arrowWidth + yBase;

        gcFConnection.setFill(opts.fConColor);
        gcFConnection.fillPolygon(new double[]{xArrowLeft, xArrowRight, xTip}, new double[]{yArrowLeft, yArrowRight, yTip}, 3);
    }

}
