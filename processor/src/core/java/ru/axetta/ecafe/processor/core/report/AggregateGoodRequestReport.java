/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.persistence.MenuExchangeRule;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents.GoodRequest;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents.GoodRequestPosition;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Product;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.text.SimpleDateFormat;
import java.util.*;

public class AggregateGoodRequestReport extends BasicReport {

    private final List<RequestItemGroups> itemGroupsList;

    public static class Builder {

        public AggregateGoodRequestReport build(Session session, Date startDate, Date endDate, List<Long> idOfOrgList)
                throws Exception {
            Date generateTime = new Date();

            List<RequestItemGroups> itemGroupsList = new ArrayList<RequestItemGroups>();
            for (Long idOfSupplierOrg : idOfOrgList) {
                Criteria orgCriteria = session.createCriteria(Org.class);
                orgCriteria.add(Restrictions.eq("idOfOrg", idOfSupplierOrg));
                Org supplierOrg = (Org) orgCriteria.uniqueResult();
                if (supplierOrg == null) {
                    continue;
                }

                // Проверка того, является ли указанная организация поставщиком,
                // и составление списка ОУ, для которых указанная орг-ия явл-ся поставщиком
                HashMap<Long, Org> destOrgMap = new HashMap<Long, Org>();
                Criteria destMenuExchangeCriteria = session.createCriteria(MenuExchangeRule.class);
                destMenuExchangeCriteria.add(Restrictions.eq("idOfSourceOrg", idOfSupplierOrg));
                List menuExchangeRuleList = destMenuExchangeCriteria.list();
                if (!menuExchangeRuleList.isEmpty()) {
                    for (Object object: menuExchangeRuleList) {
                        MenuExchangeRule menuExchangeRule = (MenuExchangeRule) object;
                        Long idOfDestOrg = menuExchangeRule.getIdOfDestOrg();
                        if (idOfDestOrg != null) {
                            Org curOrg = getOrgById(session, idOfDestOrg);
                            if (curOrg != null) {
                                destOrgMap.put(idOfDestOrg, curOrg);
                            }
                        }
                    }
                }

                if (destOrgMap.isEmpty()) {
                    continue;
                }

                RequestItemGroups requestItemGroups = new RequestItemGroups();
                List<RequestItems> requestItemsList = new ArrayList<RequestItems>();

                SupplierDetails supplierDetails = new SupplierDetails();
                supplierDetails.setIdOfSupplier(idOfSupplierOrg);
                supplierDetails.setNameOfSupplier(supplierOrg.getShortName());

                String where;
                where = " where (createdDate between " +  startDate.getTime() + " and " + endDate.getTime();
                where += " and (";
                for (Org destOrg : destOrgMap.values()) {
                    where += "orgowner=" + destOrg.getIdOfOrg() + " or ";
                }
                where = where.substring(0, where.length() - 4);
                where += ") and state=1 and deletedstate=false)";
                Query goodRequestQuery = session.createQuery("from GoodRequest" + where);
                List<Object> goodRequestObjectList = goodRequestQuery.list();

                Map<String, ProductDetails> productDetailsMap = new HashMap<String, ProductDetails>();
                for (Object goodRequestObject: goodRequestObjectList) {
                    GoodRequest goodRequest = (GoodRequest) goodRequestObject;

                    where = " where idofgoodsrequest = " + goodRequest.getGlobalId();
                    Query goodRequestPositionQuery = session.createQuery("from GoodRequestPosition" + where);
                    List<Object> goodRequestPositionObjectList = goodRequestPositionQuery.list();

                    Map<String, List<RequestItems>> requestItemsMap = new HashMap<String, List<RequestItems>>();
                    for (Object goodRequestPositionObject : goodRequestPositionObjectList) {
                        GoodRequestPosition goodRequestPosition = (GoodRequestPosition) goodRequestPositionObject;

                        Good good = goodRequestPosition.getGood();
                        Product product = goodRequestPosition.getProduct();
                        ProductDetails productDetails = new ProductDetails();
                        supplierDetails.increaseRowCount();
                        productDetails.setSupplierDetails(supplierDetails);
                        if (good != null) {
                            String nameOfGood = good.getNameOfGood();
                            productDetails.setNameOfProduct(nameOfGood);
                            if (productDetailsMap.get(nameOfGood) == null) {
                                productDetailsMap.put(nameOfGood, productDetails);
                            }
                        } else if (product != null) {
                            String nameOfProduct = product.getProductName();
                            productDetails.setNameOfProduct(nameOfProduct);
                            if (productDetailsMap.get(nameOfProduct) == null) {
                                productDetailsMap.put(nameOfProduct, productDetails);
                            }
                        } else {
                            continue;
                        }

                        RequestItems requestItems = new RequestItems();
                        requestItems.setIdOfOrg(goodRequest.getOrgOwner());
                        requestItems.setNameOfOrg(destOrgMap.get(goodRequest.getOrgOwner()).getOfficialName());
                        requestItems.setDateOfExecution(goodRequest.getDoneDate());
                        requestItems.setProductCount(goodRequestPosition.getTotalCount());
                        String nameOfProductKey;
                        if (good != null) {
                            nameOfProductKey = goodRequestPosition.getGood().getNameOfGood();
                        } else if (product != null) {
                            nameOfProductKey = goodRequestPosition.getProduct().getProductName();
                        } else {
                            continue;
                        }
                        ProductDetails productDetailsFromMap = productDetailsMap.get(nameOfProductKey);
                        productDetailsFromMap.increaseTotalCount(requestItems.getProductCount());
                        productDetailsFromMap.increaseRowCount();
                        requestItems.setProductDetails(productDetailsFromMap);

                        if (requestItemsMap.get(nameOfProductKey) == null) {
                            List<RequestItems> requestItemsListFromMap = new ArrayList<RequestItems>();
                            requestItemsListFromMap.add(requestItems);
                            requestItemsMap.put(nameOfProductKey, requestItemsListFromMap);
                        } else {
                            requestItemsMap.get(nameOfProductKey).add(requestItems);
                        }
                    }
                    if (!requestItemsMap.isEmpty()) {
                        for (List<RequestItems> requestItemsListFromMap : requestItemsMap.values()) {
                            for (RequestItems requestItemsFromMap : requestItemsListFromMap) {
                                requestItemsList.add(requestItemsFromMap);
                            }
                        }
                    }

                }

                // если для поставщика нет ни одной заявки, создать пустые элементы
                // для корректного отображения таблицы
                if (requestItemsList.isEmpty()) {
                    ProductDetails productDetails = new ProductDetails();
                    productDetails.setNameOfProduct("");
                    supplierDetails.increaseRowCount();
                    productDetails.setSupplierDetails(supplierDetails);

                    RequestItems requestItems = new RequestItems();
                    requestItems.setNameOfOrg("");
                    productDetails.increaseRowCount();
                    requestItems.setProductDetails(productDetails);

                    requestItemsList.add(requestItems);
                }

                requestItemGroups.setItemsList(requestItemsList);
                itemGroupsList.add(requestItemGroups);
            }

            return new AggregateGoodRequestReport(generateTime, new Date().getTime() - generateTime.getTime(),
                    itemGroupsList);
        }

        private Org getOrgById(Session session, Long idOfOrg) {
            Criteria orgCriteria = session.createCriteria(Org.class);
            orgCriteria.add(Restrictions.eq("idOfOrg", idOfOrg));
            Org org = (Org) orgCriteria.uniqueResult();
            return org;
        }

    }

    public AggregateGoodRequestReport() {
        super();
        this.itemGroupsList = Collections.emptyList();
    }

    public AggregateGoodRequestReport(Date generateTime, long generateDuration, List<RequestItemGroups> itemGroupsList) {
        super(generateTime, generateDuration);
        this.itemGroupsList = itemGroupsList;

    }


    public List<RequestItemGroups> getItemGroupsList() {
        return itemGroupsList;
    }

    public static class SupplierDetails {

        private Long idOfSupplier;
        private String nameOfSupplier;
        private int rowCount = 0;

        public boolean rendered(long rowNumber) {
            if ( (rowCount == 0) || (rowNumber % rowCount == 0)) {
                return true;
            }
            return false;
        }

        public Long getIdOfSupplier() {
            return idOfSupplier;
        }

        public void setIdOfSupplier(Long idOfSupplier) {
            this.idOfSupplier = idOfSupplier;
        }

        public String getNameOfSupplier() {
            return nameOfSupplier;
        }

        public void setNameOfSupplier(String nameOfSupplier) {
            this.nameOfSupplier = nameOfSupplier;
        }

        public int getRowCount() {
            if (rowCount <= 0) {
                return 1;
            }
            return rowCount;
        }

        public void setRowCount(int rowCount) {
            this.rowCount = rowCount;
        }

        public void increaseRowCount() {
            this.rowCount++;
        }

    }

    public static class ProductDetails {

        private String nameOfProduct;
        private SupplierDetails supplierDetails;
        private long totalCount = 0;
        private int rowCount = 0;

        public boolean rendered(long rowNumber) {
            if ((rowCount == 0) || (rowNumber % rowCount == 0)) {
                return true;
            }
            return false;
        }

        public SupplierDetails getSupplierDetails() {
            return supplierDetails;
        }

        public void setSupplierDetails(SupplierDetails supplierDetails) {
            this.supplierDetails = supplierDetails;
        }

        public String getNameOfProduct() {
            return nameOfProduct;
        }

        public void setNameOfProduct(String nameOfProduct) {
            this.nameOfProduct = nameOfProduct;
        }

        public long getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(long totalCount) {
            this.totalCount = totalCount;
        }

        public void increaseTotalCount(Long count) {
            if (count != null) {
                totalCount += count;
            }
        }

        public int getRowCount() {
            if (rowCount <= 0) {
                return 1;
            }
            return rowCount;
        }

        public void setRowCount(int rowCount) {
            this.rowCount = rowCount;
        }

        public void increaseRowCount() {
            this.rowCount++;
        }

    }

    public static class RequestItems {

        private Long idOfOrg;
        private String nameOfOrg;
        private Long productCount;
        private Date dateOfExecution;
        private ProductDetails productDetails;

        public ProductDetails getProductDetails() {
            return productDetails;
        }

        public void setProductDetails(ProductDetails productDetails) {
            this.productDetails = productDetails;
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public void setIdOfOrg(Long idOfOrg) {
            this.idOfOrg = idOfOrg;
        }

        public String getNameOfOrg() {
            return nameOfOrg;
        }

        public void setNameOfOrg(String nameOfOrg) {
            this.nameOfOrg = nameOfOrg;
        }

        public Date getDateOfExecution() {
            return dateOfExecution;
        }

        public String getDateOfExecutionFormatted() {
            return formatDate(dateOfExecution);
        }

        public void setDateOfExecution(Date dateOfExecution) {
            this.dateOfExecution = dateOfExecution;
        }

        public Long getProductCount() {
            return productCount;
        }

        public void setProductCount(Long productCount) {
            this.productCount = productCount;
        }

        private String formatDate(Date date) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            if (date != null) {
                String result = sdf.format(date);
                return result;
            } else {
                return null;
            }
        }

    }

    public static class RequestItemGroups {

        private List<RequestItems> itemsList;

        public List<RequestItems> getItemsList() {
            return itemsList;
        }

        public void setItemsList(List<RequestItems> itemsList) {
            this.itemsList = itemsList;
        }
    }

}
