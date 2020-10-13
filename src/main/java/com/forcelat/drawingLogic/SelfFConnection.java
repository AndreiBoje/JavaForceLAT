package com.forcelat.drawingLogic;

import com.forcelat.parsingLogic.FOptions;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;

class SelfFConnection extends FConnection {

    public SelfFConnection(GraphicsContext gc, FNode fn, String textFrom, FOptions opts) {
        this.gcFConnection = gc;
        this.fromFNode = fn;
        this.textFrom = textFrom;
        this.opts = opts;
    }

    @Override
    public void draw() {

        double angleApproach = Math.PI / 6;
        double angleOffset = Math.toRadians(opts.fConSelfAngle);

        double xBaseL = Math.cos(angleApproach - angleOffset) * fromFNode.opts.fNodeRadius + fromFNode.loc.getX();
        double yBaseL = Math.sin(angleApproach - angleOffset) * fromFNode.opts.fNodeRadius + fromFNode.loc.getY();

        double xBaseR = Math.cos(-angleApproach - angleOffset) * fromFNode.opts.fNodeRadius + fromFNode.loc.getX();
        double yBaseR = Math.sin(-angleApproach - angleOffset) * fromFNode.opts.fNodeRadius + fromFNode.loc.getY();


        double xCp1 = Math.cos(angleApproach - angleOffset) * 3.5 * fromFNode.opts.fNodeRadius + fromFNode.loc.getX();
        double yCp1 = Math.sin(angleApproach - angleOffset) * 3.5 * fromFNode.opts.fNodeRadius + fromFNode.loc.getY();

        double xCp2 = Math.cos(-angleApproach - angleOffset) * 3.5 * fromFNode.opts.fNodeRadius + fromFNode.loc.getX();
        double yCp2 = Math.sin(-angleApproach - angleOffset) * 3.5 * fromFNode.opts.fNodeRadius + fromFNode.loc.getY();

        double xMidAnchor = (xCp1 + xCp2) / 2;
        double yMidAnchor = (yCp1 + yCp2) / 2;

        double xArrow = Math.cos(-angleOffset - angleApproach) * 10 + xBaseR;
        double yArrow = Math.sin(-angleOffset - angleApproach) * 10 + yBaseR;
        Point2D locArrow = new Point2D(xArrow, yArrow);

        double xText = Math.cos(-angleOffset) * opts.fConSelfTextHeight + xMidAnchor;
        double yText = Math.sin(-angleOffset) * opts.fConSelfTextHeight + yMidAnchor;

        gcFConnection.setFill(opts.fConColor);
        gcFConnection.setTextAlign(TextAlignment.CENTER);
        gcFConnection.setTextBaseline(VPos.CENTER);
        gcFConnection.setFont(new Font("Calibri", opts.fConTextSize));
        gcFConnection.save();
        Rotate r = new Rotate(opts.fConTextAngle, xText, yText);
        gcFConnection.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
        gcFConnection.fillText(textFrom, xText, yText);
        gcFConnection.restore();
        gcFConnection.beginPath();
        gcFConnection.setStroke(opts.fConColor);
        gcFConnection.setLineWidth(opts.fConLineWidth);

        //draw bezier
        int pointsNo = 14;
        Point2D[] bezierPoints = new Point2D[pointsNo];
        float t = 0;
        float tStep = 1f / (pointsNo - 1);

        for (int i = 0; i < pointsNo; i++) {
            double x = Math.pow((1 - t), 3) * xBaseL + 3 * Math.pow((1 - t), 2) * t * xCp1 + 3 * (1 - t) * t * t * xCp2 + t * t * t * xBaseR;
            double y = Math.pow((1 - t), 3) * yBaseL + 3 * Math.pow((1 - t), 2) * t * yCp1 + 3 * (1 - t) * t * t * yCp2 + t * t * t * yBaseR;
            bezierPoints[i] = new Point2D(x, y);
            t += tStep;
        }
        for (int i = 0; i < pointsNo; i++) {
            double x = bezierPoints[i].getX();
            double y = bezierPoints[i].getY();
            gcFConnection.lineTo(x, y);
        }

        gcFConnection.stroke();
        gcFConnection.closePath();
        drawArrow(locArrow, -angleApproach + Math.PI - angleOffset);
    }

}
