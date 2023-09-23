package core.utils.protect;

import java.util.concurrent.atomic.AtomicReference;

public class ProtectedDouble {

    volatile AtomicReference<Double> value = new AtomicReference<>(0.000000D);

    public ProtectedDouble() {
    }

    public ProtectedDouble(double value) {
        this.value.set(value);
    }

    public double get() {
        synchronized (this) {
            return value.get();
        }
    }

    public double set(double value){
        synchronized (this) {
            return this.value.updateAndGet(v -> value);
        }
    }

    public double add(double value) {
        synchronized (this) {
            return this.value.updateAndGet(v -> v + value);
        }
    }

    public double subtract(double value) {
        synchronized (this) {
            return this.value.updateAndGet(v -> v - value);
        }
    }

    public double multiply(double value) {
        synchronized (this) {
            return this.value.updateAndGet(v -> v * value);
        }
    }

    public double divide(double value) {
        synchronized (this) {
            return this.value.updateAndGet(v -> v / value);
        }
    }

    public double increment() {
        synchronized (this) {
            return this.value.updateAndGet(v -> v + 1);
        }
    }
}
