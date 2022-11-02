/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.migrants;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.MigrantsUtils;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 11.05.16
 * Time: 15:30
 */

public class MigrantsProcessor extends AbstractProcessor<ResMigrants> {
    private static final Logger logger = LoggerFactory.getLogger(MigrantsProcessor.class);
    private final Migrants migrants;
    private List<Migrant> migrantsForOutRequests;
    private List<VisitReqResolutionHist> resolutionsForInRequests;

    public MigrantsProcessor(Session persistenceSession, Migrants migrants) {
        super(persistenceSession);
        this.migrants = migrants;
        migrantsForOutRequests = new ArrayList<Migrant>();
        resolutionsForInRequests = new ArrayList<VisitReqResolutionHist>();
    }

    @Override
    public ResMigrants process() throws Exception {
        ResMigrants result = new ResMigrants();
        List<ResOutcomeMigrationRequestsItem> outcomeMigrationRequestsItems = new ArrayList<ResOutcomeMigrationRequestsItem>();
        List<ResOutcomeMigrationRequestsHistoryItem> outcomeMigrationRequestsHistoryItems = new ArrayList<ResOutcomeMigrationRequestsHistoryItem>();
        List<ResIncomeMigrationRequestsHistoryItem> incomeMigrationRequestsHistoryItems = new ArrayList<ResIncomeMigrationRequestsHistoryItem>();

        if (processOutMigReqItems(outcomeMigrationRequestsItems)) {
            return null;
        }
        if (processOutMigReqHisItems(outcomeMigrationRequestsHistoryItems)) {
            return null;
        }
        if (processInMigReqHisItems(incomeMigrationRequestsHistoryItems)) {
            return null;
        }

        result.setResOutcomeMigrationRequestsItems(outcomeMigrationRequestsItems);
        result.setResOutcomeMigrationRequestsHistoryItems(outcomeMigrationRequestsHistoryItems);
        result.setResIncomeMigrationRequestsHistoryItems(incomeMigrationRequestsHistoryItems);
        return result;
    }

    public MigrantsData processData() throws Exception {
        MigrantsData result = new MigrantsData();
        List<ResIncomeMigrationRequestsItem> incomeMigrationRequestsItems = new ArrayList<ResIncomeMigrationRequestsItem>();
        List<ResIncomeMigrationRequestsHistoryItem> incomeMigrationRequestsHistoryItems = new ArrayList<ResIncomeMigrationRequestsHistoryItem>();
        List<ResOutcomeMigrationRequestsItem> outcomeMigrationRequestsItems = new ArrayList<ResOutcomeMigrationRequestsItem>();
        List<ResOutcomeMigrationRequestsHistoryItem> outcomeMigrationRequestsHistoryItems = new ArrayList<ResOutcomeMigrationRequestsHistoryItem>();

        processCurrentActiveIncomeReqs();
        processResInMigReqItems(incomeMigrationRequestsItems);
        processResInMigReqHisItems(incomeMigrationRequestsHistoryItems);
        processResOutMigReqItems(outcomeMigrationRequestsItems);
        processResOutMigReqHisItems(outcomeMigrationRequestsHistoryItems);

        result.setIncomeMigrationRequestsItems(incomeMigrationRequestsItems);
        result.setIncomeMigrationRequestsHistoryItems(incomeMigrationRequestsHistoryItems);
        result.setOutcomeMigrationRequestsItems(outcomeMigrationRequestsItems);
        result.setOutcomeMigrationRequestsHistoryItems(outcomeMigrationRequestsHistoryItems);
        return result;
    }

    private void updateBatch(List<CompositeIdOfMigrant> list) {
        Query query = session.createQuery("update Migrant set syncState = :syncState where compositeIdOfMigrant in :list");
        query.setParameter("syncState", Migrant.NOT_SYNCHRONIZED);
        query.setParameterList("list", list);
        query.executeUpdate();
        list.clear();
        session.flush();
    }

    private void processCurrentActiveIncomeReqs() throws Exception {
        List<Migrant> currentMigrants = MigrantsUtils.getSyncedMigrantsForOrg(session, migrants.getIdOfOrg());
        List<Migrant> currentMigrantsForSync = new ArrayList<Migrant>(currentMigrants.size());
        List<CompositeIdOfMigrant> list = new ArrayList<>();
        for(Migrant m : currentMigrants){
            if(!migrants.getCurrentActiveIncome().contains(m.getCompositeIdOfMigrant())){
                //m.setSyncState(Migrant.NOT_SYNCHRONIZED);
                //session.save(m);
                list.add(m.getCompositeIdOfMigrant());
                if (list.size() == 100) {
                    updateBatch(list);
                }
                currentMigrantsForSync.add(m);
            }
        }
        if (list.size() > 0) {
            updateBatch(list);
        }
        resolutionsForInRequests.addAll(MigrantsUtils.getResolutionsForMigrants(session, currentMigrantsForSync));
    }

    private void processResOutMigReqHisItems(
            List<ResOutcomeMigrationRequestsHistoryItem> outcomeMigrationRequestsHistoryItems) throws Exception {
        ResOutcomeMigrationRequestsHistoryItem outMigReqHisItem;
        List<VisitReqResolutionHist> visitReqResolutionHistList1 = MigrantsUtils.getOutcomeResolutionsForOrg(session,
                migrants.getIdOfOrg());
        if(migrantsForOutRequests.size() > 0){
            visitReqResolutionHistList1.addAll(MigrantsUtils.getResolutionsForMigrants(session, migrantsForOutRequests));
        }
        // remove duplicates
        visitReqResolutionHistList1 =
                new ArrayList<VisitReqResolutionHist>(new LinkedHashSet<VisitReqResolutionHist>(visitReqResolutionHistList1));
        for(VisitReqResolutionHist vReqHis : visitReqResolutionHistList1){
            outMigReqHisItem = new ResOutcomeMigrationRequestsHistoryItem();
            if(vReqHis.getResolution() != VisitReqResolutionHist.RES_OVERDUE_SERVER){
                outMigReqHisItem.setIdOfOrgIssuer(vReqHis.getOrgResol().getIdOfOrg());
                outMigReqHisItem.setResolution(vReqHis.getResolution());
            } else {
                outMigReqHisItem.setIdOfOrgIssuer(-1L);
                outMigReqHisItem.setResolution(VisitReqResolutionHist.RES_OVERDUE);
            }
            outMigReqHisItem.setIdOfRequest(vReqHis.getCompositeIdOfVisitReqResolutionHist().getIdOfRequest());
            outMigReqHisItem.setResolutionDateTime(vReqHis.getResolutionDateTime());
            outMigReqHisItem.setResolutionCause(vReqHis.getResolutionCause());
            if(vReqHis.getClientResol() != null) {
                outMigReqHisItem.setIdOfClientResol(vReqHis.getClientResol().getIdOfClient());
            } else {
                outMigReqHisItem.setIdOfClientResol(-1L);
            }
            outMigReqHisItem.setContactInfo(vReqHis.getContactInfo());
            outMigReqHisItem.setInitiator(vReqHis.getInitiator());
            outcomeMigrationRequestsHistoryItems.add(outMigReqHisItem);
        }
        List<VisitReqResolutionHist> param = new ArrayList<>();
        for (VisitReqResolutionHist vReqHis : visitReqResolutionHistList1) {
            param.add(vReqHis);
            if (param.size() == 1000) {
                Query query = session.createQuery("update VisitReqResolutionHist v set syncState = :syncState where v in :list");
                query.setParameter("syncState", VisitReqResolutionHist.SYNCHRONIZED);
                query.setParameterList("list", param);
                query.executeUpdate();
                param.clear();
            }
        }
        if (param.size() > 0) {
            Query query = session.createQuery("update VisitReqResolutionHist v set syncState = :syncState where v in :list");
            query.setParameter("syncState", VisitReqResolutionHist.SYNCHRONIZED);
            query.setParameterList("list", param);
            query.executeUpdate();
        }
    }

    private void processResInMigReqHisItems(
            List<ResIncomeMigrationRequestsHistoryItem> incomeMigrationRequestsHistoryItems) throws Exception {
        ResIncomeMigrationRequestsHistoryItem inMigReqHisItem;
        List<VisitReqResolutionHist> visitReqResolutionHistList = MigrantsUtils.getIncomeResolutionsForOrg(session, migrants.getIdOfOrg());
        for(VisitReqResolutionHist v : resolutionsForInRequests){
            if(!visitReqResolutionHistList.contains(v)){
                visitReqResolutionHistList.add(v);
            }
        }
        for(VisitReqResolutionHist vReqHis : visitReqResolutionHistList){
            inMigReqHisItem = new ResIncomeMigrationRequestsHistoryItem();
            if(vReqHis.getResolution() != VisitReqResolutionHist.RES_OVERDUE_SERVER){
                inMigReqHisItem.setIdOfOrgIssuer(vReqHis.getOrgResol().getIdOfOrg());
                inMigReqHisItem.setResolution(vReqHis.getResolution());
            } else {
                inMigReqHisItem.setIdOfOrgIssuer(-1L);
                inMigReqHisItem.setResolution(VisitReqResolutionHist.RES_OVERDUE);
            }
            inMigReqHisItem.setIdOfRequest(vReqHis.getCompositeIdOfVisitReqResolutionHist().getIdOfRequest());
            inMigReqHisItem.setIdOfOrgRegistry(vReqHis.getOrgRegistry().getIdOfOrg());
            inMigReqHisItem.setResolutionDateTime(vReqHis.getResolutionDateTime());
            inMigReqHisItem.setResolutionCause(vReqHis.getResolutionCause());
            if(vReqHis.getClientResol() != null) {
                inMigReqHisItem.setIdOfClientResol(vReqHis.getClientResol().getIdOfClient());
            } else {
                inMigReqHisItem.setIdOfClientResol(-1L);
            }
            inMigReqHisItem.setContactInfo(vReqHis.getContactInfo());
            inMigReqHisItem.setInitiator(vReqHis.getInitiator());
            incomeMigrationRequestsHistoryItems.add(inMigReqHisItem);
        }
        List<VisitReqResolutionHist> param = new ArrayList<>();
        for (VisitReqResolutionHist vReqHis : visitReqResolutionHistList) {
            param.add(vReqHis);
            if (param.size() == 1000) {
                Query query = session.createQuery("update VisitReqResolutionHist v set syncState = :syncState where v in :list");
                query.setParameter("syncState", VisitReqResolutionHist.SYNCHRONIZED);
                query.setParameterList("list", param);
                query.executeUpdate();
                param.clear();
            }
        }
        if (param.size() > 0) {
            Query query = session.createQuery("update VisitReqResolutionHist v set syncState = :syncState where v in :list");
            query.setParameter("syncState", VisitReqResolutionHist.SYNCHRONIZED);
            query.setParameterList("list", param);
            query.executeUpdate();
        }
    }

    private void processResInMigReqItems(List<ResIncomeMigrationRequestsItem> incomeMigrationRequestsItems)
            throws Exception {
        ResIncomeMigrationRequestsItem inMigReqItem;
        List<Migrant> migrantList = MigrantsUtils.getMigrantsForOrg(session, migrants.getIdOfOrg());
        List<Migrant> param = new ArrayList<>();
        for(Migrant migrant : migrantList){
            inMigReqItem = new ResIncomeMigrationRequestsItem(migrant);
            inMigReqItem.setIdOfOrgReg(migrant.getOrgRegistry().getIdOfOrg());
            inMigReqItem.setIdOfVendorOrgReg(migrant.getOrgRegVendor().getIdOfContragent());
            inMigReqItem.setNameOrgReg(migrant.getOrgRegistry().getShortNameInfoService());
            inMigReqItem.setRequestNumber(migrant.getRequestNumber());
            inMigReqItem.setIdOfMigrClient(migrant.getClientMigrate().getIdOfClient());
            Person person = migrant.getClientMigrate().getPerson();
            if (null != person) {
                inMigReqItem.setNameOfMigrClient(migrant.getClientMigrate().getPerson().getFullName());
            } else {
                inMigReqItem.setNameOfMigrClient("");
            }
            ClientGroup clientGroup = migrant.getClientMigrate().getClientGroup();
            if (null != clientGroup) {
                inMigReqItem.setGroupOfMigrClient(migrant.getClientMigrate().getClientGroup().getGroupName());
            } else {
                inMigReqItem.setGroupOfMigrClient("");
            }
            inMigReqItem.setVisitStartDate(migrant.getVisitStartDate());
            inMigReqItem.setVisitEndDate(migrant.getVisitEndDate());
            inMigReqItem.setInitiator(migrant.getInitiator());
            inMigReqItem.setSection(migrant.getSection());
            inMigReqItem.setResolutionCodeGroup(migrant.getResolutionCodeGroup());
            incomeMigrationRequestsItems.add(inMigReqItem);
            param.add(migrant);
            if (param.size() == 1000) {
                Query query = session.createQuery("update Migrant m set syncState = :syncState where m in :list");
                query.setParameter("syncState", Migrant.SYNCHRONIZED);
                query.setParameterList("list", param);
                query.executeUpdate();
                param.clear();
            }
        }
        if (param.size() > 0) {
            Query query = session.createQuery("update Migrant m set syncState = :syncState where m in :list");
            query.setParameter("syncState", Migrant.SYNCHRONIZED);
            query.setParameterList("list", param);
            query.executeUpdate();
        }
    }

    private void processResOutMigReqItems(List<ResOutcomeMigrationRequestsItem> outcomeMigrationRequestsItems)
            throws Exception {
        ResOutcomeMigrationRequestsItem outMigReqItem;
        List<Migrant> migrantList = MigrantsUtils.getMigrantsIdsForOrgReg(session, migrants.getIdOfOrg());
        for(Migrant migrant : migrantList){
            if(!migrants.getCurrentActiveOutcome().contains(migrant.getCompositeIdOfMigrant().getIdOfRequest())){
                migrantsForOutRequests.add(migrant);
                outMigReqItem = new ResOutcomeMigrationRequestsItem(migrant);
                outMigReqItem.setIdOfClient(migrant.getClientMigrate().getIdOfClient());
                outMigReqItem.setRequestNumber(migrant.getRequestNumber());
                outMigReqItem.setIdOfOrgVisit(migrant.getOrgVisit().getIdOfOrg());
                outMigReqItem.setVisitStartDate(migrant.getVisitStartDate());
                outMigReqItem.setVisitEndDate(migrant.getVisitEndDate());
                outMigReqItem.setInititator(migrant.getInitiator());
                outMigReqItem.setSection(migrant.getSection());
                outMigReqItem.setResolutionCodeGroup(migrant.getResolutionCodeGroup());
                outcomeMigrationRequestsItems.add(outMigReqItem);
            }
        }
    }

    private boolean processInMigReqHisItems(
            List<ResIncomeMigrationRequestsHistoryItem> incomeMigrationRequestsHistoryItems) {
        try{
            ResIncomeMigrationRequestsHistoryItem resIncomeMigrationRequestsHistoryItem;
            for(IncomeMigrationRequestsHistoryItem inMigReqHisItem : migrants.getIncomeMigrationRequestsHistoryItems()){
                if(inMigReqHisItem.getResCode().equals(IncomeMigrationRequestsHistoryItem.ERROR_CODE_ALL_OK)){
                    CompositeIdOfVisitReqResolutionHist compositeIdOfVisitReqResolutionHist
                            = new CompositeIdOfVisitReqResolutionHist(inMigReqHisItem.getIdOfRecord(), inMigReqHisItem.getIdOfRequest(), inMigReqHisItem
                            .getIdOfOrgRegistry());
                    VisitReqResolutionHist inMigReqHis = MigrantsUtils.findVisitReqResolutionHist(session,
                            compositeIdOfVisitReqResolutionHist);
                    if(inMigReqHis != null){
                        if(inMigReqHis.getOrgRegistry().getIdOfOrg().equals(inMigReqHisItem.getIdOfOrgRegistry())&&
                                inMigReqHis.getResolution().equals(inMigReqHisItem.getResolution())&&
                                inMigReqHis.getResolutionDateTime().equals(inMigReqHisItem.getResolutionDateTime())&&
                                inMigReqHis.getResolutionCause().equals(inMigReqHisItem.getResolutionCause())&&
                                inMigReqHis.getClientResol().getIdOfClient().equals(inMigReqHisItem.getIdOfClientResol())&&
                                inMigReqHis.getContactInfo().equals(inMigReqHisItem.getContactInfo())){
                            resIncomeMigrationRequestsHistoryItem = new ResIncomeMigrationRequestsHistoryItem(inMigReqHis);
                            resIncomeMigrationRequestsHistoryItem.setResCode(inMigReqHisItem.getResCode());
                        } else {
                            resIncomeMigrationRequestsHistoryItem = new ResIncomeMigrationRequestsHistoryItem(inMigReqHis);
                            resIncomeMigrationRequestsHistoryItem.setResCode(120);
                            resIncomeMigrationRequestsHistoryItem.setErrorMessage(
                                    "IncomeMigrationRequestsHistory with IdOfRecord=" + inMigReqHisItem.getIdOfRecord()
                                            + " but with other attributes already exists");
                        }
                    } else {
                        Migrant migrant = MigrantsUtils.findMigrant(session, new CompositeIdOfMigrant(inMigReqHisItem.getIdOfRequest(), inMigReqHisItem
                                .getIdOfOrgRequestIssuer()));
                        if(migrant != null){
                            Org orgReqIss = (Org)session.load(Org.class, inMigReqHisItem.getIdOfOrgRequestIssuer());
                            Client clientResol = (Client)session.load(Client.class, inMigReqHisItem.getIdOfClientResol());
                            if(inMigReqHisItem.getIdOfClientResol() == -1L) {
                                clientResol = null;
                            }
                            inMigReqHis = new VisitReqResolutionHist(compositeIdOfVisitReqResolutionHist, orgReqIss,
                                    inMigReqHisItem.getResolution(),inMigReqHisItem.getResolutionDateTime(),
                                    inMigReqHisItem.getResolutionCause(), clientResol, inMigReqHisItem.getContactInfo(),
                                    VisitReqResolutionHist.NOT_SYNCHRONIZED, VisitReqResolutionHistInitiatorEnum.INITIATOR_CLIENT);
                            session.save(inMigReqHis);
                            resIncomeMigrationRequestsHistoryItem = new ResIncomeMigrationRequestsHistoryItem(inMigReqHis);
                            resIncomeMigrationRequestsHistoryItem.setResCode(inMigReqHisItem.getResCode());
                            if(inMigReqHis.getResolution().equals(VisitReqResolutionHist.RES_REJECTED) || inMigReqHis.getResolution().equals(VisitReqResolutionHist.RES_OVERDUE)){
                                migrant.setSyncState(Migrant.CLOSED);
                                session.save(migrant);
                            }
                        } else {
                            resIncomeMigrationRequestsHistoryItem = new ResIncomeMigrationRequestsHistoryItem();
                            resIncomeMigrationRequestsHistoryItem.setIdOfRecord(inMigReqHisItem.getIdOfRequest());
                            resIncomeMigrationRequestsHistoryItem.setResCode(120);
                            resIncomeMigrationRequestsHistoryItem.setErrorMessage(
                                    "MigrationRequest for IncomeMigrationRequestsHistory with IdOfRecord="
                                            + inMigReqHisItem.getIdOfRecord() + " and IdOfOrgRegistry=" +
                                            inMigReqHisItem.getIdOfOrgRequestIssuer() +  " does not exists");
                        }
                    }
                    session.flush();
                } else {
                    resIncomeMigrationRequestsHistoryItem = new ResIncomeMigrationRequestsHistoryItem();
                    resIncomeMigrationRequestsHistoryItem.setIdOfRecord(inMigReqHisItem.getIdOfRequest());
                    resIncomeMigrationRequestsHistoryItem.setResCode(inMigReqHisItem.getResCode());
                    resIncomeMigrationRequestsHistoryItem.setErrorMessage(inMigReqHisItem.getErrorMessage());
                }
                incomeMigrationRequestsHistoryItems.add(resIncomeMigrationRequestsHistoryItem);
            }
        } catch (Exception e) {
            logger.error("Error saving IncomeMigrationRequestsHistory", e);
            return true;
        }
        return false;
    }

    private boolean processOutMigReqHisItems(
            List<ResOutcomeMigrationRequestsHistoryItem> outcomeMigrationRequestsHistoryItems) {
        try{
            ResOutcomeMigrationRequestsHistoryItem resOutcomeMigrationRequestsHistoryItem;
            for(OutcomeMigrationRequestsHistoryItem outMigReqHisItem : migrants.getOutcomeMigrationRequestsHistoryItems()){
                if(outMigReqHisItem.getResCode().equals(OutcomeMigrationRequestsHistoryItem.ERROR_CODE_ALL_OK)){
                    CompositeIdOfVisitReqResolutionHist compositeIdOfVisitReqResolutionHist
                    = new CompositeIdOfVisitReqResolutionHist(outMigReqHisItem.getIdOfRecord(), outMigReqHisItem.getIdOfRequest(), outMigReqHisItem.getIdOfOrgResol());
                    VisitReqResolutionHist outMigReqHis = MigrantsUtils.findVisitReqResolutionHist(session,
                            compositeIdOfVisitReqResolutionHist);
                    if(outMigReqHis != null){
                        if(outMigReqHis.getOrgRegistry().getIdOfOrg().equals(outMigReqHisItem.getIdOfOrgRegistry())&&
                                outMigReqHis.getResolution().equals(outMigReqHisItem.getResolution())&&
                                outMigReqHis.getResolutionDateTime().equals(outMigReqHisItem.getResolutionDateTime())&&
                                outMigReqHis.getResolutionCause().equals(outMigReqHisItem.getResolutionCause())&&
                                outMigReqHis.getClientResol().getIdOfClient().equals(outMigReqHisItem.getIdOfClientResol())&&
                                outMigReqHis.getContactInfo().equals(outMigReqHisItem.getContactInfo())){
                            resOutcomeMigrationRequestsHistoryItem = new ResOutcomeMigrationRequestsHistoryItem(outMigReqHis);
                            resOutcomeMigrationRequestsHistoryItem.setResCode(outMigReqHisItem.getResCode());
                        } else {
                            resOutcomeMigrationRequestsHistoryItem = new ResOutcomeMigrationRequestsHistoryItem(outMigReqHis);
                            resOutcomeMigrationRequestsHistoryItem.setResCode(120);
                            resOutcomeMigrationRequestsHistoryItem.setErrorMessage("OutcomeMigrationRequestsHistory with IdOfRecord="
                                    + outMigReqHisItem.getIdOfRecord() + " but with other attributes already exists");
                        }
                    } else {
                        Migrant migrant = MigrantsUtils.findMigrant(session, new CompositeIdOfMigrant(outMigReqHisItem.getIdOfRequest(), outMigReqHisItem.getIdOfOrgRegistry()));
                        if(migrant != null){
                            Org orgRegistry = (Org)session.load(Org.class, outMigReqHisItem.getIdOfOrgRegistry());
                            Client clientResol = (Client)session.load(Client.class, outMigReqHisItem.getIdOfClientResol());
                            outMigReqHis = new VisitReqResolutionHist(compositeIdOfVisitReqResolutionHist, orgRegistry, outMigReqHisItem.getResolution(),
                                    outMigReqHisItem.getResolutionDateTime(), outMigReqHisItem.getResolutionCause(), clientResol, outMigReqHisItem.getContactInfo(),
                                    VisitReqResolutionHist.NOT_SYNCHRONIZED, VisitReqResolutionHistInitiatorEnum.INITIATOR_CLIENT);
                            resOutcomeMigrationRequestsHistoryItem = new ResOutcomeMigrationRequestsHistoryItem(outMigReqHis);
                            resOutcomeMigrationRequestsHistoryItem.setResCode(outMigReqHisItem.getResCode());
                            if(outMigReqHis.getResolution().equals(VisitReqResolutionHist.RES_CANCELED)){
                                if(migrant.getSyncState().equals(Migrant.NOT_SYNCHRONIZED)){
                                    outMigReqHis.setSyncState(VisitReqResolutionHist.SYNCHRONIZED);
                                    List<VisitReqResolutionHist> list = MigrantsUtils.getNotSyncResolutionsForMigrant(session, migrant);
                                    for(VisitReqResolutionHist v : list){
                                        v.setSyncState(VisitReqResolutionHist.SYNCHRONIZED);
                                        session.save(v);
                                    }
                                }
                                migrant.setSyncState(Migrant.CLOSED);
                                session.save(migrant);
                            }
                            session.save(outMigReqHis);
                        } else {
                            resOutcomeMigrationRequestsHistoryItem = new ResOutcomeMigrationRequestsHistoryItem();
                            resOutcomeMigrationRequestsHistoryItem.setIdOfRecord(outMigReqHisItem.getIdOfRequest());
                            resOutcomeMigrationRequestsHistoryItem.setResCode(120);
                            resOutcomeMigrationRequestsHistoryItem.setErrorMessage("MigrationRequest for OutcomeMigrationRequestsHistory with IdOfRecord="
                                    + outMigReqHisItem.getIdOfRecord() + " does not exists");
                        }
                    }
                    session.flush();
                } else {
                    resOutcomeMigrationRequestsHistoryItem = new ResOutcomeMigrationRequestsHistoryItem();
                    resOutcomeMigrationRequestsHistoryItem.setIdOfRecord(outMigReqHisItem.getIdOfRequest());
                    resOutcomeMigrationRequestsHistoryItem.setResCode(outMigReqHisItem.getResCode());
                    resOutcomeMigrationRequestsHistoryItem.setErrorMessage(outMigReqHisItem.getErrorMessage());
                }
                outcomeMigrationRequestsHistoryItems.add(resOutcomeMigrationRequestsHistoryItem);
            }
        } catch (Exception e) {
            logger.error("Error saving OutcomeMigrationRequestsHistory", e);
            return true;
        }
        return false;
    }

    private boolean processOutMigReqItems(List<ResOutcomeMigrationRequestsItem> outcomeMigrationRequestsItems) {
        try{
            ResOutcomeMigrationRequestsItem resOutcomeMigrationRequestsItem;
            for(OutcomeMigrationRequestsItem outMigReqItem : migrants.getOutcomeMigrationRequestsItems()){
                if(outMigReqItem.getResCode().equals(OutcomeMigrationRequestsItem.ERROR_CODE_ALL_OK)){
                    CompositeIdOfMigrant compositeIdOfMigrant = new CompositeIdOfMigrant(outMigReqItem.getIdOfRequest(), outMigReqItem.getIdOfOrgRegistry());
                    Migrant migrant = MigrantsUtils.findMigrant(session, compositeIdOfMigrant);
                    if(migrant != null){
                        if(migrant.getClientMigrate().getIdOfClient().equals(outMigReqItem.getIdOfClient())&&
                                migrant.getRequestNumber().equals(outMigReqItem.getRequestNumber()) &&
                                migrant.getOrgVisit().getIdOfOrg().equals(outMigReqItem.getIdOfOrgVisit())&&
                                migrant.getVisitStartDate().equals(outMigReqItem.getVisitStartDate())&&
                                migrant.getVisitEndDate().equals(outMigReqItem.getVisitEndDate())){
                            resOutcomeMigrationRequestsItem = new ResOutcomeMigrationRequestsItem(migrant);
                            resOutcomeMigrationRequestsItem.setResCode(outMigReqItem.getResCode());
                        } else {
                            resOutcomeMigrationRequestsItem = new ResOutcomeMigrationRequestsItem(migrant);
                            resOutcomeMigrationRequestsItem.setResCode(110);
                            resOutcomeMigrationRequestsItem.setErrorMessage("OutcomeMigrationRequests with IdOfRequest=" + outMigReqItem.getIdOfRequest()
                                    + " but with other attributes already exists");
                        }
                    } else {
                        Org orgRegistry = (Org)session.load(Org.class, outMigReqItem.getIdOfOrgRegistry());
                        Org orgVisit = (Org)session.load(Org.class, outMigReqItem.getIdOfOrgVisit());
                        Contragent contragent = (Contragent)session.load(Contragent.class, orgRegistry.getDefaultSupplier().getIdOfContragent());
                        Client clientMigrate = (Client)session.load(Client.class, outMigReqItem.getIdOfClient());
                        migrant = new Migrant(compositeIdOfMigrant, orgRegistry.getDefaultSupplier(),
                                outMigReqItem.getRequestNumber(), clientMigrate, orgVisit,
                                outMigReqItem.getVisitStartDate(), outMigReqItem.getVisitEndDate(), Migrant.NOT_SYNCHRONIZED);
                        migrant.setOrgRegVendor(contragent);
                        migrant.setInitiator(MigrantInitiatorEnum.INITIATOR_ORG);
                        migrant.setSection(outMigReqItem.getSection());
                        migrant.setResolutionCodeGroup(outMigReqItem.getResolutionCodeGroup());
                        session.save(migrant);
                        resOutcomeMigrationRequestsItem = new ResOutcomeMigrationRequestsItem(migrant);
                        resOutcomeMigrationRequestsItem.setResCode(outMigReqItem.getResCode());
                    }
                    session.flush();
                } else {
                    resOutcomeMigrationRequestsItem = new ResOutcomeMigrationRequestsItem();
                    resOutcomeMigrationRequestsItem.setIdOfRequest(outMigReqItem.getIdOfRequest());
                    resOutcomeMigrationRequestsItem.setResCode(outMigReqItem.getResCode());
                    resOutcomeMigrationRequestsItem.setErrorMessage(outMigReqItem.getErrorMessage());
                }
                outcomeMigrationRequestsItems.add(resOutcomeMigrationRequestsItem);
            }

        } catch (Exception e) {
            logger.error("Error saving OutcomeMigrationRequests", e);
            return true;
        }
        return false;
    }

}
