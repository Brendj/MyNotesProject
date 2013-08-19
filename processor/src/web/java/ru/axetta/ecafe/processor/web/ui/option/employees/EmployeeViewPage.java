package ru.axetta.ecafe.processor.web.ui.option.employees;

import ru.axetta.ecafe.processor.core.daoservices.employees.CardItem;
import ru.axetta.ecafe.processor.core.daoservices.employees.EmployeeServiceBean;
import ru.axetta.ecafe.processor.core.daoservices.employees.VisitorItem;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

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
public class EmployeeViewPage extends BasicWorkspacePage{

    @Autowired
    private EmployeeServiceBean serviceBean;
    @Autowired
    private EmployeeGroupPage employeeGroupPage;

    private VisitorItem employee;

    @Override
    public void onShow() throws Exception {
        employee = employeeGroupPage.getCurrentEmployee();
        List<CardItem> cardItems = serviceBean.findCardsByEmployee(employee.getIdOfVisitor());
        employee.clearCardItems();
        employee.addCard(cardItems);
    }

    public VisitorItem getEmployee() {
        return employee;
    }

    @Override
    public String getPageFilename() {
        return "option/employees/employee/view";
    }
}
