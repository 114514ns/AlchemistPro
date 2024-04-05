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
        //Tools.writeFile(gson.toJson(itemList),"items.json");
        //System.exit(0);
        //Tools.writeFile(gson.toJson(itemList),"items.json");
        //System.exit(0);
        val start = System.currentTimeMillis()
        val results: MutableList<Result> = ArrayList<Result>()
        val service = Executors.newFixedThreadPool(12)
        for (l in 0..4) {

            while (true) {
                val recipes = mutableListOf<Item>()
                var count = 0
                while (true) {
                    val item = RandomUtil.randomEle(itemList)
                    if (item.level.ordinal == l) {
                        item.dangerValue = randomDanger(item.name)
                        recipes.add(item)
                        count++
                        if (count == 10) {
                            break
                        }
                    }
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
                var flag = true
                newMap.forEach loop@{
                    val total = it.value
                    val size = it.key.size
                    var itemByLevel = getItemsByLevel(it.key[0].chest, it.key[0].level)
                    itemByLevel.forEach {
                        val result = Result()
                        var wearAmount = getWearAmount(average.toFloat())
                        var specificItem = getSpecificItem(it.name, wearAmount)
                        if (specificItem.name == null) {
                            flag = false

                        }
                        result.setOriginItem(specificItem)
                        result.setRate(total / itemByLevel.size)
                        result.setAmountValue(average.toFloat())
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
            total += getMinAmount(item!!.name)
        }
        return total / 10
    }
    /*
    fun getItemByName(name : String) {
        itemList.f
    }
    */


    fun getItemsByLevel(chest: String, level: Level): List<Item> {
        val result = mutableListOf<Item>()
        Chests.forName(chest).items.forEach {
            if (it.level == level) {
                result.add(it)
            }
        }
        return result
    }

    fun getSpecificItem(name: String, level: DangerLevel): Item {
        var found = false
        var item: Item? = null
        itemList.forEach loop@{
            if (it.name.contains(name)) {
                if (it.danger == level) {
                    item = it
                    return@loop
                }
            }
        }
        return item ?: Item()
    }


    fun getMinAmount(name: String): Double {
        return if (name.contains("崭新出厂")) {
            0.01
        } else if (name.contains("略有磨损")) {
            0.07
        } else if (name.contains("久经沙场")) {
            0.15
        } else if (name.contains("破损不堪")) {
            0.38
        } else if (name.contains("战痕累累")) {
            0.45
        } else {
            1.14514
        }
    }

    fun randomDanger(name: String): Double {
        return if (name.contains("崭新出厂")) {
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
