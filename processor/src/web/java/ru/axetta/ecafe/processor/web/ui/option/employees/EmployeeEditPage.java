package ru.axetta.ecafe.processor.web.ui.option.employees;

import ru.axetta.ecafe.processor.core.daoservices.employees.CardItem;
import ru.axetta.ecafe.processor.core.daoservices.employees.EmployeeServiceBean;
import ru.axetta.ecafe.processor.core.daoservices.employees.VisitorItem;
import ru.axetta.ecafe.processor.core.persistence.Visitor;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class EmployeeEditPage extends BasicWorkspacePage{

    private final static Logger LOGGER = LoggerFactory.getLogger(EmployeeEditPage.class);

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

    public Object save(){
        try {
            Long id = serviceBean.saveEmployee(employee);
            printMessage("Данные успешно сохранены");
        } catch (Exception e) {
            printError("Ошибка при сохранении: "+e.getMessage());
            LOGGER.error("Error by update employee info:",e);
        }
        return null;
    }

    public Object clear() {
        employee = serviceBean.findEmployeesByIdOfVisitor(employee.getIdOfVisitor());
        employeeGroupPage.setCurrentEmployee(employee);
        super.show();
        return null;
    }

    public Object addCard(){
        return null;
    }

    public VisitorItem getEmployee() {
        return employee;
    }

    @Override
    public String getPageFilename() {
        return "option/employees/employee/edit";
    }
}
