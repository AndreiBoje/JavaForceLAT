package com.forcelat;

import javafx.scene.control.TextArea;

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
                //TODO: ability to add more options besides the default ones
                //parse add command
                if (lineData.get(0).equals("add")) {
                    //check for args
                    if (lineData.contains("-id") && lineData.contains("-pos") && lineData.contains("-txt")) {
                        int idIndex = lineData.indexOf("-id") + 1;
                        int posIndex = lineData.indexOf("-pos") + 1;
                        int txtIndex = lineData.indexOf("-txt") + 1;

                        //parse args
                        int ID = 0;
                        double xPos = -1, yPos = -1;
                        String txt = lineData.get(txtIndex);

                        try {
                            ID = Integer.parseInt(lineData.get(idIndex));
                        } catch (NumberFormatException e) {
                            //  System.out.println("ERROR: There's been an error parsing the ID! Is it a number?");
                        }

                        try {
                            if (lineData.get(posIndex).split(",").length == 2) {
                                xPos = Double.parseDouble(lineData.get(posIndex).split(",")[0]);
                                yPos = Double.parseDouble(lineData.get(posIndex).split(",")[1]);
                            }
                        } catch (NumberFormatException e) {
                            //  System.out.println("ERROR: There's been an error parsing the Position! Is it a double,double?");
                        }

                        //adding the node
                        fnm.addFNode(xPos, yPos, ID, txt);
                    }
                }
                //parse ucon command
                if (lineData.get(0).equals("ucon")) {
                    //check for args
                    if (lineData.contains("-id")) {
                        int idIndex = lineData.indexOf("-id") + 1;
                        int txtIndex = lineData.indexOf("-txt") + 1;

                        //parse args
                        int IDStart = -1, IDEnd = -1;
                        String txt = "";

                        if (lineData.contains("-txt"))
                            txt = lineData.get(txtIndex);

                        try {
                            if (lineData.get(idIndex).split(",").length == 2) {
                                IDStart = Integer.parseInt(lineData.get(idIndex).split(",")[0]);
                                IDEnd = Integer.parseInt(lineData.get(idIndex).split(",")[1]);
                            }
                        } catch (NumberFormatException e) {
                            //  System.out.println("ERROR: There's been an error parsing the Indexes! Is it an int,int?");
                        }
                        fnm.unidFConnection(IDStart, IDEnd, txt);
                    }
                }
                //parse bcon command
                if (lineData.get(0).equals("bcon")) {
                    //check for args
                    if (lineData.contains("-id")) {
                        int idIndex = lineData.indexOf("-id") + 1;
                        int txtIndexFrom = lineData.indexOf("-txt") + 1;
                        int txtIndexTo = lineData.indexOf("-txt") + 2;

                        //parse args
                        int IDStart = -1, IDEnd = -1;
                        String txtFrom = " ";
                        String txtTo = " ";

                        if (lineData.contains("-txt")) {
                            txtFrom = lineData.get(txtIndexFrom);
                            txtTo = lineData.get(txtIndexTo);
                        }

                        try {
                            if (lineData.get(idIndex).split(",").length == 2) {
                                IDStart = Integer.parseInt(lineData.get(idIndex).split(",")[0]);
                                IDEnd = Integer.parseInt(lineData.get(idIndex).split(",")[1]);
                            }
                        } catch (NumberFormatException e) {
                            //  System.out.println("ERROR: There's been an error parsing the Indexes! Is it an int,int?");
                        }
                        fnm.bidFConnection(IDStart, IDEnd, txtFrom, txtTo);
                    }
                }
                //parse jpr command
                if (lineData.get(0).equals("jpr")) {
                    if (lineData.contains("-id")) {
                        int idIndex = lineData.indexOf("-id") + 1;
                        int txtIndex = lineData.indexOf("-txt") + 1;

                        //parse args
                        int IDStart = -1, IDEnd = -1;
                        String txt = "";

                        if (lineData.contains("-txt"))
                            txt = lineData.get(txtIndex);

                        try {
                            if (lineData.get(idIndex).split(",").length == 2) {
                                IDStart = Integer.parseInt(lineData.get(idIndex).split(",")[0]);
                                IDEnd = Integer.parseInt(lineData.get(idIndex).split(",")[1]);
                            }
                        } catch (NumberFormatException e) {
                            //  System.out.println("ERROR: There's been an error parsing the Indexes! Is it an int,int?");
                        }
                        fnm.jprFConnection(IDStart, IDEnd, txt);
                    }
                }
                //parse self command
                if (lineData.get(0).equals("self")) {
                    //check for args
                    if (lineData.contains("-id")) {
                        int idIndex = lineData.indexOf("-id") + 1;
                        int txtIndex = lineData.indexOf("-txt") + 1;

                        //parse args
                        int ID = -1;
                        String txt = "";

                        if (lineData.contains("-txt"))
                            txt = lineData.get(txtIndex);

                        try {
                            ID = Integer.parseInt(lineData.get(idIndex));
                        } catch (NumberFormatException e) {
                            //  System.out.println("ERROR: There's been an error parsing the Indexes! Is it an int,int?");
                        }
                        fnm.selfFConnection(ID, txt);
                    }
                }
                //TODO: add set node to be start command
                //TODO: add set node to be final command
                //TODO: add alter defaults command
            }
        } catch (IndexOutOfBoundsException e) {
            //   System.out.println("ERROR: There's been an indexing error! Are there enough arguments?");
        }
        fnm.display();
    }
}
