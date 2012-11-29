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
import java.util.Date;
import java.util.List;

@Component
@Scope("session")
public class GoodRequestListPage extends BasicWorkspacePage {

    private List<GoodRequest> goodRequestList;
    private static Long idOfOrg;
    private Date baseDate = DateUtils.addMonths(new Date(), -1);
    private Date endDate = new Date();
    private Boolean deletedFlag;
    private Integer state;

    @PersistenceContext
    private EntityManager entityManager;

    public Object onSearch() throws Exception{
        reload();
        return null;
    }

    public Object onClear() throws Exception {
        baseDate = DateUtils.addMonths(new Date(), -1);
        endDate = new Date();
        deletedFlag = null;
        state = null;
        return null;
    }

    @Transactional
    public void reload() throws Exception{
        String where = "(createdDate between " +  baseDate.getTime() + " and " + endDate.getTime() + ")";
        where += " and orgowner=" + idOfOrg;
        if (deletedFlag != null) {
            where = (where.equals("")?"":where + " and ") + "deletedstate=" + deletedFlag;
        }
        if (state != null) {
            where = (where.equals("")?"":where + " and ") + "state=" + state;
        }

        where = (where.equals("")?"":" where ") + where;
        TypedQuery<GoodRequest> query = entityManager.createQuery("from GoodRequest " + where, GoodRequest.class);
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

    public SelectItem[] getStateSelectItemArray() {
        SelectItem[] items = new SelectItem[GoodRequest.GOOD_REQUEST_STATES.length];
        for (int i = 0; i < GoodRequest.GOOD_REQUEST_STATES.length; i++) {
            items[i] = new SelectItem(i, GoodRequest.GOOD_REQUEST_STATES[i]);
        }
        return items;
    }

    public static Long getIdOfOrg() {
        return idOfOrg;
    }

    public static void setIdOfOrg(Long idOfOrg) {
        GoodRequestListPage.idOfOrg = idOfOrg;
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

    public Boolean isDeleted() {
        return deletedFlag;
    }

    public void setDeletedFlag(Boolean deletedFlag) {
        this.deletedFlag = deletedFlag;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

}
