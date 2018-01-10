package ru.axetta.ecafe.processor.web.ui.option.employees;

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
public class EmployeeListPage extends BasicWorkspacePage{

    @Autowired
    private EmployeeServiceBean serviceBean;
    @Autowired
    private EmployeeGroupPage employeeGroupPage;

    private List<VisitorItem> employees;
    // 0 - показывать всех, 1 - актуальных, 2 - удаленных.
    private int showMode = 1;

    private String filter;

    public int getShowMode() {
        return showMode;
    }

    public void setShowMode(int showMode) {
        this.showMode = showMode;
    }

    @Override
    public void onShow() throws Exception {
        if (showMode == 0) {
            employees = serviceBean.findAllEmployees();
        } else {
            employees = showMode == 1 ? serviceBean.findAllEmployees(false) : serviceBean.findAllEmployees(true);
        }
    }

    public Object clearFilter() throws Exception {
        setFilter("");
        onShow();
        return null;
    }

    public List<VisitorItem> getEmployees() {
        return employees;
    }

    @Override
    public String getPageFilename() {
        return "option/employees/employee/list";
    }

    public Object deleteEmployee() {
        serviceBean.deleteEmployee(employeeGroupPage.getCurrentEmployee().getIdOfVisitor());
        return null;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        serviceBean.setFilter(filter);
        this.filter = filter;
    }
}
