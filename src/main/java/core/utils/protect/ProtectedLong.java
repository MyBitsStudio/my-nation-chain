package core.utils.protect;

import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

public class ProtectedLong implements Serializable {

    @Serial
    private static final long serialVersionUID = 8102559931435268752L;
    volatile AtomicLong value = new AtomicLong(0L);

    public ProtectedLong() {
    }

    public ProtectedLong(long value) {
        this.value.set(value);
    }

    public long get() {
        synchronized (this) {
            return value.get();
        }
    }

    public void set(long value) {
        synchronized (this) {
            this.value.set(value);
        }
    }

    public long add(long value) {
        synchronized (this) {
            return this.value.updateAndGet(v -> v + value);
        }
    }

    public long subtract(long value) {
        synchronized (this) {
            return this.value.updateAndGet(v -> v - value);
        }
    }

    public long multiply(long value) {
        synchronized (this) {
            return this.value.updateAndGet(v -> v * value);
        }
    }

    public long divide(long value) {
        synchronized (this) {
            return this.value.updateAndGet(v -> v / value);
        }
    }

    public long increment() {
        synchronized (this) {
            return this.value.updateAndGet(v -> v + 1);
        }
    }
}
