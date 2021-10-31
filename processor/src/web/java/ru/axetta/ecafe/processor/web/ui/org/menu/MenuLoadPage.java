/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.menu;


import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.sync.LoadContext;
import ru.axetta.ecafe.processor.core.sync.SyncRequest;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 03.12.12
 * Time: 17:18
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class MenuLoadPage extends BasicWorkspacePage {

    private List<LineResult> lineResults = Collections.emptyList();
    private int successLineNumber;
    private Long idOfOrg;

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public List<LineResult> getLineResults() {
        return lineResults;
    }

    public String showMenuLoadResultCSVList() {
        return "/back-office/menu_load_result_csv_list.jsp";
    }

    public int getLineResultSize() {
        return lineResults.size();
    }

    public int getSuccessLineNumber() {
        return successLineNumber;
    }

    public String getPageFilename() {
        return "org/menu/load_menu";
    }


    public void menuLoadFileListener(FileUploadEvent event) {
        UploadedFile item = event.getUploadedFile();
        InputStream inputStream = null;
        long dataSize = 0;
        try {
            byte[] data = item.getData();
            dataSize = data.length;
            inputStream = new ByteArrayInputStream(data);
            loadMenu(inputStream, dataSize);
            printMessage("Меню загружено");
        } catch (Exception e) {
            printError("Ошибка при загрузке/регистрации данных по меню: ");
            logAndPrintMessage("Error from load menu: ", e);
        } finally {
            close(inputStream);
        }
    }

    public void loadMenu(InputStream inputStream, long dataSize) throws Exception {
        lineResults = new ArrayList<LineResult>();
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(inputStream);


        TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
        DateFormat dateOnlyFormat = new SimpleDateFormat("dd.MM.yyyy");
        dateOnlyFormat.setTimeZone(utcTimeZone);

        TimeZone localTimeZone = TimeZone.getTimeZone("Europe/Moscow");
        DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        timeFormat.setTimeZone(localTimeZone);

        SyncRequest.MenuGroups menuGroups = new SyncRequest.MenuGroups.Builder().build(document);
        LoadContext loadContext = new LoadContext(menuGroups, 5L, timeFormat, dateOnlyFormat);
        SyncRequest.ReqMenu reqMenu = new SyncRequest.ReqMenu.Builder(loadContext).build(document);

        processSyncMenu(idOfOrg, reqMenu);

    }

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    @Autowired
    private DAOService daoService;

    @Transactional
    protected boolean isOrgMenuExchangeSource(Long idOfOrg) {
        TypedQuery<Integer> query = entityManager.createQuery("select 1 from MenuExchangeRule discountrule where discountrule.idOfSourceOrg = :idOfSourceOrg", Integer.class);
        query.setParameter("idOfSourceOrg", idOfOrg);
        query.setMaxResults(1);
        return !query.getResultList().isEmpty();
    }

    @Transactional
    protected Menu findMenu(Org organization, int menuSource, Date menuDate)
            throws Exception {
        Menu menu = null;
        TypedQuery<Menu> query = entityManager.createQuery("from Menu where org=:org and menuSource=:menuSource and menuDate=:menuDate",Menu.class);
        query.setParameter("org", organization);
        query.setParameter("menuSource", menuSource);
        query.setParameter("menuDate", menuDate);
        List<Menu> menuList = query.getResultList();
        if(!(menuList==null || menuList.isEmpty())){
            menu = menuList.get(0);
        }
        return menu;
    }

    @Transactional
    protected void processSyncMenu(Long idOfOrg, SyncRequest.ReqMenu reqMenu) throws Exception {
        if (null != reqMenu) {
            Org organization = entityManager.getReference(Org.class, idOfOrg);

            boolean bOrgIsMenuExchangeSource = isOrgMenuExchangeSource(idOfOrg);

            /// сохраняем секцию Settings
            if (bOrgIsMenuExchangeSource && (reqMenu.getSettingsSectionRawXML() != null)) {
                MenuExchange menuExchangeSettings = new MenuExchange(new Date(0), idOfOrg,
                        reqMenu.getSettingsSectionRawXML(), MenuExchange.FLAG_SETTINGS);
                // persistenceSession.saveOrUpdate(menuExchangeSettings);
                daoService.persistEntity(menuExchangeSettings);
            }

            Iterator<SyncRequest.ReqMenu.Item> menuItems = reqMenu.getItems();
            boolean bFirstMenuItem = true;
            while (menuItems.hasNext()) {
                SyncRequest.ReqMenu.Item item = menuItems.next();
                /// сохраняем данные меню для распространения
                if (bOrgIsMenuExchangeSource) {
                    MenuExchange menuExchange = new MenuExchange(item.getDate(), idOfOrg, item.getRawXmlText(),
                            bFirstMenuItem ? MenuExchange.FLAG_ANCHOR_MENU : MenuExchange.FLAG_NONE);
                    daoService.persistEntity(menuExchange);
                }
                Date menuDate = item.getDate();

                Menu menu = findMenu(organization, Menu.ORG_MENU_SOURCE, menuDate);
                if (null == menu) {
                    menu = new Menu(organization, menuDate, new Date(), Menu.ORG_MENU_SOURCE,
                            bFirstMenuItem ? Menu.FLAG_ANCHOR_MENU : Menu.FLAG_NONE, item.hashCode());
                    daoService.persistEntity(menu);
                }
                processReqAssortment(organization, menuDate, item.getReqAssortments());
                processReqMenuDetails(organization, menuDate, menu, item, item.getReqMenuDetails());
                processReqComplexInfos(organization, menuDate, menu, item.getReqComplexInfos());
                bFirstMenuItem = false;
            }

        }
    }

    @Transactional
    protected MenuDetail findMenuDetailByLocalId(Menu menu, Long localIdOfMenu) {
        MenuDetail menuDetail = null;
        TypedQuery<MenuDetail> q = entityManager.createQuery("FROM MenuDetail WHERE menu=:menu AND localIdOfMenu=:localIdOfMenu",MenuDetail.class);
        q.setParameter("menu", menu);
        q.setParameter("localIdOfMenu", localIdOfMenu);
        List<MenuDetail> menuDetailList = q.getResultList();
        if(!(menuDetailList==null || menuDetailList.isEmpty())){
            menuDetail = menuDetailList.get(0);
        }
        return menuDetail;
    }

    @Transactional
    protected void deleteComplexInfoForDate(Org organization, Date menuDate) {
        Date endDate = DateUtils.addDays(menuDate, 1);
        TypedQuery<ComplexInfo> query = entityManager.createQuery("FROM ComplexInfo WHERE org=:org AND menuDate>=:fromDate AND menuDate<=:endDate", ComplexInfo.class);
        query.setParameter("org", organization);
        query.setParameter("fromDate", menuDate);
        query.setParameter("endDate", endDate);
        List<ComplexInfo> complexInfoList = query.getResultList();
        if(!(complexInfoList==null || complexInfoList.isEmpty())){
            for (ComplexInfo complexInfo: complexInfoList){
                daoService.deleteEntity(complexInfo);
            }
        }
    }

    @Transactional
    protected void processReqComplexInfos(Org organization, Date menuDate, Menu menu,
                                          List<SyncRequest.ReqMenu.Item.ReqComplexInfo> reqComplexInfos) throws Exception {
        deleteComplexInfoForDate(organization, menuDate);

        for (SyncRequest.ReqMenu.Item.ReqComplexInfo reqComplexInfo : reqComplexInfos) {
            ComplexInfo complexInfo = new ComplexInfo(reqComplexInfo.getComplexId(), organization, menuDate,
                    reqComplexInfo.getModeFree(), reqComplexInfo.getModeGrant(), reqComplexInfo.getModeOfAdd(),
                    reqComplexInfo.getComplexMenuName());
            if (reqComplexInfo.getUseTrDiscount() != null) {
                complexInfo.setUseTrDiscount(reqComplexInfo.getUseTrDiscount());
            }
            if (reqComplexInfo.getCurrentPrice() != null) {
                complexInfo.setCurrentPrice(reqComplexInfo.getCurrentPrice());
            }
            SyncRequest.ReqMenu.Item.ReqMenuDetail reqMenuDetail = reqComplexInfo.getReqMenuDetail();
            if (reqMenuDetail != null) {
                MenuDetail menuDetailOptional = findMenuDetailByLocalId(menu,
                        reqComplexInfo.getReqMenuDetail().getIdOfMenu());
                if (menuDetailOptional != null) {
                    complexInfo.setMenuDetail(menuDetailOptional);
                }
            }

            SyncRequest.ReqMenu.Item.ReqComplexInfo.ReqComplexInfoDiscountDetail reqComplexInfoDiscountDetail =
                    reqComplexInfo.getComplexInfoDiscountDetail();
            if (reqComplexInfoDiscountDetail != null) {
                double size = reqComplexInfoDiscountDetail.getSize();
                int isAllGroups = reqComplexInfoDiscountDetail.getIsAllGroups();
                Integer maxCount = reqComplexInfoDiscountDetail.getMaxCount();
                Long idOfClientGroup = reqComplexInfoDiscountDetail.getIdOfClientGroup();
                ComplexInfoDiscountDetail complexInfoDiscountDetail = new ComplexInfoDiscountDetail(size, isAllGroups);
                if (idOfClientGroup != null) {
                    CompositeIdOfClientGroup compId = new CompositeIdOfClientGroup(organization.getIdOfOrg(), idOfClientGroup);
                    ClientGroup clientGroup = entityManager.getReference(ClientGroup.class, compId);
                    complexInfoDiscountDetail.setClientGroup(clientGroup);
                    complexInfoDiscountDetail.setOrg(clientGroup.getOrg());
                }
                if (maxCount != null) {
                    complexInfoDiscountDetail.setMaxCount(maxCount);
                }

                daoService.persistEntity(complexInfoDiscountDetail);
                lineResults.add(new LineResult(0,"OK","ComplexInfoDiscountDetail",complexInfoDiscountDetail.getIdOfDiscountDetail()));
                complexInfo.setDiscountDetail(complexInfoDiscountDetail);
            }
            daoService.persistEntity(complexInfo);
            lineResults.add(new LineResult(0,"OK","ComplexInfoDetail",complexInfo.getIdOfComplexInfo()));

            for (SyncRequest.ReqMenu.Item.ReqComplexInfo.ReqComplexInfoDetail reqComplexInfoDetail : reqComplexInfo
                    .getComplexInfoDetails()) {
                MenuDetail menuDetail = findMenuDetailByLocalId(menu,
                        reqComplexInfoDetail.getReqMenuDetail().getIdOfMenu());
                if (menuDetail == null) {
                    lineResults.add(new LineResult(-1,"MenuDetail not found for complex detail with localIdOfMenu="+ reqComplexInfoDetail
                            .getReqMenuDetail().getIdOfMenu(),"MenuDetail",null));
                    return;
                }
                ComplexInfoDetail complexInfoDetail = new ComplexInfoDetail(complexInfo, menuDetail);
                Long idOfItem = reqComplexInfoDetail.getIdOfItem();
                if (idOfItem != null) {
                    complexInfoDetail.setIdOfItem(idOfItem);
                }
                Integer menuItemCount = reqComplexInfoDetail.getCount();
                if (menuItemCount != null) {
                    complexInfoDetail.setCount(menuItemCount);
                }
                daoService.persistEntity(complexInfoDetail);
                lineResults.add(new LineResult(0,"OK","ComplexInfoDetail",complexInfo.getIdOfComplexInfo()));
            }
        }
    }

    @Transactional
    protected void deleteAssortmentForDate(Org organization, Date menuDate) {
        Date endDate = DateUtils.addDays(menuDate, 1);
        TypedQuery<Assortment> query = entityManager.createQuery("FROM Assortment WHERE org=:org AND beginDate>=:fromDate AND beginDate<=:endDate", Assortment.class);
        query.setParameter("org", organization);
        query.setParameter("fromDate", menuDate);
        query.setParameter("endDate", endDate);
        List<Assortment> assortmentList = query.getResultList();
        if(!(assortmentList==null || assortmentList.isEmpty())){
            for (Assortment assortment: assortmentList){
                daoService.deleteEntity(assortment);
            }
        }
    }

    @Transactional
    protected void processReqAssortment(Org organization, Date menuDate,
                                        List<SyncRequest.ReqMenu.Item.ReqAssortment> reqAssortments)  throws Exception{
        deleteAssortmentForDate(organization, menuDate);
        for (SyncRequest.ReqMenu.Item.ReqAssortment reqAssortment : reqAssortments) {
            Assortment assortment = new Assortment(organization, menuDate, reqAssortment.getName(),
                    reqAssortment.getFullName(), reqAssortment.getGroup(), reqAssortment.getMenuOrigin(),
                    reqAssortment.getMenuOutput(), reqAssortment.getPrice(), reqAssortment.getFat(),
                    reqAssortment.getCarbohydrates(), reqAssortment.getCalories(), reqAssortment.getVitB1(),
                    reqAssortment.getVitC(), reqAssortment.getVitA(), reqAssortment.getVitE(), reqAssortment.getMinCa(),
                    reqAssortment.getMinP(), reqAssortment.getMinMg(), reqAssortment.getMinFe());
            daoService.persistEntity(assortment);
            lineResults.add(new LineResult(0,"OK","Assortment",assortment.getIdOfAst()));
        }
    }

    @Transactional
    protected void processReqMenuDetails(Org organization, Date menuDate, Menu menu,
                                         SyncRequest.ReqMenu.Item item, Iterator<SyncRequest.ReqMenu.Item.ReqMenuDetail> reqMenuDetails)
            throws Exception {
        while (reqMenuDetails.hasNext()) {
            SyncRequest.ReqMenu.Item.ReqMenuDetail reqMenuDetail = reqMenuDetails.next();
            if (null == findMenuDetailByLocalId(menu, reqMenuDetail.getIdOfMenu())) {
                MenuDetail menuDetail = new MenuDetail(menu, reqMenuDetail.getPath(), reqMenuDetail.getName(),
                        reqMenuDetail.getMenuOrigin(), reqMenuDetail.getAvailableNow(),
                        reqMenuDetail.getFlags());
                menuDetail.setLocalIdOfMenu(reqMenuDetail.getIdOfMenu());
                menuDetail.setGroupName(reqMenuDetail.getGroup());
                menuDetail.setMenuDetailOutput(reqMenuDetail.getOutput());
                menuDetail.setPrice(reqMenuDetail.getPrice());
                menuDetail.setPriority(reqMenuDetail.getPriority());
                menuDetail.setProtein(reqMenuDetail.getProtein());
                menuDetail.setFat(reqMenuDetail.getFat());
                menuDetail.setCarbohydrates(reqMenuDetail.getCarbohydrates());
                menuDetail.setCalories(reqMenuDetail.getCalories());
                menuDetail.setVitB1(reqMenuDetail.getVitB1());
                menuDetail.setVitC(reqMenuDetail.getVitC());
                menuDetail.setVitA(reqMenuDetail.getVitA());
                menuDetail.setVitE(reqMenuDetail.getVitE());
                menuDetail.setMinCa(reqMenuDetail.getMinCa());
                menuDetail.setMinP(reqMenuDetail.getMinP());
                menuDetail.setMinMg(reqMenuDetail.getMinMg());
                menuDetail.setMinFe(reqMenuDetail.getMinFe());
                menuDetail.setVitB2(reqMenuDetail.getVitB2());
                menuDetail.setVitPp(reqMenuDetail.getVitPp());

                //persistenceSession.save(menuDetail);
                //menu.addMenuDetail(menuDetail);
                daoService.persistEntity(menuDetail);
                lineResults.add(new LineResult(0,"OK","MenuDetail",menuDetail.getIdOfMenuDetail()));
            }
        }

        // Ищем "лишние" элементы меню
        List<MenuDetail> superfluousMenuDetails = new LinkedList<MenuDetail>();
        menu = entityManager.merge(menu);
        for (MenuDetail menuDetail : menu.getMenuDetails()) {
            if (!find(menuDetail, item)) {
                superfluousMenuDetails.add(menuDetail);
            }
        }
        // Удаляем "лишние" элементы меню
        for (MenuDetail menuDetail : superfluousMenuDetails) {
            menu.removeMenuDetail(menuDetail);
            //persistenceSession.delete(menuDetail);
            daoService.deleteEntity(menuDetail);
            lineResults.remove(new LineResult(0,"OK","MenuDetail",menuDetail.getIdOfMenuDetail()));
        }

    }

    @Transactional
    protected boolean find(MenuDetail menuDetail, SyncRequest.ReqMenu.Item menuItem) throws Exception {
        Iterator<SyncRequest.ReqMenu.Item.ReqMenuDetail> reqMenuDetails = menuItem.getReqMenuDetails();
        while (reqMenuDetails.hasNext()) {
            SyncRequest.ReqMenu.Item.ReqMenuDetail reqMenuDetail = reqMenuDetails.next();
            Long localIdOfMenu = menuDetail.getLocalIdOfMenu();
            // если есть локальный ID то ищем по нему, если нет - то по имени
            if ((localIdOfMenu != null && reqMenuDetail.getIdOfMenu() != null && (localIdOfMenu
                    .equals(reqMenuDetail.getIdOfMenu()))) || (localIdOfMenu == null && StringUtils
                    .equals(menuDetail.getMenuDetailName(), reqMenuDetail.getName()))) {
                return true;
            }
        }
        return false;
    }

    private void close(InputStream inputStream) {
        if (null != inputStream) {
            try {
                inputStream.close();
            } catch (Exception e) {
                getLogger().error("failed to close input stream", e);
            }
        }
    }

    private static Node findFirstChildElement(Node node, String name) throws Exception {
        Node currNode = node.getFirstChild();
        while (null != currNode) {
            if (Node.ELEMENT_NODE == currNode.getNodeType() && currNode.getNodeName().equals(name)) {
                return currNode;
            }
            currNode = currNode.getNextSibling();
        }
        return null;
    }

    public static class LineResult {
        private final int resultCode;
        private final String message;
        private final String description;
        private final Long id;

        public LineResult( int resultCode, String message, String description, Long id) {
            this.resultCode = resultCode;
            this.message = message;
            this.description=description;
            this.id = id;
        }

        public int getResultCode() {
            return resultCode;
        }

        public String getMessage() {
            return message;
        }

        public String getDescription() {
            return description;
        }

        public Long getId() {
            return id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            LineResult that = (LineResult) o;

            if (resultCode != that.resultCode) {
                return false;
            }
            if (!description.equals(that.description)) {
                return false;
            }
            if (!id.equals(that.id)) {
                return false;
            }
            if (!message.equals(that.message)) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = resultCode;
            result = 31 * result + message.hashCode();
            result = 31 * result + description.hashCode();
            result = 31 * result + id.hashCode();
            return result;
        }
    }

}
