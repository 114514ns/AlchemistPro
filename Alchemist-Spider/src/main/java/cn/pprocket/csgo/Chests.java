package cn.pprocket.csgo;

import cn.hutool.core.io.file.FileReader;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.io.File;

public class Chests {
    public static Chest forName(String chestName) {
        for (Chest chest : Main.INSTANCE.getChestList()) {
            if (chest.getName().equals(chestName)) {
                return chest;
            }
        }

        return null;
    }
}
