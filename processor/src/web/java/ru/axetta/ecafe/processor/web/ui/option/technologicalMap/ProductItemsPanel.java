package ru.axetta.ecafe.processor.web.ui.option.technologicalMap;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.Product;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.TechnologicalMap;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.TechnologicalMapProduct;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.option.technologicalMap.ProductItem;

import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 03.07.12
 * Time: 13:29
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ProductItemsPanel {

    private TechnologicalMap technologicalMap;

    public TechnologicalMap getTechnologicalMap() {
        return technologicalMap;
    }

    public void setTechnologicalMap(TechnologicalMap technologicalMap) {
        this.technologicalMap = technologicalMap;
    }

    private List<ProductItem> productItems = new LinkedList<ProductItem>();

    public void reload(List<TechnologicalMapProduct> technologicalMapProductList) throws Exception {
        List<Product> pr = new LinkedList<Product>();
        for (TechnologicalMapProduct tmp: technologicalMapProductList){
            pr.add(tmp.getProduct());
        }
        List<Product> productList = DAOService.getInstance().getDistributedObjects(Product.class);
        productItems.clear();
        for (Product product: productList){
            productItems.add(new ProductItem(pr.contains(product), product));
        }
    }

    public Object addProducts() {
        //productItems = RuntimeContext.getAppContext().getBean(ProductItemsPanel.class).getProductItems();
        for (ProductItem productItem: productItems){
           if(productItem.getChecked()){
            TechnologicalMapProduct technologicalMapProduct = new TechnologicalMapProduct();
            technologicalMapProduct.setIdOfProduct(productItem.getProduct().getGlobalId());
            technologicalMapProduct.setNameOfProduct(productItem.getProduct().getProductName());
            technologicalMapProduct.setDeletedState(false);
            technologicalMapProduct.setTechnologicalMap(technologicalMap);
            technologicalMap.addTechnologicalMapProduct(technologicalMapProduct);
           }
        }

       /* List<ProductItem> products = new LinkedList<ProductItem>();
        for (ProductItem pi: productItems){
            if(pi.getChecked()){
                products.add(pi);
            }
        }
        productItems = products;*/
        return null;
    }

    public List<ProductItem> getProductItems() {
        return productItems;
    }

    public void setProductItems(List<ProductItem> productItems) {
        this.productItems = productItems;
    }
}
