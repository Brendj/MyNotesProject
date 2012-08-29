package ru.axetta.ecafe.processor.web.ui.option.configurationProvider.technologicalMap.technologicalMapProduct;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Product;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.TechnologicalMap;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.TechnologicalMapProduct;
import ru.axetta.ecafe.processor.web.ui.BasicPage;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 03.07.12
 * Time: 13:29
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class ProductItemsPanel extends BasicPage {

    private final Queue<ProductSelect> completeHandlerLists = new LinkedList<ProductSelect>();

    private TechnologicalMap technologicalMap;

    private List<ProductItem> productItems = new ArrayList<ProductItem>();
    private List<Product> pr = new ArrayList<Product>();
    private String filter="";

    @PersistenceContext
    private EntityManager entityManager;

    public void pushCompleteHandlerList(ProductSelect handlerList) {
        completeHandlerLists.add(handlerList);
    }

    public Object addProducts(){
        completeSelection(true);
        return null;
    }

    private void completeSelection(boolean flag){
        List<ProductItem> productItemList = new ArrayList<ProductItem>();
        if(flag){
            for (ProductItem productItem: productItems){
                if(productItem.getChecked()) productItemList.add(productItem);
            }
        }
        if (!completeHandlerLists.isEmpty()) {
            completeHandlerLists.peek().select(productItemList);
            completeHandlerLists.poll();
        }
    }

    public Object cancel(){
        completeSelection(false);
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
