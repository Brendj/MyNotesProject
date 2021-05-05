package ru.iteco.restservice.servise.data;

import java.util.HashMap;
import java.util.Map;

public class ProhibitionData {
    private final Map<String, Long> prohibitByFilter;
    private final Map<String, Long> prohibitByName;
    private final Map<String, Long> prohibitByGroup;

    public ProhibitionData() {
        prohibitByFilter = new HashMap<>();
        prohibitByName = new HashMap<>();
        prohibitByGroup = new HashMap<>();
    }

    public Map<String, Long> getProhibitByFilter() {
        return prohibitByFilter;
    }

    public Map<String, Long> getProhibitByName() {
        return prohibitByName;
    }

    public Map<String, Long> getProhibitByGroup() {
        return prohibitByGroup;
    }

}
