package me.tom.cascade.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.AllArgsConstructor;
import me.tom.cascade.CascadeBootstrap;
import me.tom.cascade.net.pipeline.PipelineInitializer;

@AllArgsConstructor
public class CascadeProxy {
	private static final Logger LOGGER = LoggerFactory.getLogger(CascadeProxy.class);
	
    private final int port;

    public void start() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new PipelineInitializer());

            ChannelFuture future = bootstrap.bind(port).sync();
            LOGGER.info("Proxy server started on port {}", port);
            LOGGER.debug("Server is directed at target host {} on port {}", CascadeBootstrap.CONFIG.getTargetHost(), CascadeBootstrap.CONFIG.getTargetPort());
            
            if(!CascadeBootstrap.CONFIG.isAuthVerification()) {
            	LOGGER.warn("Authentication verification is not enabled! Offline accounts are able to reach the backend!");
            }

            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}