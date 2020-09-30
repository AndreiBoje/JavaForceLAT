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
    private TextArea ta;
    private FNodeManager fnm;

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
        ArrayList<String> rawArgs = new ArrayList<>();
        ArrayList<Integer> indexes = new ArrayList<>();

        fnm.clear();

        try {
            for (int i = 0; i < rawLines.size(); i++) {
                ArrayList<String> lineData = (ArrayList<String>) Arrays.stream(rawLines.get(i).split(" ")).collect(Collectors.toList());

                //parse add command
                if (lineData.get(0).equals("add")) {

                    //check for args
                    if (lineData.contains("-id") && lineData.contains("-pos")) {
                        int idIndex = lineData.indexOf("-id") + 1;
                        int posIndex = lineData.indexOf("-pos") + 1;
                       // int txtIndex = lineData.indexOf("-txt") + 1;

                        System.out.println("The id : " + lineData.get(idIndex));
                        System.out.println("The pos: " + lineData.get(posIndex));
                        //System.out.println("The txt: " + lineData.get(txtIndex));

                        //parse args
                        int ID = Integer.parseInt(lineData.get(idIndex));
                        double xPos = -1, yPos = -1;

                        if (lineData.get(posIndex).split(",").length == 2) {
                            xPos = Double.parseDouble(lineData.get(posIndex).split(",")[0]);
                            yPos = Double.parseDouble(lineData.get(posIndex).split(",")[1]);
                        }

                        fnm.addFNode(xPos, yPos, ID);
                    }
                }
            }
        } catch (IndexOutOfBoundsException ignored) { }
        fnm.display();

    }
}
