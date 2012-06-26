/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.technologicalMap.product;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.TechnologicalMapProduct;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 01.06.12
 * Time: 14:39
 * To change this template use File | Settings | File Templates.
 */
public class ProductCreatePage extends BasicWorkspacePage {

    //Наименование продукта
    private String name;
    //Масса брутто, г
    private Float grossMass;
    //Масса нетто, г
    private Float netMass;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        p.setGrossMass(grossMass);
        p.setName(name);
        p.setNetMass(netMass);
        session.save(p);
    }

}
