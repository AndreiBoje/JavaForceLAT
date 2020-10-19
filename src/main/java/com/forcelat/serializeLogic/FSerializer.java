package com.forcelat.serializeLogic;

import com.forcelat.drawingLogic.FNode;
import com.forcelat.drawingLogic.FNodeManager;
import com.forcelat.parsingLogic.FOptions;
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

    public static File saveFile = null;

    public static void setLastProjectPath(String projPath) {
        try {
            File file = new File("config.txt");
            if (file.createNewFile()) {
                FileWriter fw = new FileWriter(file);
                fw.write(projPath);
                fw.close();
            }
        } catch (IOException e) {
        }
    }
    public static File getLastProjectPath(){
        try{
            File file = new File("config.txt");
            BufferedReader fr = new BufferedReader(new FileReader(file));
            return new File(fr.readLine());
        }catch(Exception e){
            return new File(System.getProperty("user.home"));
        }
    }

    public static String serializeAs(TextArea mainTa, FNodeManager fnm, File projPathFile) {

        final FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(projPathFile.getAbsolutePath()));
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("FLAT", "*.flat"));
        saveFile = fc.showSaveDialog(fnm.gc.getCanvas().getScene().getWindow());
        if (saveFile == null)
            return null;

        FSerObject fobj = new FSerObject();
        fobj.textData = mainTa.getText();
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
            return saveFile.getName();
        } catch (IOException ignored) {
            ignored.printStackTrace();
            return null;
        }
    }

    public static String serialize(TextArea mainTa, FNodeManager fnm, File projPathFile) {

        if (saveFile == null)
            return null;

        FSerObject fobj = new FSerObject();
        fobj.textData = mainTa.getText();
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
            return saveFile.getName();
        } catch (IOException ignored) {
            ignored.printStackTrace();
            return null;
        }
    }

    public static String deserialize(TextArea ta, FNodeManager fnm, File projPathFile) {
        final FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(projPathFile.getAbsolutePath()));
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("FLAT", "*.flat"));
        saveFile = fc.showOpenDialog(fnm.gc.getCanvas().getScene().getWindow());
        if (saveFile == null)
            return null;

        try {
            FileInputStream fis = new FileInputStream(saveFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            FSerObject fobj = (FSerObject) ois.readObject();

            ta.clear();
            fnm.FNodeMap.clear();
            fnm.FNodeIDGiver = fobj.IDGiver;
            fnm.gc.getCanvas().setHeight(fobj.canvasH);
            fnm.gc.getCanvas().setWidth(fobj.canvasW);

            for (Integer ID : fobj.FNodeMapLoc.keySet()) {
                FOptions opts = new FOptions();
                fnm.addFNode(fobj.FNodeMapLoc.get(ID).x, fobj.FNodeMapLoc.get(ID).y, ID.toString(), opts);
            }
            ta.setText(fobj.textData);
            fnm.display();
            return saveFile.getName();

        } catch (IOException | ClassNotFoundException ignored) {
            ignored.printStackTrace();
            return null;
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
