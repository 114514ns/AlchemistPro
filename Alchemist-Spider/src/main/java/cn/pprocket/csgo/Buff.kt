package cn.pprocket.csgo


import cn.hutool.core.io.file.FileReader
import cn.hutool.core.io.file.FileWriter
import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONObject
import java.io.File
import java.util.concurrent.Executors

object Buff {
    var pool = Executors.newFixedThreadPool(8)
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
                flag = false
            }
            if (it.contains("印花") && !it.contains("印花集")) {
                flag = false;
            }
            if (it.matches(Regex("[a-zA-Z]+"))) {
                flag = false
            }
            if (!it.contains("崭新出场") && !it.contains("略有磨损") && !it.contains("久经沙场") && !it.contains("破碎不堪") && !it.contains("战痕累累") ) {
                flag = false
            }
            if (flag) {

            }
        }
        return result
    }
    fun getItems():List<Item> {
        var result = mutableListOf<Item>()
        return result
    }
}

fun main() {
    Proxy.init()
    Thread.sleep(2000)
    var chests = Buff.getChests()
    var str = JSON.toJSONString(chests)
    FileWriter.create(File("chests.json")).write(str)

}
