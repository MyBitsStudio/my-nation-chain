package core.ledger.block.transaction;

import core.LedgerAttributes;
import core.ledger.HyperLedger;
import core.ledger.contract.Contract;
import core.utils.CryptoUtilities;
import core.utils.TimeUtilities;
import core.utils.protect.ProtectedDouble;
import core.utils.protect.ProtectedInteger;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter@Setter
public abstract class Transaction {

    protected String id, block, node;
    protected PublicKey sender, receiver;
    protected ProtectedDouble gasFee = new ProtectedDouble(), value = new ProtectedDouble();
    protected byte[] signature;
    protected ProtectedInteger sequence = new ProtectedInteger();
    protected Contract contract;
    protected final Map<String, String> PROPERTIES = new ConcurrentHashMap<>();
    protected List<TransactionInput> inputs;
    protected List<TransactionOutput> outputs;

    @Nullable String getHash() {
        sequence.increment();
        return CryptoUtilities.applySha256(
                CryptoUtilities.fromKey(sender) +
                        CryptoUtilities.fromKey(receiver) +
                        value + sequence
        );
    }

    protected ProtectedDouble getInputsValue() {
        ProtectedDouble total = new ProtectedDouble();
        for(TransactionInput i : inputs) {
            total.add(i.getUTXO().value().get());
        }
        return total;
    }

    public boolean processTransaction() {
        PROPERTIES.put("start", TimeUtilities.getTimeUnformatted());

        if(!verifySignature()) {
            PROPERTIES.put("status", "INVALID_SIGNATURE");
            PROPERTIES.put("timestamp", TimeUtilities.getTimeUnformatted());
            return false;
        }

        for(TransactionInput i : inputs) {
            i.setUTXO(HyperLedger.getLedger().getUTXO().get(i.getTOID()));
        }

        if(getInputsValue().get() < LedgerAttributes.getDoubleValue("MINIMUM_TRANSACTION_AMOUNT")) {
            PROPERTIES.put("status", "INSUFFICIENT_FUNDS");
            PROPERTIES.put("timestamp", TimeUtilities.getTimeUnformatted());
            return false;
        }

        double leftOver = getInputsValue().subtract(value.subtract(gasFee.get()));
        id = getHash();
        outputs.add(new TransactionOutput( this.receiver, new ProtectedDouble(value.subtract(gasFee.get())), id, contract.getAddress())); //send value to recipient
        outputs.add(new TransactionOutput( this.sender, new ProtectedDouble(leftOver) , id, contract.getAddress())); //send the left over 'change' back to sender
        outputs.add(new TransactionOutput( this.contract.getGas().getPublicKey(), new ProtectedDouble(gasFee.get()), id, contract.getAddress())); //send gas fee to contract

        for(TransactionOutput o : outputs) {
            HyperLedger.getLedger().getUTXO().put(o.PTID() , o);
        }

        for(TransactionInput i : inputs) {
            if(i.getUTXO() == null) continue; //if Transaction can't be found skip it
            HyperLedger.getLedger().getUTXO().remove(i.getUTXO().PTID());
        }

        PROPERTIES.put("status", "FINISH");
        PROPERTIES.put("timestamp", TimeUtilities.getTimeUnformatted());
        return true;
    }

    protected abstract void generateSignature(PrivateKey key);
    public abstract boolean verifySignature();
    public abstract void onComplete();

    @Override
    public String toString(){
        return "Transaction ID : " + this.id + "\n " +
                "Sender : " + CryptoUtilities.fromKey(sender) + " \n" +
                "Recipient : " + CryptoUtilities.fromKey(receiver) + " \n" +
                "Value : " + value.get() + " \n" +
                "Gas Fee : " + gasFee.get() + " \n" +
//                "Contract : " + contract.getAddress() + " \n" +
//                "Contract Hash : " + contract.getHash() + " \n" +
                "Properties : " + PROPERTIES + " \n" +
                "Block : " + block + " \n" +
                "Sequence : " + sequence.get() + " \n" +
                "Signature : " + Arrays.toString(signature) + " \n" +
                "Hash : " + getHash() + " \n" +
                "Inputs : " + inputs + " \n" +
                "Outputs : " + outputs + " \n";
    }
}
