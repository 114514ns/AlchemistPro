package cn.pprocket.csgo.network;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONObject;
import okhttp3.OkHttpClient;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static cn.pprocket.csgo.network.HttpUtil.selector;

public class Proxy {
    public static List<java.net.Proxy> proxies = new CopyOnWriteArrayList<>();
    public static void init() {
        Thread t = new Thread(() -> {
            while (true) {
                proxies.clear();
                String res = HttpUtil.get("http://get.3ip.cn/dmgetip.asp?apikey=cca12ece&pwd=1977a8575144a1b918a847bc0b0b29f1&getnum=200&httptype=0&geshi=2&fenge=1&fengefu=&Contenttype=1&operate=all&setcity=all&provin=jiangsu");
                JSONObject jsonObject = JSONObject.parse(res);
                jsonObject.getJSONArray("data").forEach(ele -> {
                    JSONObject var1 = (JSONObject) ele;
                    String ip = var1.getString("ip");
                    Integer port = var1.getInteger("port");
                    java.net.Proxy proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP,new InetSocketAddress(ip,port));
                    proxies.add(proxy);
                });
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        Thread t2 = new Thread(() -> {
            while (true) {
                try {
                    if (!proxies.isEmpty()) {
                        Collections.shuffle(proxies);
                    }
                    Thread.sleep(20);
                } catch (Exception e) {

                }
            }
        });
        t2.start();
        new Thread(() -> {
            while (true) {
                cn.pprocket.csgo.network.HttpUtil.client = new OkHttpClient.Builder()
                        .proxySelector(selector)
                        .build();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}
