package ru.axetta.ecafe.processor.web.ui.option.discountrule;

import ru.axetta.ecafe.processor.core.persistence.ComplexRole;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 26.04.13
 * Time: 13:59
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class ComplexRuleEditPage extends BasicWorkspacePage {

    @Autowired
    private DAOService daoService;
    private List<ComplexRole> complexRoles;

    @Override
    public void onShow() throws Exception {
        complexRoles = DAOReadonlyService.getInstance().findComplexRoles();
    }

    public Object updateRule(){
        try {
            complexRoles = daoService.updateComplexRoles(complexRoles);
            printMessage("Данные обновлены успешно");
        } catch (Exception e){
            printError("Ошибка при подготовке страницы");
            getLogger().error("Failed to load page", e);
        }
        return null;
    }


    @Override
    public String getPageFilename() {
        return "option/discountrule/complex_roles";
    }

    public List<ComplexRole> getComplexRoles() {
        return complexRoles;
    }
}
