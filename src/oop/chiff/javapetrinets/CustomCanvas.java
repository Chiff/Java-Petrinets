package oop.chiff.javapetrinets;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CustomCanvas extends JPanel {
    private boolean init = false;

    private HashMap<Integer, Shape> shapes;
    private HashMap<Arc, ArrayList<Shape>> arcs;
    private Document petrinet;
    private ButtonManager buttonManager;

    private TwoPhaseClickAction twoClickAction = null;

    public CustomCanvas() {
        Dimension dim = new Dimension(2000, 2000);
        setPreferredSize(dim);
        shapes = new HashMap<>();
        arcs = new HashMap<>();

        setBackground(Color.white);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                super.mouseClicked(me);
                Shape s = getClickedShape(me);

                if (twoClickAction != null) {
                    executeTwoClickAction(s, me.getX(), me.getY());
                    Main.sender.send("end of double-click");
                } else {

                    if (buttonManager.getSimpleAction().equals("Place") || buttonManager.getSimpleAction().equals("Transition")) {
                        createNewNode(me, buttonManager.getSimpleAction());
                        return;
                    }

                    if (s == null)
                        return;

                    Node n = shapeToNode(s);

                    if ((buttonManager.getSimpleAction().equals("Arc") || buttonManager.getSimpleAction().equals("Move")) && n != null) {
                        int startID = n.getId();
                        twoClickAction = new TwoPhaseClickAction(startID);
                        return;
                    }

                    elementActionExecute(n, s);

                    repaint();
                }
            }
        });
    }

    //vykonanie operacie s PLACE, TRANSITION a ARC
    private void elementActionExecute(Node n, Shape s) {
        Arc a;
        if (n == null) {
            a = shapeToArc(s);
            if (a == null)
                return;
            buttonManager.execute(a);
        } else {
            if (n instanceof Transition)
                buttonManager.execute((Transition) n);
            else {
                buttonManager.execute((Place) n);
            }
        }
    }

    //vykonanie operacie MOVE a new ARC
    private void executeTwoClickAction(Shape s, int x, int y) {
        int oldID = twoClickAction.getElId();
        twoClickAction = null;

        if (s == null && buttonManager.getSimpleAction().equals("Move")) {
            try {
                Node n = petrinet.getNodeById(oldID);
                n.setX(x);
                n.setY(y);
                repaint();
            } catch (NonExistingElementException e) {
                System.err.println(e.getMessage());
            }
        } else if (buttonManager.getSimpleAction().equals("Arc") && s != null) {
            int newID = Objects.requireNonNull(shapeToNode(s)).getId();
            createNewArc(oldID, newID);
        }
    }

    //vytvorenie novej hrany
    private void createNewArc(int inID, int outID) {
        int id = petrinet.getLastUsedId() + 1;
        petrinet.setlastUsedId(id);

        String type = JOptionPane.showInputDialog(this, "Select arc type\nregular, inhibitor, reset", "regular");
        if (!type.equals("reset") && !type.equals("inhibitor") && !type.equals("regular"))
            type = "regular";

        String multiplicity = JOptionPane.showInputDialog(this, "Select arc multiplicity", "1");
        int result;
        try {
            result = Integer.parseInt(multiplicity);
        } catch (NumberFormatException e) {
            result = -1;
        }

        Arc a = new Arc(inID, outID, id, result, type);
        a.setDoc(petrinet);
        try {
            a.arcCheck();
        } catch (IncorrectArcDefinition | NonExistingElementException e) {
            System.err.println(e.getMessage());
            return;
        }

        petrinet.arc.add(a);

        repaint();
    }

    private void createNewNode(MouseEvent me, String type) {
        int id = petrinet.getLastUsedId() + 1;
        petrinet.setlastUsedId(id);
        Node n;
        if (type.equals("Transition")) {
            n = new Transition(me.getX(), me.getY(), id);
            petrinet.transition.add((Transition) n);
            petrinet.nodeMap.put(id, n);
        } else {
            n = new Place(me.getX(), me.getY(), id);
            petrinet.place.add((Place) n);
            petrinet.nodeMap.put(id, n);
        }

        n.setDoc(petrinet);
        repaint();
    }

    private Shape getClickedShape(MouseEvent me) {
        for (Shape s : shapes.values())
            if (s.contains(me.getPoint()))
                return s;

        for (ArrayList<Shape> shp : arcs.values())
            for (Shape s : shp)
                if (s.getClass().toString().contains("Line2D") && lineIntesects(me, (Line2D) s))
                    return s;

        return null;
    }

    private Node shapeToNode(Shape s) {
        try {
            return petrinet.getNodeById((Integer) getKeyFromValue(shapes, s));
        } catch (NullPointerException | NonExistingElementException e) {
            if (e.getMessage() != null)
                System.err.println(e.getMessage());
            else
                e.printStackTrace();
            return null;
        }
    }

    private Arc shapeToArc(Shape s) {
        try {
            return (Arc) getKeyFromValue(arcs, s);
        } catch (NullPointerException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    private boolean lineIntesects(MouseEvent me, Line2D line) {
        int HITBOX_SIZE = 6;

        int xLine = me.getX();
        int yLine = me.getY();

        int boxX = xLine - HITBOX_SIZE / 2;
        int boxY = yLine - HITBOX_SIZE / 2;

        return line.intersects(boxX, boxY, HITBOX_SIZE, HITBOX_SIZE);
    }

    private void drawPetrinet() {
        this.shapes = new HashMap<>();
        this.arcs = new HashMap<>();

        this.drawArcs();
        this.drawTransitions();
        this.drawPlaces();
    }

    private void drawArcs() {
        ArrayList<Arc> arcs2 = petrinet.getArc();
        for (Arc a : arcs2)
            a.arcDraw(arcs);
    }

    private void drawTransitions() {
        ArrayList<Transition> transitions = petrinet.getTransition();
        for (Transition t : transitions)
            t.transitionDraw(shapes);
    }

    private void drawPlaces() {
        ArrayList<Place> places = petrinet.getPlace();
        for (Place p : places)
            p.placeDraw(shapes);
    }

    public void setPetrinet(Document petrinet) {
        this.petrinet = petrinet;
        this.shapes = new HashMap<>();
        this.arcs = new HashMap<>();
        init = true;
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics grphcs) {
        super.paintComponent(grphcs);
        grphcs.clearRect(0, 0, 2000, 2000);
        Graphics2D g2d = (Graphics2D) grphcs;

        if (init) {
            this.drawPetrinet();

            for (ArrayList<Shape> shp : arcs.values())
                for (Shape s : shp) {
                    g2d.setPaint(Color.DARK_GRAY);
                    g2d.draw(s);
                }

            shapes.forEach((k, v) -> drawNode(k, v, g2d));
        }
    }

    private void drawNode(int key, Shape s, Graphics2D g2d) {
        Node n;
        try {
            n = petrinet.getNodeById(key);
        } catch (NonExistingElementException e) {
            return;
        }

        g2d.setPaint(Color.white);
        if (n instanceof Transition) {
            try {
                ArrayList<Arc> inArc = n.findMyArcs(true);
                if (((Transition) n).sourceCheck(inArc)) {
                    g2d.setPaint(Color.green);
                }
            } catch (NonExistingElementException e) {
                System.err.println(e.getMessage());
            }
        }

        g2d.fill(s);
        g2d.setPaint(Color.black);
        g2d.draw(s);

        if (n instanceof Place)
            g2d.drawString(Integer.toString(((Place) n).getTokens()), n.getX() + 12, n.getY() + 20);

    }

    private Object getKeyFromValue(Map hm, Object value) {
        for (Object o : hm.keySet()) {
            if (hm.get(o) instanceof ArrayList) {
                if (((ArrayList) hm.get(o)).contains(value))
                    return o;
            } else if (hm.get(o).equals(value)) {
                return o;
            }
        }
        return null;
    }

    public void setButtonManager(ButtonManager buttonManager) {
        this.buttonManager = buttonManager;
    }
}