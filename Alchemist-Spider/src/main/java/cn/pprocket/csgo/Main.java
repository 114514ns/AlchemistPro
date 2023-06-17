package cn.pprocket.csgo;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONObject;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.*;

public class Main {
    public static void main(String[] args) throws InterruptedException{
        Thread.sleep(5000);
        List<Chest> chests = Buff.INSTANCE.getChests();
        System.console();
    }

}
