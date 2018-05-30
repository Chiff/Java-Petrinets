package oop.chiff.javapetrinets;

public class SendDevMessage{
    private boolean DEV;

    public SendDevMessage(boolean isDev){
        this.DEV = isDev;
    }

    public void send(String message){
        if(DEV)
            System.out.println(message);
    }
}