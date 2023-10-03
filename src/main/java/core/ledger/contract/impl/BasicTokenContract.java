package core.ledger.contract.impl;

import core.ledger.contract.Contract;
import core.ledger.contract.ContractType;

import java.util.List;

public class BasicTokenContract extends Contract {
    public BasicTokenContract(String name) {
        super(name);
        type = ContractType.TOKEN;
    }

    public BasicTokenContract(String address, String name, String hash, List<String> wallets){
        super(address, name, hash, wallets);
    }


}
