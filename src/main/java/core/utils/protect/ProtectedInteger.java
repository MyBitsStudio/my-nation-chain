package core.utils.protect;

import java.util.concurrent.atomic.AtomicInteger;

public class ProtectedInteger {

    volatile AtomicInteger value = new AtomicInteger(0);

    public ProtectedInteger() {
    }

    public ProtectedInteger(int value) {
        this.value.set(value);
    }

    public int get() {
        synchronized (this) {
            return value.get();
        }
    }

    public double set(int value){
        synchronized (this) {
            return this.value.updateAndGet(v -> value);
        }
    }

    public int add(int value) {
        synchronized (this) {
            return this.value.updateAndGet(v -> v + value);
        }
    }

    public int subtract(int value) {
        synchronized (this) {
            return this.value.updateAndGet(v -> v - value);
        }
    }

    public int multiply(int value) {
        synchronized (this) {
            return this.value.updateAndGet(v -> v * value);
        }
    }

    public int divide(int value) {
        synchronized (this) {
            return this.value.updateAndGet(v -> v / value);
        }
    }

    public int increment() {
        synchronized (this) {
            return this.value.updateAndGet(v -> v + 1);
        }
    }
}
