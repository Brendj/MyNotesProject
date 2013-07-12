/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.basic.good;

import ru.axetta.ecafe.processor.core.persistence.GoodsBasicBasket;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.abstractpage.AbstractFilter;
import ru.axetta.ecafe.processor.web.ui.abstractpage.AbstractListPage;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 11.07.13
 * Time: 18:10
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class BasicGoodListPage extends AbstractListPage<GoodsBasicBasket, BasicGoodItem> {

    private BasicWorkspacePage groupPage = new BasicWorkspacePage();
    private List<GoodsBasicBasket> basicGoodList;
    private BasicGoodFilter basicGoodFilter = new BasicGoodFilter();

    @Override
    protected String getPageFileName() {
        return "commodity_accounting/configuration_provider/basicGood/list";
    }

    @Override
    protected Class<GoodsBasicBasket> getEntityClass() {
        return GoodsBasicBasket.class;
    }

    @Override
    protected BasicGoodItem createItem() {
        return new BasicGoodItem();
    }

    @Override
    protected String getSortField() {
        return "idOfBasicGood";
    }

    @Override
    public BasicGoodFilter getFilter() {
        return basicGoodFilter;
    }

    public BasicWorkspacePage getGroupPage() {
        return groupPage;
    }
}
