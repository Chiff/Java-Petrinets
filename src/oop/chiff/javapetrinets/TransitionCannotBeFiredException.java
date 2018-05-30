package oop.chiff.javapetrinets;

public class TransitionCannotBeFiredException extends ModifiedException {

    public TransitionCannotBeFiredException(int id) {
        super("Dany prechod ID: " +id+ " nie je spustitelny.");
    }
}
