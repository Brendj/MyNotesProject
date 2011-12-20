/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service;

import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.util.DigitalSignatureUtils;

import org.hibernate.classic.Session;

import java.security.KeyPair;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class BuildSignKeysPage extends BasicWorkspacePage {

    private String privateSignKey;
    private String publicSignKey;

    public String getPageFilename() {
        return "service/sign_keys";
    }

    public String getPrivateSignKey() {
        return privateSignKey;
    }

    public String getPublicSignKey() {
        return publicSignKey;
    }

    public void fill(Session session) throws Exception {

    }

    public void buildSignKes(Session session) throws Exception {
        this.privateSignKey = null;
        this.publicSignKey = null;
        KeyPair keyPair = DigitalSignatureUtils.generateKeyPair();
        this.privateSignKey = DigitalSignatureUtils.convertToString(keyPair.getPrivate());
        this.publicSignKey = DigitalSignatureUtils.convertToString(keyPair.getPublic());
    }
}