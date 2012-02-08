/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.category;

import ru.axetta.ecafe.processor.core.persistence.CategoryOrg;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.richfaces.component.html.HtmlPanelMenuGroup;
import org.richfaces.component.html.HtmlPanelMenuItem;
import org.springframework.stereotype.Component;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: damir
 * Date: 06.02.12
 * Time: 12:07
 * To change this template use File | Settings | File Templates.
 */
@Component

public class CategoryOrgViewPage extends BasicWorkspacePage {

    private Logger logger = Logger.getLogger("CategoryOrgViewPage");

    private Long selectedIdOfCategoryOrg;
    private CategoryOrg currCategoryOrg;
    private List<Org> orgList = Collections.emptyList();
    private List<String> names = Collections.emptyList();

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    @PersistenceContext
    EntityManager entityManager;

    public String getPageFilename() {
        return "option/orgcategories/view";
    }

    //super.getPageTitle() - пуста так как предыдущая ссылка не является компонентом mainMenu
   /* public String getPageTitle() {
        return super.getPageTitle() + currCategoryOrg.getCategoryName();
    }*/

    @Override
    public void onShow() throws Exception {
        orgList = new LinkedList<Org>();
        if(currCategoryOrg.getOrgs().isEmpty()){
            printMessage("У категории нет орагнизаций.");
        } else{
            for(Org org:  currCategoryOrg.getOrgs()){
                orgList.add(org);
            }
        }


    }

    public Long getSelectedIdOfCategoryOrg() {
        return selectedIdOfCategoryOrg;
    }

    public void setSelectedIdOfCategoryOrg(Long selectedIdOfCategoryOrg) {
        this.selectedIdOfCategoryOrg = selectedIdOfCategoryOrg;
    }

    public CategoryOrg getCurrCategoryOrg() {
        return currCategoryOrg;
    }

    public List<Org> getOrgList() {
        return orgList;
    }

    public void setOrgList(List<Org> orgList) {
        this.orgList = orgList;
    }

    public void setCurrCategoryOrg(CategoryOrg currCategoryOrg) {
        this.currCategoryOrg = currCategoryOrg;
    }
}
