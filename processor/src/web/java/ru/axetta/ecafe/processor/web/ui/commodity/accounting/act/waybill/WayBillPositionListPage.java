package ru.axetta.ecafe.processor.web.ui.commodity.accounting.act.waybill;

import ru.axetta.ecafe.processor.core.daoservices.commodity.accounting.WayBillPositionService;
import ru.axetta.ecafe.processor.core.daoservices.commodity.accounting.items.WayBillPositionItem;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 05.11.13
 * Time: 11:58
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class WayBillPositionListPage extends BasicWorkspacePage {

    private List<WayBillPositionItem> wayBillPositionItems = new ArrayList<WayBillPositionItem>();
    private String shortName;
    private WayBillItem wayBillItem;
    private String status;
    private Boolean deletedState = true;
    @Autowired
    private WayBillPositionService service;

    @Override
    public void onShow() throws Exception {
        if(wayBillItem!=null && wayBillItem.getIdOfWayBill()!=null){
            wayBillPositionItems = service.findByWayBill(wayBillItem.getIdOfWayBill());
        }
    }

    public Object reset(){
        deletedState = true;
        wayBillItem = null;
        wayBillPositionItems = new ArrayList<WayBillPositionItem>();
        return null;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getDeletedState() {
        return deletedState;
    }

    public void setDeletedState(Boolean deletedState) {
        this.deletedState = deletedState;
    }

    public WayBillItem getWayBillItem() {
        return wayBillItem;
    }

    public void setWayBillItem(WayBillItem wayBillItem) {
        this.wayBillItem = wayBillItem;
    }

    public List<WayBillPositionItem> getWayBillPositionItems() {
        return wayBillPositionItems;
    }

    @Override
    public String getPageFilename() {
        return "commodity_accounting/waybill/waybillposition";
    }

}
