package cn.pprocket.csgo.item;

import cn.pprocket.csgo.DangerLevel;
import cn.pprocket.csgo.Level;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class Item {

    private String name;

    private float price;

    private String chest;

    private int buffId;

    private DangerLevel danger;

    private List<String> higher;

    private Level level;

}
