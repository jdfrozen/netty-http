package com.frozen.nettyhttp.nettyhttpserver.handler;

import com.frozen.nettyhttp.nettyhttpserver.component.manager.HttpSessionManaager;
import com.frozen.nettyhttp.nettyhttpserver.component.session.DefaultHttpSession;
import com.frozen.nettyhttp.nettyhttpserver.component.session.HttpSession;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Iterator;
import java.util.Set;

/**
 * @Auther: 冯默风
 * @Date: 2018/12/30 11:05
 * @Description:
 */
public class HttpServerHandler extends ChannelInboundHandlerAdapter {
    private static Logger log = LoggerFactory.getLogger(HttpServerHandler.class);
    private HttpRequest request;
    private DefaultFullHttpResponse response;
    private HttpSession httpSession;
    private ChannelHandlerContext ctx;

    private void setSessionId(boolean exists){
        if(exists==false){// 用户发送来的头信息里面不包含有SessionId内容
            String encodeCookie = ServerCookieEncoder.STRICT.encode(HttpSession.SESSIONID,HttpSessionManaager.createSession());
            this.response.headers().set(HttpHeaderNames.SET_COOKIE,encodeCookie);
        }
    }

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
}
