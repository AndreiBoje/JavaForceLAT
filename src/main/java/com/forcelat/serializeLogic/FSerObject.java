package com.forcelat.serializeLogic;

import java.io.Serializable;
import java.util.TreeMap;

//"Store" class
public class FSerObject implements Serializable {
    String textData;
    int IDGiver;
    TreeMap<Integer, FPoint2D> FNodeMapLoc = new TreeMap<>();
    int canvasW,canvasH;
}
