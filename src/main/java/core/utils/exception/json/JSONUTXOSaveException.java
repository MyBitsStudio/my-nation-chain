package core.utils.exception.json;

public class JSONUTXOSaveException extends RuntimeException{

    public JSONUTXOSaveException(String message, Exception e){
        super("[JSON UTXO SAVE EXCEPTION] "+message, e);
    }
}
