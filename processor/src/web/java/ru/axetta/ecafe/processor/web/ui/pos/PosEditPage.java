/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.pos;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.POS;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentSelectPage;

import org.hibernate.classic.Session;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 22.11.11
 * Time: 13:56
 * To change this template use File | Settings | File Templates.
 */
public class PosEditPage extends BasicWorkspacePage
        implements ContragentSelectPage.CompleteHandler {
    public static class ContragentItem {

        private final Long idOfContragent;
        private final String contragentName;

        public ContragentItem(Contragent contragent) {
            this.idOfContragent = contragent.getIdOfContragent();
            this.contragentName = contragent.getContragentName();
        }

        public ContragentItem() {
            this.idOfContragent = null;
            this.contragentName = null;
        }

        public Long getIdOfContragent() {
            return idOfContragent;
        }

        public String getContragentName() {
            return contragentName;
        }
    }

    public String getPageFilename() {
        return "contragent/pos/edit";
    }

    public void completeContragentSelection(Session session, Long idOfContragent, int multiContrFlags) throws Exception {
        if (null != idOfContragent) {
            Contragent contragent = (Contragent) session.load(Contragent.class, idOfContragent);
            this.contragent = new ContragentItem(contragent);
        }
    }

    private ContragentItem contragent = new ContragentItem();
    private String name;
    private String description;
    private int state;
    private int flags;
    private String publicKey;

    public ContragentItem getContragent() {
        return contragent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public void fill(Session session, Long idOfPos) throws Exception {
        POS pos = (POS) session.load(POS.class, idOfPos);
        fill(pos);
    }

    public void updatePos(Session persistenceSession, Long idOfPos) throws Exception {
        Contragent contragent = (Contragent) persistenceSession.load(Contragent.class, this.contragent.getIdOfContragent());
        POS pos = (POS) persistenceSession.load(POS.class, idOfPos);
        pos.setContragent(contragent);
        pos.setName(name);
        pos.setDescription(description);
        pos.setState(state);
        pos.setFlags(flags);
        pos.setPublicKey(publicKey);
        persistenceSession.update(pos);
        fill(pos);
    }

    private void fill(POS pos) throws Exception {
        this.contragent = new ContragentItem(pos.getContragent());
        this.name = pos.getName();
        this.description = pos.getDescription();
        this.state = pos.getState();
        this.flags = pos.getFlags();
        this.publicKey = pos.getPublicKey();
    }
}