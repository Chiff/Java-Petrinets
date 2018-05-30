package oop.chiff.javapetrinets;

public class TwoPhaseClickAction {
    private int elId;

    public TwoPhaseClickAction(int elId) {
        Main.sender.send("Novy dvoj klik zacal");
        this.elId = elId;
    }

    public int getElId() {
        return elId;
    }
}
