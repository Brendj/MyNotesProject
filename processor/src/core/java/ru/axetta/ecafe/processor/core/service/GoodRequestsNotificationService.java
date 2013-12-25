package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DOCurrentOrgVersion;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequestPosition;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.report.GoodRequestsReport;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 03.12.13
 * Time: 16:23
 * To change this template use File | Settings | File Templates.
 */
@Service
public class GoodRequestsNotificationService {

    private final static Logger LOGGER = LoggerFactory.getLogger(GoodRequestsNotificationService.class);

    @Transactional
    public void notifyGoodRequestsReport() throws Exception{
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        if(runtimeContext.getOptionValueBool(Option.OPTION_ENABLE_NOTIFICATION_GOOD_REQUEST_CHANGE)){
            Session session = entityManager.unwrap(Session.class);
            Criteria criteria = session.createCriteria(Contragent.class);
            criteria.add(Restrictions.eq("classId", Contragent.TSP));
            List<Contragent> contragentTSPList = criteria.list();
            for (Contragent contragent: contragentTSPList){
                final String requestNotifyEmailAddress = contragent.getRequestNotifyMailList();
                if(StringUtils.isNotEmpty(requestNotifyEmailAddress)){
                    Set<Org> orgSet = contragent.getOrgs();
                    List<Long> idOfOrgs = new ArrayList<Long>(orgSet.size());
                    TreeSet<Long>  idOfSuplOrg = new TreeSet<Long>();
                    for (Org org: orgSet){
                        idOfOrgs.add(org.getIdOfOrg());
                        Long menuExchangeSourceOrgId = DAOUtils.findMenuExchangeSourceOrg(session, org.getIdOfOrg());
                        if(menuExchangeSourceOrgId!=null){
                            idOfSuplOrg.add(menuExchangeSourceOrgId);
                        }
                    }
                    if(idOfOrgs.isEmpty()) break;
                    String sql = "from DOCurrentOrgVersion where idOfOrg in :orgOwner";
                    Query query = session.createQuery(sql);
                    query.setParameterList("orgOwner", idOfOrgs);
                    List<DOCurrentOrgVersion> currentOrgVersions = query.list();
                    List<Long> currOrg = new ArrayList<Long>(currentOrgVersions.size());
                    for (DOCurrentOrgVersion version: currentOrgVersions){
                        currOrg.add(version.getIdOfOrg());
                    }
                    // Создадим записи если имеются максимальные значения
                    if(currOrg.size()!=idOfOrgs.size()){
                        for (Long orgOwner: idOfOrgs){
                            if(!currOrg.contains(orgOwner)){
                                sql = "select max(globalVersion) from GoodRequestPosition p where p.orgOwner=:orgOwner";
                                query = session.createQuery(sql);
                                query.setParameter("orgOwner", orgOwner);
                                List list = query.list();
                                if(!(list.isEmpty() || list.get(0)==null)){
                                    Object o = list.get(0);
                                    Long maxVal =  Long.valueOf(o.toString());
                                    DOCurrentOrgVersion doCurrentOrgVersion = new DOCurrentOrgVersion();
                                    doCurrentOrgVersion.setIdOfOrg(orgOwner);
                                    doCurrentOrgVersion.setLastVersion(maxVal);
                                    doCurrentOrgVersion.setObjectId(DOCurrentOrgVersion.GOOD_REQUEST_POSITION);
                                    session.persist(doCurrentOrgVersion);
                                }
                            }
                        }
                    }
                    if(currentOrgVersions.isEmpty())break;
                    Date currentDate = new Date();
                    List<GoodRequestPosition> positions = new ArrayList<GoodRequestPosition>();
                    for (DOCurrentOrgVersion version: currentOrgVersions){
                        sql = "from GoodRequestPosition p where p.globalVersion>:v and p.orgOwner=:orgOwner group by p.goodRequest, p.globalId";
                        Query q = session.createQuery(sql);
                        q.setParameter("v", version.getLastVersion());
                        q.setParameter("orgOwner", version.getIdOfOrg());
                        List<GoodRequestPosition> list = q.list();
                        if(!list.isEmpty()){
                            positions.addAll(list);
                        }
                    }
                    if(positions.isEmpty()){
                        LOGGER.debug("GoodRequest change list is empty");
                        break;
                    }
                    GoodRequestsReport.Builder reportBuilder = new GoodRequestsReport.Builder();
                    currentDate = CalendarUtils.truncateToDayOfMonth(currentDate);
                    GoodRequestsReport goodRequests = reportBuilder.build(session,currentDate,
                            CalendarUtils.addDays(currentDate, 31), new ArrayList<Long>(idOfSuplOrg));
                    if(goodRequests.getGoodRequestItems().size()<2){
                        LOGGER.debug("GoodRequests Report is empty");
                        break; // Строка итого всегда присутсвует
                    }
                    Object[] columnNames = goodRequests.getColumnNames();
                    StringBuilder newValueHistory = new StringBuilder();
                    newValueHistory.append("<table border=1 cellpadding=0 cellspacing=0><tr>");
                    for (Object o: columnNames){
                        newValueHistory.append("<th align=center>");
                        newValueHistory.append(o.toString()).append("</th>");
                    }
                    List<GoodRequestsReport.RequestItem> items = goodRequests.getGoodRequestItems();
                    newValueHistory.append("</tr>");
                    for (GoodRequestsReport.RequestItem item: items){
                        newValueHistory.append("<tr>");
                        for (Object o: columnNames){
                            final String colName = o.toString();
                            boolean isGood = false;
                            final String rowValue = item.getRowValue(colName, 1);
                            if(!(StringUtils.isAlphanumeric(colName) || StringUtils.isAlphaSpace(colName))){
                                for (GoodRequestPosition position: positions){
                                    if(position.getCurrentElementId()!=null){
                                        final boolean b = position.getCurrentElementId().equals(item.getIdOfGood());
                                        Calendar calendarDoneDate = Calendar.getInstance();
                                        calendarDoneDate.setTime(position.getGoodRequest().getDoneDate());
                                        CalendarUtils.truncateToDayOfMonth(calendarDoneDate);
                                        Date doneDate = calendarDoneDate.getTime();
                                        Calendar calendar = item.getColumnDate(colName);
                                        CalendarUtils.truncateToDayOfMonth(calendar);
                                        Date day = calendar.getTime();
                                        final boolean c = day.equals(doneDate);
                                        final boolean d = position.getOrgOwner().equals(item.getIdOfOrg());
                                        if(b && c && d){
                                            isGood = true; break;
                                        }
                                    }
                                }
                            }
                            newValueHistory.append("<td style='text-align: center;"+(isGood?"background-color: palevioletred; color: #fff;":"")+"'>");
                            newValueHistory.append(rowValue);
                            newValueHistory.append("</td>");
                        }
                        newValueHistory.append("</tr>");
                    }
                    newValueHistory.append("</table>");
                    final String fio = contragent.getContactPerson().getSurnameAndFirstLetters();
                    String[] values = {"contactPerson", fio, "reportValues", newValueHistory.toString()};
                    String addresses[] =  StringUtils.split(requestNotifyEmailAddress, ";");
                    boolean sended = false;
                    for (String address: addresses){
                         sended |= eventNotificationService.sendEmail(address, notificationType, values);
                    }
                    if(sended){
                        for (DOCurrentOrgVersion version: currentOrgVersions){
                            sql = "select max(globalVersion) from GoodRequestPosition p where p.orgOwner=:orgOwner";
                            query = session.createQuery(sql);
                            query.setParameter("orgOwner", version.getIdOfOrg());
                            List list = query.list();
                            if(!(list.isEmpty() || list.get(0)==null)){
                                Object o = list.get(0);
                                Long maxval =  Long.valueOf(o.toString());
                                version.setLastVersion(maxval);
                                session.update(version);
                            }
                        }
                    }
                } else {
                    LOGGER.debug("Contragent mail list is empty: {"+contragent.getRequestNotifyMailList()+"}");
                }
            }
        } else {
            LOGGER.debug("Disable option notification good request change");
        }
    }

    private final static String notificationType = EventNotificationService.NOTIFICATION_GOOD_REQUEST_CHANGE;

    @Autowired
    private EventNotificationService eventNotificationService;

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

}
