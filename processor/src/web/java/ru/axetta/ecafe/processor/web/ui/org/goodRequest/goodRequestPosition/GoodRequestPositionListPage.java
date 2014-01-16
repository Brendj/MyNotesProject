/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.goodRequest.goodRequestPosition;

import ru.axetta.ecafe.processor.core.daoservices.commodity.accounting.GoodRequestRepository;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequest;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequestPosition;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Component
@Scope("session")
public class GoodRequestPositionListPage extends BasicWorkspacePage {

    private List<GoodRequestPosition> goodRequestPositionList;
    //private Long idOfGoodRequest;
    private GoodRequest goodRequest;

    //@PersistenceContext(unitName = "processorPU")
    //private EntityManager entityManager;

    @Autowired
    private GoodRequestRepository goodRequestRepository;

    @Override
    public void onShow() {}

    public Object onSearch() throws Exception {
        reload();
        show();
        return null;
    }

    //@Transactional
    public void reload() throws Exception {
        if(goodRequest!=null){
            goodRequestPositionList = goodRequestRepository.getGoodRequestPositionByGoodRequest(goodRequest);
        }

        //String where = "idofgoodsrequest=" + idOfGoodRequest;
        //where = (where.equals("")?"":" where ") + where;
        //TypedQuery<GoodRequestPosition> query = entityManager.createQuery("from GoodRequestPosition " + where, GoodRequestPosition.class);
        //goodRequestPositionList = query.getResultList();
    }

    public String getPageTitle() {
        return super.getPageTitle() + String.format(" (%d)", (goodRequestPositionList==null?0:goodRequestPositionList.size()));
    }

    public String getPageFilename() {
        return "org/good_request/positions";
    }

    public Boolean getEmptyGoodRequestPositionList(){
        return  this.goodRequestPositionList == null || this.goodRequestPositionList.isEmpty();
    }

    public List<GoodRequestPosition> getGoodRequestPositionList() {
        return goodRequestPositionList;
    }

    public void setGoodRequestPositionList(List<GoodRequestPosition> goodRequestPositionList) {
        this.goodRequestPositionList = goodRequestPositionList;
    }

    //public Long getIdOfGoodRequest() {
    //    return idOfGoodRequest;
    //}
    //
    //public void setIdOfGoodRequest(Long idOfGoodRequest) {
    //    this.idOfGoodRequest = idOfGoodRequest;
    //}

    public GoodRequest getGoodRequest() {
        return goodRequest;
    }

    public void setGoodRequest(GoodRequest goodRequest) {
        this.goodRequest = goodRequest;
    }
}
