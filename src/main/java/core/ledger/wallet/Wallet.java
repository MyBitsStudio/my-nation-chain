package core.ledger.wallet;

import core.ledger.HyperLedger;
import core.ledger.block.transaction.TransactionOutput;
import core.ledger.contract.Contract;
import core.ledger.wallet.props.Mnemonic;
import core.utils.CryptoUtilities;
import core.utils.StringUtilities;
import lombok.Getter;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public abstract class Wallet {

    protected PrivateKey privateKey;
    protected PublicKey publicKey;
    protected byte[] accessKey;
    protected String[] unencrypted;
    protected final String publicAddress;

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
