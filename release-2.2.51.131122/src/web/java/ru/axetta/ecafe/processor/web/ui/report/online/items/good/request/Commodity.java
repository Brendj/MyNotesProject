package ru.axetta.ecafe.processor.web.ui.report.online.items.good.request;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 22.05.13
 * Time: 13:07
 * To change this template use File | Settings | File Templates.
 */
public class Commodity {
    private final String name;
    private final Long totalCount;

    public Commodity(String name, Long totalCount) {
        this.name = name;
        this.totalCount = totalCount;
    }

    public String getName() {
        return name;
    }

    public Long getTotalCount() {
        return totalCount;
    }
}
