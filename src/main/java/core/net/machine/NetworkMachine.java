package core.net.machine;

public abstract class NetworkMachine {

    protected int port;

    public NetworkMachine(int port) {
        this.port = port;
    }

    public abstract void generate();
}
