package core.utils.exception.json;

public class JSONBlockLoadException extends RuntimeException{

    public JSONBlockLoadException(String message, Exception e){
        super("[JSON BLOCK LOAD EXCEPTION] "+message, e);
    }
}
