package core;

import core.io.task.Chain;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LedgerLauncher {

    public final static ExecutorService LOADER = Executors.newSingleThreadExecutor();
    public static void main(String[] args){

        Chain.bound(null)
                .runFn(0, LedgerLauncher::setProperties)
                .then(3, () -> {

        });
    }

    private static void setProperties(){
        Security.setProperty("crypto.policy", "unlimited");
        Security.addProvider(new BouncyCastleProvider());
    }
}
