/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.user;

import ru.axetta.ecafe.processor.core.persistence.Function;
import ru.axetta.ecafe.processor.core.persistence.User;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 10:20:05
 * To change this template use File | Settings | File Templates.
 */
public class FunctionViewer {

    public static class Item implements Comparable {

        private final String functionName;
        private final Long functionId;

        public String getFunctionName() {
            return functionName;
        }

        public Item(Function function) {
            this.functionId = function.getIdOfFunction();
            this.functionName = function.getFunctionName();
        }

        public int compareTo(Object o) {
            return (int)(functionId-((Item)o).functionId);
        }
    }

    private List<Item> items = Collections.emptyList();

    public List<Item> getItems() {
        return items;
    }

    public void fill(User user) throws Exception {
        List<Item> items = new LinkedList<Item>();
        Set<Function> userFunctions = user.getFunctions();
        for (Function function : userFunctions) {
            Item item = new Item(function);
            items.add(item);
        }
        Collections.sort(items);
        this.items = items;
    }
}