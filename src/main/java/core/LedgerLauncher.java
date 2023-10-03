package core;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import core.io.task.Chain;
import core.io.task.TaskHandler;
import core.io.task.impl.BlockGenerator;
import core.io.task.impl.ChainValidator;
import core.io.task.impl.NetworkValidator;
import core.io.threads.CoreEngineThreadFactory;
import core.io.threads.ServerThreadFactory;
import core.io.threads.impl.ChainThread;
import core.ledger.HyperLedger;
import core.ledger.wallet.props.Mnemonic;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.concurrent.*;

public class LedgerLauncher {

    public final static ExecutorService LOADER = Executors.newSingleThreadExecutor();
    private static final ThreadFactory THREAD_FACTORY_BUILDER = new ThreadFactoryBuilder().setThreadFactory(new CoreEngineThreadFactory()).build();
    public static void main(String[] args){

        Chain.bound(LOADER)
                .runFn(0, LedgerLauncher::setProperties)
                .then(3, Mnemonic::initPhrases)
                .then(5, HyperLedger.getLedger()::initiate)
                .then(10, () -> TaskHandler.submit(new ChainValidator()))
                .then(5, () -> {
                    //start network keys
                }).then(3, () -> {
                    //start network
                })
                .then(5, () -> TaskHandler.submit(new NetworkValidator()))
                .then(5, () -> TaskHandler.submit(new BlockGenerator()))
                .then(5, startMainThread())
        ;
    }

    private static void setProperties(){
        Security.setProperty("crypto.policy", "unlimited");
        Security.addProvider(new BouncyCastleProvider());
    }

    private static Runnable startMainThread(){
        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(THREAD_FACTORY_BUILDER);
        final ScheduledFuture<?> handle = executorService.scheduleAtFixedRate(new ChainThread(), 0,
                LedgerAttributes.getIntValue("ENGINE_PROCESSING_CYCLE_RATE"),
                TimeUnit.MILLISECONDS);
        final Thread exceptionHandlerThread = new Thread(() -> {
            try {
                handle.get();
            } catch (ExecutionException | InterruptedException e){

            }
        }, "Core Thread Exception Handler");
        exceptionHandlerThread.start();
        return null;
    }
}
