package core.utils.exception.json;

public class JSONContractLoadException extends RuntimeException{

    public JSONContractLoadException(String message, Exception e){
        super("[JSON CONTRACT LOAD EXCEPTION] "+message, e);
    }
}
