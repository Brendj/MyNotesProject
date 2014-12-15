/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.libriary;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DOConfirm;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.LibraryDistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Sasha
 * Date: 13.11.14
 * Time: 12:42
 * To change this template use File | Settings | File Templates.
 */
public class ExchangeIn extends LibraryDistributedObject {

    private static enum ExchangeStatusType{
        /*0*/ NEW,
        /*1*/ SEND,
        /*2*/ CONFIRMED,
        /*3*/ REJECTED,
        /*4*/ TRANSFERRED
    }

    private static HashMap<ExchangeStatusType, String> exchangeStatusTypeStringHashMap = new HashMap<ExchangeStatusType, String>();
    static {
        exchangeStatusTypeStringHashMap.put(ExchangeStatusType.NEW, "Новая заявка");
        exchangeStatusTypeStringHashMap.put(ExchangeStatusType.SEND, "Отправлено");
        exchangeStatusTypeStringHashMap.put(ExchangeStatusType.CONFIRMED, "Подтверждено");
        exchangeStatusTypeStringHashMap.put(ExchangeStatusType.REJECTED, "Отклонено");
        exchangeStatusTypeStringHashMap.put(ExchangeStatusType.TRANSFERRED, "Книги переданы");
    }

    private String caption;
    private Date incomeDate;
    private String commentIn;
    private String school;
    private String status;
    private String commentOut;

    @Override
    public void createProjections(Criteria criteria) {
        ProjectionList projectionList = Projections.projectionList();
        addDistributedObjectProjectionList(projectionList);

        projectionList.add(Projections.property("commentOut"), "commentOut");
        projectionList.add(Projections.property("commentIn"), "commentIn");
        projectionList.add(Projections.property("status"), "status");
        projectionList.add(Projections.property("school"), "school");
        projectionList.add(Projections.property("caption"), "caption");
        projectionList.add(Projections.property("incomeDate"), "incomeDate");

        criteria.setProjection(projectionList);
    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {
        // Проверка на дублирование данных
        Criteria exchangeInCriteria = session.createCriteria(ExchangeIn.class);
        exchangeInCriteria.add(Restrictions.eq("guid", getGuid()));
        List exchangeInList = exchangeInCriteria.list();
        session.clear();
        if(exchangeInList == null || exchangeInList.isEmpty()){
            DistributedObjectException distributedObjectException =  new DistributedObjectException("ExchangeIn NOT_FOUND_VALUE ExchangeOut");
            distributedObjectException.setData(getGuid());
            throw  distributedObjectException;
        }

        // Здесь находим экземляры instance из БД, соответствующие позициям заявок и сохраняем их в таблице
        // DOConfirms, если статус заявки поменялся на "Книги переданы"
        if (getStatus().equals(exchangeStatusTypeStringHashMap.get(ExchangeStatusType.TRANSFERRED))) {
            try {
                searchForObjectsAndSend(session);
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    private void searchForObjectsAndSend(Session session) throws Exception {

        ExchangeIn exchangeIn = DAOUtils.findDistributedObjectByRefGUID(ExchangeIn.class, session, getGuid());

        // Получение позиций по данной заявке
        Criteria exchangeInPosCriteria = session.createCriteria(ExchangeInPos.class);
        exchangeInPosCriteria.add(Restrictions.eq("exchangeIn", exchangeIn));
        List<ExchangeInPos> exchangeInPosList = (List<ExchangeInPos>) exchangeInPosCriteria.list();

        for (ExchangeInPos exchangeInPos : exchangeInPosList) {

            // Получение экземпляров ссылающихся на ту же библиотечную запись, что и позиция по заявке
            Criteria instanceCriteria = session.createCriteria(Instance.class);
            instanceCriteria.add(Restrictions.eq("publication", exchangeInPos.getPublication()));
            List<Instance> instanceList = (List<Instance>) instanceCriteria.list();

            for (Instance instance : instanceList) {

                // Проверка наличия ссылки с экземляра на заявку и отсылка при ее наличии
                ExchangeIn exchangeInFromInstance = instance.getExchangeIn();
                if (exchangeInFromInstance != null) {
                    if (instance.getExchangeIn().getGuid().equals(exchangeIn.getGuid())) {

                        // Получение заявки ExchangeOut, т.к. почему-то не вышло для этого использовать this
                        ExchangeOut exchangeOutEqualToThis = DAOUtils.findDistributedObjectByRefGUID(ExchangeOut.class, session, getGuid());
                        if (!(exchangeOutEqualToThis.getOrgOwner() ==  null || instance.getGuid() == null)) {

                            // Проверка на наличие уже отосланных объектов
                            Criteria doConfirmUniqnessCheckCriteria = session.createCriteria(DOConfirm.class);
                            doConfirmUniqnessCheckCriteria.add(Restrictions.eq("distributedObjectClassName", "Instance"));
                            doConfirmUniqnessCheckCriteria.add(Restrictions.eq("guid", instance.getGuid()));
                            doConfirmUniqnessCheckCriteria.add(Restrictions.eq("orgOwner", exchangeOutEqualToThis.getOrgOwner()));
                            List<DOConfirm> doConfirmList = (List<DOConfirm>) doConfirmUniqnessCheckCriteria.list();

                            if (doConfirmList.size() <= 0) {

                                // Поиск парной (к найденному объекту0 instance парного объекта issuable
                                //Criteria searchForIssuableCriteria = session.createCriteria(Issuable.class);
                                //searchForIssuableCriteria.createAlias("Instance","i", JoinType.LEFT_OUTER_JOIN);
                                //searchForIssuableCriteria.add(Restrictions.eq("i.guid", instance.getGuid()));
                                //Issuable issuablePairToInstance = (Issuable) searchForIssuableCriteria.uniqueResult();


                                String issuableGuid = null;
                                try {
                                    // Здесь должен быть только один объект
                                    Set<Issuable> issuablePairToInstance = instance.getIssuableInternal();
                                    issuableGuid = issuablePairToInstance.iterator().next().getGuid();
                                } catch (Exception e) {
                                    throw e;
                                } finally {
                                    if (issuableGuid != "" || issuableGuid != null) {
                                        session.saveOrUpdate(new DOConfirm("Instance", instance.getGuid(), exchangeOutEqualToThis.getOrgOwner()));
                                        session.saveOrUpdate(new DOConfirm("Issuable", issuableGuid, exchangeOutEqualToThis.getOrgOwner()));
                                    } else {
                                        DistributedObjectException distributedObjectException =  new DistributedObjectException("ExchangeIn DATA_ERROR Instance GUID = '"
                                                + instance.getGuid() + "'.");
                                        distributedObjectException.setData(getGuid());
                                        throw  distributedObjectException;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        session.clear();
    }

    @Override
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion,
            String currentLastGuid, Integer currentLimit) throws Exception {
        return null; //toSelfProcess(session, idOfOrg, currentMaxVersion, currentLastGuid, currentLimit);
    }

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "CommentOut", commentOut);
        XMLUtils.setAttributeIfNotNull(element, "CommentIn", commentIn);
        XMLUtils.setAttributeIfNotNull(element, "IncomeDate", new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(incomeDate));
        XMLUtils.setAttributeIfNotNull(element, "Status", status);
        XMLUtils.setAttributeIfNotNull(element, "School", school);
        XMLUtils.setAttributeIfNotNull(element, "Caption", caption);
    }

    @Override
    public ExchangeIn parseAttributes(Node node) throws Exception {
        setCommentOut(XMLUtils.getStringAttributeValue(node, "CommentOut", 255));
        setCommentIn(XMLUtils.getStringAttributeValue(node, "CommentIn", 255));
        setIncomeDate(XMLUtils.getDateTimeAttributeValue(node, "IncomeDate"));
        setStatus(XMLUtils.getStringAttributeValue(node, "Status", 255));
        setSchool(XMLUtils.getStringAttributeValue(node, "School", 255));
        setCaption(XMLUtils.getStringAttributeValue(node, "Caption", 255));
        setSendAll(SendToAssociatedOrgs.DontSend);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        //setOrgOwner(distributedObject.getOrgOwner());
        setCommentOut(((ExchangeIn) distributedObject).getCommentOut());
        setCommentIn(((ExchangeIn) distributedObject).getCommentIn());
        setIncomeDate(((ExchangeIn) distributedObject).getIncomeDate());
        setStatus(((ExchangeIn) distributedObject).getStatus());
        setSchool(((ExchangeIn) distributedObject).getSchool());
        setCaption(((ExchangeIn) distributedObject).getCaption());
    }

    public String getCommentOut() {
        return commentOut;
    }

    public void setCommentOut(String commentOut) {
        this.commentOut = commentOut;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public Date getIncomeDate() {
        return incomeDate;
    }

    public void setIncomeDate(Date incomeDate) {
        this.incomeDate = incomeDate;
    }

    public String getCommentIn() {
        return commentIn;
    }

    public void setCommentIn(String commentIn) {
        this.commentIn = commentIn;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}
