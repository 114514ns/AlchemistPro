package cn.pprocket.csgo;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.util.RandomUtil;
import cn.pprocket.csgo.item.Item;
import com.alibaba.fastjson2.JSON;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws InterruptedException{
        String items = FileReader.create(new File("items.json")).readString();
        Item[] objects = JSON.parseArray(items).toArray(Item.class);
        ExecutorService service = Executors.newFixedThreadPool(8);
        for (int i = 0;i<1000000;i++) {
            service.submit(() -> {
                Item[] itemArray = new Item[10];
                for(int j = 0;j<10;j++) {
                    int random = RandomUtil.randomInt(0,objects.length-1);
                    itemArray[j] = objects[(random)];
                } //随机获取10个Item对象并放入itemArray
                for(int k = 0;k<10;k++) {
                    Item item = itemArray[k];
                    Chest chest = Chests.forName(item.getChest());
                    chest.getItems().forEach(ele -> {

                    });
                }
            });
        }
    }

}
