/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.user;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.Person;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 18.11.13
 * Time: 13:54
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class ThinClientUserViewPage extends BasicWorkspacePage {
    private Long idOfClient;
    private String username;
    private String roleName;
    private Person person;
    private Client cl;
    private Org org;
    private long role;
    private static final Logger logger = LoggerFactory.getLogger(ThinClientUserViewPage.class);
    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    public String getPageTitle() {
        return super.getPageTitle() + " " + (username == null ? "" : username);
    }

    public String getPageFilename() {
        return "option/user/thin_client/view";
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient < 1 ? null : idOfClient;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getUsername() {
        return username;
    }

    public Person getPerson() {
        return person;
    }

    public Client getCl() {
        return cl;
    }

    public Org getOrg() {
        return org;
    }

    public long getRole() {
        return role;
    }

    @Override
    public void onShow() throws Exception {
        RuntimeContext.getAppContext().getBean(ThinClientUserViewPage.class).fill();
    }

    @Transactional
    public void fill() throws Exception {
        Session session = null;
        try {
            if (idOfClient == null) {
                return;
            }
            session = (Session) entityManager.getDelegate();
            fill(session);
        } catch (Exception e) {
            logger.error("Failed to load thin client users", e);
        } finally {
        }
    }

    public void fill(Session session) throws Exception {
        if (idOfClient == null) {
            return;
        }
        Query q = session.createSQLQuery("select cf_thin_client_users.idofclient, cf_clients.idoforg, "
                                        + "cf_thin_client_users.role, cf_thin_client_users.username "
                                        + "from cf_thin_client_users "
                                        + "left join cf_clients on cf_thin_client_users.idofclient=cf_clients.idofclient "
                                        + "where cf_thin_client_users.idofclient=:idofclient "
                                        + "order by cf_clients.idoforg");
        q.setLong("idofclient", idOfClient);
        List users = q.list();
        for (Object o : users) {
            Object entry [] = (Object []) o;
            long idOfClient = ((BigInteger) entry [0]).longValue();
            long idOfOrg    = ((BigInteger) entry [1]).longValue();
            role            = ((Integer) entry [2]).intValue();
            username        = ((String) entry [3]).trim();

            cl = DAOReadonlyService.getInstance().findClientById(idOfClient); cl = (Client) session.merge(cl);
            person = cl.getPerson();
            String fullName = person.getFullName();
            org = DAOReadonlyService.getInstance().findOrg(idOfOrg);
            roleName = ThinClientUserListPage.DEFAULT_ROLE;
        }
    }

    public void clearClient () {
        idOfClient = null;
        username = null;
        roleName = null;
        person = null;
        cl = null;
        org = null;
        role = 0;
    }
}
