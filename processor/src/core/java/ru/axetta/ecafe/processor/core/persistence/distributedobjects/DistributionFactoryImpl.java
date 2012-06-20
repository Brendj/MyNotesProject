/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 20.06.12
 * Time: 10:52
 * To change this template use File | Settings | File Templates.
 */
public class DistributionFactoryImpl implements DistributionFactory {

    public ProductGuide createProductGuide(){
        return new ProductGuide();
    }

}
