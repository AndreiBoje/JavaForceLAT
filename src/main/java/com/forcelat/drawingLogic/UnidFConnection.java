package com.forcelat.drawingLogic;

import com.forcelat.parsingLogic.FOptions;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;

class UnidFConnection extends FConnection {

    public UnidFConnection(GraphicsContext gcFConnection, Point2D from, Point2D to, FOptions opts) {
        //draw
        this.opts = opts;
        this.gcFConnection = gcFConnection;
        Point2D mid = new Point2D((from.getX() + to.getX()) / 2, (to.getY() + from.getY()) / 2);
        gcFConnection.beginPath();
        gcFConnection.setStroke(opts.fConColor);
        gcFConnection.moveTo(from.getX(), from.getY());
        gcFConnection.lineTo(to.getX(), to.getY());
        gcFConnection.stroke();
        drawArrow(mid, -Math.toRadians(opts.fConStartAngle) - Math.PI);
    }

    public UnidFConnection(GraphicsContext gcFConnection, FNode fromFNode, FNode toFNode, String text, FOptions opts) {
        this.gcFConnection = gcFConnection;
        this.fromFNode = fromFNode;
        this.toFNode = toFNode;
        this.textFrom = text;
        this.opts = opts;
    }

    @Override
    public void draw() {
        //calculate
        double distX = (fromFNode.loc.getX() - toFNode.loc.getX());
        double distY = (fromFNode.loc.getY() - toFNode.loc.getY());

        double angle = Math.atan2(distY, distX);

        double xStart = Math.cos(angle - Math.PI) * fromFNode.opts.fNodeRadius + fromFNode.loc.getX();
        double yStart = Math.sin(angle - Math.PI) * fromFNode.opts.fNodeRadius + fromFNode.loc.getY();

        double xEnd = Math.cos(angle) * toFNode.opts.fNodeRadius + toFNode.loc.getX();
        double yEnd = Math.sin(angle) * toFNode.opts.fNodeRadius + toFNode.loc.getY();

        double xArrow = (xStart + xEnd) / 2;
        double yArrow = (yStart + yEnd) / 2;
        Point2D arrowLoc = new Point2D(xArrow, yArrow);

        double xText, yText;

        if (!opts.fConFlipText) {
            xText = Math.cos(angle + Math.PI / 2) * opts.fConTextHeight + xArrow;
            yText = Math.sin(angle + Math.PI / 2) * opts.fConTextHeight + yArrow;
        } else {
            xText = Math.cos(angle - Math.PI / 2) * opts.fConTextHeight + xArrow;
            yText = Math.sin(angle - Math.PI / 2) * opts.fConTextHeight + yArrow;
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
        gcFConnection.lineTo(xEnd, yEnd);
        gcFConnection.stroke();
        gcFConnection.closePath();

        if (opts.fConHasArrow)
            drawArrow(arrowLoc, angle + Math.PI);
    }
}
