package com.forcelat;

import javafx.geometry.Point2D;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.*;
import java.util.TreeMap;

class FPoint2D implements Serializable {
    double x, y;

    public FPoint2D(double x, double y) {
        this.x = x;
        this.y = y;
    }
}

class FSerObject implements Serializable {
    String textData;
    int IDGiver;
    TreeMap<Integer, FPoint2D> FNodeMapLoc = new TreeMap<>();

}

public class FSerializer {

    public void serialize(String textData, FNodeManager fnm,File saveDest) {
        FSerObject fobj = new FSerObject();
        fobj.textData = textData;
        fobj.IDGiver = 0;
        for (int ID : fnm.FNodeMap.keySet())
            fobj.FNodeMapLoc.put(ID, new FPoint2D(fnm.FNodeMap.get(ID).loc.getX(), fnm.FNodeMap.get(ID).loc.getY()));

        try {
            FileOutputStream fos = new FileOutputStream(saveDest);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(fobj);
            oos.close();
            fos.close();
        } catch (IOException ignored) {
            ignored.printStackTrace();
        }
    }

    public void deserialize(TextArea ta, FNodeManager fnm,File openFile) {

        try {
            FileInputStream fis = new FileInputStream(openFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            FSerObject fobj = (FSerObject) ois.readObject();

            ta.clear();
            fnm.FNodeMap.clear();
            fnm.FNodeIDGiver = fobj.IDGiver;
            ta.setText(fobj.textData);

            for (int ID : fobj.FNodeMapLoc.keySet())
                fnm.FNodeMap.get(ID).loc = new Point2D(fobj.FNodeMapLoc.get(ID).x, fobj.FNodeMapLoc.get(ID).y);

        } catch (IOException | ClassNotFoundException ignored) {
            ignored.printStackTrace();
        }
    }
}
