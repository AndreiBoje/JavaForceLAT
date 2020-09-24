package com.forcelat;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
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
    private double bezierHeight = 40f;
    private int bezierPointsCount = 8;
    private int IDGiver = 0;
    private int selectedFNode=-1;
    //TODO: Implement selection here,not in main program;

    private TreeMap<Integer, FNode> FNodeMap = new TreeMap<>();

    public FNodeManager(GraphicsContext gc) {
        this.gc = gc;
    }

    public int addFNode(Point2D centerPos, double radius, Color color) {
        FNode fn = new FNode(centerPos, radius, color, IDGiver);
        FNodeMap.put(IDGiver, fn);
        return IDGiver++;
    }

    public void deleteFNode(int nodeID){
        //No check needed for nodeID,doesn't do anything if not found
        if(FNodeMap.size() == 0) return;
        FNodeMap.remove(nodeID);

        //Delete every reference of this node in the other nodes
        for(FNode fn : FNodeMap.values()){
            fn.connIDList = fn.connIDList.stream().filter(e -> e != nodeID).collect(Collectors.toCollection(ArrayList::new));
        }
        System.out.println(FNodeMap.size());
    }

    public void moveFNodeTo(int ID,Point2D pos){
        FNodeMap.get(ID).centerPos = pos;
    }

    public int getFNodeInRange(Point2D pos,double range){
        int shortestID=-1;
        double shortestRange=range;

        for(Integer fnID : FNodeMap.keySet()){
            FNode fn = FNodeMap.get(fnID);
            Point2D fn_pos = new Point2D(fn.centerPos.getX(),fn.centerPos.getY());
            double dist = fn_pos.distance(pos);

            if(dist < shortestRange){
                shortestRange = dist;
                shortestID=fnID;
            }
        }

        return shortestID;
    }

    public int getNearestFNode(Point2D pos){
        double shortestDist=9999;
        int shortestID=-1;

        for(Integer fnID : FNodeMap.keySet()){
            FNode fn = FNodeMap.get(fnID);
            Point2D fn_pos = new Point2D(fn.centerPos.getX(),fn.centerPos.getY());
            double dist = fn_pos.distance(pos);

            if(dist < shortestDist){
                shortestDist = dist;
                shortestID=fnID;
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
                connectFNodeIDs2(fn.ID, connID);
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

    //TODO: make bezier height dependent on distance
    private boolean connectFNodeIDs2(int index1, int index2) {
        //if (index1 < 0 || index1 > FNodeList.size() || index2 < 0 || index2 > FNodeList.size()) return false;

        FNode fn1 = FNodeMap.get(index2);
        FNode fn2 = FNodeMap.get(index1);

        double distanceX = fn2.centerPos.getX() - fn1.centerPos.getX();
        double distanceY = fn2.centerPos.getY() - fn1.centerPos.getY();
        double angleEnd = (Math.atan2(distanceY, distanceX) - Math.PI);
        double angleStart = (Math.atan2(distanceY, distanceX));

        double xStart = Math.cos(angleEnd) * fn2.radius + fn2.centerPos.getX();
        double yStart = Math.sin(angleEnd) * fn2.radius + fn2.centerPos.getY();

        double xEnd = Math.cos(angleStart) * fn1.radius + fn1.centerPos.getX();
        double yEnd = Math.sin(angleStart) * fn1.radius + fn1.centerPos.getY();

        double xEndExt = Math.cos(angleEnd) * fn1.radius * -arrowExtendFactor + fn1.centerPos.getX();
        double yEndExt = Math.sin(angleEnd) * fn1.radius * -arrowExtendFactor + fn1.centerPos.getY();

        //bezier
        Point2D midPoint = new Point2D((xStart + xEndExt) / 2, (yStart + yEndExt) / 2);
        double angleBezier = angleEnd - Math.PI / 2;
        double xControl = Math.cos(angleBezier) * bezierHeight + midPoint.getX();
        double yControl = Math.sin(angleBezier) * bezierHeight + midPoint.getY();

        Point2D[] bezierPoints = new Point2D[bezierPointsCount];
        double t = 0;
        double tStep = 1f / (bezierPointsCount - 1);

        for (int i = 0; i < bezierPointsCount; i++) {
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
        for (int i = 0; i < bezierPointsCount; i++) {
            double x = bezierPoints[i].getX();
            double y = bezierPoints[i].getY();
            gc.lineTo(x, y);
            gc.stroke();
        }
        double xArrowLine1 = Math.cos(angleEnd - Math.PI / 2) * arrowWidth + xEndExt;
        double yArrowLine1 = Math.sin(angleEnd - Math.PI / 2) * arrowWidth + yEndExt;
        double xArrowLine2 = Math.cos(angleEnd + Math.PI / 2) * arrowWidth + xEndExt;
        double yArrowLine2 = Math.sin(angleEnd + Math.PI / 2) * arrowWidth + yEndExt;


        gc.setFill(connectionColor);
        gc.fillPolygon(new double[]{xArrowLine1, xArrowLine2, xEnd},
                new double[]{yArrowLine1, yArrowLine2, yEnd}, 3);

        return true;
    }

    private boolean connectFNodeIDs(int index1, int index2) {
//        if (index1 < 0 || index1 > FNodeList.size() || index2 < 0 || index2 > FNodeList.size()) return false;
        FNode fn1 = FNodeMap.get(index2);
        FNode fn2 = FNodeMap.get(index1);

        double distanceX = fn2.centerPos.getX() - fn1.centerPos.getX();
        double distanceY = fn2.centerPos.getY() - fn1.centerPos.getY();
        double angleEnd = (Math.atan2(distanceY, distanceX) - Math.PI);
        double angleStart = (Math.atan2(distanceY, distanceX));

        double xStart = Math.cos(angleEnd) * fn2.radius + fn2.centerPos.getX();
        double yStart = Math.sin(angleEnd) * fn2.radius + fn2.centerPos.getY();

        double xEnd = Math.cos(angleStart) * fn1.radius + fn1.centerPos.getX();
        double yEnd = Math.sin(angleStart) * fn1.radius + fn1.centerPos.getY();

        double xEndExt = Math.cos(angleEnd) * fn1.radius * -arrowExtendFactor + fn1.centerPos.getX();
        double yEndExt = Math.sin(angleEnd) * fn1.radius * -arrowExtendFactor + fn1.centerPos.getY();


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

        return true;
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

    public void setBezierHeight(double bezierHeight) {
        this.bezierHeight = bezierHeight;
    }

    public int getFNodeCount() {
        return FNodeMap.size();
    }
}
