/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.menu;

import ru.axetta.ecafe.processor.core.persistence.MenuDetail;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: damir
 * Date: 13.01.12
 * Time: 20:42
 * To change this template use File | Settings | File Templates.
 */
public class MenuDetailsPage extends BasicWorkspacePage {

    private List<MenuDetailItem> menuDetailItems = Collections.emptyList();
    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public static class MenuDetailItem{
        private Long idOfMenuDetail;
        private String menuDetailName;
        private String groupName;
        private String menuDetailOutput;
        private Long price;
        private Double protein;
        private Double fat;
        private Double carbohydrates;
        private Double calories;
        private Double vitB1;
        private Double vitC;
        private Double vitA;
        private Double vitE;
        private Double minCa;
        private Double minP;
        private Double minMg;
        private Double minFe;
        private int menuOrigin;
        private Long localIdOfMenu;
        private int availableNow;
        private String menuPath;

        public MenuDetailItem(){ }

        public MenuDetailItem(Long idOfMenuDetail, String menuDetailName, String groupName, String menuDetailOutput,
                Long price, Double protein, Double fat, Double carbohydrates, Double calories, Double vitB1,
                Double vitC, Double vitA, Double vitE, Double minCa, Double minP, Double minMg, Double minFe,
                int menuOrigin, Long localIdOfMenu, int availableNow, String menuPath) {
            this.idOfMenuDetail = idOfMenuDetail;
            this.menuDetailName = menuDetailName;
            this.groupName = groupName;
            this.menuDetailOutput = menuDetailOutput;
            this.price = price;
            this.protein = protein;
            this.fat = fat;
            this.carbohydrates = carbohydrates;
            this.calories = calories;
            this.vitB1 = vitB1;
            this.vitC = vitC;
            this.vitA = vitA;
            this.vitE = vitE;
            this.minCa = minCa;
            this.minP = minP;
            this.minMg = minMg;
            this.minFe = minFe;
            this.menuOrigin = menuOrigin;
            this.localIdOfMenu = localIdOfMenu;
            this.availableNow = availableNow;
            this.menuPath = menuPath;
        }

        public Long getIdOfMenuDetail() {
            return idOfMenuDetail;
        }

        public void setIdOfMenuDetail(Long idOfMenuDetail) {
            this.idOfMenuDetail = idOfMenuDetail;
        }

        public String getMenuDetailName() {
            return menuDetailName;
        }

        public void setMenuDetailName(String menuDetailName) {
            this.menuDetailName = menuDetailName;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public String getMenuDetailOutput() {
            return menuDetailOutput;
        }

        public void setMenuDetailOutput(String menuDetailOutput) {
            this.menuDetailOutput = menuDetailOutput;
        }

        public Long getPrice() {
            return price;
        }

        public void setPrice(Long price) {
            this.price = price;
        }

        public Double getProtein() {
            return protein;
        }

        public void setProtein(Double protein) {
            this.protein = protein;
        }

        public Double getFat() {
            return fat;
        }

        public void setFat(Double fat) {
            this.fat = fat;
        }

        public Double getCarbohydrates() {
            return carbohydrates;
        }

        public void setCarbohydrates(Double carbohydrates) {
            this.carbohydrates = carbohydrates;
        }

        public Double getCalories() {
            return calories;
        }

        public void setCalories(Double calories) {
            this.calories = calories;
        }

        public Double getVitB1() {
            return vitB1;
        }

        public void setVitB1(Double vitB1) {
            this.vitB1 = vitB1;
        }

        public Double getVitC() {
            return vitC;
        }

        public void setVitC(Double vitC) {
            this.vitC = vitC;
        }

        public Double getVitA() {
            return vitA;
        }

        public void setVitA(Double vitA) {
            this.vitA = vitA;
        }

        public Double getVitE() {
            return vitE;
        }

        public void setVitE(Double vitE) {
            this.vitE = vitE;
        }

        public Double getMinCa() {
            return minCa;
        }

        public void setMinCa(Double minCa) {
            this.minCa = minCa;
        }

        public Double getMinP() {
            return minP;
        }

        public void setMinP(Double minP) {
            this.minP = minP;
        }

        public Double getMinMg() {
            return minMg;
        }

        public void setMinMg(Double minMg) {
            this.minMg = minMg;
        }

        public Double getMinFe() {
            return minFe;
        }

        public void setMinFe(Double minFe) {
            this.minFe = minFe;
        }

        public int getMenuOrigin() {
            return menuOrigin;
        }

        public void setMenuOrigin(int menuOrigin) {
            this.menuOrigin = menuOrigin;
        }

        public Long getLocalIdOfMenu() {
            return localIdOfMenu;
        }

        public void setLocalIdOfMenu(Long localIdOfMenu) {
            this.localIdOfMenu = localIdOfMenu;
        }

        public int getAvailableNow() {
            return availableNow;
        }

        public void setAvailableNow(int availableNow) {
            this.availableNow = availableNow;
        }

        public String getMenuPath() {
            return menuPath;
        }

        public void setMenuPath(String menuPath) {
            this.menuPath = menuPath;
        }
    }

    public void buildListMenuView(Session session, Long idOfMenu) throws Exception {
        List<MenuDetailItem> items = new LinkedList<MenuDetailItem>();
        Criteria menuDetailCriteria = session.createCriteria(MenuDetail.class);
        menuDetailCriteria.add(Restrictions.eq("menu.idOfMenu",idOfMenu));
        List results = menuDetailCriteria.list();
        this.count = results.size();
        for (Object object : results) {
            MenuDetail menuDetail = (MenuDetail) object;
            MenuDetailItem item= new MenuDetailItem();
            item.setIdOfMenuDetail(menuDetail.getIdOfMenuDetail());
            item.setAvailableNow(menuDetail.getAvailableNow());
            item.setVitE(menuDetail.getVitE());
            item.setCalories(menuDetail.getCalories());
            item.setCarbohydrates(menuDetail.getCarbohydrates());
            item.setFat(menuDetail.getFat());
            item.setGroupName(menuDetail.getGroupName());
            item.setLocalIdOfMenu(menuDetail.getLocalIdOfMenu());
            item.setMenuDetailName(menuDetail.getMenuDetailName());
            item.setMenuDetailOutput(menuDetail.getMenuDetailOutput());
            item.setMenuOrigin(menuDetail.getMenuOrigin());
            item.setMenuPath(menuDetail.getMenuPath());
            item.setMinCa(menuDetail.getMinCa());
            item.setMinFe(menuDetail.getMinFe());
            item.setMinMg(menuDetail.getMinMg());
            item.setMinP(menuDetail.getMinP());
            item.setVitA(menuDetail.getVitA());
            item.setVitB1(menuDetail.getVitB1());
            item.setVitC(menuDetail.getVitC());
            items.add(item);
        }
        this.menuDetailItems=items;
    }

    public List<MenuDetailItem> getMenuDetailItems() {
        return menuDetailItems;
    }

    public void setMenuDetailItems(List<MenuDetailItem> menuDetailItems) {
        this.menuDetailItems = menuDetailItems;
    }

    public String getPageFilename() {
        return "org/menu/details";
    }

}
