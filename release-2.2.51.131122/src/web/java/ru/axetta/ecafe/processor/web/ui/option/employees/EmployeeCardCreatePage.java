package ru.axetta.ecafe.processor.web.ui.option.employees;

import ru.axetta.ecafe.processor.core.daoservices.employees.CardItem;
import ru.axetta.ecafe.processor.core.daoservices.employees.EmployeeServiceBean;
import ru.axetta.ecafe.processor.core.daoservices.employees.VisitorItem;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 14.08.13
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class EmployeeCardCreatePage extends BasicWorkspacePage implements SelectEmployee{

    private final static Logger LOGGER = LoggerFactory.getLogger(EmployeeCardCreatePage.class);

    @Autowired
    private EmployeeServiceBean serviceBean;

    private CardItem card;
    private final CardOperationStationMenu cardOperationStationMenu = new CardOperationStationMenu();

    @Override
    public void onShow() throws Exception {
        card = new CardItem();
    }

    @Override
    public void completeSelection(VisitorItem employee) {
        if(employee!=null){
            card.setVisitorItem(employee);
        }
    }

    public Object save(){
        try {
            Long id = serviceBean.saveEmployeeCard(card, card.getVisitorItem().getIdOfVisitor());
            printMessage("Данные успешно сохранены");
        } catch (Exception e) {
            printError("Ошибка при сохранении: "+e.getMessage());
            LOGGER.error("Error by update employee info:",e);
        }
        return null;
    }

    public CardItem getCard() {
        return card;
    }

    public CardOperationStationMenu getCardOperationStationMenu() {
        return cardOperationStationMenu;
    }

    @Override
    public String getPageFilename() {
        return "option/employees/card/create";
    }
}
