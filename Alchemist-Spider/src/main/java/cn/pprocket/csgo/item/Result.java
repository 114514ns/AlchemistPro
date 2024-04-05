package cn.pprocket.csgo.item;

import cn.pprocket.csgo.DangerLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class Result {
    public Item originItem;
    public DangerLevel dangerLevel;
    public float amountValue;
    public double rate;
    public double price;
}