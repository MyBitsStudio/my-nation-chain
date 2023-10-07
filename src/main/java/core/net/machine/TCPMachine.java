package core.net.machine;

import core.net.Network;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.jetbrains.annotations.NotNull;

public class TCPMachine extends NetworkMachine {

    public TCPMachine(int port){
        super(port);
        generate();
    }

    @Override
    public void generate(){
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(2);

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.SO_REUSEADDR, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(@NotNull SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();

                            //p.addLast(new ChannelsHandler(network));
                            p.addLast(new ObjectDecoder(ClassResolvers
                                    .cacheDisabled(getClass().getClassLoader())));
                            p.addLast(new ObjectEncoder());
                            //p.addLast(new TCPObjectHandler(network));

                        }
                    })
            ;

            ChannelFuture f = b.bind(port).sync();

            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
