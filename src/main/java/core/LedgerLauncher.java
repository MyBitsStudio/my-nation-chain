package core;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import core.io.task.TaskHandler;
import core.io.task.impl.BlockGenerator;
import core.io.task.impl.ChainValidator;
import core.io.task.impl.NetworkValidator;
import core.io.threads.CoreEngineThreadFactory;
import core.io.threads.impl.ChainThread;
import core.ledger.HyperLedger;
import core.ledger.wallet.props.Mnemonic;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.concurrent.*;

public class LedgerLauncher {
    private static final ThreadFactory THREAD_FACTORY_BUILDER = new ThreadFactoryBuilder().setThreadFactory(new CoreEngineThreadFactory()).build();
    public static void main(String[] args){
        CompletableFuture<Void> future =
                CompletableFuture
                        .runAsync(LedgerLauncher::setProperties)
                        .thenRunAsync(Mnemonic::initPhrases)
                        .thenRunAsync(HyperLedger.getLedger()::initiate)
                        .thenRunAsync(() -> TaskHandler.submit(new ChainValidator()))
                        .thenRunAsync(() -> {
                            //start network keys
                        }).thenRunAsync(() -> {
                            //start network
                        })
                        .thenRunAsync(() -> TaskHandler.submit(new NetworkValidator()))
                        .thenRunAsync(() -> TaskHandler.submit(new BlockGenerator()))
                        .thenRunAsync(LedgerLauncher::startMainThread);

        try{
            future.get();
        } catch (InterruptedException | ExecutionException e){
            e.printStackTrace();
        }
    }

    private static void setProperties(){
        System.out.println("Setting properties");
        Security.setProperty("crypto.policy", "unlimited");
        Security.addProvider(new BouncyCastleProvider());
    }

    private static void startMainThread(){
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
    }
}
