package com.exercise;

import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import javax.net.ssl.SSLException;
import java.io.*;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class HttpclientTest {

    @Test
    public void firstHttpclientDemo() {
        String  url = "http://www.baidu.com";
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get =  new HttpGet(url);
        HttpPost post = new HttpPost(url);
        try {
            CloseableHttpResponse response = client.execute(get);
            System.out.println(response.getStatusLine());

            response = client.execute(post);
            System.out.println(response.getAllHeaders());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void setParams() throws Exception{
        //https://www.baidu.com/s?ie=UTF-8&wd=httpclient
        URI uri = new URIBuilder()
                .setScheme("http")
                .setHost("www.baidu.com")
                .setPath("/s")
                .setParameter("ie", "UTF-8")
                .setParameter("wd", "httpclient")
                .build();
        HttpGet get = new HttpGet(uri);
        System.out.println(get.getURI());

        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = client.execute(get);
        System.out.println(response.getStatusLine());
    }

    @Test
    public void operateHeader() {
        HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1,
                HttpStatus.SC_OK, "OK");
        response.addHeader("Set-Cookie", "c1=a;path=/;domain=localhost");
        response.addHeader("Set-Cookie", "c2=b;path=\"/\";c3=c;domain=\"localhost\"");
        Header h1 = response.getFirstHeader("Set-Cookie");
        System.out.println(h1);
        Header h2 = response.getLastHeader("Set-Cookie");
        System.out.println(h2);
        Header[] hs = response.getHeaders("Set-Cookie");
        System.out.println(hs.length);

        HeaderIterator it = response.headerIterator("Set-Cookie");
        while (it.hasNext()) {
            System.out.println(it.next());
        }

        HeaderElementIterator heit = new BasicHeaderElementIterator(response.headerIterator("Set-Cookie"));
        while (heit.hasNext()) {
            HeaderElement element = heit.nextElement();
            System.out.println(element.getName() + "=" + element.getValue());
            NameValuePair[] params = element.getParameters();
            for (int i = 0; i < params.length; i++) {
                System.out.println(i + "" + params[i]);
            }

        }
    }

    @Test
    public void testEntity() {
        StringEntity entity = new StringEntity("important message", ContentType.create("text/plain", "UTF-8"));
        System.out.println(entity.getContentType());
        System.out.println(entity.getContentLength());
        try {
            System.out.println(EntityUtils.toString(entity));
            System.out.println(EntityUtils.toByteArray(entity).length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void responseEntity() {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet("https://www.baidu.com");
        try {
            CloseableHttpResponse response = client.execute(get);
            HttpEntity entity = response.getEntity();
            if(entity != null) {
                InputStream inputStream = entity.getContent();
                Reader reader = new InputStreamReader(inputStream);
                char[] str = new char[1024];
                int len = 0;
                while ((len = reader.read(str)) != -1) {
                    System.out.println(new String(str, 0, len));
                }
//                System.out.println(EntityUtils.toString(entity));
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testEntity2() {
        File file = new File("somefile.txt");
        try {
            if(!file.exists()) file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileEntity entity = new FileEntity(file, ContentType.create("text/plain", "UTF-8"));
        HttpPost httpPost = new HttpPost("https://www.baidu.com");
        httpPost.setEntity(entity);
        System.out.println(file.getAbsolutePath());
        System.out.println(file.exists());
    }

    @Test
    public void testParams() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("params1", "value1"));
        params.add(new BasicNameValuePair("params2", "value2"));

        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, Consts.UTF_8);
        HttpPost post = new HttpPost("https://www.baidu.com");
        post.setEntity(entity);

    }

    @Test
    public void testContext() throws IOException {
        CloseableHttpClient client = HttpClients.custom().addInterceptorLast(
                new HttpRequestInterceptor() {
                    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
                        AtomicInteger count = (AtomicInteger) context.getAttribute("count");
                        request.addHeader("Count", Integer.toString(count.getAndIncrement()));
                    }
                }
        ).build();

        AtomicInteger count = new AtomicInteger(1);
        HttpClientContext context = HttpClientContext.create();
        context.setAttribute("count", count);
        HttpGet get = new HttpGet("http://www.baiduc.com");
        for (int i = 0; i < 3; i++) {
            CloseableHttpResponse response = client.execute(get, context);
            HttpEntity entity = response.getEntity();
            System.out.println(EntityUtils.toString(entity));
            response.close();
        }
        client.close();

    }

    @Test
    public void retry() {
        HttpRequestRetryHandler retryHandler = new HttpRequestRetryHandler() {
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {

                if(executionCount >= 5) {
                    return  false;
                }
                if(exception instanceof  InterruptedIOException) {
                    return  false;
                }
                if(exception instanceof UnknownHostException) {
                    return false;
                }
                if(exception instanceof ConnectTimeoutException) {
                    return false;
                }
                if(exception instanceof SSLException) {
                    return false;
                }
                HttpClientContext context1 = HttpClientContext.adapt(context);
                HttpRequest request = context1.getRequest();
                boolean idempotent = !(request instanceof  HttpEntityEnclosingRequest);
                if(idempotent) {
                    return  true;
                }
                return false;
            }
        };
        CloseableHttpClient client = HttpClients.custom().setRetryHandler(retryHandler).build();


    }
}