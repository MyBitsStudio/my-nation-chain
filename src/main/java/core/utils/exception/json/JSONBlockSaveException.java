package core.utils.exception.json;

public class JSONBlockSaveException extends RuntimeException{

    public JSONBlockSaveException(String message, Exception e){
        super("[JSON BLOCK SAVE EXCEPTION] "+message, e);
    }
}
