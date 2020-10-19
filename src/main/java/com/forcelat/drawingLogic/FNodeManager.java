package com.forcelat.drawingLogic;

import com.forcelat.parsingLogic.FOptions;
import com.forcelat.uiLogic.FSaveSense;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.TreeMap;


public class FNodeManager {
    public Integer FNodeIDGiver = 0;
    Color clearScreenColor = Color.WHITE;
    public GraphicsContext gc;
    ScrollPane sp;
    public TreeMap<Integer, FNode> FNodeMap = new TreeMap<>();
    HashMap<Pair<Integer, Integer>, FConnection> FConnectionMap = new HashMap<>();
    int selectedFNodeID = -1;
    int dragStep = 20;

    public FNodeManager(GraphicsContext gc, ScrollPane sp) {
        this.gc = gc;
        this.sp = sp;
        //just for screen clear
        display();

    }

    public void setDragStep(int dragStep) {
        this.dragStep = dragStep;
    }

    public void clear() {
        FConnectionMap.clear();
        display();
    }

    public void initInteractivity() {
        //SHIFT + DRAG = MOVE VIEW AREA
        sp.setOnKeyPressed(e -> {
            if (e.isShiftDown()) {
                sp.setPannable(true);
            }
        });
        sp.setOnKeyReleased(e -> {
            sp.setPannable(false);
        });


        Canvas canvas = gc.getCanvas();

        canvas.setOnMousePressed(e -> {
            //CTRL+ LMB = PLACE NODE THERE
            if (e.getButton() == MouseButton.PRIMARY && !e.isShiftDown() && e.isControlDown()) {
                FOptions opts = new FOptions();
                int i = (int) e.getX();
                int j = (int) e.getY();

                while (i % dragStep != 0)
                    i++;
                while (j % dragStep != 0)
                    j++;
                addFNode(i, j, FNodeIDGiver.toString(), opts);
                display();
                FSaveSense.changed();
            }
            //CTRL+ RMB = DELETE NODE THERE
            else if (e.getButton() == MouseButton.SECONDARY && !e.isShiftDown() && e.isControlDown()) {

                double searchRadius = 30;
                Point2D cursorLoc = new Point2D(e.getX(), e.getY());

                for (FNode fn : FNodeMap.values()) {
                    double dist = cursorLoc.distance(fn.loc);
                    if (dist <= searchRadius) {
                        FNodeMap.remove(fn.ID);
                        display();
                        break;
                    }
                }
                FSaveSense.changed();
            }
        });

        //LMB = MOVE SELECTED NODE
        canvas.setOnMouseDragged(e -> {
            if (sp.isPannable()) return;
            //get nearest node ID in range HARDCODED: 30 (radius default)
            double searchRadius = 30;
            Point2D cursorLoc = new Point2D(e.getX(), e.getY());

            if (!e.isControlDown()) {
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

                    int i = (int) cursorLoc.getX();
                    int j = (int) cursorLoc.getY();

                    while (i % dragStep != 0)
                        i++;
                    while (j % dragStep != 0)
                        j++;
                    fn.loc = new Point2D(i, j);

                    {
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
                }
                display();
                FSaveSense.changed();
            }
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
            if (fn.alias != null) {
                if (fn.fname.equals(txt) || fn.alias.equals(txt))
                    return fn.ID;
            } else if (fn.fname.equals(txt))
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
