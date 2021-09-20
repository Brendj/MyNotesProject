/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.org.Contract;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by i.semenov on 02.05.2017.
 */
public class DeliveredServicesElectronicCollationReportBuilder extends DeliveredServicesReportBuilder {

    public DeliveredServicesElectronicCollationReportBuilder() {
        super();
    }

    public DeliveredServicesElectronicCollationReportBuilder(String templateFilename) {
        super(templateFilename);
    }

    public boolean confirmMessage(Session session, Date startTime, Date endTime,
            Calendar calendar, Long orgId, Long contragent, Long contract, String region, Boolean otherRegions)
            throws Exception {

        boolean b = findNotConfirmedTaloons(session, startTime, endTime, contragent,
                contract);

        return b;
    }

    public DeliveredServicesItem.DeliveredServicesData findNotNullGoodsFullNameByOrg(Session session, Date start, Date end,
            Long contragent, Long contract, Map<String, Object> parameterMap, Boolean withoutFriendly) {
        String contragentCondition = "";
        if (contragent != null) {
            contragentCondition = "(cf_orgs.defaultsupplier=" + contragent + ") AND ";
        }

        String contractOrgsCondition = "";
        if (contract != null) {
            //  Вытаскиваем те оргии, которые привязаны к контракту и устанавливаем их как ограничения. !Будет заменено!
            Query query = session
                    .createSQLQuery("SELECT idoforg FROM cf_orgs WHERE idofcontract=:contract");//.createQuery(sql);
            query.setParameter("contract", contract);
            List res = query.list();
            for (Object entry : res) {
                Long org = ((BigInteger) entry).longValue();
                if (contractOrgsCondition.length() > 0) {
                    contractOrgsCondition = contractOrgsCondition.concat(", ");
                }
                contractOrgsCondition = contractOrgsCondition.concat("" + org);
            }

            //  Берем даты начала и окончания контракта, если они выходят за рамки выбранных пользователем дат, то
            //  ограничиваем временные рамки
            Criteria contractCriteria = session.createCriteria(Contract.class);
            contractCriteria.add(Restrictions.eq("globalId", contract));
            Contract c = (Contract) contractCriteria.uniqueResult();
            if (c.getDateOfConclusion().getTime() > start.getTime()) {
                start.setTime(c.getDateOfConclusion().getTime());
            }
            if (c.getDateOfClosing().getTime() < end.getTime()) {
                end.setTime(c.getDateOfClosing().getTime());
            }
        }
        if (contractOrgsCondition.length() > 0) {
            contractOrgsCondition = " cf_orgs.idoforg in (" + contractOrgsCondition + ") and ";
        }
        String orgCondition = "";
        if ((orgShortItemList != null) && (!orgShortItemList.isEmpty())) {
            String in_str = "";
            for (BasicReportJob.OrgShortItem orgShortItem : orgShortItemList) {
                if (withoutFriendly) {
                    in_str += orgShortItem.getIdOfOrg().toString() + ",";
                } else {
                    Org o = (Org) session.load(Org.class, orgShortItem.getIdOfOrg());
                    for (Org fo : o.getFriendlyOrg()) {
                        in_str += fo.getIdOfOrg().toString() + ",";
                    }
                }
            }
            if (in_str.length() > 0) {
                in_str = in_str.substring(0, in_str.length() - 1);
                orgCondition = String.format(" cf_orgs.idoforg in (%s) and ", in_str);
            }
        }

        String districtCondition = "";
        if ((region != null) && !region.isEmpty()) {
            //если выбран регион - надо анализировать флаг otherRegions
            if (otherRegions) {
                districtCondition = String
                        .format(" cf_orgs.idoforg in (select distinct friendlyorg from cf_friendly_organization f "
                                        + "join cf_orgs o on f.currentorg = o.idoforg where o.district = '%s') and ",
                                region);
            } else {
                districtCondition = String
                        .format(" cf_orgs.idoforg in (select distinct friendlyorg from cf_friendly_organization f "
                                        + "join cf_orgs o on f.currentorg = o.idoforg where o.district = '%s') and cf_orgs.district = '%s' and ",
                                region, region);
            }
        }

        //Дополнительная инфа из таблицы cf_taloon_approval
        String sqlTaloon = "SELECT cf_orgs.shortnameinfoservice, "
                + "CASE WHEN cft.goodsname IS NULL OR cft.goodsname = '' THEN split_part(cft.taloonname, '/', 1) ELSE split_part(cft.goodsname, '/', 1) END AS level1, "
                + "CASE WHEN cft.goodsname IS NULL OR cft.goodsname = '' THEN split_part(cft.taloonname, '/', 2) ELSE split_part(cft.goodsname, '/', 2) END AS level2, "
                + "CASE WHEN cft.goodsname IS NULL OR cft.goodsname = '' THEN split_part(cft.taloonname, '/', 3) "
                     + "WHEN split_part(cft.goodsname, '/', 3) = '' THEN cf_wt_agegroup_items.description ELSE split_part(cft.goodsname, '/', 3) END AS level3, "
                + "CASE WHEN cft.goodsname IS NULL OR cft.goodsname = '' THEN split_part(cft.taloonname, '/', 4) "
                     + "WHEN split_part(cft.goodsname, '/', 4) = '' THEN cf_wt_diet_type.description ELSE split_part(cft.goodsname, '/', 4) END AS level4, "
                + "sum(cft.soldedqty) AS cnt, "
                + "cft.price AS price, sum(cft.soldedqty) * cft.price AS sum, cf_orgs.shortaddress, "
                + "substring(cf_orgs.officialname FROM '[^[:alnum:]]* {0,1}№ {0,1}([0-9]*)'), "
                + "cf_orgs.idoforg, CASE WHEN cft.taloonname ILIKE '%вода%' THEN 1 ELSE 0 END AS orderType, cft.taloondate "
                + "FROM cf_taloon_approval cft JOIN cf_orgs ON cft.idoforg = cf_orgs.idoforg "
                + "left join cf_wt_complexes on cf_wt_complexes.idofcomplex = cft.complexid "
                + "left join cf_wt_agegroup_items on cf_wt_agegroup_items.idofagegroupitem = cf_wt_complexes.idofagegroupitem "
                + "left join cf_wt_diet_type on cf_wt_diet_type.idofdiettype = cf_wt_complexes.idofdiettype "
                + "WHERE cft.deletedstate = FALSE AND "
                + contragentCondition + contractOrgsCondition + orgCondition + districtCondition
                + "cft.taloondate BETWEEN :start AND :end"
                + " GROUP BY cf_orgs.idoforg, cf_orgs.officialname, shortaddress, cft.soldedqty, level1, level2, level3, level4, cft.price, shortaddress, cft.taloonname, cft.taloondate";
        Query queryTaloon = session.createSQLQuery(sqlTaloon);
        queryTaloon.setParameter("start", start.getTime());
        queryTaloon.setParameter("end", end.getTime());

        List res = queryTaloon.list();
        DeliveredServicesItem.DeliveredServicesData result = getResult(res, parameterMap);
        return result;
    }

    //Для вывода предупреждения
    public  boolean findNotConfirmedTaloons(Session session, Date start, Date end,
            Long contragent, Long contract) {
        boolean b = false;

        String contragentCondition = "";
        if (contragent != null) {
            contragentCondition = "(cf_orgs.defaultsupplier=" + contragent + ") AND ";
        }

        String contractOrgsCondition = "";
        if (contract != null) {
            //  Вытаскиваем те оргии, которые привязаны к контракту и устанавливаем их как ограничения. !Будет заменено!
            Query query = session
                    .createSQLQuery("SELECT idoforg FROM cf_orgs WHERE idofcontract=:contract");//.createQuery(sql);
            query.setParameter("contract", contract);
            List res = query.list();
            for (Object entry : res) {
                Long org = ((BigInteger) entry).longValue();
                if (contractOrgsCondition.length() > 0) {
                    contractOrgsCondition = contractOrgsCondition.concat(", ");
                }
                contractOrgsCondition = contractOrgsCondition.concat("" + org);
            }

            //  Берем даты начала и окончания контракта, если они выходят за рамки выбранных пользователем дат, то
            //  ограничиваем временные рамки
            Criteria contractCriteria = session.createCriteria(Contract.class);
            contractCriteria.add(Restrictions.eq("globalId", contract));
            Contract c = (Contract) contractCriteria.uniqueResult();
            if (c.getDateOfConclusion().getTime() > start.getTime()) {
                start.setTime(c.getDateOfConclusion().getTime());
            }
            if (c.getDateOfClosing().getTime() < end.getTime()) {
                end.setTime(c.getDateOfClosing().getTime());
            }
        }
        if (contractOrgsCondition.length() > 0) {
            contractOrgsCondition = " cf_orgs.idoforg in (" + contractOrgsCondition + ") and ";
        }
        String orgCondition = "";
        String in_str = "";
        if (orgShortItemList != null) {
            if(!orgShortItemList.isEmpty()) {
                for (BasicReportJob.OrgShortItem orgShortItem : orgShortItemList) {
                    Org o = (Org) session.load(Org.class, orgShortItem.getIdOfOrg());

                    for (Org fo : o.getFriendlyOrg()) {
                        in_str += fo.getIdOfOrg().toString() + ",";
                    }
                }
                if (in_str.length() > 0) {
                    in_str = in_str.substring(0, in_str.length() - 1);
                    orgCondition = String.format(" cf_orgs.idoforg in (%s) and ", in_str);
                }
            }
        }

        String districtCondition = "";
        if ((region != null) && !region.isEmpty()) {
            //если выбран регион - надо анализировать флаг otherRegions
            if (otherRegions) {
                districtCondition = String
                        .format(" cf_orgs.idoforg in (select distinct friendlyorg from cf_friendly_organization f "
                                        + "join cf_orgs o on f.currentorg = o.idoforg where o.district = '%s') and ",
                                region);
            } else {
                districtCondition = String
                        .format(" cf_orgs.idoforg in (select distinct friendlyorg from cf_friendly_organization f "
                                        + "join cf_orgs o on f.currentorg = o.idoforg where o.district = '%s') and cf_orgs.district = '%s' and ",
                                region, region);
            }
        }

        String sqlTaloonError = "SELECT cft.ispp_state, cft.pp_state "
                + "FROM cf_taloon_approval cft JOIN cf_orgs ON cft.idoforg = cf_orgs.idoforg WHERE cft.deletedstate = FALSE  AND "
                + "(cft.ispp_state in (0) OR cft.pp_state in (0,2)) AND "
                + contragentCondition + contractOrgsCondition + orgCondition + districtCondition
                + "cft.taloondate BETWEEN :start AND :end";
        Query queryTaloonError = session.createSQLQuery(sqlTaloonError);
        queryTaloonError.setParameter("start", start.getTime());
        queryTaloonError.setParameter("end", end.getTime());

        List resTaloonError = queryTaloonError.list();

        if (!resTaloonError.isEmpty()) {
            b = true;
        }

        return b;
    }

}
