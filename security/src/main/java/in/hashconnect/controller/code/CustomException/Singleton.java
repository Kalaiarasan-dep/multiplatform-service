package in.hashconnect.controller.code.CustomException;

public final class Singleton {
    private final Singleton intance = new Singleton();
    private Singleton(){}

    public static Singleton getInstance(){
        return new Singleton();
    }

}


