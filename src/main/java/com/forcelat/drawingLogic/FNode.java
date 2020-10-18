package com.forcelat.drawingLogic;

import com.forcelat.parsingLogic.FOptions;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.HashSet;

public class FNode {
    GraphicsContext gcFNode;
    public Point2D loc;
    Integer ID;
    public String fname,alias;
    public FOptions opts;
    HashSet<Integer> unidConnectionTo = new HashSet<>();
    HashSet<Integer> jprConnectionTo = new HashSet<>();
    HashSet<Integer> bidConnectsWith = new HashSet<>();
    boolean selfConnects = false;
    boolean isFinal = false;
    boolean isStart = false;

    public FNode(GraphicsContext gcFNode, Point2D location, int ID, String fname, FOptions opts) {
        this.gcFNode = gcFNode;
        this.loc = location; //CHANGE THIS HARDCODED THING LATER
        this.ID = ID;
        this.fname = fname;
        this.opts = opts;
    }

    public void draw() {
        if (opts.fNodeShowText) {
            gcFNode.setFill(opts.fNodeColor);
            gcFNode.setFont(new Font("Calibri", opts.fNodeTextSize));
            gcFNode.setTextAlign(TextAlignment.CENTER);
            gcFNode.setTextBaseline(VPos.CENTER);
            gcFNode.fillText(alias != null ? alias : fname, loc.getX(), loc.getY());
        }

        if (isFinal) {
            gcFNode.strokeOval(loc.getX() - opts.fNodeSmallRadius, loc.getY() - opts.fNodeSmallRadius, opts.fNodeSmallRadius * 2, opts.fNodeSmallRadius * 2);
        }

        if (isStart) {
            double angleDeg = Math.toRadians(opts.fConStartAngle);
            double xEnd = Math.cos(-angleDeg) * opts.fNodeRadius + loc.getX();
            double yEnd = Math.sin(-angleDeg) * opts.fNodeRadius + loc.getY();

            double xStart = Math.cos(-angleDeg) * opts.fConStartLength + xEnd;
            double yStart = Math.sin(-angleDeg) * opts.fConStartLength + yEnd;

            new UnidFConnection(gcFNode, new Point2D(xStart, yStart), new Point2D(xEnd, yEnd), opts);
        }
        //draw contour
        gcFNode.setStroke(opts.fNodeColor);
        gcFNode.setFill(opts.fNodeColor);
        gcFNode.setLineWidth(opts.fNodeWidth);
        if (opts.fNodeFill)
            gcFNode.fillOval(loc.getX() - opts.fNodeRadius, loc.getY() - opts.fNodeRadius, opts.fNodeRadius * 2, opts.fNodeRadius * 2);
        else
            gcFNode.strokeOval(loc.getX() - opts.fNodeRadius, loc.getY() - opts.fNodeRadius, opts.fNodeRadius * 2, opts.fNodeRadius * 2);

    }
}
