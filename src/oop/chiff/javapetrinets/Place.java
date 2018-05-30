package oop.chiff.javapetrinets;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.HashMap;

public class Place extends Node {
    private String stat;
    private int tokens;

    public Place(int x, int y, int id){
        this.x = x;
        this.y = y;
        this.id = id;
        this.label = "";
        this.tokens = 0;
    }

    public Place(){}

    public void placeDraw(HashMap<Integer, Shape> hm) {
        Shape place = new Ellipse2D.Double(x, y, 30, 30);
        hm.put(id, place);
    }

    public String getStatic() {
        return stat;
    }

    public void setStatic(String stat) {
        this.stat = stat;
    }

    public int getTokens() {
        return tokens;
    }

    public void addToken() {
        tokens++;
    }

    public void deleteToken() {
        if (tokens > 0)
            tokens--;
    }

    public void setTokens(int t) {
        this.tokens = t;
    }

    @Override
    public String toString() {
        return "ClassPojo [id = " + id + ", static = " + stat + ", tokens = " + tokens + ", label = " + label + ", y = " + y + ", x = " + x + "]";
    }
}