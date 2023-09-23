package core.ledger.block.transaction;

import core.ledger.wallet.Wallet;
import core.utils.protect.ProtectedDouble;

import java.util.List;

public class TransactionBuilder {

    private int OPCODE;

    private ProtectedDouble value = new ProtectedDouble();

    private String contract;

    private List<TransactionInput> inputs;

    private Wallet to, from;

    private Transaction build;

    public TransactionBuilder setType(int type){
        this.OPCODE = type;
        return this;
    }

    public TransactionBuilder setAmount(double amount){
        this.value.set(amount);
        return this;
    }

    public TransactionBuilder setContract(String contract){
        this.contract = contract;
        return this;
    }

    public TransactionBuilder setFrom(Wallet from){
        this.from = from;
        return this;
    }

    public TransactionBuilder setTo(Wallet to){
        this.to = to;
        return this;
    }
}
