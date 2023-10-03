package core.ledger.block.transaction;

import lombok.Getter;
import lombok.Setter;

public class TransactionInput {
    private String TOID;

    public String getTOID() {
        return TOID;
    }

    public void setTOID(String TOID) {
        this.TOID = TOID;
    }

    public TransactionOutput getUTXO() {
        return UTXO;
    }

    public void setUTXO(TransactionOutput UTXO) {
        this.UTXO = UTXO;
    }

    private TransactionOutput UTXO;

    public TransactionInput(String TOID, TransactionOutput UTXO){
        this.TOID = TOID;
        this.UTXO = UTXO;
    }
}
