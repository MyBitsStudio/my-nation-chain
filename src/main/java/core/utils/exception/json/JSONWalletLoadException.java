package core.utils.exception.json;

public class JSONWalletLoadException extends RuntimeException{

    public JSONWalletLoadException(String message, Exception e){
        super("[JSON WALLET LOAD EXCEPTION] "+message, e);
    }
}
