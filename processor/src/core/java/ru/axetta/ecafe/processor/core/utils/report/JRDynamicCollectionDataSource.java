/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils.report;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.data.JRAbstractBeanDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Iterator;

public class JRDynamicCollectionDataSource extends JRAbstractBeanDataSource {
    private Collection<? extends DynamicBean> data;
    private Iterator<? extends DynamicBean> iterator;
    private DynamicBean currentBean;
    private final static Logger logger = LoggerFactory.getLogger(JRDynamicCollectionDataSource.class);

    public JRDynamicCollectionDataSource(Collection<? extends DynamicBean> beanCollection) {
        this(beanCollection, true);
    }

    public JRDynamicCollectionDataSource(Collection<? extends DynamicBean> beanCollection, boolean isUseFieldDescription) {
        super(isUseFieldDescription);
        this.data = beanCollection;
        if (this.data != null) {
            this.iterator = this.data.iterator();
        }
    }

    public boolean next() {
        boolean hasNext = false;
        if (this.iterator != null) {
            hasNext = this.iterator.hasNext();
            if (hasNext) {
                this.currentBean = this.iterator.next();
            }
        }
        return hasNext;
    }

    public Object getFieldValue(JRField field) throws JRException {
        Object value = null;
        try {
            value = this.getFieldValue(this.currentBean, field);
        } catch (Exception e) {
            try {
                value = this.currentBean.getValue(this.getPropertyName(field), field.getValueClass());
            } catch (Exception ex) {
                logger.error(String.format("Unable to take field value: %s", field), ex);
            }
        }
        return value;
    }

    public void moveFirst() {
        if (this.data != null) {
            this.iterator = this.data.iterator();
        }
    }

    public Collection<? extends DynamicBean> getData() {
        return this.data;
    }

    public int getRecordCount() {
        return this.data == null ? 0 : this.data.size();
    }

    public JRDynamicCollectionDataSource cloneDataSource() {
        return new JRDynamicCollectionDataSource(this.data);
    }
}
