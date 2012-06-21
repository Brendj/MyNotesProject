/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.productGuide;

import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.ProductGuide;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.ConfigurationProviderMenu;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import javax.faces.model.SelectItem;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 10.05.12
 * Time: 9:39
 * To change this template use File | Settings | File Templates.
 */
public class ProductGuideListPage extends BasicWorkspacePage {



    private List<Item> items = Collections.emptyList();
    //private LinkedList<Item> editedItems = new LinkedList<Item>();
    private final ConfigurationProviderMenu configurationProviderMenu = new ConfigurationProviderMenu();
    private ConfigurationProvider cp;
    public  static String[] showDeletedComboText = {"Скрыть", "Показать"};
    private String showDeletedSelectedText = showDeletedComboText[1];
    private ShowDeletedComboMenu showDeletedComboMenu = new ShowDeletedComboMenu();

    public ShowDeletedComboMenu getShowDeletedComboMenu() {
        return showDeletedComboMenu;
    }

    public String getShowDeletedSelectedText() {
        return showDeletedSelectedText;
    }

    public void setShowDeletedSelectedText(String showDeletedSelectedText) {
        this.showDeletedSelectedText = showDeletedSelectedText;
    }

    public boolean isShowDeleted() {
        return showDeletedSelectedText.equals(showDeletedComboText[1]);
    }

    public ConfigurationProviderMenu getConfigurationProviderMenu() {
        return configurationProviderMenu;
    }

    public String getPageFilename() {
        return "report/product_guide/list";
    }

    public String getPageTitle() {
        return super.getPageTitle() + String.format(" (%d)", items.size());
    }

    public List<Item> getItems() {
        return items;
    }

    public void fillConfigurationProviderComboBox(Session persistenceSession) throws Exception {
        configurationProviderMenu.readAllItems(persistenceSession);
        if (MainPage.getSessionInstance().getCurrentConfigurationProvider()==null &&
                configurationProviderMenu.getItems() != null && configurationProviderMenu.getItems().length > 0) {
            MainPage.getSessionInstance().setCurrentConfigurationProvider(configurationProviderMenu.getItems()[0].getLabel());
        }
    }

    public void fill(Session persistenceSession, String configurationProvider) throws Exception{
        //List list = DAOUtils.findProductGuide(persistenceSession);
        //selectItems = ConfigurationProviderMenu.readAllItems(persistenceSession);
        //if (selectItems != null && selectItems.length > 0) {
            //MainPage.getSessionInstance().setCurrentConfigurationProvider((ConfigurationProvider)selectItems[0].getValue());
        //ConfigurationProvider cp = MainPage.getSessionInstance().getCurrentConfigurationProvider();
        cp = (ConfigurationProvider)DAOUtils.findConfigurationProvider(persistenceSession, configurationProvider);
        if (cp != null) {
            Set set = cp.getProducts();
            items = new ArrayList<Item>();
            for (Object o : set) {
                ProductGuide pg = (ProductGuide)o;
                if (!isShowDeleted() && pg.isDeleted())
                    continue;
                if (pg.getUserCreate()!=null)
                    pg.getUserCreate().getUserName();
                if (pg.getUserEdit()!=null)
                    pg.getUserEdit().getUserName();
                if (pg.getUserDelete()!=null)
                    pg.getUserDelete().getUserName(); // обращаемся к полю, чтобы оно загрузилось из бд хибернейтом
                items.add(new Item(pg.getIdOfProductGuide(), pg.getCode(), pg.getFullName(), pg.getProductName(),
                        pg.getOkpCode(), pg.getUserCreate(), pg.getUserEdit(), pg.getUserDelete(), pg.getCreateTime(),
                        pg.getEditTime(), pg.getDeleteTime(), pg.isDeleted(), pg.getIdOfConfigurationProvider()));
            }
        }
        //if (cp != null) {
        //    //Set set = cp.getProducts();
        //    items = new ArrayList<Item>();
        //    List list = DAOUtils.findProductGuideByConfigurationProvider(persistenceSession, cp.getIdOfConfigurationProvider());
        //    for (Object o : list) {
        //        ProductGuide pg = (ProductGuide)o;
        //        if (pg.getUserDelete()!=null)
        //            pg.getUserDelete().getUserName(); // обращаемся к полю, чтобы оно загрузилось из бд хибернейтом
        //        items.add(new Item(pg.getIdOfProductGuide(), pg.getCode(), pg.getFullName(), pg.getProductName(),
        //                pg.getOkpCode(), pg.getUserCreate(), pg.getUserEdit(), pg.getUserDelete(), pg.getCreateTime(),
        //                pg.getEditTime(), pg.getDeleteTime(), pg.isDeleted()));
        //    }
        //}
    }

    public void remove(Session session, String configurationProvider, long id) throws Exception {
        ProductGuide productGuide = (ProductGuide)session.load(ProductGuide.class, id);
        cp = (ConfigurationProvider)DAOUtils.findConfigurationProvider(session, configurationProvider);
        cp.getProducts().remove(productGuide);
        session.update(cp);
        session.delete(productGuide);
        fill(session, MainPage.getSessionInstance().getCurrentConfigurationProvider());
    }

    public void setEdited(Long id) {
        for (Item item : this.getItems()) {
            if ((id==Item.NOT_SAVED_IN_DB_ID) || (item.getIdOfProductGuide().equals(id)))
                //editedItems.add(item);
            item.setEdited(true);
        }
    }

    public void updateProducts(Session persistenceSession, User user, String configurationProvider) throws Exception {
        for (Item item : items) {
            update(persistenceSession, user, configurationProvider, item);
        }
        //editedItems = new LinkedList<Item>();
        fill(persistenceSession, configurationProvider);
    }

    private void update(Session persistenceSession, User user, String configurationProvider, Item item) throws Exception{
        //ProductGuide pg = (ProductGuide) DAOUtils.findProductGuide(persistenceSession, item.getIdOfProductGuide());
        //ConfigurationProvider newCp = (ConfigurationProvider)DAOUtils.findConfigurationProvider(persistenceSession, configurationProvider);
        //if (newCp!=null && !newCp.getIdOfConfigurationProvider().equals(item.getIdofconfigurationprovider())) {
        //    ConfigurationProvider oldCp = (ConfigurationProvider)DAOUtils.findConfigurationProvider(persistenceSession, item.getIdOfProductGuide());
        //    if (oldCp!=null) {
        //        oldCp.getProducts().remove(pg);
        //        persistenceSession.update(oldCp);
        //    }
        //    newCp.getProducts().add(pg);
        //    persistenceSession.update(newCp);
        //}
        ConfigurationProvider newCp = (ConfigurationProvider)DAOUtils.findConfigurationProvider(persistenceSession, configurationProvider);

        if (newCp == null)
            throw new Exception(String.format("Не найдена указанная конфигурация поставщика = %s", configurationProvider));

        if (StringUtils.isEmpty(item.getCode()))
            throw new ProductGuideException("Код продукта не должен быть пустым.");

        if (StringUtils.isEmpty(item.getFullName()))
            throw new ProductGuideException("Полное наименоваение продукта не должно быть пустым.");

        if (StringUtils.isEmpty(item.getProductName()))
            throw new ProductGuideException("Товарное название продукта не должно быть пустым.");



        ProductGuide pg = null;
        Long id = item.getIdOfProductGuide();
        if (id == Item.NOT_SAVED_IN_DB_ID)
            id = null;
        if (id!=null) {

            int count = 0;
            for (Item i :this.getItems()) {
                if (i.getCode().equals(item.getCode()))
                    count++;
            }
            if (count > 1)
                throw new ProductGuideException(String.format("Продуктов с кодом %s больше одного.", item.getCode()));

            pg = (ProductGuide) DAOUtils.findProductGuide(persistenceSession, id);

            if (!newCp.getIdOfConfigurationProvider().equals(item.getIdofconfigurationprovider())) {
                ConfigurationProvider oldCp = (ConfigurationProvider)DAOUtils.findConfigurationProvider(persistenceSession, id);
                if (oldCp!=null) {
                    oldCp.getProducts().remove(pg);
                    persistenceSession.update(oldCp);
                }
                newCp.getProducts().add(pg);
                persistenceSession.update(newCp);
            }
            pg.setEditTime(new Date());
            pg.setUserEdit(user);
        } else {
            pg = new ProductGuide();
            newCp.getProducts().add(pg);
            persistenceSession.update(newCp);
            pg.setUserCreate(user);
            pg.setCreateTime(new Date());
        }

        pg.setCode(item.getCode());
        pg.setFullName(item.getFullName());
        pg.setProductName(item.getProductName());
        pg.setOkpCode(item.getOkpCode());
        pg.setDeleted(item.isDeleted());
        if (item.isDeleted()) {
            pg.setDeleteTime(new Date());
            pg.setUserDelete(user);
        } else {
            pg.setDeleteTime(null);
            pg.setUserDelete(null);
        }

        persistenceSession.saveOrUpdate(pg);
        item.setEdited(false);
    }

    public void insert() {
        Item item = new Item();
        item.setIdOfProductGuide(Item.NOT_SAVED_IN_DB_ID);
        this.getItems().add(item);
        //this.editedItems.add(item);
    }

    public String[] getShowDeletedComboText() {
        return showDeletedComboText;
    }

    public class ShowDeletedComboMenu {

        private SelectItem[] items;

        public ShowDeletedComboMenu() {
            items = new SelectItem[2];
            items[0] = new SelectItem(ProductGuideListPage.showDeletedComboText[0]);
            items[1] = new SelectItem(ProductGuideListPage.showDeletedComboText[1]);
        }

        public SelectItem[] getItems() {
            return items;
        }
    }

    public class ProductGuideException extends Exception {
        public ProductGuideException(String message) {
            super(message);
        }
    }
}
