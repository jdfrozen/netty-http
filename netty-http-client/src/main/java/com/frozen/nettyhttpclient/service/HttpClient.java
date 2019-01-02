package com.frozen.nettyhttpclient.service;

import com.frozen.nettyhttp.constants.info.HostInfo;
import com.frozen.nettyhttpclient.handler.HttpClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther: Frozen
 * @Date: 2019/1/2 14:55
 * @Description: netty 客户端服务
 */
public class HttpClient {

    private static final Logger log = LoggerFactory.getLogger(HttpClient.class);
    public void run() throws Exception{
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap client = new Bootstrap();
            client.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new HttpRequestDecoder());
                    socketChannel.pipeline().addLast(new HttpRequestEncoder());
                    socketChannel.pipeline().addLast(new HttpClientHandler());
                }
            });
            ChannelFuture channelFuture = client.connect(HostInfo.HOST_NAME,HostInfo.PORT);
            String url = "http://" + HostInfo.HOST_NAME + ":" + HostInfo.PORT ;
            DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1,HttpMethod.GET,url);
            request.headers().set(HttpHeaderNames.HOST,HostInfo.HOST_NAME);
            request.headers().set(HttpHeaderNames.CONTENT_LENGTH,String.valueOf(request.content().readableBytes()));
            request.headers().set(HttpHeaderNames.COOKIE,"nothing");
            channelFuture.channel().writeAndFlush(request);
            channelFuture.channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully();
        }
    }
}
