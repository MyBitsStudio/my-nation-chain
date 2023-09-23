package core.ledger.block.transaction;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class TransactionInput {
    private String TOID;
    private TransactionOutput UTXO;

    public TransactionInput(String TOID, TransactionOutput UTXO){
        this.TOID = TOID;
        this.UTXO = UTXO;
    }
}
