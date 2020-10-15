package com.forcelat.serializeLogic;

import com.forcelat.drawingLogic.FNode;
import com.forcelat.drawingLogic.FNodeManager;
import javafx.geometry.Point2D;
import javafx.scene.control.TextArea;

import java.io.*;

//TODO: SERIALIZE CANVAS SIZE ALSO

public class FSerializer {

    public static void serialize(String textData, FNodeManager fnm, File saveDest) {
        FSerObject fobj = new FSerObject();
        fobj.textData = textData;
        fobj.IDGiver = 0;
        int i=0;
        for(FNode fn : fnm.FNodeMap.values()){
            fobj.FNodeMapLoc.put(i,new FPoint2D(fn.loc.getX(),fn.loc.getY()));
            i++;
        }
        fobj.canvasH = (int)fnm.gc.getCanvas().getHeight();
        fobj.canvasW = (int)fnm.gc.getCanvas().getWidth();

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

    public static void deserialize(TextArea ta, FNodeManager fnm ,File openFile) {

        try {
            FileInputStream fis = new FileInputStream(openFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            FSerObject fobj = (FSerObject) ois.readObject();

            ta.clear();
            fnm.FNodeMap.clear();
            fnm.FNodeIDGiver = fobj.IDGiver;
            ta.setText(fobj.textData);
            fnm.gc.getCanvas().setHeight(fobj.canvasH);
            fnm.gc.getCanvas().setWidth(fobj.canvasW);

            for (int ID : fobj.FNodeMapLoc.keySet())
                fnm.FNodeMap.get(ID).loc = new Point2D(fobj.FNodeMapLoc.get(ID).x, fobj.FNodeMapLoc.get(ID).y);

        } catch (IOException | ClassNotFoundException ignored) {
            ignored.printStackTrace();
        }
    }
    public static void saveImage(){
        //TODO:
    }
    public static void saveImageAs(){
        //TODO:
    }
}
