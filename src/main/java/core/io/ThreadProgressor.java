package core.io;

import core.io.threads.PostThreadFactory;
import core.io.threads.ServerThreadFactory;

import java.util.concurrent.*;

public class ThreadProgressor {

    private final static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    private final static ForkJoinPool serverPool =
            new ForkJoinPool(Runtime.getRuntime().availableProcessors() / 4, new ServerThreadFactory(), null, true),
                                      postPool =
            new ForkJoinPool(Runtime.getRuntime().availableProcessors() / 4, new PostThreadFactory(), null, true);
    private static final LinkedBlockingQueue<Runnable> serverQueue = new LinkedBlockingQueue<>(),
                                                       postQueue = new LinkedBlockingQueue<>();

    private ThreadProgressor() {
        throw new UnsupportedOperationException(
                "This class cannot be instantiated!");
    }

    public static void submitToServer(boolean immediate, Runnable run){
        if(immediate){
            executor.execute(run);
        } else {
            serverQueue.offer(run);
        }
    }

    public static void submitToPost(Runnable run){
        postQueue.offer(run);
    }


    public static void sequence(){
        Runnable t;
        while ((t = serverQueue.poll()) != null) {
            serverPool.execute(t);
        }
        while ((t = postQueue.poll()) != null) {
            postPool.execute(t);
        }
    }

    public static void shutdown(){
        executor.shutdown();
        serverPool.shutdown();
    }


}
