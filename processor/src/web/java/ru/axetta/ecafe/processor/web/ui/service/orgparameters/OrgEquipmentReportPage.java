/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.orgparameters;

import ru.axetta.ecafe.processor.web.ui.org.OrgListSelectPage;
import ru.axetta.ecafe.processor.web.ui.report.online.OnlineReportPage;

import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

//import ru.axetta.ecafe.processor.core.report.orgparameters.orgequipment.OrgEquipmentReportItem;

@Component
@Scope("session")
@DependsOn("runtimeContext")
public class OrgEquipmentReportPage extends OnlineReportPage implements OrgListSelectPage.CompleteHandlerList {
    //private static final Logger logger = LoggerFactory.getLogger(OrgEquipmentReportPage.class);
    //private String selectedDistricts = "";
    //private Boolean allFriendlyOrgs = false;
    //private List<SelectItem> listOfOrgDistricts;
    ////private List<OrgEquipmentReportItem> items = new LinkedList<>();
    //
    //private List<SelectItem> buildListOfOrgDistricts(Session session) {
    //    List<String> allDistricts;
    //    List<SelectItem> selectItemList = new LinkedList<SelectItem>();
    //    selectItemList.add(new SelectItem("", "Все"));
    //    try{
    //        allDistricts = DAOUtils.getAllDistinctDepartmentsFromOrgs(session);
    //        for(String district : allDistricts){
    //            selectItemList.add(new SelectItem(district, district));
    //        }
    //    } catch (Exception e){
    //        logger.error("Can't build Districts items", e);
    //    }
    //    return selectItemList;
    //}
    //
    //public String getSelectedDistricts() {
    //    return selectedDistricts;
    //}
    //
    //public void setSelectedDistricts(String selectedDistricts) {
    //    this.selectedDistricts = selectedDistricts;
    //}
    //
    //public Boolean getAllFriendlyOrgs() {
    //    return allFriendlyOrgs;
    //}
    //
    //public void setAllFriendlyOrgs(Boolean allFriendlyOrgs) {
    //    this.allFriendlyOrgs = allFriendlyOrgs;
    //}
    //
    //public List<SelectItem> getListOfOrgDistricts() {
    //    return listOfOrgDistricts;
    //}
    //
    //public void setListOfOrgDistricts(List<SelectItem> listOfOrgDistricts) {
    //    this.listOfOrgDistricts = listOfOrgDistricts;
    //}
    //
    ////public List<OrgEquipmentReportItem> getItems() {
    ////    return items;
    ////}
    ////
    ////public void setItems(List<OrgEquipmentReportItem> items) {
    ////    this.items = items;
    ////}
}
