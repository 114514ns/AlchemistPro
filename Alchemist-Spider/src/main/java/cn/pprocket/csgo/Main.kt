package cn.pprocket.csgo

import cn.hutool.core.io.file.FileReader
import cn.hutool.core.util.RandomUtil
import cn.pprocket.csgo.item.Item
import cn.pprocket.csgo.item.Result
import com.alibaba.fastjson2.JSON

import lombok.extern.log4j.Log4j
import java.io.File
import java.util.*
import java.util.concurrent.Executors
import javax.swing.DebugGraphics


@Log4j
object Main {
    var chestList = JSON.parseArray(FileReader(File("chests.json")).readString(), Chest::class.java)
    var itemList = JSON.parseArray(FileReader(File("items.json")).readString(), Item::class.java)

    @Throws(InterruptedException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val items = FileReader.create(File("items.json")).readString()
        val objects = JSON.parseArray(items).toArray(
            Item::class.java
        )
        //Tools.writeFile(gson.toJson(itemList),"items.json");
        //System.exit(0);
        //Tools.writeFile(gson.toJson(itemList),"items.json");
        //System.exit(0);
        val start = System.currentTimeMillis()
        val results: MutableList<Result> = ArrayList<Result>()
        val service = Executors.newFixedThreadPool(12)
        for (l in 0..4) {
            for (j in 0 until 10000000) {
                val recipes = mutableListOf<Item>()
                val result = mutableListOf<Item>()
                for (k in 0..9) {
                    recipes.add(RandomUtil.randomEle(itemList))
                }
                var average = getAverageAmount(recipes)
                val map = mutableMapOf<String, Int>()
                val newMap = mutableMapOf<List<Item>, Double>()
                recipes.forEach {
                    if (map.containsKey(it.chest)) {
                        map[it.chest] = map[it.chest]!! + 1
                    } else {
                        map[it.chest] = 1
                    }
                }
                map.forEach {
                    val list = mutableListOf<Item>()
                    val name = it.key
                    recipes.forEach {
                        if (it.chest.equals(name)) {
                            list.add(it)
                        }
                    }
                    newMap[list] = it.value.toDouble() / 10
                }
                var times = 0;
                newMap.forEach {
                    val total = it.value
                    val size = it.key.size
                    it.key.forEach {
                        val result = Result()
                        result.setOriginItem(it)
                        result.setRate(total / size)
                        times++;
                        results.add(result)
                    }
                }
                DebugGraphics.LOG_OPTION
            }
        }
        service.shutdown()
    }

    fun getAverageAmount(items: List<Item?>): Double {
        var total = 0.0
        for (i in items.indices) {
            val item = items[i]
            total += getMinAmount(item!!)
        }
        return total / 10
    }

    fun getItemByLevel(chest: String, level: Level): List<Item> {
        val result = mutableListOf<Item>()
        Chests.forName(chest).items.forEach {
            if (it.level == level) {
                result.add(it)
            }
        }
        return result
    }


    fun getMinAmount(item: Item): Double {
        val name = item.name
        return if (item.name.contains("崭新出厂")) {
            RandomUtil.randomDouble(0.01, 0.06)
        } else if (name.contains("略有磨损")) {
            RandomUtil.randomDouble(0.07, 0.14)
        } else if (name.contains("久经沙场")) {
            RandomUtil.randomDouble(0.15, 0.38)
        } else if (name.contains("破损不堪")) {
            RandomUtil.randomDouble(0.38, 0.45)
        } else if (name.contains("战痕累累")) {
            RandomUtil.randomDouble(0.45, 0.8)
        } else {
            1.14514
        }
    }

    fun getWearAmount(amount: Float): DangerLevel {
        return if (amount <= 0.06) {
            DangerLevel.FACTORY_NEW
        } else if (amount <= 0.14) {
            DangerLevel.MINIMAL_WORN
        } else if (amount <= 0.37) {
            DangerLevel.FIELD_TESTED
        } else if (amount <= 0.45) {
            DangerLevel.WELL_WORN
        } else {
            DangerLevel.BATTLE_SCARRED
        }
    }
}
