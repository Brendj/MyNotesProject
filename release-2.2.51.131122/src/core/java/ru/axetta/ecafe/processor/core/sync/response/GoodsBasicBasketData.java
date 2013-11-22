/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.response;

import ru.axetta.ecafe.processor.core.persistence.GoodsBasicBasket;
import ru.axetta.ecafe.processor.core.persistence.MenuExchangeRule;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 13.02.13
 * Time: 13:12
 * To change this template use File | Settings | File Templates.
 */
public class GoodsBasicBasketData {

    private List<GoodsBasicBasketDataElement> basicBasketDataElementList;

    public GoodsBasicBasketData() {}

    public List<GoodsBasicBasketDataElement> getBasicBasketDataElementList() {
        return basicBasketDataElementList;
    }

    public void process(Session session, Long idOfOrg) throws Exception{
        //Criteria criteria = session.createCriteria(MenuExchangeRule.class);
        //criteria.add(Restrictions.eq("idOfSourceOrg",idOfOrg));
        //List list = criteria.list();
        //if(list.isEmpty()){
        //    basicBasketDataElementList = new ArrayList<GoodsBasicBasketDataElement>(0);
        //}else {
        //}
        Criteria goodsBasicBasketCriteria = session.createCriteria(GoodsBasicBasket.class);
        List resultList = goodsBasicBasketCriteria.list();
        basicBasketDataElementList = new ArrayList<GoodsBasicBasketDataElement>();
        for (Object object: resultList){
            GoodsBasicBasket goodsBasicBasket = (GoodsBasicBasket) object;
            basicBasketDataElementList.add(new GoodsBasicBasketDataElement(goodsBasicBasket));
        }
    }

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("GoodsBasicBasketData");
        for (GoodsBasicBasketDataElement goodsBasicBasketDataElement : this.basicBasketDataElementList) {
            element.appendChild(goodsBasicBasketDataElement.toElement(document));
        }
        return element;
    }

}
