/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.admin;

import ru.axetta.ecafe.processor.core.SpringApplicationContext;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: timur
 * Date: 20.08.12
 * Time: 16:10
 * To change this template use File | Settings | File Templates.
 */
public class AdminPage {
    private List<CityItem> cityItems;

    public Object addCity(){
       CityPage cityPage=(CityPage) SpringApplicationContext.getBean("cityPage");

        return cityPage.addCity();

    }

    public List<CityItem> getCityItems() {
        CityPage cityPage=(CityPage) SpringApplicationContext.getBean("cityPage");
        return cityPage.getCityItems();
    }

    public void setCityItems(List<CityItem> cityItems) {
        CityPage cityPage=(CityPage) SpringApplicationContext.getBean("cityPage");
      //
      // cityPage.setCityItems(cityItems);
        this.cityItems = cityItems;
    }
}
