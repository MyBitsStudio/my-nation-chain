package core.utils.data.serial;

import core.ledger.block.Block;
import core.ledger.block.transaction.TransactionOutput;
import core.ledger.contract.Contract;
import core.ledger.wallet.Wallet;
import core.utils.data.ChainSave;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SerialSave implements ChainSave {

    @Override
    public void saveBlock(Block block) {
        synchronized (blockLocation()){
            try{
                storeSerializableClass(block, new File(blockLocation() + block.getLink().get() + ".block"));
            }catch(IOException i){
                i.printStackTrace();
            }
        }
    }

    @Override
    public void saveContract(Contract contract) {
        synchronized (contractLocation()){
            try{
                storeSerializableClass(contract, new File(contractLocation() + contract.getAddress() + ".contract"));
            }catch(IOException i){
                i.printStackTrace();
            }
        }
    }

    @Override
    public void saveWallet(Wallet wallet) {
        synchronized (walletLocation()){
            try{
                storeSerializableClass(wallet, new File(walletLocation() + wallet.getPublicAddress() + ".wallet"));
            }catch(IOException i){
                i.printStackTrace();
            }
        }
    }

    @Override
    public void saveUTXO(Map<String, TransactionOutput> UTXO) {
        synchronized (UTXOLocation()+"UTXO.utxo"){
            try{
                storeSerializableClass((Serializable) UTXO, new File(UTXOLocation() + "UTXO.utxo"));
            }catch(IOException i){
                i.printStackTrace();
            }
        }
    }

    @Override
    public Block loadBlock(String block) {
        try(FileInputStream fileInputStream = new FileInputStream(blockLocation() + block + ".block");
            ObjectInputStream in = new ObjectInputStream(fileInputStream)) {
            return (Block) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Contract loadContract(String contract) {
        try(FileInputStream fileInputStream = new FileInputStream(contractLocation() + contract + ".contract");
            ObjectInputStream in = new ObjectInputStream(fileInputStream)) {
            return (Contract) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Wallet loadWallet(String wallet) {
        try(FileInputStream fileInputStream = new FileInputStream(walletLocation() + wallet + ".wallet");
            ObjectInputStream in = new ObjectInputStream(fileInputStream)) {
            return (Wallet) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map<String, TransactionOutput> loadUTXO() {
        try(FileInputStream fileInputStream = new FileInputStream(UTXOLocation() + "UTXO.utxo");
            ObjectInputStream in = new ObjectInputStream(fileInputStream)) {
            return (Map<String, TransactionOutput>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Block> loadChain() {
        List<Block> blocks = new ArrayList<>();
        File[] files = new File(blockLocation()).listFiles();
        if(files != null){
            for(File file : files){
                try(FileInputStream fileInputStream = new FileInputStream(file);
                    ObjectInputStream in = new ObjectInputStream(fileInputStream)) {
                    blocks.add((Block) in.readObject());
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return blocks;
    }

    @Override
    public List<Contract> loadContracts() {
        List<Contract> contracts = new ArrayList<>();
        File[] files = new File(contractLocation()).listFiles();
        if(files != null){
            for(File file : files){
                try(FileInputStream fileInputStream = new FileInputStream(file);
                    ObjectInputStream in = new ObjectInputStream(fileInputStream)) {
                    contracts.add((Contract) in.readObject());
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return contracts;
    }

    @Override
    public List<Wallet> loadWallets() {
        List<Wallet> wallets = new ArrayList<>();
        File[] files = new File(walletLocation()).listFiles();
        if(files != null){
            for(File file : files){
                try(FileInputStream fileInputStream = new FileInputStream(file);
                    ObjectInputStream in = new ObjectInputStream(fileInputStream)) {
                    wallets.add((Wallet) in.readObject());
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public String blockLocation() {
        return ".data/serial/blocks/";
    }

    @Override
    public String contractLocation() {
        return ".data/serial/contracts/";
    }

    @Override
    public String walletLocation() {
        return ".data/serial/wallets/";
    }

    @Override
    public String UTXOLocation() {
        return ".data/serial/";
    }

    private void storeSerializableClass(Serializable o, File f)
            throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));
        out.writeObject(o);
        out.flush();
        out.close();
    }
}
