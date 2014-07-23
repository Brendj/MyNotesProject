/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.monitoring;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.stat.Statistics;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


/**
 * Системные показатели
 * page: persistance_cache_monitor.jsp
 * Created with IntelliJ IDEA.
 * User: Shamil
 * Date: 22.07.14
 * Time: 10:56
 */
@Component
@Scope("session")
public class MonitoringPersistanceCachePage extends BasicWorkspacePage {
    private Statistics procesorStat;
    private Statistics reportStat;

    public Object getProcesorStat() {
        return procesorStat;
    }

    public Object getReportStat() {
        return reportStat;
    }

    @Override
    public void onShow() throws Exception {
        procesorStat = RuntimeContext.statisticPersistenceSession();
        reportStat = RuntimeContext.statisticReportPersistenceSession();
    }

    @Override
    public String getPageFilename() {
        return "monitoring/persistance_cache_monitor";
    }

    public Object update()  throws Exception  {
        onShow();
        procesorStat.getSecondLevelCacheHitCount();
        return null;
    }

}
