package oop.chiff.javapetrinets;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

@XmlRootElement
@XmlAccessorType(value = XmlAccessType.FIELD)
public class Arc {
    @XmlTransient
    private Document doc;
    private String type;
    private int id;
    private int destinationId;
    private int multiplicity;
    private int sourceId;

    public Arc(int source, int destination, int id, int multiplicity, String type) {
        this.sourceId = source;
        this.destinationId = destination;
        this.id = id;
        this.type = type;
        this.multiplicity = (type.equals("reset")) ? 1 : multiplicity;
    }

    public Arc() {}

    public void arcDraw(HashMap<Arc, ArrayList<Shape>> s) {

        int startX, startY, endX, endY;
        try {
            startX = doc.getNodeById(getSourceId()).getX() + 15;
            startY = doc.getNodeById(getSourceId()).getY() + 15;
            endX = doc.getNodeById(getDestinationId()).getX() + 15;
            endY = doc.getNodeById(getDestinationId()).getY() + 15;
        } catch (NonExistingElementException e) {
            return;
        }
        Shape arc = new Line2D.Double(startX, startY, endX, endY);
        Point.Double start = new Point.Double(startX, startY);
        Point.Double end = new Point.Double(endX, endY);

        ArrayList<Shape> shapes = new ArrayList<>();

        drawArrowHead(end, start, shapes);

        shapes.add(arc);
        s.put(this, shapes);

    }

    public void removeThisArc() {
        doc.removeArc(this);
    }

    private void drawArrowHead(Point.Double tip, Point.Double tail, ArrayList<Shape> s) {

        double phi = Math.toRadians(30);
        int barb = 15;

        double dy = tip.y - tail.y;
        double dx = tip.x - tail.x;
        double theta = Math.atan2(dy, dx);
        double rho = theta + phi;

        tip.x = tip.x - 25 * Math.cos(theta);
        tip.y = tip.y - 25 * Math.sin(theta);

        if (!type.equals("inhibitor")) {
            setBarb(tip, s, phi, barb, theta, rho);

            if (type.equals("reset")) {
                tip.x = tip.x - 10 * Math.cos(theta);
                tip.y = tip.y - 10 * Math.sin(theta);
                rho = theta + phi;
                setBarb(tip, s, phi, barb, theta, rho);
            }
        } else {
            int CIRCLE_SIZE = 10;
            s.add(new Ellipse2D.Double(tip.x, tip.y, CIRCLE_SIZE, CIRCLE_SIZE));
        }
    }

    private void setBarb(Point2D.Double tip, ArrayList<Shape> s, double phi, int barb, double theta, double rho) {
        double x;
        double y;
        for (int j = 0; j < 2; j++) {
            x = tip.x - barb * Math.cos(rho);
            y = tip.y - barb * Math.sin(rho);
            s.add(new Line2D.Double(tip.x, tip.y, x, y));
            rho = theta - phi;
        }
    }

    public boolean canBeFired() throws NonExistingElementException {
        if (type.equals("inhibitor"))
            return ((Place) getSourceNode()).getTokens() < multiplicity;
        return type.equals("reset") || ((Place) getSourceNode()).getTokens() >= multiplicity;
    }

    public void useThisArc(boolean inArc) throws NonExistingElementException {
        if (type.equals("reset")) {
            ((Place) getSourceNode()).setTokens(0);
            return;
        }
        if (type.equals("inhibitor"))
            return;

        if (inArc)
            ((Place) getSourceNode()).setTokens(((Place) getSourceNode()).getTokens() - multiplicity);
        else ((Place) getDestinationNode()).setTokens(((Place) getDestinationNode()).getTokens() + multiplicity);
    }

    public void arcCheck() throws IncorrectArcDefinition, NonExistingElementException {
        if (!checkMe())
            throw new IncorrectArcDefinition("Nespravne zadefinovana hrana, id hrany: " + this.id);
        if (!incorrectNodeTypes())
            throw new IncorrectArcDefinition("Hrana medzi vrcholmi rovnakeho typu sa neda vytvorit, id hrany: " + this.id);
        if (!definitionCheck())
            throw new IncorrectArcDefinition(type + " musi obsahovat source node typu place, id hrany: " + this.id);
    }

    private boolean checkMe() {
        if (multiplicity < 1)
            return false;
        return destinationId != sourceId && destinationId != 0 && sourceId != 0; //jaxb nastavi 0 ak dany xml element neexistuje a petriflow generuje id > 0
    }

    private boolean definitionCheck() throws NonExistingElementException {
        if (type.equals("inhibitor") || type.equals("reset"))
            return doc.getNodeById(this.sourceId) instanceof Place;
        return true;
    }

    private boolean incorrectNodeTypes() throws NonExistingElementException {
        Node n[] = {null, null};

        n[0] = doc.getNodeById(this.sourceId);
        n[1] = doc.getNodeById(this.destinationId);

        return (n[0] instanceof Transition && n[1] instanceof Place) || (n[0] instanceof Place && n[1] instanceof Transition);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDestinationId() {
        return destinationId;
    }

    public Node getDestinationNode() throws NonExistingElementException {
        return doc.getNodeById(destinationId);
    }

    public void setDestinationId(int destinationId) {
        this.destinationId = destinationId;
    }

    public int getMultiplicity() {
        return multiplicity;
    }

    public void setMultiplicity(int multiplicity) {
        this.multiplicity = multiplicity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSourceId() {
        return sourceId;
    }

    public Node getSourceNode() throws NonExistingElementException {
        return doc.getNodeById(sourceId);
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public Document getDoc() {
        return doc;
    }

    public void setDoc(Document doc) {
        this.doc = doc;
    }

    @Override
    public String toString() {
        return "ClassPojo [id = " + id + ", destinationId = " + destinationId + ", multiplicity = " + multiplicity + ", type = " + type + ", sourceId = " + sourceId + "]";
    }
}
