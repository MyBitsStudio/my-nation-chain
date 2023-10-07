package core.io.threads.impl;

import core.io.task.TaskHandler;

public class ChainThread implements Runnable {
    @Override
    public void run() {
        TaskHandler.sequence();
    }
}
