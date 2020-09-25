package com.forcelat;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class FNodeManager {

    private class FNode {
        private Point2D centerPos;
        private Color color = Color.BLACK;
        private double radius = 30;
        private int ID;
        private ArrayList<Integer> connIDList = new ArrayList<>();

        private FNode(Point2D centerPos, double radius, Color color, int ID) {
            this.centerPos = centerPos;
            this.radius = radius;
            this.color = color;
            this.ID = ID;
        }
    }

    private GraphicsContext gc;
    private Color connectionColor = Color.RED;
    private double arrowExtendFactor = 1.5f; //how far from edge should arrow start
    private double arrowWidth = 10f;
    private int IDGiver = 0;
    private int selectedFNode = -1;
    private Color selectedColor = Color.GREEN;
    private boolean isDragging = false;
    private int prevID;
    private TreeMap<Integer, FNode> FNodeMap = new TreeMap<>();

    public FNodeManager(GraphicsContext gc) {
        this.gc = gc;
    }

    public void initListeners() {
        Canvas canvas = gc.getCanvas();
        canvas.setFocusTraversable(true);
        //A + LMB -> place node
        //A + RMB -> delete node
        //LMB Drag -> move node around
        //S + LMB -> select node

        canvas.setOnKeyPressed(e->{
            //canvas.requestFocus();
            System.out.println(e.getCode().getCode());
        });

        //Place/Remove FNode
        canvas.setOnMouseReleased(e -> {
            //TODO: If node in range,select it else place new node there
            Point2D pos = new Point2D(e.getX(), e.getY());
            //Place
            if (e.getButton() == MouseButton.PRIMARY && !isDragging) {
                int currID = addFNode(pos, 30, Color.RED);

               // System.out.println(e.getClickCount());

                if (FNodeMap.size() > 1)
                    addConnection(prevID, currID);

                prevID = currID;
            }
            //Remove
            if (e.getButton() == MouseButton.SECONDARY && !isDragging) {
                int id = getFNodeInRange(pos, 30);
                deleteFNode(id);
            }
            display();
        });
        //Drag FNode
        canvas.setOnMouseDragged(e -> {
            //TODO: Mark found node as selected (if any)
            isDragging = true;
            if (e.getButton() == MouseButton.PRIMARY) {
                Point2D scanPos = new Point2D(e.getX(), e.getY());
                selectAndMove(scanPos, 30);
                display();
            }

        });
        //Reset dragging & Selected node
        canvas.setOnMousePressed(e -> {
            isDragging = false;
            resetSelection();
        });
    }

    public int addFNode(Point2D centerPos, double radius, Color color) {

        prevID = IDGiver - 1;
        FNode fn = new FNode(centerPos, radius, color, IDGiver);
        FNodeMap.put(IDGiver, fn);
        return IDGiver++;
    }

    private void selectAndMove(Point2D scanPos, double scanRange) {
        if (selectedFNode == -1)
            selectedFNode = getFNodeInRange(scanPos, scanRange);
        else
            moveFNodeTo(selectedFNode, scanPos);
    }

    private void resetSelection() {
        selectedFNode = -1;
    }

    private void deleteFNode(int nodeID) {
        //No check needed for nodeID,doesn't do anything if not found
        if (FNodeMap.size() == 0) return;
        FNodeMap.remove(nodeID);

        //Delete every reference of this node in the other nodes
        for (FNode fn : FNodeMap.values()) {
            fn.connIDList = fn.connIDList.stream().filter(e -> e != nodeID).collect(Collectors.toCollection(ArrayList::new));
        }
        System.out.println(FNodeMap.size());
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

    private int getNearestFNode(Point2D pos) {
        double shortestDist = 9999;
        int shortestID = -1;

        for (Integer fnID : FNodeMap.keySet()) {
            FNode fn = FNodeMap.get(fnID);
            Point2D fn_pos = new Point2D(fn.centerPos.getX(), fn.centerPos.getY());
            double dist = fn_pos.distance(pos);

            if (dist < shortestDist) {
                shortestDist = dist;
                shortestID = fnID;
            }
        }
        return shortestID;
    }

    public void addConnection(int idFrom, int idTo) {
        FNodeMap.get(idFrom).connIDList.add(idTo);
    }

    public void display() {
        //clear screen
        double width = gc.getCanvas().getWidth();
        double height = gc.getCanvas().getHeight();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, width, height);

        for (FNode fn : FNodeMap.values()) {
            //draw every connection
            for (Integer connID : fn.connIDList) {
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

            if (selectedFNode == fn.ID) {
                gc.setFill(selectedColor);
                double selRadius = fn.radius * 0.8f;
                gc.fillOval(x-selRadius, y-selRadius, selRadius * 2, selRadius * 2);
            }
        }
    }

    private void connectFNodeIDs2(int fromID, int toID) {
        //if (index1 < 0 || index1 > FNodeList.size() || index2 < 0 || index2 > FNodeList.size()) return false;

        FNode fn1 = FNodeMap.get(toID);
        FNode fn2 = FNodeMap.get(fromID);

        boolean fromHasPointingArrow = fn1.connIDList.contains(fn2.ID);

        double distanceX = fn2.centerPos.getX() - fn1.centerPos.getX();
        double distanceY = fn2.centerPos.getY() - fn1.centerPos.getY();
        double angleStart;
        double angleEnd;

        if (fromHasPointingArrow) {
            angleEnd = (Math.atan2(distanceY, distanceX) + Math.PI / 6);
            angleStart = (Math.atan2(distanceY, distanceX) - Math.PI);
        } else {
            angleEnd = (Math.atan2(distanceY, distanceX));
            angleStart = (Math.atan2(distanceY, distanceX) - Math.PI);
        }

        double xStart = Math.cos(angleStart) * fn2.radius + fn2.centerPos.getX();
        double yStart = Math.sin(angleStart) * fn2.radius + fn2.centerPos.getY();

        double xEnd = Math.cos(angleEnd) * fn1.radius + fn1.centerPos.getX();
        double yEnd = Math.sin(angleEnd) * fn1.radius + fn1.centerPos.getY();

        double xEndExt = Math.cos(angleEnd) * fn1.radius * arrowExtendFactor + fn1.centerPos.getX();
        double yEndExt = Math.sin(angleEnd) * fn1.radius * arrowExtendFactor + fn1.centerPos.getY();


        //bezier
        Point2D midPoint = new Point2D((xStart + xEndExt) / 2, (yStart + yEndExt) / 2);
        double angleBezier;

        if (fromHasPointingArrow) {
            angleBezier = angleEnd + Math.PI / 2;
        } else {
            angleBezier = angleEnd - Math.PI / 2;
        }

        double xControl = Math.cos(angleBezier) * 30 + midPoint.getX();
        double yControl = Math.sin(angleBezier) * 30 + midPoint.getY();

        Point2D[] bezierPoints = new Point2D[20];
        double t = 0;
        double tStep = 1f / (20 - 1);

        for (int i = 0; i < 20; i++) {
            double x0 = t * xStart + (1 - t) * xControl;
            double y0 = t * yStart + (1 - t) * yControl;

            double x2 = t * xControl + (1 - t) * xEndExt;
            double y2 = t * yControl + (1 - t) * yEndExt;

            double x = t * x0 + (1 - t) * x2;
            double y = t * y0 + (1 - t) * y2;
            bezierPoints[i] = new Point2D(x, y);
            t += tStep;
        }

        gc.beginPath();
        gc.setStroke(connectionColor);
        gc.setLineWidth(3);
        gc.stroke();
        for (int i = 0; i < 20; i++) {
            double x = bezierPoints[i].getX();
            double y = bezierPoints[i].getY();
            gc.lineTo(x, y);
            gc.stroke();
        }
        gc.closePath();

        double xArrowLine1 = Math.cos(angleEnd - Math.PI / 2) * arrowWidth + xEndExt;
        double yArrowLine1 = Math.sin(angleEnd - Math.PI / 2) * arrowWidth + yEndExt;
        double xArrowLine2 = Math.cos(angleEnd + Math.PI / 2) * arrowWidth + xEndExt;
        double yArrowLine2 = Math.sin(angleEnd + Math.PI / 2) * arrowWidth + yEndExt;


        gc.setFill(connectionColor);
        gc.fillPolygon(new double[]{xArrowLine1, xArrowLine2, xEnd},
                new double[]{yArrowLine1, yArrowLine2, yEnd}, 3);

    }

    private void connectFNodeIDs(int fromID, int toID) {
        FNode fn1 = FNodeMap.get(toID);
        FNode fn2 = FNodeMap.get(fromID);

        boolean fromHasPointingArrow = fn1.connIDList.contains(fn2.ID);

        double distanceX = fn2.centerPos.getX() - fn1.centerPos.getX();
        double distanceY = fn2.centerPos.getY() - fn1.centerPos.getY();
        double angleEnd;
        double angleStart;
        double approachAngle = Math.PI / 10;

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
            gc.setLineWidth(3);
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

        double xStart = Math.cos(angleStart) * fn2.radius + fn2.centerPos.getX();
        double yStart = Math.sin(angleStart) * fn2.radius + fn2.centerPos.getY();

        double xEndExt = Math.cos(angleEnd) * fn1.radius * arrowExtendFactor + fn1.centerPos.getX();
        double yEndExt = Math.sin(angleEnd) * fn1.radius * arrowExtendFactor + fn1.centerPos.getY();

        double xEndRot = Math.cos(angleEnd + (approachAngle - Math.PI / 20) * f) * fn1.radius + fn1.centerPos.getX();
        double yEndRot = Math.sin(angleEnd + (approachAngle - Math.PI / 20) * f) * fn1.radius + fn1.centerPos.getY();

        gc.beginPath();
        gc.setStroke(connectionColor);
        gc.setLineWidth(3);
        gc.stroke();
        gc.moveTo(xStart, yStart);
        gc.lineTo(xEndExt, yEndExt);
        gc.stroke();

        double xArrowLine1 = Math.cos(angleEnd - Math.PI / 2 - approachAngle * f) * arrowWidth + xEndExt;
        double yArrowLine1 = Math.sin(angleEnd - Math.PI / 2 - approachAngle * f) * arrowWidth + yEndExt;
        double xArrowLine2 = Math.cos(angleEnd + Math.PI / 2 - approachAngle * f) * arrowWidth + xEndExt;
        double yArrowLine2 = Math.sin(angleEnd + Math.PI / 2 - approachAngle * f) * arrowWidth + yEndExt;

        gc.setFill(connectionColor);
        gc.fillPolygon(new double[]{xArrowLine1, xArrowLine2, xEndRot},
                new double[]{yArrowLine1, yArrowLine2, yEndRot}, 3);
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
