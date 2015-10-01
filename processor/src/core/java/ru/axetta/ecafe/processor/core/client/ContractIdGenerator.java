/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.client;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 13.10.2009
 * Time: 15:42:35
 * To change this template use File | Settings | File Templates.
 */
public class ContractIdGenerator {

    //private static final Logger logger = LoggerFactory.getLogger(ContractIdGenerator.class);
    private static final long MIN_ORDER_ID = 0;
    private static final long MAX_ORDER_ID = 99999;

    private final SessionFactory sessionFactory;

    public ContractIdGenerator(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public long generateTransactionFree (long idOfOrg, Session session) throws Exception {
        Org org = (Org) session.load(Org.class, idOfOrg);
        long lastClientContractId = org.getLastClientContractId();

        Long l = null;
        String s = (String) RuntimeContext.getInstance().getConfigProperties().get("ru.ecafe.internal.contract.maxId");
        if(s != null &&!s.isEmpty()){
            l = Long.parseLong(s);
        }
        if (MIN_ORDER_ID > lastClientContractId
                || ((l==null) && MAX_ORDER_ID < lastClientContractId)
                || ((l!=null) && l < lastClientContractId) ) {
            throw new IllegalArgumentException("Too large last client contractId");
        }
        lastClientContractId++;

        long newClientContractId;
        if(l == null){
            newClientContractId = getNextContractId(org.getIdOfOrg(), lastClientContractId);
        }else {
            newClientContractId = addLastDigitByLuhn(org.getIdOfOrg() * l + lastClientContractId);
        }
        org.setLastClientContractId(lastClientContractId);
        session.update(org);
        session.flush();
        return newClientContractId;
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

    public long generate(long idOfOrg) throws Exception {
        Transaction transaction = null;
        Session session = sessionFactory.openSession();
        try {
            transaction = session.beginTransaction();

            long newClientContractId =generateTransactionFree (idOfOrg, session);

            transaction.commit();
            transaction = null;
            return newClientContractId;
        } finally {
            //HibernateUtils.rollback(transaction, logger);
            //HibernateUtils.close(session, logger);
        }
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