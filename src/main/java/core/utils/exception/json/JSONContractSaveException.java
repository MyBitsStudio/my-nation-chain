package core.utils.exception.json;

public class JSONContractSaveException extends RuntimeException{

    public JSONContractSaveException(String message, Exception e){
        super("[JSON CONTRACT SAVE EXCEPTION] "+message, e);
    }
}
