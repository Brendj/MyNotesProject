package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRCsvExporterParameter;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.daoservices.order.OrderDetailsDAOService;
import ru.axetta.ecafe.processor.core.daoservices.order.items.GoodItem;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicJasperReport;
import ru.axetta.ecafe.processor.core.report.RegisterStampReport;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;
import ru.axetta.ecafe.processor.web.ui.org.OrgShortItem;
import ru.axetta.ecafe.processor.web.ui.report.online.items.stamp.RegisterStampPageItem;
import ru.axetta.ecafe.processor.web.ui.report.online.items.stamp.Tree;
import ru.axetta.ecafe.processor.web.ui.report.online.items.stamp.Visitor;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
public class RegisterStampPage extends BasicWorkspacePage implements OrgSelectPage.CompleteHandler, Visitor<String> {

    private final static Logger logger = LoggerFactory.getLogger(RegisterStampPage.class);

    @Override
    public Visitor<String> visitTree(Tree<String> tree) {
        return new RegisterStampPage(lvl1, lvl2, lvlBottom);
    }

    @Override
    public void visitData(Tree<String> parent, String data) {
        int level = parent.getLevel();
        switch (level){
            case 3: lvl1.add(new HashMap.SimpleImmutableEntry<String, Tree>(data,parent)); break;
            case 4: lvl2.add(new HashMap.SimpleImmutableEntry<String, Tree>(data,parent)); break;
        }

        if(parent.getChildCount()<1){
            lvlBottom.add(new HashMap.SimpleImmutableEntry<String, Tree>(data, parent));
        }
    }

    public Integer getLastLvlElements(){
        int size = 0;
        for (Map.Entry<String,Tree> entry: lvl1){
            if(entry.getValue().getChildCount()==0) size++;
        }
        return lvl2.size() + size;
    }

    public RegisterStampPage() {}

    public RegisterStampPage(List<Map.Entry<String, Tree>> lvl1, List<Map.Entry<String, Tree>> lvl2,
            List<Map.Entry<String, Tree>> lvlBottom) {
        this.lvl1 = lvl1;
        this.lvl2 = lvl2;
        this.lvlBottom = lvlBottom;
    }

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private RuntimeContext runtimeContext;
    @Autowired
    private DAOService daoService;

    private OrgShortItem org;
    private Date start;
    private Date end;
    private final OrderDetailsDAOService service = new OrderDetailsDAOService();
    private List<RegisterStampPageItem> pageItems = new ArrayList<RegisterStampPageItem>();
    private List<GoodItem> allGoods = new LinkedList<GoodItem>();

    private List<Map.Entry<String,Tree>> lvl1 = new ArrayList<Map.Entry<String, Tree>>();
    private List<Map.Entry<String,Tree>> lvl2 = new ArrayList<Map.Entry<String, Tree>>();
    private List<Map.Entry<String,Tree>> lvlBottom = new ArrayList<Map.Entry<String, Tree>>();
    protected Calendar localCalendar;

    @Override
    public void onShow() throws Exception {
        service.setSession((Session) entityManager.getDelegate());

        FacesContext facesContext = FacesContext.getCurrentInstance();
        localCalendar = runtimeContext
                .getDefaultLocalCalendar((HttpSession) facesContext.getExternalContext().getSession(false));

        localCalendar.setTime(new Date());
        this.start = DateUtils.truncate(localCalendar, Calendar.MONTH).getTime();

        localCalendar.setTime(this.start);
        localCalendar.add(Calendar.MONTH, 1);
        //localCalendar.add(Calendar.SECOND, -1);
        this.end = localCalendar.getTime();
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

    public List<Map.Entry<String, Tree>> getLvl1() {
        return lvl1;
    }

    public List<Map.Entry<String, Tree>> getLvl2() {
        return lvl2;
    }

    public List<Map.Entry<String, Tree>> getLvlBottom() {
        return lvlBottom;
    }

    public void showCSVList(ActionEvent actionEvent){
        AutoReportGenerator autoReportGenerator = runtimeContext.getAutoReportGenerator();
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + RegisterStampReport.class.getSimpleName() + ".jasper";
        RegisterStampReport.Builder builder = new RegisterStampReport.Builder(templateFilename);
        builder.setOrg(daoService.getOrg(this.org.getIdOfOrg()));
        Session session = (Session) entityManager.getDelegate();
        try {
            RegisterStampReport registerStampReport = (RegisterStampReport) builder.build(session,start, end, localCalendar);

            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

            ServletOutputStream servletOutputStream = response.getOutputStream();

            facesContext.responseComplete();
            response.setContentType("application/csv");
            response.setHeader("Content-disposition", "inline;filename=register_stamp.csv");

            JRCsvExporter csvExporter = new JRCsvExporter();
            csvExporter.setParameter(JRCsvExporterParameter.JASPER_PRINT, registerStampReport.getPrint());
            csvExporter.setParameter(JRCsvExporterParameter.OUTPUT_STREAM, servletOutputStream);
            csvExporter.setParameter(JRCsvExporterParameter.FIELD_DELIMITER, ";");
            csvExporter.setParameter(JRCsvExporterParameter.CHARACTER_ENCODING, "windows-1251");
            csvExporter.exportReport();

            servletOutputStream.flush();
            servletOutputStream.close();

        } catch (JRException fnfe) {
            //String message = (fnfe.getCause()==null?fnfe.getMessage():fnfe.getCause().getMessage());
            logAndPrintMessage("Ошибка при подготовке отчета:",fnfe);
        } catch (Exception e) {
            logAndPrintMessage("Error generate csv file ",e);
        }
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
            allGoods = goods;
            Tree<String> forest = new Tree<String>("Количество", 0, null);
            Tree<String> current = forest;
            List<String> namesList = new ArrayList<String>();
            lvl1 = new ArrayList<Map.Entry<String, Tree>>();
            lvl2 = new ArrayList<Map.Entry<String, Tree>>();
            lvlBottom = new ArrayList<Map.Entry<String, Tree>>();
            for(GoodItem item: goods){
                namesList.add(item.getFullName());
            }
            for (String tree : namesList) {
                Tree<String> root = current;
                for (String data : tree.split("/")) {
                    current = current.child(data, tree);
                }
                current = root;
            }
            forest.accept(this);
        }
    }

    private void refresh() throws Exception {
        DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy");
        localCalendar.setTime(start);
        RegisterStampPageItem total = new RegisterStampPageItem("Итого", allGoods);
        RegisterStampPageItem allTotal = new RegisterStampPageItem("Всего кол-во:", allGoods);
        while (end.getTime()>localCalendar.getTimeInMillis()){
            String date = timeFormat.format(localCalendar.getTime());
            RegisterStampPageItem item = new RegisterStampPageItem(date, allGoods);
            for (String l: item.getSetKey()){
                Long val = service.findNotNullGoodsFullNameByOrgByDayAndGoodEq(org.getIdOfOrg(),localCalendar.getTime(), l);
                item.addValue(l, val);
                total.addValue(l, val);
                allTotal.addValue(l, val);
            }
            pageItems.add(item);
            localCalendar.add(Calendar.DATE,1);
        }
        pageItems.add(total);
        RegisterStampPageItem dailySampleItem = new RegisterStampPageItem("Суточная проба", allGoods);
        for (String l: dailySampleItem.getSetKey()){
            Long val = service.findNotNullGoodsFullNameByOrgByDailySampleAndGoodEq(org.getIdOfOrg(),start, end, l);
            dailySampleItem.addValue(l, val);
            allTotal.addValue(l, val);
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
        localCalendar.setTime(end);
        localCalendar.add(Calendar.DAY_OF_MONTH,1);
        localCalendar.add(Calendar.SECOND, -1);
        this.end = localCalendar.getTime();
    }

    @Override
    public String getPageFilename() {
        return "report/online/registerstamp_report";
    }

}
