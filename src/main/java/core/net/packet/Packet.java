package core.net.packet;

import core.utils.protect.ProtectedBoolean;
import lombok.Getter;

import java.nio.channels.Channel;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Packet {

    @Getter
    protected final PacketOperations opcode;
    protected List<PacketProperties> properties = new CopyOnWriteArrayList<>();
    protected ProtectedBoolean active = new ProtectedBoolean(true);
    protected final Channel channel;

    public Packet(PacketOperations opcode, Channel channel) {
        this.opcode = opcode;
        this.channel = channel;
    }

    public abstract void handle();
    public abstract int response();
    public abstract String message();

    public boolean invalid(){ return !properties.isEmpty(); }
}
