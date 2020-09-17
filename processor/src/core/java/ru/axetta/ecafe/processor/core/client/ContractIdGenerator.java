/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.client;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.OrgPreContractId;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 13.10.2009
 * Time: 15:42:35
 * To change this template use File | Settings | File Templates.
 */
public class ContractIdGenerator {

    private static final Logger logger = LoggerFactory.getLogger(ContractIdGenerator.class);
    private static final long MIN_ORDER_ID = 0;
    private static final long MAX_ORDER_ID = 99999;
    private static final int BATCH_COUNT = 100;

    private final SessionFactory sessionFactory;
    private String preContractIdOrgs;
    private List<Long> preContractIdOrgList = new ArrayList<>();

    private final String PRE_GENERATE_DEFAULT = "NO";
    private final String PRE_GENERATE_ALL = "ALL";

    public ContractIdGenerator(Properties properties, SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.preContractIdOrgs = properties.getProperty("ecafe.processor.client.contractId.preGenerate", PRE_GENERATE_DEFAULT);
        try {
            String[] arr = preContractIdOrgs.split(",");
            for (String s : arr) {
                preContractIdOrgList.add(new Long(s));
            }
        } catch (Exception e) { }
        logger.info("PreContractIdOrgs=" + preContractIdOrgs);
    }

    public long generateTransactionFree (long idOfOrg) throws Exception {
        return generateTransactionFree(idOfOrg, 1).get(0);
    }

    public List<Long> generateTransactionFree (long idOfOrg, int count) throws Exception {
        if (!usePreContractIds(idOfOrg)) {
            return generateTransactionFreeOldWay(idOfOrg, count);
        }
        List<Long> list = DAOService.getInstance().getOrgPreContractIdList(idOfOrg, count);
        while (list.size() < count) {
            //если в таблице не хватает прегенеренных л/с, то делаем новые
            generateAndSaveContractIdBatch(idOfOrg, BATCH_COUNT);
            list = DAOService.getInstance().getOrgPreContractIdList(idOfOrg, count);
        }
        return list;
    }

    private boolean usePreContractIds(long idOfOrg) {
        if (preContractIdOrgs.equals(PRE_GENERATE_DEFAULT)) return false;
        if (preContractIdOrgs.equals(PRE_GENERATE_ALL)) return true;
        return preContractIdOrgList.contains(idOfOrg);
    }

    private List<Long> generateTransactionFreeOldWay(long idOfOrg, int count) throws Exception {
        Long lastClientContractId = DAOUtils.updateOrgLastContractIdWithPessimisticLock(idOfOrg, count);

        Long contractIdSize = null;
        String s = (String) RuntimeContext.getInstance().getConfigProperties().get("ecafe.processor.client.contractIdSize");
        if(s != null &&!s.isEmpty()){
            contractIdSize = Long.parseLong(s);
        } else {
            contractIdSize = 10L;
        }
        Long divider = (contractIdSize == 10) ? 1000000000L : 100000000L;
        List<Long> lastClientContractIds = DAOService.getInstance().getNextFreeLastClientContractId(divider, idOfOrg, lastClientContractId, count);
        if (lastClientContractIds.isEmpty()) {
            throw new IllegalArgumentException("Not available client contractId");
        }

        return getNextContractIds(idOfOrg, lastClientContractIds);
    }

    public void generateAndSaveContractIdBatch (long idOfOrg, int count) throws Exception {
        List<Long> contractIds = generateTransactionFreeOldWay(idOfOrg, count);
        DAOService.getInstance().saveOrgPreContractIds(idOfOrg, contractIds);
    }

    public void updateUsedContractId(Session session, long contractId, long idOfOrg) {
        if (!usePreContractIds(idOfOrg)) return;
        Query query = session.createQuery("select opc from OrgPreContractId opc where opc.contractId = :contractId");
        query.setParameter("contractId", contractId);
        OrgPreContractId orgPreContractId = (OrgPreContractId) query.uniqueResult();
        orgPreContractId.setUsed(true);
        orgPreContractId.setUsedDate(new Date());
        session.update(orgPreContractId);
    }

    protected static List<Long> getNextContractIds(long idOfOrg, List<Long> lastClientContractIds) {
        List<Long> result = new ArrayList<Long>();
        for(Long lastClientContractId : lastClientContractIds){
            result.add(getNextContractId(idOfOrg, lastClientContractId));
        }
        return result;
    }

    protected static long getNextContractId(long idOfOrg, long lastClientContractId) {
        long newClientContractId;
        if (lastClientContractId > 9999) {
            newClientContractId = addLastDigitByLuhn((10000*(long) (lastClientContractId/10000) + idOfOrg) * 10000 + ((long) (lastClientContractId%10000)));
        } else {
            newClientContractId = addLastDigitByLuhn(idOfOrg * 10000 + lastClientContractId);
        }
        return newClientContractId;
    }

    public static long addLastDigitByLuhn(long value) {
        long sum = 0;
        long currValue = value;
        boolean evenDigit = true;
        while (currValue != 0) {
            long currDigit = currValue % 10;
            if (evenDigit) {
                currDigit *= 2;
                if (currDigit > 9) {
                    currDigit -= 9;
                }
            }
            sum += currDigit;
            if (sum > 10) {
                sum -= 10;
            }
            evenDigit = !evenDigit;
            currValue = currValue / 10;
        }
        long controlDigit = 0 == sum ? 0 : 10 - sum;
        return value * 10 + controlDigit;
    }

    public static boolean luhnTest(String number){
        int s1 = 0, s2 = 0;
        String reverse = new StringBuffer(number).reverse().toString();
        for(int i = 0 ;i < reverse.length();i++){
            int digit = Character.digit(reverse.charAt(i), 10);
            if(i % 2 == 0){//this is for odd digits, they are 1-indexed in the algorithm
                s1 += digit;
            }else{//add 2 * digit for 0-4, add 2 * digit - 9 for 5-9
                s2 += 2 * digit;
                if(digit >= 5){
                    s2 -= 9;
                }
            }
        }
        return (s1 + s2) % 10 == 0;
    }

}