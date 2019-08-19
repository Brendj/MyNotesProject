package ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings;

import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSetting;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingItem;

import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 18.12.13
 * Time: 16:43
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractParserBySettingValue {

    private String[] values;

    protected AbstractParserBySettingValue(String[] values) throws ParseException {
        parse(values);
        this.values = values;
    }

    protected abstract void parse(String[] values) throws ParseException;

    public abstract String build();
    public abstract boolean check();

    public Set<OrgSettingItem> buildSetOfOrgSettingItem(OrgSetting setting, Long orgSettingNextVersion){
        Set<OrgSettingItem> result = new HashSet<>();
        Date now = new Date();
        for(int i = 0; i < values.length; i++){
            if(StringUtils.isBlank(values[i])){
                continue;
            }
            OrgSettingItem item = new OrgSettingItem();
            item.setOrgSetting(setting);
            item.setCreatedDate(now);
            item.setLastUpdate(now);
            item.setVersion(orgSettingNextVersion);

            item.setSettingValue(values[i]);
            item.setSettingType(gettypeByIndex(i));

            result.add(item);
        }
        return result;
    }

    protected abstract Integer gettypeByIndex(Integer index);
}
