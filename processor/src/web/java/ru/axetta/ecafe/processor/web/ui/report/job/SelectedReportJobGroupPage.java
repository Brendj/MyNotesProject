/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.job;

import ru.axetta.ecafe.processor.core.persistence.SchedulerJob;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.classic.Session;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 07.06.2010
 * Time: 14:23:22
 * To change this template use File | Settings | File Templates.
 */
public class SelectedReportJobGroupPage extends BasicWorkspacePage {

    private String title;

    public String getTitle() {
        return title;
    }

    public void fill(Session session, Long idOfReportJob) throws Exception {
        SchedulerJob schedulerJob = (SchedulerJob) session.load(SchedulerJob.class, idOfReportJob);
        if (null == schedulerJob) {
            this.title = null;
        } else {
            this.title = String.format("%d: %s", schedulerJob.getIdOfSchedulerJob(), schedulerJob.getJobName());
        }
    }

}