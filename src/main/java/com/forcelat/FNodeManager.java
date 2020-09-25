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
    private boolean isDragging = false;
    //TODO: Implement selection here,not in main program;

    private TreeMap<Integer, FNode> FNodeMap = new TreeMap<>();

    Stack<Integer> prevID = new Stack<>();

    public FNodeManager(GraphicsContext gc) {
        this.gc = gc;
    }

    public void initListeners() {
        Canvas canvas = gc.getCanvas();
        //Place/Remove FNode
        canvas.setOnMouseReleased(e -> {
            Point2D pos = new Point2D(e.getX(), e.getY());
            //Place
            if (e.getButton() == MouseButton.PRIMARY && !isDragging) {
                int currID = addFNode(pos, 30, Color.BLUE);

                if (FNodeMap.size() > 1)
                    addConnection(prevID.pop(), currID);

                prevID.push(currID);
            }
            //Remove
            if (e.getButton() == MouseButton.SECONDARY && !isDragging) {
                int id = getFNodeInRange(pos, 30);
                deleteFNode(id);
            }
            display();
        });
        //Drag Fnode //TODO: made "range" modular
        canvas.setOnMouseDragged(e -> {
            isDragging = true;
            if (e.getButton() == MouseButton.PRIMARY) {
                Point2D scanPos = new Point2D(e.getX(), e.getY());
                selectAndMove(scanPos, 30);
                display();
            }

        });
        //Reset dragging
        canvas.setOnMousePressed(e -> {
            isDragging = false;
            resetSelection();
        });
    }

    public int addFNode(Point2D centerPos, double radius, Color color) {
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

            //draw every node
            double x = fn.centerPos.getX() - fn.radius;
            double y = fn.centerPos.getY() - fn.radius;
            double radius = fn.radius;
            gc.setStroke(fn.color);
            gc.setLineWidth(5);
            gc.strokeOval(x, y, radius * 2, radius * 2);

        }
    }

    //TODO: Handle self connection
    //TODO: Handle out of bounds indexes
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
//        if (index1 < 0 || index1 > FNodeList.size() || index2 < 0 || index2 > FNodeList.size()) return false;
        FNode fn1 = FNodeMap.get(toID);
        FNode fn2 = FNodeMap.get(fromID);

        boolean fromHasPointingArrow = fn1.connIDList.contains(fn2.ID);
        boolean fromPointsToSelf = fn1.connIDList.contains(fn1.ID);

        double distanceX = fn2.centerPos.getX() - fn1.centerPos.getX();
        double distanceY = fn2.centerPos.getY() - fn1.centerPos.getY();
        double angleEnd;
        double angleStart;

        if (fromHasPointingArrow) {
            angleEnd = (Math.atan2(distanceY, distanceX) + Math.PI / 6);
            angleStart = (Math.atan2(distanceY, distanceX) - Math.PI - Math.PI / 6);
        } else {
            angleEnd = (Math.atan2(distanceY, distanceX));
            angleStart = (Math.atan2(distanceY, distanceX) - Math.PI);
        }

        if (fromPointsToSelf) {
            //TODO: 4 Point bezier arrow to itself,accordingly rotated arrow tip
            //bezier
            int bezierPointsCount = 20;
            Point2D basePoint = new Point2D(fn1.centerPos.getX(), fn1.centerPos.getY() - fn1.radius);
            Point2D cp1 = new Point2D(basePoint.getX() + fn1.radius * 2, basePoint.getY() - fn1.radius * 2);
            Point2D cp2 = new Point2D(basePoint.getX() - fn1.radius * 2, basePoint.getY() - fn1.radius * 2);

            double angle = -Math.PI/3;
            double x0 = Math.cos(angle)* fn2.radius+fn1.centerPos.getX();
            double y0 = Math.sin(angle)* fn2.radius+fn1.centerPos.getY();

            gc.setFill(Color.GREEN);
            gc.fillOval(cp1.getX() - 5, cp1.getY() - 5, 10, 10);
            gc.fillOval(x0 - 5, y0 - 5, 10, 10);

            //gc.fillOval(cp1.getX() - 5, cp1.getY() - 5, 10, 10);
            //gc.fillOval(cp2.getX() - 5, cp2.getY() - 5, 10, 10);
            //gc.fillOval(arrowTip.getX() - 5, arrowTip.getY() - 5, 10, 10);

            Point2D[] bezierPoints = new Point2D[bezierPointsCount];
            double t = 0;
            double tStep = 1f / (bezierPointsCount - 1);

            for (int i = 0; i < bezierPointsCount; i++) {
                double x = Math.pow((1 - t), 3) * basePoint.getX() + 3 * Math.pow((1 - t), 2) * t * cp1.getX() + 3 * (1 - t) * t * t * cp2.getX() + t * t * t * basePoint.getX();
                double y = Math.pow((1 - t), 3) * basePoint.getY() + 3 * Math.pow((1 - t), 2) * t * cp1.getY() + 3 * (1 - t) * t * t * cp2.getY() + t * t * t * basePoint.getY();
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
            Point2D arrowBasePoint = new Point2D(fn1.centerPos.getX(), fn1.centerPos.getY() - fn1.radius * arrowExtendFactor);
            Point2D arrowPoint1 = new Point2D(arrowBasePoint.getX() - arrowWidth, arrowBasePoint.getY());
            Point2D arrowPoint2 = new Point2D(arrowBasePoint.getX() + arrowWidth, arrowBasePoint.getY());

            gc.setFill(connectionColor);
            //gc.fillPolygon(new double[]{arrowPoint1.getX(), arrowPoint2.getX(), arrowTip.getX()},
              //      new double[]{arrowPoint1.getY(), arrowPoint2.getY(), arrowTip.getY()}, 3);
            return;
        }

        double xStart = Math.cos(angleStart) * fn2.radius + fn2.centerPos.getX();
        double yStart = Math.sin(angleStart) * fn2.radius + fn2.centerPos.getY();

        double xEnd = Math.cos(angleEnd) * fn1.radius + fn1.centerPos.getX();
        double yEnd = Math.sin(angleEnd) * fn1.radius + fn1.centerPos.getY();

        double xEndExt = Math.cos(angleEnd) * fn1.radius * arrowExtendFactor + fn1.centerPos.getX();
        double yEndExt = Math.sin(angleEnd) * fn1.radius * arrowExtendFactor + fn1.centerPos.getY();


        gc.beginPath();
        gc.setStroke(connectionColor);
        gc.setLineWidth(3);
        gc.stroke();
        gc.moveTo(xStart, yStart);
        gc.lineTo(xEndExt, yEndExt);
        gc.stroke();

        double xArrowLine1 = Math.cos(angleEnd - Math.PI / 2) * arrowWidth + xEndExt;
        double yArrowLine1 = Math.sin(angleEnd - Math.PI / 2) * arrowWidth + yEndExt;
        double xArrowLine2 = Math.cos(angleEnd + Math.PI / 2) * arrowWidth + xEndExt;
        double yArrowLine2 = Math.sin(angleEnd + Math.PI / 2) * arrowWidth + yEndExt;


        gc.setFill(connectionColor);
        gc.fillPolygon(new double[]{xArrowLine1, xArrowLine2, xEnd},
                new double[]{yArrowLine1, yArrowLine2, yEnd}, 3);
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
