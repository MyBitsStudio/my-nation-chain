package core.ledger.block.transaction.impl;

import core.ledger.block.transaction.Transaction;
import core.ledger.block.transaction.TransactionIdentity;
import core.ledger.block.transaction.TransactionInput;
import core.ledger.contract.Contract;
import core.utils.CryptoUtilities;
import core.utils.protect.ProtectedDouble;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class SystemTransaction extends Transaction {

    public SystemTransaction(PublicKey from, PublicKey to, double value, ArrayList<TransactionInput> inputs, Contract contract){
        super();
        this.sender = from;
        this.receiver = to;
        this.value = new ProtectedDouble(value);
        this.inputs = inputs;
        outputs = new ArrayList<>();
        this.contract = contract;
    }
    @Override
    public void generateSignature(PrivateKey key) {
        String data = CryptoUtilities.fromKey(sender) + CryptoUtilities.fromKey(receiver) + value + TransactionIdentity.SYSTEM + 0x0;
        signature = CryptoUtilities.applyECDSASig(key,data);
    }

    @Override
    public boolean verifySignature() {
        String data = CryptoUtilities.fromKey(sender) + CryptoUtilities.fromKey(receiver) + value + TransactionIdentity.SYSTEM + 0x0;
        return CryptoUtilities.verifyECDSASig(sender, data, signature);
    }

    @Override
    public void onComplete() {

    }
}
