/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FieldProcessor {
    public static class Def {
        public Object fieldId;
        public int defaultPos;
        public boolean required;
        public String fieldName;
        public String defValue;
        public int realPos=-1;
        public String currentValue;

        public Def(int defaultPos, boolean required, String fieldName, String defValue, Object fieldId) {
            this.defaultPos = defaultPos;
            this.required = required;
            this.fieldName = fieldName;
            this.defValue = defValue;
            this.fieldId = fieldId;
        }
        boolean isIncluded() { return realPos!=-1; }

        public String getValue() {
            if (currentValue==null) return defValue;
            return currentValue;
        }

        @Override
        protected Object clone() {
            return new Def(defaultPos, required, fieldName, defValue, fieldId);
        }
    }

    public static class Config {
        protected DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        protected Def[] currentConfig;
        public Config(Def[] fieldInfo) {
            currentConfig = new Def[fieldInfo.length];
            for (int n=0;n<fieldInfo.length;++n) currentConfig[n]=(Def)fieldInfo[n].clone();
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

        public boolean getValueBool(Object id) {
            String v = getValue(id);
            if (v==null) return false;
            return !v.equals("0");
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
    }

}
