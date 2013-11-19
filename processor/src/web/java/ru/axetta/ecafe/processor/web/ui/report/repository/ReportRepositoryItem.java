/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.repository;

import ru.axetta.ecafe.processor.core.daoservices.context.ContextDAOServices;
import ru.axetta.ecafe.processor.core.persistence.ReportHandleRule;
import ru.axetta.ecafe.processor.core.persistence.ReportInfo;
import ru.axetta.ecafe.processor.core.report.ContragentPaymentReport;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.abstractpage.AbstractEntityItem;
import ru.axetta.ecafe.processor.web.ui.abstractpage.AbstractFilter;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.ListType;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReportRepositoryItem extends AbstractEntityItem<ReportInfo>  {
    public static class Filter extends AbstractFilter {
        String ruleName, reportName, orgNum, tag;
        Date createdDate, startDate, endDate;
        List<Long> idOfOrgList;
        private Long idOfContragentReceiver;
        private Long idOfContragentPayer;

        @Override
        public boolean isEmpty() {
            return (StringUtils.isEmpty(ruleName) && StringUtils.isEmpty(tag) && StringUtils.isEmpty(reportName)&& StringUtils.isEmpty(orgNum) && createdDate==null && startDate==null && endDate==null);
        }

        @Override
        public void clear() {
            ruleName =  reportName = orgNum = tag = "";
            createdDate = startDate = endDate = null;
        }

        @Override
        protected void apply(EntityManager entityManager, Criteria crit) {
            //  Ограничение на просмотр оргов для пользователя
            try {
                Long idOfUser = MainPage.getSessionInstance().getCurrentUser().getIdOfUser();
                ContextDAOServices.getInstance().buildOrgOrContragentRestriction(idOfUser, "idOfOrg",
                                        ContragentPaymentReport.PARAM_CONTRAGENT_RECEIVER_ID, crit);
            } catch (Exception e) {
            }
            if (idOfOrgList != null && idOfOrgList.size() > 0) {
                crit.add(Restrictions.in("idOfOrg", idOfOrgList));
            }
            if (!StringUtils.isEmpty(ruleName)) crit.add(Restrictions.like("ruleName", ruleName, MatchMode.ANYWHERE).ignoreCase());
            if (!StringUtils.isEmpty(tag)) crit.add(Restrictions.like("tag", tag, MatchMode.ANYWHERE).ignoreCase());
            if (!StringUtils.isEmpty(reportName)) crit.add(Restrictions.like("reportName", reportName, MatchMode.ANYWHERE).ignoreCase());
            if (!StringUtils.isEmpty(orgNum)) crit.add(Restrictions.eq("orgNum", orgNum));
            if (createdDate!=null) crit.add(Restrictions.and(
                    Restrictions.ge("createdDate", CalendarUtils.truncateToDayOfMonth(createdDate)),
                    Restrictions.le("createdDate", CalendarUtils.truncateToDayOfMonthAndAddDay(createdDate))));
            if (startDate!=null) {
                crit.add(Restrictions.ge("startDate", CalendarUtils.truncateToDayOfMonth(startDate)));
                if (endDate==null) endDate = CalendarUtils.addOneDay(startDate);
            }
            if (idOfContragentReceiver != null) {
                crit.add(Restrictions.eq(ContragentPaymentReport.PARAM_CONTRAGENT_RECEIVER_ID, idOfContragentReceiver));
            }
            if (idOfContragentPayer != null) {
                crit.add(Restrictions.eq(ContragentPaymentReport.PARAM_CONTRAGENT_PAYER_ID, idOfContragentPayer));
            }
            if (endDate!=null) {
                Calendar localCalendar = Calendar.getInstance();
                localCalendar.setTime(endDate);
                localCalendar.add(Calendar.DAY_OF_MONTH,1);
                crit.add(Restrictions.le("endDate", CalendarUtils.truncateToDayOfMonth(localCalendar.getTime())));
            }
        }

        public String getRuleName() {
            return ruleName;
        }

        public void setRuleName(String ruleName) {
            this.ruleName = ruleName;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getReportName() {
            return reportName;
        }

        public void setReportName(String reportName) {
            this.reportName = reportName;
        }

        public Date getCreatedDate() {
            return createdDate;
        }

        public void setCreatedDate(Date createdDate) {
            this.createdDate = createdDate;
        }

        public Date getStartDate() {
            return startDate;
        }

        public void setStartDate(Date startDate) {
            this.startDate = startDate;
        }

        public Date getEndDate() {
            return endDate;
        }

        public void setEndDate(Date endDate) {
            this.endDate = endDate;
        }

        public String getOrgNum() {
            return orgNum;
        }

        public void setOrgNum(String orgNum) {
            this.orgNum = orgNum;
        }

        public List<Long> getIdOfOrgList() {
            return idOfOrgList;
        }

        public void setIdOfOrgList(List<Long> idOfOrgList) {
            this.idOfOrgList = idOfOrgList;
        }

        public Long getIdOfContragentReceiver() {
            return idOfContragentReceiver;
        }

        public void setIdOfContragentReceiver(Long idOfContragentReceiver) {
            this.idOfContragentReceiver = idOfContragentReceiver;
        }

        public Long getIdOfContragentPayer() {
            return idOfContragentPayer;
        }

        public void setIdOfContragentPayer(Long idOfContragentPayer) {
            this.idOfContragentPayer = idOfContragentPayer;
        }
    }


    private Long idOfReportInfo;
    private String ruleName;
    private Integer documentFormat;
    private String reportName;
    private Date createdDate;
    private Long generationTime;
    private Date startDate;
    private Date endDate;
    private String reportFile;
    private String orgNum;
    private String tag;
    private Long idOfContragentReceiver;
    private String contragentReceiver;
    private Long idOfContragentPayer;
    private String contragentPayer;
    private List<Long> idOfOrgList;

    @Override
    public void fillForList(EntityManager entityManager, ReportInfo entity) {
        idOfReportInfo = entity.getIdOfReportInfo();
        ruleName = entity.getRuleName();
        documentFormat = entity.getDocumentFormat();
        reportName = entity.getReportName();
        createdDate = entity.getCreatedDate();
        generationTime = entity.getGenerationTime();
        startDate = entity.getStartDate();
        endDate = entity.getEndDate();
        reportFile = entity.getReportFile();
        orgNum = entity.getOrgNum();
        tag = entity.getTag();
        idOfContragentReceiver = entity.getIdOfContragentReceiver();
        contragentReceiver = entity.getContragentReceiver();
        idOfContragentPayer = entity.getIdOfContragentPayer();
        contragentPayer = entity.getContragentPayer();
    }

    @Override
    protected void fill(EntityManager entityManager, ReportInfo entity) {
        fillForList(entityManager, entity);
    }

    @Override
    protected void saveTo(EntityManager entityManager, ReportInfo entity) {
    }

    @Override
    public ReportInfo getEntity(EntityManager entityManager) {
        return entityManager.find(ReportInfo.class, idOfReportInfo);
    }

    @Override
    protected ReportInfo createEmptyEntity() {
        return null;
    }

    public Long getIdOfReportInfo() {
        return idOfReportInfo;
    }

    public String getRuleName() {
        return ruleName;
    }

    public Integer getDocumentFormat() {
        return documentFormat;
    }
    
    public String getDocumentFormatAsString() {
        return ReportHandleRule.getDocumentFormatAsString(documentFormat);
    }

    public String getReportName() {
        return reportName;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Long getGenerationTime() {
        return generationTime;
    }
    
    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getReportFile() {
        return reportFile;
    }

    public String getOrgNum() {
        return orgNum;
    }

    public String getTag() {
        return tag;
    }

    public Long getIdOfContragentReceiver() {
        return idOfContragentReceiver;
    }

    public void setIdOfContragentReceiver(Long idOfContragentReceiver) {
        this.idOfContragentReceiver = idOfContragentReceiver;
    }

    public String getContragentReceiver() {
        return contragentReceiver;
    }

    public String getContragentPayer() {
        return contragentPayer;
    }

    public void setContragentReceiver(String contragentReceiver) {
        this.contragentReceiver = contragentReceiver;
    }

    @Override
    public String toString() {
        return reportFile;
    }
}
