package core.io.task;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Task {

    public static final Object KEY = new Object();

    private String name;

    private int delay, countdown, runDuration;

    private boolean immediate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDelay() {
        return delay;
    }

    public int getCountdown() {
        return countdown;
    }

    public void setCountdown(int countdown) {
        this.countdown = countdown;
    }

    public int getRunDuration() {
        return runDuration;
    }

    public void setRunDuration(int runDuration) {
        this.runDuration = runDuration;
    }

    public boolean isImmediate() {
        return immediate;
    }

    public void setImmediate(boolean immediate) {
        this.immediate = immediate;
    }

    public AtomicBoolean getRunning() {
        return running;
    }

    public Runnable getOnComplete() {
        return onComplete;
    }

    public void setOnComplete(Runnable onComplete) {
        this.onComplete = onComplete;
    }

    public Object getBoss() {
        return boss;
    }

    public void setBoss(Object boss) {
        this.boss = boss;
    }

    private final AtomicBoolean running = new AtomicBoolean(true);

    private Runnable onComplete;

    private Object boss;

    public Task() {
        this("Task", 1);
    }

    public Task(String name) {
        this(name, 1);
    }

    public Task(String name, boolean immediate) {
        this(name, 1, immediate);
    }

    public Task(String name, int delay) {
        this(name, delay, false);
        this.bind(KEY);
    }

    public Task(String name, int delay, boolean immediate) {
        this(name, delay, KEY, immediate);
    }

    public Task(String name, int delay, Object key, boolean immediate) {
        this.name = name;
        this.delay = delay;
        this.countdown = delay;
        this.immediate = immediate;
        this.bind(key);
    }

    public final Task bind(Object key) {
        this.boss = key;
        return this;
    }
    public Task onStop(Runnable r) {
        this.onComplete = r;
        return this;
    }

    public void onStop() {
        if (onComplete != null) {
            onComplete.run();
        }
    }

    public void setDelay(int delay) {
        if (delay > 0)
            this.delay = delay;
    }

    public boolean sequence() {
        if (running.get()) {
            long start = System.currentTimeMillis();
            increaseRunDuration();
            if (--countdown == 0) {
                execute();
                countdown = delay;
            }
            long elapsed = System.currentTimeMillis() - start;
            //put log here
        }
        return running.get();
    }

    protected abstract void execute();

    protected void onTick() {

    }

    protected void onStart() {

    }

    public void stop() {
        running.set(false);
        onStop();
    }

    private void increaseRunDuration() {
        runDuration++;
        if (runDuration >= 6100) {
            stop();
        }
    }

}
