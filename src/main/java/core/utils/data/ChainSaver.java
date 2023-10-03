package core.utils.data;

import core.LedgerAttributes;
import core.ledger.block.Block;
import core.ledger.block.transaction.TransactionOutput;
import core.ledger.contract.Contract;
import core.ledger.wallet.Wallet;
import core.utils.data.json.JsonSaver;

import java.util.Map;

public class ChainSaver {

    private static ChainSaver instance;

    public static ChainSaver getInstance(){
        if(instance == null){
            instance = new ChainSaver();
        }
        return instance;
    }

    private ChainSave chainSave;

    public ChainSaver(){
        assign();
    }

    private void assign(){
        String className = LedgerAttributes.getStringValue("CHAIN_SAVING_CYCLE");
        switch (className) {
            case "XML" -> {
            }
            case "YAML" -> {
            }
            case "SERIAL" -> {
            }
            default -> chainSave = new JsonSaver();
        }
    }

    public void saveBlock(Block block){
        chainSave.saveBlock(block);
    }

    public void saveContract(Contract contract){
        chainSave.saveContract(contract);
    }

    public void saveWallet(Wallet wallet){
        chainSave.saveWallet(wallet);
    }

    public void saveUTXO(Map<String, TransactionOutput> UTXO){
        chainSave.saveUTXO(UTXO);
    }

    public Block loadBlock(String block){
        return chainSave.loadBlock(block);
    }

    public Contract loadContract(String contract){
        return chainSave.loadContract(contract);
    }

    public Wallet loadWallet(String wallet){
        return chainSave.loadWallet(wallet);
    }

    public Map<String, TransactionOutput> loadUTXO(){
        return chainSave.loadUTXO();
    }

}
