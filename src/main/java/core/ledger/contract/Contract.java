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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Getter
public abstract class Contract {

    public String getAddress() {
        return address;
    }

    public String getTokenName() {
        return tokenName;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Map<String, TransactionOutput> getTransactions() {
        return transactions;
    }

    public MintWallet getMint() {
        return mint;
    }

    public void setMint(MintWallet mint) {
        this.mint = mint;
    }

    public GasWallet getGas() {
        return gas;
    }

    public void setGas(GasWallet gas) {
        this.gas = gas;
    }

    public ContractWallet getGenesis() {
        return genesis;
    }

    public void setGenesis(ContractWallet genesis) {
        this.genesis = genesis;
    }

    public ContractType getType() {
        return type;
    }

    public void setType(ContractType type) {
        this.type = type;
    }

    public List<String> getWallets() {
        return wallets;
    }

    public void setWallets(List<String> wallets) {
        this.wallets = wallets;
    }

    protected final String address, tokenName;
    protected String hash;
    protected final Map<String, TransactionOutput> transactions = new ConcurrentHashMap<>();
    protected MintWallet mint;
    protected GasWallet gas;
    protected ContractWallet genesis;
    protected ContractType type;
    protected List<String> wallets = new ArrayList<>();

    public Contract(String name){
        this.tokenName = name;
        this.address = "6xf"+ StringUtilities.createRandomString(21);
        this.genesis = new ContractWallet(this);
        this.gas = new GasWallet(this);
        this.mint = new MintWallet(this);
        generateHash();
    }

    public Contract(String address, String name, String hash, List<String> wallets){
        this.address = address;
        this.tokenName = name;
        this.hash = hash;
        this.wallets.addAll(wallets);
    }

    public void postLoad(){

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
