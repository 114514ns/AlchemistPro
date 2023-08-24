package cn.pprocket.csgo;

import cn.pprocket.csgo.item.Item;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Chest {
    public String name;
    public List<Item> items;
}
