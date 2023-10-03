package core.tools;

import core.ledger.block.Block;
import core.ledger.block.transaction.TransactionOutput;
import core.ledger.block.transaction.impl.SystemTransaction;
import core.ledger.contract.Contract;
import core.ledger.contract.impl.BasicTokenContract;
import core.ledger.wallet.impl.ContractWallet;
import core.utils.data.ChainSaver;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChainStarter {

    public static void main(String... args){
        Security.setProperty("crypto.policy", "unlimited");
        Security.addProvider(new BouncyCastleProvider());

        Contract genesis = new BasicTokenContract("Block Coin");

        ContractWallet wallet = new ContractWallet(genesis);
        SystemTransaction transaction = new SystemTransaction(wallet.getPublicKey(), genesis.getGenesis().getPublicKey(), 6000000000000.0000001d, null, genesis);
        transaction.generateSignature(wallet.getPrivateKey());
        transaction.setId("0");
        transaction.setContract(genesis);
        transaction.getOutputs().add(new TransactionOutput(transaction.getReceiver(), transaction.getValue(), transaction.getId(), transaction.getContract().getAddress()));

        Map<String, TransactionOutput> UTXO = new ConcurrentHashMap<>();
        UTXO.put(transaction.getOutputs().get(0).PTID(), transaction.getOutputs().get(0));

        Block genesisBlock = new Block(null, 0.000000d);
        genesisBlock.open();
        genesisBlock.addTransaction(transaction);
        genesisBlock.mineBlock(0);
        genesisBlock.close();
        Block newBlock = new Block(genesisBlock, 0.000001d);
        newBlock.open();
        newBlock.mineBlock(0);

        System.out.println("Genesis Block: " + genesisBlock);
        System.out.println("Block 1: " + newBlock);

        ChainSaver.getInstance().saveBlock(genesisBlock);
        ChainSaver.getInstance().saveBlock(newBlock);
        ChainSaver.getInstance().saveContract(genesis);
        ChainSaver.getInstance().saveWallet(wallet);
        ChainSaver.getInstance().saveUTXO(UTXO);

    }
}
