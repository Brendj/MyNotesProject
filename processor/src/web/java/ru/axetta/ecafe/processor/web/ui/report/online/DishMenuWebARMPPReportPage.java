/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.export.JRCsvExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtAgeGroupItem;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtComplex;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtGroupItem;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.DishMenuWebArmPPItem;
import ru.axetta.ecafe.processor.core.report.DishMenuWebArmPPReport;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentListSelectPage;
import ru.axetta.ecafe.processor.web.ui.webTechnolog.ComplexListSelectPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

public class DishMenuWebARMPPReportPage extends OnlineReportPage
        implements ContragentListSelectPage.CompleteHandler, ComplexListSelectPage.CompleteHandler {

    private final static Logger logger = LoggerFactory.getLogger(DishMenuWebARMPPReportPage.class);

    public String getPageFilename() {
        return "report/online/dish_menu_webarmpp_report";
    }

    private List<DishMenuWebArmPPItem> items;
    private Long selectidTypeFoodId;
    private Long selectidAgeGroup;
    private Long selectArchived;
    private Long selectinBufet = 1L;
    private Long selectinComplex = 1L;

    private String contragentFilter = "Не выбрано";
    private String complexFilter = "Не выбрано";
    private String contragentIds;
    private String complexIds;
    private Long dishCount = 0L;
    private String seachName = "";
    private Boolean showContagentName = false;
    private List<ContragentItem> contragentItems = new ArrayList<ContragentItem>();
    private List<ComplexItem> complexItems = new ArrayList<ComplexItem>();

    public SelectItem[] getTypesOfFood() {
        List<WtGroupItem> wtGroupItems = DAOReadonlyService.getInstance().getMapTypeFoods();
        SelectItem[] items = new SelectItem[wtGroupItems.size() + 1];
        items[0] = new SelectItem(-1, "Не выбрано");
        int n = 1;
        for (WtGroupItem wtGroupItem : wtGroupItems) {
            items[n] = new SelectItem(wtGroupItem.getIdOfGroupItem(), wtGroupItem.getDescription());
            ++n;
        }
        return items;
    }

    public SelectItem[] getAgeGroup() {
        List<WtAgeGroupItem> ageGroups = DAOReadonlyService.getInstance().getAgeGroups();
        SelectItem[] items = new SelectItem[ageGroups.size() + 1];
        items[0] = new SelectItem(-1, "Не выбрано");
        int n = 1;
        for (WtAgeGroupItem wtAgeGroupItem : ageGroups) {
            items[n] = new SelectItem(wtAgeGroupItem.getIdOfAgeGroupItem(), wtAgeGroupItem.getDescription());
            ++n;
        }
        return items;
    }

    public SelectItem[] getArchiveds() {
        SelectItem[] items = new SelectItem[3];
        items[0] = new SelectItem(1, "Без архивных");
        items[1] = new SelectItem(2, "Включая архивные");
        items[2] = new SelectItem(3, "Только архивные");
        return items;
    }

    public SelectItem[] getFiltersForAddedColumns() {
        SelectItem[] items = new SelectItem[4];
        items[0] = new SelectItem(1, "Не выбрано");
        items[1] = new SelectItem(2, "Включая нахождения");
        items[2] = new SelectItem(3, "Только с нахождениями");
        items[3] = new SelectItem(4, "Без нахождения");
        return items;
    }

    public Object buildReportHTML() {
        boolean good = true;
        if (idOfOrgList.isEmpty() && contragentItems.isEmpty()) {
            printError("Выберите организацию или контрагента");
            items = new ArrayList<>();
            setDishCount(0L);
            good = false;
        }
        if (!seachName.trim().isEmpty() && seachName.trim().length() < 3) {
            printError("Фильтр по названию должен содержать минимум 3 символа");
            items = new ArrayList<>();
            setDishCount(0L);
            good = false;
        }
        if (good) {
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
                    items = builder.createDataSource(persistenceSession, selectinBufet.intValue(), selectinComplex.intValue());
                    if (items != null)
                        setDishCount((long) items.size());
                    else
                        setDishCount(0L);
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
        }
        return items;
    }

    public void generateXLS(ActionEvent event) {
        boolean good = true;
        if (idOfOrgList.isEmpty() && contragentItems.isEmpty()) {
            printError("Выберите организацию или контрагента");
            items = new ArrayList<>();
            setDishCount(0L);
            good = false;
        }
        if (!seachName.trim().isEmpty() && seachName.trim().length() < 3) {
            printError("Фильтр по названию должен содержать минимум 3 символа");
            items = new ArrayList<>();
            setDishCount(0L);
            good = false;
        }
        if (good)
        {
            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            String templateFilename = checkIsExistFile();
            if (templateFilename != null) {
                DishMenuWebArmPPReport.Builder builder = new DishMenuWebArmPPReport.Builder(templateFilename);
                Session persistenceSession = null;
                Transaction persistenceTransaction = null;
                BasicReportJob report = null;
                try {
                    persistenceSession = runtimeContext.createReportPersistenceSession();
                    persistenceTransaction = persistenceSession.beginTransaction();
                    builder.setReportProperties(buildProperties());
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
                        response.setHeader("Content-disposition", "inline;filename=DishMenuReport.xls");
                        JRXlsExporter xlsExporter = new JRXlsExporter();
                        xlsExporter.setParameter(JRCsvExporterParameter.JASPER_PRINT, report.getPrint());
                        xlsExporter.setParameter(JRCsvExporterParameter.OUTPUT_STREAM, servletOutputStream);
                        xlsExporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
                        xlsExporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
                        xlsExporter
                                .setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
                        xlsExporter.setParameter(JRCsvExporterParameter.CHARACTER_ENCODING, "windows-1251");
                        xlsExporter.exportReport();
                        servletOutputStream.close();
                    }
                } catch (Exception e) {
                    logAndPrintMessage("Ошибка при выгрузке отчета:", e);
                }
            }
        }
    }

    private String checkIsExistFile() {
        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
        String templateShortFilename;
        boolean inBufetBool = Boolean.parseBoolean(renderBufet());
        boolean inComplexBool = Boolean.parseBoolean(renderComplex());
        if (inBufetBool && inComplexBool) {
            templateShortFilename = "dishARMReport_full.jasper";
        } else {
            if (!inBufetBool && !inComplexBool) {
                templateShortFilename = "dishARMReport.jasper";
            } else {
                if (inBufetBool && !inComplexBool) {
                    templateShortFilename = "dishARMReport_menu.jasper";
                } else {
                    templateShortFilename = "dishARMReport_complex.jasper";
                }
            }
        }
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + templateShortFilename;
        if (!(new File(templateFilename)).exists()) {
            printError(String.format("Не найден файл шаблона '%s'", templateFilename));
            return null;
        }


        return templateFilename;
    }

    public String  renderBufet() {
        if (selectinBufet.intValue()==2 || selectinBufet.intValue()==3)
            return "true";
        else
            return "false";
    }

    public String renderComplex() {
        if (selectinComplex.intValue()==2 || selectinComplex.intValue()==3)
            return "true";
        else
            return "false";
    }

    private Properties buildProperties() {
        Properties properties = new Properties();
        if (idOfOrgList != null && !idOfOrgList.isEmpty()) {
            properties.setProperty(ReportPropertiesUtils.P_ID_OF_ORG, StringUtils.join(idOfOrgList, ','));
        }

        if (contragentItems != null && !contragentItems.isEmpty()) {
            properties.setProperty(DishMenuWebArmPPReport.P_ID_OF_CONTRAGENT, contragentIds);
        }

        if (complexItems != null && !complexItems.isEmpty()) {
            properties.setProperty(DishMenuWebArmPPReport.P_ID_OF_COMPLEXES, complexIds);
        }
        if (selectidTypeFoodId != -1) {
            properties.setProperty(DishMenuWebArmPPReport.P_ID_OF_TYPES_FOOD, selectidTypeFoodId.toString());
        }
        if (selectidAgeGroup != -1) {
            properties.setProperty(DishMenuWebArmPPReport.P_ID_OF_AGE_GROUP, selectidAgeGroup.toString());
        }

        if (!seachName.trim().isEmpty())
        {
            properties.setProperty(DishMenuWebArmPPReport.P_DISHNAME, seachName.trim());
        }
        properties.setProperty(DishMenuWebArmPPReport.P_ARCHIVED, selectArchived.toString());
        properties.setProperty(DishMenuWebArmPPReport.P_BUFET, selectinBufet.toString());
        properties.setProperty(DishMenuWebArmPPReport.P_COMPLEX, selectinComplex.toString());
        properties.setProperty(DishMenuWebArmPPReport.P_SHOW_CONTAGENT, showContagentName.toString());

        return properties;
    }

    public Object showContragentListSelectPage() {
        if (idOfOrgList != null && !idOfOrgList.isEmpty()) {
            MainPage.getSessionInstance().showContragentListSelectPage(idOfOrgList);
        } else {
            MainPage.getSessionInstance().showContragentListSelectPage();
        }
        return null;
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
        if (contragentItems != null && !contragentItems.isEmpty())
            changeOrgsForContagents(contragentItems, new ArrayList<Long>(idOfOrgList));
        setComplexFilterInfo(new ArrayList<ComplexItem>());
    }

    private void changeOrgsForContagents(List<ContragentItem> contragentItems, List<Long> idofOrgs) {
        if (contragentItems != null && contragentItems != null) {
            idOfOrgList = new ArrayList<>();
            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                persistenceSession = runtimeContext.createReportPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                List<Long> idOrgs = new ArrayList<>();
                for (ContragentItem contragentItem : contragentItems) {
                    Contragent contragent = (Contragent) persistenceSession
                            .get(Contragent.class, contragentItem.getIdOfContragent());
                    for (Org org : contragent.getOrgs()) {
                        idOrgs.add(org.getIdOfOrg());
                    }
                }
                for (Long orgid : idofOrgs) {
                    if (idOrgs.contains(orgid)) {
                        idOfOrgList.add(orgid);
                    }
                }
                Map<Long, String> orgMap = new HashMap<>();
                for (Long idOfOrgs : idOfOrgList) {
                    Org org = (Org) persistenceSession.get(Org.class, idOfOrgs);
                    orgMap.put(org.getIdOfOrg(), org.getShortName());
                }
                setOrgFilterInfo(orgMap);
                persistenceTransaction.commit();
                persistenceTransaction = null;
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
            }
        }
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

    public String getGetStringIdOfOrgList() {
        return idOfOrgList.toString().replaceAll("[^0-9,]","");
    }

    public boolean isEmptySelectedOrg() {
        if (idOfOrgList == null)
            return true;
        else
            return idOfOrgList.isEmpty();
    }


    public Object showOrgListSelectPage() {
        if (contragentItems != null && !contragentItems.isEmpty()) {
            List<Long> idOfContragentList = new ArrayList<>();
            for (ContragentItem contragentItem : contragentItems) {
                idOfContragentList.add(contragentItem.getIdOfContragent());
            }
            //MainPage.getSessionInstance().showOrgListSelectPageWebArm(idOfContragentList, Boolean.TRUE);
            MainPage.getSessionInstance().showOrgListSelectPageWebArm(idOfContragentList, null);
        } else {
            //MainPage.getSessionInstance().showOrgListSelectPageWebArm(Boolean.TRUE);
            MainPage.getSessionInstance().showOrgListSelectPageWebArm(null);
        }
        return null;
    }

    @Override
    public void completeOrgListSelection(Map<Long, String> orgMap) throws Exception {
        if (orgMap != null) {
            setOrgFilterInfo(orgMap);
            if (idOfOrgList != null && !idOfOrgList.isEmpty())
                changeContagentsForOrgs(idOfOrgList, new ArrayList<>(contragentItems));
            setComplexFilterInfo(new ArrayList<ComplexItem>());
        }
    }

    public void setOrgFilterInfo(Map<Long, String> orgMap) {
        idOfOrgList = new ArrayList<Long>();
        if (orgMap.isEmpty()) {
            filter = "Не выбрано";
        } else {
            filter = "";
            for (Long idOfOrg : orgMap.keySet()) {
                idOfOrgList.add(idOfOrg);
                filter = filter.concat(orgMap.get(idOfOrg) + "; ");
            }
            filter = filter.substring(0, filter.length() - 1);
        }
    }

    private void changeContagentsForOrgs(List<Long> idOfOrgList, List<ContragentItem> contragentItems) {
        if (idOfOrgList != null && !idOfOrgList.isEmpty()) {
            this.contragentItems = new ArrayList<>();
            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                persistenceSession = runtimeContext.createReportPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                List<Long> contragentsId = new ArrayList<>();
                for (Long idOrg : idOfOrgList) {
                    Org org = (Org) persistenceSession.get(Org.class, idOrg);
                    contragentsId.add(org.getDefaultSupplier().getIdOfContragent());
                }
                for (ContragentItem contragentItem : contragentItems) {
                    if (contragentsId.contains(contragentItem.idOfContragent)) {
                        this.contragentItems.add(contragentItem);
                    }
                }
                setContragentFilterInfo(this.contragentItems);
                persistenceTransaction.commit();
                persistenceTransaction = null;
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
            }
        }
    }

    public boolean isShowComplexList() {
        if (idOfOrgList == null || idOfOrgList.isEmpty())
        {
            if (contragentItems == null || contragentItems.isEmpty())
                return false;
            else
                return true;
        }
        else
            return true;

    }

    @Override
    public void complexListSelection(Session session, List<Long> idOfComplexs) throws Exception {
        complexItems.clear();
        for (Long idOfComplex : idOfComplexs) {
            WtComplex wtComplex = (WtComplex) session.load(WtComplex.class, idOfComplex);
            ComplexItem complexItem = new ComplexItem(wtComplex);
            complexItems.add(complexItem);
        }
        setComplexFilterInfo(complexItems);
    }

    private void setComplexFilterInfo(List<ComplexItem> complexItems) {
        StringBuilder str = new StringBuilder();
        StringBuilder ids = new StringBuilder();
        if (complexItems.isEmpty()) {
            complexFilter = "Не выбрано";
        } else {
            for (ComplexItem it : complexItems) {
                if (str.length() > 0) {
                    str.append("; ");
                    ids.append(",");
                }
                str.append(it.getComplexName());
                ids.append(it.getIdOfComplex());
            }
            complexFilter = str.toString();
        }
        complexIds = ids.toString();
    }

    public String getColourForSell(DishMenuWebArmPPItem dishMenuWebArmPPItem)
    {
        if (dishMenuWebArmPPItem.getArchived() == 1)
        {
            return "background: #cccccc;";
        }
        else
            return "";
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

    public String getComplexIds() {
        return complexIds;
    }

    public void setComplexIds(String complexIds) {
        this.complexIds = complexIds;
    }

    public String getComplexFilter() {
        return complexFilter;
    }

    public void setComplexFilter(String complexFilter) {
        this.complexFilter = complexFilter;
    }

    public List<ComplexItem> getComplexItems() {
        return complexItems;
    }

    public void setComplexItems(List<ComplexItem> complexItems) {
        this.complexItems = complexItems;
    }

    public Long getSelectArchived() {
        return selectArchived;
    }

    public void setSelectArchived(Long selectArchived) {
        this.selectArchived = selectArchived;
    }

    public Long getSelectinBufet() {
        return selectinBufet;
    }

    public void setSelectinBufet(Long selectinBufet) {
        this.selectinBufet = selectinBufet;
    }

    public Long getSelectinComplex() {
        return selectinComplex;
    }

    public void setSelectinComplex(Long selectinComplex) {
        this.selectinComplex = selectinComplex;
    }

    public Long getDishCount() {
        return dishCount;
    }

    public void setDishCount(Long dishCount) {
        this.dishCount = dishCount;
    }

    public String getSeachName() {
        return seachName;
    }

    public void setSeachName(String seachName) {
        this.seachName = seachName;
    }

    public Boolean getShowContagentName() {
        return showContagentName;
    }

    public void setShowContagentName(Boolean showContagentName) {
        this.showContagentName = showContagentName;
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

    public static class ComplexItem {

        private final Long idOfComplex;
        private final String complexName;


        public ComplexItem(WtComplex wtComplex) {
            this.idOfComplex = wtComplex.getIdOfComplex();
            this.complexName = wtComplex.getName();
        }

        public String getComplexName() {
            return complexName;
        }

        public Long getIdOfComplex() {
            return idOfComplex;
        }
    }
}