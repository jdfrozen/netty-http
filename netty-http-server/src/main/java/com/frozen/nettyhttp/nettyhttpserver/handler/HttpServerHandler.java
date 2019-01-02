package com.frozen.nettyhttp.nettyhttpserver.handler;

import com.frozen.nettyhttp.nettyhttpserver.component.manager.HttpSessionManaager;
import com.frozen.nettyhttp.nettyhttpserver.component.session.DefaultHttpSession;
import com.frozen.nettyhttp.nettyhttpserver.component.session.HttpSession;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.handler.codec.http.multipart.DiskFileUpload;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.util.Iterator;
import java.util.Set;

/**
 * @Auther: 冯默风
 * @Date: 2018/12/30 11:05
 * @Description: httpServer处理Handler
 */
public class HttpServerHandler extends ChannelInboundHandlerAdapter {
    private static Logger log = LoggerFactory.getLogger(HttpServerHandler.class);
    private HttpRequest request;
    private DefaultFullHttpResponse response;
    private HttpSession httpSession;
    private ChannelHandlerContext ctx;

    @Override
    public void channelRead(ChannelHandlerContext ctx,Object msg){
        this.ctx =ctx;
        if(msg instanceof HttpRequest){
            this.request = (HttpRequest) msg;
            log.info("【Netty-HTTP服务器端】uri = " + this.request.uri() + "、Method = " + this.request.method() + "、Headers = " + request.headers());
            this.handleUrl(this.request.uri());
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
        cause.printStackTrace();
        ctx.close();
    }

    private void responseWrite(String content) {
        ByteBuf buf = Unpooled.copiedBuffer(content,CharsetUtil.UTF_8) ;
        this.response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK,buf) ;
        this.response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/html;charset=UTF-8") ; // 设置MIME类型
        this.response.headers().set(HttpHeaderNames.CONTENT_LENGTH,String.valueOf(buf.readableBytes())) ; // 设置回应数据长度
        this.setSessionId(this.isHasSessionId());
        ctx.writeAndFlush(this.response).addListener(ChannelFutureListener.CLOSE) ; // 数据回应完毕之后进行操作关闭
    }

    /**
     * 依据传入的标记内容进行是否向客户端Cookie中保存有SessionId数据的操作
     * @param exists
     */
    private void setSessionId(boolean exists){
        if(exists==false){// 用户发送来的头信息里面不包含有SessionId内容
            String encodeCookie = ServerCookieEncoder.STRICT.encode(HttpSession.SESSIONID,HttpSessionManaager.createSession());
            this.response.headers().set(HttpHeaderNames.SET_COOKIE,encodeCookie);
        }
    }

    /**
     * 当前所发送的请求里面是否存在有指定的 SessionID数据信息
     * @return 如果存在返回true，否则返回false
     */
    public boolean isHasSessionId(){
        String cookieStr = this.request.headers().get(HttpHeaderNames.COOKIE);
        if(StringUtils.isEmpty(cookieStr)){
            return false;
        }
        Set<Cookie> cookieSet =  ServerCookieDecoder.STRICT.decode(cookieStr);
        Iterator<Cookie> iter = cookieSet.iterator();
        while (iter.hasNext()){
            Cookie cookie = iter.next();
            if(HttpSession.SESSIONID.equals(cookie.name())){
                if(HttpSessionManaager.isExists(cookie.value())){
                    this.httpSession = HttpSessionManaager.getSession(cookie.value());
                    return true;
                }
            }
        }
        return false;
    }

    public void info() {
        String content =
                "<html>" +
                        "  <head>" +
                        "       <title>Hello Netty</title>" +
                        "   </head>" +
                        "   <body>" +
                        "       <h1>好好学习，天天向上</h1>" +
                        "       <img src='/show.png'>" +
                        "   </body>" +
                        "</html>";   // HTTP服务器可以回应的数据就是HTML代码
        this.responseWrite(content);
    }

    public void favicon() {
        try {
            this.sendImage("favicon.ico");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void handleUrl(String uri) {
        if ("/info".equals(uri)) {
            this.info();
        } else if ("/favicon.ico".equals(uri)) {
            this.favicon();
        } else if ("/show.png".equals(uri)) {
            this.show() ;
        }
    }
    public void show() {
        try {
            this.sendImage("show.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void sendImage(String fileName) throws Exception {
        String filePath = DiskFileUpload.baseDirectory + fileName ;
        File sendFile = new File(filePath) ;
        HttpResponse imageResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK) ;
//        imageResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH,String.valueOf(sendFile.length())) ;
        MimetypesFileTypeMap mimeMap = new MimetypesFileTypeMap() ;
        imageResponse.headers().set(HttpHeaderNames.CONTENT_TYPE,mimeMap.getContentType(sendFile)) ;
        imageResponse.headers().set(HttpHeaderNames.CONNECTION,HttpHeaderValues.KEEP_ALIVE) ;
        this.ctx.writeAndFlush(imageResponse) ;
        this.ctx.writeAndFlush(new ChunkedFile(sendFile)) ;
        // 在多媒体信息发送完毕只后需要设置一个空的消息体，否则内容无法显示
        ChannelFuture channelFuture = this.ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT) ;
        channelFuture.addListener(ChannelFutureListener.CLOSE) ;
    }
}
