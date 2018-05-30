package oop.chiff.javapetrinets;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;

public class Transition extends Node {

    public Transition(int x, int y, int id){
        this.x = x;
        this.y = y;
        this.id = id;
        this.label = "";
    }

    public Transition(){}

    public void transitionDraw(HashMap<Integer, Shape> hm) {
        Shape transition = new Rectangle2D.Double(x, y, 30, 30);
        hm.put(id, transition);
    }

    public void fireTransition() throws NonExistingElementException, TransitionCannotBeFiredException {
        ArrayList<Arc> inArc = findMyArcs(true);
        ArrayList<Arc> outArc = findMyArcs(false);

        Main.sender.send("--- ID: " + id + ",LABEL: " + label + " ---");
        Main.sender.send("Before play: " + doc.showMarking());
        if (sourceCheck(inArc)) {
            for (Arc anInArc : inArc) anInArc.useThisArc(true);
            for (Arc anOutArc : outArc) anOutArc.useThisArc(false);
            Main.sender.send("New marking: " + doc.showMarking());
        } else {
            Main.sender.send("Operation not successful, creating exception");
            throw new TransitionCannotBeFiredException(this.id);
        }
    }

    public boolean sourceCheck(ArrayList<Arc> inArc) throws NonExistingElementException {
        for (Arc anInArc : inArc)
            if (!anInArc.canBeFired())
                return false;

        return true;
    }

    @Override
    public String toString() {
        return "ClassPojo [id = " + id + ", label = " + label + ", y = " + y + ", x = " + x + "]";
    }
}
