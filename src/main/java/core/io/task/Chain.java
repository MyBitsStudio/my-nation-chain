package core.io.task;

import java.util.function.BooleanSupplier;

public class Chain<T> {

    public static boolean DEBUG_CHAIN = false;

    /**
     * Attached Key
     */
    private T boss;

    /**
     * Name of Task
     */
    private String name;

    /**
     * Next node within the chain.
     */
    private Chain<T> nextNode;

    /**
     * Supplier with condition to stop
     */

    private BooleanSupplier cancelCondition;

    /**
     * Supplier with condition to start
     */

    private BooleanSupplier startCondition;


    private int cycleDelay = 1;

    private Object task;

    private boolean repeats = false, interrupted = false;

    public Chain<T> name(String name) {
        this.name = name;
        return this;
    }

    public static <T> Chain<T> bound(T owner) {
        Chain<T> chain = new Chain<>();
        chain.boss = owner;
        //chain.findSource();
        return chain;
    }

    public final Chain<T> cancelWhen(BooleanSupplier predicates) {
        cancelCondition = predicates;
        return this;
    }

    public Chain<T> waitUntil(int tickBetweenLoop, BooleanSupplier condition, Runnable work) {
        if (this.task != null) {
            nextNode = bound(boss);
            nextNode.task = work;
            nextNode.name = name;
            nextNode.startCondition = condition;
            nextNode.cycleDelay = tickBetweenLoop;
            nextNode.repeats = true;
            return nextNode;
        }
        startCondition = condition;
        cycleDelay = tickBetweenLoop;
        this.task = work;
        repeats = true;
        startChainExecution();
        return this;
    }

    public Chain<T> repeatingTask(int tickBetweenLoop, Runnable work) {
        if (this.task != null) {
            nextNode = bound(boss);
            nextNode.task = work;
            nextNode.name = name;
            nextNode.cycleDelay = tickBetweenLoop;
            nextNode.repeats = true;
            return nextNode;
        }
        this.task = work;
        cycleDelay = tickBetweenLoop;
        repeats = true;
        startChainExecution();
        return this;
    }

    private void startChainExecution() {
        if (cycleDelay == 0) {
            attemptWork();
        } else {
            TaskHandler.submit(new Task(name != null ? name : "", cycleDelay, false) {
                @Override
                protected void execute() {
                    attemptWork();
                    if (!repeats)
                        stop();
                }

                @Override
                public void onStop() {
                    if (interrupted) {
                        return;
                    }
                    super.onStop();
                    if (nextNode != null) {
                        nextNode.startChainExecution();
                    }
                }
            }.bind(boss));
        }
    }

    private void attemptWork() {
        if (interrupted)
            return;
        if (cancelCondition != null && cancelCondition.getAsBoolean()) {
            if (DEBUG_CHAIN) {
                System.out.println("[DEBUG_CHAIN] Cancel condition was True, stopping work for "+boss);
            }
            repeats = false; // condition to cancel was true, stop looping
            return;
        }
        if (startCondition != null) {
            if (!startCondition.getAsBoolean()) {
                if (DEBUG_CHAIN) {
                    System.out.println("[DEBUG_CHAIN] execution condition false. Won't run for " + boss);
                }
                return;
            }
            repeats = false; // condition to execute the task (aka stop looping) is true
        }
        if (DEBUG_CHAIN) {
            System.out.println("Running task for " + boss);
        }
        if (task != null) {
            if (task instanceof Runnable)
                ((Runnable)task).run();
            else {
                System.err.println("Unknown workload type: "+task.getClass());
            }
        }
    }

    public static Chain runGlobal(int startAfterTicks, Runnable work) {
        return bound(null).runFn(startAfterTicks, work);
    }

    public Chain<T> runFn(int startAfterTicks, Runnable work) {
        if (this.task != null) {
            return then(startAfterTicks, work);
        }
        cycleDelay = startAfterTicks;
        this.task = work;
        startChainExecution();
        return this;

    }

    public Chain<T> then(Runnable nextWork) {
        if (this.task == null) {
            return runFn(1, nextWork);
        }
        nextNode = bound(boss); // make a new one
        nextNode.task = nextWork; // init work
        nextNode.name = name; // re-use the name
        return nextNode;
    }

    public Chain<T> then(int startDelay, Runnable nextWork) {
        if (this.task == null) {
            return runFn(startDelay, nextWork);
        }
        nextNode = bound(boss); // make a new one
        nextNode.task = nextWork; // init work
        nextNode.name = name; // re-use the name
        nextNode.cycleDelay = startDelay;
        return nextNode;
    }

    public Chain<T> repeatIf(int tickBetweenLoop, BooleanSupplier condition/* Runnable WORK is integrated into CONDITION*/) {
        if (this.task == null) {
            task = null; // SEE CONDITION - condition IS the workload! intrgrated into one method for execute+evaluate
            name = name; // re-use the name
            cancelCondition = condition; // NOTE : this is actually a 2 in 1 version of work.
            // cancel condition will evaluate and itself is the Runnable Work.
            cycleDelay = tickBetweenLoop;
            repeats = true;
            return this;
        }
        nextNode = bound(boss); // make a new one
        nextNode.task = null; // SEE CONDITION - condition IS the workload! intrgrated into one method for execute+evaluate
        nextNode.name = name; // re-use the name
        nextNode.cancelCondition = condition; // NOTE : this is actually a 2 in 1 version of work.
        nextNode.cycleDelay = tickBetweenLoop;
        nextNode.repeats = true;
        return nextNode;
    }

}
