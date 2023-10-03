package core.utils.exception.json;

public class JSONWalletSaveException extends RuntimeException{

    public JSONWalletSaveException(String message, Exception e){
        super("[JSON WALLET SAVE EXCEPTION] "+message, e);
    }
}
