/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;

import org.hibernate.Session;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 11.02.12
 * Time: 16:19
 * To change this template use File | Settings | File Templates.
 */

/**
 * Класс отчета по льготам ОУ
 */
public class AllOrgsDiscountsReport extends BasicReport {

    final static String staticColumnNames[] = {"Наименование ОУ", "Всего льготных"};

    private static List<OrgDiscounts> itemsList;
    private List<String> columnNames;

    public Object[] getColumnNames() {
        return columnNames.toArray();
    }

    public List<OrgDiscounts> getItemsList() {
        return itemsList;
    }

    public AllOrgsDiscountsReport(Date generateTime, long generateDuration, List<OrgDiscounts> itemsList, List<String> dynamicColumnNames) {
        super(generateTime, generateDuration);
        this.itemsList = itemsList;
        columnNames = new ArrayList<String>();
        for (String name : staticColumnNames) {
            columnNames.add(name);
        }
        columnNames.addAll(dynamicColumnNames);
    }

    public AllOrgsDiscountsReport() {
        super();
        this.itemsList = Collections.emptyList();
        columnNames = new ArrayList<String>();
        for (String name : staticColumnNames) {
            columnNames.add(name);
        }
    }

    public static class Builder {
        public AllOrgsDiscountsReport build(Session session) throws Exception{

            Date generateTime = new Date();

            // запрос id, name льгот
            String queryCategoryIds = "select cd.idOfCategoryDiscount, cd.categoryName "
                    + " from CategoryDiscount cd "
                    + " where cd.idOfCategoryDiscount >= 0 "
                    + " order by idOfCategoryDiscount";
            List categoryDiscountList = session.createQuery(queryCategoryIds).list();

            // лист наименований категорий льгот
            List<String> dynamicColumnNames = new ArrayList<String>(categoryDiscountList.size());

            // мап для получения индекса категории в результирующем листе по id
            Map<Long, Integer> categoryIndexMap = new HashMap<Long, Integer>(categoryDiscountList.size());

            int i = 0;
            for (Object cc : categoryDiscountList) {
                Object objs[] = (Object[])cc;
                categoryIndexMap.put((Long)objs[0], i++);
                dynamicColumnNames.add((String)objs[1]);
            }

            // запрос на количество льготных по ОУ
            String queryFullDiscount = "select cl.org.id, count(distinct cl.idOfClient), cl.org.shortName "
                    + " from Client cl , ClientsCategoryDiscount cc "
                    + " where cc.idOfClient=cl.idOfClient "
                    + " and cc.idOfCategoryDiscount >= 0 "
                    + " group by cl.org.id, cl.org.shortName "
                    + " order by cl.org.id ";

            List resultFullDiscountsList = session.createQuery(queryFullDiscount).list();

            Map<Long, OrgDiscounts> result = new HashMap<Long, OrgDiscounts>(resultFullDiscountsList.size());

            // создаем бины строк таблицы отчета
            for (Object obj : resultFullDiscountsList) {
                Object orgDiscounts[] = (Object[])obj;
                Long orgId = (Long)orgDiscounts[0];
                // наименование, общее количество льготных, количество полей
                OrgDiscounts item = new OrgDiscounts(orgDiscounts[2], orgDiscounts[1], categoryDiscountList.size() + 2);
                result.put(orgId, item);
            }

            // запрос количества льготников по категориям
            String q = "select cl.org.id, cc.idOfCategoryDiscount, count (cl.idOfClient) "
                    + " from Client cl, ClientsCategoryDiscount cc "
                    + " where cc.idOfClient=cl.idOfClient "
                    + " and cc.idOfCategoryDiscount >= 0 "
                    + " group by cl.org.id, cc.idOfCategoryDiscount "
                    + " order by cl.org.id, cc.idOfCategoryDiscount";

            List resultDiscountsList = session.createQuery(q).list(); // лист результата запроса

            for (Object obj : resultDiscountsList) {
                Object discount[] = (Object [])obj;
                OrgDiscounts item = result.get((Long)discount[0]);
                if (item == null) {
                    throw new Exception("Ошибка при формирования отчета по льготам: в таблице ClientsCategoryDiscount обнаружен клиент, которого нет в таблице Clients.");
                } else {
                    item.setValue(categoryIndexMap.get((Long)discount[1]).intValue()+2, (Long)discount[2]);
                }
            }

            List<OrgDiscounts> itemsList = new ArrayList<OrgDiscounts>(result.size());
            itemsList.addAll(result.values());

            return new AllOrgsDiscountsReport(generateTime, new Date().getTime() - generateTime.getTime(), itemsList, dynamicColumnNames);
        }
    }

    // класс для записи таблицы отчета по льготам (все организации)
    public static class OrgDiscounts {

        // значения: название ОУ, общее количество льготных, количество льготных по категориям
        private Object value[];

        public OrgDiscounts(Object name, Object fullCount, int size) throws Exception {
            value = new Object[size];
            if (size > 2) {
                value[0] = name;
                value[1] = fullCount;
                for (int i = 2; i < size; i++) {
                    value[i] = 0L;
                }
            } else {
                throw new Exception("Ошибка при инициализации объекта OrgDiscounts: size должен быть больше 2.");
            }
        }

        public List<Object> getValues() {
            List<Object> result = new ArrayList<Object>(value.length);
            for (Object o : value) {
                result.add(o);
            }
            return result;
        }

        public void setValue(int ind, Long count) {
            value[ind] = count;
        }
    }
}
