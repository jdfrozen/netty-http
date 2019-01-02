package com.frozen.nettyhttpclient.main;

import com.frozen.nettyhttpclient.service.HttpClient;

/**
 * @Auther: Frozen
 * @Date: 2019/1/2 14:54
 * @Description:
 */
public class HttpClientMain {
    public static void main(String[] args) throws Exception {
        HttpClient client = new HttpClient();
        client.run();
    }
}
