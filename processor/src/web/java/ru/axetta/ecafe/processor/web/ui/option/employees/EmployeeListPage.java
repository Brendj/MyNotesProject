package ru.axetta.ecafe.processor.web.ui.option.employees;

import ru.axetta.ecafe.processor.core.daoservices.employees.EmployeeServiceBean;
import ru.axetta.ecafe.processor.core.daoservices.employees.VisitorItem;
import ru.axetta.ecafe.processor.core.persistence.Visitor;
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
public class EmployeeListPage extends BasicWorkspacePage{

    @Autowired
    private EmployeeServiceBean serviceBean;

    private List<VisitorItem> employees;

    @Override
    public void onShow() throws Exception {
        employees = serviceBean.findAllEmployees();
    }

    public List<VisitorItem> getEmployees() {
        return employees;
    }

    @Override
    public String getPageFilename() {
        return "option/employees/employee/list";
    }
}
