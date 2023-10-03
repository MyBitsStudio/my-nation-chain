package core.io.threads;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;

public class CoreEngineThreadFactory implements ThreadFactory {

    @Override
    public Thread newThread(@NotNull Runnable r) {
        final Thread thread = new Thread(r);
        thread.setName("core-engine-thread-^"+PostThreadFactory.threadCount.getAndIncrement());
        thread.setPriority(Thread.MAX_PRIORITY);
        return thread;
    }
}
