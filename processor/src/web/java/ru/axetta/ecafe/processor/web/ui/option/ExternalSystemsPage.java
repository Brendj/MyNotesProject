/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@Scope("session")
public class ExternalSystemsPage extends BasicWorkspacePage {

    @Autowired
    RuntimeContext runtimeContext;

    private Boolean enabled;
    private String url;
    private List<ExternalSystemItem> types;
    private List<ExternalSystemItem> operationTypes;

    public Object save() {
        try {
            runtimeContext.setOptionValue(Option.OPTION_EXTERNAL_SYSTEM_ENABLED, enabled);
            runtimeContext.setOptionValue(Option.OPTION_EXTERNAL_SYSTEM_ENABLED, url);
            runtimeContext.setOptionValue(Option.OPTION_EXTERNAL_SYSTEM_TYPES, getTypesString(types));
            runtimeContext.setOptionValue(Option.OPTION_EXTERNAL_SYSTEM_OPERATION_TYPES, getTypesString(operationTypes));

            runtimeContext.saveOptionValues();
        } catch (Exception e) {
            logAndPrintMessage("Ошибка при сохранении", e);
        }
        return null;
    }

    @Override
    public void onShow() throws Exception {
        enabled = runtimeContext.getOptionValueBool(Option.OPTION_EXTERNAL_SYSTEM_ENABLED);
        url = runtimeContext.getOptionValueString(Option.OPTION_EXTERNAL_SYSTEM_URL);
        types = getExternalTypes(runtimeContext.getOptionValueString(Option.OPTION_EXTERNAL_SYSTEM_TYPES),  Card.TYPE_NAMES);
        operationTypes = getExternalTypes(runtimeContext.getOptionValueString(Option.OPTION_EXTERNAL_SYSTEM_OPERATION_TYPES), Card.STATE_NAMES);
    }

    private List<ExternalSystemItem> getExternalTypes(String option, String[] names) {
        List<ExternalSystemItem> result = new ArrayList<ExternalSystemItem>();
        String[] s = option.split(",");
        List<String> list = new ArrayList<String>(Arrays.asList(s));
        for(int i = 0; i < names.length; i++) {
            result.add(new ExternalSystemItem(i, names[i], list.contains(Integer.valueOf(i).toString())));
        }
        return result;
    }

    private String getTypesString(List<ExternalSystemItem> list) {
        List<Integer> result = new ArrayList<Integer>();
        for(ExternalSystemItem item : list) {
            if(item.isEnabled()) {
                result.add(item.getType());
            }
        }
        Collections.sort(result);
        return StringUtils.join(result, ",");
    }

    @Override
    public String getPageFilename() {
        return "option/external_systems_page";
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<ExternalSystemItem> getTypes() {
        return types;
    }

    public void setTypes(List<ExternalSystemItem> types) {
        this.types = types;
    }

    public List<ExternalSystemItem> getOperationTypes() {
        return operationTypes;
    }

    public void setOperationTypes(List<ExternalSystemItem> operationTypes) {
        this.operationTypes = operationTypes;
    }
}
