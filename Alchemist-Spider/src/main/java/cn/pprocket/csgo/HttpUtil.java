package cn.pprocket.csgo;

import cn.hutool.aop.ProxyUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.List;

public class HttpUtil {
    static ProxySelector selector = new ProxySelector() {

        @Override
        public List<java.net.Proxy> select(URI uri) {
            return Proxy.proxies;
        }
        @Override
        public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {

        }
    };
    static OkHttpClient client = new OkHttpClient.Builder()
            .proxySelector(selector)
            //.addInterceptor(new RetryInterceptor(3))
            .build();
    public static String get(String url) {
        long start = System.currentTimeMillis();
        String res = "";
        try {
            Request request = new Request.Builder()
                    .get()
                    .url(url)
                    .build();
            res = client.newCall(request).execute().body().string();
        } catch (Exception e) {
            return get(url);
        }
        long end = System.currentTimeMillis();
        //System.out.println("耗时：  " + (end-start));
        return res;
    }
}
