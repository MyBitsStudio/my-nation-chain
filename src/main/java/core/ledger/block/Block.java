package core.ledger.block;

import core.ledger.block.transaction.Transaction;
import core.utils.CryptoUtilities;
import core.utils.protect.ProtectedDouble;
import core.utils.protect.ProtectedInteger;
import core.utils.protect.ProtectedLong;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class Block implements Comparable<Block> {

    private String hash;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public String getMerkle() {
        return merkle;
    }

    public void setMerkle(String merkle) {
        this.merkle = merkle;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public ProtectedLong getTimestamp() {
        return timestamp;
    }

    public ProtectedInteger getNonce() {
        return nonce;
    }

    public ProtectedInteger getLink() {
        return link;
    }

    public ProtectedDouble getValue() {
        return value;
    }

    public BlockStage getStage() {
        return stage;
    }

    public void setStage(BlockStage stage) {
        this.stage = stage;
    }

    private String previousHash;
    private String merkle;
    private final List<Transaction> transactions = new CopyOnWriteArrayList<>();
    private final ProtectedLong timestamp = new ProtectedLong(new Date().getTime());
    private final ProtectedInteger nonce = new ProtectedInteger(), link = new ProtectedInteger();
    private final ProtectedDouble value;
    private BlockStage stage = BlockStage.WAITING;

    public Block(Block previous, double value){
        if(Objects.equals(previous, null)){
            this.previousHash = "LEGACY-BLOCK";
            this.link.set(1);
        } else {
            previousHash = previous.getPreviousHash();
            this.link.set(previous.getLink().get() + 1);
        }
        this.value = new ProtectedDouble(value);
        this.hash = calculateHash();
    }

    public Block(String hash, String previousHash, String merkle, int link, long timestamp, int nonce, double value, BlockStage stage,
                 List<Transaction> transactions) {
        this.hash = hash;
        this.previousHash = previousHash;
        this.merkle = merkle;
        this.link.set(link);
        this.timestamp.set(timestamp);
        this.nonce.set(nonce);
        this.value = new ProtectedDouble(value);
        this.stage = stage;
        this.transactions.addAll(transactions);
    }

    public String calculateHash() {
        return CryptoUtilities.applySha256(
                previousHash +
                        timestamp.get() +
                        nonce +
                        merkle
        );
    }

    public void addTransaction(Transaction transaction) {
        if(!Objects.equals(stage, BlockStage.OPEN)){
            //LogHelper.logChain("Not open ", LogHelper.ERROR, true);
            return;
        }
        if(Objects.equals(transaction, null)) {
            //LogHelper.logChain("Transaction Null ", LogHelper.ERROR, true);
            return;
        }
        if(!Objects.equals("LEGACY-BLOCK", previousHash)){
            if((!transaction.processTransaction())) {
                // LogHelper.logChain("Transaction "+transaction.getTransactionId() +" wasn't processed", LogHelper.ERROR, true);
                return;
            }
        }
        transaction.setBlock(this.hash);
        transactions.add(transaction);
    }

    public void mineBlock(int difficulty) {
        merkle = CryptoUtilities.getMerkleRoot(transactions);
        String target = CryptoUtilities.getDificultyString(difficulty);
        while(!hash.substring(0, difficulty).equals(target)) {
            nonce.increment();
            hash = calculateHash();
        }
        // LogHelper.logChain("Block Mined : "+this.getHash(), LogHelper.DEBUG, true);
    }

    @Override
    public int compareTo(@NotNull Block o) {
        return this.link.get() - o.link.get();
    }

    public void open(){
        this.stage = BlockStage.OPEN;
    }

    public Block blockIngest(){
        this.stage = BlockStage.BOOTING;

        return this;
    }

    public Block blockDigest(){
        this.stage = BlockStage.REVIEW;
        return this;
    }

    public void close(){
        this.stage = BlockStage.CLOSED;

    }

    @Override
    public String toString(){
        return "-----*----- Block "+link.get()+" -----*-----\n" +
                "Transactions : "+transactions.size()+"\n" +
                "Transaction List : "+ Arrays.toString(transactions.toArray())+"\n" +
                "Hash : "+hash+"\n" +
                "Time : "+timestamp.get()+"\n" +
                "Previous Block : "+previousHash+"\n" +
                "Balance : "+value.get()+"\n";
    }
}
