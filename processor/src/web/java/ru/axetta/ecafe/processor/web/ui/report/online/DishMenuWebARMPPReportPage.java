/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtAgeGroupItem;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtGroupItem;
import ru.axetta.ecafe.processor.core.report.*;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;
import ru.axetta.ecafe.processor.web.ui.client.ClientFilter;
import ru.axetta.ecafe.processor.web.ui.client.ClientSelectListPage;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentListSelectPage;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentSelectPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.*;

public class DishMenuWebARMPPReportPage extends OnlineReportPage implements ContragentListSelectPage.CompleteHandler {

    private final static Logger logger = LoggerFactory.getLogger(DishMenuWebARMPPReportPage.class);

    public String getPageFilename() {
        return "report/online/dish_menu_webarmpp_report";
    }

    private List<DishMenuWebArmPPItem> items;
    private Long selectidTypeFoodId;
    private Long selectidAgeGroup;
    private Boolean archived = false;

    private String contragentFilter = "Не выбрано";
    private String contragentIds;
    private List<ContragentItem> contragentItems = new ArrayList<ContragentItem>();

    public SelectItem[] getTypesOfFood() {
        List<WtGroupItem> wtGroupItems = DAOService.getInstance().getMapTypeFoods();
        SelectItem[] items = new SelectItem[wtGroupItems.size()+1];
        items[0]=new SelectItem(-1, "Не выбрано");
        int n=1;
        for (WtGroupItem wtGroupItem : wtGroupItems) {
            items[n]=new SelectItem(wtGroupItem.getIdOfGroupItem(), wtGroupItem.getDescription());
            ++n;
        }
        return items;
    }

    public SelectItem[] getAgeGroup() {
        List<WtAgeGroupItem> ageGroups = DAOService.getInstance().getAgeGroups();
        SelectItem[] items = new SelectItem[ageGroups.size()+1];
        items[0]=new SelectItem(-1, "Не выбрано");
        int n=1;
        for (WtAgeGroupItem wtAgeGroupItem : ageGroups) {
            items[n]=new SelectItem(wtAgeGroupItem.getIdOfAgeGroupItem(), wtAgeGroupItem.getDescription());
            ++n;
        }
        return items;
    }

    public Object buildReportHTML() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String templateFilename = checkIsExistFile();

        if (templateFilename == null) {
            return null;
        }
        DishMenuWebArmPPReport.Builder builder = new DishMenuWebArmPPReport.Builder(templateFilename);
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            try {
                persistenceSession = runtimeContext.createReportPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                builder.setReportProperties(buildProperties());
                items = builder.createDataSource(persistenceSession);
                persistenceTransaction.commit();
                persistenceTransaction = null;
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
            }
        } catch (Exception e) {
            logger.error("Failed export report : ", e);
            printError("Ошибка при подготовке отчета: " + e.getMessage());
        }
        return items;
    }

    public void generateXLS(ActionEvent event) {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String templateFilename = checkIsExistFile();
        if (templateFilename != null) {
            DetailedEnterEventReport.Builder builder = new DetailedEnterEventReport.Builder(templateFilename);
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            BasicReportJob report = null;
            try {
                persistenceSession = runtimeContext.createReportPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();

                if (idOfOrg == null) {
                    printError(String.format("Выберите организацию "));
                }

                //builder.setReportProperties(buildProperties(persistenceSession));

                report = builder.build(persistenceSession, startDate, endDate, localCalendar);
                persistenceTransaction.commit();
                persistenceTransaction = null;
            } catch (Exception e) {
                logger.error("Failed export report : ", e);
                printError("Ошибка при подготовке отчета: " + e.getMessage());
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
            }

            FacesContext facesContext = FacesContext.getCurrentInstance();
            try {
                if (report != null) {
                    HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext()
                            .getResponse();
                    ServletOutputStream servletOutputStream = response.getOutputStream();
                    facesContext.getResponseComplete();
                    facesContext.responseComplete();
                    response.setContentType("application/xls");
                    response.setHeader("Content-disposition", "inline;filename=DetailedEnterEventReport.xls");
                    JRXlsExporter xlsExporter = new JRXlsExporter();
                    xlsExporter.setParameter(JRCsvExporterParameter.JASPER_PRINT, report.getPrint());
                    xlsExporter.setParameter(JRCsvExporterParameter.OUTPUT_STREAM, servletOutputStream);
                    xlsExporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
                    xlsExporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
                    xlsExporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
                    xlsExporter.setParameter(JRCsvExporterParameter.CHARACTER_ENCODING, "windows-1251");
                    xlsExporter.exportReport();
                    servletOutputStream.close();
                }
            } catch (Exception e) {
                logAndPrintMessage("Ошибка при выгрузке отчета:", e);
            }
        }
    }

    private String checkIsExistFile() {
        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
        String templateShortFilename = "DetailedEnterEventReport.jasper";
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + templateShortFilename;
        if (!(new File(templateFilename)).exists()) {
            printError(String.format("Не найден файл шаблона '%s'", templateFilename));
            return null;
        }
        return templateFilename;
    }

    private Properties buildProperties() {
        Properties properties = new Properties();
        if (idOfOrgList != null && !idOfOrgList.isEmpty()) {
            properties.setProperty(ReportPropertiesUtils.P_ID_OF_ORG, StringUtils.join(idOfOrgList, ','));
        }

        if (contragentItems != null && !contragentItems.isEmpty()) {
            properties
                    .setProperty(DishMenuWebArmPPReport.P_ID_OF_CONTRAGENT, contragentIds);
        }
        if (selectidTypeFoodId != -1)
        {
            properties
                    .setProperty(DishMenuWebArmPPReport.P_ID_OF_TYPES_FOOD, selectidTypeFoodId.toString());
        }
        if (selectidAgeGroup != -1)
        {
            properties
                    .setProperty(DishMenuWebArmPPReport.P_ID_OF_AGE_GROUP, selectidTypeFoodId.toString());
        }
        properties
                .setProperty(DishMenuWebArmPPReport.P_ARCHIVED, Boolean.toString(archived));

        return properties;
    }

    @Override
    public void completeContragentListSelection(Session session, List<Long> idOfContragentList, int multiContrFlag,
            String classTypes) throws Exception {
        contragentItems.clear();
        for (Long idOfContragent : idOfContragentList) {
            Contragent currentContragent = (Contragent) session.load(Contragent.class, idOfContragent);
            ContragentItem contragentItem = new ContragentItem(currentContragent);
            contragentItems.add(contragentItem);
        }
        setContragentFilterInfo(contragentItems);
    }

    private void setContragentFilterInfo(List<ContragentItem> contragentItems) {
        StringBuilder str = new StringBuilder();
        StringBuilder ids = new StringBuilder();
        if (contragentItems.isEmpty()) {
            contragentFilter = "Не выбрано";
        } else {
            for (ContragentItem it : contragentItems) {
                if (str.length() > 0) {
                    str.append("; ");
                    ids.append(",");
                }
                str.append(it.getContragentName());
                ids.append(it.getIdOfContragent());
            }
            contragentFilter = str.toString();
        }
        contragentIds = ids.toString();
    }

    public PeriodTypeMenu getPeriodTypeMenu() {
        return periodTypeMenu;
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public List<DishMenuWebArmPPItem> getItems() {
        return items;
    }

    public void setItems(List<DishMenuWebArmPPItem> items1) {
        this.items = items1;
    }

    public Long getSelectidTypeFoodId() {
        return selectidTypeFoodId;
    }

    public void setSelectidTypeFoodId(Long selectidTypeFoodId) {
        this.selectidTypeFoodId = selectidTypeFoodId;
    }

    public Long getSelectidAgeGroup() {
        return selectidAgeGroup;
    }

    public void setSelectidAgeGroup(Long selectidAgeGroup) {
        this.selectidAgeGroup = selectidAgeGroup;
    }

    public Boolean getArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public String getContragentFilter() {
        return contragentFilter;
    }

    public void setContragentFilter(String contragentFilter) {
        this.contragentFilter = contragentFilter;
    }

    public String getContragentIds() {
        return contragentIds;
    }

    public void setContragentIds(String contragentIds) {
        this.contragentIds = contragentIds;
    }

    public static class ContragentItem {

        private final Long idOfContragent;
        private final String contragentName;

        public ContragentItem(Contragent contragent) {
            this.idOfContragent = contragent.getIdOfContragent();
            this.contragentName = contragent.getContragentName();
        }

        public ContragentItem() {
            this.idOfContragent = null;
            this.contragentName = null;
        }

        public Long getIdOfContragent() {
            return idOfContragent;
        }

        public String getContragentName() {
            return contragentName;
        }
    }
}
