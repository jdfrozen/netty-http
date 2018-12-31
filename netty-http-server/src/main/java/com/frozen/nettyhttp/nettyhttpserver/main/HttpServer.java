package com.frozen.nettyhttp.nettyhttpserver.main;

import com.frozen.nettyhttp.nettyhttpserver.handler.HttpServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther: 冯默风
 * @Date: 2018/12/31 19:37
 * @Description:
 */
public class HttpServer {
    private static Logger log = LoggerFactory.getLogger(HttpServer.class);

    public void run() throws Exception{
        EventLoopGroup bossGroup = new NioEventLoopGroup(10);
        EventLoopGroup workGroup = new NioEventLoopGroup(20);
        log.info("服务器启动成功，监听端口为："+9999);
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workGroup).channel(NioServerSocketChannel.class);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {

                }
            });
            serverBootstrap.option(ChannelOption.SO_BACKLOG,128);
            serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE,true);
            ChannelFuture future = serverBootstrap.bind(9999).sync();
            future.channel().closeFuture().sync();
        }finally {
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
