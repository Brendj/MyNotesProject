/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.products;

import ru.axetta.ecafe.processor.core.daoservices.commodity.accounting.ConfigurationProviderService;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.ConfigurationProviderDistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.sql.JoinType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 30.05.12
 * Time: 11:07
 * To change this template use File | Settings | File Templates.
 */
public class TechnologicalMap extends ConfigurationProviderDistributedObject {

    /*  private String groupName;*/
    private String nameOfTechnologicalMap;

    private String numberOfTechnologicalMap;

    //Технология приготовления
    private String technologyOfPreparation;
    private String timeOfRealization;

    // Температура приготовления
    private String tempOfPreparation;

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
    private TechnologicalMapGroup technologicalMapGroup;

    //private Long idOfConfigurationProvider;
    private Set<TechnologicalMapProduct> technologicalMapProductInternal = new HashSet<TechnologicalMapProduct>();
    private User userCreate;
    private User userDelete;

    private User userEdit;
    private String guidOfTMG;
    private Integer lifeTime;

    private Set<Good> goodInternal;

    @Override
    public void createProjections(Criteria criteria, int currentLimit, String currentLastGuid) {
        criteria.createAlias("technologicalMapGroup","tmg", JoinType.LEFT_OUTER_JOIN);

        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.property("guid"), "guid");
        projectionList.add(Projections.property("globalVersion"), "globalVersion");
        projectionList.add(Projections.property("deletedState"), "deletedState");
        projectionList.add(Projections.property("orgOwner"), "orgOwner");

        projectionList.add(Projections.property("nameOfTechnologicalMap"), "nameOfTechnologicalMap");
        projectionList.add(Projections.property("numberOfTechnologicalMap"), "numberOfTechnologicalMap");
        projectionList.add(Projections.property("technologyOfPreparation"), "technologyOfPreparation");
        projectionList.add(Projections.property("lifeTime"), "lifeTime");
        projectionList.add(Projections.property("energyValue"), "energyValue");
        projectionList.add(Projections.property("proteins"), "proteins");
        projectionList.add(Projections.property("carbohydrates"), "carbohydrates");
        projectionList.add(Projections.property("fats"), "fats");
        projectionList.add(Projections.property("microElCa"), "microElCa");
        projectionList.add(Projections.property("microElMg"), "microElMg");
        projectionList.add(Projections.property("microElP"), "microElP");
        projectionList.add(Projections.property("microElFe"), "microElFe");
        projectionList.add(Projections.property("vitaminA"), "vitaminA");
        projectionList.add(Projections.property("vitaminB1"), "vitaminB1");
        projectionList.add(Projections.property("vitaminB2"), "vitaminB2");
        projectionList.add(Projections.property("vitaminPp"), "vitaminPp");
        projectionList.add(Projections.property("vitaminC"), "vitaminC");
        projectionList.add(Projections.property("vitaminE"), "vitaminE");

        projectionList.add(Projections.property("tmg.guid"), "guidOfTMG");

        criteria.setProjection(projectionList);
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
        setNameOfTechnologicalMap(((TechnologicalMap) distributedObject).getNameOfTechnologicalMap());
        setNumberOfTechnologicalMap(((TechnologicalMap) distributedObject).getNumberOfTechnologicalMap());
        setTechnologyOfPreparation(((TechnologicalMap) distributedObject).getTechnologyOfPreparation());
        setTempOfPreparation(((TechnologicalMap) distributedObject).getTempOfPreparation());
        setLifeTime(((TechnologicalMap) distributedObject).getLifeTime());

        setEnergyValue(((TechnologicalMap) distributedObject).getEnergyValue());
        setProteins(((TechnologicalMap) distributedObject).getProteins());
        setCarbohydrates(((TechnologicalMap) distributedObject).getCarbohydrates());
        setFats(((TechnologicalMap) distributedObject).getFats());

        setMicroElCa(((TechnologicalMap) distributedObject).getMicroElCa());
        setMicroElFe(((TechnologicalMap) distributedObject).getMicroElFe());
        setMicroElMg(((TechnologicalMap) distributedObject).getMicroElMg());
        setMicroElP(((TechnologicalMap) distributedObject).getMicroElP());

        setVitaminA(((TechnologicalMap) distributedObject).getVitaminA());
        setVitaminB1(((TechnologicalMap) distributedObject).getVitaminB1());
        setVitaminB2(((TechnologicalMap) distributedObject).getVitaminB2());
        setVitaminC(((TechnologicalMap) distributedObject).getVitaminC());
        setVitaminE(((TechnologicalMap) distributedObject).getVitaminE());
        setVitaminPp(((TechnologicalMap) distributedObject).getVitaminPp());

        setIdOfConfigurationProvider(((TechnologicalMap) distributedObject).getIdOfConfigurationProvider());
    }

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "OrgOwner", orgOwner);
        XMLUtils.setAttributeIfNotNull(element, "Name", nameOfTechnologicalMap);
        XMLUtils.setAttributeIfNotNull(element, "Number", numberOfTechnologicalMap);
        XMLUtils.setAttributeIfNotNull(element, "Technology", technologyOfPreparation);
        XMLUtils.setAttributeIfNotNull(element, "TempPreparation", tempOfPreparation);
        XMLUtils.setAttributeIfNotNull(element, "LifeTime", lifeTime);
        XMLUtils.setAttributeIfNotNull(element, "Energy", energyValue);
        XMLUtils.setAttributeIfNotNull(element, "Proteins", proteins);
        XMLUtils.setAttributeIfNotNull(element, "Carbohydrates", carbohydrates);
        XMLUtils.setAttributeIfNotNull(element, "Fats", fats);
        XMLUtils.setAttributeIfNotNull(element, "Ca", microElCa);
        XMLUtils.setAttributeIfNotNull(element, "Mg", microElMg);
        XMLUtils.setAttributeIfNotNull(element, "P", microElP);
        XMLUtils.setAttributeIfNotNull(element, "Fe", microElFe);
        XMLUtils.setAttributeIfNotNull(element, "VA", vitaminA);
        XMLUtils.setAttributeIfNotNull(element, "VB1", vitaminB1);
        XMLUtils.setAttributeIfNotNull(element, "VB2", vitaminB2);
        XMLUtils.setAttributeIfNotNull(element, "VPp", vitaminPp);
        XMLUtils.setAttributeIfNotNull(element, "VC", vitaminC);
        XMLUtils.setAttributeIfNotNull(element, "VE", vitaminE);
        //XMLUtils.setAttributeIfNotNull(element, "GuidOfTMG", technologicalMapGroup.getGuid());
        XMLUtils.setAttributeIfNotNull(element, "GuidOfTMG", guidOfTMG);
    }

    @Override
    protected TechnologicalMap parseAttributes(Node node) throws Exception {
        Long longOrgOwner = XMLUtils.getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null){
            setOrgOwner(longOrgOwner);
        } else {
            throw new DistributedObjectException("OrgOwner is empty");
        }
        String stringNameOfTechnologicalMap = XMLUtils.getStringAttributeValue(node, "Name", 128);
        if (stringNameOfTechnologicalMap != null) {
            setNameOfTechnologicalMap(stringNameOfTechnologicalMap);
        }
        String numberOfTechnologicalMap = XMLUtils.getStringAttributeValue(node, "Number", 128);
        if (numberOfTechnologicalMap != null) {
            setNumberOfTechnologicalMap(numberOfTechnologicalMap);
        }
        String stringTechnologyOfPreparation = XMLUtils.getStringAttributeValue(node, "Technology", 4096);
        if (stringTechnologyOfPreparation != null) {
            setTechnologyOfPreparation(stringTechnologyOfPreparation);
        }
        String stringTempOfPreparation = XMLUtils.getStringAttributeValue(node, "TempOfPreparation", 128);
        if (stringTempOfPreparation != null) {
            setTempOfPreparation(stringTempOfPreparation);
        }
        Integer integerLifeTime = XMLUtils.getIntegerAttributeValue(node, "LifeTime");
        if (integerLifeTime != null) {
            setLifeTime(integerLifeTime);
        }
        Float floatEnergyValue = XMLUtils.getFloatAttributeValue(node, "Energy");
        if (floatEnergyValue != null) {
            setEnergyValue(floatEnergyValue);
        }
        Float floatProteins = XMLUtils.getFloatAttributeValue(node, "Proteins");
        if (floatProteins != null) {
            setProteins(floatProteins);
        }
        Float floatCarbohydrates = XMLUtils.getFloatAttributeValue(node, "Carbohydrates");
        if (floatCarbohydrates != null) {
            setCarbohydrates(floatCarbohydrates);
        }
        Float floatFats = XMLUtils.getFloatAttributeValue(node, "Fats");
        if (floatFats != null) {
            setFats(floatFats);
        }
        Float floatMicroElCa = XMLUtils.getFloatAttributeValue(node, "Ca");
        if (floatMicroElCa != null) {
            setMicroElCa(floatMicroElCa);
        }
        Float floatMicroElMg = XMLUtils.getFloatAttributeValue(node, "Mg");
        if (floatMicroElMg != null) {
            setMicroElMg(floatMicroElMg);
        }
        Float floatMicroElP = XMLUtils.getFloatAttributeValue(node, "P");
        if (floatMicroElP != null) {
            setMicroElP(floatMicroElP);
        }
        Float floatMicroElFe = XMLUtils.getFloatAttributeValue(node, "Fe");
        if (floatMicroElFe != null) {
            setMicroElFe(floatMicroElFe);
        }
        Float floatVitaminA = XMLUtils.getFloatAttributeValue(node, "VA");
        if (floatVitaminA != null) {
            setVitaminA(floatVitaminA);
        }
        Float floatVitaminB1 = XMLUtils.getFloatAttributeValue(node, "VB1");
        if (floatVitaminB1 != null) {
            setVitaminB1(floatVitaminB1);
        }
        Float floatVitaminB2 = XMLUtils.getFloatAttributeValue(node, "VB2");
        if (floatVitaminB2 != null) {
            setVitaminB2(floatVitaminB2);
        }
        Float floatVitaminPp = XMLUtils.getFloatAttributeValue(node, "VPp");
        if (floatVitaminPp != null) {
            setVitaminPp(floatVitaminPp);
        }
        Float floatVitaminC = XMLUtils.getFloatAttributeValue(node, "VC");
        if (floatVitaminC != null) {
            setVitaminC(floatVitaminC);
        }
        Float floatVitaminE = XMLUtils.getFloatAttributeValue(node, "VE");
        if (floatVitaminE != null) {
            setVitaminE(floatVitaminE);
        }
        guidOfTMG = XMLUtils.getStringAttributeValue(node, "GuidOfTMG", 36);
        setSendAll(SendToAssociatedOrgs.SendToAll);
        return this;
    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {
        TechnologicalMapGroup tmg = DAOUtils.findDistributedObjectByRefGUID(TechnologicalMapGroup.class, session, guidOfTMG);
        if(tmg==null) throw new DistributedObjectException("NOT_FOUND_VALUE");
        setTechnologicalMapGroup(tmg);
        try {
            idOfConfigurationProvider = ConfigurationProviderService.extractIdOfConfigurationProviderByIdOfOrg(session, idOfOrg);
        } catch (Exception e) {
            throw new DistributedObjectException(e.getMessage());
        }
    }

    public Set<Good> getGoodInternal() {
        return goodInternal;
    }

    public void setGoodInternal(Set<Good> goodInternal) {
        this.goodInternal = goodInternal;
    }

    public List<TechnologicalMapProduct> getTechnologicalMapProduct() {
        return Collections.unmodifiableList(new ArrayList<TechnologicalMapProduct>(getTechnologicalMapProductInternal()));
    }

    public void addTechnologicalMapProduct(TechnologicalMapProduct technologicalMapProduct) {
        technologicalMapProductInternal.add(technologicalMapProduct);
    }

    public void removeTechnologicalMapProduct(TechnologicalMapProduct technologicalMapProduct) {
        technologicalMapProductInternal.remove(technologicalMapProduct);
    }

    private Set<TechnologicalMapProduct> getTechnologicalMapProductInternal() {
        return technologicalMapProductInternal;
    }

    private void setTechnologicalMapProductInternal(Set<TechnologicalMapProduct> technologicalMapProductInternal) {
        this.technologicalMapProductInternal = technologicalMapProductInternal;
    }

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

    public String getNumberOfTechnologicalMap() {
        return numberOfTechnologicalMap;
    }

    public void setNumberOfTechnologicalMap(String numberOfTechnologicalMap) {
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

    //public Long getIdOfConfigurationProvider() {
    //    return idOfConfigurationProvider;
    //}
    //
    //public void setIdOfConfigurationProvider(Long idOfConfigurationProvider) {
    //    this.idOfConfigurationProvider = idOfConfigurationProvider;
    //}

    public TechnologicalMapGroup getTechnologicalMapGroup() {
        return technologicalMapGroup;
    }

    public User getUserEdit() {
        return userEdit;
    }

    public void setUserEdit(User userEdit) {
        this.userEdit = userEdit;
    }

    public User getUserDelete() {
        return userDelete;
    }

    public void setUserDelete(User userDelete) {
        this.userDelete = userDelete;
    }

    public User getUserCreate() {
        return userCreate;
    }

    public void setUserCreate(User userCreate) {
        this.userCreate = userCreate;
    }

    public void setTechnologicalMapGroup(TechnologicalMapGroup technologicalMapGroup) {
        this.technologicalMapGroup = technologicalMapGroup;
    }

    public Integer getLifeTime() {
        return lifeTime;
    }

    public void setLifeTime(Integer lifeTime) {
        this.lifeTime = lifeTime;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("TechnologicalMap");
        sb.append("{nameOfTechnologicalMap='").append(nameOfTechnologicalMap).append('\'');
        sb.append(", numberOfTechnologicalMap=").append(numberOfTechnologicalMap);
        sb.append(", technologyOfPreparation='").append(technologyOfPreparation).append('\'');
        sb.append(", timeOfRealization='").append(timeOfRealization).append('\'');
        sb.append(", tempOfPreparation='").append(tempOfPreparation).append('\'');
        sb.append(", proteins=").append(proteins);
        sb.append(", carbohydrates=").append(carbohydrates);
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
        sb.append(", technologicalMapGroup=").append(technologicalMapGroup);
        sb.append(", idOfConfigurationProvider=").append(idOfConfigurationProvider);
        sb.append(", technologicalMapProductInternal=").append(technologicalMapProductInternal);
        sb.append(", userCreate=").append(userCreate);
        sb.append(", userDelete=").append(userDelete);
        sb.append(", userEdit=").append(userEdit);
        sb.append(", guidOfTMG='").append(guidOfTMG).append('\'');
        sb.append(", lifeTime=").append(lifeTime);
        sb.append('}');
        return sb.toString();
    }
}
