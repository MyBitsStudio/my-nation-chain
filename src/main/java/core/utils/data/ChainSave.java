package core.utils.data;

import core.ledger.block.Block;
import core.ledger.block.transaction.TransactionOutput;
import core.ledger.contract.Contract;
import core.ledger.wallet.Wallet;

import java.util.List;
import java.util.Map;

public interface ChainSave {


    void saveBlock(Block block);
    void saveContract(Contract contract);
    void saveWallet(Wallet wallet);
    void saveUTXO(Map<String, TransactionOutput> UTXO);

    Block loadBlock(String block);
    Contract loadContract(String contract);
    Wallet loadWallet(String wallet);
    Map<String, TransactionOutput> loadUTXO();
    List<Block> loadChain();
    List<Contract> loadContracts();
    List<Wallet> loadWallets();

    String blockLocation();
    String contractLocation();
    String walletLocation();
    String UTXOLocation();
}
