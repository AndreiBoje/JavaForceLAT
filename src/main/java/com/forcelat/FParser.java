package com.forcelat;

import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class FParser {

    //TODO: Operator tokens
    //ucon
    //bcon
    //scon
    //jcon
    //add
    //everything else is invalid
    //TODO: General syntax in use
    //add -id 0 -pos 200,200 -txt "This is text"           <- obligatory for now (and only ones)
    //ucon -id 0,1 -txt "This is text"                     <- idem
    //bcon -id 0,1 -txt "This is toTxt" "This is fromTxt"  <- idem
    //scon -id 0 -txt "This is text"                       <- idem
    //jcon -id 0,1 -txt "This is text"                     <- idem
    private final TextArea ta;
    private final FNodeManager fnm;

    public FParser(TextArea ta, FNodeManager fnm) {
        this.ta = ta;
        this.fnm = fnm;
    }

    public void beginFParse() {
        //fnm.initInteractivity();
        ta.setOnKeyTyped(e -> loopFParse());
    }

    private void loopFParse() throws IndexOutOfBoundsException {

        ArrayList<String> rawLines = (ArrayList<String>) Arrays.stream(ta.getText().split("\n")).collect(Collectors.toList());

        fnm.clear();

        try {
            for (String rawLine : rawLines) {
                ArrayList<String> lineData = (ArrayList<String>) Arrays.stream(rawLine.split(" ")).collect(Collectors.toList());
                //parse add command
                if (lineData.get(0).equals("add")) {
                    //check for args
                    if (lineData.contains("-name") && lineData.contains("-pos")) {
                        FOptions opts = new FOptions();

                        int nameIndex = lineData.indexOf("-name") + 1;
                        int posIndex = lineData.indexOf("-pos") + 1;

                        //fnode name
                        String fName = lineData.get(nameIndex);

                        //fnode pos
                        double xPos = -1, yPos = -1;
                        try {
                            String[] data = lineData.get(posIndex).split(",");
                            if (data.length == 2) {
                                xPos = Double.parseDouble(data[0]);
                                yPos = Double.parseDouble(data[1]);
                            }
                        } catch (NumberFormatException e) {
                            xPos = -1;
                            yPos = -1;
                        }

                        //OPTIONS START HERE!!!!
                        //fnode alias
                        String fAlias = opts.fNodeAlias;
                        if (lineData.contains("-alias")) {
                            int aliasIndex = lineData.indexOf("-alias") + 1;
                            fAlias = lineData.get(aliasIndex);
                        }
                        //fnode radius
                        double fRadius = opts.fNodeRadius;
                        if (lineData.contains("-radius")) {
                            int radiusIndex = lineData.indexOf("-radius") + 1;
                            try {
                                fRadius = Double.parseDouble(lineData.get(radiusIndex));
                            } catch (NumberFormatException e) {
                            }
                        }
                        //fnode color
                        Color fColor = opts.fNodeColor;
                        if (lineData.contains("-color")) {
                            int colorIndex = lineData.indexOf("-color") + 1;
                            try {
                                fColor = Color.web(lineData.get(colorIndex));
                            } catch (IllegalArgumentException e) {
                            }
                        }
                        //fnode fontSize
                        double fNodeTextSize = opts.fNodeTextSize;
                        if (lineData.contains("-tsize")) {
                            int textSizeIndex = lineData.indexOf("-tsize") + 1;
                            try {
                                fNodeTextSize = Double.parseDouble(lineData.get(textSizeIndex));
                            } catch (NumberFormatException e) {
                            }
                        }
                        opts.fNodeAlias = fAlias;
                        opts.fNodeRadius = fRadius;
                        opts.fNodeColor = fColor;
                        opts.fNodeTextSize = fNodeTextSize;

                        fnm.addFNode(xPos, yPos, fName, opts);
                    }
                }
                //parse flow command
                if (lineData.get(0).equals("flow")) {
                    //check for args
                    if (lineData.contains("-nodes")) {
                        FOptions opts = new FOptions();
                        int nodesIndex = lineData.indexOf("-nodes") + 1;
                        int txtIndex = lineData.indexOf("-text") + 1;

                        int argsNo = -1;
                        ArrayList<Integer> IDs = new ArrayList<>();
                        ArrayList<String> txts = new ArrayList<>();
                        try {
                            argsNo = lineData.get(nodesIndex).split(",").length;

                            if (argsNo >= 2)
                                for (int i = 0; i < argsNo; i++)
                                    IDs.add(fnm.getFNodeIDByTxt(lineData.get(nodesIndex).split(",")[i]));

                        } catch (IndexOutOfBoundsException e) {
                        }

                        if (lineData.contains("-text")) {
                            for (int i = 0; i < argsNo - 1; i++)
                                txts.add(lineData.get(txtIndex + i));
                        }

                        for (int i = 0; i < argsNo - 1; i++)
                            if (txts.size() != 0)
                                fnm.unidFConnection(IDs.get(i), IDs.get(i + 1), txts.get(i),opts);
                            else
                                fnm.unidFConnection(IDs.get(i), IDs.get(i + 1), " ",opts);

                    }

                }
                //parse ucon command
                if (lineData.get(0).equals("ucon")) {
                    //check for args
                    if (lineData.contains("-nodes")) {
                        FOptions opts = new FOptions();
                        //fnodes
                        int namesIndex = lineData.indexOf("-nodes") + 1;
                        int argsNo = -1;
                        ArrayList<Integer> IDs = new ArrayList<>();
                        try {
                            String[] names = lineData.get(namesIndex).split(",");
                            argsNo = names.length;
                            if (argsNo >= 2)
                                for (int i = 0; i < argsNo; i++)
                                    IDs.add(fnm.getFNodeIDByTxt(names[i]));
                        } catch (IndexOutOfBoundsException e) {
                        }

                        //ftexts (semi required cus of the code's structure)
                        int textsIndex = lineData.indexOf("-text") + 1;
                        ArrayList<String> conTexts = new ArrayList<>();
                        if (lineData.contains("-text")) {
                            for (int i = 0; i < argsNo - 1; i++)
                                conTexts.add(lineData.get(textsIndex + i));
                        }
                        //OPTIONS START HERE!!!
                        //fConnnectionColor
                        Color fConColor = opts.fConColor;
                        if(lineData.contains("-color")){
                            int colorIndex = lineData.indexOf("-color") + 1;
                            try {
                                fConColor = Color.web(lineData.get(colorIndex));
                            } catch (IllegalArgumentException e) {
                            }
                        }

                        //fConTextAngle
                        double fConTextAngle = opts.fConTextAngle;
                        if (lineData.contains("-tangle")) {
                            int textAngleIndex = lineData.indexOf("-tangle") + 1;
                            try {
                                fConTextAngle = Double.parseDouble(lineData.get(textAngleIndex));
                            } catch (NumberFormatException e) {
                            }
                        }

                        //fConTextSize
                        double fConTextSize = opts.fConTextSize;
                        if (lineData.contains("-tsize")) {
                            int textSizeIndex = lineData.indexOf("-tsize") + 1;
                            try {
                                fConTextSize = Double.parseDouble(lineData.get(textSizeIndex));
                            } catch (NumberFormatException e) {
                            }
                        }
                        //fConFlipText
                        boolean fConFlipText = opts.fConFlipText;
                        if(lineData.contains("-tflip"))
                            fConFlipText = true;

                        opts.fConFlipText = fConFlipText;
                        opts.fConColor = fConColor;
                        opts.fConTextAngle = fConTextAngle;
                        opts.fConTextSize = fConTextSize;

                        for (int i = 0; i < argsNo - 1; i++)
                            if (conTexts.size() != 0)
                                fnm.unidFConnection(IDs.get(0), IDs.get(i + 1), conTexts.get(i),opts);
                            else
                                fnm.unidFConnection(IDs.get(0), IDs.get(i + 1), " ",opts);
                    }
                }
                //parse bcon command
                if (lineData.get(0).equals("bcon")) {
                    //check for args
                    if (lineData.contains("-nodes")) {
                        int nodesIndex = lineData.indexOf("-nodes") + 1;
                        int txtIndex = lineData.indexOf("-text") + 1;

                        int argsNo = -1;
                        ArrayList<Integer> IDs = new ArrayList<>();
                        ArrayList<String> txts = new ArrayList<>();
                        try {
                            argsNo = lineData.get(nodesIndex).split(",").length;

                            if (argsNo == 2)
                                for (int i = 0; i < argsNo; i++)
                                    IDs.add(fnm.getFNodeIDByTxt(lineData.get(nodesIndex).split(",")[i]));

                        } catch (IndexOutOfBoundsException e) {
                        }

                        if (lineData.contains("-text")) {
                            txts.add(lineData.get(txtIndex));
                            txts.add(lineData.get(txtIndex + 1));
                        }

                        if (txts.size() != 0)
                            fnm.bidFConnection(IDs.get(0), IDs.get(1), txts.get(0), txts.get(1));
                        else
                            fnm.bidFConnection(IDs.get(0), IDs.get(1), " ", " ");
                    }
                }
                //parse jpr command
                if (lineData.get(0).equals("jpr")) {
                    if (lineData.contains("-nodes")) {
                        int nodesIndex = lineData.indexOf("-nodes") + 1;
                        int txtIndex = lineData.indexOf("-text") + 1;

                        int argsNo = -1;
                        ArrayList<Integer> IDs = new ArrayList<>();
                        ArrayList<String> txts = new ArrayList<>();
                        try {
                            argsNo = lineData.get(nodesIndex).split(",").length;

                            if (argsNo == 2)
                                for (int i = 0; i < argsNo; i++)
                                    IDs.add(fnm.getFNodeIDByTxt(lineData.get(nodesIndex).split(",")[i]));

                        } catch (IndexOutOfBoundsException e) {
                        }

                        if (lineData.contains("-text"))
                            txts.add(lineData.get(txtIndex));


                        if (txts.size() != 0)
                            fnm.jprFConnection(IDs.get(0), IDs.get(1), txts.get(0));
                        else
                            fnm.jprFConnection(IDs.get(0), IDs.get(1), " ");
                    }
                }
                //parse self command
                if (lineData.get(0).equals("self")) {
                    //check for args
                    if (lineData.contains("-node")) {
                        int nodeIndex = lineData.indexOf("-node") + 1;
                        int txtIndex = lineData.indexOf("-text") + 1;

                        //parse args
                        int ID = fnm.getFNodeIDByTxt(lineData.get(nodeIndex));
                        String txt = "";

                        if (lineData.contains("-text"))
                            txt = lineData.get(txtIndex);


                        fnm.selfFConnection(ID, txt);
                    }
                }
                //parse start command
                if (lineData.get(0).equals("start")) {
                    if (lineData.contains("-node")) {
                        int nodesIndex = lineData.indexOf("-node") + 1;

                        try {
                            fnm.makeStart(lineData.get(nodesIndex));

                        } catch (IndexOutOfBoundsException e) {
                        }
                    }
                }
                //parse final command
                if (lineData.get(0).equals("final")) {
                    if (lineData.contains("-nodes")) {
                        int nodesIndex = lineData.indexOf("-nodes") + 1;

                        try {
                            ArrayList<String> nodes = new ArrayList<>();
                            int argsNo = lineData.get(nodesIndex).split(",").length;

                            for (int i = 0; i < argsNo; i++)
                                fnm.makeFinal(lineData.get(nodesIndex).split(",")[i]);

                        } catch (IndexOutOfBoundsException e) {
                        }
                    }
                }
                //TODO: add alter defaults command
            }
        } catch (IndexOutOfBoundsException e) {
            //   System.out.println("ERROR: There's been an indexing error! Are there enough arguments?");
        }
        fnm.display();
    }
}
