/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.regularPaymentsReport;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 11.11.13
 * Time: 16:11
 */

public class RPRDataLoader {

    private Session session;

    public RPRDataLoader(Session session) {
        this.session = session;
    }

    @SuppressWarnings("unchecked")
    public List<ReportItem> getReportData(Date startDate, Date endDate) {
        //Transaction tr = session.beginTransaction();
        List<ReportItem> items = new ArrayList<ReportItem>();
        Query query = session.createQuery(
                "select rp.idOfPayment, rp.paymentAmount, rp.paymentDate, rp.clientBalance, rp.success, rp.rrn, " +
                        "o.shortName, cl.contractId, cl.person.firstName, cl.person.secondName, cl.person.surname, " +
                        "rp.status, mr.errorDescription \n" +
                        "from RegularPayment rp join rp.mfrRequest mr join rp.client cl join cl.org o \n" +
                        "where rp.paymentDate between :startDate and :endDate \n" +
                        "order by rp.paymentDate, o.shortName, cl.contractId, cl.person.surname, cl.person.firstName")
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate);
        List<Object[]> res = query.list();
        for (Object[] record : res) {
            ReportItem item = new ReportItem();
            item.setIdOfPayment((Long) record[0]);
            item.setPaymentSum((Long) record[1]);
            item.setPaymentDate((Date) record[2]);
            item.setClientBalance((Long) record[3]);
            item.setSuccess((Boolean) record[4] ? "Да" : "Нет");
            item.setRrn((Long) record[5]);
            item.setOrgName((String) record[6]);
            item.setContractId((Long) record[7]);
            item.setName((String) record[8]);
            item.setSecondName((String) record[9]);
            item.setSurname((String) record[10]);
            item.setStatus((String) record[11]);
            item.setErrorMessage((String) record[12]);
            items.add(item);
        }
        //tr.commit();
        return items;
    }
}
