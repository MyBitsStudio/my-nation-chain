package core.ledger.block.transaction;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

public class TransactionInput implements Serializable {
    @Serial
    private static final long serialVersionUID = 5538795576441422632L;
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
