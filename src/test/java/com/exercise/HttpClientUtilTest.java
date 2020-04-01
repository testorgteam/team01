package com.exercise;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class HttpClientUtilTest extends TestCase {

    @Test
    public void testDoGet() throws IOException, URISyntaxException {
        String url = "http://www.baidu.com/s";
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ie","UTF-8");
        map.put("wd","httpclient");
        HttpClientUtil httpClientUtil = new HttpClientUtil();
        HttpResult httpResult = httpClientUtil.doGet(url,map);
        System.out.println(httpResult.getCode());
        System.out.println(httpResult.getBody());
    }

    @Test
    public void testDoPost() throws Exception {
        String url = "http://www.baidu.com/s";
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ie","UTF-8");
        map.put("wd","httpclient");
        HttpClientUtil httpClientUtil = new HttpClientUtil();
        HttpResult httpResult = httpClientUtil.doPost(url,map);
        System.out.println(httpResult.getCode());
        System.out.println(httpResult.getBody());
    }
}