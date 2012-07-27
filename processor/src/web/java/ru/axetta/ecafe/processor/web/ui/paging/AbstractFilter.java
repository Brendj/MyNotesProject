/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.paging;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 26.07.12
 * Time: 23:47
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractFilter {

    public static final String GETTER_PREFIX = "get";
    public static final String SETTER_PREFIX = "set";
    protected Field[] fields;

    public static class FieldInfo {

        int filteringType;
        String name;
        Object value;
        Object lo, hi;

        public FieldInfo(String name, int filteringType, Object value) {
            this.name = name;
            this.filteringType = filteringType;
            this.value = value;
        }

        public int getFilteringType() {
            return filteringType;
        }

        public void setFilteringType(int filteringType) {
            this.filteringType = filteringType;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public Object getLo() {
            return lo;
        }

        public void setLo(Object lo) {
            this.lo = lo;
        }

        public Object getHi() {
            return hi;
        }

        public void setHi(Object hi) {
            this.hi = hi;
        }
    }

    final protected Class clazz;

    private AbstractFilter() {
        this.clazz = null;
    }

    public AbstractFilter(Class clazz) {
        this.clazz = clazz;
    }

    public String createGetterName(String fieldName) {
        String getterName = GETTER_PREFIX + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        return getterName;
    }

    public String createSetterName(String fieldName) {
        String getterName = SETTER_PREFIX + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        return getterName;
    }

    public List retrieveList(Session session)
            throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        List<FieldInfo> properties = new LinkedList<FieldInfo>();
        if (fields == null) {
            fields = this.getClass().getDeclaredFields();
        }
        for (Field field : fields) {
            if (field.isAnnotationPresent(FilterProperty.class)) {
                FilterProperty filterProperty = field.getAnnotation(FilterProperty.class);
                Method method = this.getClass().getMethod(createGetterName(field.getName()));
                properties.add(new FieldInfo(field.getName(), filterProperty.filteringType(), method.invoke(this)));
            }
        }

        Criteria criteria = session.createCriteria(clazz);

        for (FieldInfo fieldInfo : properties) {
            if (fieldInfo.getValue() == null) {
                continue;
            }
            SimpleExpression se = null;
            switch (fieldInfo.getFilteringType()) {

                case FilterProperty.EQUALITY:
                    se = Restrictions.eq(fieldInfo.getName(), fieldInfo.getValue());
                    break;
                case FilterProperty.GREATER_OR_EQUAL:
                    se = Restrictions.ge(fieldInfo.getName(), fieldInfo.getValue());
                    break;
                case FilterProperty.GREATER:
                    se = Restrictions.gt(fieldInfo.getName(), fieldInfo.getValue());
                    break;
                case FilterProperty.LESS_OR_EQUAL:
                    se = Restrictions.le(fieldInfo.getName(), fieldInfo.getValue());
                    break;
                case FilterProperty.LESS:
                    se = Restrictions.lt(fieldInfo.getName(), fieldInfo.getValue());
                    break;
                case FilterProperty.LIKE:
                    se = Restrictions.like(fieldInfo.getName(), (String) fieldInfo.getValue(), MatchMode.ANYWHERE);
                    break;
            }
            criteria.add(se);
        }

        return criteria.list();
    }

    public void clear()
            throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        /* Нельзя выдергивать все поля!!! Только с аннотацией!!! Переписать!!!  */
        if (fields == null) {
            fields = this.getClass().getDeclaredFields();
        }

        for (Field field : fields) {
            if (field.isAnnotationPresent(FilterProperty.class)) {
                Method method = this.getClass().getMethod(createSetterName(field.getName()), field.getType());
                try {
                    method.invoke(this, new Object[]{null});
                } catch (Exception e) {
                    method.invoke(this, 0);
                }
            }
        }
    }

    public abstract boolean isEmpty();

    public String getStatus() {
        if (isEmpty()) {
            return "нет";
        }
        return "установлен";
    }
}
