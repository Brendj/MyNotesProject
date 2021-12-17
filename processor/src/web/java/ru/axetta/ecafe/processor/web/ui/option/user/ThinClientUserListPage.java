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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 18.11.13
 * Time: 11:26
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class ThinClientUserListPage extends BasicWorkspacePage {
    public static final String DEFAULT_ROLE = "Администратор";
    private static final Logger logger = LoggerFactory.getLogger(ThinClientUserListPage.class);
    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;


    public static class Item {

        private final Long idOfClient;
        private final String userName;
        private final String clientName;
        private final Long idOfOrg;
        private final String orgName;
        private final Integer role;
        private final String roleName;

        public Item(Client cl, String userName, Person person, Org org, Integer role, String roleName) {
            idOfClient = cl.getIdOfClient();
            this.userName = userName;
            clientName = person.getFullName();
            idOfOrg = org.getIdOfOrg();
            orgName = org.getOfficialName();
            this.role = role;
            this.roleName = roleName;
        }

        public Long getIdOfClient() {
            return idOfClient;
        }

        public String getClientName() {
            return clientName;
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public String getOrgName() {
            return orgName;
        }

        public Integer getRole() {
            return role;
        }

        public String getRoleName() {
            return roleName;
        }

        public String getUserName() {
            return userName;
        }
    }

    private List<Item> items = Collections.emptyList();
    private long clientToRemove;

    public long getClientToRemove() {
        return clientToRemove;
    }

    public void setClientToRemove(long clientToRemove) {
        this.clientToRemove = clientToRemove;
    }

    public String getPageFilename() {
        return "option/user/thin_client/list";
    }

    public String getPageTitle() {
        return super.getPageTitle() + String.format(" (%d)", items.size());
    }

    public List<Item> getItems() {
        return items;
    }

    @Override
    public void onShow() throws Exception {
        RuntimeContext.getAppContext().getBean(ThinClientUserListPage.class).fill();
    }

    @Transactional
    public void fill() throws Exception {
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            fill(session);
        } catch (Exception e) {
            logger.error("Failed to load thin client users", e);
        } finally {
        }
    }

    public void fill(Session session) throws Exception {
        List<Item> items = new LinkedList<Item>();
        Query q = session.createSQLQuery("select cf_thin_client_users.idofclient, cf_clients.idoforg, "
                                        + "cf_thin_client_users.role, cf_thin_client_users.username "
                                        + "from cf_thin_client_users "
                                        + "left join cf_clients on cf_thin_client_users.idofclient=cf_clients.idofclient "
                                        + "order by cf_clients.idoforg");
        List users = q.list();
        for (Object o : users) {
            Object entry [] = (Object []) o;
            long idOfClient = ((BigInteger) entry [0]).longValue();
            long idOfOrg    = ((BigInteger) entry [1]).longValue();
            int role        = ((Integer) entry [2]).intValue();
            String username = ((String) entry [3]).trim();
            
            Client cl = DAOReadonlyService.getInstance().findClientById(idOfClient);
            cl = (Client) session.merge(cl);
            Person person = cl.getPerson();
            Org org = DAOReadonlyService.getInstance().findOrg(idOfOrg);
            
            items.add(new Item(cl, username, person, org, role, DEFAULT_ROLE));
        }
        this.items = items;
    }

    public void doRemoveUser(Long idOfClient) throws Exception {
        boolean clearView = false;
        ThinClientUserViewPage view = RuntimeContext.getAppContext().getBean(ThinClientUserViewPage.class);
        if (view != null && view.getIdOfClient() != null &&
            idOfClient == view.getIdOfClient()) {
            clearView = true;
        }

        RuntimeContext.getAppContext().getBean(ThinClientUserListPage.class).removeUser(idOfClient);
        if (clearView) {
            view.clearClient();
        }
    }

    @Transactional
    public void removeUser(Long idOfClient) throws Exception {
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            Query q = session.createSQLQuery("delete from cf_thin_client_users where idofclient=:idofclient");
            q.setLong("idofclient", idOfClient);
            q.executeUpdate();
        } catch (Exception e) {
            logger.error("Failed to remove thin client user", e);
        } finally {
        }
    }
}