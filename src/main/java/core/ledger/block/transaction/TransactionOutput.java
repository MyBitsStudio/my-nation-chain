package core.ledger.block.transaction;

import core.utils.CryptoUtilities;
import core.utils.protect.ProtectedDouble;

import java.io.Serializable;
import java.security.PublicKey;

public record TransactionOutput(PublicKey receiver, ProtectedDouble value, String PTID, String contractAddress) implements Serializable {
    public boolean isMine(PublicKey publicKey) {
        return CryptoUtilities.fromKey(receiver).equals(CryptoUtilities.fromKey(publicKey));
    }
}
