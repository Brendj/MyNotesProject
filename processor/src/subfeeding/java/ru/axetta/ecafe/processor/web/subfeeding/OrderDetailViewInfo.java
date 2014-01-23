/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.subfeeding;

import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 11.12.13
 * Time: 15:58
 */

public class OrderDetailViewInfo {

    public Map<Integer, ComplexViewInfo> complexesByType = new HashMap<Integer, ComplexViewInfo>();
    public Set<SeparateDish> dishesInfo = new HashSet<SeparateDish>();

    public static class ComplexViewInfo {

        public String name;
        public Long sum;
        public Set<String> complexDetails = new HashSet<String>();
        public int count;
        public boolean isComplete;

        private ComplexViewInfo(String name, Long sum, boolean isComplete) {
            this.count = 1;
            this.name = name;
            this.sum = sum;
            this.isComplete = isComplete;
        }

        public void addComplexDetail(String name) {
            complexDetails.add("* " + name);
        }

        @Override
        public String toString() {
            StringBuilder s = new StringBuilder();
            return s.append("Комплекс ").append(name).append(" - ").append(CurrencyStringUtils.copecksToRubles(sum))
                    .append(" руб.").append(count > 1 ? " x " + count : "").append(":<br/>")
                    .append(StringUtils.join(complexDetails, "<br/>")).toString();
        }
    }

    public static class SeparateDish {

        public String name;
        public Long sum;
        public Long count;

        public SeparateDish(String name, Long sum, Long count) {
            this.name = name;
            this.sum = sum;
            this.count = count;
        }

        @Override
        public String toString() {
            StringBuilder s = new StringBuilder();
            return s.append(name).append(" - ").append(CurrencyStringUtils.copecksToRubles(sum)).append(" руб.")
                    .append(count > 1L ? " x " + count : "").append("<br/>").toString();
        }
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (ComplexViewInfo cvi : complexesByType.values()) {
            s.append(cvi.toString());
        }
        for (SeparateDish sd : dishesInfo) {
            s.append(sd.toString());
        }
        return s.toString();
    }

    public ComplexViewInfo createComplexViewInfo(int menuType, String name, Long sum, boolean isComplete) {
        ComplexViewInfo cvi = complexesByType.get(menuType);
        if (cvi == null) {
            cvi = new ComplexViewInfo(name, sum, isComplete);
            complexesByType.put(menuType, cvi);
        } else {
            if (cvi.isComplete) {
                cvi.count++;
            } else {
                cvi.name = name;
                cvi.sum = sum;
            }
        }
        return cvi;
    }

    public SeparateDish createSeparateDish(String name, Long sum, Long count) {
        SeparateDish sd = new SeparateDish(name, sum, count);
        dishesInfo.add(sd);
        return sd;
    }
}
