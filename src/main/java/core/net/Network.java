package core.net;

import core.net.machine.NetworkMachine;
import core.utils.MathUtilities;
import core.utils.protect.ProtectedInteger;

import java.math.BigInteger;
import java.nio.channels.Channel;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Network {

    private static Network network;

    public static Network singleton(){
        if(network == null) network = new Network();
        return network;
    }

    private final Map<Channel, List<String>> channelMap = new ConcurrentHashMap<>();
    private final ProtectedInteger radix = new ProtectedInteger(MathUtilities.random(13, 765));
    private final List<NetworkMachine> machines = new CopyOnWriteArrayList<>();

    private Network(){}

    public String radix(){return new BigInteger(""+radix.get()).toString(6);}
    public void increment(){
        radix.increment();
    }

    public void start(){

    }



}
