package cn.pprocket.csgo


import cn.hutool.core.io.file.FileReader
import cn.hutool.core.io.file.FileWriter
import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONObject
import com.alibaba.fastjson2.TypeReference
import java.io.File
import java.util.*
import java.util.concurrent.Executors

object Buff {
    var chestList = mutableListOf<Chest>()
    //var pool = Executors.newFixedThreadPool(8)
    fun getChests(): MutableList<Chest> {
        var result = mutableListOf<Chest>()
        for (i in 1..2) {
            var query = "https://buff.163.com/api/market/csgo_container_list?type=${
                if (i == 1) {
                    "weapon_cases"
                } else {
                    "map_collections"
                }
            }&page_num=1&page_size=60"
            var res = HttpUtil.get(query)
            var v1 = JSONObject.parse(res).getJSONObject("data").getJSONArray("items")
            v1.forEach {
                var obj = it as JSONObject
                var chestName = obj["name"]
                var req = ""
                var chest = Chest()
                chest.name = (chestName as String)
                var chestCode = obj["value"] as String
                chestCode = chestCode.replace("&", "%26")
                chestCode = chestCode.replace(" ", "%20")
                if (i == 2) {
                    req =
                        "https://buff.163.com/api/market/csgo_container?container=${chestCode}&is_container=1&container_type=itemset&unusual_only=0&game=csgo&appid=730"
                } else {
                    req = "https://buff.163.com/api/market/csgo_container?container=${
                        chestCode
                    }&is_container=1&container_type=weaponcase&unusual_only=0&game=csgo&appid=730"
                }
                var res = HttpUtil.get(req);
                var jsonObject = JSONObject.parse(res).getJSONObject("data").getJSONArray("items")
                var items = mutableListOf<Item>()
                jsonObject.forEach {
                    var item = Item()
                    var var1 = it as JSONObject
                    item.name = var1.getString("localized_name")
                    item.chest = chestName
                    items.add(item)

                }
                chest.items = items
                println("正在爬取 ：  ${chestName}")
                result.add(chest)
            }
        }
        return result
    }
    fun getIds():List<Int> {
        var result = mutableListOf<Int>()
        var lines = FileReader.create(File("input.json")).readLines()
        lines.forEach {
            var flag = true
            var jsonObject = JSONObject.parse(it)
            if (!it.contains("csgo")) {
                flag = false //排除dota里的东西
            }
            if (it.contains("印花") && !it.contains("印花集")) {
                flag = false; //排除贴纸
            }
            if (it.matches(Regex("[a-zA-Z]+"))) {
                flag = false //排除英文
            }
            /*
            if (!it.contains("崭新出场") && !it.contains("略有磨损") && !it.contains("久经沙场") && !it.contains("破碎不堪") && !it.contains("战痕累累") ) {
                flag = false
            }

             */

            if (it.contains("StatTrak")) {
                flag = false
            }
            if (it.contains("纪念品")) {
                flag = false
            }
            if (!it.contains("崭新出厂")) {
                flag = false
            }
            if (it.contains("纪念品")) {
                flag = false
            }
            if (flag) {
                result.add(jsonObject.getInteger("buff_id"))
            }
        }
        return result
    }
    fun getItems(ids:List<Int>):List<Item> {
        var result = mutableListOf<Item>()

        var t = Thread {
            while (true) {
                var start = result.size
                Thread.sleep(1000)
                var end = result.size
                println("${end-start}/s，已爬取： $end")
            }
        }
        t.start()
        ids.forEach {
            if (!contains(result,it)) {
                var get = HttpUtil.get("https://buff.163.com/api/market/goods/info?goods_id=$it")
                var parse = JSONObject.parse(get).getJSONObject("data")
                var id = parse.getInteger("id")
                var name = parse.getString("name")
                var price = parse.getString("sell_min_price").toFloat()
                var level = getLevel(name)
                var shortName = parse.getString("short_name")
                var item = Item(name,price, searchInChest(name).name,id,level)
                result.add(item)
                var related = parse.getJSONArray("relative_goods")
                related.forEach {
                    var obj = it as JSONObject
                    var levelName = obj.getString("tag_name")
                    var level = getLevel(levelName)
                    var nameInside = "$shortName ($levelName)"

                    if (!obj.getString("tag_name").contains("Stat") && !name.equals(nameInside)) { //排除暗金
                        var item1 = Item()
                        var price = obj.getString("sell_min_price").toFloat()
                        var id = obj.getInteger("goods_id")

                        item1.buffId = id
                        item1.name = nameInside
                        item1.level = level
                        item1.price = price
                        item1.chest = searchInChest(name).name
                        result.add(item1)
                    }
                }
                System.console()
            }
        }
        result.removeIf { it.chest == null }
        t.stop()

        return result
    }
    private fun searchInChest(name: String,):Chest {
        var chest = Chest()
        chestList.forEach {
            for (item1 in it.getItems()) {
                if (name.contains(item1.name)) {
                    chest = it
                }
            }
        }
        return chest
    }
    private fun getLevel(string: String):DangerLevel {
        if (string.contains("崭新出厂")) return DangerLevel.FACTORY_NEW
        if (string.contains("略有磨损")) return DangerLevel.MINIMAL_WORN
        if (string.contains("久经沙场")) return DangerLevel.FIELD_TESTED
        if (string.contains("破损不堪")) return DangerLevel.WELL_WORN
        else return DangerLevel.BATTLE_SCARRED
    }
    fun contains(list:List<Item>,id:Int):Boolean {
        var result = false;
        list.forEach {
            if (it.buffId == id) {
                result = true
            }
        }
        return result
    }

}


fun main() {
    Proxy.init()
    
    println("请输入操作代码")
    println("1表示生成箱子列表")
    println("2表示生成物品列表")
    Thread.sleep(2000)

    var int = Scanner(System.`in`).nextInt()

    when (int) {
        OPCode.GEN_CHESTS.ordinal ->{
            var chests = Buff.getChests()
            var str = JSON.toJSONString(chests)
            FileWriter.create(File("chests.json")).write(str)
        }
        OPCode.GEN_ITEMS.ordinal -> {
            Buff.chestList = JSON.parseArray(FileReader(File("chests.json")).readString(),Chest::class.java)
            var items = Buff.getItems(Buff.getIds())
            FileWriter.create(File("items.json")).write(JSONObject.toJSONString(items))
        }

    }
}
