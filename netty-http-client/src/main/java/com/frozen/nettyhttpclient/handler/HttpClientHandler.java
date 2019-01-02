package com.frozen.nettyhttpclient.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther: Frozen
 * @Date: 2019/1/2 14:36
 * @Description: 客户端处理的Handler
 */
public class HttpClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(HttpClientHandler.class);
    @Override
    public void channelRead(ChannelHandlerContext ctx,Object msg) throws Exception{
        if(msg instanceof HttpResponse){
            HttpResponse response = (HttpResponse) msg;
            log.info("【Netty-Http客户端】ContenType = "+response.headers().get(HttpHeaderNames.CONTENT_TYPE));
            log.info("【Netty-Http客户端】ContentLength = "+response.headers().get(HttpHeaderNames.CONTENT_LENGTH));
            log.info("【Netty-Http客户端】SET-COOKIE = "+ServerCookieDecoder.STRICT.decode(response.headers().get(HttpHeaderNames.SET_COOKIE)));
        }
        if(msg instanceof HttpConstants){
            HttpContent content = (HttpContent) msg;
            ByteBuf buf = content.content();
            log.info("【Netty-HTTP客户端】"+buf.toString(CharsetUtil.UTF_8));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause) throws Exception{
        cause.printStackTrace();
        ctx.close();
    }
}
