package ru.axetta.ecafe.processor.web.ui.dul;

import org.hibernate.Criteria;
import org.hibernate.Session;
import ru.axetta.ecafe.processor.core.persistence.DulDetail;
import ru.axetta.ecafe.processor.core.persistence.DulGuide;
import ru.axetta.ecafe.processor.web.ui.BasicPage;
import ru.axetta.ecafe.processor.web.ui.client.ClientFilter;
import ru.axetta.ecafe.processor.web.ui.client.ClientSelectPage;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class DulSelectPage extends BasicPage {

    public interface CompleteHandler {
        void completeDulSelection(Session session, DulGuide dulGuide) throws Exception;
    }

    private DulGuide dulGuide;
    private List<DulDetail> dulDetailList;
    private List<DulGuide> dulGuideList;

    public void fill(Session persistenceSession) {
        Criteria criteria = persistenceSession.createCriteria(DulGuide.class);
        List<DulGuide> allDulGuides = (List<DulGuide>) criteria.list();
        List<DulGuide> useDulGuides = new ArrayList<>();
        if(dulDetailList != null && !dulDetailList.isEmpty())
            useDulGuides = dulDetailList.stream()
                .filter(d -> d.getDeleteState() == null || !d.getDeleteState())
                .map(DulDetail::getDulGuide)
                .collect(Collectors.toList());

        this.dulGuideList = new ArrayList<>();

        if (dulDetailList != null && !dulDetailList.isEmpty()) {
            for (DulGuide dulGuide : allDulGuides) {
                if (!useDulGuides.contains(dulGuide))
                    this.dulGuideList.add(dulGuide);
            }
        } else this.dulGuideList = allDulGuides;
    }
    public void cancelDulSelection() {
        completeHandlers.clear();
        this.dulGuide = null;
    }

    public DulGuide getDulGuide() {
        return dulGuide;
    }

    public void setDulGuide(DulGuide dulGuide) {
        this.dulGuide = dulGuide;
    }

    public List<DulDetail> getDulDetailList() {
        return dulDetailList;
    }

    public void setDulDetailList(List<DulDetail> dulDetailList) {
        this.dulDetailList = dulDetailList;
    }

    public List<DulGuide> getDulGuideList() {
        return dulGuideList;
    }

    public void setDulGuideList(List<DulGuide> dulGuideList) {
        this.dulGuideList = dulGuideList;
    }

    private final Stack<DulSelectPage.CompleteHandler> completeHandlers = new Stack<>();

    public void pushCompleteHandler(DulSelectPage.CompleteHandler handler) {
        completeHandlers.push(handler);
    }

    public void completeDulSelection(Session persistenceSession) throws Exception {
        if (!completeHandlers.empty()) {
            completeHandlers.peek().completeDulSelection(persistenceSession, this.dulGuide);
            completeHandlers.pop();
        }
    }

}
