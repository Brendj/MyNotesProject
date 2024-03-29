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
public class EmployeeCardViewPage extends BasicWorkspacePage{

    @Autowired
    private EmployeeServiceBean serviceBean;
    @Autowired
    private EmployeeCardGroupPage employeeCardGroupPage;

    private CardItem card;

    @Override
    public void onShow() throws Exception {
        card = employeeCardGroupPage.getCurrentCard();
        if(card.getVisitorItem()==null){
            VisitorItem visitorItem = serviceBean.getEmployeeByCard(card.getId());
            card.setVisitorItem(visitorItem);
        }
    }

    public CardItem getCard() {
        return card;
    }

    @Override
    public String getPageFilename() {
        return "option/employees/card/view";
    }
}
