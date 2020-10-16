package com.forcelat.serializeLogic;

import com.forcelat.drawingLogic.FNode;
import com.forcelat.drawingLogic.FNodeManager;
import com.forcelat.uiLogic.imageSizeUI;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Point2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.TextArea;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.io.*;

public class FSerializer {

    static File saveFile = null;

    public static boolean serialize(String textData, FNodeManager fnm, File projPathFile) {

        if (saveFile == null) {
            final FileChooser fc = new FileChooser();
            fc.setInitialDirectory(new File(projPathFile.getAbsolutePath()));
            fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("FLAT", "*.flat"));
            saveFile = fc.showSaveDialog(fnm.gc.getCanvas().getScene().getWindow());
            if (saveFile == null)
                return false;
        }

        FSerObject fobj = new FSerObject();
        fobj.textData = textData;
        fobj.IDGiver = 0;
        int i = 0;
        for (FNode fn : fnm.FNodeMap.values()) {
            fobj.FNodeMapLoc.put(i, new FPoint2D(fn.loc.getX(), fn.loc.getY()));
            i++;
        }
        fobj.canvasH = (int) fnm.gc.getCanvas().getHeight();
        fobj.canvasW = (int) fnm.gc.getCanvas().getWidth();

        try {
            FileOutputStream fos = new FileOutputStream(saveFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(fobj);
            oos.close();
            fos.close();
            return true;
        } catch (IOException ignored) {
            ignored.printStackTrace();
            return false;
        }
    }

    public static boolean deserialize(TextArea ta, FNodeManager fnm, File projPathFile) {

        final FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(projPathFile.getAbsolutePath()));
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("FLAT", "*.flat"));
        saveFile = fc.showOpenDialog(fnm.gc.getCanvas().getScene().getWindow());
        if (saveFile == null)
            return false;

        try {
            FileInputStream fis = new FileInputStream(saveFile);
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
            fnm.display();
            return true;

        } catch (IOException | ClassNotFoundException ignored) {
            ignored.printStackTrace();
            return false;
        }
    }

    public static void exportImage(FNodeManager fnm, File projPathFile) {
        final FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(projPathFile.getAbsolutePath()));
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
        fc.getExtensionFilters().add(extFilter);
        File imgSaveFile = fc.showSaveDialog(fnm.gc.getCanvas().getScene().getWindow());
        WritableImage image = fnm.gc.getCanvas().snapshot(new SnapshotParameters(), null);
        try {
            if (imgSaveFile != null)
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", imgSaveFile);

        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
    }
}
