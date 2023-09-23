package core.ledger.contract;

import core.ledger.HyperLedger;
import core.ledger.block.transaction.TransactionOutput;
import core.ledger.wallet.Wallet;
import core.ledger.wallet.impl.ContractWallet;
import core.ledger.wallet.impl.GasWallet;
import core.ledger.wallet.impl.MintWallet;
import core.utils.CryptoUtilities;
import core.utils.StringUtilities;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Getter
public abstract class Contract {

    private final String address, tokenName;
    private String hash;
    private final Map<String, TransactionOutput> transactions = new ConcurrentHashMap<>();
    private final MintWallet mint;
    private final GasWallet gas;
    private final ContractWallet genesis;

    public Contract(String name){
        this.tokenName = name;
        this.address = "6xf"+ StringUtilities.createRandomString(21);
        this.genesis = new ContractWallet(this);
        this.gas = new GasWallet(this);
        this.mint = new MintWallet(this);
        generateHash();
    }

    public void generateHash(){
        this.hash = CryptoUtilities.applySha256(
                address + genesis.getPublicAddress() +
                        StringUtilities.createRandomString(12) + tokenName
        );
    }

    public double getBalance() {
        double total = 0.000000D;
        for (ConcurrentMap.Entry<String, TransactionOutput> item : HyperLedger.getLedger().getUTXO().entrySet()){
            TransactionOutput UTXO = item.getValue();
            if(UTXO.contractAddress().equals(address)) {
                transactions.putIfAbsent(UTXO.PTID(),UTXO);
                total += UTXO.value().get();
            }
        }
        return total;
    }

}
