package core.utils.exception;

public class KeySavingException extends RuntimeException{

    public KeySavingException(String message, Exception e){
        super("[KEY SAVING EXCEPTION] "+message, e);
    }
}
