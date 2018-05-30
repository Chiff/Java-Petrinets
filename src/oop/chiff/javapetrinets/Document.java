package oop.chiff.javapetrinets;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;

@XmlRootElement(name = "document")
public class Document {
    protected ArrayList<Transition> transition;
    protected ArrayList<Place> place;
    protected ArrayList<Arc> arc;
    protected HashMap<Integer, Node> nodeMap;

    @XmlTransient
    private int lastUsedId = 0;

    public Document() {
        this.arc = new ArrayList<>();
        this.place = new ArrayList<>();
        this.transition = new ArrayList<>();
    }

    public String showMarking() {
        StringBuilder s = new StringBuilder();
        for (Place aPlace : this.place) s.append(aPlace.getTokens()).append(", ");
        return s.toString();
    }

    private boolean isLastId(int id){
        return id > lastUsedId;
    }

    public void createHashMap() {
        HashMap<Integer, Node> map = new HashMap<>();

        for (Transition aTransition : transition) {
            map.put(aTransition.getId(), aTransition);
            if(isLastId(aTransition.getId()))
                lastUsedId = aTransition.getId();
        }
        for (Place aPlace : place) {
            map.put(aPlace.getId(), aPlace);
            if(isLastId(aPlace.getId()))
                lastUsedId = aPlace.getId();
        }

        this.nodeMap = map;
    }

    public void removeNode(Transition t) {
        nodeMap.remove(t);
        transition.remove(t);
    }

    public void removeNode(Place p) {
        nodeMap.remove(p);
        place.remove(p);
    }

    public void removeArc(Arc a) {
        arc.remove(a);
    }

    public Node getNodeById(int id) throws NonExistingElementException {
        Node n;

        try {
            n = this.nodeMap.get(id);
        } catch (NullPointerException e) {
            throw new NonExistingElementException(id);
        }
        return n;
    }

    public void arcValidate() throws IncorrectArcDefinition, NonExistingElementException {
        for (Arc aArc : arc) {
            aArc.arcCheck();
            if(isLastId(aArc.getId()))
                lastUsedId = aArc.getId();
        }
    }

    public void setDocuments() {
        this.arc.forEach((a) -> a.setDoc(this));
        this.place.forEach((p) -> p.setDoc(this));
        this.transition.forEach((t) -> t.setDoc(this));
    }

    public ArrayList<Arc> getArc() {
        return arc;
    }

    @XmlElement(name = "arc")
    public void setArc(ArrayList<Arc> arc) {
        this.arc = arc;
    }

    public ArrayList<Transition> getTransition() {
        return transition;
    }

    @XmlElement(name = "transition")
    public void setTransition(ArrayList<Transition> transition) {
        this.transition = transition;
    }

    public ArrayList<Place> getPlace() {
        return place;
    }

    @XmlElement(name = "place")
    public void setPlace(ArrayList<Place> place) {
        this.place = place;
    }

    public int getLastUsedId() {
        return lastUsedId;
    }

    public void setlastUsedId(int id) {
        this.lastUsedId = id;
    }

    @Override
    public String toString() {
        return "ClassPojo [arcs = " + arc.size() + ", transitions = " + transition.size() + ", places = " + place.size() + "]";
    }
}
