package core.ledger.contract.impl;

import core.ledger.contract.Contract;
import core.ledger.contract.ContractType;

import java.io.Serial;
import java.util.List;

public class BasicTokenContract extends Contract {
    @Serial
    private static final long serialVersionUID = 8537552226940299540L;

    public BasicTokenContract(String name) {
        super(name);
        type = ContractType.TOKEN;
    }

    public BasicTokenContract(String address, String name, String hash, List<String> wallets){
        super(address, name, hash, wallets);
    }


}
