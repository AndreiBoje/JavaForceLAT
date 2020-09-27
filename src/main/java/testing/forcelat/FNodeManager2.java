package testing.forcelat;

import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;


class FNode {
    GraphicsContext gcFNode;
    Point2D loc = new Point2D(0, 0);
    Color color = Color.BLACK;
    int strokeWidth = 3;
    double radius = 30;
    Integer ID;
    HashSet<Integer> unidConnectionTo = new HashSet<>();
    HashSet<Integer> bidConnectsWith = new HashSet<>();
    boolean selfConnects = false;

    public FNode(GraphicsContext gcFNode, Point2D location, int ID) {
        this.gcFNode = gcFNode;
        this.loc = location;
        this.ID = ID;
    }

    public void draw() {
        //draw text (placeholder for now!!!!)
        gcFNode.setFill(Color.RED);
        gcFNode.setFont(new Font("Calibri", 30));
        gcFNode.setTextAlign(TextAlignment.CENTER);
        gcFNode.setTextBaseline(VPos.CENTER);
        gcFNode.fillText(ID.toString(), loc.getX(), loc.getY());

        //draw contour
        gcFNode.setStroke(color);
        gcFNode.setLineWidth(strokeWidth);
        gcFNode.strokeOval(loc.getX() - radius, loc.getY() - radius, radius * 2, radius * 2);
    }
}

abstract class FConnection {
    GraphicsContext gcFConnection;
    Color color = Color.BLACK;
    int lineWidth = 3;
    Point2D fromLoc, fromLoc2;
    Point2D toLoc, toLoc2;
    Point2D cp1, cp2;
    Point2D arrowBase = new Point2D(0, 0);
    double arrowAngle = 0;
    Point2D arrowBase2 = new Point2D(0, 0);
    double arrowAngle2 = 0;


    public abstract void draw();

    public abstract void setArrowData(Point2D baseLoc, double angle);

    public abstract void setArrowData2(Point2D baseLoc, double angle, Point2D baseLoc2, double angle2);

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

        gcFConnection.setFill(color);
        gcFConnection.fillPolygon(new double[]{xArrowLeft, xArrowRight, xTip}, new double[]{yArrowLeft, yArrowRight, yTip}, 3);
    }

}

class UnidFConnection extends FConnection {

    public UnidFConnection(GraphicsContext gcFConnection, Point2D fromLoc, Point2D toLoc) {
        this.gcFConnection = gcFConnection;
        this.fromLoc = fromLoc;
        this.toLoc = toLoc;
    }

    @Override
    public void draw() {
        gcFConnection.beginPath();
        gcFConnection.setStroke(color);
        gcFConnection.setLineWidth(lineWidth);
        gcFConnection.moveTo(fromLoc.getX(), fromLoc.getY());
        gcFConnection.lineTo(toLoc.getX(), toLoc.getY());
        gcFConnection.stroke();
        gcFConnection.closePath();
        drawArrow(arrowBase, arrowAngle);
    }

    @Override
    public void setArrowData(Point2D baseLoc, double angle) {
        this.arrowBase = baseLoc;
        this.arrowAngle = angle;
    }

    @Override
    public void setArrowData2(Point2D baseLoc, double angle, Point2D baseLoc2, double angle2) {

    }
}

class BidFConnection extends FConnection {

    public BidFConnection(GraphicsContext gcFConnection, Point2D fromLoc, Point2D toLoc, Point2D fromLoc2, Point2D toLoc2) {
        this.gcFConnection = gcFConnection;
        this.fromLoc = fromLoc;
        this.toLoc = toLoc;
        this.fromLoc2 = fromLoc2;
        this.toLoc2 = toLoc2;
    }

    @Override
    public void draw() {
        gcFConnection.beginPath();
        gcFConnection.setStroke(color);
        gcFConnection.setLineWidth(lineWidth);
        gcFConnection.moveTo(fromLoc.getX(), fromLoc.getY());
        gcFConnection.lineTo(toLoc.getX(), toLoc.getY());
        gcFConnection.moveTo(fromLoc2.getX(), fromLoc2.getY());
        gcFConnection.lineTo(toLoc2.getX(), toLoc2.getY());
        gcFConnection.stroke();
        gcFConnection.closePath();
        drawArrow(arrowBase, arrowAngle);
        drawArrow(arrowBase2, arrowAngle2);

    }

    @Override
    public void setArrowData(Point2D baseLoc, double angle) {

    }

    @Override
    public void setArrowData2(Point2D baseLoc, double angle, Point2D baseLoc2, double angle2) {
        this.arrowBase = baseLoc;
        this.arrowBase2 = baseLoc2;
        this.arrowAngle = angle;
        this.arrowAngle2 = angle2;
    }
}

class SelfFConnection extends FConnection {

    public SelfFConnection(GraphicsContext gc, Point2D startLoc, Point2D cp1, Point2D cp2, Point2D endLoc) {
        this.gcFConnection = gc;
        this.fromLoc = startLoc;
        this.toLoc = endLoc;
        this.cp1 = cp1;
        this.cp2 = cp2;
    }

    @Override
    public void draw() {
        gcFConnection.beginPath();
        gcFConnection.setStroke(color);
        gcFConnection.setLineWidth(lineWidth);

        //draw bezier
        int pointsNo = 14;
        Point2D[] bezierPoints = new Point2D[pointsNo];
        float t = 0;
        float tStep = 1f / (pointsNo - 1);

        for (int i = 0; i < pointsNo; i++) {
            double x = Math.pow((1 - t), 3) * fromLoc.getX() + 3 * Math.pow((1 - t), 2) * t * cp1.getX() + 3 * (1 - t) * t * t * cp2.getX() + t * t * t * toLoc.getX();
            double y = Math.pow((1 - t), 3) * fromLoc.getY() + 3 * Math.pow((1 - t), 2) * t * cp1.getY() + 3 * (1 - t) * t * t * cp2.getY() + t * t * t * toLoc.getY();
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
        drawArrow(arrowBase, arrowAngle);
    }

    @Override
    public void setArrowData(Point2D baseLoc, double angle) {
        this.arrowBase = baseLoc;
        this.arrowAngle = angle;
    }

    @Override
    public void setArrowData2(Point2D baseLoc, double angle, Point2D baseLoc2, double angle2) {

    }
}

class JprFConnection extends FConnection {

    public JprFConnection(GraphicsContext gc,Point2D fromLoc,Point2D cp1,Point2D cp2,Point2D toLoc) {
        this.gcFConnection = gc;
        this.fromLoc = fromLoc;
        this.cp1 = cp1;
        this.cp2 = cp2;
        this.toLoc=toLoc;
    }

    @Override
    public void draw() {
        gcFConnection.beginPath();
        gcFConnection.setStroke(color);
        gcFConnection.setLineWidth(lineWidth);
        gcFConnection.moveTo(fromLoc.getX(),fromLoc.getY());
        gcFConnection.lineTo(cp1.getX(),cp1.getY());
        gcFConnection.lineTo(cp2.getX(),cp2.getY());
        gcFConnection.lineTo(toLoc.getX(),toLoc.getY());
        gcFConnection.stroke();
        gcFConnection.closePath();
        drawArrow(arrowBase, arrowAngle);
    }

    @Override
    public void setArrowData(Point2D baseLoc, double angle) {
        this.arrowBase=baseLoc;
        this.arrowAngle=angle;
    }

    @Override
    public void setArrowData2(Point2D baseLoc, double angle, Point2D baseLoc2, double angle2) {

    }
}


public class FNodeManager2 {
    private int FConnIDGiver = 0;
    private Color clearScreenColor = Color.WHITE;
    private GraphicsContext gc;
    private TreeMap<Integer, FNode> FNodeMap = new TreeMap<>();
    private TreeMap<Integer, FConnection> FConnectionMap = new TreeMap<>();

    public FNodeManager2(GraphicsContext gc) {
        this.gc = gc;
    }

    public void addFNode(double xPos, double yPos, int ID) {
        Point2D loc = new Point2D(xPos, yPos);
        FNode fn = new FNode(gc, loc, ID);
        FNodeMap.put(ID, fn);
    }

    public void jprFConnection(int fromID, int toID) {
        FNode fromFNode = FNodeMap.get(fromID);
        FNode toFNode = FNodeMap.get(toID);

        if (fromFNode == null || toFNode == null) return;

        double distX = (fromFNode.loc.getX() - toFNode.loc.getX());
        double distY = (fromFNode.loc.getY() - toFNode.loc.getY());

        double angle = Math.atan2(distY, distX);
        double angleRise = Math.PI/4;
        double extendFactor = 100;

        double xStart = Math.cos(angle+Math.PI-angleRise)*fromFNode.radius+fromFNode.loc.getX();
        double yStart = Math.sin(angle+Math.PI-angleRise)*fromFNode.radius+fromFNode.loc.getY();
        Point2D startLoc = new Point2D(xStart,yStart);

        double xEnd = Math.cos(angle+angleRise)*toFNode.radius+toFNode.loc.getX();
        double yEnd = Math.sin(angle+angleRise)*toFNode.radius+toFNode.loc.getY();
        Point2D endLoc = new Point2D(xEnd,yEnd);

        double xHandleL = Math.cos(angle+Math.PI-angleRise)*extendFactor+fromFNode.loc.getX();
        double yHandleL = Math.sin(angle+Math.PI-angleRise)*extendFactor+fromFNode.loc.getY();
        Point2D cp1 = new Point2D(xHandleL,yHandleL);

        double xHandleR = Math.cos(angle+angleRise)*extendFactor+toFNode.loc.getX();
        double yHandleR = Math.sin(angle+angleRise)*extendFactor+toFNode.loc.getY();
        Point2D cp2 = new Point2D(xHandleR,yHandleR);

        double xArrow = (xHandleL+xHandleR)/2;
        double yArrow = (yHandleL+yHandleR)/2;
        Point2D arrowLoc = new Point2D(xArrow,yArrow);

        /*gc.setFill(Color.BLUE);
        gc.fillOval(xStart-5,yStart-5,10,10);
        gc.fillOval(xEnd-5,yEnd-5,10,10);
        gc.fillOval(xHandleL-5,yHandleL-5,10,10);
        gc.fillOval(xHandleR-5,yHandleR-5,10,10);*/

        FConnection fc = new JprFConnection(gc, startLoc,cp1,cp2, endLoc);
        fc.setArrowData(arrowLoc, angle+Math.PI);

        fromFNode.unidConnectionTo.add(toID);
        FConnectionMap.put(FConnIDGiver++, fc);

    }

    public void unidFConnection(int fromID, int toID) {
        FNode fromFNode = FNodeMap.get(fromID);
        FNode toFNode = FNodeMap.get(toID);

        if (fromFNode == null || toFNode == null) return;

        double distX = (fromFNode.loc.getX() - toFNode.loc.getX());
        double distY = (fromFNode.loc.getY() - toFNode.loc.getY());

        double angle = Math.atan2(distY, distX);

        double xStart = Math.cos(angle - Math.PI) * fromFNode.radius + fromFNode.loc.getX();
        double yStart = Math.sin(angle - Math.PI) * fromFNode.radius + fromFNode.loc.getY();
        Point2D startLoc = new Point2D(xStart, yStart);

        double xEnd = Math.cos(angle) * toFNode.radius + toFNode.loc.getX();
        double yEnd = Math.sin(angle) * toFNode.radius + toFNode.loc.getY();
        Point2D endLoc = new Point2D(xEnd, yEnd);

        double xArrow = (startLoc.getX() + endLoc.getX()) / 2;
        double yArrow = (startLoc.getY() + endLoc.getY()) / 2;
        Point2D arrowLoc = new Point2D(xArrow, yArrow);

        FConnection fc = new UnidFConnection(gc, startLoc, endLoc);
        fc.setArrowData(arrowLoc, angle + Math.PI);

        fromFNode.unidConnectionTo.add(toID);
        FConnectionMap.put(FConnIDGiver++, fc);
    }

    public void bidFConnection(int fromID, int toID) {
        FNode fromFNode = FNodeMap.get(fromID);
        FNode toFNode = FNodeMap.get(toID);

        if (fromFNode == null || toFNode == null) return;

        double distX = (fromFNode.loc.getX() - toFNode.loc.getX());
        double distY = (fromFNode.loc.getY() - toFNode.loc.getY());

        double angleBase = Math.atan2(distY, distX);
        double angleApproach = Math.PI / 8;

        //first arrow

        double xStart = Math.cos(angleBase - Math.PI - angleApproach) * fromFNode.radius + fromFNode.loc.getX();
        double yStart = Math.sin(angleBase - Math.PI - angleApproach) * fromFNode.radius + fromFNode.loc.getY();
        Point2D startLoc = new Point2D(xStart, yStart);

        double xEnd = Math.cos(angleBase + angleApproach) * toFNode.radius + toFNode.loc.getX();
        double yEnd = Math.sin(angleBase + angleApproach) * toFNode.radius + toFNode.loc.getY();
        Point2D endLoc = new Point2D(xEnd, yEnd);

        double xArrow = (startLoc.getX() + endLoc.getX()) / 2;
        double yArrow = (startLoc.getY() + endLoc.getY()) / 2;
        Point2D arrowLoc = new Point2D(xArrow, yArrow);

        //second arrow

        double xStart2 = Math.cos(angleBase - Math.PI + angleApproach) * fromFNode.radius + fromFNode.loc.getX();
        double yStart2 = Math.sin(angleBase - Math.PI + angleApproach) * fromFNode.radius + fromFNode.loc.getY();
        Point2D startLoc2 = new Point2D(xStart2, yStart2);

        double xEnd2 = Math.cos(angleBase - angleApproach) * toFNode.radius + toFNode.loc.getX();
        double yEnd2 = Math.sin(angleBase - angleApproach) * toFNode.radius + toFNode.loc.getY();
        Point2D endLoc2 = new Point2D(xEnd2, yEnd2);

        double xArrow2 = (startLoc2.getX() + endLoc2.getX()) / 2;
        double yArrow2 = (startLoc2.getY() + endLoc2.getY()) / 2;
        Point2D arrowLoc2 = new Point2D(xArrow2, yArrow2);

        FConnection fc = new BidFConnection(gc, startLoc, endLoc, startLoc2, endLoc2);
        fc.setArrowData2(arrowLoc, angleBase, arrowLoc2, angleBase - Math.PI);

        fromFNode.bidConnectsWith.add(toID);
        toFNode.bidConnectsWith.add(fromID);

        FConnectionMap.put(FConnIDGiver++, fc);
    }

    public void selfFConnection(int ID) {
        FNode fn = FNodeMap.get(ID);

        if (fn == null) return;

        double angleApproach = Math.PI / 6;

        double xBaseL = Math.cos(angleApproach - Math.PI / 2) * fn.radius + fn.loc.getX();
        double yBaseL = Math.sin(angleApproach - Math.PI / 2) * fn.radius + fn.loc.getY();
        Point2D startLoc = new Point2D(xBaseL, yBaseL);

        double xBaseR = Math.cos(-angleApproach - Math.PI / 2) * fn.radius + fn.loc.getX();
        double yBaseR = Math.sin(-angleApproach - Math.PI / 2) * fn.radius + fn.loc.getY();
        Point2D endLoc = new Point2D(xBaseR, yBaseR);

        double xCp1 = fn.loc.getX() + fn.radius * 1.8;
        double yCp1 = fn.loc.getY() - fn.radius * 2.8;
        Point2D cp1 = new Point2D(xCp1, yCp1);

        double xCp2 = fn.loc.getX() - fn.radius * 1.8;
        double yCp2 = fn.loc.getY() - fn.radius * 2.8;
        Point2D cp2 = new Point2D(xCp2, yCp2);

        double xArrow = Math.cos(-angleApproach - Math.PI / 2) * 10 + xBaseR;
        double yArrow = Math.sin(-angleApproach - Math.PI / 2) * 10 + yBaseR;
        Point2D locArrow = new Point2D(xArrow, yArrow);

        //CONTROL POINTS DEBUG
        /*gc.setFill(Color.BLUE);
        gc.fillOval(xBaseL-5,yBaseL-5,10,10);
        gc.fillOval(xBaseR-5,yBaseR-5,10,10);
        gc.fillOval(xCp1-5,yCp1-5,10,10);
        gc.fillOval(xCp2-5,yCp2-5,10,10);*/

        FConnection fc = new SelfFConnection(gc, startLoc, cp1, cp2, endLoc);
        fn.selfConnects = true;
        fc.setArrowData(locArrow, -angleApproach + Math.PI / 2);
        FConnectionMap.put(FConnIDGiver++, fc);

    }


    public void display() {

        //clear last screen
        Canvas canvas = gc.getCanvas();
        // gc.setFill(clearScreenColor);
        // gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        //draw connections
        for (FConnection fc : FConnectionMap.values())
            fc.draw();

        //draw nodes
        for (FNode fn : FNodeMap.values())
            fn.draw();
    }
}
