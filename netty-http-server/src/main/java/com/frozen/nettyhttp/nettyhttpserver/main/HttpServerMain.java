package com.frozen.nettyhttp.nettyhttpserver.main;

import com.frozen.nettyhttp.nettyhttpserver.service.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther: Frozen
 * @Date: 2019/1/2 13:13
 * @Description: 启动netty服务器
 */
public class HttpServerMain {
    private static Logger log = LoggerFactory.getLogger(HttpServerMain.class);
    public static void main(String[] args) throws Exception {
        HttpServer server = new HttpServer();
        server.run();
    }
}
