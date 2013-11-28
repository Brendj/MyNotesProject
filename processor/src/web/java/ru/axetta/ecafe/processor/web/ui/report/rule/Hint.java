package ru.axetta.ecafe.processor.web.ui.report.rule;

import ru.axetta.ecafe.processor.core.RuleProcessor;
import ru.axetta.ecafe.processor.core.persistence.RuleCondition;
import ru.axetta.ecafe.processor.core.report.ReportRuleConstants;
import ru.axetta.ecafe.processor.core.report.RuleConditionItem;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 28.11.13
 * Time: 15:43
 * To change this template use File | Settings | File Templates.
 */
public class Hint {
    //  Типы
    public static final String CONTRAGENT = "contragent";
    public static final String CONTRAGENT_PAYAGENT = "contragent-payagent";
    public static final String CONTRAGENT_RECEIVER = "contragent-receiver";
    public static final String CONTRACT   = "contract";
    public static final String ORG        = "org";
    public static final String CLIENT     = "client";

    //
    private ReportRuleConstants.ParamHintWrapper hint;
    private String value;
    private List<SelectItem> listItems = new ArrayList<SelectItem>();
    private List <String> valueItems = new ArrayList <String> ();
    private String type;

    public Hint (ReportRuleConstants.ParamHintWrapper hint) {
        this.hint = hint;
        value = "";
    }

    public void fill (RuleConditionItem defaultRule, RuleCondition actualRule) {
        Map<String, String> defParams = RuleProcessor.getParametersFromString(defaultRule.getConditionConstant());

        if (defaultRule.getConditionConstant().startsWith(RuleProcessor.CONTRAGENT_PAYAGENT_EXPRESSION)) {
            type = CONTRAGENT_PAYAGENT;
        }
        else if (defaultRule.getConditionConstant().startsWith(RuleProcessor.CONTRAGENT_RECEIVER_EXPRESSION)) {
            type = CONTRAGENT_RECEIVER;
        }
        else if (defaultRule.getConditionConstant().startsWith(RuleProcessor.CONTRAGENT_EXPRESSION)) {
            type = "contragent";
        }
        else if (defaultRule.getConditionConstant().startsWith(RuleProcessor.ORG_EXPRESSION)) {
            type = ORG;
        }
        else if (defaultRule.getConditionConstant().indexOf(RuleProcessor.COMBOBOX_EXPRESSION) >= 0) {
            type = "combobox";
            SelectItem emptyItem = new SelectItem("", "");
            listItems.add(emptyItem);
            for (String key : defParams.keySet()) {
                String val = defParams.get(key);
                SelectItem item = new SelectItem(key, val);
                listItems.add(item);
            }
            if (actualRule != null && actualRule.getConditionConstant().length() > 0) {
                value = actualRule.getConditionConstant();
            }
        } else if (defaultRule.getConditionConstant().indexOf(RuleProcessor.CHECKBOX_EXPRESSION) >= 0) {
            type = "checkbox";
            for (String key : defParams.keySet()) {
                String val = defParams.get(key);
                SelectItem item = new SelectItem(key, val);
                listItems.add(item);
            }
            if (actualRule != null && actualRule.getConditionConstant().length() > 0) {
                String vals [] = actualRule.getConditionConstant().split(",");
                for (String v : vals) {
                    valueItems.add(v);
                }
            }
        } else if (defaultRule.getConditionConstant().indexOf(RuleProcessor.RADIO_EXPRESSION) >= 0) {
            type = "radio";
            for (String key : defParams.keySet()) {
                String val = defParams.get(key);
                SelectItem item = new SelectItem(key, val);
                listItems.add(item);
            }
            if (actualRule != null && actualRule.getConditionConstant().length() > 0) {
                value = actualRule.getConditionConstant();
            }
        } else if (defaultRule.getConditionConstant().indexOf(RuleProcessor.INPUT_EXPRESSION) >= 0) {
            type = "input";

            for (String key : defParams.keySet()) {
                value = defParams.get(key);
                break;
            }
            if (actualRule != null && actualRule.getConditionConstant().length() > 0) {
                value = actualRule.getConditionConstant();
            }
        } else {
            type = "output";
            for (String key : defParams.keySet()) {
                value = defParams.get(key);
                break;
            }
            if (actualRule != null && actualRule.getConditionConstant().length() > 0) {
                value = actualRule.getConditionConstant();
            }
        }
    }

    public ReportRuleConstants.ParamHintWrapper getHint() {
        return hint;
    }

    public void setHint(ReportRuleConstants.ParamHintWrapper hint) {
        this.hint = hint;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<SelectItem> getListItems() {
        return listItems;
    }

    public void setListItems(List<SelectItem> listItems) {
        this.listItems = listItems;
    }

    public List<String> getValueItems() {
        return valueItems;
    }

    public void setValueItems(List<String> valueItems) {
        this.valueItems = valueItems;
    }

    public String getType () {
        if (type != null) {
            return type;
        }
        return getType(hint.getParamHint().getName());
    }

    public boolean isSuperType () {
        return isSuperType(getType());
    }

    public static String getType (String name) {
        if (name.equals("idOfContragent")) {
            return CONTRAGENT;
        } else if (name.equals("idOfContract")) {
            return CONTRACT;
        } else if (name.equals("idOfOrg")) {
            return ORG;
        } else if (name.equals("idOfClient")) {
            return CLIENT;
        }
        return "";
    }

    public static boolean isSuperType (String type) {
        if (type.equals(CONTRAGENT) || type.equals(CONTRAGENT_PAYAGENT) ||
                type.equals(CONTRAGENT_RECEIVER) || type.equals(CONTRACT) ||
                type.equals(ORG) || type.equals(CLIENT)) {
            return true;
        }
        return false;
    }
}
