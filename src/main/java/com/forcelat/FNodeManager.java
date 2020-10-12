package com.forcelat;

import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;


class FNode {
    GraphicsContext gcFNode;
    Point2D loc;
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
        if (opts.fNodeShowText) {
            gcFNode.setFill(opts.fNodeColor);
            gcFNode.setFont(new Font("Calibri", opts.fNodeTextSize));
            gcFNode.setTextAlign(TextAlignment.CENTER);
            gcFNode.setTextBaseline(VPos.CENTER);
            gcFNode.fillText(fname, loc.getX(), loc.getY());
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

public class FNodeManager {
    private int FNodeIDGiver = 0;
    private final Color clearScreenColor = Color.WHITE;
    private final GraphicsContext gc;
    private final ScrollPane sp;
    private final TreeMap<Integer, FNode> FNodeMap = new TreeMap<>();
    private final HashSet<String> encounteredFNodeNames = new HashSet<>();
    public HashMap<String, FOptions> encounteredFNodeOptions = new HashMap<>();
    private final HashMap<Pair<Integer, Integer>, FConnection> FConnectionMap = new HashMap<>();
    private int selectedFNodeID = -1;

    public FNodeManager(GraphicsContext gc, ScrollPane sp) {
        this.gc = gc;
        this.sp = sp;
        //just for screen clear
        display();
    }

    public void putFNode(String e, FOptions o) {
        encounteredFNodeNames.add(e);
        encounteredFNodeOptions.put(e, o);
    }

    public void populateWithFNodes(double xDefault, double yDefault) {
        ArrayList<Integer> toDel = new ArrayList<>();

        for (String fName : encounteredFNodeNames) {
            int id = getFNodeIDByTxt(fName);
            if (!FNodeMap.containsKey(id)) {
                addFNode(xDefault, yDefault, fName, encounteredFNodeOptions.get(fName));
            } else {
                FNodeMap.get(id).opts = encounteredFNodeOptions.get(fName);
            }
        }

        for (FNode fn : FNodeMap.values()) {
            if (!encounteredFNodeNames.contains(fn.fname)) {
                toDel.add(fn.ID);
            }
        }

        for (int i : toDel)
            FNodeMap.remove(i);
        encounteredFNodeNames.clear();
    }

    public void clear() {
        FConnectionMap.clear();
    }

    public void initInteractivity() {
        //TODO: Remove hardcoded 'A' key
        sp.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.A) {
                if (!sp.isPannable())
                    sp.setPannable(true);
                else
                    sp.setPannable(false);
            }
        });

        Canvas canvas = gc.getCanvas();

        canvas.setOnMouseDragged(e -> {
            if (sp.isPannable()) return;
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

               // fn.loc = cursorLoc;

                /**
                 *   xPOS  68
                 *   c (step size) 5
                 *   find smallest multiple of c greater than xPOS
                 *
                 */
                int i=(int)cursorLoc.getX();
                int j=(int)cursorLoc.getY();
                int step=20;

                while(i%step!=0){
                    i++;
                }
                while(j%step!=0){
                    j++;
                }
                Point2D snap = new Point2D(i, j);
                fn.loc = snap;

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
        for (FNode fn : FNodeMap.values())
            if (fn.fname.equals(txt))
                return fn.ID;
        return -1;
    }

    public void makeFinal(String textRef) {
        FNode fn = FNodeMap.get(getFNodeIDByTxt(textRef));
        if (fn != null)
            fn.isFinal = true;
    }

    public void makeStart(String textRef, FOptions opts) {
        FNode fn = FNodeMap.get(getFNodeIDByTxt(textRef));
        if (fn != null) {
            fn.isStart = true;
            fn.opts.fConStartLength = opts.fConStartLength;
            fn.opts.fConStartAngle = opts.fConStartAngle;
            fn.opts.fConLineWidth = opts.fConLineWidth;
            fn.opts.fConColor = opts.fConColor;
        }
    }

    public void jprFConnection(int fromID, int toID, String text, FOptions opts) {
        FNode fromFNode = FNodeMap.get(fromID);
        FNode toFNode = FNodeMap.get(toID);

        if (fromFNode == null || toFNode == null) return;

        FConnection fc = new JprFConnection(gc, fromFNode, toFNode, text, opts);

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

    public void bidFConnection(int fromID, int toID, String fromText, String toText, FOptions opts) {
        FNode fromFNode = FNodeMap.get(fromID);
        FNode toFNode = FNodeMap.get(toID);

        if (fromFNode == null || toFNode == null) return;

        FConnection fc = new BidFConnection(gc, fromFNode, toFNode, fromText, toText, opts);

        fromFNode.bidConnectsWith.add(toID);
        toFNode.bidConnectsWith.add(fromID);
        Pair<Integer, Integer> key = new Pair<>(fromID, toID);
        FConnectionMap.put(key, fc);
    }

    public void selfFConnection(int ID, String text, FOptions opts) {
        FNode fn = FNodeMap.get(ID);

        if (fn == null) return;

        FConnection fc = new SelfFConnection(gc, fn, text, opts);
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
