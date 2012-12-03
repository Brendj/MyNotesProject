package ru.axetta.ecafe.processor.web.ui.org.goodRequest;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents.GoodRequest;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Scope("session")
public class GoodRequestListPage extends BasicWorkspacePage {

    private List<GoodRequest> goodRequestList;
    private Long idOfOrg;
    private Date baseDate = DateUtils.addMonths(new Date(), -1);
    private Date endDate = new Date();
    private Boolean useDeletedFilter = false;
    private Boolean deletedState;
    private Boolean useStateFilter = false;
    private List<Integer> stateList = new ArrayList<Integer>();

    SelectItem[] stateSelectItemList;

    @PersistenceContext
    private EntityManager entityManager;

    public GoodRequestListPage() {
        super();
        stateSelectItemList = new SelectItem[GoodRequest.GOOD_REQUEST_STATES.length];
        for (int i = 0; i < GoodRequest.GOOD_REQUEST_STATES.length; i++) {
            stateSelectItemList[i] = new SelectItem(i, GoodRequest.GOOD_REQUEST_STATES[i]);
        }
    }

    @Override
    public void onShow() {}

    public Object onSearch() throws Exception{
        reload();
        return null;
    }

    public Object onClear() throws Exception {
        baseDate = DateUtils.addMonths(new Date(), -1);
        endDate = new Date();
        useDeletedFilter = false;
        deletedState = null;
        useStateFilter = false;
        stateList.clear();
        return null;
    }

    @Transactional
    public void reload() throws Exception{
        String where = "(createdDate between " +  baseDate.getTime() + " and " + endDate.getTime() + ")";
        if (idOfOrg != null) {
            where = (where.equals("")?"":where + " and ") + "orgowner=" + idOfOrg;
        }
        if ((useDeletedFilter != null) && useDeletedFilter) {
            where = (where.equals("")?"":where + " and ") + "deletedstate=" + deletedState;
        }
        if ((useStateFilter != null) && useStateFilter && (stateList != null) && !stateList.isEmpty()) {
            where = (where.equals("")?"":where + " and ");
            for (Integer state : stateList) {
                if (state != null) {
                    where += "state=" + state + " or ";
                }
            }
            where = where.substring(0,where.length() - 4);
        }
        where = (where.equals("")?"":" where ") + where;
        TypedQuery<GoodRequest> query = entityManager.createQuery("from GoodRequest" + where, GoodRequest.class);
        goodRequestList = query.getResultList();
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
        if ((useDeletedFilter != null) && useDeletedFilter && deletedState != null) {
            if (deletedState) {
                filter.append(", включая удаленные");
            } else {
                filter.append(", не включая удаленные");
            }
        }
        if ((stateList != null) && !stateList.isEmpty()) {
            if (stateList.size() == 1) {
                filter.append(", только со статусом ");
            } else {
                filter.append(", только со статусами ");
            }
            for (int i = 0; i < stateList.size(); i++) {
                filter.append("\"" + GoodRequest.GOOD_REQUEST_STATES[stateList.get(i)] + "\", ");
            }
            filter = new StringBuffer().append(filter.substring(0, filter.length() - 2));
        }
        return filter.toString();
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

    public Boolean getUseDeletedFilter() {
        return useDeletedFilter;
    }

    public void setUseDeletedFilter(Boolean useDeletedFilter) {
        this.useDeletedFilter = useDeletedFilter;
    }

    public Boolean getUseStateFilter() {
        return useStateFilter;
    }

    public void setUseStateFilter(Boolean useStateFilter) {
        this.useStateFilter = useStateFilter;
    }

    public Boolean getDeletedState() {
        return deletedState;
    }

    public void setDeletedState(Boolean deletedState) {
        this.deletedState = deletedState;
    }

    public List<Integer> getStateList() {
        return stateList;
    }

    public void setStateList(List<Integer> stateList) {
        this.stateList = stateList;
    }

    public int getStatesListSize() {
        return GoodRequest.GOOD_REQUEST_STATES.length;
    }

    public void switchDeletedFilter() {
        useDeletedFilter = !useDeletedFilter;
    }

    public void switchStateFilter() {
        useStateFilter = !useStateFilter;
    }

}
