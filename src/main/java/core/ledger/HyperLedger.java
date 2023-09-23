package core.ledger;

import core.ledger.block.Block;
import core.ledger.block.transaction.Transaction;
import core.ledger.block.transaction.TransactionOutput;
import core.ledger.contract.Contract;
import core.ledger.wallet.Wallet;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
public class HyperLedger {

    private final List<Block> CHAIN = new CopyOnWriteArrayList<>();
    private final List<Contract> CONTRACTS = new CopyOnWriteArrayList<>();
    private final List<Wallet> WALLETS = new CopyOnWriteArrayList<>();
    private final Map<String, TransactionOutput> UTXO = new ConcurrentHashMap<>();
    private final AtomicBoolean booting = new AtomicBoolean(true);

    private static HyperLedger singleton;

    public static HyperLedger getLedger(){
        if(Objects.equals(singleton, null)){
            singleton = new HyperLedger();
        }
        return singleton;
    }

    public List<Block> getChain() {
        synchronized (CHAIN) {
            return CHAIN;
        }}
    public List<Wallet> getWallets() {
        synchronized (WALLETS) {
            return WALLETS;
        }}
    public Block getLatestBlock(){
        synchronized (CHAIN) {
            return CHAIN.get(CHAIN.size() - 1);
        }}
    public List<Contract> getContracts() {
        synchronized (CONTRACTS) {
            return CONTRACTS;
        }}


    public Optional<Contract> getContractByName(String name){
        synchronized (CONTRACTS) {
            for (Contract contract : CONTRACTS) {
                if (Objects.equals(contract.getTokenName(), name)) {
                    return Optional.of(contract);
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Wallet> getWallet(String address){
        synchronized (WALLETS) {
            for (Wallet wallet : WALLETS) {
                if (Objects.equals(wallet.getPublicAddress(), address)) {
                    return Optional.of(wallet);
                }
            }
        }
        return Optional.empty();
    }

    public void initiate(){

    }

    public void addTransactionToChain(@NotNull Transaction transaction){
        Block block = getLatestBlock();
        CompletableFuture<Void> future =
                CompletableFuture.runAsync(() ->
                                block.addTransaction(transaction))
                        .thenRun(() -> block.mineBlock(0))
                        .thenRun(transaction::onComplete);
        future.join();
    }
}
