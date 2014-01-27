package ru.axetta.ecafe.processor.core.report.statistics.good.request;

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
    private final Long dailySampleCount;

    public Commodity(String name, Long totalCount, Long dailySampleCount) {
        this.name = name;
        this.totalCount = totalCount;
        this.dailySampleCount = dailySampleCount;
    }

    public String getName() {
        return name;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public Long getDailySampleCount() {
        return dailySampleCount;
    }
}
