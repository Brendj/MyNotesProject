package ru.axetta.ecafe.processor.web.ui.option.employees;

import ru.axetta.ecafe.processor.core.daoservices.employees.VisitorItem;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 14.08.13
 * Time: 17:41
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class EmployeeGroupPage extends BasicWorkspacePage {

    private VisitorItem currentEmployee;

    public VisitorItem getCurrentEmployee() {
        return currentEmployee;
    }

    public void setCurrentEmployee(VisitorItem currentEmployee) {
        this.currentEmployee = currentEmployee;
    }
}
