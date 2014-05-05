/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.rest.service;

import ru.axetta.ecafe.processor.web.ClientAuthToken;
import ru.axetta.ecafe.processor.web.ejb.Configurations;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.*;
import ru.axetta.ecafe.processor.web.partner.integra.soap.ClientRoomController;
import ru.axetta.ecafe.processor.web.rest.items.ComplexItem;
import ru.axetta.ecafe.processor.web.rest.items.CycleDiagramItem;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 18.04.14
 * Time: 10:40
 * To change this template use File | Settings | File Templates.
 */
@Stateless
@Path("/diagram")
public class CycleDiagramService {

    private static final Logger logger = LoggerFactory.getLogger(CycleDiagramService.class);

    @EJB
    private Configurations configurations;

    @GET
    @Path("/list.json")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ComplexInfoExt> list(@Context HttpServletRequest req) {
        ClientAuthToken token = ClientAuthToken.loadFrom(req.getSession());
        List<ComplexInfoExt> complexes = new ArrayList<ComplexInfoExt>();
        if(token!=null) {
            try {
                ClientRoomController clientRoomController = configurations.getPort();
                ComplexInfoResult result = clientRoomController.findComplexesWithSubFeeding(token.getContractId());
                if (result.resultCode==0){
                    complexes = result.getComplexInfoList().getList();
                }
            } catch (Exception e) {
                logger.error("error", e);
            }
        }
        return complexes;
    }

    @GET
    @Path("/diagram.json")
    @Produces(MediaType.APPLICATION_JSON)
    public CycleDiagramItem currDiagram(@Context HttpServletRequest request,
                                        @Context HttpServletResponse response) throws ServletException, IOException {
        ClientAuthToken token = ClientAuthToken.loadFrom(request.getSession());
        CycleDiagramItem cycleDiagramItem = new CycleDiagramItem();
        if(token==null) {
            request.getRequestDispatcher ("../../index.html").forward(request, response);
            //return cycleDiagramItem;
        }
        try {
            ClientRoomController clientRoomController = configurations.getPort();
            final Long contractId = token.getContractId();
            ComplexInfoResult result = clientRoomController.findComplexesWithSubFeeding(contractId);
            if (result.resultCode==0){
                CycleDiagramOut cd = clientRoomController.findClientCycleDiagram(contractId);
                cycleDiagramItem.setDiagramDate(cd.getDateActivationDiagram());
                Long weekPrice = 0L;
                Map<Integer, List<String>> active = splitPlanComplexes(cd);
                List<ComplexItem> complexItemList = new ArrayList<ComplexItem>();
                for (ComplexInfoExt complexInfoExt: result.getComplexInfoList().getList()){
                    final ComplexItem complexItem = new ComplexItem();
                    complexItem.setIdOfComplex(complexInfoExt.getIdOfComplex());
                    complexItem.setName(complexInfoExt.getComplexName());
                    complexItem.setPrice(complexInfoExt.getCurrentPrice());
                    Integer[] checked = new Integer[6];
                    for (int i=0; i<checked.length; i++){
                        boolean flag = active.get(i + 1).contains(Long.toString(complexInfoExt.getIdOfComplex()));
                        checked[i] = flag?1:0;
                        weekPrice += flag?complexInfoExt.getCurrentPrice():0;
                    }
                    complexItem.setCheckarr(checked);
                    complexItemList.add(complexItem);
                }
                cycleDiagramItem.setWeekSum(weekPrice);
                cycleDiagramItem.setList(complexItemList);
            }
        } catch (Exception e) {
            logger.error("error", e);
        }
        return cycleDiagramItem;
    }

    @GET
    @Path("/nextdiagram.json")
    @Produces(MediaType.APPLICATION_JSON)
    public CycleDiagramItem nextDiagram(@Context HttpServletRequest req) {
        ClientAuthToken token = ClientAuthToken.loadFrom(req.getSession());
        CycleDiagramItem cycleDiagramItem = new CycleDiagramItem();
        if(token!=null) {
            try {
                ClientRoomController clientRoomController = configurations.getPort();
                ComplexInfoResult result = clientRoomController.findComplexesWithSubFeeding(token.getContractId());
                if (result.resultCode==0){
                    CycleDiagramOut cd = clientRoomController.findClientCycleDiagram(token.getContractId());
                    cycleDiagramItem.setDiagramDate(cd.getDateActivationDiagram());
                    Long weekPrice = 0L;
                    Map<Integer, List<String>> active = splitPlanComplexes(cd);
                    List<ComplexItem> complexItemList = new ArrayList<ComplexItem>();
                    for (ComplexInfoExt complexInfoExt: result.getComplexInfoList().getList()){
                        final ComplexItem complexItem = new ComplexItem();
                        complexItem.setIdOfComplex(complexInfoExt.getIdOfComplex());
                        complexItem.setName(complexInfoExt.getComplexName());
                        complexItem.setPrice(complexInfoExt.getCurrentPrice());
                        Integer[] checked = new Integer[6];
                        for (int i=0; i<checked.length; i++){
                            boolean flag = active.get(i + 1).contains(Long.toString(complexInfoExt.getIdOfComplex()));
                            checked[i] = flag?1:0;
                            weekPrice += flag?complexInfoExt.getCurrentPrice():0;
                        }
                        complexItem.setCheckarr(checked);
                        complexItemList.add(complexItem);
                    }
                    cycleDiagramItem.setWeekSum(weekPrice);
                    cycleDiagramItem.setList(complexItemList);
                }
            } catch (Exception e) {
                logger.error("error", e);
            }
        }
        return cycleDiagramItem;
    }

    private Map<Integer, List<String>> splitPlanComplexes(CycleDiagramOut cd) {
        Map<Integer, List<String>> activeComplexes = new HashMap<Integer, List<String>>();
        activeComplexes.put(1, Arrays.asList(StringUtils.split(StringUtils.defaultString(cd.getMonday()), ';')));
        activeComplexes.put(2, Arrays.asList(StringUtils.split(StringUtils.defaultString(cd.getTuesday()), ';')));
        activeComplexes.put(3, Arrays.asList(StringUtils.split(StringUtils.defaultString(cd.getWednesday()), ';')));
        activeComplexes.put(4, Arrays.asList(StringUtils.split(StringUtils.defaultString(cd.getThursday()), ';')));
        activeComplexes.put(5, Arrays.asList(StringUtils.split(StringUtils.defaultString(cd.getFriday()), ';')));
        activeComplexes.put(6, Arrays.asList(StringUtils.split(StringUtils.defaultString(cd.getSaturday()), ';')));
        activeComplexes.put(7, Arrays.asList(StringUtils.split(StringUtils.defaultString(cd.getSunday()), ';')));
        return activeComplexes;
    }


}
