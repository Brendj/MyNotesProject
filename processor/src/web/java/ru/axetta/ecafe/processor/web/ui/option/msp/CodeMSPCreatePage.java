/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.msp;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;
import ru.axetta.ecafe.processor.core.persistence.CodeMSP;
import ru.axetta.ecafe.processor.core.persistence.CodeMspAgeTypeGroup;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.model.SelectItem;
import java.util.LinkedList;
import java.util.List;

@Component
@Scope("session")
public class CodeMSPCreatePage extends BasicWorkspacePage {
    private static final Logger log = LoggerFactory.getLogger(CodeMSPCreatePage.class);
    
    private Integer code;
    private String description;
    private Long selectedDiscount;

    private List<SelectItem> discounts = loadDiscounts();
    private List<SelectItem> ageTypeGroups = new LinkedList<>();
    private List<String> selectedTypes;

    private List<SelectItem> loadAgeTypesGroups() {
        Session session = null;
        List<SelectItem> groups = new LinkedList<>();
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();

            List<String> result = DAOUtils.getAllAgeTypeGroups(session);
            for(String group : result){
                groups.add(new SelectItem(group, group));
            }

        } catch (Exception e){
            log.error("Can't load AgeGroup types from DB", e);
        } finally {
            HibernateUtils.close(session, log);
        }
        return groups;
    }

    private List<SelectItem> loadDiscounts() {
        List<SelectItem> result = new LinkedList<>();
        try {
            List<CategoryDiscount> categoryDiscountList = DAOReadonlyService.getInstance()
                    .getCategoryDiscountListNotDeletedTypeDiscount();

            result.add(new SelectItem(null, ""));

            for(CategoryDiscount c : categoryDiscountList){
                SelectItem item = new SelectItem(
                        c.getIdOfCategoryDiscount(),
                        c.getCategoryName());
                result.add(item);
            }

        } catch (Exception e){
            log.error("Can't load categoryDiscounts", e);
        }
        return result;
    }

    public List<String> getSelectedTypes() {
        return selectedTypes;
    }

    public void setSelectedTypes(List<String> selectedTypes) {
        this.selectedTypes = selectedTypes;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getSelectedDiscount() {
        return selectedDiscount;
    }

    public void setSelectedDiscount(Long selectedDiscount) {
        this.selectedDiscount = selectedDiscount;
    }

    public void setDiscounts(List<SelectItem> discounts) {
        this.discounts = discounts;
    }

    public List<SelectItem> getDiscounts() {
        return discounts;
    }

    public List<SelectItem> getAgeTypeGroups() {
        if(ageTypeGroups.isEmpty()){
            ageTypeGroups = loadAgeTypesGroups();
        }
        return ageTypeGroups;
    }

    public void setAgeTypeGroups(List<SelectItem> ageTypeGroups) {
        this.ageTypeGroups = ageTypeGroups;
    }

    @Override
    public String getPageFilename() {
        return "option/msp/create";
    }

    public void onSave() {
        if(code == null || code.equals(0)){
            printError("Введите код МСП");
            return;
        }
        if (StringUtils.isEmpty(description)) {
            printError("Добавьте описание кода МСП");
            return;
        }
        if(selectedDiscount == null || selectedDiscount.equals(0L)){
            printError("Укажите льготу");
            return;
        }
        if(CollectionUtils.isEmpty(selectedTypes)){
            printError("Укажите хотябы одну возрастную категорию");
            return;
        }

        Session session = null;
        Transaction transaction = null;

        try{
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            CodeMSP codeMSP = new CodeMSP();
            codeMSP.setDescription(description);
            codeMSP.setCode(code);

            if(selectedDiscount != null){
                CategoryDiscount discount = (CategoryDiscount) session.get(CategoryDiscount.class, selectedDiscount);
                codeMSP.setCategoryDiscount(discount);
            }

            for(String type :  selectedTypes){
                CodeMspAgeTypeGroup group = new CodeMspAgeTypeGroup();
                group.setCodeMSP(codeMSP);
                group.setAgeTypeGroup(type);

                codeMSP.getCodeMspAgeTypeGroupSet().add(group);
            }

            session.save(codeMSP);

            transaction.commit();
            transaction = null;

            session.close();

            printMessage("Запись создана");
        } catch (Exception e){
            printError("Невозможно создать код МСП: " + e.getMessage());
            log.error("Can't create MSP code", e);
        } finally {
            HibernateUtils.rollback(transaction, log);
            HibernateUtils.close(session, log);
        }
    }
}
