package cn.pprocket.csgo.item;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResultItem {
    Item item;
    float rate;
    float wear;

}
