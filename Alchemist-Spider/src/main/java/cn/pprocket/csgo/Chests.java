package cn.pprocket.csgo;

import cn.hutool.core.io.file.FileReader;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.io.File;

public class Chests {
    private static Chest[] arrays;
    static {
        String chests = FileReader.create(new File("chests.json")).readString();
        arrays = JSON.parseArray(chests).toArray(Chest.class);
    }
    public static Chest forName(String chestName) {
        for (Chest chest : arrays) {
            if (chest.getName().equals(chestName)) {
                return chest;
            }
        }
        return null;
    }
}
