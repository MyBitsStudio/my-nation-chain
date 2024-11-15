package core.ledger;

import core.ledger.block.Block;
import core.ledger.block.transaction.Transaction;
import core.ledger.block.transaction.TransactionOutput;
import core.ledger.contract.Contract;
import core.ledger.wallet.Wallet;
import core.utils.data.ChainSaver;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

public class HyperLedger {

    private final List<Block> CHAIN = new CopyOnWriteArrayList<>();

    public List<Block> getCHAIN() {
        return CHAIN;
    }

    public List<Contract> getCONTRACTS() {
        return CONTRACTS;
    }

    public List<Wallet> getWALLETS() {
        return WALLETS;
    }

    public Map<String, TransactionOutput> getUTXO() {
        return UTXO;
    }

    public AtomicBoolean getBooting() {
        return booting;
    }

    public static HyperLedger getSingleton() {
        return singleton;
    }

    public static void setSingleton(HyperLedger singleton) {
        HyperLedger.singleton = singleton;
    }

    private final List<Contract> CONTRACTS = new CopyOnWriteArrayList<>();
    private final List<Wallet> WALLETS = new CopyOnWriteArrayList<>();
    private Map<String, TransactionOutput> UTXO = new ConcurrentHashMap<>();
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
        System.out.println("Initiating HyperLedger");
        synchronized (booting){
                CompletableFuture<Void> future =
                        CompletableFuture.runAsync(() -> UTXO = ChainSaver.getInstance().loadUTXO())
                        .thenRunAsync(() -> {
                            List<Block> blocks = ChainSaver.getInstance().loadChain();
                            if(blocks != null) CHAIN.addAll(blocks);
                            Collections.sort(CHAIN);
                            for(Block block : CHAIN){
                                System.out.println(block);
                            }
                        })
                        .thenRunAsync(() -> {
                            List<Contract> contracts = ChainSaver.getInstance().loadContracts();
                            if(contracts != null) CONTRACTS.addAll(contracts);
                            for(Contract contract : CONTRACTS){
                                System.out.println(contract);
                            }
                        })
                        .thenRunAsync(() -> {
                            List<Wallet> wallets = ChainSaver.getInstance().loadWallets();
                            if(wallets != null) WALLETS.addAll(wallets);
                            for(Wallet wallet : WALLETS){
                                System.out.println(wallet);
                            }
                        })
                        ;
                try{
                    future.get();
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        booting.set(false);
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
