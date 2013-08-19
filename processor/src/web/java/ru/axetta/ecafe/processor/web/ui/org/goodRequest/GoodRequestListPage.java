package ru.axetta.ecafe.processor.web.ui.org.goodRequest;

import ru.axetta.ecafe.processor.core.daoservices.commodity.accounting.GoodRequestService;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents.DocumentState;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents.GoodRequest;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.model.SelectItem;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Scope("session")
public class GoodRequestListPage extends BasicWorkspacePage {

    private List<GoodRequest> goodRequestList;
    private Long idOfOrg;
    private Date baseDate = DateUtils.addMonths(new Date(), -1);
    private Date endDate = new Date();
    private Integer deletedState = 2;
    private List<DocumentState> stateList = new ArrayList<DocumentState>();
    private Integer[] documentState;
    private SelectItem[] stateSelectItemList;
    private static DocumentState[] documentStates = DocumentState.values();

    @Autowired
    private GoodRequestService goodRequestService;

    @Override
    public void onShow() {
        stateSelectItemList = new SelectItem[documentStates.length];
        for (int i = 0; i < documentStates.length; i++) {
            stateSelectItemList[i] = new SelectItem(i, documentStates[i].toString());
        }
        baseDate = DateUtils.addMonths(new Date(), -1);
        endDate = new Date();
        deletedState = 2;
        stateList.clear();
    }

    public Object onSearch() throws Exception{
        reload();
        return null;
    }

    public Object onClear() throws Exception {
        onShow();
        return null;
    }

    public void reload() throws Exception{
        stateList.clear();
        for (Integer i: documentState){
            stateList.add(DocumentState.values()[i]);
        }
        Calendar localCalendar = Calendar.getInstance();
        localCalendar.setTime(endDate);
        localCalendar.add(Calendar.DAY_OF_MONTH,1);
        Date end = localCalendar.getTime();
        goodRequestList = goodRequestService.findByFilter(idOfOrg,stateList,baseDate,end,deletedState);
    }

    public String getPageTitle() {
        return super.getPageTitle() + String.format(" (%d)", (goodRequestList==null?0:goodRequestList.size()));
    }

    public String getPageFilename() {
        return "org/good_request/list";
    }

    public Boolean getEmptyGoodRequestList(){
        return  this.goodRequestList == null || this.goodRequestList.isEmpty();
    }

    public SelectItem[] getStateSelectItemList() {
        return stateSelectItemList;
    }

    public String getFilter() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        StringBuffer filter = new StringBuffer();
        filter.append(sdf.format(baseDate));
        filter.append(" - ");
        filter.append(sdf.format(endDate));
        if (deletedState != 2) {
            if (deletedState == 1) {
                filter.append(", только удаленные");
            } else {
                filter.append(", только неудаленные");
            }
        }
        if ((stateList != null) && !stateList.isEmpty()) {
            if (stateList.size() == 1) {
                filter.append(", только со статусом ");
            } else {
                filter.append(", только со статусами ");
            }
            for (DocumentState aStateList : stateList) {
                filter.append("\"").append(documentStates[aStateList.ordinal()].toString()).append("\", ");
            }
            filter = new StringBuffer().append(filter.substring(0, filter.length() - 2));
        }
        return filter.toString();
    }

    public Integer[] getDocumentState() {
        return documentState;
    }

    public void setDocumentState(Integer[] documentState) {
        this.documentState = documentState;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public List<GoodRequest> getGoodRequestList() {
        return goodRequestList;
    }

    public void setGoodRequestList(List<GoodRequest> goodRequestList) {
        this.goodRequestList = goodRequestList;
    }

    public Date getBaseDate() {
        return baseDate;
    }

    public void setBaseDate(Date baseDate) {
        this.baseDate = baseDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getDeletedState() {
        return deletedState;
    }

    public void setDeletedState(Integer deletedState) {
        this.deletedState = deletedState;
    }

}
