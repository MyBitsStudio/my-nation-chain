package core.utils.exception;

public class KeyLoadingException extends RuntimeException{

    public KeyLoadingException(String message, Exception e){
        super("[KEY LOADING EXCEPTION] "+message, e);
    }
}
