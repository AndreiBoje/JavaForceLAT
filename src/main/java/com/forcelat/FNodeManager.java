package com.forcelat;

import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;


class FNode {
    GraphicsContext gcFNode;
    Point2D loc;
    int strokeWidth = 3;
    Integer ID;
    String fname;
    FOptions opts;
    HashSet<Integer> unidConnectionTo = new HashSet<>();
    HashSet<Integer> jprConnectionTo = new HashSet<>();
    HashSet<Integer> bidConnectsWith = new HashSet<>();
    boolean selfConnects = false;
    boolean isFinal = false;
    boolean isStart = false;

    public FNode(GraphicsContext gcFNode, Point2D location, int ID, String fname, FOptions opts) {
        this.gcFNode = gcFNode;
        this.loc = location;
        this.ID = ID;
        this.fname = fname;
        this.opts = opts;
    }

    public void draw() {
        gcFNode.setFill(opts.fNodeColor);
        gcFNode.setFont(new Font("Calibri", opts.fNodeTextSize));
        gcFNode.setTextAlign(TextAlignment.CENTER);
        gcFNode.setTextBaseline(VPos.CENTER);
        gcFNode.fillText(fname, loc.getX(), loc.getY());

        //draw contour
        gcFNode.setStroke(opts.fNodeColor);
        gcFNode.setLineWidth(strokeWidth);
        gcFNode.strokeOval(loc.getX() - opts.fNodeRadius, loc.getY() - opts.fNodeRadius, opts.fNodeRadius * 2, opts.fNodeRadius * 2);

        if (isFinal) {
            gcFNode.strokeOval(loc.getX() - opts.fNodeSmallRadius, loc.getY() - opts.fNodeSmallRadius, opts.fNodeSmallRadius * 2, opts.fNodeSmallRadius * 2);
        }
        if (isStart) {
            double xArrowStart = loc.getX() - 100;
            double yArrowStart = loc.getY();
            double xArrowEnd = loc.getX() - opts.fNodeRadius;
            double yArrowEnd = loc.getY();
            UnidFConnection uc = new UnidFConnection(gcFNode, new Point2D(xArrowStart, yArrowStart), new Point2D(xArrowEnd, yArrowEnd));
        }
    }
}

abstract class FConnection {
    GraphicsContext gcFConnection;
    Color color = Color.BLACK;
    int lineWidth = 3;
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

class UnidFConnection extends FConnection {

    public UnidFConnection(GraphicsContext gcFConnection, Point2D from, Point2D to) {
        //draw
        this.gcFConnection = gcFConnection;
        Point2D mid = new Point2D((from.getX() + to.getX()) / 2, (to.getY() + from.getY()) / 2);
        gcFConnection.beginPath();
        gcFConnection.setStroke(Color.BLACK);
        gcFConnection.moveTo(from.getX(), from.getY());
        gcFConnection.lineTo(to.getX(), to.getY());
        gcFConnection.stroke();
        drawArrow(mid, 0);
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
            xText = Math.cos(angle + Math.PI / 2) * 20 + xArrow;
            yText = Math.sin(angle + Math.PI / 2) * 20 + yArrow;
        } else {
            xText = Math.cos(angle - Math.PI / 2) * 20 + xArrow;
            yText = Math.sin(angle - Math.PI / 2) * 20 + yArrow;
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
        gcFConnection.setLineWidth(lineWidth);
        gcFConnection.moveTo(xStart, yStart);
        gcFConnection.lineTo(xEnd, yEnd);
        gcFConnection.stroke();
        gcFConnection.closePath();
        drawArrow(arrowLoc, angle + Math.PI);
    }
}

class BidFConnection extends FConnection {

    public BidFConnection(GraphicsContext gcFConnection, FNode fromFNode, FNode toFNode, String textFrom, String textTo) {
        this.gcFConnection = gcFConnection;
        this.fromFNode = fromFNode;
        this.toFNode = toFNode;
        this.textFrom = textFrom;
        this.textTo = textTo;
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

        double xText = Math.cos(angleBase + Math.PI / 2) * 20 + xArrow;
        double yText = Math.sin(angleBase + Math.PI / 2) * 20 + yArrow;

        double xText2 = Math.cos(angleBase - Math.PI / 2) * 20 + xArrow2;
        double yText2 = Math.sin(angleBase - Math.PI / 2) * 20 + yArrow2;
        //draw
        gcFConnection.setFill(color);
        gcFConnection.setTextAlign(TextAlignment.CENTER);
        gcFConnection.setTextBaseline(VPos.CENTER);
        gcFConnection.setFont(new Font("Calibri", 20));
        gcFConnection.save();
        Rotate r = new Rotate(0, xText, yText);
        gcFConnection.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
        gcFConnection.fillText(textFrom, xText2, yText2);
        gcFConnection.fillText(textTo, xText, yText);
        gcFConnection.restore();
        gcFConnection.beginPath();
        gcFConnection.setStroke(color);
        gcFConnection.setLineWidth(lineWidth);
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

class SelfFConnection extends FConnection {

    public SelfFConnection(GraphicsContext gc, FNode fn, String textFrom) {
        this.gcFConnection = gc;
        this.fromFNode = fn;
        this.textFrom = textFrom;
    }

    @Override
    public void draw() {

        double angleApproach = Math.PI / 6;

        double xBaseL = Math.cos(angleApproach - Math.PI / 2) * fromFNode.opts.fNodeRadius + fromFNode.loc.getX();
        double yBaseL = Math.sin(angleApproach - Math.PI / 2) * fromFNode.opts.fNodeRadius + fromFNode.loc.getY();

        double xBaseR = Math.cos(-angleApproach - Math.PI / 2) * fromFNode.opts.fNodeRadius + fromFNode.loc.getX();
        double yBaseR = Math.sin(-angleApproach - Math.PI / 2) * fromFNode.opts.fNodeRadius + fromFNode.loc.getY();

        double xCp1 = fromFNode.loc.getX() + fromFNode.opts.fNodeRadius * 1.8;
        double yCp1 = fromFNode.loc.getY() - fromFNode.opts.fNodeRadius * 2.8;

        double xCp2 = fromFNode.loc.getX() - fromFNode.opts.fNodeRadius * 1.8;
        double yCp2 = fromFNode.loc.getY() - fromFNode.opts.fNodeRadius * 2.8;

        double xArrow = Math.cos(-angleApproach - Math.PI / 2) * 10 + xBaseR;
        double yArrow = Math.sin(-angleApproach - Math.PI / 2) * 10 + yBaseR;
        Point2D locArrow = new Point2D(xArrow, yArrow);

        double xText = (xCp1 + xCp2) / 2;
        double yText = yCp1 + 5;

        gcFConnection.setFill(color);
        gcFConnection.setTextAlign(TextAlignment.CENTER);
        gcFConnection.setTextBaseline(VPos.CENTER);
        gcFConnection.setFont(new Font("Calibri", 20));
        gcFConnection.save();
        Rotate r = new Rotate(0, xText, yText);
        gcFConnection.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
        gcFConnection.fillText(textFrom, xText, yText);
        gcFConnection.restore();
        gcFConnection.beginPath();
        gcFConnection.setStroke(color);
        gcFConnection.setLineWidth(lineWidth);

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
        drawArrow(locArrow, -angleApproach + Math.PI / 2);
    }

}

class JprFConnection extends FConnection {

    public JprFConnection(GraphicsContext gc, FNode fromFNode, FNode toFNode, String textFrom) {
        this.gcFConnection = gc;
        this.fromFNode = fromFNode;
        this.toFNode = toFNode;
        this.textFrom = textFrom;
    }

    @Override
    public void draw() {
        //calculate
        double distX = (fromFNode.loc.getX() - toFNode.loc.getX());
        double distY = (fromFNode.loc.getY() - toFNode.loc.getY());

        double angle = Math.atan2(distY, distX);
        double angleRise = Math.PI / 4;
        double extendFactor = 100; //bug on big radii

        double xStart = Math.cos(angle + Math.PI - angleRise) * fromFNode.opts.fNodeRadius + fromFNode.loc.getX();
        double yStart = Math.sin(angle + Math.PI - angleRise) * fromFNode.opts.fNodeRadius + fromFNode.loc.getY();

        double xEnd = Math.cos(angle + angleRise) * toFNode.opts.fNodeRadius + toFNode.loc.getX();
        double yEnd = Math.sin(angle + angleRise) * toFNode.opts.fNodeRadius + toFNode.loc.getY();

        double xHandleL = Math.cos(angle + Math.PI - angleRise) * extendFactor + fromFNode.loc.getX();
        double yHandleL = Math.sin(angle + Math.PI - angleRise) * extendFactor + fromFNode.loc.getY();

        double xHandleR = Math.cos(angle + angleRise) * extendFactor + toFNode.loc.getX();
        double yHandleR = Math.sin(angle + angleRise) * extendFactor + toFNode.loc.getY();

        double xArrow = (xHandleL + xHandleR) / 2;
        double yArrow = (yHandleL + yHandleR) / 2;
        Point2D arrowLoc = new Point2D(xArrow, yArrow);

        double xText = Math.cos(angle + Math.PI / 2) * 20 + xArrow;
        double yText = Math.sin(angle + Math.PI / 2) * 20 + yArrow;
        //draw

        gcFConnection.setFill(color);
        gcFConnection.setTextAlign(TextAlignment.CENTER);
        gcFConnection.setTextBaseline(VPos.CENTER);
        gcFConnection.setFont(new Font("Calibri", 20));
        gcFConnection.save();
        Rotate r = new Rotate(0, xText, yText);
        gcFConnection.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
        gcFConnection.fillText(textFrom, xText, yText);
        gcFConnection.restore();
        gcFConnection.beginPath();
        gcFConnection.setStroke(color);
        gcFConnection.setLineWidth(lineWidth);
        gcFConnection.moveTo(xStart, yStart);
        gcFConnection.lineTo(xHandleL, yHandleL);
        gcFConnection.lineTo(xHandleR, yHandleR);
        gcFConnection.lineTo(xEnd, yEnd);
        gcFConnection.stroke();
        gcFConnection.closePath();
        drawArrow(arrowLoc, angle + Math.PI);
    }
}

public class FNodeManager {
    //defaults

    private int FNodeIDGiver = 0;
    private Color clearScreenColor = Color.WHITE;
    private final GraphicsContext gc;
    private final TreeMap<Integer, FNode> FNodeMap = new TreeMap<>();
    private final HashMap<Pair<Integer, Integer>, FConnection> FConnectionMap = new HashMap<>();
    private int selectedFNodeID = -1;

    public FNodeManager(GraphicsContext gc) {
        this.gc = gc;
    }

    public void clear() {
        FNodeMap.clear();
        FConnectionMap.clear();
    }

    public void initInteractivity() {
        Canvas canvas = gc.getCanvas();

        canvas.setOnMouseDragged(e -> {
            //get nearest node ID in range HARDCODED: 30 (radius default)
            double searchRadius = 30;
            Point2D cursorLoc = new Point2D(e.getX(), e.getY());

            if (selectedFNodeID == -1) {
                for (FNode fn : FNodeMap.values()) {
                    double dist = cursorLoc.distance(fn.loc);
                    if (dist <= searchRadius) {
                        selectedFNodeID = fn.ID;
                        break;
                    }
                }
            }

            if (selectedFNodeID != -1) {

                FNode fn = FNodeMap.get(selectedFNodeID);

                fn.loc = cursorLoc;

                for (int ID : fn.unidConnectionTo) {
                    Pair<Integer, Integer> pair = new Pair<>(selectedFNodeID, ID);
                    if (FConnectionMap.containsKey(pair))
                        FConnectionMap.get(pair).updatePos(fn);
                }
                for (int ID : fn.bidConnectsWith) {
                    Pair<Integer, Integer> pair = new Pair<>(selectedFNodeID, ID);
                    if (FConnectionMap.containsKey(pair))
                        FConnectionMap.get(pair).updatePos(fn);
                }
                for (int ID : fn.jprConnectionTo) {
                    Pair<Integer, Integer> pair = new Pair<>(selectedFNodeID, ID);
                    if (FConnectionMap.containsKey(pair))
                        FConnectionMap.get(pair).updatePos(fn);
                }
                if (fn.selfConnects) {
                    Pair<Integer, Integer> pair = new Pair<>(selectedFNodeID, selectedFNodeID);
                    FConnectionMap.get(pair).updatePos(fn);
                }
            }
            display();
        });
        //reset selection
        canvas.setOnMouseReleased(e -> {
            selectedFNodeID = -1;
        });
    }

    public void addFNode(double xPos, double yPos, String fname, FOptions opts) {
        Point2D loc = new Point2D(xPos, yPos);
        FNode fn = new FNode(gc, loc, FNodeIDGiver, fname, opts);
        FNodeMap.put(FNodeIDGiver, fn);
        FNodeIDGiver++;
    }

    public int getFNodeIDByTxt(String txt) {

        //ALSO BY TEXTREFERENCE? MAYBE
        for (FNode fn : FNodeMap.values())
            if (fn.opts.fNodeAlias.equals(txt) || fn.fname.equals(txt))
                return fn.ID;
        return -1;
    }

    public void makeFinal(String textRef) {
        FNode fn = FNodeMap.get(getFNodeIDByTxt(textRef));
        if (fn != null)
            fn.isFinal = true;
    }

    public void makeStart(String textRef) {
        FNode fn = FNodeMap.get(getFNodeIDByTxt(textRef));
        if (fn != null)
            fn.isStart = true;
    }

    public void jprFConnection(int fromID, int toID, String text) {
        FNode fromFNode = FNodeMap.get(fromID);
        FNode toFNode = FNodeMap.get(toID);

        if (fromFNode == null || toFNode == null) return;

        FConnection fc = new JprFConnection(gc, fromFNode, toFNode, text);

        fromFNode.jprConnectionTo.add(toID);
        Pair<Integer, Integer> key = new Pair<>(fromID, toID);
        FConnectionMap.put(key, fc);
    }

    public void unidFConnection(int fromID, int toID, String text, FOptions opts) {
        FNode fromFNode = FNodeMap.get(fromID);
        FNode toFNode = FNodeMap.get(toID);

        if (fromFNode == null || toFNode == null) return;

        FConnection fc = new UnidFConnection(gc, fromFNode, toFNode, text, opts);

        fromFNode.unidConnectionTo.add(toID);
        Pair<Integer, Integer> key = new Pair<>(fromID, toID);
        FConnectionMap.put(key, fc);
    }

    public void bidFConnection(int fromID, int toID, String fromText, String toText) {
        FNode fromFNode = FNodeMap.get(fromID);
        FNode toFNode = FNodeMap.get(toID);

        if (fromFNode == null || toFNode == null) return;

        FConnection fc = new BidFConnection(gc, fromFNode, toFNode, fromText, toText);

        fromFNode.bidConnectsWith.add(toID);
        toFNode.bidConnectsWith.add(fromID);
        Pair<Integer, Integer> key = new Pair<>(fromID, toID);
        FConnectionMap.put(key, fc);
    }

    public void selfFConnection(int ID, String text) {
        FNode fn = FNodeMap.get(ID);

        if (fn == null) return;

        FConnection fc = new SelfFConnection(gc, fn, text);
        fn.selfConnects = true;
        Pair<Integer, Integer> key = new Pair<>(ID, ID);
        FConnectionMap.put(key, fc);

    }

    public void displayGrid() {
        double width = gc.getCanvas().getWidth();
        double height = gc.getCanvas().getHeight();
        double spacing = 100; //pixels

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.beginPath();

        for (int x = 0; x < width; x += spacing) {
            for (int y = 0; y < height; y += spacing) {
                gc.moveTo(0, y);
                gc.lineTo(width, y);
                gc.moveTo(x, 0);
                gc.lineTo(x, height);
            }
        }
        gc.stroke();
    }

    public void display() {

        //clear last screen
        Canvas canvas = gc.getCanvas();
        gc.setFill(clearScreenColor);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        //displayGrid();

        //draw connections
        for (FConnection fc : FConnectionMap.values())
            fc.draw();


        //draw nodes
        for (FNode fn : FNodeMap.values())
            fn.draw();
    }
}
