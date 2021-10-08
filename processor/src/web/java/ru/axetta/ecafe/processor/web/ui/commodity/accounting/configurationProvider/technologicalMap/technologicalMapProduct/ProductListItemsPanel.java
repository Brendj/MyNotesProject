package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.technologicalMap.technologicalMapProduct;

import ru.axetta.ecafe.processor.core.daoservices.context.ContextDAOServices;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Product;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.TechnologicalMap;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.TechnologicalMapProduct;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicPage;
import ru.axetta.ecafe.processor.web.ui.MainPage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 03.07.12
 * Time: 13:29
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class ProductListItemsPanel extends BasicPage {

    private final static Logger logger = LoggerFactory.getLogger(ProductListItemsPanel.class);
    private final Queue<ProductListSelect> completeHandlerLists = new LinkedList<ProductListSelect>();

    private TechnologicalMap technologicalMap;

    private List<ProductListItem> productItems = new ArrayList<ProductListItem>();
    private List<Product> pr = new ArrayList<Product>();
    private String filter="";

    @Autowired
    private DAOService daoService;
    @Autowired
    private ContextDAOServices contextDAOServices;
    @Autowired
    private DAOReadonlyService daoReadonlyService;

    public void pushCompleteHandlerList(ProductListSelect handlerList) {
        completeHandlerLists.add(handlerList);
    }

    public Object addProducts(){
        completeSelection(true);
        return null;
    }

    private void completeSelection(boolean flag){
        List<ProductListItem> productItemList = new ArrayList<ProductListItem>();
        if(flag){
            for (ProductListItem productItem: productItems){
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
        pr.clear();
        for (TechnologicalMapProduct tmp: technologicalMapProductList){
            pr.add(tmp.getProduct());
        }
        List<Product> productList = retrieveProduct();//= DAOService.getInstance().getDistributedObjects(Product.class);
        productItems.clear();
        for (Product product: productList){
            if (!pr.contains(product)) {
                productItems.add(new ProductListItem(pr.contains(product), product));
            }
        }
    }

    public Object updateTechnologicalMapProductListSelectPage(){
        List<Product> productList = null;
        try {
            productList = retrieveProduct();
            productItems.clear();
            for (Product product: productList){
                if (!pr.contains(product)) {
                    productItems.add(new ProductListItem(pr.contains(product), product));
                }
            }
        } catch (Exception e) {
            printError("Ошибка при загрузке страницы: "+e.getMessage());
            logger.error("Error load page", e);
        }

        return null;
    }

    private List<Product> retrieveProduct() throws Exception {
        //String where="";
        //if (StringUtils.isNotEmpty(filter)){
        //    where = "where UPPER(productName) like '%"+filter.toUpperCase()+"%'";
        //}
        //String query = "from Product "+ where + " order by id";
        //return entityManager.createQuery(query, Product.class).getResultList();
        User user = MainPage.getSessionInstance().getCurrentUser();
        List<Long> orgOwners = contextDAOServices.findOrgOwnersByContragentSet(user.getIdOfUser());
        return daoReadonlyService.findProductByConfigurationProvider(orgOwners, filter);
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

    public List<ProductListItem> getProductItems() {
        return productItems;
    }

    public void setProductItems(List<ProductListItem> productItems) {
        this.productItems = productItems;
    }

    public void printError(String msg) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }
}
