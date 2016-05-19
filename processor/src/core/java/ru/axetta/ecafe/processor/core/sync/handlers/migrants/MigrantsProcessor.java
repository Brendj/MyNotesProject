/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.migrants;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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

    public MigrantsProcessor(Session persistenceSession, Migrants migrants) {
        super(persistenceSession);
        this.migrants = migrants;
    }

    @Override
    public ResMigrants process() throws Exception {
        ResMigrants result = new ResMigrants();
        List<ResOutcomeMigrationRequestsItem> outcomeMigrationRequestsItems = new ArrayList<ResOutcomeMigrationRequestsItem>();
        List<ResOutcomeMigrationRequestsHistoryItem> outcomeMigrationRequestsHistoryItems = new ArrayList<ResOutcomeMigrationRequestsHistoryItem>();
        List<ResIncomeMigrationRequestsHistoryItem> incomeMigrationRequestsHistoryItems = new ArrayList<ResIncomeMigrationRequestsHistoryItem>();

        try{
            ResOutcomeMigrationRequestsItem resOutcomeMigrationRequestsItem;
            for(OutcomeMigrationRequestsItem outMigReqItem : migrants.getOutcomeMigrationRequestsItems()){
                if(outMigReqItem.getResCode().equals(OutcomeMigrationRequestsItem.ERROR_CODE_ALL_OK)){
                    CompositeIdOfMigrant compositeIdOfMigrant = new CompositeIdOfMigrant(outMigReqItem.getIdOfRequest(), outMigReqItem.getIdOfOrgRegistry());
                    Migrant migrant = DAOUtils.findMigrant(session, compositeIdOfMigrant);
                    if(migrant != null){
                        if(migrant.getClientMigrate().getIdOfClient().equals(outMigReqItem.getIdOfClient())&&
                                migrant.getOrgVisit().getIdOfOrg().equals(outMigReqItem.getIdOfOrgVisit())&&
                                migrant.getVisitStartDate().equals(outMigReqItem.getVisitStartDate())&&
                                migrant.getVisitEndDate().equals(outMigReqItem.getVisitEndDate())){
                            resOutcomeMigrationRequestsItem = new ResOutcomeMigrationRequestsItem(migrant);
                            resOutcomeMigrationRequestsItem.setResCode(outMigReqItem.getResCode());
                        } else {
                            resOutcomeMigrationRequestsItem = new ResOutcomeMigrationRequestsItem(migrant);
                            resOutcomeMigrationRequestsItem.setResCode(110);
                            resOutcomeMigrationRequestsItem.setErrorMessage("OutcomeMigrationRequests with IdOfRequest=" + outMigReqItem.getIdOfRequest()
                                    + "but with other attributes already exists");
                        }
                    } else {
                        Org orgRegistry = (Org)session.load(Org.class, outMigReqItem.getIdOfOrgRegistry());
                        Org orgVisit = (Org)session.load(Org.class, outMigReqItem.getIdOfOrgVisit());
                        Contragent contragent = (Contragent)session.load(Contragent.class, orgRegistry.getDefaultSupplier().getIdOfContragent());
                        Client clientMigrate = (Client)session.load(Client.class, outMigReqItem.getIdOfClient());
                        migrant = new Migrant(compositeIdOfMigrant, orgRegistry.getDefaultSupplier(), clientMigrate,
                                orgVisit, outMigReqItem.getVisitStartDate(), outMigReqItem.getVisitEndDate(), 0);
                        migrant.setOrgRegVendor(contragent);
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
            return null;
        }

        try{
            ResOutcomeMigrationRequestsHistoryItem resOutcomeMigrationRequestsHistoryItem;
            for(OutcomeMigrationRequestsHistoryItem outMigReqHisItem : migrants.getOutcomeMigrationRequestsHistoryItems()){
                if(outMigReqHisItem.getResCode().equals(OutcomeMigrationRequestsHistoryItem.ERROR_CODE_ALL_OK)){
                    CompositeIdOfVisitReqResolutionHist compositeIdOfVisitReqResolutionHist
                    = new CompositeIdOfVisitReqResolutionHist(outMigReqHisItem.getIdOfRecord(), outMigReqHisItem.getIdOfRequest(), outMigReqHisItem.getIdOfOrgResol());
                    VisitReqResolutionHist outMigReqHis = DAOUtils.findVisitReqResolutionHist(session,
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
                        Migrant migrant = DAOUtils.findMigrant(session, new CompositeIdOfMigrant(outMigReqHisItem.getIdOfRequest(), outMigReqHisItem.getIdOfOrgRegistry()));
                        if(migrant != null){
                            Org orgRegistry = (Org)session.load(Org.class, outMigReqHisItem.getIdOfOrgRegistry());
                            Client clientResol = (Client)session.load(Client.class, outMigReqHisItem.getIdOfClientResol());
                            outMigReqHis = new VisitReqResolutionHist(compositeIdOfVisitReqResolutionHist, orgRegistry, outMigReqHisItem.getResolution(),
                                    outMigReqHisItem.getResolutionDateTime(), outMigReqHisItem.getResolutionCause(), clientResol, outMigReqHisItem.getContactInfo(), 0);
                            session.save(outMigReqHis);
                            resOutcomeMigrationRequestsHistoryItem = new ResOutcomeMigrationRequestsHistoryItem(outMigReqHis);
                            resOutcomeMigrationRequestsHistoryItem.setResCode(outMigReqHisItem.getResCode());
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
            return null;
        }

        try{
            ResIncomeMigrationRequestsHistoryItem resIncomeMigrationRequestsHistoryItem;
            for(IncomeMigrationRequestsHistoryItem inMigReqHisItem : migrants.getIncomeMigrationRequestsHistoryItems()){
                if(inMigReqHisItem.getResCode().equals(IncomeMigrationRequestsHistoryItem.ERROR_CODE_ALL_OK)){
                    CompositeIdOfVisitReqResolutionHist compositeIdOfVisitReqResolutionHist
                            = new CompositeIdOfVisitReqResolutionHist(inMigReqHisItem.getIdOfRecord(), inMigReqHisItem.getIdOfRequest(), inMigReqHisItem
                            .getIdOfOrgRegistry());
                    VisitReqResolutionHist inMigReqHis = DAOUtils.findVisitReqResolutionHist(session,
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
                        Migrant migrant = DAOUtils.findMigrant(session, new CompositeIdOfMigrant(inMigReqHisItem.getIdOfRequest(), inMigReqHisItem
                                .getIdOfOrgRequestIssuer()));
                        if(migrant != null){
                            Org orgReqIss = (Org)session.load(Org.class, inMigReqHisItem.getIdOfOrgRequestIssuer());
                            Client clientResol = (Client)session.load(Client.class, inMigReqHisItem.getIdOfClientResol());
                            inMigReqHis = new VisitReqResolutionHist(compositeIdOfVisitReqResolutionHist, orgReqIss,
                                    inMigReqHisItem.getResolution(),inMigReqHisItem.getResolutionDateTime(),
                                    inMigReqHisItem.getResolutionCause(), clientResol, inMigReqHisItem.getContactInfo(), 0);
                            session.save(inMigReqHis);
                            resIncomeMigrationRequestsHistoryItem = new ResIncomeMigrationRequestsHistoryItem(inMigReqHis);
                            resIncomeMigrationRequestsHistoryItem.setResCode(inMigReqHisItem.getResCode());
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
            return null;
        }

        result.setResOutcomeMigrationRequestsItems(outcomeMigrationRequestsItems);
        result.setResOutcomeMigrationRequestsHistoryItems(outcomeMigrationRequestsHistoryItems);
        result.setResIncomeMigrationRequestsHistoryItems(incomeMigrationRequestsHistoryItems);
        return result;
    }

    public MigrantsData processData() throws Exception {
        List<Client> clientList = DAOUtils.getActiveMigrantsForOrg(session, migrants.getIdOfOrg());
        MigrantsData result = new MigrantsData();
        List<ResIncomeMigrationRequestsItem> incomeMigrationRequestsItems = new ArrayList<ResIncomeMigrationRequestsItem>();
        List<ResIncomeMigrationRequestsHistoryItem> incomeMigrationRequestsHistoryItems = new ArrayList<ResIncomeMigrationRequestsHistoryItem>();
        List<ResOutcomeMigrationRequestsHistoryItem> outcomeMigrationRequestsHistoryItems = new ArrayList<ResOutcomeMigrationRequestsHistoryItem>();

        ResIncomeMigrationRequestsItem inMigReqItem;
        List<Migrant> migrantList = DAOUtils.getMigrantsForOrg(session, migrants.getIdOfOrg());
        for(Migrant migrant : migrantList){
            inMigReqItem = new ResIncomeMigrationRequestsItem(migrant);
            inMigReqItem.setIdOfOrgReg(migrant.getOrgRegistry().getIdOfOrg());
            inMigReqItem.setIdOfVendorOrgReg(migrant.getOrgRegVendor().getIdOfContragent());
            inMigReqItem.setNameOrgReg(migrant.getOrgRegistry().getShortName());
            inMigReqItem.setIdOfMigrClient(migrant.getClientMigrate().getIdOfClient());
            inMigReqItem.setNameOfMigrClient(migrant.getClientMigrate().getPerson().getFullName());
            inMigReqItem.setGroupOfMigrClient(migrant.getClientMigrate().getClientGroup().getGroupName());
            inMigReqItem.setVisitStartDate(migrant.getVisitStartDate());
            inMigReqItem.setVisitEndDate(migrant.getVisitEndDate());
            incomeMigrationRequestsItems.add(inMigReqItem);
            migrant.setSyncState(1);
            session.save(migrant);
        }
        session.flush();

        ResIncomeMigrationRequestsHistoryItem inMigReqHisItem;
        List<VisitReqResolutionHist> visitReqResolutionHistList = DAOUtils.getIncomeResolutionsForOrg(session, migrants.getIdOfOrg());
        for(VisitReqResolutionHist vReqHis : visitReqResolutionHistList){
            inMigReqHisItem = new ResIncomeMigrationRequestsHistoryItem();
            inMigReqHisItem.setIdOfOrgIssuer(vReqHis.getOrgResol().getIdOfOrg());
            inMigReqHisItem.setIdOfRequest(vReqHis.getCompositeIdOfVisitReqResolutionHist().getIdOfRequest());
            inMigReqHisItem.setResolution(vReqHis.getResolution());
            inMigReqHisItem.setResolutionDateTime(vReqHis.getResolutionDateTime());
            inMigReqHisItem.setResolutionCause(vReqHis.getResolutionCause());
            inMigReqHisItem.setIdOfClientResol(vReqHis.getClientResol().getIdOfClient());
            inMigReqHisItem.setContactInfo(vReqHis.getContactInfo());
            incomeMigrationRequestsHistoryItems.add(inMigReqHisItem);
            vReqHis.setSyncState(1);
            session.save(vReqHis);
        }

        ResOutcomeMigrationRequestsHistoryItem outMigReqHisItem;
        List<VisitReqResolutionHist> visitReqResolutionHistList1 = DAOUtils.getOutcomeResolutionsForOrg(session,
                migrants.getIdOfOrg());
        for(VisitReqResolutionHist vReqHis : visitReqResolutionHistList1){
            outMigReqHisItem = new ResOutcomeMigrationRequestsHistoryItem();
            outMigReqHisItem.setIdOfOrgIssuer(vReqHis.getOrgResol().getIdOfOrg());
            outMigReqHisItem.setIdOfRequest(vReqHis.getCompositeIdOfVisitReqResolutionHist().getIdOfRequest());
            outMigReqHisItem.setResolution(vReqHis.getResolution());
            outMigReqHisItem.setResolutionDateTime(vReqHis.getResolutionDateTime());
            outMigReqHisItem.setResolutionCause(vReqHis.getResolutionCause());
            outMigReqHisItem.setIdOfClientResol(vReqHis.getClientResol().getIdOfClient());
            outMigReqHisItem.setContactInfo(vReqHis.getContactInfo());
            outcomeMigrationRequestsHistoryItems.add(outMigReqHisItem);
            vReqHis.setSyncState(1);
            session.save(vReqHis);
        }

        result.setIncomeMigrationRequestsItems(incomeMigrationRequestsItems);
        result.setIncomeMigrationRequestsHistoryItems(incomeMigrationRequestsHistoryItems);
        result.setOutcomeMigrationRequestsHistoryItems(outcomeMigrationRequestsHistoryItems);
        return result;
    }
}
