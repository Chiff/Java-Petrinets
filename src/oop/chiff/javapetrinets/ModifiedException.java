package oop.chiff.javapetrinets;

public class ModifiedException extends Exception {
    private String message;

    public ModifiedException(String message) {
        super(message);
        this.message = this.getClass().getSimpleName() + ": " + message;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
