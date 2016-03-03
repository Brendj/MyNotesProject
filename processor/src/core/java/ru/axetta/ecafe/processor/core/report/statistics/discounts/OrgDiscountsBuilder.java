/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.discounts;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.OrganizationType;
import ru.axetta.ecafe.processor.core.persistence.utils.FriendlyOrganizationsInfoModel;
import ru.axetta.ecafe.processor.core.persistence.utils.OrgUtils;
import ru.axetta.ecafe.processor.core.report.BasicReportForOrgJob;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.statistics.discounts.model.CategoryItem;
import ru.axetta.ecafe.processor.core.report.statistics.discounts.model.ClientItem;
import ru.axetta.ecafe.processor.core.report.statistics.discounts.model.GroupItem;
import ru.axetta.ecafe.processor.core.report.statistics.discounts.model.OrgItem;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 24.02.16
 * Time: 17:58
 */
public class OrgDiscountsBuilder extends BasicReportForOrgJob.Builder {

    private final String templateFilename;
    private final String subReportDir;
    private List<CategoryItem> categoryTotal = new ArrayList<CategoryItem>();

    public OrgDiscountsBuilder(String templateFilename) {
        this.templateFilename = templateFilename;
        subReportDir = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath();
    }

    public void setCategoryTotal(List<CategoryItem> categoryTotal) {
        this.categoryTotal = categoryTotal;
    }

    @Override
    public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
        if (!(new File(this.templateFilename)).exists()) {
            throw new Exception(String.format("Не найден файл шаблона '%s'", templateFilename));
        }
        Long idOfOrg = Long.parseLong(
                StringUtils.trimToEmpty(getReportProperties().getProperty(ReportPropertiesUtils.P_ID_OF_ORG)));
        Boolean showReserve = Boolean.valueOf(getReportProperties().getProperty("showReserve"));
        Boolean showPayComplex = Boolean.valueOf(getReportProperties().getProperty("showPayComplex"));
        String orgFilter = StringUtils.trimToEmpty(getReportProperties().getProperty("orgFilter"));

        Date generateBeginTime = new Date();

        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("generateTime", new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime()));
        parameterMap.put("IS_IGNORE_PAGINATION", true);
        parameterMap.put("SUBREPORT_DIR", subReportDir);

        List<Long> idOfOrgList = getIdOfOrgList(session, idOfOrg, orgFilter);
        if (idOfOrgList.size() == 1) {
            parameterMap.put("isOneOrg", 1);
        }

        JRDataSource dataSource = buildDataSource(session, idOfOrgList, startTime, endTime, showReserve,
                showPayComplex);

        parameterMap.put("categoryItemAllOrg", categoryTotal);

        JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);

        Date generateEndTime = new Date();

        final long generateDuration = generateEndTime.getTime() - generateBeginTime.getTime();
        return new OrgDiscountsReport(generateBeginTime, generateDuration, jasperPrint, startTime, endTime);
    }

    private JRDataSource buildDataSource(Session session, List<Long> idOfOrgList, Date startTime, Date endTime,
            Boolean showReserve, Boolean showPayComplex) {
        CategoryItem.loadCategoriesMap(session);
        List<OrgItem> orgItemList = new ArrayList<OrgItem>();
        for (Long currentIdOfOrg : idOfOrgList) {
            OrgItem currentOrg = new OrgItem();
            Org orgPersistence = (Org) session.load(Org.class, currentIdOfOrg);
            currentOrg.setShortName(orgPersistence.getShortName());
            currentOrg.setGroupItemList(getGroupItemsByOrgId(session, currentIdOfOrg, showReserve, showPayComplex));
            currentOrg.countCategory();
            Collections.sort(currentOrg.getCategoryItemOrg());
            Collections.sort(currentOrg.getGroupItemList());
            orgItemList.add(currentOrg);
        }
        countAllCategories(orgItemList);

        return new JRBeanCollectionDataSource(orgItemList);
    }

    private List<GroupItem> getGroupItemsByOrgId(Session session, Long idOfOrg, Boolean showReserve,
            Boolean showPayComplex) {
        List<GroupItem> groupItems = new ArrayList<GroupItem>();
        String q =
                "select c.idOfClient, c.idOfClientGroup, c.clientGroup.groupName, c.person.firstName, c.person.secondName, c.person.surname, cd.categoryName "
                        + " from Client c, ClientsCategoryDiscount cc, CategoryDiscount cd"
                        + " where c.idOfClient=cc.idOfClient and " + " cc.idOfCategoryDiscount >= 0 and "
                        + " cd.idOfCategoryDiscount=cc.idOfCategoryDiscount and " + " c.org.idOfOrg = " + idOfOrg
                        + " order by c.idOfClientGroup, c.person.secondName";
        List clientList = session.createQuery(q).list();
        Long lastClientId = -1L;
        ClientItem lastClientItem = null;
        Long lastIdGroup = -1L;
        GroupItem lastGroupItem = null;
        for (Object record : clientList) {
            Object[] client = (Object[]) record;

            GroupItem groupItem = null;
            if (!lastIdGroup.equals((Long) client[1])) {
                groupItem = new GroupItem((String) client[2]);
                groupItems.add(groupItem);
                lastGroupItem = groupItem;
                lastIdGroup = (Long) client[1];
            } else {
                groupItem = lastGroupItem;
            }

            String name = String.format("%s %s %s", (String) client[5], (String) client[3], (String) client[4]);
            String category = (String) client[6];
            if (!showReserve && category.equals("Резерв")) {
                continue;
            }
            if (!showPayComplex && category.contains("Платное")) {
                continue;
            }
            if (!lastClientId.equals((Long) client[0])) {
                lastClientItem = groupItem.addClientItem(name, category);
                groupItem.countCategory(category);
                lastClientId = (Long) client[0];
            } else {
                lastClientItem.addCategory(category);
                groupItem.countCategory(category);
            }
        }
        for (GroupItem groupItem : groupItems) {
            groupItem.sortClients();
            Collections.sort(groupItem.getCategoryItemGroup());
        }
        return groupItems;
    }

    private void countAllCategories(List<OrgItem> orgItemList) {
        for (OrgItem orgItem : orgItemList) {
            List<CategoryItem> categoryItems = orgItem.getCategoryItemOrg();
            for (CategoryItem categoryItem : categoryItems) {
                if (categoryTotal.contains(categoryItem)) {
                    categoryTotal.get(categoryTotal.indexOf(categoryItem)).count(categoryItem.getTotalByCategory());
                } else {
                    CategoryItem newCategoryItem = new CategoryItem(categoryItem.getCategory());
                    newCategoryItem.setTotalByCategory(categoryItem.getTotalByCategory());
                    categoryTotal.add(newCategoryItem);
                }
            }
        }
        Collections.sort(categoryTotal);
    }

    private List<Long> getIdOfOrgList(Session session, Long idOfOrg, String orgFilter) {
        List<Long> idOfOrgList = new ArrayList<Long>();
        if (orgFilter.equals("")) {
            idOfOrgList.add(idOfOrg);
        } else {
            Set<FriendlyOrganizationsInfoModel> friendlyOrganizationsInfoModels = OrgUtils
                    .getMainBuildingAndFriendlyOrgsList(session, new ArrayList<Long>(Arrays.asList(idOfOrg)));

            for (FriendlyOrganizationsInfoModel organizationsInfoModel : friendlyOrganizationsInfoModels) {
                Set<Org> orgSet = organizationsInfoModel.getFriendlyOrganizationsSet();
                if (orgSet.isEmpty()) {
                    Org org = (Org) session.load(Org.class, organizationsInfoModel.getIdOfOrg());
                    if ((orgFilter.equals("Корпуса СОШ") && org.getType().equals(OrganizationType.SCHOOL)) || (
                            orgFilter.equals("Корпуса ДОУ") && org.getType().equals(OrganizationType.KINDERGARTEN)
                                    || orgFilter.equals("Все корпуса"))) {
                        idOfOrgList.add(organizationsInfoModel.getIdOfOrg());
                    }
                } else {
                    for (Org org : orgSet) {
                        if ((orgFilter.equals("Корпуса СОШ") && org.getType().equals(OrganizationType.SCHOOL)) || (
                                orgFilter.equals("Корпуса ДОУ") && org.getType().equals(OrganizationType.KINDERGARTEN)
                                        || orgFilter.equals("Все корпуса"))) {
                            idOfOrgList.add(org.getIdOfOrg());
                        }
                    }
                }
            }
        }
        return idOfOrgList;
    }

}
