/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.utils;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.*;
import ru.axetta.ecafe.processor.core.sync.distributionsync.DistributedObjectException;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.IConfigProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.persistence.*;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
@Scope("singleton")
public class DAOService {

    private final static Logger logger = LoggerFactory.getLogger(DAOService.class);

    @PersistenceContext
    EntityManager em;

    @Transactional
    public List<TransactionJournal> fetchTransactionJournal(int nRecs) {
        return DAOUtils.fetchTransactionJournalRecs(em, nRecs);
    }

    public static DAOService getInstance() {
        return RuntimeContext.getAppContext().getBean(DAOService.class);
    }

    @Transactional
    public void setConfigurationProviderInDO(Class<? extends IConfigProvider> clazz,Long id, Long idOfConfigurationProvider){
        IConfigProvider distributedObject = em.find(clazz, id);
        if(distributedObject!=null){
            distributedObject.setIdOfConfigurationProvider(idOfConfigurationProvider);
            em.persist(distributedObject);
    }
    }

    @Transactional
    public void updateDeleteState(DistributedObject distributedObject) throws Exception{
        List list = em.createQuery("from "+distributedObject.getClass().getSimpleName() + " where guid='"+distributedObject.getGuid()+"'").getResultList();
        if(!list.isEmpty()){
            DistributedObject object = (DistributedObject) list.get(0);
            object.setDeletedState(true);
            object.setDeleteDate(new Date());
            em.persist(object);
        }
    }

    @Transactional
    public void setConfigurationProviderInOrg(Long idOfOrg, ConfigurationProvider configurationProvider){
        Org org = em.find(Org.class, idOfOrg);
        if(org!=null){
            org.setConfigurationProvider(configurationProvider);
            em.persist(org);
        }
    }

    @Transactional
    public <T> T findRefDistributedObject(Class<T> clazz, Long longIdOfTechnoMap){
        return em.getReference(clazz, longIdOfTechnoMap);
    }

    @Transactional
    public void setDeletedState(DistributedObject distributedObject){
        distributedObject = em.find(distributedObject.getClass(),distributedObject.getGlobalId());
        distributedObject.setDeletedState(true);
        em.persist(distributedObject);
    }

    public <T> T findDistributedObjectByRefGUID(Class<T> clazz, String guid){
        TypedQuery<T> query = em.createQuery("from "+clazz.getSimpleName()+" where guid='"+guid+"'",clazz);
        List<T> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public List<DistributedObject> findDistributedObjectByInGUID(String name, List<String> guids){
        TypedQuery<DistributedObject> query = em.createQuery("from "+name+" where guid in (:guids)",DistributedObject.class);
        query.setParameter("guids",guids);
        return query.getResultList();
    }

    @Transactional
    public List<TechnologicalMapProduct> getTechnologicalMapProduct(Long id){
        return em.createQuery("from TechnologicalMapProduct where idOfTechnoMap="+id,TechnologicalMapProduct.class).getResultList();
    }

    @Transactional
    public <T> List<T> getDistributedObjects(Class<T> clazz){
        return em.createQuery("from "+clazz.getSimpleName()+" order by id",clazz).getResultList();
    }

    @Transactional
    public List<String> getGUIDsInConfirms(String className, Long orgOwner){
        TypedQuery<String> query ;
        query = em.createQuery("Select guid from DOConfirm where orgOwner=:orgOwner and distributedObjectClassName=:distributedObjectClassName",String.class);
        query.setParameter("orgOwner",orgOwner);
        query.setParameter("distributedObjectClassName",className);
        return  query.getResultList();
    }

    public ConfigurationProvider getConfigurationProvider(Long orgOwner, Class<? extends DistributedObject> clazz) throws Exception{
        List list = Arrays.asList(clazz.getInterfaces());
        ConfigurationProvider configurationProvider = null;
        if(list.contains(IConfigProvider.class)){
            TypedQuery<ConfigurationProvider> configurationProviderQuery = null; //= em.createQuery("select configurationProvider from Org where idOfOrg=:idOfOrg", ConfigurationProvider.class);
            //configurationProviderQuery.setParameter("idOfOrg",orgOwner);
            Org org = em.find(Org.class, orgOwner);
            configurationProvider = org.getConfigurationProvider();
            /* Если есть конфигурация синхронизируемой организации */
            if(configurationProvider==null){
                TypedQuery<MenuExchangeRule> queryMER = em.createQuery("from MenuExchangeRule where idOfDestOrg=:idOfOrg",MenuExchangeRule.class);
                queryMER.setParameter("idOfOrg",orgOwner);
                queryMER.setMaxResults(1);
                MenuExchangeRule menuExchangeRule = queryMER.getSingleResult();
                if(menuExchangeRule != null){
                    Org sourceOrg = em.find(Org.class, menuExchangeRule.getIdOfSourceOrg());
                    if(sourceOrg != null){
                        configurationProvider = sourceOrg.getConfigurationProvider();
                    }
                }
            }
            if(configurationProvider == null) {
                //return new ArrayList<DistributedObject>(0);
                // При выбрасывании исключения падает вся синхронизация как быть с библиотекой?
                throw new DistributedObjectException(DistributedObjectException.ErrorType.CONFIGURATION_PROVIDER_NOT_FOUND);
            }
        }
        return configurationProvider;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<DistributedObject> getDistributedObjects(Class<? extends DistributedObject> clazz, Long currentMaxVersion,Long orgOwner) throws Exception{
        List<DistributedObject> result = null;
        try{
            TypedQuery<DistributedObject> query ;
            String where = "";
            if(orgOwner != null){
                List list = Arrays.asList(clazz.getInterfaces());
                if(list.contains(IConfigProvider.class)){
                    ConfigurationProvider configurationProvider = getConfigurationProvider(orgOwner, clazz);
                    where = " idOfConfigurationProvider="+configurationProvider.getIdOfConfigurationProvider();
                }  else {
                    where = "(orgOwner="+orgOwner+" or orgOwner = NULL) ";
                }

            }
            if(currentMaxVersion != null){
                TypedQuery<DOVersion> queryVersion = em.createQuery("from DOVersion where UPPER(distributedObjectClassName)=:distributedObjectClassName",DOVersion.class);
                queryVersion.setParameter("distributedObjectClassName",clazz.getSimpleName().toUpperCase());
                List<DOVersion> doVersionList = queryVersion.getResultList();
                Long doVersion = null;
                if(doVersionList.isEmpty()) {
                    doVersion = -1L;
                } else {
                    doVersion = doVersionList.get(0).getCurrentVersion();
                }
                where = (where.equals("")?"": where + " and ") + " (globalVersion>"+currentMaxVersion + " and globalVersion != "+doVersion+")";
            }
            String select = "from " + clazz.getSimpleName() + (where.equals("")?"":" where " + where);
            query = em.createQuery(select, DistributedObject.class);
            result = query.getResultList();
            em.flush();
        } catch (Exception e){
            logger.error("Error getDistributedObjects: ",e);
        }
        return result;
    }

    @Transactional
    public Long getDOVersionByGUID(DistributedObject distributedObject) {
        String stringQuery = String.format("select globalVersion from %s where guid='%s'",distributedObject.getClass().getSimpleName(), distributedObject.getGuid());
        Query query = em.createQuery(stringQuery);
        List list = query.getResultList();
        long version = 0L;
        if(!list.isEmpty()){
            version = (Long) list.get(0);
        }
        return version;
    }

    @Transactional
    public Long getVersionByDistributedObjects(Class clazz) {
        TypedQuery<DOVersion> query = em.createQuery("from DOVersion where UPPER(distributedObjectClassName)=:distributedObjectClassName",DOVersion.class);
        query.setParameter("distributedObjectClassName", clazz.getSimpleName().toUpperCase());
        List<DOVersion> doVersionList = query.getResultList();
        Long version = (long) 0;
        if(!doVersionList.isEmpty()){
            version = version + doVersionList.get(0).getCurrentVersion();
        }
        return version;
    }


    @Transactional
    public Long updateVersionByDistributedObjects(String name) {
        TypedQuery<DOVersion> query = em.createQuery("from DOVersion where UPPER(distributedObjectClassName)=:distributedObjectClassName",DOVersion.class);
        query.setParameter("distributedObjectClassName",name.toUpperCase());
        List<DOVersion> doVersionList = query.getResultList();
        DOVersion doVersion = null;
        Long version = null;
        if(doVersionList.size()==0) {
            doVersion = new DOVersion();
            doVersion.setCurrentVersion(0L);
            version = 0L;
        } else {
            doVersion = doVersionList.get(0);
            version = doVersion.getCurrentVersion()+1;
            doVersion.setCurrentVersion(version);
        }
        doVersion.setDistributedObjectClassName(name);
        em.persist(doVersion);
        return version;
    }

    @Transactional
    public Long getDistributedObjectVersion(DistributedObject distributedObject) {
        TypedQuery<DistributedObject> query = em.createQuery("from "+distributedObject.getClass().getSimpleName()+" where guid='"+distributedObject.getGuid()+"'", DistributedObject.class);
        List<DistributedObject> distributedObjectList = query.getResultList();
        if (distributedObjectList.isEmpty()) {
            return null;
        }
        return distributedObjectList.get(0).getGlobalVersion();
    }

    @Transactional
    public DistributedObject createDistributedObject(DistributedObject distributedObject){
        distributedObject.setCreatedDate(new Date());
        TypedQuery<DOVersion> query = em.createQuery("from DOVersion where UPPER(distributedObjectClassName)=:distributedObjectClassName",DOVersion.class);
        query.setParameter("distributedObjectClassName",distributedObject.getClass().getSimpleName().toUpperCase());
        List<DOVersion> doVersionList = query.getResultList();
        if(doVersionList.size()==0) {
            distributedObject.setGlobalVersion(0L);
        } else {
            distributedObject.setGlobalVersion(doVersionList.get(0).getCurrentVersion()-1);
        }
        return em.merge(distributedObject);
    }

    public DistributedObject findDistributedObject(DistributedObject distributedObject){
        TypedQuery<DistributedObject> query = em.createQuery("from "+distributedObject.getClass().getSimpleName()+" where guid='"+distributedObject.getGuid()+"'", DistributedObject.class);
        List<DistributedObject> distributedObjectList = query.getResultList();
        if (distributedObjectList.isEmpty()) {
            return null;
        }
        return em.find(distributedObject.getClass(),distributedObjectList.get(0).getGlobalId());
    }

    @Transactional
    public DistributedObject mergeDistributedObject(DistributedObject distributedObject, Long globalVersion){
        TypedQuery<DistributedObject> query = em.createQuery("from "+distributedObject.getClass().getSimpleName()+" where guid='"+distributedObject.getGuid()+"'", DistributedObject.class);
        List<DistributedObject> distributedObjectList = query.getResultList();
        if (distributedObjectList.isEmpty()) {
            return null;
        }
        DistributedObject d = em.find(distributedObject.getClass(),distributedObjectList.get(0).getGlobalId());
        d.fill(distributedObject);
        d.setGlobalVersion(globalVersion);
        d.setDeletedState(distributedObject.getDeletedState());
        d.setLastUpdate(new Date());
        em.persist(d);
        return em.find(distributedObject.getClass(),distributedObjectList.get(0).getGlobalId());
    }

    @Transactional
    public void persistEntity(Object entity) throws Exception{
        em.persist(entity);
    }

    @Transactional
    public Long getClientContractIdByCardId(String idOfCard) throws Exception {
        Client cl = DAOUtils.findClientByCardNo(em, Long.decode(idOfCard));
        if (cl == null) {
            return null;
        }
        return cl.getContractId();
    }

    @Transactional
    public void deleteEntity(Object entity) {
        entity = em.merge(entity);
        if (entity != null) {
            em.remove(entity);
        }
    }

    @Transactional
    public Long getContractIdByCardNo(long lCardId) throws Exception {
        Client client = DAOUtils.findClientByCardNo(em, lCardId);
        if (client != null) {
            return client.getContractId();
        }
        return null;
    }

    @Transactional
    public boolean enableClientNotificationBySMS(Long contractId, boolean state) {
        Query q = em.createQuery("update Client set notifyViaSMS=:notifyViaSMS where contractId=:contractId");
        q.setParameter("notifyViaSMS", state);
        q.setParameter("contractId", contractId);
        return q.executeUpdate()!=0;
    }

    @Transactional
    public boolean enableClientNotificationByEmail(Long contractId, boolean state) {
        Query q = em.createQuery("update Client set notifyViaEmail=:notifyViaEmail where contractId=:contractId");
        q.setParameter("notifyViaEmail", state);
        q.setParameter("contractId", contractId);
        return q.executeUpdate()!=0;
    }

    @Transactional
    public boolean setClientMobilePhone(Long contractId, String mobile) {
        Query q = em.createQuery("update Client set mobile=:mobile where contractId=:contractId");
        q.setParameter("mobile", mobile);
        q.setParameter("contractId", contractId);
        return q.executeUpdate()!=0;
    }

    @Transactional
    public boolean setClientPhone(Long contractId, String phone) {
        Query q = em.createQuery("update Client set phone=:phone where contractId=:contractId");
        q.setParameter("phone", phone);
        q.setParameter("contractId", contractId);
        return q.executeUpdate()!=0;
    }

    @Transactional
    public boolean setClientAddress(Long contractId, String address) {
        Query q = em.createQuery("update Client set address=:address where contractId=:contractId");
        q.setParameter("address", address);
        q.setParameter("contractId", contractId);
        return q.executeUpdate()!=0;
    }

    @Transactional
    public boolean setClientEmail(Long contractId, String email) {
        Query q = em.createQuery("update Client set email=:email where contractId=:contractId");
        q.setParameter("email", email);
        q.setParameter("contractId", contractId);
        return q.executeUpdate()!=0;
    }

    @Transactional
    public boolean setClientPassword(Long contractId, String base64passwordHash) {
        Query q = em.createQuery("update Client set cypheredPassword=:base64passwordHash where contractId=:contractId");
        q.setParameter("base64passwordHash",base64passwordHash);
        q.setParameter("contractId", contractId);
        return q.executeUpdate()!=0;
    }

    @Transactional
    public boolean setClientExpenditureLimit(Long contractId, long limit) {
        Query q = em.createQuery("update Client set expenditureLimit=:expenditureLimit where contractId=:contractId");
        q.setParameter("expenditureLimit", limit);
        q.setParameter("contractId", contractId);
        return q.executeUpdate()!=0;
    }
    @Transactional
    public Org getOrg(Long idOfOrg) {
        Query q = em.createQuery("from Org where idOfOrg = :idOfOrg");
        q.setParameter("idOfOrg", idOfOrg);
        List l = q.getResultList();
        if (l.size() == 0) {
            return null;
        }
        return (Org)l.get(0);
    }

    @Transactional
    public User setUserInfo(User user){
        return em.merge(user);
    }

    @Transactional
    public Client getClientByContractId(long contractId) throws Exception {
        Client cl = DAOUtils.findClientByContractId(em, contractId);
        if (cl == null) {
            return null;
        }
        return cl;
    }

    @Transactional
    public void addIntegraPartnerAccessPermissionToClient(Long idOfClient, String idOfIntegraPartner) throws Exception {
        Client cl = em.find(Client.class, idOfClient);
        if (cl == null) {
            throw new Exception("Client not found: " + idOfClient);
        }
        cl.addIntegraPartnerAccessPermission(idOfIntegraPartner);
        em.persist(cl);
    }
    @Transactional
    public List<TechnologicalMapProduct> getTechnologicalMapProducts(TechnologicalMap technologicalMap) {
        TypedQuery<TechnologicalMapProduct> query = em.createQuery("from TechnologicalMapProduct where technologicalMap=:technologicalMap", TechnologicalMapProduct.class);
        query.setParameter("technologicalMap",technologicalMap);
        return query.getResultList();
    }

    @Transactional
    public Publication getPublicationByIsbn(String isbn) {
        Query query = em.createQuery("from Publication2 where isbn=:isbn", Publication.class);
        query.setParameter("isbn", isbn);
        return (Publication) query.getSingleResult();
    }

    @Transactional
    public Publication getPublicationByHash(String hash) {
        Query query = em.createQuery("from Publication2 where hash=:hash", Publication.class);
        query.setParameter("hash", hash);
        return (Publication) query.getSingleResult();
    }
    
    @Transactional
    public Org findOrById(long idOfOrg) {
        return em.find(Org.class, idOfOrg);
    }
    
    @Transactional
    public Client findClientById(long idOfClient) {
        return em.find(Client.class, idOfClient);
    }

    @Transactional
    public <T> T saveEntity(T entity) {
        return em.merge(entity);
    }

    @Transactional
    public Client findAndDeleteLinkingToken(String linkingToken) {
        Query query = em.createQuery("from LinkingToken where token=:token");
        query.setParameter("token", linkingToken);
        try {
            LinkingToken token = (LinkingToken)query.getSingleResult();
            em.remove(token);
            return em.find(Client.class, token.getIdOfClient());
        } catch (NoResultException e) {
            return null;
        }
    }
    

    @Transactional
    public LinkingToken generateLinkingToken(Client client) {
        Query query = em.createQuery("delete from LinkingToken where idOfClient=:idOfClient");
        query.setParameter("idOfClient", client.getIdOfClient());
        query.executeUpdate();
        ////
        SecureRandom secureRandom = new SecureRandom();
        String randomToken;
        int nSize=9;
        for (int nCycle=0;;nCycle++) {
            if (nCycle==10) { nSize++; nCycle=0; }
            randomToken = new BigInteger(nSize*5, secureRandom).toString(32);
            query = em.createQuery("from LinkingToken where token=:token");
            query.setParameter("token", randomToken);
            List l = query.getResultList();
            if (l.size() == 0) {
                break;
            }
        }
        LinkingToken token = new LinkingToken();
        token.setIdOfClient(client.getIdOfClient());
        token.setToken(randomToken);
        em.persist(token);
        return token;
    }

    @Transactional
    public boolean doesClientBelongToFriendlyOrgs(Long orgId, Long idOfClient) throws Exception {
        Org org = em.find(Org.class, orgId);
        if (org == null) {
            throw new Exception("Организация не найдена: " + orgId);
        }
        Client cl = em.find(Client.class, idOfClient);
        if (cl == null) {
            throw new Exception("Клиент не найден: " + idOfClient);
        }
        if (cl.getOrg().getIdOfOrg() == orgId) {
            return true;
        }
        Set<Org> friendlyOrgs = org.getFriendlyOrg();
        for (Org o :friendlyOrgs) {
            if (cl.getOrg().getIdOfOrg() == o.getIdOfOrg()) {
                return true;
            }
        }
        return false;
    }

    @Transactional
    public List<Client> findClientsByMobilePhone(String mobilePhone) {
        TypedQuery<Client> query = em.createQuery("from Client where mobile=:mobile", Client.class);
        query.setParameter("mobile", mobilePhone);
        return query.getResultList();
    }

}
