package core.net.machine.handler;

import core.net.Network;
import core.net.utils.NetworkConstants;
import core.net.utils.Serials;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.jetbrains.annotations.NotNull;

public class TCPChannelHandler extends ChannelInboundHandlerAdapter implements ChannelHandler {

    @Override
    public void channelActive(final @NotNull ChannelHandlerContext ctx) {
        if(verifyChannel(ctx)){
            String serial = Serials.serial();

        } else {
            ctx.channel().close();
        }
    }

    @Override
    public void handlerRemoved(final @NotNull ChannelHandlerContext ctx) {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, @NotNull Throwable cause) {

    }

    private boolean verifyChannel(ChannelHandlerContext ctx){
        for(String hosts : NetworkConstants._HOSTS){
            if(ctx.channel().remoteAddress().toString().contains(hosts)){
                return true;
            }
        }
        return false;
    }
}
