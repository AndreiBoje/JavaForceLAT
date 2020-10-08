package com.forcelat;

import javafx.scene.paint.Color;

//Options and defaults for FNodes //No required params are here
public class FOptions {
    //FNodes
    public double fNodeRadius = 30;
    public double fNodeSmallRadius = 0.8 * fNodeRadius;
    public Color fNodeColor = Color.BLACK;
    public double fNodeTextSize = 30;
    public double fNodeWidth = 3;
    public boolean fNodeStatic = false;
    //FConnections
    public Color fConColor = Color.BLACK;
    public double fConTextAngle = 0;
    public double fConTextSize = 20;
    public boolean fConFlipText = false;
    public double fConTextHeight = 20;
    public double fConLineWidth = 3;
    public double fConRiseAngle = 45; //degrees
    public double fConExtendFactor = 100;
    public double fConStartLength = 50;
    public double fConStartAngle = 180;
    public double fConSelfAngle = 90;
    public boolean fConHasArrow = false;
}
