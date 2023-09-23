package core.ledger.block;

import core.ledger.block.transaction.Transaction;
import core.utils.CryptoUtilities;
import core.utils.protect.ProtectedDouble;
import core.utils.protect.ProtectedInteger;
import core.utils.protect.ProtectedLong;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class Block implements Comparable<Block> {

    private String hash, previousHash, merkle;
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
}
