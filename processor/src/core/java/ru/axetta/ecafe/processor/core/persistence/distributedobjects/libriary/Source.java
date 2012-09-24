/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.libriary;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 17.08.12
 * Time: 19:17
 * To change this template use File | Settings | File Templates.
 */
public class Source extends DistributedObject {

    private String sourceName;
    private Integer hashCode;

    public Integer getHashCode() {
        return hashCode;
    }

    public void setHashCode(Integer hashCode) {
        this.hashCode = hashCode;
    }

    @Override
    public void preProcess(Session session) throws DistributedObjectException {
        Criteria criteria = session.createCriteria(Source.class);
        criteria.add(Restrictions.eq("hashCode", getHashCode()));
        Source source = (Source) criteria.uniqueResult();
        if(!(source==null || source.getDeletedState() || guid.equals(source.getGuid()))){
            DistributedObjectException distributedObjectException =  new DistributedObjectException("Source DATA_EXIST_VALUE");
            distributedObjectException.setData(source.getGuid());
            throw  distributedObjectException;
        }
    }
    
    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "SourceName", sourceName);
    }

    @Override
    public Source parseAttributes(Node node) throws Exception{

        String sourceName = getStringAttributeValue(node, "SourceName", 127);
        if (sourceName != null) {
            setSourceName(sourceName);
        }

        setHashCode(hashCode());

        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setSourceName(((Source) distributedObject).getSourceName());
        setHashCode(((Source) distributedObject).getHashCode());
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    @Override
    public String toString() {
        return "Source{" +
                "sourceName='" + sourceName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        Source that = (Source) o;

        return !(sourceName != null ? !sourceName.equals(that.sourceName)
                : that.sourceName != null);

    }

    private static final String Consonants = "бвгджзклмнпрстфхцчшщbcdfghklmnpqrstuvwxyz1234567890";

    private static boolean isConsonant(char c) {
        for (char consonant : Consonants.toCharArray())
            if (consonant == c)
                return true;
        return false;
    }

    private String getStringForHash(String str) {
        StringBuilder sb = new StringBuilder(str);
        for (int i = sb.length() - 1; i >= 0; --i) {
            if (isConsonant(sb.charAt(i))) continue;
            sb.delete(i, i + 1);
        }
        return sb.toString().toLowerCase();
    }


    @Override
    public int hashCode() {
        return 31 * ((sourceName != null ? getStringForHash(sourceName).hashCode() : 0)) + (sourceName != null ? getStringForHash(sourceName).hashCode() : 0);
    }
}
