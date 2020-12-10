/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.msp;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;
import ru.axetta.ecafe.processor.core.persistence.CategoryDiscountDSZN;
import ru.axetta.ecafe.processor.core.persistence.CodeMSP;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.model.SelectItem;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Component
@Scope("session")
public class CodeMSPCreatePage extends BasicWorkspacePage {
    private static final Logger log = LoggerFactory.getLogger(CodeMSPCreatePage.class);
    
    private Integer code;
    private String description;
    private Long selectedDiscount;

    private List<SelectItem> discounts = loadDiscounts();

    private List<SelectItem> loadDiscounts() {
        List<SelectItem> result = new LinkedList<>();
        try {
            List<CategoryDiscountDSZN> categoryDiscountDSZNList = DAOService
                    .getInstance().getCategoryDiscountDSZNList();
            Set<CategoryDiscount> set = new HashSet<>();

            result.add(new SelectItem(null, ""));

            for(CategoryDiscountDSZN category : categoryDiscountDSZNList){
                if(category.getCategoryDiscount() != null){
                    set.add(category.getCategoryDiscount());
                }
            }

            for(CategoryDiscount c : set){
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

    @Override
    public String getPageFilename() {
        return "option/msp/create";
    }

    public void onSave() {
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
