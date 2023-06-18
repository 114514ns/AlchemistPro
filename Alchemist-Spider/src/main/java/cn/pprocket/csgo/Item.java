package cn.pprocket.csgo;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class Item {

    private String name;

    private float price;

    private String chest;

    private int buffId;

    private DangerLevel level;

}
