package cn.pprocket.csgo.network;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.List;

public class HttpUtil {
    public static ProxySelector selector = new ProxySelector() {

        @Override
        public List<java.net.Proxy> select(URI uri) {
            //System.out.println("选择了一次代理");
            return Proxy.proxies;
        }

        @Override
        public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {

        }
    };


    static OkHttpClient client = new OkHttpClient.Builder()
            .proxySelector(selector)
            .build();

    public static String get(String url) {
        String res = "";
        try {
            Request request = new Request.Builder()
                    .get()
                    .url(url)
                    .build();
            res = client.newCall(request).execute().body().string();
            Thread.sleep(80);
        } catch (Exception e) {
            return get(url);
        }
        return res;
    }
}
