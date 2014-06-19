package ru.axetta.ecafe.processor.web.subfeeding;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 18.06.14
 * Time: 15:14
 * To change this template use File | Settings | File Templates.
 */
public enum PageActions {
    ACTIVATE("/activate"), SUSPEND("/suspend"), VIEW("/view"),
    REOPEN("/reopen"), LOGOUT("/logout"),
    PLAN("/plan"), EDIT("/edit"), TRANSFER("/transfer"),
    CANCEL("/cancel"), HISTORIES("/histories");

    private String action;
    static private Map<String, PageActions> content = new HashMap<String, PageActions>();
    static {
       for (PageActions actions: PageActions.values()){
           content.put(actions.action, actions);
       }
    }


    private PageActions(String action) {
        this.action = action;
    }

    public static PageActions parseString(String pageAction){
        return content.get(pageAction);
    }

    @Override
    public String toString() {
        return action;
    }

}
