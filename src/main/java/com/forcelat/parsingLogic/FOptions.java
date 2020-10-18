package com.forcelat.parsingLogic;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

import java.io.Serializable;

//Options and defaults for FNodes //No required params are here
public class FOptions implements Serializable {
    //FNodes
    public String fNodeAlias=null;
    public double fNodeRadius = 30;
    public double fNodeSmallRadius = 0.8 * fNodeRadius;
    public Color fNodeColor = Color.BLACK;
    public double fNodeTextSize = 30;
    public double fNodeWidth = 3;
    public boolean fNodeShowText = true;
    public boolean fNodeFill = false;
    public Point2D defPos= new Point2D(90,90);
    //FConnections
    public Color fConColor = Color.BLACK;
    public double fConTextAngle = 0;
    public double fConTextSize = 20;
    public boolean fConFlipText = false;
    public double fConTextHeight = 20;
    public double fConSelfTextHeight = 0;
    public double fConLineWidth = 3;
    public double fConRiseAngle = 45; //degrees
    public double fConExtendFactor = 100;
    public double fConStartLength = 50;
    public double fConStartAngle = 180;
    public double fConSelfAngle = 90;
    public boolean fConHasArrow = false;
}
