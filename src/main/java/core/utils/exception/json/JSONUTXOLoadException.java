package core.utils.exception.json;

public class JSONUTXOLoadException extends RuntimeException{

    public JSONUTXOLoadException(String message, Exception e){
        super("[JSON UTXO LOAD EXCEPTION] "+message, e);
    }
}
