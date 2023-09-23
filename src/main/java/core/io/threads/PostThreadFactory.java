package core.io.threads;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.atomic.AtomicLong;

public class PostThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory {

    public static AtomicLong threadCount = new AtomicLong(0);
    @Override
    public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
        final ForkJoinWorkerThread thread = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
        thread.setName("post-thread-^"+threadCount.getAndIncrement());
        return thread;
    }
}
