/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.pos;

import ru.axetta.ecafe.processor.core.persistence.CompositeIdOfContragentClientAccount;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.ContragentClientAccount;
import ru.axetta.ecafe.processor.core.persistence.POS;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentSelectPage;

import org.hibernate.Session;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 22.11.11
 * Time: 9:56
 * To change this template use File | Settings | File Templates.
 */
public class PosListPage extends BasicWorkspacePage
    implements ContragentSelectPage.CompleteHandler {

    public static class ContragentItem {

        private final Long idOfContragent;
        private final String contragentName;

        public ContragentItem(Contragent contragent) {
            this.idOfContragent = contragent.getIdOfContragent();
            this.contragentName = contragent.getContragentName();
        }

        public Long getIdOfContragent() {
            return idOfContragent;
        }

        public String getContragentName() {
            return contragentName;
        }
    }

    public static class Item {

        private long idOfPos;
        private ContragentItem contragent;
        private String name;
        private String description;
        private Date createdDate;
        private int state;
        private int flags;


        public Item(POS pos) {
            this.idOfPos = pos.getIdOfPos();
            this.contragent = new ContragentItem(pos.getContragent());
            this.name = pos.getName();
            this.description = pos.getDescription();
            this.createdDate = pos.getCreatedDate();
            this.state = pos.getState();
            this.flags = pos.getFlags();
        }

        public long getIdOfPos() {
            return idOfPos;
        }

        public ContragentItem getContragent() {
            return contragent;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public Date getCreatedDate() {
            return createdDate;
        }

        public int getState() {
            return state;
        }

        public int getFlags() {
            return flags;
        }
    }

    private List<Item> items = Collections.emptyList();
    private final PosFilter filter = new PosFilter();

    public String getPageTitle() {
        return super.getPageTitle() + String.format(" (%d)", items.size());
    }

    public List<Item> getItems() {
        return items;
    }

    public String getPageFilename() {
        return "contragent/pos/list";
    }

    public PosFilter getFilter() {
        return filter;
    }

    public void completeContragentSelection(Session session, Long idOfContragent, int multiContrFlag) throws Exception {
        this.filter.completeContragentSelection(session, idOfContragent);
    }

    public void fill(Session session) throws Exception {
        List<Item> items = new LinkedList<Item>();
        if (!filter.isEmpty()) {
            List posList = filter.retrievePos(session);
            for (Object object : posList) {
                POS pos = (POS) object;
                items.add(new Item(pos));
            }
        }
        this.items = items;
    }
}
