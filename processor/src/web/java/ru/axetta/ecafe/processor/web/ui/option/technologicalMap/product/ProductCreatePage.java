/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.technologicalMap.product;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.TechnologicalMapProduct;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 01.06.12
 * Time: 14:39
 * To change this template use File | Settings | File Templates.
 */
public class ProductCreatePage extends BasicWorkspacePage {

    //Наименование продукта
    private String nameOfProduct;
    //Масса брутто, г
    private Float grossMass;
    //Масса нетто, г
    private Float netMass;

    public String getNameOfProduct() {
        return nameOfProduct;
    }

    public void setNameOfProduct(String nameOfProduct) {
        this.nameOfProduct = nameOfProduct;
    }

    public Float getGrossMass() {
        return grossMass;
    }

    public void setGrossMass(Float grossMass) {
        this.grossMass = grossMass;
    }

    public Float getNetMass() {
        return netMass;
    }

    public void setNetMass(Float netMass) {
        this.netMass = netMass;
    }

    public String getPageFilename() {
        return "option/technologicalMap/product/create";
    }

    public void createProduct(Session session) throws Exception {
        TechnologicalMapProduct p = new TechnologicalMapProduct();
        p.setGrossWeight(grossMass);
        p.setNameOfProduct(nameOfProduct);
        p.setNetWeight(netMass);
        p.setCreatedDate(new Date());
        p.setGlobalVersion(0L);
        p.setDeletedState(false);
        p.setIdOfProduct(-1L);
      //  p.setIdOfTechnoMap(-1L);
        session.save(p);
    }

}
