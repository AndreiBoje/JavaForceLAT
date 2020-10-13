package com.forcelat.drawingLogic;

import com.forcelat.parsingLogic.FOptions;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;

class JprFConnection extends FConnection {

    public JprFConnection(GraphicsContext gc, FNode fromFNode, FNode toFNode, String textFrom, FOptions opts) {
        this.gcFConnection = gc;
        this.fromFNode = fromFNode;
        this.toFNode = toFNode;
        this.textFrom = textFrom;
        this.opts = opts;
    }

    @Override
    public void draw() {
        //calculate
        double distX = (fromFNode.loc.getX() - toFNode.loc.getX());
        double distY = (fromFNode.loc.getY() - toFNode.loc.getY());

        double angle = Math.atan2(distY, distX);
        double angleRise = Math.toRadians(opts.fConRiseAngle);


        double xStart = Math.cos(angle + Math.PI - angleRise) * fromFNode.opts.fNodeRadius + fromFNode.loc.getX();
        double yStart = Math.sin(angle + Math.PI - angleRise) * fromFNode.opts.fNodeRadius + fromFNode.loc.getY();

        double xEnd = Math.cos(angle + angleRise) * toFNode.opts.fNodeRadius + toFNode.loc.getX();
        double yEnd = Math.sin(angle + angleRise) * toFNode.opts.fNodeRadius + toFNode.loc.getY();

        double xHandleL = Math.cos(angle + Math.PI - angleRise) * opts.fConExtendFactor + fromFNode.loc.getX();
        double yHandleL = Math.sin(angle + Math.PI - angleRise) * opts.fConExtendFactor + fromFNode.loc.getY();

        double xHandleR = Math.cos(angle + angleRise) * opts.fConExtendFactor + toFNode.loc.getX();
        double yHandleR = Math.sin(angle + angleRise) * opts.fConExtendFactor + toFNode.loc.getY();

        double xArrow = (xHandleL + xHandleR) / 2;
        double yArrow = (yHandleL + yHandleR) / 2;
        Point2D arrowLoc = new Point2D(xArrow, yArrow);

        double xText, yText;
        if (opts.fConFlipText) {
            xText = Math.cos(angle - Math.PI / 2) * opts.fConTextHeight + xArrow;
            yText = Math.sin(angle - Math.PI / 2) * opts.fConTextHeight + yArrow;
        } else {
            xText = Math.cos(angle + Math.PI / 2) * opts.fConTextHeight + xArrow;
            yText = Math.sin(angle + Math.PI / 2) * opts.fConTextHeight + yArrow;
        }

        //draw

        gcFConnection.setFill(opts.fConColor);
        gcFConnection.setTextAlign(TextAlignment.CENTER);
        gcFConnection.setTextBaseline(VPos.CENTER);
        gcFConnection.setFont(new Font("Calibri", opts.fConTextSize));
        gcFConnection.save();
        Rotate r = new Rotate(-opts.fConTextAngle, xText, yText);
        gcFConnection.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
        gcFConnection.fillText(textFrom, xText, yText);
        gcFConnection.restore();
        gcFConnection.beginPath();
        gcFConnection.setStroke(opts.fConColor);
        gcFConnection.setLineWidth(opts.fConLineWidth);
        gcFConnection.moveTo(xStart, yStart);
        gcFConnection.lineTo(xHandleL, yHandleL);
        gcFConnection.lineTo(xHandleR, yHandleR);
        gcFConnection.lineTo(xEnd, yEnd);
        gcFConnection.stroke();
        gcFConnection.closePath();
        if (opts.fConHasArrow)
            drawArrow(arrowLoc, angle + Math.PI);
    }
}
