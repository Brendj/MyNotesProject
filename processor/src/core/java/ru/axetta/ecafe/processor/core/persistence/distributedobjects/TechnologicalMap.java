/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 30.05.12
 * Time: 11:07
 * To change this template use File | Settings | File Templates.
 */
public class TechnologicalMap extends DistributedObject {

    private Set<TechnologicalMapProduct> technologicalMapProductInternal = new HashSet<TechnologicalMapProduct>();

    public Set<TechnologicalMapProduct> getTechnologicalMapProduct(){
        return Collections.unmodifiableSet(getTechnologicalMapProductInternal());
    }

    public void addTechnologicalMapProduct(TechnologicalMapProduct technologicalMapProduct){
        technologicalMapProductInternal.add(technologicalMapProduct);
    }

    private Set<TechnologicalMapProduct> getTechnologicalMapProductInternal() {
        return technologicalMapProductInternal;
    }

    private void setTechnologicalMapProductInternal(Set<TechnologicalMapProduct> technologicalMapProductInternal) {
        this.technologicalMapProductInternal = technologicalMapProductInternal;
    }

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element,"Name", nameOfTechnologicalMap);
        setAttribute(element,"Number", numberOfTechnologicalMap);
        setAttribute(element,"Technology", technologyOfPreparation);
        setAttribute(element,"TempPreparation", tempOfPreparation);
        setAttribute(element,"TermRealization", termOfRealization);

        setAttribute(element,"Energy", energyValue);
        setAttribute(element,"Proteins", proteins);
        setAttribute(element,"Carbohydrates", carbohydrates);
        setAttribute(element,"Fats", fats);

        setAttribute(element,"Ca", microElCa);
        setAttribute(element,"Mg", microElMg);
        setAttribute(element,"P", microElP);
        setAttribute(element,"Fe", microElFe);

        setAttribute(element,"VA", vitaminA);
        setAttribute(element,"VB1", vitaminB1);
        setAttribute(element,"VB2", vitaminB2);
        setAttribute(element,"VPp", vitaminPp);
        setAttribute(element,"VC", vitaminC);
        setAttribute(element,"VE", vitaminE);
    }

    @Override
    protected TechnologicalMap parseAttributes(Node node) {

        String stringNameOfTechnologicalMap = getStringAttributeValue(node,"Name",128);
        if(stringNameOfTechnologicalMap!=null) setNameOfTechnologicalMap(stringNameOfTechnologicalMap);

        Long numberOfTechnologicalMap = getLongAttributeValue(node,"Number");
        if(numberOfTechnologicalMap!=null) setNumberOfTechnologicalMap(numberOfTechnologicalMap);

        String stringTechnologyOfPreparation = getStringAttributeValue(node,"Technology",4096);
        if(stringTechnologyOfPreparation!=null) setTechnologyOfPreparation(stringTechnologyOfPreparation);

        String stringTempOfPreparation = getStringAttributeValue(node,"TempOfPreparation",128);
        if(stringTempOfPreparation!=null) setTempOfPreparation(stringTempOfPreparation);

        String stringTermOfRealization = getStringAttributeValue(node,"TermOfRealization",128);
        if(stringTermOfRealization!=null) setTermOfRealization(stringTermOfRealization);

        Float floatEnergyValue = getFloatAttributeValue(node,"Energy");
        if(floatEnergyValue!=null) setEnergyValue(floatEnergyValue);

        Float floatProteins = getFloatAttributeValue(node,"Proteins");
        if(floatProteins!=null) setProteins(floatProteins);

        Float floatCarbohydrates = getFloatAttributeValue(node,"Carbohydrates");
        if(floatCarbohydrates!=null) setCarbohydrates(floatCarbohydrates);

        Float floatFats = getFloatAttributeValue(node,"Fats");
        if(floatFats!=null) setFats(floatFats);

        Float floatMicroElCa = getFloatAttributeValue(node,"Ca");
        if(floatMicroElCa!=null) setMicroElCa(floatMicroElCa);

        Float floatMicroElMg = getFloatAttributeValue(node,"Mg");
        if(floatMicroElMg!=null) setMicroElMg(floatMicroElMg);

        Float floatMicroElP = getFloatAttributeValue(node,"P");
        if(floatMicroElP!=null) setMicroElP(floatMicroElP);

        Float floatMicroElFe = getFloatAttributeValue(node,"Fe");
        if(floatMicroElFe!=null) setMicroElFe(floatMicroElFe);

        Float floatVitaminA = getFloatAttributeValue(node,"VA");
        if(floatVitaminA!=null) setVitaminA(floatVitaminA);

        Float floatVitaminB1 = getFloatAttributeValue(node,"VB1");
        if(floatVitaminB1!=null) setVitaminB1(floatVitaminB1);

        Float floatVitaminB2 = getFloatAttributeValue(node,"VB2");
        if(floatVitaminB2!=null) setVitaminB2(floatVitaminB2);

        Float floatVitaminPp = getFloatAttributeValue(node,"VPp");
        if(floatVitaminPp!=null) setVitaminPp(floatVitaminPp);

        Float floatVitaminC = getFloatAttributeValue(node,"VC");
        if(floatVitaminC!=null) setVitaminC(floatVitaminC);

        Float floatVitaminE = getFloatAttributeValue(node,"VE");
        if(floatVitaminE!=null) setVitaminE(floatVitaminE);

        return this;
    }

    private String nameOfTechnologicalMap;
    private Long numberOfTechnologicalMap;

    //Технология приготовления
    private String technologyOfPreparation;

    private String timeOfRealization;
    // Температура приготовления
    private String tempOfPreparation;
    //Срок реализации в часах
    private String termOfRealization;

    // В 100 граммах данного блюда содержится:
    //Пищевые вещества, г
    private Float proteins;
    private Float carbohydrates;
    private Float fats;

    //Минеральные вещества, мг
    private Float microElCa;
    private Float microElMg;
    private Float microElP;
    private Float microElFe;

    //Энергетическая ценность (ккал)
    private Float energyValue;

    //Витамины, мг
    private Float vitaminA;
    private Float vitaminB1;
    private Float vitaminB2;
    private Float vitaminPp;
    private Float vitaminC;
    private Float vitaminE;

    public String getTempOfPreparation() {
        return tempOfPreparation;
    }

    public void setTempOfPreparation(String tempOfPreparation) {
        this.tempOfPreparation = tempOfPreparation;
    }

    public String getTechnologyOfPreparation() {
        return technologyOfPreparation;
    }

    public String getNameOfTechnologicalMap() {
        return nameOfTechnologicalMap;
    }

    public void setNameOfTechnologicalMap(String nameOfTechnologicalMap) {
        this.nameOfTechnologicalMap = nameOfTechnologicalMap;
    }

    public Long getNumberOfTechnologicalMap() {
        return numberOfTechnologicalMap;
    }

    public void setNumberOfTechnologicalMap(Long numberOfTechnologicalMap) {
        this.numberOfTechnologicalMap = numberOfTechnologicalMap;
    }

    public void setTechnologyOfPreparation(String technologyOfPreparation) {
        this.technologyOfPreparation = technologyOfPreparation;
    }

    public String getTimeOfRealization() {
        return timeOfRealization;
    }

    public void setTimeOfRealization(String timeOfRealization) {
        this.timeOfRealization = timeOfRealization;
    }

    public String getTermOfRealization() {
        return termOfRealization;
    }

    public void setTermOfRealization(String termOfRealization) {
        this.termOfRealization = termOfRealization;
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

    public Float getMicroElCa() {
        return microElCa;
    }

    public void setMicroElCa(Float microElCa) {
        this.microElCa = microElCa;
    }

    public Float getMicroElMg() {
        return microElMg;
    }

    public void setMicroElMg(Float microElMg) {
        this.microElMg = microElMg;
    }

    public Float getMicroElP() {
        return microElP;
    }

    public void setMicroElP(Float microElP) {
        this.microElP = microElP;
    }

    public Float getMicroElFe() {
        return microElFe;
    }

    public void setMicroElFe(Float microElFe) {
        this.microElFe = microElFe;
    }

    public Float getEnergyValue() {
        return energyValue;
    }

    public void setEnergyValue(Float energyValue) {
        this.energyValue = energyValue;
    }

    public Float getVitaminA() {
        return vitaminA;
    }

    public void setVitaminA(Float vitaminA) {
        this.vitaminA = vitaminA;
    }

    public Float getVitaminB1() {
        return vitaminB1;
    }

    public void setVitaminB1(Float vitaminB1) {
        this.vitaminB1 = vitaminB1;
    }

    public Float getVitaminB2() {
        return vitaminB2;
    }

    public void setVitaminB2(Float vitaminB2) {
        this.vitaminB2 = vitaminB2;
    }

    public Float getVitaminPp() {
        return vitaminPp;
    }

    public void setVitaminPp(Float vitaminPp) {
        this.vitaminPp = vitaminPp;
    }

    public Float getVitaminC() {
        return vitaminC;
    }

    public void setVitaminC(Float vitaminC) {
        this.vitaminC = vitaminC;
    }

    public Float getVitaminE() {
        return vitaminE;
    }

    public void setVitaminE(Float vitaminE) {
        this.vitaminE = vitaminE;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("TechnologicalMap");
        sb.append("{carbohydrates=").append(carbohydrates);
        sb.append(", nameOfTechnologicalMap='").append(nameOfTechnologicalMap).append('\'');
        sb.append(", numberOfTechnologicalMap=").append(numberOfTechnologicalMap);
        sb.append(", technologyOfPreparation='").append(technologyOfPreparation).append('\'');
        sb.append(", timeOfRealization='").append(timeOfRealization).append('\'');
        sb.append(", tempOfPreparation='").append(tempOfPreparation).append('\'');
        sb.append(", termOfRealization='").append(termOfRealization).append('\'');
        sb.append(", proteins=").append(proteins);
        sb.append(", fats=").append(fats);
        sb.append(", microElCa=").append(microElCa);
        sb.append(", microElMg=").append(microElMg);
        sb.append(", microElP=").append(microElP);
        sb.append(", microElFe=").append(microElFe);
        sb.append(", energyValue=").append(energyValue);
        sb.append(", vitaminA=").append(vitaminA);
        sb.append(", vitaminB1=").append(vitaminB1);
        sb.append(", vitaminB2=").append(vitaminB2);
        sb.append(", vitaminPp=").append(vitaminPp);
        sb.append(", vitaminC=").append(vitaminC);
        sb.append(", vitaminE=").append(vitaminE);
        sb.append(", globalId ='").append(globalId).append('\'');
        sb.append(", globalVersion ='").append(globalVersion).append('\'');
        sb.append(", deletedState ='").append(deletedState).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
