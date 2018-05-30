package oop.chiff.javapetrinets;

public class WrongPetriflowInputException extends ModifiedException {

    public WrongPetriflowInputException(int id) {
        super("Dane ID elementu " +id+ " nie je prechod.");
    }

}