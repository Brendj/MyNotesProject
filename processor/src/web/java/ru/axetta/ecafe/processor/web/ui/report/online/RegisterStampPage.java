package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.daoservices.order.OrderDetailsDAOService;
import ru.axetta.ecafe.processor.core.daoservices.order.items.GoodItem;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;
import ru.axetta.ecafe.processor.web.ui.org.OrgShortItem;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 30.04.13
 * Time: 15:52
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class RegisterStampPage extends BasicWorkspacePage implements OrgSelectPage.CompleteHandler{

    private final static Logger logger = LoggerFactory.getLogger(RegisterStampPage.class);

    @PersistenceContext
    private EntityManager entityManager;
    private OrgShortItem org;
    private Date start;
    private Date end;
    private final OrderDetailsDAOService service = new OrderDetailsDAOService();
    private List<RegisterStampPageItem> pageItems = new ArrayList<RegisterStampPageItem>();
    private List<GoodItem> allGoods = new ArrayList<GoodItem>();

    public List<GoodItem> getAllGoods() {
        return allGoods;
    }

    @Override
    public void onShow() throws Exception {
        service.setSession((Session) entityManager.getDelegate());
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,1);
        end = calendar.getTime();
        calendar.add(Calendar.MONTH, -1);
        start = calendar.getTime();
        clear();
    }

    public Object clear(){
        org = null;
        pageItems = new ArrayList<RegisterStampPageItem>();
        allGoods = new ArrayList<GoodItem>();
        return null;
    }

    public Object reload(){
        try {
            pageItems = new ArrayList<RegisterStampPageItem>();
            refresh();
        } catch (Exception e){
            logger.error("Error by reload data by RegisterStamp (OrderDetails  + Goods): ", e);
            printError("Ошибка при загрузке данных по реестру талонов");
        }
        return null;
    }

    @Override
    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        if (null != idOfOrg) {
            Org organization = (Org) session.load(Org.class, idOfOrg);
            org = new OrgShortItem();
            org.setIdOfOrg(organization.getIdOfOrg());
            org.setOfficialName(organization.getOfficialName());
            org.setShortName(organization.getShortName());
            List<GoodItem> goods = service.findAllGoods(organization.getIdOfOrg());
            Set<GoodItem> goodItemSet = new TreeSet<GoodItem>();
            for (GoodItem item: goods){
                goodItemSet.add(item);
            }
            allGoods = new ArrayList<GoodItem>(goodItemSet);
            Collections.reverse(allGoods);
        }
    }

    private void refresh() throws Exception {
        Calendar calendar = Calendar.getInstance();
        DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy");
        calendar.setTime(start);
        RegisterStampPageItem total = new RegisterStampPageItem(allGoods);
        RegisterStampPageItem allTotal = new RegisterStampPageItem(allGoods);
        total.date = "Итого";
        allTotal.date = "Всего кол-во:";
        do{
            String date = timeFormat.format(calendar.getTime());
            RegisterStampPageItem item = new RegisterStampPageItem(allGoods);
            item.date = date;
            for (String l: item.map.keySet()){
                Long val = service.findNotNullGoodsFullNameByOrgByDayAndGoodEq(org.getIdOfOrg(),calendar.getTime(), l);
                item.map.put(l,val+item.map.get(l));
                total.map.put(l, val + total.map.get(l));
                allTotal.map.put(l, val + allTotal.map.get(l));
            }
            pageItems.add(item);
            calendar.add(Calendar.DATE,1);
        } while (!end.equals(calendar.getTime()));
        pageItems.add(total);
        RegisterStampPageItem dailySampleItem = new RegisterStampPageItem(allGoods);
        dailySampleItem.date = "Суточная проба";
        for (String l: dailySampleItem.map.keySet()){
            Long val = service.findNotNullGoodsFullNameByOrgByDailySampleAndGoodEq(org.getIdOfOrg(),start, end, l);
            dailySampleItem.map.put(l, val + dailySampleItem.map.get(l));
            allTotal.map.put(l, val + allTotal.map.get(l));
        }
        pageItems.add(dailySampleItem);
        pageItems.add(allTotal);
    }

    public List<RegisterStampPageItem> getPageItems() {
        return pageItems;
    }

    public void setPageItems(List<RegisterStampPageItem> pageItems) {
        this.pageItems = pageItems;
    }

    public OrgShortItem getOrg() {
        return org;
    }

    public void setOrg(OrgShortItem org) {
        this.org = org;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    @Override
    public String getPageFilename() {
        return "report/online/registerstamp_report";
    }

    public int getGoodSetCount() {
        return allGoods.size();
    }

    public static class RegisterStampPageItem{

        private String date;
        private HashMap<String,Long> map = new HashMap<String, Long>();

        public RegisterStampPageItem(List<GoodItem> goods) {
            for (GoodItem good: goods){
                map.put(good.getPathPart4(),0L);
            }
        }

        public List<String> getSetKey(){
            return new ArrayList<String>(map.keySet());
        }

        public Long getValue(String id) {
            return map.get(id);
        }

        public String getDate() {
            return date;
        }


    }
}
