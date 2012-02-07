/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.category;

import ru.axetta.ecafe.processor.core.persistence.CategoryOrg;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.apache.cxf.annotations.Logging;
import org.richfaces.component.html.HtmlPanelMenuGroup;
import org.springframework.stereotype.Component;

import javax.faces.component.UIComponent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: damir
 * Date: 07.02.12
 * Time: 14:59
 * To change this template use File | Settings | File Templates.
 */
@Component

public class SelectedCategoryOrgGroupPage extends BasicWorkspacePage {

    private Logger logger = Logger.getLogger("SelectedCategoryOrgGroupPage");
    
    private Long currIdOfCategoryOrg;
    private String categoryName;

    @PersistenceContext
    EntityManager entityManager;
    
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public void onShow() throws Exception {
        CategoryOrg categoryOrg = entityManager.find(CategoryOrg.class, currIdOfCategoryOrg);
        if(null == categoryOrg){
            categoryName="Без категории";
        } else {
            categoryName=categoryOrg.getCategoryName();
        }
    }

    public Long getCurrIdOfCategoryOrg() {
        return currIdOfCategoryOrg;
    }

    public void setCurrIdOfCategoryOrg(Long currIdOfCategoryOrg) {
        this.currIdOfCategoryOrg = currIdOfCategoryOrg;
    }
}
