package core.utils.protect;

import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReference;

public class ProtectedBoolean implements Serializable {

    @Serial
    private static final long serialVersionUID = -315696047517574775L;
    volatile AtomicReference<Boolean> value = new AtomicReference<>(false);

    public ProtectedBoolean() {
    }

    public ProtectedBoolean(boolean value) {
        this.value.set(value);
    }

    public boolean get() {
        synchronized (this) {
            return value.get();
        }
    }

    public boolean set(boolean value) {
        synchronized (this) {
            return this.value.updateAndGet(v -> value);
        }
    }

    public boolean toggle() {
        synchronized (this) {
            return this.value.updateAndGet(v -> !v);
        }
    }
}
