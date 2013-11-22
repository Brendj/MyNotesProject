/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.pos;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.POS;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentSelectPage;

import org.hibernate.Session;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 22.11.11
 * Time: 10:07
 * To change this template use File | Settings | File Templates.
 */
public class PosCreatePage extends BasicWorkspacePage
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
        return "contragent/pos/create";
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

    public void completeContragentSelection(Session session, Long idOfContragent, int multiContrFlag, String classTypes) throws Exception {
        if (null != idOfContragent) {
            Contragent contragent = (Contragent) session.load(Contragent.class, idOfContragent);
            this.contragent = new ContragentItem(contragent);
        }
    }

    public void fill(Session session) throws Exception {

    }

    public void createPos(Session session) throws Exception {
        Contragent contragent = (Contragent) session.load(Contragent.class, this.contragent.getIdOfContragent());
        POS pos = new POS();
        pos.setContragent(contragent);
        pos.setCreatedDate(new Date());
        pos.setName(name);
        pos.setDescription(description);
        pos.setState(state);
        pos.setFlags(flags);
        pos.setPublicKey(publicKey);
        session.save(pos);
    }
}
