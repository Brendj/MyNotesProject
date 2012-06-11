/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 30.05.12
 * Time: 11:07
 * To change this template use File | Settings | File Templates.
 */
public class TechnologicalMap {
    private Long idOfTechnologicalMap;

    private String name;

    private List<Products> products;

    // В 100 граммах данного блюда содержится:
    //Пищевые вещества, г
    private Float proteins;
    private Float carbohydrates;
    private Float fats;

    //Минеральные вещества, мг
    private Float ca;
    private Float mg;
    private Float p;
    private Float fe;

    //Энергетическая ценность (ккал)
    private Float energyValue;

    //Витамины, мг
    private Float a;
    private Float b1;
    private Float b2;
    private Float pp;
    private Float c;
    private Float e;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //Технология приготовления
    private String technologyOfpreparation;

    //Срок реализации в часах
    private Integer termOfRealization;

    public Long getIdOfTechnologicalMap() {
        return idOfTechnologicalMap;
    }

    public void setIdOfTechnologicalMap(Long idOfTechnologicalMap) {
        this.idOfTechnologicalMap = idOfTechnologicalMap;
    }

    public List<Products> getProducts() {
        return products;
    }

    public void setProducts(List<Products> products) {
        this.products = products;
    }

    public Float getProteins() {
        return proteins;
    }

    public void setProteins(Float proteins) {
        this.proteins = proteins;
    }

    public Float getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(Float carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public Float getFats() {
        return fats;
    }

    public void setFats(Float fats) {
        this.fats = fats;
    }

    public Float getCa() {
        return ca;
    }

    public void setCa(Float ca) {
        this.ca = ca;
    }

    public Float getMg() {
        return mg;
    }

    public void setMg(Float mg) {
        this.mg = mg;
    }

    public Float getP() {
        return p;
    }

    public void setP(Float p) {
        this.p = p;
    }

    public Float getFe() {
        return fe;
    }

    public void setFe(Float fe) {
        this.fe = fe;
    }

    public Float getEnergyValue() {
        return energyValue;
    }

    public void setEnergyValue(Float energyValue) {
        this.energyValue = energyValue;
    }

    public Float getA() {
        return a;
    }

    public void setA(Float a) {
        this.a = a;
    }

    public Float getB1() {
        return b1;
    }

    public void setB1(Float b1) {
        this.b1 = b1;
    }

    public Float getB2() {
        return b2;
    }

    public void setB2(Float b2) {
        this.b2 = b2;
    }

    public Float getPp() {
        return pp;
    }

    public void setPp(Float pp) {
        this.pp = pp;
    }

    public Float getC() {
        return c;
    }

    public void setC(Float c) {
        this.c = c;
    }

    public Float getE() {
        return e;
    }

    public void setE(Float e) {
        this.e = e;
    }

    public String getTechnologyOfpreparation() {
        return technologyOfpreparation;
    }

    public void setTechnologyOfpreparation(String technologyOfpreparation) {
        this.technologyOfpreparation = technologyOfpreparation;
    }

    public Integer getTermOfRealization() {
        return termOfRealization;
    }

    public void setTermOfRealization(Integer termOfRealization) {
        this.termOfRealization = termOfRealization;
    }
}
