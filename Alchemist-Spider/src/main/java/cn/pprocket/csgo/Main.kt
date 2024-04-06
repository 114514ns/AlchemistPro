package cn.pprocket.csgo

import cn.hutool.core.io.file.FileReader
import cn.hutool.core.util.RandomUtil
import cn.pprocket.csgo.item.Item
import cn.pprocket.csgo.item.Result
import com.alibaba.fastjson2.JSON


import lombok.extern.log4j.Log4j
import java.io.File
import java.lang.Exception
import java.util.*
import java.util.concurrent.Executors
import javax.swing.DebugGraphics
import kotlin.system.measureTimeMillis


@Log4j
object Main {
    val chestList = JSON.parseArray(FileReader(File("chests.json")).readString(), Chest::class.java)
    val itemList: MutableList<Item> = JSON.parseArray(FileReader(File("items.json")).readString(), Item::class.java)
    var groupBy = itemList.groupBy { it.danger }
    val sort = itemList.groupBy { it.name.substring(0, it.name.length - 7) }

    @Throws(InterruptedException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        //Tools.writeFile(gson.toJson(itemList),"items.json");
        //System.exit(0);
        //Tools.writeFile(gson.toJson(itemList),"items.json");
        //System.exit(0);
        val sortTime = measureTimeMillis {
            itemList.sortBy { it.name }
        }

        println("排序耗时： $sortTime 毫秒")
        val start = System.currentTimeMillis()
        var times = 0L
        val service = Executors.newFixedThreadPool(8)
        val solutions = mutableListOf<Solution>()
        Thread {
            while (true) {
                val time = System.currentTimeMillis() - start
                val recipesPerSecond = times.toDouble() / (time / 1000.0)
                val output = String.format("%.2f recipes /s ", recipesPerSecond)
                println(output + "当前 ${times}")
                Thread.sleep(3000)
            }
        }.start()
        for (l in 2..4) {
            for (j in 0..100000) {
                val recipes = mutableListOf<Item>()
                var count = 0
                while (true) {
                    val item = randomItem(l)
                    item.dangerValue = randomDanger(item.name)
                    if (item.price != 0.0) { // 如果price为0，说明buff上没得卖
                        recipes.add(item)
                        count++
                    }
                    if (count == 10) {
                        break
                    }
                }
                val results: MutableList<Result> = ArrayList<Result>()
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
                newMap.forEach {
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
                        result.setPrice(specificItem.price)
                        result.setAmountValue(average.toFloat())
                        results.add(result)
                    }
                }
                if (flag) {
                    val solution = Solution()
                    solution.spend = recipes.sumOf { it.price }
                    solution.input = recipes
                    solution.output = results
                    results.sortBy { it.price }
                    //solution.rate = results.count { it.getOriginItem().price > solution.spend } * 1.0 / results.size
                    solution.rate =
                        results.sumByDouble { if (it.getOriginItem().price > solution.spend) it.rate else 0.0 }
                    times++
                    if (solution.rate > 0.3) {
                        solutions.add(solution)
                    }
                }
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
        /*
        var filter = groupBy[level]!!.filter { it.name.contains(name) }
        if (filter.isEmpty()) {
            return Item()
        }
        val result = filter[RandomUtil.randomInt(0,filter.size)]
        return result

         */
        val value = sort[name]
        if (value == null) {
            return Item()
        }
        val result = try {
            sort[name]!![level.ordinal]
        } catch (e: Exception) {
            // 这里处理异常
            Item() // 也可以返回其他值或者做其他处理
        }

        return result
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

    fun randomItem(level: Int): Item {
        //TODO 找出一个收藏品中指定品质中最便宜的
        var chest = RandomUtil.randomEle(chestList)
        var low = 99999.0
        var item: Item? = null
        chest!!.items.forEach {
            if (it.price < low && it.level.ordinal == level) {
                item = it
            }
        }
        var sortedBy =
            itemList.filter { it.level.ordinal == level && chest.name == it.chest }.sortedBy { it.price }
        return RandomUtil.randomEle(sortedBy)
    }


}
