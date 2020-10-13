package com.forcelat.drawingLogic;

import com.forcelat.parsingLogic.FOptions;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;

class BidFConnection extends FConnection {

    public BidFConnection(GraphicsContext gcFConnection, FNode fromFNode, FNode toFNode, String textFrom, String textTo, FOptions opts) {
        this.gcFConnection = gcFConnection;
        this.fromFNode = fromFNode;
        this.toFNode = toFNode;
        this.textFrom = textFrom;
        this.textTo = textTo;
        this.opts = opts;
    }

    @Override
    public void draw() {

        //calculate
        double distX = (fromFNode.loc.getX() - toFNode.loc.getX());
        double distY = (fromFNode.loc.getY() - toFNode.loc.getY());

        double angleBase = Math.atan2(distY, distX);
        double angleApproach = Math.PI / 8;

        //first arrow

        double xStart = Math.cos(angleBase - Math.PI - angleApproach) * fromFNode.opts.fNodeRadius + fromFNode.loc.getX();
        double yStart = Math.sin(angleBase - Math.PI - angleApproach) * fromFNode.opts.fNodeRadius + fromFNode.loc.getY();

        double xEnd = Math.cos(angleBase + angleApproach) * toFNode.opts.fNodeRadius + toFNode.loc.getX();
        double yEnd = Math.sin(angleBase + angleApproach) * toFNode.opts.fNodeRadius + toFNode.loc.getY();

        double xArrow = (xStart + xEnd) / 2;
        double yArrow = (yStart + yEnd) / 2;
        Point2D arrowLoc = new Point2D(xArrow, yArrow);

        //second arrow

        double xStart2 = Math.cos(angleBase - Math.PI + angleApproach) * fromFNode.opts.fNodeRadius + fromFNode.loc.getX();
        double yStart2 = Math.sin(angleBase - Math.PI + angleApproach) * fromFNode.opts.fNodeRadius + fromFNode.loc.getY();

        double xEnd2 = Math.cos(angleBase - angleApproach) * toFNode.opts.fNodeRadius + toFNode.loc.getX();
        double yEnd2 = Math.sin(angleBase - angleApproach) * toFNode.opts.fNodeRadius + toFNode.loc.getY();

        double xArrow2 = (xStart2 + xEnd2) / 2;
        double yArrow2 = (yStart2 + yEnd2) / 2;
        Point2D arrowLoc2 = new Point2D(xArrow2, yArrow2);

        double xText, yText, xText2, yText2;
        if (opts.fConFlipText) {
            xText = Math.cos(angleBase - Math.PI / 2) * opts.fConTextHeight + xArrow;
            yText = Math.sin(angleBase - Math.PI / 2) * opts.fConTextHeight + yArrow;

            xText2 = Math.cos(angleBase + Math.PI / 2) * opts.fConTextHeight + xArrow2;
            yText2 = Math.sin(angleBase + Math.PI / 2) * opts.fConTextHeight + yArrow2;
        } else {
            xText = Math.cos(angleBase + Math.PI / 2) * opts.fConTextHeight + xArrow;
            yText = Math.sin(angleBase + Math.PI / 2) * opts.fConTextHeight + yArrow;

            xText2 = Math.cos(angleBase - Math.PI / 2) * opts.fConTextHeight + xArrow2;
            yText2 = Math.sin(angleBase - Math.PI / 2) * opts.fConTextHeight + yArrow2;
        }
        //draw
        gcFConnection.setFill(opts.fConColor);
        gcFConnection.setTextAlign(TextAlignment.CENTER);
        gcFConnection.setTextBaseline(VPos.CENTER);
        gcFConnection.setFont(new Font("Calibri", opts.fConTextSize));
        gcFConnection.save();
        Rotate r = new Rotate(-opts.fConTextAngle, xText, yText);
        gcFConnection.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
        gcFConnection.fillText(textTo, xText, yText);
        gcFConnection.restore();
        gcFConnection.save();
        Rotate r1 = new Rotate(opts.fConTextAngle, xText2, yText2);
        gcFConnection.setTransform(r1.getMxx(), r1.getMyx(), r1.getMxy(), r1.getMyy(), r1.getTx(), r1.getTy());
        gcFConnection.fillText(textFrom, xText2, yText2);
        gcFConnection.restore();
        gcFConnection.beginPath();
        gcFConnection.setStroke(opts.fConColor);
        gcFConnection.setLineWidth(opts.fConLineWidth);
        gcFConnection.moveTo(xStart, yStart);
        gcFConnection.lineTo(xEnd, yEnd);
        gcFConnection.moveTo(xStart2, yStart2);
        gcFConnection.lineTo(xEnd2, yEnd2);
        gcFConnection.stroke();
        gcFConnection.closePath();
        drawArrow(arrowLoc, angleBase);
        drawArrow(arrowLoc2, angleBase - Math.PI);

    }

}
