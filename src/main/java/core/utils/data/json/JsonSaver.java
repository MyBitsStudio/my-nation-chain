package core.utils.data.json;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import core.io.ThreadProgressor;
import core.ledger.block.Block;
import core.ledger.block.BlockStage;
import core.ledger.block.transaction.Transaction;
import core.ledger.block.transaction.TransactionOutput;
import core.ledger.contract.Contract;
import core.ledger.contract.impl.BasicTokenContract;
import core.ledger.wallet.Wallet;
import core.ledger.wallet.impl.ContractWallet;
import core.ledger.wallet.impl.GasWallet;
import core.ledger.wallet.impl.MintWallet;
import core.utils.CryptoUtilities;
import core.utils.data.ChainSave;
import core.utils.exception.json.*;
import core.utils.exception.*;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Type;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class JsonSaver implements ChainSave {

    protected final Gson builder = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .setLenient()
            .create();

    protected JsonObject object;
    protected JsonArray array = new JsonArray();

    @Override
    public void saveBlock(@NotNull Block block) {
        object = new JsonObject();
        synchronized (new File(blockLocation()+block.getLink().get())){
            object.addProperty("hash", block.getHash());
            object.addProperty("previousHash", block.getPreviousHash());
            object.addProperty("merkle", block.getMerkle());
            object.addProperty("link", block.getLink().get());
            object.addProperty("timestamp", block.getTimestamp().get());
            object.addProperty("nonce", block.getNonce().get());
            object.addProperty("value", block.getValue().get());
            object.addProperty("stage", block.getStage().name());


            ThreadProgressor.submitToServer(true, () -> {
                try (FileWriter file = new FileWriter(blockLocation()+block.getLink().get()+".json")) {
                    file.write(builder.toJson(object));
                    file.flush();
                } catch (IOException e) {
                    throw new JSONBlockSaveException(e.getMessage(), e);
                }
            });
        }
    }

    @Override
    public void saveContract(@NotNull Contract contract) {
        object = new JsonObject();
        synchronized (new File(contractLocation()+contract.getAddress())){
            object.addProperty("address", contract.getAddress());
            object.addProperty("type", contract.getType().name());
            object.addProperty("tokenName", contract.getTokenName());
            object.addProperty("hash", contract.getHash());
            object.addProperty("genesis", contract.getGenesis().getPublicAddress());
            object.addProperty("gasProvider", contract.getGas().getPublicAddress());
            object.addProperty("minting", contract.getMint().getPublicAddress());
            object.add("transactions", builder.toJsonTree(contract.getTransactions()));

            ThreadProgressor.submitToServer(true, () -> {
                try (FileWriter file = new FileWriter(contractLocation()+contract.getAddress()+".json")) {
                    file.write(builder.toJson(object));
                    file.flush();
                } catch (IOException e) {
                    throw new JSONContractSaveException(e.getMessage(), e);
                }
            });
        }
    }

    @Override
    public void saveWallet(@NotNull Wallet wallet) {
        if(!new File(walletLocation()+wallet.getPublicAddress()+"/").exists()){
            new File(walletLocation()+wallet.getPublicAddress()+"/").mkdirs();
        }
        object = new JsonObject();
        synchronized (new File(walletLocation()+wallet.getPublicAddress()+"/")){
            object.addProperty("type", walletType(wallet));
            object.addProperty("publicAddress", wallet.getPublicAddress());
            object.addProperty("accessKey", Arrays.toString(wallet.getAccessKey()));
            List<String> contracts = new CopyOnWriteArrayList<>();
            wallet.getContracts().forEach(contract -> contracts.add(contract.getAddress()));
            object.add("contracts", builder.toJsonTree(contracts));

            ThreadProgressor.submitToServer(true, () -> {

                saveKeys(wallet.getPublicKey(), wallet.getPrivateKey(), wallet.getPublicAddress());

                try (FileWriter file = new FileWriter(walletLocation()+wallet.getPublicAddress()+".json")) {
                    file.write(builder.toJson(object));
                    file.flush();
                } catch (IOException e) {
                    throw new JSONWalletSaveException(e.getMessage(), e);
                }
            });
        }
    }

    @Override
    public void saveUTXO(Map<String, TransactionOutput> UTXO) {
        object = new JsonObject();
        synchronized (new File(UTXOLocation()+"UTXO.json")){
            object.add("UTXO", builder.toJsonTree(UTXO));

            ThreadProgressor.submitToServer(true, () -> {
                try (FileWriter file = new FileWriter(UTXOLocation()+"UTXO.json")) {
                    file.write(builder.toJson(object));
                    file.flush();
                } catch (IOException e) {
                    throw new JSONUTXOSaveException(e.getMessage(), e);
                }
            });
        }
    }

    @Override
    public Block loadBlock(String block) {
        synchronized (new File(blockLocation()+block)){
            try (FileReader fileReader = new FileReader(blockLocation()+block)) {
                object = builder.fromJson(fileReader, JsonObject.class);
            } catch (Exception e) {
                throw new JSONBlockLoadException(e.getMessage(), e);
            }

            if(object.has("hash") && object.has("previousHash") && object.has("merkle") && object.has("link") && object.has("timestamp") && object.has("nonce") && object.has("value") && object.has("stage") && object.has("transactions")){
                Type listType = new TypeToken<List<Transaction>>(){}.getType();
                return new Block(object.get("hash").getAsString(), object.get("previousHash").getAsString(), object.get("merkle").getAsString(), object.get("link").getAsInt(), object.get("timestamp").getAsLong(), object.get("nonce").getAsInt(), object.get("value").getAsInt(), BlockStage.valueOf(object.get("stage").getAsString()), builder.fromJson(object.get("transactions"), listType));
            }
        }
        return null;
    }

    @Override
    public Contract loadContract(String contract) {
        synchronized (new File(contractLocation()+contract)){
            try (FileReader fileReader = new FileReader(contractLocation()+contract)) {
                object = builder.fromJson(fileReader, JsonObject.class);
            } catch (Exception e) {
                throw new JSONBlockLoadException(e.getMessage(), e);
            }

            if(object.has("type") && object.has("address") && object.has("tokenName") && object.has("hash") && object.has("genesis") && object.has("gasProvider") && object.has("minting")){
                List<String> wallets = new CopyOnWriteArrayList<>();
                wallets.add(object.get("genesis").getAsString());
                wallets.add(object.get("gasProvider").getAsString());
                wallets.add(object.get("minting").getAsString());
                return switch(object.get("type").getAsString()){
                    case "TOKEN" -> new BasicTokenContract(object.get("address").getAsString(), object.get("tokenName").getAsString(), object.get("hash").getAsString(), wallets);
                    default -> null;
                };
            }
        }
        return null;
    }

    @Override
    public Wallet loadWallet(String wallet) {
        synchronized (new File(walletLocation()+wallet)){
            try (FileReader fileReader = new FileReader(walletLocation()+wallet)) {
                object = builder.fromJson(fileReader, JsonObject.class);
            } catch (Exception e) {
                throw new JSONBlockLoadException(e.getMessage(), e);
            }

            if(object.has("type") && object.has("publicAddress") && object.has("accessKey") && object.has("transactions") && object.has("contracts")){
                KeyPair pair = loadKeys(walletLocation()+wallet);
                Type contracts = new TypeToken<List<String>>(){}.getType(), transactions = new TypeToken<Map<String, TransactionOutput>>(){}.getType();
                switch(object.get("type").getAsString()){
                    case "contract" -> {
                        return new ContractWallet(pair.getPublic(), pair.getPrivate(), object.get("publicAddress").getAsString(), object.get("accessKey").getAsString(), builder.fromJson(object.get("contracts"), contracts), builder.fromJson(object.get("transactions"), transactions));
                    }
                    case "gas" -> {
                        return new GasWallet(pair.getPublic(), pair.getPrivate(), object.get("publicAddress").getAsString(), object.get("accessKey").getAsString(), builder.fromJson(object.get("contracts"), contracts), builder.fromJson(object.get("transactions"), transactions));
                    }
                    case "mint" -> {
                        return new MintWallet(pair.getPublic(), pair.getPrivate(), object.get("publicAddress").getAsString(), object.get("accessKey").getAsString(), builder.fromJson(object.get("contracts"), contracts), builder.fromJson(object.get("transactions"), transactions));
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Map<String, TransactionOutput> loadUTXO() {
        synchronized (new File(UTXOLocation()+"UTXO.json")){
            try (FileReader fileReader = new FileReader(UTXOLocation()+"UTXO.json")) {
                object = builder.fromJson(fileReader, JsonObject.class);
            } catch (Exception e) {
                throw new JSONBlockLoadException(e.getMessage(), e);
            }

            if(object.has("UTXO")){
                Type transactions = new TypeToken<Map<String, TransactionOutput>>(){}.getType();
                return builder.fromJson(object.get("UTXO"), transactions);
            }
        }
        return null;
    }

    @Override
    public String blockLocation() {
        return ".data/json/blocks/";
    }

    @Override
    public String contractLocation() {
        return ".data/json/contracts/";
    }

    @Override
    public String walletLocation() {
        return ".data/json/wallets/";
    }

    @Override
    public String UTXOLocation() {
        return ".data/json/";
    }


    private void saveKeys(@NotNull PublicKey pKey, PrivateKey privateK, String address){
        X509EncodedKeySpec spec = new X509EncodedKeySpec(pKey.getEncoded());
        try(FileWriter file = new FileWriter(walletLocation()+address+"/publicKey.key")){
            file.write(builder.toJson(spec));
            file.flush();
        } catch (IOException e) {
            throw new KeySavingException(e.getMessage(), e);
        }

        spec = new X509EncodedKeySpec(privateK.getEncoded());
        try(FileWriter file = new FileWriter(walletLocation()+address+"/privateKey.key")){
            file.write(builder.toJson(spec));
            file.flush();
        } catch (IOException e) {
            throw new KeySavingException(e.getMessage(), e);
        }
    }

    @org.jetbrains.annotations.Contract("_ -> new")
    private @NotNull KeyPair loadKeys(String path){
        byte[] encodedBytes, encodedPrivate;
        try(FileInputStream stream = new FileInputStream(path+"/publicKey.key")){
            encodedBytes = new byte[stream.available()];
            stream.read(encodedBytes);
        } catch(IOException e){
            throw new KeyLoadingException(e.getMessage(), e);
        }

        try(FileInputStream stream = new FileInputStream(path+"/privateKey.key")){
            encodedPrivate = new byte[stream.available()];
            stream.read(encodedPrivate);
        } catch(IOException e){
            throw new KeyLoadingException(e.getMessage(), e);
        }

        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedBytes);
        X509EncodedKeySpec privateKeySpec = new X509EncodedKeySpec(encodedPrivate);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("ECDSA", "BA");
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
            PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
            return new KeyPair(publicKey, privateKey);
        } catch (Exception e) {
            throw new KeyLoadingException(e.getMessage(), e);
        }
    }

    @org.jetbrains.annotations.Contract(pure = true)
    private @NotNull String walletType(Wallet wallet){
        if(wallet instanceof ContractWallet){
            return "contract";
        } else if(wallet instanceof GasWallet){
            return "gas";
        } else if(wallet instanceof MintWallet){
            return "mint";
        }
        return "";
    }
}
