package ru.axetta.ecafe.processor.web.ui.org.goodRequest;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents.GoodRequest;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents.RequestState;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
@Scope("session")
public class GoodRequestListPage extends BasicWorkspacePage {

    private List<GoodRequest> goodRequestList;
    private Long idOfOrg;
    private Date baseDate = DateUtils.addMonths(new Date(), -1);
    private Date endDate = new Date();
    private Integer deletedState = 2;
    private List<RequestState> stateList = new ArrayList<RequestState>();
    private Integer[] requestState;
    private SelectItem[] stateSelectItemList;
    private static RequestState[] requestStates = RequestState.values();

    @Autowired
    private GoodRequestService goodRequestService;

    @Override
    public void onShow() {
        stateSelectItemList = new SelectItem[requestStates.length];
        for (int i = 0; i < requestStates.length; i++) {
            stateSelectItemList[i] = new SelectItem(i, requestStates[i].toString());
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
        for (Integer i: requestState){
            stateList.add(RequestState.values()[i]);
        }
        goodRequestList = goodRequestService.findByFilter(idOfOrg,stateList,baseDate,endDate,deletedState);
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
            for (RequestState aStateList : stateList) {
                filter.append("\"").append(requestStates[aStateList.ordinal()].toString()).append("\", ");
            }
            filter = new StringBuffer().append(filter.substring(0, filter.length() - 2));
        }
        return filter.toString();
    }

    public Integer[] getRequestState() {
        return requestState;
    }

    public void setRequestState(Integer[] requestState) {
        this.requestState = requestState;
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
