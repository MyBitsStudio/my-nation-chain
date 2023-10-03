package core.utils.data.json;

import core.ledger.block.transaction.TransactionInput;
import core.ledger.block.transaction.TransactionOutput;

import java.util.List;
import java.util.Map;

public record TransactionRecord(String id, String block, String node,
                                String sender, String receiver, double gasFee, double value, byte[] signature,
                                int sequence, String contract, Map<String, String> properties,
                                List<TransactionInput> inputs, List<TransactionOutput> outputs) {
}
