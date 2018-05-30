package oop.chiff.javapetrinets;

import java.awt.*;
import java.util.ArrayList;

public class ButtonManager {
    private ArrayList<Button> tlacidla;
    private Label akcia;

    public ButtonManager(Panel p) {
        tlacidla = new ArrayList<>();
        for (int i = 0; i < p.getComponentCount(); i++) {
            Component c = p.getComponent(i);
            int result = setComponent(c);
            if (result == 0)
                buttonSetActionListener(tlacidla.get(i));
        }

        tlacidla.forEach(but -> but.setBackground(Color.LIGHT_GRAY));
    }

    //get label of clicked button
    public String getSimpleAction() {
        return akcia.getText().substring(7);
    }

    public void execute(Place p) {
        switch (getSimpleAction()) {
            case "+":
                p.addToken();
                break;
            case "-":
                p.deleteToken();
                break;
            case "Remove":
                p.removeThisNode();
                break;
            default:
                Main.sender.send("Transition doesn't support this action: " + getSimpleAction());
        }
    }

    public void execute(Transition t) {
        switch (getSimpleAction()) {
            case "Play":
                try {
                    t.fireTransition();
                } catch (NonExistingElementException | TransitionCannotBeFiredException e) {
                    System.err.println(e.getMessage());
                }
                break;
            case "Remove":
                t.removeThisNode();
                break;
            default:
                Main.sender.send("Place doesn't support this action: " + getSimpleAction());
        }
    }

    public void execute(Arc a) {
        switch (getSimpleAction()) {
            case "Remove":
                a.removeThisArc();
                break;
            default:
                Main.sender.send("Arc doesn't support this action: " + getSimpleAction());
        }
    }

    private int setComponent(Component c) {
        if (c.getName().contains("button")) {
            tlacidla.add((Button) c);
            return 0;
        } else if (c.getName().contains("label")) {
            akcia = (Label) c;
            return 1;
        }
        return -1;
    }

    private void buttonSetActionListener(Button b) {
        b.addActionListener(e -> {
            tlacidla.forEach(but -> but.setBackground(Color.LIGHT_GRAY));
            b.setBackground(Color.green);
            akcia.setText("Action: " + b.getLabel());
        });
    }
}
