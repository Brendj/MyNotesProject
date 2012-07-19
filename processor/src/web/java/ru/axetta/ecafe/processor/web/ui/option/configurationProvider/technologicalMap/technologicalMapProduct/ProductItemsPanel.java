package ru.axetta.ecafe.processor.web.ui.option.configurationProvider.technologicalMap.technologicalMapProduct;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.Product;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.TechnologicalMap;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.TechnologicalMapProduct;
import ru.axetta.ecafe.processor.web.ui.BasicPage;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 03.07.12
 * Time: 13:29
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ProductItemsPanel extends BasicPage {

    private final Stack<ProductSelect> completeHandlerLists = new Stack<ProductSelect>();

    private TechnologicalMap technologicalMap;

    private List<ProductItem> productItems = new ArrayList<ProductItem>();
    private List<Product> pr = new ArrayList<Product>();
    private String filter="";

    @PersistenceContext
    private EntityManager entityManager;

    public void pushCompleteHandlerList(ProductSelect handlerList) {
        completeHandlerLists.push(handlerList);
    }

    public Object addProducts(){
        List<ProductItem> productItemList = new ArrayList<ProductItem>();
        for (ProductItem productItem: productItems){
            if(productItem.getChecked()) productItemList.add(productItem);
        }
        if (!completeHandlerLists.empty()) {
            completeHandlerLists.peek().select(productItemList);
            completeHandlerLists.pop();
        }
        return null;
    }

    public void reload(List<TechnologicalMapProduct> technologicalMapProductList) throws Exception {
        for (TechnologicalMapProduct tmp: technologicalMapProductList){
            pr.add(tmp.getProduct());
        }
        List<Product> productList = retrieveProduct();//= DAOService.getInstance().getDistributedObjects(Product.class);
        productItems.clear();
        for (Product product: productList){
            productItems.add(new ProductItem(pr.contains(product), product));
        }
    }

    public Object updateTechnologicalMapProductListSelectPage(){
        List<Product> productList = retrieveProduct();
        productItems.clear();
        for (Product product: productList){
            productItems.add(new ProductItem(pr.contains(product), product));
        }
        return null;
    }

    @Transactional
    private List<Product> retrieveProduct(){
        String where="";
        if (StringUtils.isNotEmpty(filter)){
            where = "where UPPER(productName) like '%"+filter.toUpperCase()+"%'";
        }
        String query = "from Product "+ where + " order by id";
        return entityManager.createQuery(query, Product.class).getResultList();
    }

    public TechnologicalMap getTechnologicalMap() {
        return technologicalMap;
    }

    public void setTechnologicalMap(TechnologicalMap technologicalMap) {
        this.technologicalMap = technologicalMap;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public List<ProductItem> getProductItems() {
        return productItems;
    }

    public void setProductItems(List<ProductItem> productItems) {
        this.productItems = productItems;
    }
}
