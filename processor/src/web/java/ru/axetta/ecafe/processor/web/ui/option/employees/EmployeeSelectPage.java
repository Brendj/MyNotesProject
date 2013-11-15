/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.employees;

import ru.axetta.ecafe.processor.core.daoservices.employees.EmployeeServiceBean;
import ru.axetta.ecafe.processor.core.daoservices.employees.VisitorItem;
import ru.axetta.ecafe.processor.web.ui.BasicPage;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 14.08.13
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class EmployeeSelectPage  extends BasicPage {

    @Autowired
    private EmployeeServiceBean serviceBean;

    private VisitorItem selectEmployee;
    private List<VisitorItem> employees;

    public List<VisitorItem> getEmployees() {
        return employees;
    }

    public VisitorItem getSelectEmployee() {
        return selectEmployee;
    }

    public void setSelectEmployee(VisitorItem selectEmployee) {
        this.selectEmployee = selectEmployee;
    }

    public Object completeSelection(){
        BasicWorkspacePage basicWorkspacePage = MainPage.getSessionInstance().getCurrentWorkspacePage();
        SelectEmployee selectEmployeePanel = (SelectEmployee) basicWorkspacePage;
        selectEmployeePanel.completeSelection(selectEmployee);
        return null;
    }

    public Object show() {
        employees = serviceBean.findAllEmployees(false);
        MainPage.getSessionInstance().registerModalPageShow(this);
        return null;
    }

    public Object hide() {
        MainPage.getSessionInstance().registerModalPageHide(this);
        return null;
    }

}
