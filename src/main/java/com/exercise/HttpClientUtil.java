package com.exercise;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpClientUtil {
    private CloseableHttpClient httpClient;

    public HttpClientUtil() {
        httpClient = HttpClients.createDefault();
    }

    public HttpResult doGet(String url, Map<String, Object> map) throws URISyntaxException, IOException {
        URIBuilder uriBuilder = new URIBuilder(url);
        if(map != null) {
            for (Map.Entry<String, Object> entry: map.entrySet()) {
                uriBuilder.setParameter(entry.getKey(), entry.getValue().toString());
            }
        }
        HttpGet httpGet = new HttpGet(uriBuilder.build());
        CloseableHttpResponse response = httpClient.execute(httpGet);
        HttpResult httpResult = new HttpResult();
        httpResult.setCode(response.getStatusLine().getStatusCode());
        if(response.getEntity() != null) {
            httpResult.setBody(EntityUtils.toString(response.getEntity(), "UTF-8"));
        }
        return  httpResult;
    }


    public HttpResult doPost(String url, Map<String, Object> map) throws Exception {
        // 声明httpPost请求
        HttpPost httpPost = new HttpPost(url);
        // 判断map不为空
        if (map != null) {
            // 声明存放参数的List集合
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            // 遍历map，设置参数到list中
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                params.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
            }
            // 创建form表单对象
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params, "UTF-8");
            // 把表单对象设置到httpPost中
            httpPost.setEntity(formEntity);
        }
        // 使用HttpClient发起请求，返回response
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
        } catch (IOException e) {
            HttpResult httpResult = new HttpResult();
            httpResult.setCode(404);
            httpResult.setBody("请求失败");
            return httpResult;
        }
        // 解析response封装返回对象httpResult
        HttpResult httpResult = new HttpResult();
        // 解析数据封装HttpResult
        httpResult.setCode(response.getStatusLine().getStatusCode());
        if (response.getEntity() != null) {
            httpResult.setBody(EntityUtils.toString(response.getEntity(),"UTF-8"));
        }
        // 返回结果
        return httpResult;
    }

}
