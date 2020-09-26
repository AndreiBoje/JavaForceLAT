package com.forcelat;

import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;


import java.util.*;
import java.util.stream.Collectors;

public class FNodeManager {

    private class FNode {
        private Point2D centerPos;
        private Color color = Color.BLACK;
        private double radius = 30;
        private int ID;
        private String text;
        private HashMap<Integer,String> connIDText = new HashMap<>();

        private FNode(Point2D centerPos,String text ,double radius, Color color, int ID) {
            this.centerPos = centerPos;
            this.radius = radius;
            this.color = color;
            this.ID = ID;
            this.text = text;
        }
    }

    private GraphicsContext gc;
    private Color connectionColor = Color.RED;
    private Color nodeColor = Color.RED;
    private Color arrowColor = Color.RED;
    private Color selectedColor = Color.GREEN;
    private double arrowExtendFactor = 1.3f; //how far from edge should arrow start
    private double arrowWidth = 7f;
    private int IDGiver = 0;
    private int selectedFNode = -1;
    private int prevID;
    private int fromFNodeID = -1;
    private TreeMap<Integer, FNode> FNodeMap = new TreeMap<>();

    public FNodeManager(GraphicsContext gc) {
        this.gc = gc;
    }

    public void initListeners() {
        Canvas canvas = gc.getCanvas();
        canvas.setFocusTraversable(true);
        //CTRL + LMB -> place node
        //CTRL + RMB -> delete node
        //LMB Drag -> move node around
        //LMB -> select node

        //Place/Remove FNode
        canvas.setOnMousePressed(e -> {
            Point2D pos = new Point2D(e.getX(), e.getY());
            if (e.getButton() == MouseButton.PRIMARY && e.isControlDown()) {

                //if node not found at mouse pos,generate one
                int currID = getFNodeInRange(pos, 30);
                /*TODO: Temp disable node creation thru mouse
                if (currID == -1)
                    currID = addFNode(pos, 30, Color.RED);
                    */

                if (fromFNodeID != -1)
                    addConnection(fromFNodeID, currID,"");

                prevID = currID;
            }
            if (e.getButton() == MouseButton.SECONDARY && e.isControlDown()) {
                int fn = getFNodeInRange(pos, 30);
                deleteFNode(fn);
            }
            //select focus node
            if (e.getButton() == MouseButton.PRIMARY) {
                fromFNodeID = getFNodeInRange(pos, 30);
            }

            display();
        });
        canvas.setOnMouseDragged(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                Point2D scanPos = new Point2D(e.getX(), e.getY());
                selectAndMove(scanPos, 30);
                display();
            }
        });

        canvas.setOnMouseReleased(e -> {
            resetDragSelection();
        });

    }

    public void addFNode(Point2D centerPos, String text,double radius, Color color,int ID) {

       // prevID = IDGiver - 1;
        //FNode fn = new FNode(centerPos, radius, color, IDGiver);
        //FNodeMap.put(IDGiver, fn);
        //return IDGiver++;
        FNode fn = new FNode(centerPos,text, radius, color, ID);
        FNodeMap.put(ID, fn);
    }

    private void selectAndMove(Point2D scanPos, double scanRange) {
        if (selectedFNode == -1)
            selectedFNode = getFNodeInRange(scanPos, scanRange);
        else
            moveFNodeTo(selectedFNode, scanPos);
    }

    private void resetDragSelection() {
        selectedFNode = -1;
    }

    private void deleteFNode(int nodeID) {
        //No check needed for nodeID,doesn't do anything if not found
        if (FNodeMap.size() == 0) return;
        FNodeMap.remove(nodeID);

        //Delete every reference of this node in the other nodes
        for (FNode fn : FNodeMap.values())
            fn.connIDText.remove(nodeID);

    }

    private void moveFNodeTo(int ID, Point2D pos) {
        FNodeMap.get(ID).centerPos = pos;
    }

    private int getFNodeInRange(Point2D pos, double range) {
        int shortestID = -1;
        double shortestRange = range;

        for (Integer fnID : FNodeMap.keySet()) {
            FNode fn = FNodeMap.get(fnID);
            Point2D fn_pos = new Point2D(fn.centerPos.getX(), fn.centerPos.getY());
            double dist = fn_pos.distance(pos);

            if (dist < shortestRange) {
                shortestRange = dist;
                shortestID = fnID;
            }
        }

        return shortestID;
    }

    public void addConnection(int idFrom, int idTo,String text) {
        FNode fn = FNodeMap.get(idFrom);
        fn.connIDText.put(idTo,text);
    }

    public void display() {
        //clear screen
        double width = gc.getCanvas().getWidth();
        double height = gc.getCanvas().getHeight();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, width, height);

        for (FNode fn : FNodeMap.values()) {
            //draw every connection
            for (Integer connID : fn.connIDText.keySet()) {
                connectFNodeIDs(fn.ID, connID);
            }
        }
        for (FNode fn : FNodeMap.values()) {
            //draw every node
            double x = fn.centerPos.getX();
            double y = fn.centerPos.getY();
            double radius = fn.radius;

            gc.setStroke(fn.color);
            gc.setLineWidth(5);
            gc.strokeOval(x - radius, y - radius, radius * 2, radius * 2);


            if (fromFNodeID == fn.ID) {
                gc.setFill(selectedColor);
                double selRadius = fn.radius * 0.8f;
                gc.fillOval(x - selRadius, y - selRadius, selRadius * 2, selRadius * 2);
            }

            gc.save();
            gc.setTextBaseline(VPos.CENTER);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setFill(Color.BLACK);
            gc.setFont(new Font("Calibri", 20));

            Rotate r = new Rotate(0, x, y);
            gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());

            gc.fillText(fn.text, x, y);
            gc.restore();
        }

    }

    private void connectFNodeIDs(int fromID, int toID) {
        FNode fn1 = FNodeMap.get(toID);
        FNode fn2 = FNodeMap.get(fromID);

        boolean fromHasPointingArrow = fn1.connIDText.containsKey(fn2.ID);

        double distanceX = fn2.centerPos.getX() - fn1.centerPos.getX();
        double distanceY = fn2.centerPos.getY() - fn1.centerPos.getY();
        double angleEnd;
        double angleStart;
        double approachAngle = Math.PI / 20;

        //points to self
        if (fromID == toID) {
            int bezierPointsCount = 20;
            Point2D basePoint = new Point2D(fn1.centerPos.getX(), fn1.centerPos.getY() - fn1.radius);
            Point2D cp1 = new Point2D(basePoint.getX() + fn1.radius * 2, basePoint.getY() - fn1.radius * 2);
            Point2D cp2 = new Point2D(basePoint.getX() - fn1.radius * 2, basePoint.getY() - fn1.radius * 2);

            double angle = -Math.PI / 3;
            double xRBase = Math.cos(angle) * fn1.radius + fn1.centerPos.getX();
            double yRBase = Math.sin(angle) * fn1.radius + fn1.centerPos.getY();
            double xLBase = Math.cos(angle * 2) * fn1.radius + fn1.centerPos.getX();
            double yLBase = Math.sin(angle * 2) * fn1.radius + fn1.centerPos.getY();

            Point2D[] bezierPoints = new Point2D[bezierPointsCount];
            double t = 0;
            double tStep = 1f / (bezierPointsCount - 1);

            for (int i = 0; i < bezierPointsCount; i++) {
                double x = Math.pow((1 - t), 3) * xRBase + 3 * Math.pow((1 - t), 2) * t * cp1.getX() + 3 * (1 - t) * t * t * cp2.getX() + t * t * t * xLBase;
                double y = Math.pow((1 - t), 3) * yRBase + 3 * Math.pow((1 - t), 2) * t * cp1.getY() + 3 * (1 - t) * t * t * cp2.getY() + t * t * t * yLBase;
                bezierPoints[i] = new Point2D(x, y);
                t += tStep;
            }

            gc.beginPath();
            gc.setStroke(connectionColor);
            gc.setLineWidth(2.3);
            gc.stroke();
            for (int i = 0; i < 20; i++) {
                double x = bezierPoints[i].getX();
                double y = bezierPoints[i].getY();
                gc.lineTo(x, y);
                gc.stroke();
            }
            gc.closePath();

            //arrow
            double xArrowBase = Math.cos(angle * 2) * fn1.radius * arrowExtendFactor + fn1.centerPos.getX();
            double yArrowBase = Math.sin(angle * 2) * fn1.radius * arrowExtendFactor + fn1.centerPos.getY();

            double xArrowLeft = Math.cos(angle * 2 + Math.PI / 2) * arrowWidth + xArrowBase;
            double yArrowLeft = Math.sin(angle * 2 + Math.PI / 2) * arrowWidth + yArrowBase;

            double xArrowRight = Math.cos(angle * 2 - Math.PI / 2) * arrowWidth + xArrowBase;
            double yArrowRight = Math.sin(angle * 2 - Math.PI / 2) * arrowWidth + yArrowBase;

            gc.save();
            gc.setTextBaseline(VPos.CENTER);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setFill(Color.BLACK);
            gc.setFont(new Font("Calibri", 20));

            Rotate r = new Rotate(0, basePoint.getX(), cp1.getY());
            gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());

            gc.fillText(fn2.connIDText.get(fn1.ID), basePoint.getX(), cp1.getY());
            gc.restore();

            gc.setFill(connectionColor);
            gc.fillPolygon(new double[]{xLBase, xArrowLeft, xArrowRight},
                    new double[]{yLBase, yArrowLeft, yArrowRight}, 3);
            return;
        }

        //points to another already
        if (fromHasPointingArrow) {
            angleEnd = (Math.atan2(distanceY, distanceX) + approachAngle);
            angleStart = (Math.atan2(distanceY, distanceX) - Math.PI - approachAngle);
        }
        //"new" pointing
        else {
            angleEnd = (Math.atan2(distanceY, distanceX));
            angleStart = (Math.atan2(distanceY, distanceX) - Math.PI);
        }

        double f = fromHasPointingArrow ? 1 : 0;

        double xStart = Math.cos(angleStart - approachAngle * f) * fn2.radius + fn2.centerPos.getX();
        double yStart = Math.sin(angleStart - approachAngle * f) * fn2.radius + fn2.centerPos.getY();

        double xEndExt = Math.cos(angleEnd) * fn1.radius * arrowExtendFactor + fn1.centerPos.getX();
        double yEndExt = Math.sin(angleEnd) * fn1.radius * arrowExtendFactor + fn1.centerPos.getY();

        double xEndRot = Math.cos(angleEnd + approachAngle * f) * fn1.radius + fn1.centerPos.getX();
        double yEndRot = Math.sin(angleEnd + approachAngle * f) * fn1.radius + fn1.centerPos.getY();

        double xMid = (xStart + xEndRot) * 0.5;
        double yMid = (yStart + yEndRot) * 0.5;

        double xAdv = Math.cos(angleStart) * 10 + xMid;
        double yAdv = Math.sin(angleStart) * 10 + yMid;

        gc.beginPath();
        gc.setStroke(connectionColor);
        gc.setLineWidth(2.3);
        gc.stroke();
        gc.moveTo(xStart, yStart);
        gc.lineTo(xEndRot, yEndRot);
        gc.stroke();

        double xArrowLine1 = Math.cos(angleEnd - Math.PI / 2 - approachAngle * f) * arrowWidth + xMid;
        double yArrowLine1 = Math.sin(angleEnd - Math.PI / 2 - approachAngle * f) * arrowWidth + yMid;
        double xArrowLine2 = Math.cos(angleEnd + Math.PI / 2 - approachAngle * f) * arrowWidth + xMid;
        double yArrowLine2 = Math.sin(angleEnd + Math.PI / 2 - approachAngle * f) * arrowWidth + yMid;

        double xText = Math.cos(angleEnd + Math.PI / 2 - approachAngle * f) * 3 * arrowWidth + xMid;
        double yText = Math.sin(angleEnd + Math.PI / 2 - approachAngle * f) * 3 * arrowWidth + yMid;


        gc.save();
        gc.setTextBaseline(VPos.CENTER);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Calibri", 20));
        //posibility to change angle later
        Rotate r = new Rotate(0, xText, yText);
        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());

        gc.fillText(fn2.connIDText.get(fn1.ID), xText, yText);
        gc.restore();

        gc.setFill(connectionColor);
        gc.fillPolygon(new double[]{xArrowLine1, xArrowLine2, xAdv},
                new double[]{yArrowLine1, yArrowLine2, yAdv}, 3);
    }

    //GS
    public void setConnectionColor(Color connectionColor) {
        this.connectionColor = connectionColor;
    }

    public void setArrowExtendFactor(double extendFactor) {
        this.arrowExtendFactor = extendFactor;
    }

    public void setArrowWidth(double arrowWidth) {
        this.arrowWidth = arrowWidth;
    }

}
