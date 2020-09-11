/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils;

import ru.axetta.ecafe.processor.core.persistence.RegistryChangeGuardians;
import ru.axetta.ecafe.processor.core.service.ImportRegisterMSKClientsService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class FieldProcessor {
    final static int UPDATABLE=0, NON_UPDATABLE=1;
    public static class Def {
        public Object fieldId;
        public int defaultPos;
        public boolean requiredForInsert;
        public boolean requiredForUpdate;
        public String fieldName;
        public String defValue;
        public int realPos=-1;
        public String currentValue;
        public List<ImportRegisterMSKClientsService.GuardianInfo> objectList;
        public Set<RegistryChangeGuardians> guardiansSet;
        public boolean updatable;

        public Def(int defaultPos, boolean requiredForInsert, boolean requiredForUpdate, String fieldName, String defValue, Object fieldId, boolean updatable) {
            this.defaultPos = defaultPos;
            this.requiredForInsert = requiredForInsert;
            this.requiredForUpdate = requiredForUpdate;
            this.fieldName = fieldName;
            this.defValue = defValue;
            this.fieldId = fieldId;
            this.updatable = updatable;
        }
        boolean isIncluded() { return realPos!=-1; }
        public boolean isUpdatable() {
            return updatable;
        }

        public String getValue() {
            if (currentValue==null) return defValue;
            return currentValue;
        }

        public String getCurrentValue() {
            return currentValue;
        }

        @Override
        protected Object clone() {
            return new Def(defaultPos, requiredForInsert, requiredForUpdate, fieldName, defValue, fieldId, updatable);
        }
    }

    public static class Config {
        protected DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        protected Def[] currentConfig;
        public Config(Def[] fieldInfo, boolean useDefaultValues) {
            currentConfig = new Def[fieldInfo.length];
            for (int n=0;n<fieldInfo.length;++n) {
                currentConfig[n]=(Def)fieldInfo[n].clone();
                if (!useDefaultValues) currentConfig[n].defValue=null;
            }
        }
        protected int nFields=0;
        public void registerField(String name) throws Exception {
            Def field = getField(name);
            field.realPos = nFields++;
        }
        public Def getField(String name) throws Exception {
            for (Def fd : currentConfig) {
                if (0==fd.fieldName.compareToIgnoreCase(name)) return fd;
            }
            throw new Exception("Неизвестный атрибут: "+name);
        }
        public Def getField(Object id) throws Exception {
            for (Def fd : currentConfig) {
                if (fd.fieldId==id) return fd;
            }
            throw new Exception("Неизвестный атрибут: "+id);
        }

        public void setValues(String[] tokens) throws Exception {
            if (nFields>0) {
                if (tokens.length!=nFields) throw new Exception("Количество полей в строке не совпадает с заголовком");
            } else {
                if (tokens.length < 19) {
                    throw new Exception("Количество полей в строке меньше допустимого");
                }
            }
            for (int n=0;n<tokens.length;++n) {
                Def fd = null;
                if (nFields==0) fd = currentConfig[n];
                else {
                    for (int i=0;i<currentConfig.length;++i) {
                        if (nFields>0 && currentConfig[i].realPos==n) { fd = currentConfig[i]; break; }
                    }
                }
                fd.currentValue = tokens[n];
            }
        }

        public String getValue(Object id) {
            for (int n=0;n<currentConfig.length;++n) {
                if (currentConfig[n].fieldId==id) return currentConfig[n].getValue();
            }
            return null;
        }

        public List<ImportRegisterMSKClientsService.GuardianInfo> getValueList(Object id) {
            for (int n = 0; n < currentConfig.length; ++n) {
                if (currentConfig[n].fieldId == id) {
                    return currentConfig[n].objectList;
                }
            }
            return null;
        }

        public Set<RegistryChangeGuardians> getValueSet(Object id) {
            for (int n = 0; n < currentConfig.length; ++n) {
                if (currentConfig[n].fieldId == id) {
                    return currentConfig[n].guardiansSet;
                }
            }
            return null;
        }

        public boolean getValueBool(Object id) {
            String v = getValue(id);
            if (v==null) return false;
            return !v.equals("0");
        }

        public boolean isValueNull(Object id) {
            for (int n=0;n<currentConfig.length;++n) {
                if (currentConfig[n].fieldId==id) return (currentConfig[n].getCurrentValue() == null);
            }
            return true;
            //String v = getValue(id);
            //return (v == null);
        }

        public Date getValueDate(Object id) throws ParseException {
            String v= getValue(id);
            if (v.startsWith("#")) {
                Date today = CalendarUtils.truncateToDayOfMonth(new Date());
                if (v.equals("#CURRENT_DATE")) return today;
                int nYears = Integer.parseInt(v.substring(1));
                Calendar c = Calendar.getInstance();
                c.setTime(today);
                c.add(Calendar.YEAR, nYears);
                return c.getTime();
            }
            return dateFormat.parse(v);
        }

        public int getValueInt(Object id) throws Exception {
            String v = getValue(id);
            if (v==null) throw new Exception("Отсуствует обязательное поле: "+getField(id).fieldName);
            return Integer.parseInt(v);
        }

        public Long getValueLong(Object id) throws Exception {
            String v = getValue(id);
            if (v==null) throw new Exception("Отсуствует обязательное поле: "+getField(id).fieldName);
            return Long.parseLong(v);
        }

        public void checkRequiredFields() throws Exception {
        }

        public void setValue(Object id, Object value) throws Exception {
            if (value instanceof Date) {
                getField(id).currentValue = dateFormat.format((Date) value);
            } else {
                getField(id).currentValue = (null == value) ? null : value.toString();
            }
        }

        public void setValueList(Object id, List<ImportRegisterMSKClientsService.GuardianInfo> value) throws Exception {
            getField(id).objectList = value;
        }

        public void setValueSet(Object id, Set<RegistryChangeGuardians> value) throws Exception {
            getField(id).guardiansSet = value;
        }

        public void resetToDefaultValues(Def[] fieldInfo) throws Exception {
            for (Def def : fieldInfo) {
                setValue(def.fieldId, def.defValue);
            }
        }
    }

}
