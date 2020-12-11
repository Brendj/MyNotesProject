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

import org.apache.commons.lang.StringUtils;
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
public class CodeMSPEditPage extends BasicWorkspacePage {
    private static final Logger log = LoggerFactory.getLogger(CodeMSPEditPage.class);

    private Long idOfCode;
    private Integer code;
    private String description;
    private CodeMSP codeMSP;
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

    public void save(){
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;

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

        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            CodeMSP codeMSP = (CodeMSP) persistenceSession.merge(this.codeMSP);

            codeMSP.setCode(code);
            codeMSP.setDescription(description);

            if(selectedDiscount != null){
                CategoryDiscount discount = (CategoryDiscount) persistenceSession.get(CategoryDiscount.class, selectedDiscount);
                codeMSP.setCategoryDiscount(discount);
            }

            persistenceSession.update(codeMSP);

            persistenceTransaction.commit();
            persistenceTransaction = null;

            printMessage("Данные обновлены.");
        } catch (Exception e){
            log.error("Error saving changes", e);
            printError(e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, log);
            HibernateUtils.close(persistenceSession, log);
        }
    }

    @Override
    public void onShow() throws Exception {
        reload();
    }

    public void reload(){
        Session session = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();

            this.codeMSP = (CodeMSP) session.get(CodeMSP.class, idOfCode);
            this.code = codeMSP.getCode();
            this.description = codeMSP.getDescription();
            this.selectedDiscount = codeMSP.getCategoryDiscount().getIdOfCategoryDiscount();

            session.close();
        } catch (Exception e) {
            log.error("Can't reload code MSP", e);
        } finally {
            HibernateUtils.close(session, log);
        }
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

    public Long getIdOfCode() {
        return idOfCode;
    }

    public void setIdOfCode(Long idOfCode) {
        this.idOfCode = idOfCode;
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

    @Override
    public String getPageFilename() {
        return "option/msp/edit";
    }
}
