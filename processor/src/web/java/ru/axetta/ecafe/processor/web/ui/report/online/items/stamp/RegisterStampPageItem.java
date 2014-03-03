package ru.axetta.ecafe.processor.web.ui.report.online.items.stamp;

import ru.axetta.ecafe.processor.core.daoservices.order.items.GoodItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 14.05.13
 * Time: 12:40
 * To change this template use File | Settings | File Templates.
 */
public class RegisterStampPageItem {

    private String date;
    private HashMap<String,Long> map = new HashMap<String, Long>();

    public RegisterStampPageItem(String data, List<GoodItem> items) {
        this.date = data;
        for (GoodItem item: items){
            if(!map.containsKey(item.getFullName())){
                map.put(item.getFullName(),0L);
            }
        }
    }

    public List<String> getSetKey(){
        return new ArrayList<String>(map.keySet());
    }

    public Long getValue(String id) {
        return map.get(id);
    }

    public String getDate() {
        return date;
    }

    public void addValue(String id, Long value){
        Long val = map.get(id);
        if(val==null) val=0L;
        map.put(id, val + value);
    }
}
