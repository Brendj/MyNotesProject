package ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings;

import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSetting;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingItem;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger logger = LoggerFactory.getLogger(AbstractParserBySettingValue.class);
    private String[] values;

    protected AbstractParserBySettingValue(String[] values) throws ParseException {
        parse(values);
        this.values = values;
    }

    protected abstract void parse(String[] values) throws ParseException;

    public abstract String build();
    public abstract boolean check();
    protected abstract int getECafeSettingArrayCapacity();
    protected abstract Integer getOrgSettingTypeByIndex(Integer index);
    protected abstract Integer getIndexByOrgSettingType(Integer type);

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
            item.setSettingType(getOrgSettingTypeByIndex(i));

            result.add(item);
        }
        return result;
    }

    public String[] buildChangedValueByOrgSetting(OrgSetting setting) {
        String[] result = new String[getECafeSettingArrayCapacity()];
        for(OrgSettingItem val : setting.getOrgSettingItems()){
            try {
                Integer index = getIndexByOrgSettingType(val.getSettingType());
                if (result.length <= index) {
                    throw new IndexOutOfBoundsException(
                            String.format("By settingType %d get index %d , but resultArray length is %d", val.getSettingType(), index, result.length));
                }
                result[index] = val.getSettingValue();
            } catch (IndexOutOfBoundsException e){
                logger.error("Error in cycle: ", e);
                logger.warn("After IndexOutOfBoundsException cycle is continue");
            }
        }
        int len = values.length;
        for(int i = 0; i < len; i++){
            if(result[i] == null){
                result[i] = values[i];
            }
        }
        return result;
    }
}
