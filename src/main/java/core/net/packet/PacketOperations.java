package core.net.packet;

public enum PacketOperations {

    VERIFY_REQUEST(0x521),
    ;

    private final int opcode;

    PacketOperations(int opcode){
        this.opcode = opcode;
    }
}
