package core.ledger.wallet.impl;

import core.ledger.block.transaction.TransactionInput;
import core.ledger.block.transaction.TransactionOutput;
import core.ledger.block.transaction.impl.SystemTransaction;
import core.ledger.contract.Contract;
import core.ledger.wallet.Wallet;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Map;

public class ContractWallet extends Wallet {

    public ContractWallet(Contract contract){super(contract);}

    public SystemTransaction sendSystemTransaction(PublicKey _recipient, double value, @NotNull Contract contract){
        if(getBalance(contract.getAddress()) < value) {
            return null;
        }
        ArrayList<TransactionInput> inputs = new ArrayList<>();

        double total = 0;
        for (Map.Entry<String, TransactionOutput> item : transactions.entrySet()){
            TransactionOutput UTXO = item.getValue();
            total += UTXO.value().get();
            inputs.add(new TransactionInput(UTXO.PTID(), UTXO));
            if(total > value) break;
        }

        SystemTransaction newTransaction = new SystemTransaction(publicKey, _recipient , value, inputs, contract);
        newTransaction.setContract(contract);
        newTransaction.generateSignature(privateKey);

        for(TransactionInput input: inputs){
            transactions.remove(input.getTOID());
        }

        return newTransaction;
    }

}
