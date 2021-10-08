/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.report.AllOrgsDiscountsReport;

import org.hibernate.Session;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 11.02.12
 * Time: 16:18
 * To change this template use File | Settings | File Templates.
 */
public class AllOrgsDiscountsReportPage extends OnlineReportPage {
    private AllOrgsDiscountsReport allOrgsDiscountsReport;

    private String region;

    private Boolean showAllOrgs = false;

    public String getPageFilename() {
        return "report/online/orgs_discounts_report";
    }

    public AllOrgsDiscountsReport getAllOrgsDiscountsReport() {
        return allOrgsDiscountsReport;
    }

    public void buildReport(Session session) throws Exception {
        //allOrgsDiscountsReport = new AllOrgsDiscountsReport();
        Properties props = addRegionProperty(null, region);
        props.put("showAllOrgs", Boolean.toString(showAllOrgs));
        AllOrgsDiscountsReport.Builder builder = new AllOrgsDiscountsReport.Builder();
        builder.setReportProperties(props);
        allOrgsDiscountsReport = builder.build(session);
    }

    public List<SelectItem> getRegions() {
        List<String> regions = DAOReadonlyService.getInstance().getRegions();
        List<SelectItem> items = new ArrayList<SelectItem>();
        items.add(new SelectItem(""));
        for(String reg : regions) {
            items.add(new SelectItem(reg));
        }
        return items;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Properties addRegionProperty(Properties props, String region) {
        if(props == null) {
            props = new Properties();
        }
        if(region != null && region.trim().length() > 0) {
            props.put("region", region);
        }
        return props;
    }

    public Boolean getShowAllOrgs() {
        return showAllOrgs;
    }

    public void setShowAllOrgs(Boolean showAllOrgs) {
        this.showAllOrgs = showAllOrgs;
    }
}
