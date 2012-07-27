/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.paging;

import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 23.07.12
 * Time: 18:43
 * To change this template use File | Settings | File Templates.
 */
public class AbstractListPage extends BasicWorkspacePage {

    protected final Class itemClazz;
    protected final Class entityClazz;
    protected long selectedId;
    protected final AbstractFilter filter;

    protected List items;

    private AbstractListPage() {
        this.itemClazz = null;
        this.entityClazz = null;
        this.filter = null;
    }

    public AbstractListPage(Class itemClazz, Class entityClazz, Class filterClazz)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        this.itemClazz = itemClazz;
        this.entityClazz = entityClazz;
        Constructor constructor = filterClazz.getConstructor(Class.class);
        this.filter = (AbstractFilter) constructor.newInstance(entityClazz);
    }

    public static class FieldProperty implements Comparable<FieldProperty> {

        int order;
        String fieldName;
        String name;

        public FieldProperty(int order, String fieldName, String name) {
            this.order = order;
            this.fieldName = fieldName;
            this.name = name;
        }

        @Override
        public int compareTo(FieldProperty fieldProperty) {
            if (order > ((FieldProperty) fieldProperty).getOrder()) {
                return 1;
            } else if (order < fieldProperty.getOrder()) {
                return -1;
            } else {
                return 0;
            }
        }

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public FieldProperty[] getNarrowProperties() {

        List<FieldProperty> properties = new LinkedList<FieldProperty>();
        Field[] fields = itemClazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(NarrowProperty.class)) {
                NarrowProperty narrowProperty = field.getAnnotation(NarrowProperty.class);
                FieldProperty fieldProperty = new FieldProperty(narrowProperty.order(), field.getName(),
                        narrowProperty.name());
                properties.add(fieldProperty);
            }
        }
        FieldProperty[] fieldProperties = properties.toArray(new FieldProperty[0]);
        Arrays.sort(fieldProperties);
        return fieldProperties;
    }

    public void fill(Session session) throws Exception {
        List<AbstractItem> items = new LinkedList<AbstractItem>();
        List objects = filter.retrieveList(session);
        for (Object object : objects) {
            Constructor constructor = itemClazz.getConstructor(entityClazz);
            Object item = constructor.newInstance(object);
            AbstractItem abstractItem = (AbstractItem) item;
            items.add(abstractItem);
        }

        this.items = items;
    }

    public long getSelectedId() {
        return selectedId;
    }

    public void setSelectedId(long selectedId) {
        this.selectedId = selectedId;
    }

    public List getItems() {
        return items;
    }

    public void setItems(List items) {
        this.items = items;
    }

    public AbstractFilter getFilter() {
        return filter;
    }
}
