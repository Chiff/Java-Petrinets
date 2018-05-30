package oop.chiff.javapetrinets;

public class NonExistingElementException extends ModifiedException {

    public NonExistingElementException(int id) {
        super("Dane ID: " + id + " neexistuje.");
    }
}