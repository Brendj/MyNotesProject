package ru.axetta.ecafe.processor.core.persistence.service.org;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.dao.org.OrgRepository;
import ru.axetta.ecafe.processor.core.persistence.dao.org.OrgWritableRepository;
import ru.axetta.ecafe.processor.core.persistence.dao.report.mailing.MailingListReportsTypes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

/**
 * User: shamil
 * Date: 20.08.14
 * Time: 12:34
 */
@Service
public class OrgService {
    OrgRepository dao;
    OrgWritableRepository writableDao;

    public static OrgService getInstance() {
        return RuntimeContext.getAppContext().getBean(OrgService.class);
    }

    @Autowired
    public void setDao(OrgRepository dao) {
        this.dao = dao;
        dao.setClazz(Org.class);
    }

    @Autowired
    public void setWritableDao(OrgWritableRepository writableDao) {
        this.writableDao = writableDao;
    }

    public Org findOrg(Long id) {
        return dao.findOne(id);
    }

    public List<BigInteger> findOrgCategories(Long id) {
        return dao.findOrgCategories(id);
    }

    public Org findOrgWithFriendlyOrgs(Long id) {
        return dao.findOrgWithFriendlyOrgs(id);
    }

    public Org getMainBulding(long idOfOrg) {
        Org org = findOrgWithFriendlyOrgs(idOfOrg);
        for (Org org1 : org.getFriendlyOrg()) {
            if (org1.isMainBuilding()) {
                return org1;
            }
        }
        return org;
    }

    public Org getMainBulding(Org org) {
        return getMainBulding(org.getIdOfOrg());
    }

    public String getMailingList(Org org, MailingListReportsTypes type) {
        switch (type) {
            case NUTRITION:
                return org.getMailingListReportsOnNutrition();
            case VISITS:
                return org.getMailingListReportsOnVisits();
            case SOME_LIST_1:
                return org.getMailingListReports1();
            case SOME_LIST_2:
                return org.getMailingListReports2();
        }
        return "";
    }

    public void setMailingList(Org org, String mailingList, MailingListReportsTypes type) {
        switch (type) {
            case NUTRITION:
                org.setMailingListReportsOnNutrition(mailingList);
                break;
            case VISITS:
                org.setMailingListReportsOnVisits(mailingList);
                break;
            case SOME_LIST_1:
                org.setMailingListReports1(mailingList);
                break;
            case SOME_LIST_2:
                org.setMailingListReports2(mailingList);
                break;
        }
        saveOrg(org);
    }

    public boolean saveOrg(Org org){
        return writableDao.saveOrg(org);
    }
}
