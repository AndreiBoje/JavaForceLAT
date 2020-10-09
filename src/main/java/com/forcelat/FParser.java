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

        try {
            for (String rawLine : rawLines) {
                ArrayList<String> lineData = (ArrayList<String>) Arrays.stream(rawLine.split(" ")).collect(Collectors.toList());

                //parse put command
                if (lineData.get(0).equals("put")) {
                    //check for args
                    if (lineData.contains("-nodes")) {
                        FOptions opts = new FOptions();

                        int nodesIndex = lineData.indexOf("-nodes") + 1;
                        int posIndex = lineData.indexOf("-pos") + 1;

                        //fnodes name

                        int argsNo = -1;
                        ArrayList<String> IDs = new ArrayList<>();
                        try {
                            String[] names = lineData.get(nodesIndex).split(",");
                            argsNo = names.length;
                            for (int i = 0; i < argsNo; i++)
                                IDs.add(names[i]);
                        } catch (IndexOutOfBoundsException e) {
                        }

                        //OPTIONS START HERE!!!!
                        genericFNodeOptionsParser(lineData, opts);

                        for (int i = 0; i < argsNo; i++)
                            fnm.putFNode(IDs.get(i), opts);
                    }
                }

            }
        } catch (IndexOutOfBoundsException ignored) { }

        //TODO: ability to not show the text -> fill node instead.
        //populate canvas with found nodes
        fnm.populateWithFNodes(90, 90);
        //clear prev frame conn data
        fnm.clear();

        //Connections command handler
        try {
            for (String rawLine : rawLines) {
                ArrayList<String> lineData = (ArrayList<String>) Arrays.stream(rawLine.split(" ")).collect(Collectors.toList());

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
                        String[] strings = {"-arrow", "-color", "-tangle", "-tsize", "-tflip", "-theight", "-lwidth"};

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
                        String[] strings = {"-arrow", "-color", "-tangle", "-tsize", "-tflip", "-theight", "-lwidth"};

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
                        String[] strings = {"-color", "-tangle", "-tsize", "-theight", "-lwidth"};

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
                        String[] strings = {"-arrow", "-color", "-tangle", "-rangle", "-extf", "-tsize", "-tflip", "-theight", "-lwidth"};

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
                        String[] strings = {"-color", "-length", "-angle", "-lwidth"};

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

        //fnode radius
        double fRadius = opts.fNodeRadius;
        try {
            if (lineData.contains("-radius")) {
                int radiusIndex = lineData.indexOf("-radius") + 1;
                try {
                    fRadius = Double.parseDouble(lineData.get(radiusIndex));
                } catch (NumberFormatException e) {
                }
            }
        } catch (Exception ignored) {

        }
        //fnode color
        Color fColor = opts.fNodeColor;
        try {
            if (lineData.contains("-color")) {
                int colorIndex = lineData.indexOf("-color") + 1;
                try {
                    fColor = Color.web(lineData.get(colorIndex));
                } catch (IllegalArgumentException e) {
                }
            }
        } catch (Exception e) {

        }
        //fnode fontSize
        double fNodeTextSize = opts.fNodeTextSize;
        try {
            if (lineData.contains("-tsize")) {
                int textSizeIndex = lineData.indexOf("-tsize") + 1;
                try {
                    fNodeTextSize = Double.parseDouble(lineData.get(textSizeIndex));
                } catch (NumberFormatException e) {
                }
            }
        } catch (Exception e) {

        }

        //fnode width
        double fNodeWidth = opts.fNodeWidth;
        try {
            if (lineData.contains("-width")) {
                int fNodeWidthIndex = lineData.indexOf("-width") + 1;
                try {
                    fNodeWidth = Double.parseDouble(lineData.get(fNodeWidthIndex));
                } catch (NumberFormatException e) {
                }
            }
        } catch (Exception e) {

        }
        //fnode show text
        boolean fNodeShowText = opts.fNodeShowText;
        try {
            if (lineData.contains("-notext"))
                fNodeShowText = false;
        } catch (Exception e) {
        }

        //fnode fill node
        boolean fNodeFill = opts.fNodeFill;
        try {
            if (lineData.contains("-fill"))
                fNodeFill = true;
        } catch (Exception e) {
        }
        opts.fNodeFill = fNodeFill;
        opts.fNodeShowText = fNodeShowText;
        opts.fNodeWidth = fNodeWidth;
        opts.fNodeRadius = fRadius;
        opts.fNodeColor = fColor;
        opts.fNodeTextSize = fNodeTextSize;

    }

    public void genericFConFOptionsParser(ArrayList<String> lineData, FOptions
            opts, ArrayList<String> scanForTokens) {

        //fCon start node length
        double fConStartLength = opts.fConStartLength;
        try {
            if (scanForTokens.contains("-length") && lineData.contains("-length")) {
                int startLengthIndex = lineData.indexOf("-length") + 1;
                try {
                    fConStartLength = Double.parseDouble(lineData.get(startLengthIndex));
                } catch (NumberFormatException e) {
                }
            }
        } catch (Exception e) {
        }

        //fCon start node length
        double fConStartAngle = opts.fConStartAngle;
        try {
            if (scanForTokens.contains("-angle") && lineData.contains("-angle")) {
                int startAngleIndex = lineData.indexOf("-angle") + 1;
                try {
                    fConStartAngle = Double.parseDouble(lineData.get(startAngleIndex));
                } catch (NumberFormatException e) {
                }
            }
        } catch (Exception e) {
        }

        //fCon start node length
        double fConSelfAngle = opts.fConSelfAngle;
        try {
            if (scanForTokens.contains("-angle") && lineData.contains("-angle")) {
                int selfAngleIndex = lineData.indexOf("-angle") + 1;
                try {
                    fConSelfAngle = Double.parseDouble(lineData.get(selfAngleIndex));
                } catch (NumberFormatException e) {
                }
            }
        } catch (Exception e) {
        }

        //fCon rise angle
        double fConRiseAngle = opts.fConRiseAngle;
        try {
            if (scanForTokens.contains("-rangle") && lineData.contains("-rangle")) {
                int riseAngleIndex = lineData.indexOf("-rangle") + 1;
                try {
                    fConRiseAngle = Double.parseDouble(lineData.get(riseAngleIndex));
                } catch (NumberFormatException e) {
                }
            }
        } catch (Exception e) {
        }
        //fCon extend factor
        double fConExtendFactor = opts.fConExtendFactor;
        try {
            if (scanForTokens.contains("-extf") && lineData.contains("-extf")) {
                int extIndex = lineData.indexOf("-extf") + 1;
                try {
                    fConExtendFactor = Double.parseDouble(lineData.get(extIndex));
                } catch (NumberFormatException e) {
                }
            }
        } catch (Exception e) {
        }

        //fConnnectionColor
        Color fConColor = opts.fConColor;
        try {
            if (scanForTokens.contains("-color") && lineData.contains("-color")) {
                int colorIndex = lineData.indexOf("-color") + 1;
                try {
                    fConColor = Color.web(lineData.get(colorIndex));
                } catch (IllegalArgumentException e) {
                }
            }
        } catch (Exception e) {
        }

        //fConTextAngle
        double fConTextAngle = opts.fConTextAngle;
        try {
            if (scanForTokens.contains("-tangle") && lineData.contains("-tangle")) {
                int textAngleIndex = lineData.indexOf("-tangle") + 1;
                try {
                    fConTextAngle = Double.parseDouble(lineData.get(textAngleIndex));
                } catch (NumberFormatException e) {
                }
            }
        } catch (Exception e) {

        }

        //fConTextSize
        double fConTextSize = opts.fConTextSize;
        try {
            if (scanForTokens.contains("-tsize") && lineData.contains("-tsize")) {
                int textSizeIndex = lineData.indexOf("-tsize") + 1;
                try {
                    fConTextSize = Double.parseDouble(lineData.get(textSizeIndex));
                } catch (NumberFormatException e) {
                }
            }
        } catch (Exception e) {

        }

        //fConFlipText
        boolean fConFlipText = opts.fConFlipText;
        try {
            if (scanForTokens.contains("-tflip") && lineData.contains("-tflip"))
                fConFlipText = true;
        } catch (Exception e) {
        }

        //fConTextHeight
        double fConTextHeight = opts.fConTextHeight;
        try {
            if (scanForTokens.contains("-theight") && lineData.contains("-theight")) {
                int textHeightIndex = lineData.indexOf("-theight") + 1;
                try {
                    fConTextHeight = Double.parseDouble(lineData.get(textHeightIndex));
                } catch (NumberFormatException e) {
                }
            }
        } catch (Exception e) {
        }

        //fConSelfTextHeight
        double fConSelfTextHeight = opts.fConSelfTextHeight;
        try {
            if (scanForTokens.contains("-theight") && lineData.contains("-theight")) {
                int textHeightIndex = lineData.indexOf("-theight") + 1;
                try {
                    fConSelfTextHeight = Double.parseDouble(lineData.get(textHeightIndex));
                } catch (NumberFormatException e) {
                }
            }
        } catch (Exception e) {
        }

        //fConLineWidth
        double fConLineWidth = opts.fConLineWidth;
        try {
            if (scanForTokens.contains("-lwidth") && lineData.contains("-lwidth")) {
                int lineWidthIndex = lineData.indexOf("-lwidth") + 1;
                try {
                    fConLineWidth = Double.parseDouble(lineData.get(lineWidthIndex));
                } catch (NumberFormatException e) {
                }
            }
        } catch (Exception e) {
        }

        //fConHasArrow
        boolean fConHasArrow = opts.fConHasArrow;
        try {
            if (scanForTokens.contains("-arrow") && lineData.contains("-arrow"))
                fConHasArrow = true;
        } catch (Exception e) {
        }

        opts.fConSelfTextHeight = fConSelfTextHeight;
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
