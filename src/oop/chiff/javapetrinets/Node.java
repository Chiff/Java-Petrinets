package oop.chiff.javapetrinets;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;

@XmlRootElement
@XmlAccessorType(value = XmlAccessType.FIELD)
abstract class Node {
    @XmlTransient
    protected Document doc;
    protected String label;
    protected int id;
    protected int x;
    protected int y;

    public void removeThisNode() {
        ArrayList<Arc> inArc = findMyArcs(true);
        ArrayList<Arc> outArc = findMyArcs(false);

        if (this.getClass().toString().contains("Transition"))
            doc.removeNode((Transition) this);
        else doc.removeNode((Place) this);

        inArc.forEach(Arc::removeThisArc);
        outArc.forEach(Arc::removeThisArc);
    }

    public ArrayList<Arc> findMyArcs(boolean inArc) {
        ArrayList<Arc> a = new ArrayList<>();
        for (int i = 0; i < doc.arc.size(); i++)
            if (inArc) {
                if (doc.arc.get(i).getDestinationId() == this.id)
                    a.add(doc.arc.get(i));
            } else if (doc.arc.get(i).getSourceId() == this.id)
                a.add(doc.arc.get(i));

        return a;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Document getDoc() {
        return doc;
    }

    public void setDoc(Document doc) {
        this.doc = doc;
    }
}
