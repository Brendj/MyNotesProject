/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import com.google.visualization.datasource.base.TypeMismatchException;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableRow;
import com.google.visualization.datasource.datatable.value.Value;
import com.google.visualization.datasource.datatable.value.ValueType;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;

import org.hibernate.Session;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 09.11.12
 * Time: 18:57
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("singleton")
public class ProjectStateReportService {

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    private static final int ACTIVE_CHART_DATA = 100;
    private static final int ACTIVE_CHART_1_DATA = 101;
    private static final int ACTIVE_CHART_2_DATA = 102;
    private static final int ACTIVE_CHART_3_DATA = 103;
    private static final int ACTIVE_CHART_4_DATA = 104;
    private static final int ACTIVE_CHART_5_DATA = 105;
    private static final int ACTIVE_CHART_6_DATA = 106;
    private static final int UNIQUE_CHART_DATA = 200;
    private static final int UNIQUE_CHART_1_DATA = 201;
    private static final int UNIQUE_CHART_2_DATA = 202;
    private static final int UNIQUE_CHART_3_DATA = 203;
    private static final int UNIQUE_CHART_4_DATA = 204;
    private static final int CONTENTS_CHART_DATA = 300;
    private static final int REFILL_CHART_DATA = 400;
    private static final int REFILL_CHART_01_DATA = 401;
    private static final int REFILL_CHART_02_DATA = 402;
    private static final int INFORMING_CHART_DATA = 500;
    private static final int BENEFIT_PART_CHART_DATA = 600;
    private static final int BENEFITS_CHART_DATA = 700;
    private static final int VISITORS_CHART_DATA = 800;
    private static final int VISITORS_CHART_1_DATA = 801;
    private static final int VISITORS_CHART_2_DATA = 802;
    private static final int RATING_CHART_DATA = 900;
    private static final int RATING_CHART_1_DATA = 901;
    private static final int RATING_CHART_2_DATA = 902;
    private static final int RATING_CHART_3_DATA = 903;
    private static final int RATING_CHART_4_DATA = 904;
    private static final int RATING_CHART_5_DATA = 905;
    private static final int REFILL_PROGRESS_CHART = 1000;
    private static final int REFILL_PROGRESS_0_CHART = 1001;
    private static final int REFILL_PROGRESS_1_CHART = 1002;
    private static final int REFILL_PROGRESS_2_CHART = 1003;
    private static final int FISCAL_CHART_DATA = 2000;
    private static final int FISCAL_CHART_1_DATA = 2001;
    private static final int FISCAL_CHART_2_DATA = 2002;
    private static final int FISCAL_CHART_3_DATA = 2003;
    private static final int FISCAL_CHART_4_DATA = 2004;
    private static final int CONTRAGENTS_CHART_DATA = 3000;
    private static final int CARDS_CHART_DATA = 4000;
    private static final int CARDS_CHART_1_DATA = 4001;
    private static final int CARDS_CHART_2_DATA = 4002;
    private static final int CARDS_CHART_3_DATA = 4003;
    private static final int DISCOUNT_FLOWCHARTS_DATE_INCREMENT = -259200000; // 3 дня


    //  Для возможности отбора данных по регионам, достаточно указать два макро-заменителя
    //  в соответствующих местах - %REGION_SENSITIVE_JOIN% перед where и REGION_SENSITIVE_CLAUSE
    //  в любой части where. Для определения какие регионы участвуют и как они воздействуют на
    //  id типа данных, используется карта REGIONS_LIST (указанное значение будет прибавляться к
    //  изначальному id типа, например, в карте указан ЮВАО со значением 50, и обработка идет
    //  данных для ACTIVE_CHART_1_DATA с id типа 101, тогда 101 + 50 будет давать нужный id типа
    //  для ЮВАО); если нужны все регионы, необходимо указывать 0; следите за правильностью
    //  сбора id типа, в собранном виде значение не должно превышать исходное на 100, иначе
    //  будет нарушение id типа, например 101 + 100 - нельзя, т.к. в 201 может находиться
    //  другой тип
    //  Так же для каждого типа необходимо
    public static final String REGION_SENSITIVE_JOIN = "%REGION_SENSITIVE_JOIN%";
    public static final String REGION_SENSITIVE_CLAUSE = "%REGION_SENSITIVE_CLAUSE%";
    public Map<String, Integer> REGIONS_LIST;

    //  Используется для выполнения запросов для всех платежных агентов. Обязательно должно
    //  использоваться совместно с ComplexType (а не SimpleType). В перечислении столбцов
    //  так же должен быть указан только один столбез значений с именем PAY_AGENTS_COLUMNS.
    //  Для каждого агента, ID типа будет увеличиваться на 1 от изначального
    public static final String PAY_AGENTS_COLUMNS = "%PAY_AGENT_COLUMNS%";
    public static final String PAY_AGENTS_CLAUSE = "%PAY_AGENT_ID%";
    public static final int PAY_AGENT_MULTI_ID = 10000;
    public List<Object[]> PAY_AGENTS_LIST;
    public static String PERIODIC_AVG_COL = "%PERIODIC_AVG_COL%";
    public static String PERIODIC_AVG_GROUP = "%PERIODIC_AVG_GROUP%";


    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM");
    private static final DateFormat DB_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ProjectStateReportService.class);
    public static final Map<String, Type> TYPES;

    static {
        TYPES = new HashMap<String, Type>();
        TYPES.put("ActiveChart", new ComplexType(new Type[]{
                new SimpleType("select '' || EXTRACT(EPOCH FROM d) * 1000, count(v) " +
                        "from (select distinct regOrgSrc.idoforg as v, date_trunc('day', to_timestamp(regOrgSrc.evtdatetime / 1000)) as d "
                        +
                        "from cf_enterevents as regOrgSrc " +
                        REGION_SENSITIVE_JOIN + " "+
                        "where regOrgSrc.evtdatetime >= EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MINIMUM_DATE%') * 1000 AND "
                        +
                        "      regOrgSrc.evtdatetime < EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MAXIMUM_DATE%') * 1000 " +
                        REGION_SENSITIVE_CLAUSE + " " +
                        "union " +
                        "select distinct regOrgSrc.idoforg as v, date_trunc('day', to_timestamp(regOrgSrc.createddate / 1000)) as d "
                        +
                        "from cf_orders as regOrgSrc " +
                        REGION_SENSITIVE_JOIN + " "+
                        "where regOrgSrc.createddate >= EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MINIMUM_DATE%') * 1000 AND "
                        +
                        "      regOrgSrc.createddate < EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MAXIMUM_DATE%') * 1000 " +
                        REGION_SENSITIVE_CLAUSE + ") as oo "+
                        "group by d " +
                        "order by 1", ACTIVE_CHART_1_DATA).setIncremental(true),

                new SimpleType("select '' || EXTRACT(EPOCH FROM d) * 1000, count(v) " +
                        "from (select distinct regOrgSrc.idoforg as v, date_trunc('day', to_timestamp(regOrgSrc.evtdatetime / 1000)) as d "
                        +
                        "from cf_enterevents as regOrgSrc " +
                        REGION_SENSITIVE_JOIN + " "+
                        "where regOrgSrc.evtdatetime >= EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MINIMUM_DATE%') * 1000 AND "
                        +
                        "      regOrgSrc.evtdatetime < EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MAXIMUM_DATE%') * 1000 " +
                        REGION_SENSITIVE_CLAUSE + ") as oo "+
                        "group by d " +
                        "order by 1", ACTIVE_CHART_2_DATA).setIncremental(true),

                new SimpleType("select '' || EXTRACT(EPOCH FROM d) * 1000, count(v) " +
                        "from (select distinct regOrgSrc.idoforg as v, date_trunc('day', to_timestamp(regOrgSrc.createddate / 1000)) as d "
                        +
                        "from cf_orders as regOrgSrc " +
                        REGION_SENSITIVE_JOIN + " "+
                        "where regOrgSrc.createddate >= EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MINIMUM_DATE%') * 1000 AND "
                        +
                        "      regOrgSrc.createddate < EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MAXIMUM_DATE%') * 1000 AND "
                        +
                        "      regOrgSrc.socdiscount=0 " + REGION_SENSITIVE_CLAUSE + ") as oo " +
                        "group by d " +
                        "order by 1", ACTIVE_CHART_3_DATA).setIncremental(true),

                new SimpleType("select '' || EXTRACT(EPOCH FROM d) * 1000, count(v) " +
                        "from (select distinct regOrgSrc.idoforg as v, date_trunc('day', to_timestamp(regOrgSrc.createddate / 1000)) as d "
                        +
                        "from cf_orders as regOrgSrc " +
                        REGION_SENSITIVE_JOIN + " "+
                        "where %DATE_CLAUSE% "
                        +
                        "                                     AND "
                        +
                        "      regOrgSrc.socdiscount<>0 " + REGION_SENSITIVE_CLAUSE + ") as oo " +
                        "group by d " +
                        "order by 1", ACTIVE_CHART_4_DATA).setIncremental(true),

                new SimpleType("select '' || EXTRACT(EPOCH FROM d) * 1000, count(v) " +
                        "from ("
                        + "select distinct regOrgSrc.idoforg as v, date_trunc('day', to_timestamp(gr.dateofgoodsrequest / 1000)) as d "
                        + "from cf_goods_requests gr "
                        + "join cf_orgs regOrgSrc on regOrgSrc.idoforg=gr.orgowner "
                        + "join cf_goods_requests_positions grp on gr.idofgoodsrequest=grp.idofgoodsrequest "
                        + "join cf_goods g on g.idofgood=grp.idofgood "
                        + "join cf_goods_groups gg on g.idofgoodsgroup=gg.idofgoodsgroup "
                        + REGION_SENSITIVE_JOIN + " "
                        + "where lower(gg.nameofgoodsgroup) like '%льготн%' and "
                        + "      gr.dateofgoodsrequest>=EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MINIMUM_DATE%') * 1000 AND "
                        + "      gr.dateofgoodsrequest<EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MAXIMUM_DATE%') * 1000 "
                        + REGION_SENSITIVE_CLAUSE
                        + ") as oo " +
                        "group by d " +
                        "order by 1", ACTIVE_CHART_5_DATA).setIncremental(true),

                new SimpleType("select '' || EXTRACT(EPOCH FROM d) * 1000, count(v) " +
                        "from ("
                        + "select distinct regOrgSrc.idoforg as v, date_trunc('day', to_timestamp(gr.dateofgoodsrequest / 1000)) as d "
                        + "from cf_goods_requests gr "
                        + "join cf_orgs regOrgSrc on regOrgSrc.idoforg=gr.orgowner "
                        + "join cf_goods_requests_positions grp on gr.idofgoodsrequest=grp.idofgoodsrequest "
                        + "join cf_goods g on g.idofgood=grp.idofgood "
                        + "join cf_goods_groups gg on g.idofgoodsgroup=gg.idofgoodsgroup "
                        + REGION_SENSITIVE_JOIN + " "
                        + "where lower(gg.nameofgoodsgroup) not like '%льготн%' and "
                        + "      gr.dateofgoodsrequest>=EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MINIMUM_DATE%') * 1000 AND "
                        + "      gr.dateofgoodsrequest<EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MAXIMUM_DATE%') * 1000 "
                        + REGION_SENSITIVE_CLAUSE
                        + ") as oo " +
                        "group by d " +
                        "order by 1", ACTIVE_CHART_6_DATA).setIncremental(true),


                }, new Object[][]{
                {ValueType.DATE, "Год"},
                {ValueType.NUMBER, "Общее количество ОУ в проекте", ACTIVE_CHART_1_DATA},
                {ValueType.NUMBER, "ОУ, оказывающие услугу ПРОХОД", ACTIVE_CHART_2_DATA},
                {ValueType.NUMBER, "ОУ, оказывающие услугу Платного питания по безналичному расчету", ACTIVE_CHART_3_DATA},
                {ValueType.NUMBER, "ОУ, отражающие в системе услугу Льготного питания", ACTIVE_CHART_4_DATA},
                {ValueType.NUMBER, "ОУ, осуществляющих заказ льготного питания через ИС ПП", ACTIVE_CHART_5_DATA},
                {ValueType.NUMBER, "ОУ, осуществляющих заказ платного питания через ИС ПП", ACTIVE_CHART_6_DATA}}, ACTIVE_CHART_DATA));

        TYPES.put("UniqueChart", new ComplexType(new Type[]{
                new SimpleType("select '' || EXTRACT(EPOCH FROM d) * 1000, count(v) " +
                        "from (select distinct regOrgSrc.idofclient as v, date_trunc('day', to_timestamp(regOrgSrc.evtdatetime / 1000)) as d "
                        +
                        "from cf_enterevents as regOrgSrc " +
                        REGION_SENSITIVE_JOIN + " "+
                        "where regOrgSrc.evtdatetime >= EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MINIMUM_DATE%') * 1000 AND "
                        +
                        "      regOrgSrc.evtdatetime < EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MAXIMUM_DATE%') * 1000 "
                        + REGION_SENSITIVE_CLAUSE + " "+
                        "union " +
                        "select distinct regOrgSrc.idofclient as v, date_trunc('day', to_timestamp(regOrgSrc.createddate / 1000)) as d "
                        +
                        "from cf_orders as regOrgSrc " +
                        REGION_SENSITIVE_JOIN + " "+
                        "where regOrgSrc.createddate >= EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MINIMUM_DATE%') * 1000 AND "
                        +
                        "      regOrgSrc.createddate < EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MAXIMUM_DATE%') * 1000 " + REGION_SENSITIVE_CLAUSE + ") as oo "
                        +
                        "group by d " +
                        "order by 1", UNIQUE_CHART_1_DATA).setIncremental(true),

                new SimpleType("select '' || EXTRACT(EPOCH FROM d) * 1000, count(v) " +
                        "from (select distinct regOrgSrc.idofclient as v, date_trunc('day', to_timestamp(regOrgSrc.evtdatetime / 1000)) as d "
                        +
                        "from cf_enterevents as regOrgSrc " +
                        REGION_SENSITIVE_JOIN + " "+
                        "where regOrgSrc.evtdatetime >= EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MINIMUM_DATE%') * 1000 AND "
                        +
                        "      regOrgSrc.evtdatetime < EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MAXIMUM_DATE%') * 1000 " +
                        REGION_SENSITIVE_CLAUSE + ") as oo " +
                        "group by d " +
                        "order by 1", UNIQUE_CHART_2_DATA).setIncremental(true),

                new SimpleType("select '' || EXTRACT(EPOCH FROM d) * 1000, count(v) " +
                        "from (select distinct regOrgSrc.idofclient as v, date_trunc('day', to_timestamp(regOrgSrc.createddate / 1000)) as d "
                        +
                        "from cf_orders as regOrgSrc " +
                        REGION_SENSITIVE_JOIN + " "+
                        "where regOrgSrc.createddate >= EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MINIMUM_DATE%') * 1000 AND "
                        +
                        "      regOrgSrc.createddate < EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MAXIMUM_DATE%') * 1000 AND "
                        +
                        "      regOrgSrc.socdiscount=0 " + REGION_SENSITIVE_CLAUSE + ") as oo " +
                        "group by d " +
                        "order by 1", UNIQUE_CHART_3_DATA).setIncremental(true),

                new SimpleType("select '' || EXTRACT(EPOCH FROM d) * 1000, count(v) " +
                        "from (select distinct regOrgSrc.idofclient as v, date_trunc('day', to_timestamp(regOrgSrc.createddate / 1000)) as d "
                        +
                        "from cf_orders as regOrgSrc " +
                        REGION_SENSITIVE_JOIN + " "+
                        "where %DATE_CLAUSE% AND "
                        +
                        "      regOrgSrc.socdiscount<>0 " + REGION_SENSITIVE_CLAUSE + ") as oo " +
                        "group by d " +
                        "order by 1", UNIQUE_CHART_4_DATA).setIncremental(true)}, new Object[][]{
                {ValueType.DATE, "Год"}, {ValueType.NUMBER, "Число уникальных пользователей в день", UNIQUE_CHART_1_DATA},
                {ValueType.NUMBER, "Число уникальных пользователей услуги ПРОХОД", UNIQUE_CHART_2_DATA},
                {ValueType.NUMBER, "Число уникальных пользователей, получивших платное питание", UNIQUE_CHART_3_DATA},
                {ValueType.NUMBER, "Число уникальных пользователей, получивших льготное питание", UNIQUE_CHART_4_DATA}}, UNIQUE_CHART_DATA));
        TYPES.put("ContentsChart",
                new SimpleType("select cf_orderdetails.menugroup as g, count(cf_orderdetails.idoforder) as c " +
                        "from cf_orders as regOrgSrc " +
                        "left join cf_orderdetails on regOrgSrc.idoforg=cf_orderdetails.idoforg and regOrgSrc.idoforder=cf_orderdetails.idoforder "
                        +
                        "where regOrgSrc.createddate >= EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MINIMUM_DATE%') * 1000 AND "
                        +
                        "      regOrgSrc.createddate < EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MAXIMUM_DATE%') * 1000 AND "
                        +
                        "      cf_orderdetails.menugroup<>'' " +
                        "group by cf_orderdetails.menugroup", new Object[][]{
                        {ValueType.TEXT, "Группа меню"}, {ValueType.NUMBER, "Покупок", CONTENTS_CHART_DATA}}, CONTENTS_CHART_DATA)
                        .setPostReportMethod("parseContentsChart"));
        TYPES.put("RefillChart",
                new SimpleType("select cf_contragents.contragentname, count(cf_clientpayments.idofclientpayment) " +
                        "from cf_contragents " +
                        "left join cf_clientpayments on cf_contragents.idofcontragent=cf_clientpayments.idofcontragent "+
                        "left join cf_transactions on cf_clientpayments.idoftransaction=cf_transactions.idoftransaction "+
                        "left join cf_clients as regOrgSrc on cf_transactions.idofclient=regOrgSrc.idofclient "+
                        REGION_SENSITIVE_JOIN + " "+
                        "where cf_clientpayments.paysum<>0 " +REGION_SENSITIVE_CLAUSE + " " +
                        "group by cf_contragents.contragentname " +
                        "order by 1", new Object[][]{
                        {ValueType.TEXT, "Способ пополнения"}, {ValueType.NUMBER, "Количество пополнений", REFILL_CHART_DATA}},
                        REFILL_CHART_DATA).setPostReportMethod("parseRefillChart"));
        TYPES.put("RefillAvgChart", new ComplexType(new Type[]{
                new SimpleType("select 'Средняя сумма пополнения' as title, avg(cf_clientpayments.paysum) / 100 " +
                        "from cf_contragents " +
                        "left join cf_clientpayments on cf_contragents.idofcontragent=cf_clientpayments.idofcontragent "+
                        "left join cf_transactions on cf_clientpayments.idoftransaction=cf_transactions.idoftransaction "+
                        "left join cf_clients as regOrgSrc on cf_transactions.idofclient=regOrgSrc.idofclient "+
                        REGION_SENSITIVE_JOIN + " "+
                        "where cf_clientpayments.idofcontragent=" + PAY_AGENTS_CLAUSE + " and cf_clientpayments.paysum<>0 " +REGION_SENSITIVE_CLAUSE + " " +
                        "group by cf_contragents.contragentname " +
                        "order by 1", REFILL_CHART_02_DATA).setValueType(Double.class), },
                new Object[][]{
                        {ValueType.TEXT, "Способ пополнения"}, {ValueType.NUMBER, PAY_AGENTS_COLUMNS, REFILL_CHART_02_DATA}}, REFILL_CHART_02_DATA));
        TYPES.put("RefillProgressChart", new ComplexType(new Type[]{
                new SimpleType("select bycontr.dat, bycontr.cnt / byall.cnt * 100 "
                        + "from (select '' || EXTRACT(EPOCH FROM ooo.dat) * 1000 as dat, sum(ooo.cnt) as cnt "
                        + "from ( "
                        + "select date_trunc('day', to_timestamp(cf_clientpayments.createddate / 1000)) as dat, count(cf_clientpayments.idofclientpayment) as cnt "
                        + "from cf_clientpayments "
                        + "left join cf_transactions on cf_clientpayments.idoftransaction=cf_transactions.idoftransaction "
                        + "left join cf_clients as regOrgSrc on cf_transactions.idofclient=regOrgSrc.idofclient "
                        + REGION_SENSITIVE_JOIN + " "
                        + "where cf_clientpayments.idofcontragent=" + PAY_AGENTS_CLAUSE + " and "
                        + "      cf_clientpayments.createddate >= EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MINIMUM_DATE%') * 1000 AND "
                        + "      cf_clientpayments.createddate < EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MAXIMUM_DATE%') * 1000 "
                        + REGION_SENSITIVE_CLAUSE + " "
                        + "group by cf_clientpayments.createddate) as ooo "
                        + "group by ooo.dat) as bycontr, "
                        + "(select '' || EXTRACT(EPOCH FROM kkk.dat) * 1000 as dat, sum(kkk.cnt) as cnt "
                        + "from ("
                        + "select date_trunc('day', to_timestamp(cf_clientpayments.createddate / 1000)) as dat, count(cf_clientpayments.idofclientpayment) as cnt "
                        + "from cf_clientpayments "
                        + "left join cf_transactions on cf_clientpayments.idoftransaction=cf_transactions.idoftransaction "
                        + "left join cf_clients as regOrgSrc on cf_transactions.idofclient=regOrgSrc.idofclient "
                        + REGION_SENSITIVE_JOIN + " "
                        + "where cf_clientpayments.createddate >= EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MINIMUM_DATE%') * 1000 AND "
                        + "      cf_clientpayments.createddate < EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MAXIMUM_DATE%') * 1000 "
                        + REGION_SENSITIVE_CLAUSE + " "
                        + "group by cf_clientpayments.createddate) as kkk "
                        + "group by kkk.dat) as byall "
                        + "where bycontr.dat = byall.dat "
                        + "order by bycontr.dat", REFILL_PROGRESS_0_CHART).setIncremental(true).setValueType (Double.class)},
                new Object[][]{
                        {ValueType.DATE, "Дата"}, {ValueType.NUMBER, PAY_AGENTS_COLUMNS, REFILL_PROGRESS_0_CHART}},
                REFILL_PROGRESS_0_CHART));
        TYPES.put("InformingChart",
                new SimpleType("select 'Не предоставлены данные для информирования', count(regOrgSrc.idofclient) " +
                        "from cf_clients as regOrgSrc " +
                        "left join cf_cards on regOrgSrc.idofclient=cf_cards.idofclient " +
                        REGION_SENSITIVE_JOIN + " "+
                        "where regOrgSrc.email='' and regOrgSrc.mobile='' and cf_cards.state=0 " +
                        REGION_SENSITIVE_CLAUSE + " " +

                        "union " +
                        "select 'В систему внесен электронный почтовый адрес', count(regOrgSrc.idofclient) " +
                        "from cf_clients as regOrgSrc " +
                        "left join cf_cards on regOrgSrc.idofclient=cf_cards.idofclient " +
                        REGION_SENSITIVE_JOIN + " "+
                        "where regOrgSrc.email<>'' and cf_cards.state=0 " +
                        REGION_SENSITIVE_CLAUSE + " " +

                        "union " +

                        "select 'В систему внесен номер мобильного телефона', count(regOrgSrc.idofclient) " +
                        "from cf_clients as regOrgSrc " +
                        "left join cf_cards on regOrgSrc.idofclient=cf_cards.idofclient " +
                        REGION_SENSITIVE_JOIN + " "+
                        "where regOrgSrc.mobile<>'' and cf_cards.state=0 " + REGION_SENSITIVE_CLAUSE, new Object[][]{
                        {ValueType.TEXT, "Способ информирования"}, {ValueType.NUMBER, "Количество клиентов", INFORMING_CHART_DATA}},
                        INFORMING_CHART_DATA));
        new SimpleType("select 'Льготные категории 1-4 класс', count(cf_clients.idofclient) "
                + "from cf_clients "
                + "left join cf_cards on cf_clients.idOfClient=cf_cards.idOfClient "
                + "left join cf_clientgroups on cf_clients.idofclientgroup=cf_clientgroups.idofclientgroup and cf_clients.idoforg=cf_clientgroups.idoforg "
                + "where cf_clients.discountmode<>0 and cf_cards.state=0 AND CAST(substring(groupname FROM '[0-9]+') AS INTEGER)<>0 and CAST(substring(groupname FROM '[0-9]+') AS INTEGER)<=4 "

                + "union "

                + "select 'Прочие льготные категории', count(cf_clients.idofclient) "
                + "from cf_clients "
                + "left join cf_cards on cf_clients.idOfClient=cf_cards.idOfClient "
                + "left join cf_clientgroups on cf_clients.idofclientgroup=cf_clientgroups.idofclientgroup and cf_clients.idoforg=cf_clientgroups.idoforg "
                + "where cf_clients.discountmode<>0 and cf_cards.state=0 AND CAST(substring(groupname FROM '[0-9]+') AS INTEGER)<>0 AND CAST(substring(groupname FROM '[0-9]+') AS INTEGER)>4 "

                + "union "

                + "select 'Не имеющие льгот', count(distinct cf_clients.idofclient) "
                + "from cf_clients "
                + "left join cf_cards on cf_clients.idOfClient=cf_cards.idOfClient "
                + "left join cf_clientgroups on cf_clients.idofclientgroup=cf_clientgroups.idofclientgroup and cf_clients.idoforg=cf_clientgroups.idoforg "
                + "where cf_clients.discountmode=0 and cf_cards.state=0 AND CAST(substring(groupname FROM '[0-9]+') AS INTEGER)<>0",
                new Object[][]{
                        {ValueType.TEXT, "Льготные категории по питанию в общем составе учащихся"},
                        {ValueType.NUMBER, "Количество учащихся", BENEFIT_PART_CHART_DATA}}, BENEFIT_PART_CHART_DATA);
        TYPES.put("BenefitPartChart",
                new SimpleType("select 'Льготные категории 1-4 класс', count(cf_clients.idofclient) "
                        + "from cf_clients "
                        + "left join cf_cards on cf_clients.idOfClient=cf_cards.idOfClient "
                        + "left join cf_clientgroups on cf_clients.idofclientgroup=cf_clientgroups.idofclientgroup and cf_clients.idoforg=cf_clientgroups.idoforg "
                        + "where cf_clients.discountmode<>0 and cf_cards.state=0 AND CAST(substring(groupname FROM '[0-9]+') AS INTEGER)<>0 and CAST(substring(groupname FROM '[0-9]+') AS INTEGER)<=4 "

                        + "union "

                        + "select 'Прочие льготные категории', count(cf_clients.idofclient) "
                        + "from cf_clients "
                        + "left join cf_cards on cf_clients.idOfClient=cf_cards.idOfClient "
                        + "left join cf_clientgroups on cf_clients.idofclientgroup=cf_clientgroups.idofclientgroup and cf_clients.idoforg=cf_clientgroups.idoforg "
                        + "where cf_clients.discountmode<>0 and cf_cards.state=0 AND CAST(substring(groupname FROM '[0-9]+') AS INTEGER)<>0 AND CAST(substring(groupname FROM '[0-9]+') AS INTEGER)>4 "

                        + "union "

                        + "select 'Не имеющие льгот', count(distinct cf_clients.idofclient) "
                        + "from cf_clients "
                        + "left join cf_cards on cf_clients.idOfClient=cf_cards.idOfClient "
                        + "left join cf_clientgroups on cf_clients.idofclientgroup=cf_clientgroups.idofclientgroup and cf_clients.idoforg=cf_clientgroups.idoforg "
                        + "where cf_clients.discountmode=0 and cf_cards.state=0 AND CAST(substring(groupname FROM '[0-9]+') AS INTEGER)<>0",
                        new Object[][]{
                                {ValueType.TEXT, "Льготные категории по питанию в общем составе учащихся"},
                                {ValueType.NUMBER, "Количество учащихся", BENEFIT_PART_CHART_DATA}}, BENEFIT_PART_CHART_DATA));
        TYPES.put("BenefitsChart", new SimpleType("select cf_categorydiscounts.categoryname, count (cat) " +
                "from ( " +
                "select trim(split_part(categoriesdiscounts, ',', 1)) as cat " +
                "from cf_clients " +

                "union all " +
                "select trim(split_part(categoriesdiscounts, ',', 2)) as cat " +
                "from cf_clients " +

                "union all " +
                "select split_part(categoriesdiscounts, ',', 3) as cat " +
                "from cf_clients " +

                "union all " +
                "select split_part(categoriesdiscounts, ',', 4) as cat " +
                "from cf_clients " +

                "union all " +
                "select split_part(categoriesdiscounts, ',', 5) as cat " +
                "from cf_clients) as tbl " +
                "left join cf_categorydiscounts on int8(cat)=cf_categorydiscounts.idofcategorydiscount " +
                "where cat <> '' and int8(cat)>0 " +
                "group by cf_categorydiscounts.categoryname", new Object[][]{
                {ValueType.TEXT, "Детализация льготных категорий кроме 1-4 класса"},
                {ValueType.NUMBER, "Количество учащихся", BENEFITS_CHART_DATA}}, BENEFITS_CHART_DATA));
        TYPES.put("VisitorsChart", new ComplexType(new Type[]{
                new SimpleType("select '' || EXTRACT(EPOCH FROM d) * 1000 as date, " +
                        "       int8(sum(evnt_count) / sum(all_count) * 100) as visited " +
                        "from (select events.d as d, overall.o, overall.c as all_count, count(events.c) as evnt_count, cast(count(events.c) as float8)/cast(overall.c as float8) "
                        +
                        "from (select regOrgSrc.idoforg as o, count(regOrgSrc.idofclient) as c " +
                        "      from cf_clients as regOrgSrc " +
                        "      left join cf_cards on regOrgSrc.idOfClient=cf_cards.idOfClient " +
                        "      left join cf_clientgroups on regOrgSrc.idofclientgroup=cf_clientgroups.idofclientgroup and regOrgSrc.idoforg=cf_clientgroups.idoforg "
                        + REGION_SENSITIVE_JOIN + " " +
                        "      where cf_cards.state=0 AND CAST(substring(groupname FROM '[0-9]+') AS INTEGER)<>0 and CAST(substring(groupname FROM '[0-9]+') AS INTEGER)<=4 "
                        + REGION_SENSITIVE_CLAUSE + " " +
                        "      group by regOrgSrc.idoforg) as overall " +
                        "join " +
                        "(select DISTINCT cf_enterevents.idofclient as c, cf_enterevents.idoforg as o, date_trunc('day', to_timestamp(cf_enterevents.evtdatetime / 1000)) as d "
                        +
                        "from cf_enterevents " +
                        "left join cf_clients as regOrgSrc on regOrgSrc.idOfClient=cf_enterevents.idOfClient " +
                        "left join cf_clientgroups on regOrgSrc.idofclientgroup=cf_clientgroups.idofclientgroup and regOrgSrc.idoforg=cf_clientgroups.idoforg " +
                        REGION_SENSITIVE_JOIN + " " +
                        "where CAST(substring(groupname FROM '[0-9]+') AS INTEGER)<>0 and CAST(substring(groupname FROM '[0-9]+') AS INTEGER)<=4 "
                        + REGION_SENSITIVE_CLAUSE + " " +
                        "      and cf_enterevents.evtdatetime >= EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MINIMUM_DATE%') * 1000 AND cf_enterevents.evtdatetime < EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MAXIMUM_DATE%') * 1000 "
                        +
                        "group by cf_enterevents.idofclient, cf_enterevents.idoforg, cf_enterevents.evtdatetime) events on events.o = overall.o "
                        +
                        "group by events.d, overall.o, overall.c " +
                        "having cast(count(events.c) as float8)/cast(overall.c as float8) > 0.2) as res " +
                        "group by d", VISITORS_CHART_1_DATA).setIncremental(true),
                new SimpleType("select '' || EXTRACT(EPOCH FROM d) * 1000 as date, " +
                        "       int8(sum(evnt_count) / sum(all_count) * 100) as visited " +
                        "from (select events.d as d, overall.o, overall.c as all_count, count(events.c) as evnt_count, cast(count(events.c) as float8)/cast(overall.c as float8) "
                        +
                        "from (select regOrgSrc.idoforg as o, count(regOrgSrc.idofclient) as c " +
                        "      from cf_clients as regOrgSrc " +
                        "      left join cf_cards on regOrgSrc.idOfClient=cf_cards.idOfClient " +
                        "      left join cf_clientgroups on regOrgSrc.idofclientgroup=cf_clientgroups.idofclientgroup and regOrgSrc.idoforg=cf_clientgroups.idoforg "
                        +      REGION_SENSITIVE_JOIN + " " +
                        "      where cf_cards.state=0 AND CAST(substring(groupname FROM '[0-9]+') AS INTEGER)<>0 and CAST(substring(groupname FROM '[0-9]+') AS INTEGER)>=5 "
                        +      REGION_SENSITIVE_CLAUSE + " " +
                        "      group by regOrgSrc.idoforg) as overall " +
                        "join " +
                        "(select DISTINCT cf_enterevents.idofclient as c, cf_enterevents.idoforg as o, date_trunc('day', to_timestamp(cf_enterevents.evtdatetime / 1000)) as d "
                        +
                        "from cf_enterevents " +
                        "left join cf_clients as regOrgSrc on regOrgSrc.idOfClient=cf_enterevents.idOfClient " +
                        "left join cf_clientgroups on regOrgSrc.idofclientgroup=cf_clientgroups.idofclientgroup and regOrgSrc.idoforg=cf_clientgroups.idoforg "
                        + REGION_SENSITIVE_JOIN + " " +
                        "where CAST(substring(groupname FROM '[0-9]+') AS INTEGER)<>0 and CAST(substring(groupname FROM '[0-9]+') AS INTEGER)>=5 "
                        +
                        "      and cf_enterevents.evtdatetime >= EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MINIMUM_DATE%') * 1000 AND cf_enterevents.evtdatetime < EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MAXIMUM_DATE%') * 1000 "
                        + REGION_SENSITIVE_CLAUSE + " " +
                        "group by cf_enterevents.idofclient, cf_enterevents.idoforg, cf_enterevents.evtdatetime) events on events.o = overall.o "
                        +
                        "group by events.d, overall.o, overall.c " +
                        "having cast(count(events.c) as float8)/cast(overall.c as float8) > 0.2) as res " +
                        "group by d", VISITORS_CHART_2_DATA).setIncremental(true)}, new Object[][]{
                {ValueType.DATE, "Дата"}, {ValueType.NUMBER, "1-4 класс", VISITORS_CHART_1_DATA}, {ValueType.NUMBER, "5-11 класс", VISITORS_CHART_2_DATA}},
                VISITORS_CHART_DATA));
        TYPES.put("OrgsRatingChart", new ComplexType(new Type[]{
                new SimpleType("events", RATING_CHART_1_DATA).setPreSelectSQLMethod("parseOrgsEvents")
                        .setPeriodDaysInc(-7).setIncremental(true),
                new SimpleType("payments", RATING_CHART_2_DATA).setPreSelectSQLMethod("parseOrgsPayments")
                        .setPeriodDaysInc(-7).setIncremental(true),
                new SimpleType("discounts", RATING_CHART_3_DATA).setPreSelectSQLMethod("parseOrgsDiscounts")
                        .setPeriodDaysInc(-7).setIncremental(true).setPostReportMethod("parseRatingChart"),
                new SimpleType("rating", RATING_CHART_4_DATA).setPreSelectSQLMethod("parseOrgsRating")
                        .setPeriodDaysInc(-7).setIncremental(true),
                new SimpleType("regions", RATING_CHART_5_DATA).setPreSelectSQLMethod("parseOrgsRegions")
                        .setPeriodDaysInc(-7).setIncremental(true)}, new Object[][]{
                {ValueType.TEXT, "ОУ"}, {ValueType.NUMBER, "Проход (%)", RATING_CHART_1_DATA}, {ValueType.NUMBER, "Платное питание (%)", RATING_CHART_2_DATA},
                {ValueType.TEXT, "Льготное питание", RATING_CHART_3_DATA}, {ValueType.NUMBER, "Рейтинг (%)", RATING_CHART_4_DATA}, {ValueType.TEXT, "Округ", RATING_CHART_5_DATA},},
                RATING_CHART_DATA));

        TYPES.put("FiscalChart", new ComplexType(new Type[]{
                new SimpleType(
                          "select '' || EXTRACT(EPOCH FROM current_timestamp) * 1000 as d, int8(sum(regOrgSrc.balance) / 100) / 1000 as v "
                        + "from cf_clients regOrgSrc "
                        + REGION_SENSITIVE_JOIN + " "
                        + "where 1=1 "
                        + REGION_SENSITIVE_CLAUSE + " "
                        + "group by 1", FISCAL_CHART_1_DATA).setIncremental(true),

                new SimpleType(
                          "select '' || EXTRACT(EPOCH FROM date_trunc('day', current_timestamp)) * 1000 as d, int8(sum(v) / 100) / 1000 as v "
                        + "from (select regOrgSrc.rsum as v, date_trunc('day', to_timestamp(regOrgSrc.createddate / 1000)) as d "
                        + "      from cf_orders regOrgSrc "
                        + REGION_SENSITIVE_JOIN + " "
                        + "      where regOrgSrc.socdiscount=0 and regOrgSrc.createddate>=EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MINIMUM_DATE%') * 1000 "
                        + REGION_SENSITIVE_CLAUSE + " "
                        + "            and regOrgSrc.createddate<EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MAXIMUM_DATE%') * 1000) as oo "
                        + "group by d", FISCAL_CHART_2_DATA).setIncremental(true),

                new SimpleType(
                        "select '' || EXTRACT(EPOCH FROM d) * 1000 as d, int8(sum(v) / 100) / 1000 as v "
                        + "from (select t.transactionsum as v, date_trunc('day', to_timestamp(t.transactiondate / 1000)) as d "
                        + "      from cf_transactions t "
                        + "      join cf_clients regOrgSrc on t.idofclient=regOrgSrc.idofclient "
                        + REGION_SENSITIVE_JOIN + " "
                        + "      where t.transactiondate>=EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MINIMUM_DATE%') * 1000 and "
                        + "            t.transactiondate<EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MAXIMUM_DATE%') * 1000 "
                        + REGION_SENSITIVE_CLAUSE
                        + "            and t.transactionsum>0) as oo "
                        + "group by 1", FISCAL_CHART_3_DATA).setIncremental(true),

                new SimpleType(
                        "select '' || EXTRACT(EPOCH FROM current_timestamp) * 1000 as d, "
                        + "       case when sum(cl.subbalance1) is not null THEN int8(sum(cl.subbalance1) / 100) / 1000 "
                        + "       else 0 end "
                        + "from cf_clients cl "
                        + "join cf_subscriber_feeding f on f.idofclient=cl.idofclient "
                        + "where f.wassuspended=false and lastdatepausesubscription=null and datecreateservice is not null",
                        FISCAL_CHART_4_DATA).setIncremental(true)
                }, new Object[][]{
                {ValueType.DATE, "Год"},
                {ValueType.NUMBER, "Остатки на лицевых счетах (тыс. руб.)", FISCAL_CHART_1_DATA},
                {ValueType.NUMBER, "Суммы платных дневных продаж (тыс. руб.)", FISCAL_CHART_2_DATA},
                {ValueType.NUMBER, "Сумма пополнений (тыс. руб.)", FISCAL_CHART_3_DATA},
                {ValueType.NUMBER, "Остатки на субсчете (тыс. руб.)", FISCAL_CHART_4_DATA} }, FISCAL_CHART_DATA));

        TYPES.put("CardsChart", new ComplexType(new Type[]{
                new SimpleType(
                        "select '' || int8(d)*1000, count(idofcard) "
                        + "from ("
                                + "select '' || EXTRACT(EPOCH FROM date_trunc('day', to_timestamp(c.createddate / 1000))) as d , c.idofcard "
                                + "from cf_cards c "
                                + "where c.createddate>=EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MINIMUM_DATE%') * 1000 and "
                                + "      c.createddate<EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MAXIMUM_DATE%') * 1000) as dat "
                        + "group by dat.d",
                        CARDS_CHART_1_DATA).setIncremental(true),

                new SimpleType(
                        "select '' || int8(d)*1000, count(idofcard) "
                        + "from ("
                                + "select '' || EXTRACT(EPOCH FROM date_trunc('day', to_timestamp(c.createddate / 1000))) as d , c.idofcard "
                                + "from cf_cards c "
                                + "join cf_history_card h on c.idofcard=h.idofcard and c.idofclient=h.newowner "
                                + "where c.createddate>=EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MINIMUM_DATE%') * 1000 and "
                                + "      c.createddate<EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MAXIMUM_DATE%') * 1000) as dat "
                        + "group by dat.d",
                        CARDS_CHART_2_DATA).setIncremental(true),

                new SimpleType(
                        "select '' || int8(d)*1000, count(idofcard) "
                        + "from ("
                                + "select '' || EXTRACT(EPOCH FROM date_trunc('day', to_timestamp(c.createddate / 1000))) as d , c.idofcard "
                                + "from cf_cards c "
                                + "left join cf_cards c2 on c.idofclient=c2.idofclient and c.idofcard<>c2.idofcard "
                                + "where c.createddate>=EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MINIMUM_DATE%') * 1000 and "
                                + "      c.createddate<EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MAXIMUM_DATE%') * 1000) as dat "
                        + "group by dat.d",
                        CARDS_CHART_3_DATA).setIncremental(true)
        }, new Object[][]{
                {ValueType.DATE, "День"},
                {ValueType.NUMBER, "Количество новых зарегистрированных карт (тыс. шт.)", CARDS_CHART_1_DATA},
                {ValueType.NUMBER, "Количество перерегистрированных карт (тыс. шт.)", CARDS_CHART_2_DATA},
                {ValueType.NUMBER, "Количество новых карт зарегистрированных на клиентов, у которых была карта (тыс. шт.)", CARDS_CHART_3_DATA} },
                CARDS_CHART_DATA));
        //initContragetsChartType();
    }



    public void runInitContragetsChartTypeAtStartup() {
        long l = System.currentTimeMillis();

        initContragetsChartType();
        l = System.currentTimeMillis() - l;
        if(l > 50000){
            logger.warn("runInitContragetsChartTypeAtStartup time:" +  l );
        }
    }

    public static ProjectStateReportService.Type getChartType(String reportType) {
        if(reportType.equals("ContragentsChart")) {
            initContragetsChartType();
        }
        return TYPES.get(reportType);
    }

    public static void initContragetsChartType() {
        if(TYPES.get("ContragentsChart") != null) {
            return;
        }


        List<Contragent> contragents = DAOReadonlyService.getInstance().getContragentsListFromOrders();//.getContragentsList(Contragent.TSP);
        if(contragents != null) {
            Type [] types = new Type[contragents.size()];
            for(int i=0; i<contragents.size(); i++) {
                types[i] = new SimpleType(
                        "select '' || EXTRACT(EPOCH FROM d) * 1000 as d, int8(sum(v) / 100) / 1000 as v "
                        + "from (select regOrgSrc.rsum as v, date_trunc('day', to_timestamp(regOrgSrc.createddate / 1000)) as d "
                        + "      from cf_orders regOrgSrc "
                        + REGION_SENSITIVE_JOIN + " "
                        + "      where regOrgSrc.socdiscount=0 and regOrgSrc.createddate>=EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MINIMUM_DATE%') * 1000 and "
                        + "            regOrgSrc.createddate<EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MAXIMUM_DATE%') * 1000 and "
                        + "            regOrgSrc.idofcontragent=" + contragents.get(i).getIdOfContragent() + " "
                        + REGION_SENSITIVE_JOIN + " "
                        + "      ) as oo "
                        + "group by d", CONTRAGENTS_CHART_DATA + i + 1).setIncremental(true);
            }
            Object[][] props = new Object[contragents.size() + 1][2];
            props [0] = new Object[] { ValueType.DATE, "Год" };
            for(int i=0; i<contragents.size(); i++) {
                Contragent cc = contragents.get(i);
                props [i + 1] = new Object [] {ValueType.NUMBER, cc.getContragentName(), CONTRAGENTS_CHART_DATA + i + 1};
            }
            TYPES.put("ContragentsChart", new ComplexType(types, props, CONTRAGENTS_CHART_DATA));
        }
    }


    private static final String INSERT_SQL = "INSERT INTO cf_projectstate_data (GenerationDate, Period, Region, Type, StringKey, StringValue, Comments) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String DELETE_SQL = "DELETE FROM cf_projectstate_data WHERE Period=? AND Type=? and Region=?";
    private static final String SAVE_DELETE_SQL = "DELETE FROM cf_projectstate_data WHERE Period=? AND Type=? and Region=? and StringKey=? ";
    //private static final String SELECT_SQL = "SELECT StringKey, StringValue FROM cf_projectstate_data WHERE Type=? and Period<=? and Region=? order by Period DESC, StringKey";
    private static final String SELECT_SQL = "SELECT StringKey, StringValue FROM cf_projectstate_data WHERE Type=? and Period=(select max(period) from cf_projectstate_data where type=? and region=?) and Region=? order by Period DESC, StringKey";
    private static final String PERIODIC_SELECT_SQL = "SELECT distinct StringKey, StringValue FROM cf_projectstate_data WHERE INT8(StringKey) <= EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MAXIMUM_DATE%') * 1000 and INT8(StringKey) >= EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MINIMUM_DATE%') * 1000 AND Type=? AND Region=? order by StringKey";
    private static final String PERIODIC_AVG_SELECT_SQL =
            "SELECT distinct substring(StringKey from '[^[:alnum:]]* {0,1}№ {0,1}(\\w*-?\\w*)'), " + PERIODIC_AVG_COL + " "
                    + "FROM cf_projectstate_data "
                    + "WHERE period <= EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MAXIMUM_DATE%') * 1000 and "
                    + "      period >= EXTRACT(EPOCH FROM TIMESTAMP WITH TIME ZONE '%MINIMUM_DATE%') * 1000 AND "
                    + "      Type=? and Region=? and substring(StringKey from '[^[:alnum:]]* {0,1}№ {0,1}([0-9]*)') <> '' "
                    + PERIODIC_AVG_GROUP + " order by 2 desc, 1";
    private static final String CHECK_SQL = "SELECT Period FROM cf_projectstate_data WHERE Type=? and Region=? order by Period DESC";


    public static boolean isOn() {
        return RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_PROJECT_STATE_REPORT_ON);
    }


    public static void setOn(boolean on) {
        RuntimeContext.getInstance()
                .setOptionValueWithSave(Option.OPTION_PROJECT_STATE_REPORT_ON, "" + (on ? "1" : "0"));
    }


    public void run() {
        RuntimeContext.getAppContext().getBean(ProjectStateReportService.class).doRun();
    }

    @Transactional
    public void doRun() {
        try {
            log("Project State started..");
            RuntimeContext runtimeContext = null;
            Session session = null;
            try {
                runtimeContext = RuntimeContext.getInstance();
                session = (Session) entityManager.getDelegate();
                /*session = runtimeContext.createPersistenceSession();*/

                initDictionaries(session);
            } catch (Exception e) {
            }
    
            if (!RuntimeContext.getInstance().isMainNode() || !isOn()) {
                //if (1 == 1) {
                log("Project State is turned off. You have to activate this tool using common Settings");
                return;
            }

            initContragetsChartType();
            Map<Integer, Boolean> clearedTypes = new HashMap<Integer, Boolean>();
            try {
                for (String t : TYPES.keySet()) {
                    parseType(session, TYPES.get(t), clearedTypes);
                }
            } catch (Exception e) {
            }
        } catch (Exception e) {
            logger.error("Failed to buid project state data", e);
        }
        log("Project state data builing is complete");
    }

    private synchronized void initDictionaries(Session session) throws Exception {
        if (PAY_AGENTS_LIST != null) {
            return;
        }
        initRegions(session);
        initPayContragents(session);
    }


    public void parseType(Session session, Type t, Map<Integer, Boolean> clearedTypes) {
        try {
            if (t instanceof SimpleType) {
                //  Если в SQL есть использование полатежного агента, то необходимо
                //  выполнять данный запрос ко всем существующим платежным агентам
                int agentCount = 0;
                if (((SimpleType) t).getSQL().indexOf(PAY_AGENTS_CLAUSE) > 0) {
                    agentCount = PAY_AGENTS_LIST.size();
                }

                int agentI = 0;
                do {
                    for (String regionName : REGIONS_LIST.keySet()) {
                        //  Если регион не "Все округа" и в запросе типа не указано что должен учитываться регион,
                        //  то пропускаем этот регион. Будем дожидаться региона "Все округа", у которого id типа
                        //  не увеличивается
                        int regionTypeInc = REGIONS_LIST.get(regionName);
                        if (regionTypeInc != 0 && ((SimpleType) t).getSQL().indexOf(REGION_SENSITIVE_CLAUSE) < 0) {
                            continue;
                        }
                        // Платежный агент, иногда необходимо для макро-подставноки
                        Integer idOfContragent = agentCount == 0 ? 0 : (Integer) PAY_AGENTS_LIST.get(agentI)[0];

                        //  Получаем даты для запуска
                        Calendar lastUpload = getLastUploadData(session, (SimpleType) t, regionName, idOfContragent);
                        Calendar today = getToday();

                        Map<String, String> data = null;
                        if (((SimpleType) t).getPreSelectSQLMethod() != null) {
                            data = new HashMap<String, String>();
                            executeDataMethod((SimpleType) t, data, session,
                                    addMethodParameters(lastUpload, today, regionName, idOfContragent,
                                            t.getReportType(), null), ((SimpleType) t).getPreSelectSQLMethod());
                        } else {
                            data = loadData(session, (SimpleType) t, regionName, lastUpload, today, idOfContragent);
                        }
                        if (data == null) {
                            return;
                        }
                        executeDataMethod((SimpleType) t, data, session,
                                addMethodParameters(lastUpload, today, t.getReportType(), null),
                                ((SimpleType) t).getPostSelectSQLMethod());
                        saveData(session, data, regionName, t, clearedTypes, idOfContragent);
                    }
                    agentI++;
                } while (agentI < agentCount);
            } else if (t instanceof ComplexType) {
                ComplexType ct = (ComplexType) t;
                Type types[] = ct.getTypes();
                for (Type t2 : types) {
                    parseType(session, t2, clearedTypes);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to process type " + t.getReportType(), e);
        }
    }


    @Transactional
    public void saveData(Session session, Map<String, String> data, String regionName, Type t,
            Map<Integer, Boolean> clearedTypes, int contragentInc) {
        try {
            if (data.isEmpty()) {
                return;
            }

            long ms = System.currentTimeMillis();
            Calendar cal = getToday();
            int type = t.getReportType() + buildPayAgentTypeInc(contragentInc);  //  Увеличиваем ID типа, если это агент
            long period = cal.getTimeInMillis();

            org.hibernate.Query q;
            if (!clearedTypes.containsKey(t.getReportType())) {
                q = session.createSQLQuery(DELETE_SQL);
                q.setLong(0, period);
                q.setInteger(1, type);
                q.setString(2, regionName);
                q.executeUpdate();
                clearedTypes.put(t.getReportType(), true);
            }
            q = session.createSQLQuery(INSERT_SQL);
            q.setLong(0, ms);
            q.setLong(1, period);
            q.setString(2, regionName);
            q.setInteger(3, type);
            q.setString(6, "Base: " + t.getReportType() + "; agent: " + buildPayAgentTypeInc(contragentInc));
            for (String k : data.keySet()) {
                q.setString(4, k);
                q.setString(5, data.get(k));
                saveDeleteData(period, type, regionName, k, session);
                q.executeUpdate();
            }
        } catch (Exception e) {
            logger.error("Failed to save report data into database for " + t.getReportType(), e);
        }
    }

    public void saveDeleteData(long period, int type, String regionName, String key, Session session) {
        org.hibernate.Query q = session.createSQLQuery(SAVE_DELETE_SQL);
        q.setLong(0, period);
        q.setInteger(1, type);
        q.setString(2, regionName);
        q.setString(3, key);
        q.executeUpdate();
    }


    public Calendar getLastUploadData(Session session, SimpleType t, String regionName, int idofcontragent) {
        try {
            org.hibernate.Query q = session.createSQLQuery(applyMacroReplace(CHECK_SQL, t.getReportType()));
            q.setInteger(0, t.getReportType() + buildPayAgentTypeInc(idofcontragent));
            q.setString(1, regionName);
            List resultList = q.list();
            for (Object entry : resultList) {
                BigInteger e = (BigInteger) entry;
                Calendar res = Calendar.getInstance();
                res.setTimeInMillis(e.longValue());
                return res;
            }
        } catch (Exception e) {
            logger.error("Failed to check existance of data for report " + t.getReportType(), e);
        }

        return getStartDate();
    }


    public Map<String, String> loadData(Session session, SimpleType t, String regionName, Calendar lastUpload,
            Calendar today, Integer idOfContragent) {
        try {
            Map<String, String> result = new TreeMap<String, String>();
            if (t.isIncremental() && today.getTimeInMillis() <= lastUpload.getTimeInMillis()) {
                return result;
            }
            String finalSQL = applyMacroReplace(t.getSQL(), t.getReportType(), lastUpload, today, 0, regionName,
                    idOfContragent);
            //logger.error("PROJECT_STATE SQL COMMAND: " + finalSQL);

            log(t.getReportType() + " :: " + regionName + " :: SQL:__ " + finalSQL);
            org.hibernate.Query q = session.createSQLQuery(finalSQL);             /*!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!*/
            List resultList = q.list();//Collections.EMPTY_LIST;
            for (Object entry : resultList) {
                Object e[] = (Object[]) entry;
                String key = (String) e[0];
                String value = null;

                if (t.getValueType() == Integer.class) {
                    value = String.format("%d", ((BigInteger) e[1]).intValue());
                }
                if (t.getValueType() == Double.class) {
                    value = String.format("%s", ((BigDecimal) e[1]).doubleValue());
                }
                if (t.getValueType() == String.class) {
                    value = ((String) e[1]).trim();
                }
                result.put(key, value);
            }
            return result;
        } catch (Exception e) {
            logger.error("Failed to load report data from database for " + t.getReportType(), e);
            return null;
        }
    }


    public static Calendar getStartDate() {
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.YEAR, 2012);
        cal.set(Calendar.MONTH, Calendar.SEPTEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 1);




        /*cal.set(Calendar.YEAR, 2014);
        cal.set(Calendar.MONTH, Calendar.MARCH);
        cal.set(Calendar.DAY_OF_MONTH, 1);*/


        return cal;
    }


    //public int DAY_OF_MONTH = 10;
    public static Calendar getToday() {
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);


        /*cal.set(Calendar.YEAR, 2014);
        cal.set(Calendar.MONTH, Calendar.FEBRUARY);
        cal.set(Calendar.DAY_OF_MONTH, 27);*/

        return cal;
    }


    public String applyMacroReplace(String sql, int type) {
        Calendar min = getStartDate();
        Calendar max = getToday();
        return applyMacroReplace(sql, type, min, max);
    }


    public String applyMacroReplace(String sql, int type, Calendar min, Calendar max) {
        return applyMacroReplace(sql, type, min, max, 0, null);
    }


    public String applyMacroReplace(String sql, int type, Calendar min, Calendar max, int daysInc) {
        return applyMacroReplace(sql, type, min, max, daysInc, null);
    }


    public String applyMacroReplace(String sql, int type, Calendar min, Calendar max, int daysInc, String regionName) {
        return applyMacroReplace(sql, type, min, max, daysInc, regionName, null);
    }


    public String applyMacroReplace(String sql, int type, Calendar min, Calendar max, int daysInc, String regionName,
            Integer idOfContragent) {
        if (sql.indexOf("%DATE_CLAUSE%") > -1) {
            //  Для льготного питания получаем данные на 3 дня раньше текущего, при cохранении так же необходимо следить за сохраняемой датой
            if (type == ACTIVE_CHART_4_DATA || type == UNIQUE_CHART_4_DATA) {
                Calendar maxD = new GregorianCalendar();
                Calendar minD = new GregorianCalendar();
                maxD.setTimeInMillis(max.getTimeInMillis() + DISCOUNT_FLOWCHARTS_DATE_INCREMENT);
                minD.setTimeInMillis(min.getTimeInMillis() + DISCOUNT_FLOWCHARTS_DATE_INCREMENT);
                sql = sql.replaceAll("%DATE_CLAUSE%", "regOrgSrc.createddate >= EXTRACT(EPOCH FROM TIMESTAMP '" + DB_DATE_FORMAT.format(minD.getTime()) + "') * 1000 AND "
                        + "regOrgSrc.createddate < EXTRACT(EPOCH FROM TIMESTAMP '" + DB_DATE_FORMAT.format(maxD.getTime()) + "') * 1000");

            } else {
                sql = sql.replaceAll("%MAXIMUM_DATE_CLAUSE%", "EXTRACT(EPOCH FROM TIMESTAMP '%MINIMUM_DATE%') * 1000 AND EXTRACT(EPOCH FROM TIMESTAMP '%MAXIMUM_DATE%') * 1000");
            }
        }
        if (sql.indexOf("%MINIMUM_DATE%") > -1) {
            sql = sql.replaceAll("%MINIMUM_DATE%", DB_DATE_FORMAT.format(min.getTime()));
        }
        if (sql.indexOf("%MAXIMUM_DATE%") > -1) {
            sql = sql.replaceAll("%MAXIMUM_DATE%", DB_DATE_FORMAT.format(max.getTime()));
        }
        if (sql.indexOf(PERIODIC_AVG_COL) > -1) {
            if (type == RATING_CHART_3_DATA) {
                sql = sql.replaceAll(PERIODIC_AVG_COL, " max(StringValue) ");
            }
            if (type == RATING_CHART_5_DATA) {
                sql = sql.replaceAll(PERIODIC_AVG_COL, " StringValue ");
            } else {
                sql = sql.replaceAll(PERIODIC_AVG_COL,
                        " sum(cast(StringValue as double precision)) / least(count(distinct period), %PERIOD_LENGTH%) || '' ");
            }
        }
        if (sql.indexOf(PERIODIC_AVG_GROUP) > -1) {
            if (type != RATING_CHART_5_DATA) {
                sql = sql.replaceAll(PERIODIC_AVG_GROUP, " group by StringKey ");
            } else {
                sql = sql.replaceAll(PERIODIC_AVG_GROUP, " ");
            }
        }
        if (sql.indexOf("%PERIOD_LENGTH%") > -1) {
            sql = sql.replaceAll("%PERIOD_LENGTH%", "" + daysInc);
        }
        if (sql.indexOf(REGION_SENSITIVE_JOIN) > -1) {
            String replaceRegionSQL = "";
            if (regionName != null && regionName.length() > 0) {
                int inc = REGIONS_LIST.get(regionName);
                replaceRegionSQL = inc == 0 ? "" : " left join cf_orgs as regOrg on regOrg.idoforg=regOrgSrc.idoforg ";
            }
            sql = sql.replaceAll(REGION_SENSITIVE_JOIN, replaceRegionSQL);
        }
        if (sql.indexOf(REGION_SENSITIVE_CLAUSE) > -1) {
            String replaceRegionSQL = "";
            if (regionName != null && regionName.length() > 0) {
                int inc = REGIONS_LIST.get(regionName);
                replaceRegionSQL = inc == 0 ? "" : " and regOrg.district='" + regionName + "' ";
            }
            sql = sql.replaceAll(REGION_SENSITIVE_CLAUSE, replaceRegionSQL);
        }
        if (sql.indexOf(PAY_AGENTS_CLAUSE) > -1) {
            sql = sql.replaceAll(PAY_AGENTS_CLAUSE, "" + idOfContragent);
        }

        //
        return sql;
    }

    public DataTable generateReport(RuntimeContext runtimeContext, Calendar dateAt, Calendar dateTo, String regionName,
            Type t, String encoding) throws IllegalArgumentException {
        if (runtimeContext == null || dateTo == null || dateAt == null || t == null) {
            throw new IllegalArgumentException("RuntimeContext, Calendar and Type cannot be null(s)");
        }

        if (regionName == null) {
            regionName = "Все округа";
        }
        dateAt.set(Calendar.HOUR_OF_DAY, 0);
        dateAt.set(Calendar.MINUTE, 0);
        dateAt.set(Calendar.SECOND, 0);
        dateAt.set(Calendar.MILLISECOND, 0);
        dateTo.set(Calendar.HOUR_OF_DAY, 0);
        dateTo.set(Calendar.MINUTE, 0);
        dateTo.set(Calendar.SECOND, 0);
        dateTo.set(Calendar.MILLISECOND, 0);
        encoding = encoding == "" ? null : encoding;

        try {
            Session session = runtimeContext.createReportPersistenceSession();

            initDictionaries(session);

            Map<String, List<Item>> data = loadReportData(session, dateAt, dateTo, regionName, t);
            session.close();

            if (t instanceof SimpleType) {
                executeDataMethod((SimpleType) t, data, session,
                        addMethodParameters(dateAt, dateTo, t.getReportType(), null),
                        ((SimpleType) t).getPostReportMethod());
            } else if (t instanceof ComplexType) {
                ComplexType ct = (ComplexType) t;
                for (Type t2 : ct.getTypes()) {
                    executeDataMethod((SimpleType) t2, data, session,
                            addMethodParameters(dateAt, dateTo, t.getReportType(), null),
                            ((SimpleType) t2).getPostReportMethod());
                }
            }
            normalizeData (data);
            DataTable dataTable = buildDataTable(data, t, encoding);
            return dataTable;
        } catch (Exception e) {
            logger.error("Failed to load data from database for report " + t.getReportType() + " generation", e);
        }
        return null;
    }


    private Map<String, List<Item>> loadReportData(Session session, Calendar dateAt, Calendar dateTo,
            String regionName, Type t) {
        return loadReportData(session, dateAt, dateTo, regionName, t, new TreeMap<String, List<Item>>());
    }


    private Map<String, List<Item>> loadReportData(Session session, Calendar dateAt, Calendar dateTo,
            String regionName, Type t, Map<String, List<Item>> result) {
        try {
            if (t instanceof SimpleType) {
                //  Если в SQL есть использование полатежного агента, то необходимо
                //  выполнять данный запрос ко всем существующим платежным агентам
                int agentCount = 0;
                if (((SimpleType) t).getSQL().indexOf(PAY_AGENTS_CLAUSE) > 0) {
                    agentCount = PAY_AGENTS_LIST.size();
                }

                int agentI = 0;
                do {
                    // Платежный агент, иногда необходимо для макро-подставноки
                    Integer idOfContragent = agentCount == 0 ? 0 : (Integer) PAY_AGENTS_LIST.get(agentI)[0];

                    Map<String, Item> res = loadReportData(session, dateAt, dateTo, regionName, idOfContragent,
                            (SimpleType) t);
                    if (res == null) {
                        return result;
                    }
                    for (String k : res.keySet()) {
                        List<Item> vals = result.get(k);
                        if (vals == null) {
                            vals = new ArrayList<Item>();
                            result.put(k, vals);
                        }
                        vals.add(res.get(k));
                    }
                    agentI++;
                } while (agentI < agentCount);
            } else if (t instanceof ComplexType) {
                Type types[] = ((ComplexType) t).getTypes();
                for (Type t2 : types) {
                    loadReportData(session, dateAt, dateTo, regionName, (SimpleType) t2, result);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to parse report using type: " + t.getReportType() + " and date: " + dateTo, e);
        }
        return result;
    }


    private Map<String, Item> loadReportData(Session session, Calendar dateAt, Calendar dateTo, String regionName,
            int payAgentInc, SimpleType t) {
        try {
            Map<String, Item> result = new TreeMap<String, Item>();
            org.hibernate.Query q = null;
            int type = t.getReportType() + buildPayAgentTypeInc(
                    payAgentInc); //  Увеличиваем ID типа, если это конрагент или регион
            if (t.isIncremental()) {
                String certainSQL = PERIODIC_SELECT_SQL;
                if (t.getPeriodDaysInc() != 0) {
                    certainSQL = PERIODIC_AVG_SELECT_SQL;
                    // Убираем N дней, если это указано у типа
                    dateAt.setTimeInMillis(dateTo.getTimeInMillis() + 86400000 * t.getPeriodDaysInc());
                }
                q = session.createSQLQuery(
                        applyMacroReplace(certainSQL, t.getReportType(), dateAt, dateTo, Math.abs(t.getPeriodDaysInc()),
                                regionName));
                q.setString(1, regionName);
            } else {
                q = session.createSQLQuery(SELECT_SQL);
                q.setInteger(1, type);
                q.setString(2, regionName);
                q.setString(3, regionName);
            }
            q.setInteger(0, type);


            //  Временный фикс для 402
            int insertType = t.getReportType ();
            if (t.getReportType() == REFILL_CHART_02_DATA || t.getReportType() == REFILL_PROGRESS_0_CHART) {
                insertType = type;
            }
            for (Object entry : q.list()) {
                Object e[] = (Object[]) entry;
                result.put(((String) e[0]).trim(), new Item (insertType, ((String) e[1]).trim()));
            }
            return result;
        } catch (Exception e) {
            logger.error("Failed to load report using type: " + t.getReportType() + " and date: " + dateTo, e);
            return null;
        }
    }


    private DataTable buildDataTable(Map<String, List<Item>> data, Type t, String encoding) throws TypeMismatchException {
        DataTable dt = new DataTable();
        ArrayList cd = new ArrayList();
        for (int i = 0; i < t.getColumns().length; i++) {
            Object col[] = t.getColumns()[i];
            ValueType vt = (ValueType) col[0];
            //  Если необходимо показать информацию по платежным агентам, то заменяем колонку PAY_AGENTS_COLUMNS
            //  всему доступными платежными агентами, тип оставляем по умолчанию
            if (((String) col[1]).equals(PAY_AGENTS_COLUMNS)) {
                int y = 1;
                for (Object[] ag : PAY_AGENTS_LIST) {
                    cd.add(new ColumnDescription("col" + (i + 1 + y * 1000), vt == ValueType.DATE ? ValueType.TEXT : vt,
                            encode ((String) ag[1], encoding)));
                    y++;
                }
            } else {
                cd.add(new ColumnDescription("col" + (i + 1), vt == ValueType.DATE ? ValueType.TEXT : vt,
                        encode ((String) col[1], encoding)));
            }
        }
        dt.addColumns(cd);

        for (String k : data.keySet()) {
            TableRow r = new TableRow();
            List<Item> vals = data.get(k);

            if (vals == null || vals.size() < 1) {
                continue;
            }

            if (t.getColumns()[0][0] == ValueType.TEXT) {
                r.addCell(k);
            } else if (t.getColumns()[0][0] == ValueType.DATE) {
                Date d = new Date(Long.parseLong(k));
                if (d.getDay() == 0 || d.getDay() == 6) {
                    continue;
                }
                r.addCell(DATE_FORMAT.format(d));
            } else if (t.getColumns()[0][0] == ValueType.NUMBER) {
                r.addCell(Long.parseLong(k));
            }


            int agentInc = 0;
            if (t.getColumns().length > 0 && ((String) t.getColumns()[1][1]).equals(PAY_AGENTS_COLUMNS)) {
                agentInc = PAY_AGENTS_LIST.size() - 1;
            }
            //  Обрабаытваем агентов - для каждого из них, выбираем данные того типа, который ему соответствует
            int agentCount = 0;
            if ((t.getReportType() == REFILL_CHART_02_DATA || t.getReportType() == REFILL_PROGRESS_0_CHART) &&
                    t.getColumns().length > 0 && ((String) t.getColumns()[1][1]).equals(PAY_AGENTS_COLUMNS)) {
                agentCount = PAY_AGENTS_LIST.size();
            }
            int agentI = 0;
            do {
                Integer idOfContragent = agentCount == 0 ? 0 : (Integer) PAY_AGENTS_LIST.get(agentI)[0];
                int colsLimit = t.getColumns().length + agentInc;
                if (idOfContragent != 0) {
                    colsLimit = 2;
                }
                for (int i = 1; i < colsLimit; i++) {
                    Object col[] = t.getColumns()[agentInc != 0 ? 1 : i];       // Подсчет переделать
                    Integer type = null;
                    if (col != null && col [2] != null) {
                        type = (Integer) col [2];
                    }
                    if (idOfContragent != 0) {
                        type = t.getReportType() + buildPayAgentTypeInc(idOfContragent);
                        col = t.getColumns()[1];
                    }
                    String val = getValue (vals, type);


                    if ((ValueType) col[0] == ValueType.TEXT) {
                        if (i - 1 >= vals.size()) {
                            r.addCell("");
                        } else {
                            if(val != null) {
                                r.addCell(encode (val, encoding));
                            } else {
                                r.addCell("");
                            }
                        }
                    } else if ((ValueType) col[0] == ValueType.NUMBER) {
                        //  Проверка, если выполняем за выходной и выполняются конкретные типы,
                        //  то добавляем null вместо значений, чтобы линия прервалась и потом продолжилась
                        if (val == null) {
                            r.addCell(Value.getNullValueFromValueType(ValueType.NUMBER));
                            continue;
                        }

                        if (i - 1 >= vals.size()) {
                            r.addCell(0);
                        } else {
                            try {
                                r.addCell(Integer.parseInt(val));
                            } catch (NumberFormatException nfe) {
                                if (val != null && val.length() > 0) {
                                    r.addCell(new BigDecimal(Double.parseDouble(val))
                                            .setScale(1, BigDecimal.ROUND_HALF_DOWN).doubleValue());
                                } else {
                                    r.addCell(new BigDecimal(0D)
                                            .setScale(1, BigDecimal.ROUND_HALF_DOWN).doubleValue());
                                }
                            }
                        }
                    }
                }
                agentI++;
            } while (agentI < agentCount);
            dt.addRow(r);
        }

        return dt;
    }


    public String encode (String str, String encoding)
    {
        /*if (encoding != null) {
            try {
                str = new String(str.getBytes("UTF-8"), encoding);
            } catch (Exception e) {
            }
        }*/
        return str;
    }


    protected static class ComplexType implements Type {

        private Type types[];
        protected Object[][] columns;
        private int type;
        private boolean incremental;


        public ComplexType(Type types[], Object[][] columns, int type) {
            this.types = types;
            this.columns = columns;
            this.type = type;
        }

        public Object[][] getColumns() {
            return columns;
        }

        public Type[] getTypes() {
            return types;
        }

        public int getReportType() {
            return type;
        }

        public ComplexType setIncremental(boolean incremental) {
            this.incremental = incremental;
            return this;
        }

        public boolean isIncremental() {
            return incremental;
        }
    }


    protected static class SimpleType implements Type {

        private String preSelectSQL;
        private String postSelectSQL;
        private String postReport;
        private String sql;
        protected Object[][] columns;
        private int type;
        private Class valueType;
        private boolean incremental = false;
        private String parsingMethod = null;
        private int periodDaysInc = 0;
        private boolean regionSensitive;


        public SimpleType(String sql, int type) {
            this.sql = sql;
            this.columns = null;
            this.type = type;
            valueType = Integer.class;
        }

        public SimpleType(String sql, Object[][] columns, int type) {
            this.sql = sql;
            this.columns = columns;
            this.type = type;
            valueType = Integer.class;
        }

        public SimpleType(String sql, Object[][] columns, int type, Class valueType) {
            this.sql = sql;
            this.columns = columns;
            this.type = type;
            this.valueType = valueType;
        }

        public SimpleType setValueType(Class valueType) {
            this.valueType = valueType;
            return this;
        }

        public Object[][] getColumns() {
            return columns;
        }

        public Class getValueType() {
            return valueType;
        }

        public String getSQL() {
            return sql;
        }

        public int getReportType() {
            return type;
        }

        public SimpleType setPostSelectSQLMethod(String postSelectSQL) {
            this.postSelectSQL = postSelectSQL;
            return this;
        }

        public String getPostSelectSQLMethod() {
            return postSelectSQL;
        }

        public SimpleType setPostReportMethod(String postReport) {
            this.postReport = postReport;
            return this;
        }

        public String getPostReportMethod() {
            return postReport;
        }

        public SimpleType setPreSelectSQLMethod(String preSelectSQL) {
            this.preSelectSQL = preSelectSQL;
            return this;
        }

        public String getPreSelectSQLMethod() {
            return preSelectSQL;
        }

        public SimpleType setIncremental(boolean incremental) {
            this.incremental = incremental;
            return this;
        }

        public boolean isIncremental() {
            return incremental;
        }

        public SimpleType setPeriodDaysInc(int periodDaysInc) {
            this.periodDaysInc = periodDaysInc;
            return this;
        }

        public int getPeriodDaysInc() {
            return periodDaysInc;
        }


        public SimpleType setRegionSensitive(boolean regionSensitive) {
            this.regionSensitive = regionSensitive;
            return this;
        }

        public boolean getRegionSensitive() {
            return regionSensitive;
        }
    }


    public interface Type {

        public Object[][] getColumns();

        public int getReportType();

        public boolean isIncremental();
    }


    public Map<String, Object> addMethodParameters(Calendar dateAt, Calendar dateTo, int reportType,
            Map<String, Object> params) {
        return addMethodParameters(dateAt, dateTo, "", reportType, params);
    }


    public Map<String, Object> addMethodParameters(Calendar dateAt, Calendar dateTo, String regionName, int reportType,
            Map<String, Object> params) {
        return addMethodParameters(dateAt, dateTo, "", null, reportType, params);
    }


    public Map<String, Object> addMethodParameters(Calendar dateAt, Calendar dateTo, String regionName,
            Integer idOfContragent, int reportType, Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<String, Object>();
        }
        params.put("dateAt", dateAt);
        params.put("dateTo", dateTo);
        params.put("regionName", regionName);
        params.put("reportType", reportType);
        params.put("idOfContragent", idOfContragent);
        return params;
    }


    public void executeDataMethod(SimpleType t, Map data, Session session, Map<String, Object> params, String method) {
        if (method == null || method.length() < 1) {
            return;
        }

        java.lang.reflect.Method meth;
        try {
            meth = ProjectStateReportService.class.getDeclaredMethod(method, Object.class, Object.class, Object.class);
            meth.invoke(this, data, session, params);
        } catch (Exception e) {
            logger.error("Failed to execute support method " + method);
        }
    }


    public void parseOrgsPayments(Object dataSource, Object sessionObj, Object paramsObj) {
        parseOrgsRequest(dataSource, sessionObj, paramsObj,
                "select cf_orgs.idoforg, count(distinct cf_orders.idofclient) "
                        + "from cf_orgs "
                        + "left join cf_friendly_organization on cf_orgs.idoforg=currentorg "
                        + "left join cf_orders on cf_orders.idoforg=idoffriendlyorg or cf_orders.idoforg=currentorg "
                        + "left join cf_clients on cf_orders.idofclient=cf_clients.idofclient "
                        + "where cf_orders.socdiscount=0 and cf_clients.idOfClientGroup<" + ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue() + " and "
                        + "      cf_orders.createddate >= EXTRACT(EPOCH FROM TIMESTAMP '%MINIMUM_DATE%') * 1000 AND "
                        + "      cf_orders.createddate < EXTRACT(EPOCH FROM TIMESTAMP '%MAXIMUM_DATE%') * 1000 and cf_orgs.state<>0 "
                        + "group by cf_orgs.idoforg "
                        + "order by cf_orgs.idoforg"
                /*"select cf_orgs.idoforg, count(distinct cf_orders.idofclient) " +
                "from cf_orders, cf_orgs " +
                "where cf_orders.socdiscount=0 and cf_orgs.idoforg=cf_orders.idoforg and " +
                "cf_orders.createddate between EXTRACT(EPOCH FROM TIMESTAMP '%MINIMUM_DATE%') * 1000 AND " +
                "EXTRACT(EPOCH FROM TIMESTAMP '%MAXIMUM_DATE%') * 1000 " +
                " and cf_orgs.state<>0 "+
                "group by cf_orgs.idoforg " +
                "order by cf_orgs.idoforg"*/, false);
    }


    public void parseOrgsDiscounts(Object dataSource, Object sessionObj, Object paramsObj) {
        parseOrgsRequest(dataSource, sessionObj, paramsObj,
                "select cf_orgs.idoforg, count(distinct cf_orders.idofclient) "
                        + "from cf_orgs "
                        + "left join cf_friendly_organization on cf_orgs.idoforg=currentorg "
                        + "left join cf_orders on cf_orders.idoforg=idoffriendlyorg or cf_orders.idoforg=currentorg "
                        + "left join cf_clients on cf_orders.idofclient=cf_clients.idofclient "
                        + "where cf_orders.socdiscount<>0 and cf_clients.idOfClientGroup<" + ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue() + " and "
                        + "      cf_orders.createddate >= EXTRACT(EPOCH FROM TIMESTAMP '%MINIMUM_DATE%') * 1000 AND "
                        + "      cf_orders.createddate < EXTRACT(EPOCH FROM TIMESTAMP '%MAXIMUM_DATE%') * 1000 and cf_orgs.state<>0 "
                        + "group by cf_orgs.idoforg "
                        + "order by cf_orgs.idoforg"
                /*"select cf_orgs.idoforg, count(distinct cf_orders.idofclient) " +
                        "from cf_orders, cf_orgs " +
                        "where cf_orders.socdiscount<>0 and cf_orgs.idoforg=cf_orders.idoforg and " +
                        "cf_orders.createddate between EXTRACT(EPOCH FROM TIMESTAMP '%MINIMUM_DATE%') * 1000 AND " +
                        "EXTRACT(EPOCH FROM TIMESTAMP '%MAXIMUM_DATE%') * 1000 " +
                        " and cf_orgs.state<>0 "+
                        "group by cf_orgs.idoforg " +
                        "order by cf_orgs.idoforg"*/, true);
    }


    public void parseOrgsEvents(Object dataSource, Object sessionObj, Object paramsObj) {
        parseOrgsRequest(dataSource, sessionObj, paramsObj,
                "select cf_enterevents.idoforg, count(distinct cf_enterevents.idofclient) "
                        + "from cf_orgs "
                        + "left join cf_friendly_organization on cf_orgs.idoforg=currentorg "
                        + "left join cf_enterevents on cf_enterevents.idoforg=idoffriendlyorg or cf_enterevents.idoforg=currentorg "
                        + "left join cf_clients on cf_enterevents.idofclient=cf_clients.idofclient "
                        + "where cf_clients.idOfClientGroup<" + ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue() + " and "
                        + "      cf_enterevents.evtdatetime >= EXTRACT(EPOCH FROM TIMESTAMP '%MINIMUM_DATE%') * 1000 AND "
                        + "      cf_enterevents.evtdatetime < EXTRACT(EPOCH FROM TIMESTAMP '%MAXIMUM_DATE%') * 1000 and cf_orgs.state<>0 "
                        + "group by cf_enterevents.idoforg "
                        + "order by cf_enterevents.idoforg"
                /*"select cf_orgs.idoforg, count(distinct cf_enterevents.idofclient) " +
                        "from cf_enterevents, cf_orgs " +
                        "where cf_orgs.idoforg=cf_enterevents.idoforg and " +
                        "cf_enterevents.evtdatetime between EXTRACT(EPOCH FROM TIMESTAMP '%MINIMUM_DATE%') * 1000 AND "
                        +
                        "EXTRACT(EPOCH FROM TIMESTAMP '%MAXIMUM_DATE%') * 1000 " +
                        " and cf_orgs.state<>0 "+
                        "group by cf_orgs.idoforg " +
                        "order by cf_orgs.idoforg"*/, false);
    }

    public void parseOrgsRequest(Object dataSource, Object sessionObj, Object paramsObj, String sql,
            boolean absoluteIfExists) {
        Map<String, Object> params = (Map<String, Object>) paramsObj;
        Map<String, String> data = (Map<String, String>) dataSource;
        Calendar dateAt = (Calendar) params.get("dateAt");
        Calendar dateTo = (Calendar) params.get("dateTo");
        String regionName = (String) params.get("regionName");
        Integer reportType = (Integer) params.get("reportType");
        data.clear();

        Session session = (Session) sessionObj;
        try {
            Map<Object[], Long> clientsCount = getClientsCount(session);
            Map<Long, Long> targetsCount = new HashMap<Long, Long>();
            String finalSQL = applyMacroReplace(sql, reportType, dateAt, dateTo);
            org.hibernate.Query q = session.createSQLQuery(finalSQL);
            log(reportType + " :: " + regionName + " :: SQL:__ " + finalSQL);
            List resultList = q.list();//Collections.EMPTY_LIST;                                          !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            for (Object entry : resultList) {
                Object e[] = (Object[]) entry;
                targetsCount.put(((BigInteger) e[0]).longValue(), ((BigInteger) e[1]).longValue());
            }

            for (Object key[] : clientsCount.keySet()) {
                Long id = (Long) key[0];
                String name = (String) key[1];

                double clientCount = new Double(clientsCount.get(key));
                double targetCount = new Double(targetsCount.get(id) == null ? 0L : targetsCount.get(id));
                double percent = targetCount / clientCount;
                if (absoluteIfExists) {
                    percent = targetCount > 0 ? 1 : 0;
                }
                data.put(name, "" + new BigDecimal(Math.min(100D, Math.max(0D, percent * 100)))
                        .setScale(1, BigDecimal.ROUND_HALF_DOWN).doubleValue());
            }
        } catch (Exception e) {
            logger.error("Failed to load events from database", e);
        }
    }

    public void parseOrgsRegions(Object dataSource, Object sessionObj, Object paramsObj) {
        Map<String, Object> params = (Map<String, Object>) paramsObj;
        Map<String, String> data = (Map<String, String>) dataSource;
        Calendar dateAt = (Calendar) params.get("dateAt");
        Calendar dateTo = (Calendar) params.get("dateTo");
        Integer reportType = (Integer) params.get("reportType");
        data.clear();

        Session session = (Session) sessionObj;
        try {
            String sql = "SELECT cf_orgs.officialname, cf_orgs.district, count(cf_clients.idofclient) "
                    + "from cf_orgs "
                    + "left join cf_clients on  cf_clients.idoforg=cf_orgs.idoforg "
                    + "where officialname<>'' and cf_orgs.state<>0 "
                    + "group by cf_orgs.officialname, cf_orgs.district "
                    + "order by cf_orgs.officialname";
            Map<Long, Long> targetsCount = new HashMap<Long, Long>();
            String finalSQL = applyMacroReplace(sql, reportType, dateAt, dateTo);
            org.hibernate.Query q = session.createSQLQuery(finalSQL);
            List resultList = q.list();//Collections.EMPTY_LIST;
            for (Object entry : resultList) {
                Object e[] = (Object[]) entry;
                if (((BigInteger) e[2]).longValue() > 0) {
                    data.put((String) e[0], (String) e[1]);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to load events from database", e);
        }
    }


    public void parseOrgsRating(Object dataSource, Object sessionObj, Object paramsObj) {
        Map<String, Object> params = (Map<String, Object>) paramsObj;
        Map<String, String> data = (Map<String, String>) dataSource;
        Calendar dateAt = (Calendar) params.get("dateAt");
        Calendar dateTo = (Calendar) params.get("dateTo");
        data.clear();

        Session session = (Session) sessionObj;
        try {
            Map<String, String> paymentsCount = new HashMap<String, String>();
            Map<String, String> discountsCount = new HashMap<String, String>();
            Map<String, String> eventsCount = new HashMap<String, String>();

            parseOrgsPayments(paymentsCount, sessionObj, paramsObj);
            parseOrgsDiscounts(discountsCount, sessionObj, paramsObj);
            parseOrgsEvents(eventsCount, sessionObj, paramsObj);


            for (String orgName : eventsCount.keySet()) {
                double paymentCount = new Double(paymentsCount.get(orgName) == null ? "0" : paymentsCount.get(orgName));
                double discountCount = new Double(
                        discountsCount.get(orgName) == null ? "0" : discountsCount.get(orgName));
                double eventCount = new Double(eventsCount.get(orgName) == null ? "0" : eventsCount.get(orgName));

                double rating = (eventCount + paymentCount + discountCount) / 3;
                data.put(orgName, "" + new BigDecimal(Math.min(100D, Math.max(0D, rating)))
                        .setScale(1, BigDecimal.ROUND_HALF_DOWN).doubleValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Map<Object[], Long> getClientsCount(Session session) {
        Map<Object[], Long> clientsCount = new HashMap<Object[], Long>();
        String sql = "select cf_orgs.idoforg, officialname, count(distinct cf_clients.idofclient) as cnt "
                + "from cf_orgs "
                + "left join cf_friendly_organization on cf_orgs.idoforg=currentorg "
                + "left join cf_clients on cf_clients.idoforg=idoffriendlyorg or cf_clients.idoforg=currentorg "
                + "where cf_orgs.state<>0 and cf_clients.idOfClientGroup<" + ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue() + " "
                + "group by cf_orgs.idoforg, officialname "
                + "order by cf_orgs.idoforg";
        org.hibernate.Query q = session.createSQLQuery(sql

                /*"select distinct dat.idoforg, dat.officialname, int8(max(dat.cnt)) "
                        + "from (select dat.idoforg, dat.officialname, sum(dat.cnt) as cnt "
                        + "      from (select cf_orgs.idoforg, cf_orgs.officialname, friends.cnt "
                        + "            from cf_orgs "
                        + "            left join (select cf_friendly_organization.friendlyorg, count(cf_clients) as cnt "
                        + "                       from cf_clients, cf_friendly_organization "
                        + "                       where cf_clients.idoforg=cf_friendly_organization.currentorg and cf_clients.idoforg=cf_friendly_organization.friendlyorg "
                        + "                       group by cf_friendly_organization.friendlyorg) as friends on friends.friendlyorg=cf_orgs.idoforg "
                        + "            where cf_orgs.officialname<>'' " + "            union all "
                        + "            select cf_orgs.idoforg, cf_orgs.officialname, friends.cnt "
                        + "            from cf_orgs "
                        + "            left join (select cf_friendly_organization.friendlyorg, count(cf_clients) as cnt "
                        + "                       from cf_clients, cf_friendly_organization "
                        + "                       where cf_clients.idoforg=cf_friendly_organization.currentorg and cf_clients.idoforg<>cf_friendly_organization.friendlyorg "
                        + "                       group by cf_friendly_organization.friendlyorg) as friends on friends.friendlyorg=cf_orgs.idoforg "
                        + "            where cf_orgs.officialname<>'') as dat "
                        + "      group by dat.idoforg, dat.officialname " + "      union "
                        + "      select cf_orgs.idoforg, cf_orgs.officialname, count(cf_clients.idofclient) as cnt "
                        + "      from cf_clients, cf_orgs "
                        + "      where cf_clients.idoforg=cf_orgs.idoforg and cf_orgs.officialname<>'' and cf_orgs.state<>0 "
                        + "      group by cf_orgs.idoforg, cf_orgs.officialname) as dat " + " where dat.cnt<>0 "
                        + "group by dat.idoforg, dat.officialname " + "order by 1"*/);
        log("Clients count :: SQL:__ " + sql);
        List resultList = q.list();/*                       !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!*/
        for (Object entry : resultList) {
            Object e[] = (Object[]) entry;
            clientsCount.put(new Object[]{((BigInteger) e[0]).longValue(), ((String) e[1]).trim()},
                    ((BigInteger) e[2]).longValue());
        }
        return clientsCount;
    }


    public void parseContentsChart(Object dataSource, Object sessionObj, Object paramsObj) {
        Map<String, List<Item>> data = (Map<String, List<Item>>) dataSource;

        List<Item> list = data.get("Соб. Произв.");
        if (list != null) {
            data.put("Собственное производство", list);
            data.remove("Соб. Произв.");
        }

        list = data.get("Напитки уп.");
        if (list != null) {
            data.put("Напитки упакованные", list);
            data.remove("Напитки уп.");
        }

        list = data.get("Молочная прод.");
        if (list != null) {
            data.put("Молочная продукция", list);
            data.remove("Молочная прод.");
        }

        list = data.get("Гор. блюда");
        if (list != null) {
            data.put("Горячие блюда", list);
            data.remove("Гор. блюда");
        }
    }


    public void parseRefillChart(Object dataSource, Object sessionObj, Object paramsObj) {
        Map<String, List<Item>> data = (Map<String, List<Item>>) dataSource;
        List<Item> list = data.get("Банк Москвы");
        data.put("Через Банк Москвы", list);
        data.remove("Банк Москвы");

        list = data.get("Конкорд-ПА");
        data.put("Через терминалы поставщика питания", list);
        data.remove("Конкорд-ПА");

        list = data.get("Сбербанк-Москва");
        data.put("Через Сбербанк-Москва", list);
        data.remove("Сбербанк-Москва");
    }


    public void parseRatingChart(Object dataSource, Object sessionObj, Object paramsObj) {
        Map<String, List<Item>> data = (Map<String, List<Item>>) dataSource;
        for (String k : data.keySet()) {
            List<Item> dat = data.get(k);
            Item it = dat.get(2);
            if (it.value.indexOf("0") == 0) {
                dat.set(2, new Item (it.type, "Нет"));
            } else {
                dat.set(2, new Item (it.type, "Да"));
            }
        }
    }


    public void parseRefillAvgChart(Object dataSource, Object sessionObj, Object paramsObj) {
        Map<String, String> data = (Map<String, String>) dataSource;

        String list = data.get("Конкорд-ПА");
        data.put("Терминалы поставщика питания", list);
        data.remove("Конкорд-ПА");
    }


    public void parse1_4Visitors(Object dataSource, Object sessionObj, Object paramsObj) {
        Map<String, String> data = (Map<String, String>) dataSource;
    }


    public String getRegionByTypeInc(int regionTypeInc) {
        for (String reg : REGIONS_LIST.keySet()) {
            if (REGIONS_LIST.get(reg) == regionTypeInc) {
                return reg;
            }
        }
        return "";
    }


    private void initRegions(Session session) throws Exception {
        try {
            int i = 0;
            REGIONS_LIST = new HashMap<String, Integer>();
            REGIONS_LIST.put("Все округа", i);
            org.hibernate.Query q = session
                    .createSQLQuery("select distinct district from cf_orgs where district <> '' order by district");
            List resultList = q.list();
            for (Object entry : resultList) {
                String n = (String) entry;
                i += 10;
                REGIONS_LIST.put(n, i);
            }
        } catch (Exception e) {
            throw e;
        }
    }


    private void initPayContragents(Session session) throws Exception {
        try {
            PAY_AGENTS_LIST = new ArrayList<Object[]>();
            org.hibernate.Query q = session.createSQLQuery(
                    "select idofcontragent, contragentname from cf_contragents where classid=" + Contragent.PAY_AGENT
                            + " order by idofcontragent");
            List resultList = q.list();
            for (Object entry : resultList) {
                Object[] vals = (Object[]) entry;
                PAY_AGENTS_LIST.add(new Object[]{((BigInteger) vals[0]).intValue(), (String) vals[1]});
            }
        } catch (Exception e) {
            logger.error("Failed to load pay agents from database", e);
            throw e;
        }
    }

    public int buildPayAgentTypeInc(int idofcontragent) {
        return idofcontragent == 0 ? 0 : PAY_AGENT_MULTI_ID + idofcontragent;
    }


    public void normalizeData (Map <String, List <Item>> data) {
        for (String k : data.keySet()) {
            List <Item> vals = data.get(k);
            for (Type t : TYPES.values()) {
                if (t instanceof SimpleType) {
                    normalizeData(vals, (SimpleType) t);
                } else if (t instanceof ComplexType) {
                    ComplexType ct = (ComplexType) t;
                    Type types[] = ct.getTypes();
                    for (Type t2 : types) {
                        normalizeData (vals, (SimpleType) t2);
                    }
                }
            }
        }
    }

    public void normalizeData (List <Item> vals, SimpleType t) {
        boolean found = false;
        if (vals != null) {
            for (Item i : vals) {
                if (t.getReportType() == i.type) {
                    found = true;
                    break;
                }
            }
        }
        if (!found) {
            vals.add(new Item(t.getReportType(), null));
        }
    }


    public String getValue (List <Item> items, Integer type) {
        if (items == null || type == null) {
            return "";
        }
        for (Item i : items) {
            if (i.type == type) {
                return i.value;
            }
        }
        return "";
    }

    public static class Item {
        public int type;
        public String value;

        public Item (int type, String value) {
            this.type = type;
            this.value = value;
        }
    }

    public void log(String message) {
        logger.info(message);
    }
}