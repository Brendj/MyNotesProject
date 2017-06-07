package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.OrderDetail;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 08.12.16
 * Time: 14:42
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("singleton")
public class SummaryDownloadMakerService extends SummaryDownloadBaseService {
    Logger logger = LoggerFactory.getLogger(SummaryDownloadMakerService.class);
    public static final String FOLDER_PROPERTY = "ecafe.processor.download.summary.folder";
    public static final String NODE = "ecafe.processor.download.summary.node";
    public static final String USER = "ecafe.processor.download.summary.user";
    public static final String PASSWORD = "ecafe.processor.download.summary.password";

    @PersistenceContext(unitName = "reportsPU")
    private EntityManager entityManager;

    protected String getNode() {
        return NODE;
    }

    public void run(Date startDate, Date endDate) throws RuntimeException {
        logger.info("Start make summary report file for download");
        try {
            String filename = RuntimeContext.getInstance().getPropertiesValue(FOLDER_PROPERTY, null);
            if (filename == null) {
                throw new Exception(String.format("Not found property %s in application config", FOLDER_PROPERTY));
            }
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
            filename += "/" + df.format(endDate) + ".csv";

            String str_query = "select org.idoforg, org.shortname, coalesce(od.menudetailname, '') as menudetailname, "
                    + "coalesce(od.menuoutput, '') as menuoutput, coalesce(od.menugroup, '') as menugroup, od.menuorigin, "
                    + "coalesce(od.itemcode, '') as itemcode, coalesce(od.manufacturer, '') as manufacturer, "
                    + "sum(od.qty) as amount, sum(od.qty * od.rprice) as summa, sum(od.qty * od.discount) as discount, od.menutype, od.rprice, od.discount as detail_discount "
                    + "from cf_orgs org inner join cf_orders o on org.idoforg = o.idoforg "
                    + "inner join cf_orderdetails od on o.idoforg = od.idoforg and o.idoforder = od.idoforder "
                    + "where o.orderdate between :startDate and :endDate and o.state = 0 "
                    + "group by org.idoforg, org.shortname, od.menugroup, od.menuorigin, od.menudetailname, od.menutype, "
                    + "od.itemcode, od.manufacturer, od.menuoutput, od.rprice, od.discount "
                    + "order by org.idoforg, org.shortname, "
                    + "case when od.menutype <> 0 and od.menutype < 100 then (od.menutype + 100)*10 when od.menutype = 0 then 2000 else od.menutype*10+1 end, "
                    + "od.menudetailname";
            Query query = entityManager.createNativeQuery(str_query);
            query.setParameter("startDate", startDate.getTime());
            query.setParameter("endDate", endDate.getTime());
            List list = query.getResultList();
            String complex = "";
            List<String> result = new ArrayList<String>();
            result.add("Ид ОО;Название ОО;Наименование блюда;Комплекс;Код товара;Производитель;Группа меню;Вид производства;Выход порции;Количество;Сумма;Сумма скидки;Цена (за единицу) с учетом скидки;Скидка (на единицу);Тип меню");
            for (Object o : list) {
                Object row[] = (Object[]) o;
                int menuType = (Integer) row[11];
                if (menuType > 0 && menuType < 100) {
                    complex = (String) row[2];
                }
                StringBuilder b = new StringBuilder();
                b.append(((BigInteger) row[0]).longValue()).append(";");                    //ид. орг
                b.append((String) row[1]).append(";");                                      //название орг
                b.append((String) row[2]).append(";");                                      //Наименование блюда
                if (menuType == 0)
                    b.append("Буфет").append(";");                                          //Комплекс = Буфет для menuType=0
                else if (menuType < 100)
                    b.append(";");                                                          //Комплекс=пусто для самого комплекса
                else b.append(complex).append(";");                                         //Комплекс
                b.append((String) row[6]).append(";");                                      //Код товара
                b.append((String) row[7]).append(";");                                      //Производитель
                b.append((String) row[4]).append(";");                                      //Группа меню
                b.append(OrderDetail.getMenuOriginAsString((Integer) row[5])).append(";");  //Вид производства
                b.append((String) row[3]).append(";");                                      //Выход порции
                b.append(((BigInteger) row[8]).longValue()).append(";");                    //Количество
                Long sum = ((BigDecimal) row[9]).longValue();
                if (menuType < 100)
                    b.append(CurrencyStringUtils.copecksToRubles(sum, 1)).append(";");      //Сумма без льгот
                else b.append(";");
                Long sumDiscount = ((BigDecimal) row[10]).longValue();
                if (menuType < 100)
                    b.append(CurrencyStringUtils.copecksToRubles(sumDiscount, 1)).append(";");          //Сумма скидки
                else b.append(";");
                Long rPrice = ((BigInteger) row[12]).longValue();
                if (menuType < 100)
                    b.append(CurrencyStringUtils.copecksToRubles(rPrice, 1)).append(";");       //Цена за единицу с учетом скидки
                else b.append(";");
                Long discount = ((BigInteger) row[13]).longValue();
                if (menuType < 100)
                    b.append(CurrencyStringUtils.copecksToRubles(discount, 1)).append(";");                 //Скидка на единицу
                else b.append(";");
                b.append(menuType);                                                              //Тип меню
                result.add(b.toString());
            }
            File file = new File(filename);
            FileUtils.writeLines(file, result);
        } catch (Exception e) {
            logger.error("Error build summary purchase file", e);
            throw new RuntimeException(e.getMessage());
        }
    }

}
