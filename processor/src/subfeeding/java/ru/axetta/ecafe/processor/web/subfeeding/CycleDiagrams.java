/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.subfeeding;

import ru.axetta.ecafe.processor.web.partner.integra.dataflow.CycleDiagramList;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.CycleDiagramOut;
import ru.axetta.ecafe.processor.web.partner.integra.soap.ClientRoomController;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 17.06.14
 * Time: 10:24
 * To change this template use File | Settings | File Templates.
 */
public class CycleDiagrams {

    private ClientRoomController clientRoomController;
    private CycleDiagramList cycleDiagrams;
    private List<CycleDiagram> cycleDiagramList;

    public static CycleDiagrams buildHistoryList(ClientRoomController clientRoomController, Long contractId,
            Date startDate, Date endDate) {
        CycleDiagrams diagrams = new CycleDiagrams(clientRoomController);
        diagrams.buildList(contractId, startDate, endDate);
        return diagrams;
    }

    public static CycleDiagrams buildList(ClientRoomController clientRoomController, Long contractId) {
        CycleDiagrams diagrams = new CycleDiagrams(clientRoomController);
        diagrams.buildList(contractId);
        return diagrams;
    }

    private void buildList(Long contractId) {
        cycleDiagrams = clientRoomController.getCycleDiagramList(contractId);
        boolean clientdiagramExist = isCycleDiagramExist();
        cycleDiagramList = new ArrayList<CycleDiagram>();
        if (clientdiagramExist) {
            final List<CycleDiagramOut> diagrams = cycleDiagrams.cycleDiagramListExt.getC();
            Collections.sort(diagrams, buildDateActivationDiagramComparator());
            CycleDiagram lastDiagram = null;
            for (CycleDiagramOut cycleDiagramOut : diagrams) {
                final CycleDiagram diagram = new CycleDiagram(cycleDiagramOut);
                if (lastDiagram != null) {
                    lastDiagram.setDateDeactivationDiagram(diagram.getDateActivationDiagram());
                }
                cycleDiagramList.add(diagram);
                lastDiagram = diagram;
            }
        }
    }

    private void buildList(Long contractId, Date startDate, Date endDate) {
        cycleDiagrams = getCycleDiagram(contractId, startDate, endDate);
        boolean clientdiagramExist = isCycleDiagramExist();
        cycleDiagramList = new ArrayList<CycleDiagram>();
        if (clientdiagramExist) {
            for (CycleDiagramOut cycleDiagramOut : cycleDiagrams.cycleDiagramListExt.getC()) {
                cycleDiagramList.add(new CycleDiagram(cycleDiagramOut));
            }
            Collections.sort(cycleDiagramList, buildUpdateDateComparator());
        }
    }

    public List<CycleDiagram> getCycleDiagramList() {
        return cycleDiagramList;
    }

    public boolean isCycleDiagramExist() {
        return cycleDiagrams != null && cycleDiagrams.cycleDiagramListExt != null && !cycleDiagrams.cycleDiagramListExt
                .getC().isEmpty();
    }

    private CycleDiagramList getCycleDiagram(Long contractId, Date startDate, Date endDate) {
        return clientRoomController.getCycleDiagramHistoryList(contractId, startDate, endDate);
    }

    private CycleDiagrams(ClientRoomController clientRoomController) {
        this.clientRoomController = clientRoomController;
    }

    static class CycleDiagramCompareByUpdateDate implements Comparator<CycleDiagram> {

        @Override
        public int compare(CycleDiagram o1, CycleDiagram o2) {
            return o1.getUpdateDate().compareTo(o2.getUpdateDate());
        }
    }

    static class CycleDiagramExtCompareByDateActivationDiagram implements Comparator<CycleDiagramOut> {

        @Override
        public int compare(CycleDiagramOut o1, CycleDiagramOut o2) {
            return o1.getDateActivationDiagram().compareTo(o2.getDateActivationDiagram());
        }
    }

    public static CycleDiagramExtCompareByDateActivationDiagram buildDateActivationDiagramComparator() {
        return new CycleDiagramExtCompareByDateActivationDiagram();
    }

    public static CycleDiagramCompareByUpdateDate buildUpdateDateComparator() {
        return new CycleDiagramCompareByUpdateDate();
    }

}
