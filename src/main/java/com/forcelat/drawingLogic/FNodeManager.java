package com.forcelat.drawingLogic;

import com.forcelat.parsingLogic.FOptions;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;


public class FNodeManager {
    public int FNodeIDGiver = 0;
    Color clearScreenColor = Color.WHITE;
    GraphicsContext gc;
    ScrollPane sp;
    public TreeMap<Integer, FNode> FNodeMap = new TreeMap<>();
    HashSet<String> encounteredFNodeNames = new HashSet<>();
    HashMap<String, FOptions> encounteredFNodeOptions = new HashMap<>();
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

    public void putFNode(String e, FOptions o) {
        encounteredFNodeNames.add(e);
        encounteredFNodeOptions.put(e, o);
    }

    public void populateWithFNodes() {
        ArrayList<Integer> toDel = new ArrayList<>();

        for (String fName : encounteredFNodeNames) {
            int id = getFNodeIDByTxt(fName);
            if (!FNodeMap.containsKey(id)) {
                addFNode(0, 0, fName, encounteredFNodeOptions.get(fName));
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

        sp.setOnKeyPressed(e -> {
            if (e.isShiftDown())
                sp.setPannable(true);
        });
        sp.setOnKeyReleased(e -> {
            sp.setPannable(false);
        });


        Canvas canvas = gc.getCanvas();

        sp.setOnKeyPressed(e -> {
            if (e.isControlDown()) {

            }
        });

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

                int i = (int) cursorLoc.getX();
                int j = (int) cursorLoc.getY();

                while (i % dragStep != 0)
                    i++;
                while (j % dragStep != 0)
                    j++;
                fn.loc = new Point2D(i, j);


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
