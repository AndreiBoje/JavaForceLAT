package com.forcelat;

import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class FParser {

    private final TextArea ta;
    private final FNodeManager fnm;

    public FParser(TextArea ta, FNodeManager fnm) {
        this.ta = ta;
        this.fnm = fnm;
    }

    public void beginFParse() {
        fnm.initInteractivity();
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
                        genericFNodeOptionsParser(lineData, opts);

                        fnm.addFNode(xPos, yPos, fName, opts);
                    }
                }
                //parse flow command
                if (lineData.get(0).equals("flow")) {
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
                        //generic options parser,pass opts by ref
                        ArrayList<String> checkForTokens = new ArrayList<>();
                        String[] strings = {"-arrow","-color", "-tangle", "-tsize", "-tflip", "-theight", "-lwidth"};

                        checkForTokens.addAll(Arrays.asList(strings));
                        genericFConFOptionsParser(lineData, opts, checkForTokens);

                        for (int i = 0; i < argsNo - 1; i++)
                            if (conTexts.size() != 0)
                                fnm.unidFConnection(IDs.get(i), IDs.get(i + 1), conTexts.get(i), opts);
                            else
                                fnm.unidFConnection(IDs.get(i), IDs.get(i + 1), " ", opts);

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
                        ArrayList<String> checkForTokens = new ArrayList<>();
                        String[] strings = {"-arrow","-color", "-tangle", "-tsize", "-tflip", "-theight", "-lwidth"};

                        checkForTokens.addAll(Arrays.asList(strings));
                        genericFConFOptionsParser(lineData, opts, checkForTokens);

                        //UCON SPECIFIC -flip
                        if (lineData.contains("-flip")) {
                            for (int i = 0; i < argsNo - 1; i++)
                                if (conTexts.size() != 0)
                                    fnm.unidFConnection(IDs.get(i + 1), IDs.get(0), conTexts.get(i), opts);
                                else
                                    fnm.unidFConnection(IDs.get(i + 1), IDs.get(0), " ", opts);
                        } else {
                            for (int i = 0; i < argsNo - 1; i++)
                                if (conTexts.size() != 0)
                                    fnm.unidFConnection(IDs.get(0), IDs.get(i + 1), conTexts.get(i), opts);
                                else
                                    fnm.unidFConnection(IDs.get(0), IDs.get(i + 1), " ", opts);
                        }
                    }
                }
                //parse bcon command
                if (lineData.get(0).equals("bcon")) {
                    //check for args
                    if (lineData.contains("-nodes")) {
                        FOptions opts = new FOptions();
                        //fnodes
                        int argsNo = -1;
                        int namesIndex = lineData.indexOf("-nodes") + 1;
                        ArrayList<Integer> IDs = new ArrayList<>();
                        ArrayList<String> texts = new ArrayList<>();
                        try {
                            argsNo = lineData.get(namesIndex).split(",").length;

                            if (argsNo == 2)
                                for (int i = 0; i < argsNo; i++)
                                    IDs.add(fnm.getFNodeIDByTxt(lineData.get(namesIndex).split(",")[i]));

                        } catch (IndexOutOfBoundsException e) {
                        }
                        //ftext
                        int textsIndex = -1;
                        if (lineData.contains("-text")) {
                            textsIndex = lineData.indexOf("-text") + 1;
                            texts.add(lineData.get(textsIndex));
                            texts.add(lineData.get(textsIndex + 1));
                        }

                        //OPTIONS START HERE!!!
                        ArrayList<String> checkForTokens = new ArrayList<>();
                        String[] strings = {"-arrow","-color", "-tangle", "-tsize", "-tflip", "-theight", "-lwidth"};

                        checkForTokens.addAll(Arrays.asList(strings));
                        genericFConFOptionsParser(lineData, opts, checkForTokens);

                        if (texts.size() != 0)
                            fnm.bidFConnection(IDs.get(0), IDs.get(1), texts.get(0), texts.get(1), opts);
                        else
                            fnm.bidFConnection(IDs.get(0), IDs.get(1), " ", " ", opts);
                    }
                }
                //parse jpr command
                if (lineData.get(0).equals("jpr")) {
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
                        ArrayList<String> checkForTokens = new ArrayList<>();
                        String[] strings = {"-arrow","-color", "-tangle", "-rangle", "-extf", "-tsize", "-tflip", "-theight", "-lwidth"};

                        checkForTokens.addAll(Arrays.asList(strings));
                        genericFConFOptionsParser(lineData, opts, checkForTokens);

                        for (int i = 0; i < argsNo - 1; i++)
                            if (conTexts.size() != 0)
                                fnm.jprFConnection(IDs.get(0), IDs.get(i + 1), conTexts.get(i), opts);
                            else
                                fnm.jprFConnection(IDs.get(0), IDs.get(i + 1), " ", opts);
                    }
                }
                //parse self command
                if (lineData.get(0).equals("self")) {
                    //check for args
                    if (lineData.contains("-node")) {
                        FOptions opts = new FOptions();
                        int nodeIndex = lineData.indexOf("-node") + 1;
                        int txtIndex = lineData.indexOf("-text") + 1;

                        //parse args
                        int ID = fnm.getFNodeIDByTxt(lineData.get(nodeIndex));
                        String txt = "";

                        if (lineData.contains("-text"))
                            txt = lineData.get(txtIndex);

                        ArrayList<String> checkForTokens = new ArrayList<>();
                        String[] strings = {"-color", "-angle", "-tangle", "-tsize", "-tflip", "-theight", "-lwidth"};

                        checkForTokens.addAll(Arrays.asList(strings));
                        genericFConFOptionsParser(lineData, opts, checkForTokens);

                        fnm.selfFConnection(ID, txt, opts);
                    }
                }
                //parse start command
                if (lineData.get(0).equals("start")) {
                    if (lineData.contains("-node")) {
                        FOptions opts = new FOptions();

                        int nodesIndex = lineData.indexOf("-node") + 1;

                        ArrayList<String> checkForTokens = new ArrayList<>();
                        String[] strings = {"-length", "-angle", "-lwidth"};

                        checkForTokens.addAll(Arrays.asList(strings));
                        genericFConFOptionsParser(lineData, opts, checkForTokens);
                        fnm.makeStart(lineData.get(nodesIndex), opts);

                    }
                }
                //parse final command
                if (lineData.get(0).equals("final")) {
                    if (lineData.contains("-nodes")) {
                        FOptions opts = new FOptions();

                        int nodesIndex = lineData.indexOf("-nodes") + 1;
                        ArrayList<String> nodes = new ArrayList<>();
                        int argsNo = -1;
                        try {
                            argsNo = lineData.get(nodesIndex).split(",").length;
                        } catch (IndexOutOfBoundsException e) {
                        }

                        for (int i = 0; i < argsNo; i++)
                            fnm.makeFinal(lineData.get(nodesIndex).split(",")[i]);
                    }
                }
            }
        } catch (IndexOutOfBoundsException e) {
            //   System.out.println("ERROR: There's been an indexing error! Are there enough arguments?");
        }
        fnm.display();
    }

    public void genericFNodeOptionsParser(ArrayList<String> lineData, FOptions opts) {
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

        //fnode width
        double fNodeWidth = opts.fNodeWidth;
        if (lineData.contains("-width")) {
            int fNodeWidthIndex = lineData.indexOf("-width") + 1;
            try {
                fNodeWidth = Double.parseDouble(lineData.get(fNodeWidthIndex));
            } catch (NumberFormatException e) {
            }
        }

        opts.fNodeWidth = fNodeWidth;
        opts.fNodeAlias = fAlias;
        opts.fNodeRadius = fRadius;
        opts.fNodeColor = fColor;
        opts.fNodeTextSize = fNodeTextSize;

    }

    public void genericFConFOptionsParser(ArrayList<String> lineData, FOptions opts, ArrayList<String> scanForTokens) {

        //fCon start node length
        double fConStartLength = opts.fConStartLength;
        if (scanForTokens.contains("-length") && lineData.contains("-length")) {
            int startLengthIndex = lineData.indexOf("-length") + 1;
            try {
                fConStartLength = Double.parseDouble(lineData.get(startLengthIndex));
            } catch (NumberFormatException e) {
            }
        }

        //fCon start node length
        double fConStartAngle = opts.fConStartAngle;
        if (scanForTokens.contains("-angle") && lineData.contains("-angle")) {
            int startAngleIndex = lineData.indexOf("-angle") + 1;
            try {
                fConStartAngle = Double.parseDouble(lineData.get(startAngleIndex));
            } catch (NumberFormatException e) {
            }
        }

        //fCon start node length
        double fConSelfAngle = opts.fConSelfAngle;
        if (scanForTokens.contains("-angle") && lineData.contains("-angle")) {
            int selfAngleIndex = lineData.indexOf("-angle") + 1;
            try {
                fConSelfAngle = Double.parseDouble(lineData.get(selfAngleIndex));
            } catch (NumberFormatException e) {
            }
        }

        //fCon rise angle
        double fConRiseAngle = opts.fConRiseAngle;
        if (scanForTokens.contains("-rangle") && lineData.contains("-rangle")) {
            int riseAngleIndex = lineData.indexOf("-rangle") + 1;
            try {
                fConRiseAngle = Double.parseDouble(lineData.get(riseAngleIndex));
            } catch (NumberFormatException e) {
            }
        }
        //fCon extend factor
        double fConExtendFactor = opts.fConExtendFactor;
        if (scanForTokens.contains("-extf") && lineData.contains("-extf")) {
            int extIndex = lineData.indexOf("-extf") + 1;
            try {
                fConExtendFactor = Double.parseDouble(lineData.get(extIndex));
            } catch (NumberFormatException e) {
            }
        }

        //fConnnectionColor
        Color fConColor = opts.fConColor;
        if (scanForTokens.contains("-color") && lineData.contains("-color")) {
            int colorIndex = lineData.indexOf("-color") + 1;
            try {
                fConColor = Color.web(lineData.get(colorIndex));
            } catch (IllegalArgumentException e) {
            }
        }

        //fConTextAngle
        double fConTextAngle = opts.fConTextAngle;
        if (scanForTokens.contains("-tangle") && lineData.contains("-tangle")) {
            int textAngleIndex = lineData.indexOf("-tangle") + 1;
            try {
                fConTextAngle = Double.parseDouble(lineData.get(textAngleIndex));
            } catch (NumberFormatException e) {
            }
        }

        //fConTextSize
        double fConTextSize = opts.fConTextSize;
        if (scanForTokens.contains("-tsize") && lineData.contains("-tsize")) {
            int textSizeIndex = lineData.indexOf("-tsize") + 1;
            try {
                fConTextSize = Double.parseDouble(lineData.get(textSizeIndex));
            } catch (NumberFormatException e) {
            }
        }

        //fConFlipText
        boolean fConFlipText = opts.fConFlipText;
        if (scanForTokens.contains("-tflip") && lineData.contains("-tflip"))
            fConFlipText = true;

        //fConTextHeight
        double fConTextHeight = opts.fConTextHeight;
        if (scanForTokens.contains("-theight") && lineData.contains("-theight")) {
            int textHeightIndex = lineData.indexOf("-theight") + 1;
            try {
                fConTextHeight = Double.parseDouble(lineData.get(textHeightIndex));
            } catch (NumberFormatException e) {
            }
        }

        //fConLineWidth
        double fConLineWidth = opts.fConLineWidth;
        if (scanForTokens.contains("-lwidth") && lineData.contains("-lwidth")) {
            int lineWidthIndex = lineData.indexOf("-lwidth") + 1;
            try {
                fConLineWidth = Double.parseDouble(lineData.get(lineWidthIndex));
            } catch (NumberFormatException e) {
            }
        }

        //fConHasArrow
        boolean fConHasArrow = opts.fConHasArrow;
        if (scanForTokens.contains("-arrow") && lineData.contains("-arrow"))
            fConHasArrow = true;

        opts.fConHasArrow = fConHasArrow;
        opts.fConSelfAngle = fConSelfAngle;
        opts.fConStartAngle = fConStartAngle;
        opts.fConStartLength = fConStartLength;
        opts.fConLineWidth = fConLineWidth;
        opts.fConTextHeight = fConTextHeight;
        opts.fConFlipText = fConFlipText;
        opts.fConColor = fConColor;
        opts.fConTextAngle = fConTextAngle;
        opts.fConTextSize = fConTextSize;
        opts.fConExtendFactor = fConExtendFactor;
        opts.fConRiseAngle = fConRiseAngle;
    }
}
