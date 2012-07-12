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

    private List<ProductItem> productItems = new LinkedList<ProductItem>();

    private String filter="";

    @PersistenceContext
    private EntityManager entityManager;

    public void pushCompleteHandlerList(ProductSelect handlerList) {
        completeHandlerLists.push(handlerList);
    }

    public Object addProducts(){
        List<ProductItem> productItemList = new LinkedList<ProductItem>();
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
        List<Product> pr = new LinkedList<Product>();
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
        retrieveProduct();
        return null;
    }

    @Transactional
    private List<Product> retrieveProduct(){
        /*CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> query = criteriaBuilder.createQuery(Product.class);
        Root<Product> c = query.from(Product.class);
        query.select(c);
        //query.orderBy(criteriaBuilder.asc(c.get("globalId")));
        if (StringUtils.isNotEmpty(filter) && !filter.equals("")) {
            Expression<String> p = criteriaBuilder.parameter(String.class);
            query.where(criteriaBuilder.like(c.<String>get("productName"), "%" + filter + "%"));
            //query.where(criteriaBuilder.like(c.get("productName"), p));
        }*/
        String where="";
        if (StringUtils.isNotEmpty(filter)){
            where = "where productName like '%"+filter+"%'";
        }
        String query = "from Product "+ where + " order by id";
        return entityManager.createQuery(query, Product.class).getResultList();
    }

    /*
    public Object addProducts() {
        //productItems = RuntimeContext.getAppContext().getBean(ProductItemsPanel.class).getProductItems();
        for (ProductItem productItem: productItems){
           if(productItem.getChecked()){
            TechnologicalMapProduct technologicalMapProduct = new TechnologicalMapProduct();
           // technologicalMapProduct.setIdOfProduct(productItem.getProduct().getGlobalId());
               technologicalMapProduct.setProduct(productItem.getProduct());
            technologicalMapProduct.setNameOfProduct(productItem.getProduct().getProductName());
            technologicalMapProduct.setDeletedState(false);
            technologicalMapProduct.setTechnologicalMap(technologicalMap);
            technologicalMap.addTechnologicalMapProduct(technologicalMapProduct);
           }                                                                      from Product where productName like '%хлеб%' order by id
        }

        List<ProductItem> products = new LinkedList<ProductItem>();
        for (ProductItem pi: productItems){
            if(pi.getChecked()){
                products.add(pi);
            }
        }
        productItems = products;
        return null;
    }*/

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
