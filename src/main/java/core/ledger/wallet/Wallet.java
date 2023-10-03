package core.ledger.wallet;

import core.ledger.HyperLedger;
import core.ledger.block.transaction.TransactionOutput;
import core.ledger.contract.Contract;
import core.ledger.wallet.props.Mnemonic;
import core.utils.CryptoUtilities;
import core.utils.StringUtilities;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Wallet implements Serializable {

    @Serial
    private static final long serialVersionUID = -6949400179600134713L;

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public byte[] getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(byte[] accessKey) {
        this.accessKey = accessKey;
    }

    public String[] getUnencrypted() {
        return unencrypted;
    }

    public void setUnencrypted(String[] unencrypted) {
        this.unencrypted = unencrypted;
    }

    public String getPublicAddress() {
        return publicAddress;
    }

    public List<String> getContractList() {
        return contractList;
    }

    public ConcurrentHashMap<String, TransactionOutput> getTransactions() {
        return transactions;
    }

    public List<Contract> getContracts() {
        return contracts;
    }

    public void setContracts(List<Contract> contracts) {
        this.contracts = contracts;
    }

    protected PrivateKey privateKey;
    protected PublicKey publicKey;
    protected byte[] accessKey;
    protected String[] unencrypted;
    protected final String publicAddress;
    protected final List<String> contractList = new ArrayList<>();

    protected final ConcurrentHashMap<String, TransactionOutput> transactions = new ConcurrentHashMap<>();
    protected List<Contract> contracts = new CopyOnWriteArrayList<>();

    public Wallet(){
        generateKeyPair();
        this.publicAddress = "0x"+ StringUtilities.createRandomString(16);
        contracts.add(0, HyperLedger.getLedger().getContractByName("Block Coin").get());
        unencrypted = Mnemonic.buildMnemonicPassword();
    }

    public Wallet(Contract contract){
        generateKeyPair();
        this.publicAddress = "0x"+ StringUtilities.createRandomString(16);
        contracts.add(0, contract);
    }

    public Wallet(PublicKey publicKey, PrivateKey privateKey, String publicAddress, @NotNull String accessKey,
                  List<String> contracts, Map<String, TransactionOutput> transactions){
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.publicAddress = publicAddress;
        this.accessKey = accessKey.getBytes();
        this.contractList.addAll(contracts);
        this.transactions.putAll(transactions);
    }

    public void postLoad(){

    }

    public Contract getContract(String address){
        for (Contract contract : contracts) {
            if (contract.getAddress().equals(address)) return contract;
        }
        return null;
    }

    public void addContract(Contract contract){
        if(!contracts.contains(contract))
            contracts.add(contract);
    }

    public void generateAccessKey(){
        this.accessKey = CryptoUtilities.applyECDSASig(privateKey, Arrays.toString(unencrypted));
        this.unencrypted = null;
    }

    public boolean verifyAccessKey(String[] mnemonic){
        return CryptoUtilities.verifyECDSASig(publicKey, Arrays.toString(mnemonic), accessKey);
    }

    protected void generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            keyGen.initialize(ecSpec, random);
            KeyPair keyPair = keyGen.generateKeyPair();
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public double getBalance(String contract) {
        double total = 0.000000;
        for (ConcurrentMap.Entry<String, TransactionOutput> item : HyperLedger.getLedger().getUTXO().entrySet()){
            TransactionOutput UTXO = item.getValue();
            if(UTXO.isMine(publicKey) && UTXO.contractAddress().equals(contract)) {
                transactions.putIfAbsent(UTXO.PTID(), UTXO);
                total += UTXO.value().get();
            }
        }
        return total;
    }
}
